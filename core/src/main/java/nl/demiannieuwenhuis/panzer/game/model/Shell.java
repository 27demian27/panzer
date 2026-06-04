package nl.demiannieuwenhuis.panzer.game.model;

import lombok.Getter;
import nl.demiannieuwenhuis.physics.rigidbody.shapes.Circle;
import nl.demiannieuwenhuis.physics.util.Vector2D;

/**
 * A class that represents a cannons shell and its projectile.
 */
public class Shell {

    private float speed;
    private @Getter float damage;
    private @Getter final Tank shooter;

    public final Circle hitbox;
    private @Getter final Vector2D direction;

    public Shell(float speed, float damage, Tank shooter, Vector2D direction, Circle hitbox) {
        this.speed = speed;
        this.damage = damage;
        this.shooter = shooter;
        this.direction = direction;
        this.hitbox = hitbox;
    }

    public void update(float dt) {
        hitbox.setX(hitbox.getX() + direction.x * speed * dt);
        hitbox.setY(hitbox.getY() + direction.y * speed * dt);
    }
}
