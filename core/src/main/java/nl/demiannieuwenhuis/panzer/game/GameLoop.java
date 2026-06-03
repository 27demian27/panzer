package nl.demiannieuwenhuis.panzer.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import nl.demiannieuwenhuis.panzer.Battleground;
import nl.demiannieuwenhuis.panzer.game.model.Direction8;
import nl.demiannieuwenhuis.panzer.game.model.Tank;

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
        battleground.getPlayerTank().setDirection(direction);
    }

    private Direction8 computePlayerDirection() {
        String moveDirection = "";
        if (Gdx.input.isKeyPressed(Input.Keys.W)) moveDirection += "N";
        if (Gdx.input.isKeyPressed(Input.Keys.S)) moveDirection += "S";
        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveDirection += "E";
        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveDirection += "W";

        if (moveDirection.contains("NS")) moveDirection = moveDirection.replace("NS", "");
        if (moveDirection.contains("EW")) moveDirection = moveDirection.replace("EW", "");

        System.out.println("moveDirection: " + moveDirection + "\n");

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
