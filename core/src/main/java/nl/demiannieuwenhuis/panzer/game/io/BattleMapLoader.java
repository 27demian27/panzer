package nl.demiannieuwenhuis.panzer.game.io;

import nl.demiannieuwenhuis.panzer.game.model.world.Tile;

import java.io.*;
import java.nio.file.Path;

public class BattleMapLoader {

    public static Tile[][] loadBattleMap(String BattleMapName) throws IOException {
        Path saveFile = BattleMapWriter.SAVES_DIR.resolve(BattleMapName);
        return loadBattleMap(saveFile.toFile());
    }

    public static Tile[][] loadBattleMap(File BattleMapFile) throws IOException {
        if (!BattleMapFile.isFile())
            throw new FileNotFoundException();

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(BattleMapFile))) {
            return (Tile[][]) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
