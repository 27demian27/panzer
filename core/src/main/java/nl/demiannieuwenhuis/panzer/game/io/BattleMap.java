package nl.demiannieuwenhuis.panzer.game.io;

import nl.demiannieuwenhuis.panzer.game.model.world.Tile;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BattleMap implements Serializable {

    public final String name;
    public final String fileName;
    public final LocalDateTime lastEdited;
    public final TileData[][] tileDataGrid;


    private BattleMap(String name, TileData[][] tileDataGrid) {
        this.name = name;
        this.tileDataGrid = tileDataGrid;
        this.fileName = name + BattleMapWriter.FILENAME_SUFFIX + BattleMapWriter.FILE_EXTENSION;
        this.lastEdited = LocalDateTime.now();
    }

    public static BattleMap of(String name, Tile[][] tileGrid) {
        if (tileGrid == null || tileGrid.length == 0 || tileGrid[0].length == 0) {
            throw new RuntimeException("Tile grid is empty");
        }

        return new BattleMap(name, tileGridToTileDataGrid(tileGrid));
    }

    public static TileData[][] tileGridToTileDataGrid(Tile[][] tileGrid) {
        TileData[][] tileDataGrid = new TileData[tileGrid.length][tileGrid[0].length];

        for (int i = 0; i < tileGrid.length; i++) {
            for (int j = 0; j < tileGrid[i].length; j++) {
                tileDataGrid[i][j] = TileMapper.toData(tileGrid[i][j]);
            }
        }
        return tileDataGrid;
    }

    public static Tile[][] tileDataGridToTileGrid(TileData[][] tileDataGrid) {
        Tile[][] tileGrid = new Tile[tileDataGrid.length][tileDataGrid[0].length];

        for (int i = 0; i < tileDataGrid.length; i++) {
            for (int j = 0; j < tileDataGrid[i].length; j++) {
                tileGrid[i][j] = TileMapper.fromData(tileDataGrid[i][j]);
            }
        }
        return tileGrid;
    }
}
