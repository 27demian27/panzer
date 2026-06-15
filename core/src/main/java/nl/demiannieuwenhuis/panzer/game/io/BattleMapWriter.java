package nl.demiannieuwenhuis.panzer.game.io;

import nl.demiannieuwenhuis.panzer.game.model.world.Tile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;

public class BattleMapWriter {

    private final Tile[][] tileGrid;

    public final static Path SAVES_DIR = Path.of("core/src/main/resources/battlemaps");
    public final static String FILENAME_SUFFIX = "_level";
    public final static String FILE_EXTENSION = ".map";

    public BattleMapWriter(Tile[][] tileGrid) {
        this.tileGrid = tileGrid;
    }

    public void saveBattleMap(BattleMap battleMap) throws IOException {
        Path saveFile = SAVES_DIR.resolve(battleMap.name + FILENAME_SUFFIX + FILE_EXTENSION);
        try(ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(saveFile.toFile()))) {
            outputStream.writeObject(battleMap);
        }
    }
}
