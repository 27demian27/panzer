package nl.demiannieuwenhuis.panzer.game.physics.rigidbody;

import lombok.Getter;
import nl.demiannieuwenhuis.panzer.game.physics.util.Vector2D;

@Getter
public class Rect extends Body {

    private double width;
    private double height;

    public Rect(double mass, double x, double y, double width, double height) {
        super(mass, x, y);

        this.width = width;
        this.height = height;
    }

    @Override
    public boolean contains(double x, double y) {
        return ((x > this.x && x < this.x + width) && (y > this.y && y < this.y + height));
    }

    @Override
    public Vector2D getCenterOfMass() {
        return new Vector2D(x + width / 2.0, y + height / 2.0);
    }

}
