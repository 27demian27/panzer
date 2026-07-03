package nl.demiannieuwenhuis.panzer.game.net.server;

import com.badlogic.gdx.Gdx;
import lombok.Getter;
import lombok.Setter;
import nl.demiannieuwenhuis.panzer.game.net.client.Client;
import nl.demiannieuwenhuis.panzer.game.net.GameRoom;
import nl.demiannieuwenhuis.panzer.game.net.dto.TankUpdate;
import nl.demiannieuwenhuis.panzer.game.net.dto.WorldUpdate;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BattleServer extends Thread {

    private final GameRoom gameRoom;
    private DatagramSocket socket;
    private boolean running;
    byte[] buffer = new byte[1024];

    private @Getter @Setter WorldUpdate latestSnapshot;


    public BattleServer(int port, GameRoom gameRoom) throws SocketException {
        this.gameRoom = gameRoom;
        socket = new DatagramSocket(port);
        socket.setSoTimeout(200);
        setDaemon(false);
        Gdx.app.log("BattleServer", "Started UDP server on port " + port);
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            long start = System.nanoTime();

            try {

                Gdx.app.log("BattleServer", gameRoom.getConnectedClients().size() + " clients connected");
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(inPacket);

                Gdx.app.log("BattleServer", "Received packet from " + inPacket.getSocketAddress());
                Gdx.app.log("BattleServer", "Packet size: " + inPacket.getLength() + " bytes");


                ByteBuffer incTankBuffer = ByteBuffer.wrap(
                    inPacket.getData(),
                    0,
                    inPacket.getLength()
                );

                long incomingSequenceNumber = incTankBuffer.getLong();


                Optional<Client> optionalClient = gameRoom.findConnectedClient(inPacket.getSocketAddress());
                if (optionalClient.isEmpty()) {
                    gameRoom.addClientConnection(inPacket.getSocketAddress());
                    continue;
                } else {
                    Client client = optionalClient.get();
                    if (client.getMaxSequenceNumber() < incomingSequenceNumber) {
                        client.setMaxSequenceNumber(incomingSequenceNumber);
                        client.touch();
                    } else {
                        Gdx.app.log("BattleServer", "Skipping packet from " +
                            inPacket.getSocketAddress() +"\n Reason: packet outdated");
                        continue;
                    }
                }

                if (latestSnapshot == null) continue;

                TankUpdate tankUpdate = TankUpdate.fromBuffer(incTankBuffer);

                List<TankUpdate> merged = new ArrayList<>(latestSnapshot.tankUpdates);
                merged.removeIf(t -> t.UID() == tankUpdate.UID());
                merged.add(tankUpdate);

                WorldUpdate outSnapshot = new WorldUpdate(latestSnapshot.sequenceNumber, merged);

                ByteBuffer outWorldBuffer = outSnapshot.toBuffer();

                byte[] data = new byte[outWorldBuffer.remaining()];
                outWorldBuffer.get(data);
                for (Client client : gameRoom.getConnectedClients()) {
                    Gdx.app.log(
                        "BattleServer", "Sending WorldUpdate to client " + client.getId() +" | " + client.getSocketAddress()
                    );
                    socket.send(new DatagramPacket(data, data.length, client.getSocketAddress()));
                }




            } catch (SocketTimeoutException ignored) {

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            gameRoom.disconnectInactiveClients();
            long elapsed = System.nanoTime() - start;
            gameRoom.incrementClientsLastActiveTimes(elapsed);

            if (gameRoom.getConnectedClients().isEmpty()) {
                Gdx.app.log("BattleServer", "No more connected clients...");
                Gdx.app.log("BattleServer", "Shutting down server.");
                break;
            }

        }
        socket.close();
    }
}
