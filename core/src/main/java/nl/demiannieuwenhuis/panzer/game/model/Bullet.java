package nl.demiannieuwenhuis.panzer.game.model;

import nl.demiannieuwenhuis.physics.rigidbody.shapes.Circle;

public class Bullet {

    private final Circle hitbox;

    public Bullet(Circle hitbox) {
        this.hitbox = hitbox;
    }
}
