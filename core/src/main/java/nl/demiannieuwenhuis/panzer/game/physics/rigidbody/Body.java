package nl.demiannieuwenhuis.panzer.game.physics.rigidbody;

import lombok.Getter;
import lombok.Setter;
import nl.demiannieuwenhuis.panzer.game.physics.util.Vector2D;

import java.io.Serializable;

@Getter
public abstract class Body implements Serializable {


    public final double mass;
    protected @Setter double rotation;

    @Setter protected double x;
    @Setter protected double y;

    private static final double velocity_dampening = 0.05;

    public Body(double mass, double x, double y) {
        this.mass = mass;
        this.x = x;
        this.y = y;
        rotation = 0.0;
    }

    public void rotateAroundPoint(Vector2D point, double angle) {
        System.out.println("Before: " + new Vector2D(x, y));
        double radians = Math.toRadians(angle);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double dx = this.x - point.x;
        double dy = this.y - point.y;

        this.x = dx * cos - dy * sin;
        this.y = dx * sin + dy * cos;
        System.out.println("After: " + new Vector2D(x, y));
    }

    public abstract boolean contains(double x, double y);

    /**
     * Calculates and gets center of mass for a uniformly dense rigid body.
     * @return Vector2D point where center of mass resides
     */
    public abstract Vector2D getCenterOfMass();
}
