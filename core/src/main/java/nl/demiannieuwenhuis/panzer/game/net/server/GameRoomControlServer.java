package nl.demiannieuwenhuis.panzer.game.net.server;

import com.badlogic.gdx.Gdx;
import nl.demiannieuwenhuis.panzer.game.net.GameRoom;
import nl.demiannieuwenhuis.panzer.game.net.dto.tcp.GameRoomInfo;
import nl.demiannieuwenhuis.panzer.game.net.dto.tcp.TcpRequestType;

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
        Gdx.app.log("GameRoomControlServer", "Started TCP server on port " + port);
    }


    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();

                new Thread(() -> handleClient(clientSocket)).start();

            } catch (IOException e) {
                Gdx.app.error("GameRoomControlServer", "Error while making connection: ", e);
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

            if (TcpRequestType.toType(type) == TcpRequestType.INFO) {
                Gdx.app.log("GameRoomControlServer", "Incoming room info request");
                byte[] payload = createGameInfoPayload();

                out.writeInt(payload.length);
                out.writeByte(TcpRequestType.INFO.toByte());
                out.write(payload);
                out.flush();
            }

        } catch (IOException e) {
            Gdx.app.error("GameRoomControlServer", "Error while handling request: ", e);
        }
    }

    private byte[] createGameInfoPayload() throws IOException {
        GameRoomInfo gameRoomInfo = new GameRoomInfo(
            gameRoom.code,
            gameRoom.getConnectedClients().size(),
            GameRoom.MAX_PLAYERS,
            gameRoom.mapFileName
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream packetOut = new DataOutputStream(baos);

        packetOut.write(gameRoomInfo.toByteArray());

        return baos.toByteArray();
    }
}
