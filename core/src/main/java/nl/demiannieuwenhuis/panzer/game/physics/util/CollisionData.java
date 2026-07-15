package nl.demiannieuwenhuis.panzer.game.physics.util;

public class CollisionData {
    public boolean colliding;
    public Vector2D normal;
    public double penetration;

    public CollisionData(boolean colliding, Vector2D normal, double penetration) {
        this.colliding = colliding;
        this.normal = normal;
        this.penetration = penetration;
    }

    @Override
    public String toString() {
        return getClass() + "\ncolliding: " + colliding + "\nnormal: " + normal + "\npenetration: " + penetration;
    }
}
