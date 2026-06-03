package nl.demiannieuwenhuis.panzer.game.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import nl.demiannieuwenhuis.panzer.game.model.Tank;

public class Renderer {
    public static final Color HULL_COLOR = new Color(0.427f, 0.439f, 0.310f, 1.0f);
    public static final Color TURRET_COLOR = new Color(HULL_COLOR).sub(0.05f, 0.05f, 0.05f, 1.0f);
    public static final Color TRACK_COLOR = Color.DARK_GRAY;

    private final ShapeRenderer shapeRenderer;


    public Renderer() {
        this.shapeRenderer = new ShapeRenderer();
    }

    public void renderTank(Tank tank) {
        float tank_width = (float) tank.hitbox.getWidth();
        float tank_length = (float) tank.hitbox.getHeight();
        float x = (float) tank.hitbox.getX();
        float y = (float) tank.hitbox.getY();


        float tracksSpacingX = tank_width / 6.0f;
        float tracksSpacingY = tank_length / 12.0f;
        float tracksWidth = tank_width / 3.0f;
        float tracksLength = tank_length - 2 * tracksSpacingY;
        float centerX = x + tank_width / 2f;
        float centerY = y + tank_length / 2f;

        float turretWidth = tank_width / 1.25f;
        float turretLength = turretWidth;
        float cannonWidth = tank_width / 6.0f;
        float cannonLength = tank_length / 2.0f;

        // LEFT TRACK
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(TRACK_COLOR);
        shapeRenderer.rect(
            x - tracksSpacingX , y + tracksSpacingY,
            centerX - (x - tracksSpacingX), centerY - (y + tracksSpacingY),
            tracksWidth, tracksLength,
            1.0f, 1.0f,
            tank.getRotation()
        );

        // RIGHT TRACK
        shapeRenderer.setColor(TRACK_COLOR);
        shapeRenderer.rect(
            x + tank_width - tracksSpacingX , y + tracksSpacingY,
            centerX - (x + tank_width - tracksSpacingX), centerY - (y + tracksSpacingY),
            tracksWidth, tracksLength,
            1.0f, 1.0f,
            tank.getRotation()
        );

        // HULL
        shapeRenderer.setColor(HULL_COLOR);
        shapeRenderer.rect(
            x, y,
            tank_width / 2.0f, tank_length / 2.0f,
            tank_width, tank_length,
            1.0f, 1.0f,
            tank.getRotation()
        );

        shapeRenderer.setColor(new Color(HULL_COLOR).add(0.05f, 0.05f, 0.05f, 1.0f));
        shapeRenderer.rect(
            x + tank_width / 8.0f, y + tank_width / 10.0f,
            tank_width / 2.0f - tank_width / 8.0f, tank_length / 2.0f - tank_width / 8.0f,
            tank_width - 2 * (tank_width / 8.0f), tank_length / 6.0f,
            1.0f, 1.0f,
            tank.getRotation()
        );



        // TURRET
        shapeRenderer.setColor(TURRET_COLOR);
        shapeRenderer.rect(
            centerX - cannonWidth / 2.0f, centerY + turretLength / 2.0f,
            cannonWidth / 2.0f, -turretLength / 2.0f,
            cannonWidth, cannonLength,
            1.0f, 1.0f,
            tank.cannon.getAngle()
        );
        shapeRenderer.rect(
            centerX - turretWidth / 2.0f, centerY - turretLength / 2.0f,
            turretWidth / 2.0f, turretLength / 2.0f,
            turretWidth, turretLength,
            1.0f, 1.0f,
            tank.cannon.getAngle()
        );

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

}
