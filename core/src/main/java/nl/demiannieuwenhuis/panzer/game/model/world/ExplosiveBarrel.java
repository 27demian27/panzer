package nl.demiannieuwenhuis.panzer.game.model.world;

import lombok.Getter;
import nl.demiannieuwenhuis.physics.rigidbody.shapes.Circle;

public class ExplosiveBarrel extends Hazard<Circle> {

    public final float damage;
    public final float blastRadius;
    private @Getter long explosionTime;
    private @Getter boolean exploded;

    public ExplosiveBarrel(float x, float y, float radius, float damage, float blastRadius, Tile tile) {
        super(tile, new Circle(1, x, y, radius));

        this.damage = damage;
        this.blastRadius = blastRadius;
        this.explosionTime = 0;
        this.exploded = false;
    }

    public static ExplosiveBarrel create(Tile tile, float damage) {
        return new ExplosiveBarrel(
            (float) tile.getHitBox().getX(),
            (float) tile.getHitBox().getY(),
            (Battleground.TILE_SIZE / 2.0f) * 0.8f,
            damage,
            100.0f,
            tile
        );
    }

    public void explode() {
        if (exploded) return;

        exploded = true;
        explosionTime = System.nanoTime();
    }
}
