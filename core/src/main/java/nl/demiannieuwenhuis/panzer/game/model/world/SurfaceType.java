package nl.demiannieuwenhuis.panzer.game.model.world;

import com.badlogic.gdx.graphics.Color;

public enum SurfaceType {
    GRASS, SAND, WATER, TARMAC;

    public  Color getTerrainColor() {
        return switch (this) {
            case GRASS -> new Color(0.553f, 0.702f, 0.427f, 1.0f);
            case SAND -> new Color(0.796f,0.741f,0.576f, 1.0f);
            case WATER -> new Color(Color.ROYAL);
            case TARMAC -> new Color(0.549f, 0.549f, 0.549f, 1.0f);
        };
    }
}
