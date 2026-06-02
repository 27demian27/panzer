package nl.demiannieuwenhuis.panzer.game.model;

import nl.demiannieuwenhuis.physics.rigidbody.shapes.Rect;

public class Tank {
    public final Rect hitbox;
    public final Cannon cannon;

    public Tank(float x, float y, float width, float height, double mass) {
        this.hitbox = new Rect(mass, x, y, width, height);
        this.cannon = new Cannon();
    }
}
