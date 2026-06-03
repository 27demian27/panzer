package nl.demiannieuwenhuis.panzer.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import nl.demiannieuwenhuis.panzer.game.model.Battleground;
import nl.demiannieuwenhuis.panzer.game.model.Direction8;
import nl.demiannieuwenhuis.panzer.game.model.Tank;
import nl.demiannieuwenhuis.physics.util.Vector2D;

import java.util.Vector;

public class GameLoop implements Runnable {

    private final Battleground battleground;
    private boolean running = false;

    private boolean stopped = false;

    public GameLoop(Battleground battleground) {
        this.battleground = battleground;
    }

    @Override
    public void run() {
        running = true;
        try {
            while (!stopped) {
                if (running) {
                    handleControls();
                    updateBattleground();
                }
                Thread.sleep(17);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleControls() {
        Direction8 direction = computePlayerDirection();

        if (direction == null) {
            battleground.getPlayerTank().setStationary(true);
        } else {
            battleground.getPlayerTank().setStationary(false);
            battleground.getPlayerTank().setDirection(direction);
        }

        battleground.getPlayerTank().cannon.setRotating_direction(computePlayerCannonRotationDirection());

    }

    private short computePlayerCannonRotationDirection() {
        Tank playerTank = battleground.getPlayerTank();
        Vector2D mousePointer = new Vector2D(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        Vector2D cannonOrigin = new Vector2D(playerTank.hitbox.getCenterOfMass());
        float mouseAngle = (float) (mousePointer.subtract(cannonOrigin).angle() * (180 / Math.PI)) - 90.0f;

        float diff = mouseAngle - playerTank.cannon.getAngle();

        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;

        System.out.println(diff);

        if (diff > -4.0f && diff < 4.0f)
            return 0;

        if (diff > 0)
            return 1;
        if (diff < 0)
            return -1;

        return 0;
    }

    private Direction8 computePlayerDirection() {
        String moveDirection = "";
        if (Gdx.input.isKeyPressed(Input.Keys.W)) moveDirection += "N";
        if (Gdx.input.isKeyPressed(Input.Keys.S)) moveDirection += "S";
        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveDirection += "E";
        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveDirection += "W";

        if (moveDirection.contains("NS")) moveDirection = moveDirection.replace("NS", "");
        if (moveDirection.contains("EW")) moveDirection = moveDirection.replace("EW", "");

        if (moveDirection.isBlank()) return null;

        return Direction8.valueOf(moveDirection);
    }

    private void updateBattleground() {
        for (Tank tank : battleground.getTanks()) {
            tank.update(0.017f);
        }
    }

    public void stop() {
        this.stopped = true;
    }

    public void pause() {
        this.running = false;
    }

    public void resume() {
        this.running = true;
    }
}
