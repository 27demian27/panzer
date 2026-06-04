package nl.demiannieuwenhuis.panzer.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import nl.demiannieuwenhuis.panzer.game.model.Battleground;
import nl.demiannieuwenhuis.panzer.game.model.Shell;
import nl.demiannieuwenhuis.panzer.game.model.Direction8;
import nl.demiannieuwenhuis.panzer.game.model.Tank;
import nl.demiannieuwenhuis.physics.util.Vector2D;

import java.util.ListIterator;

public class GameLoop implements Runnable {

    public final static float MAX_GAME_UPDATE_TIME = 0.017f;

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
                    updateBots();
                    updateBattleground();
                } else {
                    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                        resume();
                    }
                }
                Thread.sleep((long) (MAX_GAME_UPDATE_TIME * 1000));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateBots() {
        for (Tank bot : battleground.getBotTanks()) {
            bot.getBotScript().execute(MAX_GAME_UPDATE_TIME);
        }
    }

    private void handleControls() {
        if (battleground.getPlayerTanks() == null || battleground.getPlayerTanks().isEmpty())
            return;

        Tank playerTank = battleground.getPlayerTanks().getFirst();
        Direction8 direction = computePlayerDirection();

        if (direction == null) {
            playerTank.setStationary(true);
        } else {
            playerTank.setStationary(false);
            playerTank.setDirection(direction);
        }

        playerTank.cannon.setRotating_direction(computePlayerCannonRotationDirection(playerTank));

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            boolean shot = playerTank.cannon.tryShoot();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            pause();
        }
    }

    private short computePlayerCannonRotationDirection(Tank playerTank) {
        Vector2D mousePointer = new Vector2D(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        Vector2D cannonOrigin = new Vector2D(playerTank.hitbox.getCenterOfMass());
        float mouseAngle = (float) (mousePointer.subtract(cannonOrigin).angle() * (180 / Math.PI)) - 90.0f;

        float diff = mouseAngle - playerTank.cannon.getAngle();

        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;


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
            tank.update(MAX_GAME_UPDATE_TIME);
            if (tank.cannon.hasShootRequest()) {
                Shell shell = tank.shoot();
                battleground.addBullet(shell);
                tank.cannon.clearShotRequest();
            }
            battleground.clampTankPos(tank);
        }

        for (Shell shell : battleground.getShells()) {
            shell.update(MAX_GAME_UPDATE_TIME);
        }

        battleground.removeOutOfBoundsShells();
        battleground.resolveShellHits();

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
