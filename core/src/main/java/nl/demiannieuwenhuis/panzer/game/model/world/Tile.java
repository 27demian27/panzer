package nl.demiannieuwenhuis.panzer.game.model.world;

import lombok.Getter;
import lombok.Setter;
import nl.demiannieuwenhuis.panzer.game.model.tank.Direction8;
import nl.demiannieuwenhuis.panzer.game.physics.rigidbody.Rect;

import java.beans.Transient;
import java.io.Serializable;

public class Tile implements Serializable {

    public static final int SUBTILES_COUNT = 5;

    private final float x, y;
    private @Getter @Setter SurfaceType surfaceType;
    public @Getter @Setter ContentType contentType;
    public final Direction8 orientation;

    public final Rect[][] subTiles;
    public boolean[][] subTilesTrackMarks;

    private @Getter @Setter boolean selected;

    public Tile(float x, float y, SurfaceType surfaceType, ContentType contentType, Direction8 orientation) {
        this.x = x;
        this.y = y;
        this.surfaceType = surfaceType;
        this.contentType = contentType;
        this.orientation = orientation;
        this.subTiles = initializeSubTiles();
        this.selected = false;
    }

    private Rect[][] initializeSubTiles() {
        Rect[][] subTiles = new Rect[SUBTILES_COUNT][SUBTILES_COUNT];
        subTilesTrackMarks = new boolean[SUBTILES_COUNT][SUBTILES_COUNT];

        float subTileSize = Battleground.TILE_SIZE / SUBTILES_COUNT;

        for (int i = 0; i < SUBTILES_COUNT; i++) {
            for (int j = 0; j < SUBTILES_COUNT; j++) {
                subTilesTrackMarks[i][j] = false;
                subTiles[i][j] =
                    new Rect(0, x + i * subTileSize, y + j * subTileSize, subTileSize, subTileSize);
            }
        }
        return subTiles;
    }

    public Rect getHitBox() {
        return new Rect(Double.POSITIVE_INFINITY, x, y, Battleground.TILE_SIZE, Battleground.TILE_SIZE);
    }
}
