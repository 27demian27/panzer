package nl.demiannieuwenhuis.panzer.game.io;

import java.io.Serializable;

public final class TileData implements Serializable {
    public float x;
    public float y;

    public String surfaceType;
    public String contentType;

    public boolean[][] subTilesTrackMarks;
    public boolean selected;

}
