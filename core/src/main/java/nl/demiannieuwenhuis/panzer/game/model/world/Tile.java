package nl.demiannieuwenhuis.panzer.game.model.world;

import nl.demiannieuwenhuis.panzer.game.model.tank.Direction8;

public class Tile {

    public final SurfaceType surfaceType;
    public final Direction8 orientation;

    public Tile(SurfaceType surfaceType, Direction8 orientation) {
        this.surfaceType = surfaceType;
        this.orientation = orientation;
    }
}
