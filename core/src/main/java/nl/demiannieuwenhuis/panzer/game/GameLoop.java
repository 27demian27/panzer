package nl.demiannieuwenhuis.panzer.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import lombok.Getter;
import nl.demiannieuwenhuis.panzer.game.ai.BotScript;
import nl.demiannieuwenhuis.panzer.game.graphics.Renderer;
import nl.demiannieuwenhuis.panzer.game.model.world.Battleground;
import nl.demiannieuwenhuis.panzer.game.model.tank.Shell;
import nl.demiannieuwenhuis.panzer.game.model.tank.Direction8;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;
import nl.demiannieuwenhuis.physics.util.Vector2D;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameLoop implements Runnable {

    public final static float MAX_GAME_UPDATE_TIME = 0.017f;

    private final Battleground battleground;

    private final Renderer renderer;
    private AtomicBoolean running;

    private AtomicBoolean stopped;

    public GameLoop(Battleground battleground, Renderer renderer) {
        this.battleground = battleground;
        this.renderer = renderer;
        this.running = new AtomicBoolean(false);
        this.stopped = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        running.set(true);
        Gdx.graphics.setCursor(renderer.getCrosshairCursor());
        try {
            Thread.sleep(100);
            while (!stopped.get()) {
                if (running.get()) {
                    handleControls();
                    updateBots();
                    updateBattleground();
                }
                Thread.sleep((long) (MAX_GAME_UPDATE_TIME * 1000));
            }
            System.out.println("stopped");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateBots() {
        for (Tank bot : battleground.getBotTanks()) {
            BotScript botScript = bot.getBotScript();
            if (botScript != null)
                botScript.execute(MAX_GAME_UPDATE_TIME);
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
            playerTank.setMoveDirection(direction);
        }

        playerTank.cannon.setRotating_direction(Tank.computeCannonRotationDirection(
            playerTank, new Vector2D(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()))
        );

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            boolean shot = playerTank.cannon.tryShoot();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            stop();
        }
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
        battleground.resolveWallCollisions();
    }

    public void stop() {
        running.set(false);
        stopped.set(true);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }

    public void pause() {
        running.set(false);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }

    public void resume() {
        if (!stopped.get())
            running.set(true);
        Gdx.graphics.setCursor(renderer.getCrosshairCursor());
    }

    public boolean isRunning() {
        return running.get();
    }

    public boolean isStopped() {
        return stopped.get();
    }
}
