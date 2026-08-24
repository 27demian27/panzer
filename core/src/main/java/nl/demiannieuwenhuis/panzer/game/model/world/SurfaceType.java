package nl.demiannieuwenhuis.panzer.game.model.world;

import com.badlogic.gdx.graphics.Color;
import lombok.Getter;

public enum SurfaceType {
    GRASS("grass"),
    SAND("sand"),
    WATER("water"),
    TARMAC("tarmac"),
    MUD("mud"),
    UNKNOWN("unknown");

    private @Getter String id;

    SurfaceType(String id) {
        this.id = id;
    }

    public static SurfaceType fromId(String id) {
        for (SurfaceType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public Color getTerrainColor() {
        return switch (this) {
            case GRASS -> new Color(0.553f, 0.702f, 0.427f, 1.0f);
            case SAND -> new Color(0.796f,0.741f,0.576f, 1.0f);
            case WATER -> new Color(Color.ROYAL);
            case TARMAC -> new Color(0.549f, 0.549f, 0.549f, 1.0f);
            case MUD -> new Color(Color.BROWN);
            case UNKNOWN -> new Color(Color.WHITE);
        };
    }
}
