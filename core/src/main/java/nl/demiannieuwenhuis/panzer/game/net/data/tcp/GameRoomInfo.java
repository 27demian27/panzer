package nl.demiannieuwenhuis.panzer.game.net.data.tcp;

import java.io.*;

public record GameRoomInfo(String code, int playerCount, int maxPlayers, String mapFilename) {

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeUTF(code);
        dos.writeInt(playerCount);
        dos.writeInt(maxPlayers);
        dos.writeUTF(mapFilename);

        return baos.toByteArray();
    }

    public static GameRoomInfo fromByteArray(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        return new GameRoomInfo(dis.readUTF(), dis.readInt(), dis.readInt(), dis.readUTF());
    }

    @Override
    public String toString() {
        return "Game Room Info\n" +
            "Code: " + code + "\n" +
            "Player Count: " + playerCount + "\n" +
            "Max Players: " + maxPlayers + "\n" +
            "Map Filename: " + mapFilename + "\n";
     }
}
