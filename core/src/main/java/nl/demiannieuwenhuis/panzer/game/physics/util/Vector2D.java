package nl.demiannieuwenhuis.panzer.game.physics.util;

import java.io.Serializable;

/**
 * Class representing a vector on a 2D plane.
 */
public class Vector2D implements Serializable {
    public final double x;
    public final double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

    public static double dot(Vector2D vec1, Vector2D vec2) {
        return vec1.x * vec2.x + vec1.y * vec2.y;
    }

    public double angle() {
        return Math.atan2(y, x);
    }

    public double length() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public Vector2D normalized() {
        double magnitude = length();
        return new Vector2D(x / magnitude, y / magnitude);
    }

    public Vector2D scale(double multiplier) {
        return new Vector2D(x * multiplier, y * multiplier);
    }

    public Vector2D rotate(double theta) {
        return new Vector2D(x * Math.cos(theta) - y * Math.sin(theta), x * Math.sin(theta) + y * Math.cos(theta));
    }

    public Vector2D subtract(Vector2D vec) {
        return new Vector2D(x - vec.x, y - vec.y);
    }

    public Vector2D add(Vector2D vec) {
        return new Vector2D(x + vec.x, y + vec.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector2D vec) {
            return (this.x == vec.x && this.y == vec.y);
        }
        return false;
    }
}
