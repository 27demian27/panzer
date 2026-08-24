package nl.demiannieuwenhuis.panzer.game.io;


import com.badlogic.gdx.Gdx;

import java.io.*;
import java.nio.file.Path;

public class BattleMapLoader {

    public static BattleMap loadBattleMap(String BattleMapName) throws IOException {
        Path saveFile = BattleMapWriter.SAVES_DIR.resolve(BattleMapName);
        return loadBattleMap(saveFile.toFile());
    }

    public static BattleMap loadBattleMap(File battleMapFile) throws IOException {
        if (!battleMapFile.isFile())
            throw new FileNotFoundException();

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(battleMapFile))) {
            Gdx.app.log("loadBattleMap", "Loading map: " + battleMapFile.getName());
            return (BattleMap) inputStream.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            Gdx.app.log("loadBattleMap", "Could not load: " + battleMapFile.getName(), e);
            return null;
        }
    }

}
