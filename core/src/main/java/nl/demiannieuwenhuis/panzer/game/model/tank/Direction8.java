package nl.demiannieuwenhuis.panzer.game.model.tank;

/**
 * The four intercardinal directions.
 */
public enum Direction8 {
    N, NE, E, SE, S, SW, W, NW, NONE;

    // Rotatie hoeken zijn een beetje raar, maar werken zo. LibGDX gedrag.
    public static float getRotation(Direction8 direction) {
        return switch (direction) {
            case W -> 90.0f;
            case NW -> -315.0f;
            case N -> 0.0f;
            case NE -> -45.0f;
            case E -> 270.0f;
            case SE -> -135.0f;
            case S -> 180.0f;
            case SW -> -225.0f;
            case NONE -> 0.0f;
            case null -> 0.0f;
        };
    }
}
