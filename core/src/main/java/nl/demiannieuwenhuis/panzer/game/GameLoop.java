package nl.demiannieuwenhuis.panzer.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import nl.demiannieuwenhuis.panzer.game.ai.BotScript;
import nl.demiannieuwenhuis.panzer.game.graphics.Renderer;
import nl.demiannieuwenhuis.panzer.game.model.tank.TankInputType;
import nl.demiannieuwenhuis.panzer.game.model.world.Battleground;
import nl.demiannieuwenhuis.panzer.game.model.tank.Shell;
import nl.demiannieuwenhuis.panzer.game.model.tank.Direction8;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;
import nl.demiannieuwenhuis.panzer.game.net.dto.udp.TankUpdate;
import nl.demiannieuwenhuis.panzer.game.net.dto.udp.WorldUpdate;
import nl.demiannieuwenhuis.panzer.game.net.server.BattleServer;
import nl.demiannieuwenhuis.panzer.game.net.client.ClientConnection;
import nl.demiannieuwenhuis.panzer.game.physics.util.Vector2D;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameLoop implements Runnable {

    public final static float MAX_GAME_UPDATE_TIME = 0.017f;
    private static final float POSITION_INTERPOLATION_FACTOR = 0.10f;

    private final Battleground battleground;
    private final ClientConnection playerClientConnection;
    private final BattleServer battleServer;
    private final Tank playerTank;
    private final Renderer renderer;
    private AtomicBoolean running;

    private AtomicBoolean stopped;


    public GameLoop(
        Battleground battleground,
        ClientConnection playerClientConnection,
        BattleServer battleServer,
        Renderer renderer
    ) {
        this.battleground = battleground;
        this.playerClientConnection = playerClientConnection;
        this.battleServer = battleServer;
        if (playerClientConnection == null) {
            playerTank = battleground.getPlayerTanks().getFirst();
        } else {
            playerTank = playerClientConnection.getPlayerTank();
        }

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

                    if (playerClientConnection != null) {
                        updateClients();
                    } else {
                        updateBots();
                    }

                    updateBattleground();
                }
                Thread.sleep((long) (MAX_GAME_UPDATE_TIME * 1000));
            }
            Gdx.app.log("GameLoop", "GameLoop stopped.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateClients() {
        try {
            playerClientConnection.sendTankPacket();

            if (battleServer != null) { // HOST
                WorldUpdate serverSnapshot = battleground.getUpdateSnapshot();
                Gdx.app.log("GameLoop", "New server snapshot: \n" + serverSnapshot);

                battleServer.setLatestSnapshot(serverSnapshot);
            }

            WorldUpdate worldUpdate = playerClientConnection.pollLatestWorldUpdate();
            if (worldUpdate != null) {
                applySnapshot(worldUpdate);
            }


        } catch (SocketTimeoutException ignored) {
            System.out.println("socket timeout");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void applySnapshot(WorldUpdate worldUpdate) {

        for (TankUpdate tankUpdate : worldUpdate.tankUpdates()) {
            if (tankUpdate.UID() != playerTank.UID) {
                Optional<Tank> optionalTank = battleground.findTankByUID(tankUpdate.UID());

                optionalTank.ifPresentOrElse(
                    tank -> {
                        tank.setCurrentHealth(tankUpdate.currentHealth());
                        tank.setMoveDirection(tankUpdate.moveDirection());
                        tank.setVisualDirection(tankUpdate.visualDirection());
                        tank.cannon.setAngle(tankUpdate.cannonAngle());

                        tank.setDisabled(tankUpdate.disabled());
                        if (tank.isDisabled()) {
                            return;
                        }

                        double interpolationX =
                            tank.hitbox.getX() + (tankUpdate.x() - tank.hitbox.getX()) * POSITION_INTERPOLATION_FACTOR;
                        double interpolationY =
                            tank.hitbox.getY() + (tankUpdate.y() - tank.hitbox.getY()) * POSITION_INTERPOLATION_FACTOR;
                        tank.hitbox.setX(interpolationX);
                        tank.hitbox.setY(interpolationY);

                        if (tankUpdate.shooting()) tank.cannon.tryShoot();
                        tank.setStationary(tankUpdate.stationary());
                    }, () -> {
                        Tank newTank = new Tank(
                            tankUpdate.UID(),
                            tankUpdate.x(),
                            tankUpdate.y(),
                            30,
                            50,
                            1,
                            TankInputType.PLAYER
                        );
                        if (tankUpdate.shooting()) newTank.cannon.tryShoot();
                        battleground.addTank(newTank);
                    }
                    );

            }
        }
    }

    private void updateBots() {
        for (Tank bot : battleground.getBotTanks()) {
            if (bot.isDisabled()) continue;

            BotScript botScript = bot.getBotScript();
            if (botScript != null)
                botScript.execute(MAX_GAME_UPDATE_TIME);
        }
    }

    private void handleControls() {

        if (playerTank == null || playerTank.isDisabled())
            return;

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
            if (tank.isDisabled()) continue;

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

//        battleground.resolveTileCollisions(); for ruts. (experimental)
        battleground.removeOutOfBoundsShells();
        battleground.resolveShellHits();
        battleground.resolveWallCollisions();
        battleground.resolveHazardCollisions();
    }

    public void stop() {
        running.set(false);
        stopped.set(true);
        if (playerClientConnection != null) playerClientConnection.close();
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
