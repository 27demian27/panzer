package nl.demiannieuwenhuis.panzer.game.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;

import java.util.HashMap;
import java.util.Map;


public class Animator {


    private final ShapeRenderer shapeRenderer;

    private final Map<Tank, Float> tankDisabledAnimationTimes;
    private final Map<Tank, Float> tankShootAnimationTimes;

    private final float tankShootAnimationDuration = 0.2f;


    public Animator(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
        this.tankDisabledAnimationTimes = new HashMap<>();
        this.tankShootAnimationTimes = new HashMap<>();
    }

    public void tankAnimations(Tank tank, float dt) {
        if (tank.isDisabled())
            disabledAnimation(tank, dt);

        float lastShotDelta = (System.nanoTime() - tank.cannon.getLast_shot()) / (float) 1_000_000_000;

        if (lastShotDelta <= tankShootAnimationDuration) {
            shootAnimation(tank, dt);
        }
    }

    private void disabledAnimation(Tank tank, float dt) {
        float maxSmokeRadius = (float) tank.hitbox.getWidth();
        float animationDuration = 3.5f;
        float centerX = (float) (tank.hitbox.getX() + tank.hitbox.getWidth() / 2);
        float centerY = (float) (tank.hitbox.getY() + tank.hitbox.getHeight() / 2);
        float smokeSpacing = 8.0f;

        Float animationTime = tankDisabledAnimationTimes.get(tank);
        if (animationTime == null) {
            animationTime = 0.0f;
        } else {
            animationTime = (animationTime + dt) % animationDuration;
        }

        float time1 = animationTime;
        float time2 = (animationTime + animationDuration / 3) % animationDuration;
        float time3 = (animationTime + 2 * animationDuration / 3) % animationDuration;

        shapeRenderer.setColor(0, 0, 0, 1 - time1 / animationDuration);
        shapeRenderer.circle(
            centerX - smokeSpacing,
            centerY - smokeSpacing,
            maxSmokeRadius * time1 / animationDuration
        );

        shapeRenderer.setColor(0, 0, 0, 1 - time2 / animationDuration);
        shapeRenderer.circle(
            centerX,
            centerY + smokeSpacing,
            maxSmokeRadius * time2 / animationDuration
        );

        shapeRenderer.setColor(0, 0, 0, 1 - time3 / animationDuration);
        shapeRenderer.circle(
            centerX + smokeSpacing,
            centerY - smokeSpacing,
            maxSmokeRadius * time3 / animationDuration
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
