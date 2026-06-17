package nl.demiannieuwenhuis.panzer.game.net;

import com.badlogic.gdx.Gdx;
import lombok.Getter;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class ClientConnection {
    private DatagramSocket udpSocket;
    private Socket tcpSocket;
    private InetAddress address;
    private String roomCode;

    private @Getter Tank playerTank;

    byte[] buffer = new byte[1024];
    private long packetsSent;

    public ClientConnection(String roomCode, Tank playerTank) throws IOException {
        this.playerTank = playerTank;
        this.roomCode = roomCode;
        udpSocket = new DatagramSocket();
        tcpSocket = new Socket("localhost", GameRoom.unhashRoomCode(roomCode));
        udpSocket.setSoTimeout(200);
        address = InetAddress.getByName("localhost");
        packetsSent = 0;
    }

    public void sendTankPacket() throws IOException {
        TankUpdate tankUpdate = new TankUpdate(
            ++packetsSent,
            playerTank.UID,
            playerTank.hitbox.getX(),
            playerTank.hitbox.getY(),
            playerTank.hitbox.getRotation(),
            playerTank.cannon.getAngle(),
            playerTank.cannon.hasShootRequest()
        );

        ByteBuffer buffer = tankUpdate.toBuffer();

//        Gdx.app.log("ClientConnection", "sending tank update\n" + tankUpdate);

        byte[] data = buffer.array();

        DatagramPacket packet = new DatagramPacket(data, data.length, address, GameRoom.unhashRoomCode(roomCode));

        udpSocket.send(packet);
    }

    public TankUpdate receiveTankPacket() throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, GameRoom.unhashRoomCode(roomCode));
        udpSocket.receive(packet);

        ByteBuffer buffer = ByteBuffer.wrap(
            packet.getData(),
            0,
            packet.getLength()
        );

        return new TankUpdate(buffer);
    }

    public GameRoomInfo getGameRoomInfo() throws IOException {
        try (
            DataInputStream in = new DataInputStream(tcpSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(tcpSocket.getOutputStream())
            )
        {
            int outLength = 8;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream packetOut = new DataOutputStream(baos);

            packetOut.writeInt(outLength);
            packetOut.writeByte(0); // INFO REQUEST

            byte[] payload = baos.toByteArray();

            out.write(payload.length);
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

            packetIn.close();
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
