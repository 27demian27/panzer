package nl.demiannieuwenhuis.panzer.game.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import nl.demiannieuwenhuis.panzer.game.model.tank.Direction8;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;
import nl.demiannieuwenhuis.panzer.game.model.world.Battleground;
import nl.demiannieuwenhuis.panzer.game.model.world.ExplosiveBarrel;
import nl.demiannieuwenhuis.panzer.game.model.world.Hazard;
import nl.demiannieuwenhuis.panzer.game.physics.rigidbody.Circle;

import java.util.HashMap;
import java.util.Map;


public class Animator {


    private final ShapeRenderer shapeRenderer;

    private final Map<Tank, Float> tankDisabledAnimationTimes;
    private final Map<Tank, Float> tankShootAnimationTimes;
    private final Map<Tank, Map<Vector2, Float>> tankExhaustAnimationTimes;
    private final Map<ExplosiveBarrel, Float> barrelExplosionAnimationTimes;

    private final float tankDisabledAnimationDuration = 3.5f;
    private final float tankShootAnimationDuration = 0.2f;
    private final float barrelExplosionAnimationDuration = 0.2f;
    private final float tankExhaustSmokeLinger = 0.6f;


    public Animator(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
        this.tankDisabledAnimationTimes = new HashMap<>();
        this.tankShootAnimationTimes = new HashMap<>();
        this.tankExhaustAnimationTimes = new HashMap<>();
        this.barrelExplosionAnimationTimes = new HashMap<>();
    }

    public void tankAnimations(Tank tank, float dt) {

        float lastShotDelta = (System.nanoTime() - tank.cannon.getLast_shot()) / 1_000_000_000.0f;

        if (lastShotDelta <= tankShootAnimationDuration) {
            shootAnimation(tank, dt);
        }

        if (tank.isDisabled()) {
            disabledAnimation(tank, dt);
        } else {
            exhaustAnimation(tank, dt);
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

    private void exhaustAnimation(Tank tank, float dt) {
        if (!tankExhaustAnimationTimes.containsKey(tank)) tankExhaustAnimationTimes.put(tank, new HashMap<>());

        float EXHAUST_SMOKE_RADIUS = 6.0f;
        float SPACING_FROM_CENTER = 0.6f;

        Map<Vector2, Float> exhaustSmokes = tankExhaustAnimationTimes.get(tank);

        float lastSmoke = exhaustSmokes.values().stream().reduce(Float.MAX_VALUE, Math::min);
        float smokesPerSecond = tank.getEngineRpm() / 60.0f;
        if (lastSmoke >= 1 / smokesPerSecond) {
            float x = (float) tank.hitbox.getX();
            float y = (float) tank.hitbox.getY();
            float centerX = (float) (tank.hitbox.getX() + tank.hitbox.getWidth() / 2.0f);
            float centerY = (float) (tank.hitbox.getY() + tank.hitbox.getHeight() / 2.0f);
            float leftExhaustPosX = (float) (x + (tank.hitbox.getWidth() / 2.0f * SPACING_FROM_CENTER));
            float leftExhaustPosY = y;
            float rightExhaustPosX = (float) (x + tank.hitbox.getWidth() - (tank.hitbox.getWidth() / 2.0f * SPACING_FROM_CENTER));
            float rightExhaustPosY = y;

            Vector2 location1 = new Vector2(leftExhaustPosX, leftExhaustPosY)
                .rotateAroundDeg(new Vector2(centerX, centerY), Direction8.getRotation(tank.getVisualDirection()));
            Vector2 location2 = new Vector2(rightExhaustPosX, rightExhaustPosY)
                .rotateAroundDeg(new Vector2(centerX, centerY), Direction8.getRotation(tank.getVisualDirection()));

            exhaustSmokes.put(location1, 0.0f);
//            exhaustSmokes.put(location2, 0.0f);
        }

       exhaustSmokes.forEach((location, time) -> {
           shapeRenderer.setColor(0.500f, 0.500f, 0.500f, 1 - time / tankExhaustSmokeLinger - 0.4f);
           shapeRenderer.circle(location.x, location.y, EXHAUST_SMOKE_RADIUS * (1 - time / tankExhaustSmokeLinger));

           exhaustSmokes.put(location, time + dt);
       });

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
