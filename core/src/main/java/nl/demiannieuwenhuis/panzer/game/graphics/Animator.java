package nl.demiannieuwenhuis.panzer.game.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;
import nl.demiannieuwenhuis.panzer.game.model.world.Battleground;
import nl.demiannieuwenhuis.panzer.game.model.world.ExplosiveBarrel;
import nl.demiannieuwenhuis.panzer.game.model.world.Hazard;
import nl.demiannieuwenhuis.physics.rigidbody.shapes.Circle;

import java.util.HashMap;
import java.util.Map;


public class Animator {


    private final ShapeRenderer shapeRenderer;

    private final Map<Tank, Float> tankDisabledAnimationTimes;
    private final Map<Tank, Float> tankShootAnimationTimes;
    private final Map<ExplosiveBarrel, Float> barrelExplosionAnimationTimes;

    private final float tankDisabledAnimationDuration = 3.5f;
    private final float tankShootAnimationDuration = 0.2f;
    private final float barrelExplosionAnimationDuration = 0.2f;


    public Animator(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
        this.tankDisabledAnimationTimes = new HashMap<>();
        this.tankShootAnimationTimes = new HashMap<>();
        this.barrelExplosionAnimationTimes = new HashMap<>();
    }

    public void tankAnimations(Tank tank, float dt) {
        if (tank.isDisabled())
            disabledAnimation(tank, dt);

        float lastShotDelta = (System.nanoTime() - tank.cannon.getLast_shot()) / 1_000_000_000.0f;

        if (lastShotDelta <= tankShootAnimationDuration) {
            shootAnimation(tank, dt);
        }
    }

    public void hazardAnimations(Battleground battleground, float dt) {
        for (Hazard<?> hazard : battleground.getHazards()) {
            if (hazard instanceof ExplosiveBarrel barrel) {
                float lastExplosionDelta = ((System.nanoTime() - barrel.getExplosionTime()) / 1_000_000_000.0f);
                if (barrel.isExploded() && lastExplosionDelta <= barrelExplosionAnimationDuration) {
                    barrelExplosionAnimation(barrel, dt);
                } else if (barrel.isExploded()) {
                    battleground.removeHazard(barrel);
                }
            }
        }
    }

    private void barrelExplosionAnimation(ExplosiveBarrel barrel, float dt) {
        Circle hitbox = barrel.hitbox;

        float centerX = (float) (hitbox.getX() + hitbox.radius / 2);
        float centerY = (float) (hitbox.getY() + hitbox.radius / 2);

        float animationDuration = barrelExplosionAnimationDuration;
        Float animationTime = barrelExplosionAnimationTimes.get(barrel);
        if (animationTime == null) {
            animationTime = 0.0f;
        } else {
            animationTime = (animationTime + dt) % animationDuration;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(new Color(Color.YELLOW).sub(0, 0, 0, animationTime / animationDuration));
        shapeRenderer.circle(
            centerX,
            centerY,
            (animationTime / animationDuration) * barrel.blastRadius
        );

        barrelExplosionAnimationTimes.put(barrel, animationTime);
        shapeRenderer.end();
    }

    private void disabledAnimation(Tank tank, float dt) {
        float maxSmokeRadius = (float) tank.hitbox.getWidth();
        float centerX = (float) (tank.hitbox.getX() + tank.hitbox.getWidth() / 2);
        float centerY = (float) (tank.hitbox.getY() + tank.hitbox.getHeight() / 2);
        float smokeSpacing = 8.0f;

        Float animationTime = tankDisabledAnimationTimes.get(tank);
        if (animationTime == null) {
            animationTime = 0.0f;
        } else {
            animationTime = (animationTime + dt) % tankDisabledAnimationDuration;
        }

        float time1 = animationTime;
        float time2 = (animationTime + tankDisabledAnimationDuration / 3) % tankDisabledAnimationDuration;
        float time3 = (animationTime + 2 * tankDisabledAnimationDuration / 3) % tankDisabledAnimationDuration;

        shapeRenderer.setColor(0, 0, 0, 1 - time1 / tankDisabledAnimationDuration);
        shapeRenderer.circle(
            centerX - smokeSpacing,
            centerY - smokeSpacing,
            maxSmokeRadius * time1 / tankDisabledAnimationDuration
        );

        shapeRenderer.setColor(0, 0, 0, 1 - time2 / tankDisabledAnimationDuration);
        shapeRenderer.circle(
            centerX,
            centerY + smokeSpacing,
            maxSmokeRadius * time2 / tankDisabledAnimationDuration
        );

        shapeRenderer.setColor(0, 0, 0, 1 - time3 / tankDisabledAnimationDuration);
        shapeRenderer.circle(
            centerX + smokeSpacing,
            centerY - smokeSpacing,
            maxSmokeRadius * time3 / tankDisabledAnimationDuration
        );

        tankDisabledAnimationTimes.put(tank, animationTime);
    }

    private void shootAnimation(Tank tank, float dt) {
        Float animationTime = tankShootAnimationTimes.get(tank);
        if (animationTime == null) {
            animationTime = 0.0f;
        } else {
            animationTime = (animationTime + dt) % tankShootAnimationDuration;
        }

        float x = (float) tank.hitbox.getX();
        float y = (float) tank.hitbox.getY();
        float tank_width = (float) tank.hitbox.getWidth();
        float tank_length = (float) tank.hitbox.getHeight();
        float centerX = x + tank_width / 2.0f;
        float centerY = y + tank_length / 2.0f;
        float turretWidth = tank_width / 1.25f;
        float turretLength = turretWidth;
        float cannonWidth = tank.cannon.getWidth();
        float cannonLength = tank.cannon.getLength();

        shapeRenderer.setColor(new Color(Color.WHITE).sub(0, 0, 0, animationTime / tankShootAnimationDuration));
        shapeRenderer.rect(
            centerX - cannonWidth / 2.0f, centerY + turretLength / 2.0f,
            cannonWidth / 2.0f, -turretLength / 2.0f,
            cannonWidth, cannonLength,
            1.0f, 1.0f,
            tank.cannon.getAngle()
        );

        tankShootAnimationTimes.put(tank, animationTime);
    }

}
