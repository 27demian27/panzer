package nl.demiannieuwenhuis.panzer.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import lombok.Getter;
import lombok.Setter;
import nl.demiannieuwenhuis.panzer.game.graphics.Renderer;
import nl.demiannieuwenhuis.panzer.game.model.world.Battleground;
import nl.demiannieuwenhuis.panzer.game.model.world.Tile;

public class WorldEditor {

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

    
    public void highlightTile(float x, float y) {
        battleground.findTile(x, y).ifPresent(
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

    public void stop() {
        this.stopped = true;
    }
}
