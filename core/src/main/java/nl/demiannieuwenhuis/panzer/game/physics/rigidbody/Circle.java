package nl.demiannieuwenhuis.panzer.game.physics.rigidbody;

import nl.demiannieuwenhuis.panzer.game.physics.util.Vector2D;

public class Circle extends Body {

    public final double radius;

    public Circle(double mass, double x, double y, double radius) {
        super(mass, x, y);

        this.radius = radius;
    }

    @Override
    public boolean contains(double x, double y) {
        return getCenterOfMass().subtract(new Vector2D(x, y)).length() < radius;
    }

    @Override
    public Vector2D getCenterOfMass() {
        return new Vector2D(x + radius, y + radius);
    }
}
