package nl.demiannieuwenhuis.panzer.game.io;

import nl.demiannieuwenhuis.panzer.game.model.world.Tile;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BattleMap implements Serializable {

    public final String name;
    public final String fileName;
    public final LocalDateTime lastEdited;
    public final Tile[][] tileGrid;


    public BattleMap(String name, Tile[][] tileGrid) {
        this.name = name;
        this.tileGrid = tileGrid;
        this.fileName = name + BattleMapWriter.FILENAME_SUFFIX + BattleMapWriter.FILE_EXTENSION;
        this.lastEdited = LocalDateTime.now();
    }
}
