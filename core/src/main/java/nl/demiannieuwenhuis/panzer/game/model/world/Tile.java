package nl.demiannieuwenhuis.panzer.game.model.world;

import lombok.Getter;
import lombok.Setter;
import nl.demiannieuwenhuis.panzer.game.model.tank.Direction8;
import nl.demiannieuwenhuis.physics.rigidbody.shapes.Rect;

public class Tile {

    private final float x, y;
    public final SurfaceType surfaceType;
    public final ContentType contentType;
    public final Direction8 orientation;

    private @Getter @Setter boolean highlighted;

    public Tile(float x, float y, SurfaceType surfaceType, ContentType contentType, Direction8 orientation) {
        this.x = x;
        this.y = y;
        this.surfaceType = surfaceType;
        this.contentType = contentType;
        this.orientation = orientation;
        this.highlighted = false;
    }

    public Rect getHitBox() {
        return new Rect(Double.POSITIVE_INFINITY, x, y, Battleground.TILE_SIZE,Battleground.TILE_SIZE);
    }
}
