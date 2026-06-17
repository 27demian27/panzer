package nl.demiannieuwenhuis.panzer.game.net;

import com.badlogic.gdx.Gdx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Optional;

public class BattleServer extends Thread {

    private final GameRoom gameRoom;
    private DatagramSocket socket;
    private boolean running;
    byte[] buffer = new byte[1024];

    public BattleServer(int port, GameRoom gameRoom) throws SocketException {
        this.gameRoom = gameRoom;
        socket = new DatagramSocket(port);
        socket.setSoTimeout(200);
        setDaemon(false);
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


                ByteBuffer buffer = ByteBuffer.wrap(
                    inPacket.getData(),
                    0,
                    inPacket.getLength()
                );

                TankUpdate tankUpdate = new TankUpdate(buffer);

                Optional<Client> optionalClient = gameRoom.findConnectedClient(inPacket.getSocketAddress());
                if (optionalClient.isEmpty()) {
                    gameRoom.addClientConnection(inPacket.getSocketAddress());
                } else {
                    Client client = optionalClient.get();
                    if (client.maxSequenceNumber >= tankUpdate.sequenceNumber) {
                        Gdx.app.log("BattleServer", "Not sending packet to clients (outdated packet)");
                        continue;
                    }
                    client.touch();
                }


                for (Client client : gameRoom.getConnectedClients()) {
                    DatagramPacket outPacket = new DatagramPacket(
                        buffer.array(),
                        buffer.array().length,
                        client.getSocketAddress()
                    );
                    socket.send(outPacket);
                }


            } catch (IOException e) {
                e.printStackTrace();
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
