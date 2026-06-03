package nl.demiannieuwenhuis.panzer.game.model;

import lombok.Getter;
import lombok.Setter;
import nl.demiannieuwenhuis.physics.rigidbody.shapes.Rect;

public class Tank {
    public final Rect hitbox;
    public final Cannon cannon;

    private final @Getter float movement_speed = 78;
    private final float diag_components_speed = (float) Math.sqrt(Math.pow(movement_speed, 2) / 2.0);

    private @Setter Direction8 direction;

    private @Setter boolean stationary;

    public Tank(float x, float y, float width, float height, double mass) {
        this.hitbox = new Rect(mass, x, y, width, height);
        this.cannon = new Cannon();
        this.direction = null;
        this.stationary = true;
    }

    public void update(float dt) {
        if (!stationary) {
            switch (direction) {
                case W -> hitbox.setX(hitbox.getX() - movement_speed * dt);
                case NW -> {
                    hitbox.setX(hitbox.getX() - diag_components_speed * dt);
                    hitbox.setY(hitbox.getY() + diag_components_speed * dt);
                }
                case N -> hitbox.setY(hitbox.getY() + movement_speed * dt);
                case NE -> {
                    hitbox.setX(hitbox.getX() + diag_components_speed * dt);
                    hitbox.setY(hitbox.getY() + diag_components_speed * dt);
                }
                case E -> hitbox.setX(hitbox.getX() + movement_speed * dt);
                case SE -> {
                    hitbox.setX(hitbox.getX() + diag_components_speed * dt);
                    hitbox.setY(hitbox.getY() - diag_components_speed * dt);
                }
                case S -> hitbox.setY(hitbox.getY() - movement_speed * dt);
                case SW -> {
                    hitbox.setX(hitbox.getX() - diag_components_speed* dt);
                    hitbox.setY(hitbox.getY() - diag_components_speed * dt);
                }
            }
        }
    }

    // Rotatie hoeken zijn een beetje raar, maar werken zo. Onderzoek nodig naar LibGDX gedrag.
    public float getRotation() {
        return switch (direction) {
            case W -> 90.0f;
            case NW -> -315.0f;
            case N -> 0.0f;
            case NE -> -45.0f;
            case E -> 270.0f;
            case SE -> -135.0f;
            case S -> 180.0f;
            case SW -> -225.0f;
            case null -> 0.0f;
        };
    }

}
