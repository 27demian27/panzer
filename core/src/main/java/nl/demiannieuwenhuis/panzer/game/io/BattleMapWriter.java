package nl.demiannieuwenhuis.panzer.game.io;

import nl.demiannieuwenhuis.panzer.game.model.world.Tile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;

public class BattleMapWriter {

    private final Tile[][] tileGrid;

    public final static Path SAVES_DIR = Path.of("core/src/main/resources/battlemaps");

    public BattleMapWriter(Tile[][] tileGrid) {
        this.tileGrid = tileGrid;
    }

    public void saveStage(String stageName) throws IOException {
        Path saveFile = SAVES_DIR.resolve(stageName + "_stage" +".map");
        try(ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(saveFile.toFile()))) {
            outputStream.writeObject(tileGrid);
        }
    }
}
