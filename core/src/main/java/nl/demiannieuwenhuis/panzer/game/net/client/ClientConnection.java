package nl.demiannieuwenhuis.panzer.game.net.client;

import com.badlogic.gdx.Gdx;
import lombok.Getter;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;
import nl.demiannieuwenhuis.panzer.game.net.GameRoom;
import nl.demiannieuwenhuis.panzer.game.net.data.tcp.GameRoomInfo;
import nl.demiannieuwenhuis.panzer.game.net.data.udp.TankUpdate;
import nl.demiannieuwenhuis.panzer.game.net.data.udp.WorldUpdate;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ClientConnection {
    private DatagramSocket udpSocket;
    private Socket tcpSocket;

    private InetAddress address;
    private String roomCode;

    private @Getter Tank playerTank;

    byte[] buffer = new byte[1024];
    private long sequenceNumber = 0;
    private @Getter long maxWorldUpdateSequenceNumber = Long.MIN_VALUE;

    private final AtomicReference<WorldUpdate> latestWorldUpdate = new AtomicReference<>();

    public ClientConnection(String roomCode, Tank playerTank) throws IOException {
        this.playerTank = playerTank;
        this.roomCode = roomCode;
        udpSocket = new DatagramSocket();
        tcpSocket = new Socket("localhost", GameRoom.unhashRoomCode(roomCode));
        udpSocket.setSoTimeout(200);
        address = InetAddress.getByName("localhost");
    }

    public void startReceiving() {
        Thread receiver = new Thread(() -> {
            while (!udpSocket.isClosed()) {
                try {
                    WorldUpdate update = receiveWorldUpdate();
                    long incoming = update.sequenceNumber();
                    latestWorldUpdate.updateAndGet(current ->
                        (current == null || incoming >= current.sequenceNumber()) ? update : current
                    );
                } catch (SocketTimeoutException ignored) {
                } catch (IOException e) {
                    if (!udpSocket.isClosed()) throw new RuntimeException(e);
                }
            }
        }, "udp-receiver");
        receiver.setDaemon(true);
        receiver.start();
    }

    public WorldUpdate pollLatestWorldUpdate() {
        return latestWorldUpdate.getAndSet(null);
    }

    public void sendTankPacket() throws IOException {
        TankUpdate tankUpdate = playerTank.getUpdateSnapshot();

        ByteBuffer buffer = ByteBuffer.allocate(TankUpdate.BYTES + Long.BYTES);
        buffer.putLong(++sequenceNumber);
        buffer.put(tankUpdate.toBuffer());

        Gdx.app.log("ClientConnection", "Sending tank update:\n" + tankUpdate);

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        DatagramPacket packet = new DatagramPacket(data, data.length, address, GameRoom.unhashRoomCode(roomCode));

        udpSocket.send(packet);
    }

    private WorldUpdate receiveWorldUpdate() throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        udpSocket.receive(packet);

        Gdx.app.log("ClientConnection", "Received packet from " + packet.getSocketAddress());
        Gdx.app.log("ClientConnection", "Packet size: " + packet.getLength() + " bytes");

        ByteBuffer worldBuffer = ByteBuffer.wrap(
            packet.getData(),
            0,
            packet.getLength()
        );

        long incomingSequenceNumber = worldBuffer.getLong();
        Gdx.app.log("ClientConnection", "Packet sequence number: " + incomingSequenceNumber);

        if (incomingSequenceNumber > maxWorldUpdateSequenceNumber)
            maxWorldUpdateSequenceNumber = incomingSequenceNumber;

        List<TankUpdate> tankUpdates = new ArrayList<>();
        while (worldBuffer.remaining() >= TankUpdate.BYTES) {
            ByteBuffer tankSlice = worldBuffer.slice();
            tankSlice.limit(TankUpdate.BYTES);
            tankUpdates.add(TankUpdate.fromBuffer(tankSlice));
            worldBuffer.position(worldBuffer.position() + TankUpdate.BYTES);
        }

        return new WorldUpdate(incomingSequenceNumber, tankUpdates);
    }

    public GameRoomInfo getGameRoomInfo() throws IOException {
        try (
            Socket socket = new Socket("localhost", GameRoom.unhashRoomCode(roomCode));
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream packetOut = new DataOutputStream(baos);

            packetOut.writeByte(0); // INFO REQUEST

            byte[] payload = baos.toByteArray();

            out.writeInt(payload.length);
            out.write(payload);
            out.flush();

            int inLength = in.readInt();
            byte[] data = new byte[inLength];
            in.readFully(data);

            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            DataInputStream packetIn = new DataInputStream(bais);

            byte type = packetIn.readByte();
            if (type != 0) {
                Gdx.app.error("ClientConnection", "TCP request types do not match (" + type + " != 0)");
                throw new RuntimeException("TCP request type mismatch");
            }
            GameRoomInfo gameRoomInfo = new GameRoomInfo(
                packetIn.readUTF(),
                packetIn.readInt(),
                packetIn.readInt(),
                packetIn.readUTF()
            );
            Gdx.app.log("ClientConnection", "Got GameRoomInfo: " + gameRoomInfo);

            return gameRoomInfo;
        }
    }

    public void close() {
        try {
            tcpSocket.close();
        } catch (IOException e) {
            Gdx.app.error("ClientConnection", "Could not close TCP connection");
        }
        udpSocket.close();
    }
}
