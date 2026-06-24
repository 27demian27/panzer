package nl.demiannieuwenhuis.panzer.game.graphics;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;

import java.util.HashMap;
import java.util.Map;


public class Animator {


    private final ShapeRenderer shapeRenderer;

    private final Map<Tank, Float> tankDisabledAnimationTimes;

    public Animator(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
        this.tankDisabledAnimationTimes = new HashMap<>();
    }

    public void disabledAnimation(Tank tank, float dt) {
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

        tankDisabledAnimationTimes.put(tank, animationTime + dt);
    }


}
