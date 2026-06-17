package nl.demiannieuwenhuis.panzer.game.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GameRoomControlServer extends Thread {

    private final GameRoom gameRoom;
    private ServerSocket serverSocket;

    private boolean running;

    public GameRoomControlServer(int port, GameRoom gameRoom) throws IOException {
        this.gameRoom = gameRoom;
        serverSocket = new ServerSocket(port);
    }


    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();

                new Thread(() -> handleClient(clientSocket)).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                Socket socket = clientSocket;
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())
            )
        {
            int length = in.readInt();
            byte[] data = new byte[length];
            in.readFully(data);

            DataInputStream msg = new DataInputStream(new ByteArrayInputStream(data));
            byte type = msg.readByte();

            if (type == 0) { // INFO REQUEST
                byte[] payload = createGameInfoPayload();

                out.writeInt(payload.length);
                out.write(payload);
                out.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] createGameInfoPayload() throws IOException {
        GameRoomInfo info = new GameRoomInfo(
            gameRoom.code,
            gameRoom.getConnectedClients().size(),
            GameRoom.MAX_PLAYERS,
            gameRoom.mapFileName
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream packetOut = new DataOutputStream(baos);

        packetOut.writeByte(0);
        packetOut.writeUTF(info.code());
        packetOut.writeInt(info.playerCount());
        packetOut.writeInt(info.maxPlayers());
        packetOut.writeUTF(info.mapFileName());

        byte[] payload = baos.toByteArray();
        return payload;
    }
}
