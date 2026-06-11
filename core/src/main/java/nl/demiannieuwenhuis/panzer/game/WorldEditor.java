package nl.demiannieuwenhuis.panzer.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import lombok.Getter;
import lombok.Setter;
import nl.demiannieuwenhuis.panzer.game.graphics.Renderer;
import nl.demiannieuwenhuis.panzer.game.model.world.Battleground;
import nl.demiannieuwenhuis.panzer.game.model.world.Tile;

public class WorldEditor implements Runnable {

    public final static float EDITOR_UPDATE_TIME = 0.017f;
    private final Battleground battleground;

    private final Renderer renderer;

    private @Getter boolean running;

    private @Getter boolean stopped;

    private @Getter Tile highlightedTile;

    public WorldEditor(Battleground battleground, Renderer renderer) {
        this.battleground = battleground;
        this.renderer = renderer;
    }

    @Override
    public void run() {
        running = true;
        try {
            while (!stopped) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    stop();
                }
                handleControls();

                Thread.sleep((long) (EDITOR_UPDATE_TIME * 1000));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void handleControls() {

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            battleground.findTile(mouseX, mouseY).ifPresent(
                tile -> {
                    if (highlightedTile != null)
                        highlightedTile.setHighlighted(false);
                    if (tile != highlightedTile) {
                        highlightedTile = tile;
                        highlightedTile.setHighlighted(true);
                    } else {
                        highlightedTile.setHighlighted(false);
                        highlightedTile = null;
                    }
                }
            );
        }



    }

    public void stop() {
        this.stopped = true;
    }
}
