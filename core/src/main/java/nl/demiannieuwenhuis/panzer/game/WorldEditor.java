package nl.demiannieuwenhuis.panzer.game;

import com.badlogic.gdx.math.Vector4;
import lombok.Getter;
import nl.demiannieuwenhuis.panzer.game.graphics.Renderer;
import nl.demiannieuwenhuis.panzer.game.model.world.Battleground;
import nl.demiannieuwenhuis.panzer.game.model.world.SurfaceType;
import nl.demiannieuwenhuis.panzer.game.model.world.Tile;

import java.util.*;

public class WorldEditor {

    public final static float EDITOR_UPDATE_TIME = 0.017f;
    private final Battleground battleground;

    private final Renderer renderer;

    private @Getter boolean running;

    private @Getter boolean stopped;

    private @Getter Set<Tile> selectedTiles;

    public WorldEditor(Battleground battleground, Renderer renderer) {
        this.battleground = battleground;
        this.renderer = renderer;
        selectedTiles = new HashSet<>();
    }


    public void selectTile(float x, float y) {
        battleground.findTile(x, y).ifPresent(
            tile -> {
                if (selectedTiles.contains(tile)) {
                    tile.setSelected(false);
                    selectedTiles.remove(tile);
                }
                else {
                    selectedTiles.clear();
                    tile.setSelected(true);
                    selectedTiles.add(tile);
                }
            }
        );
    }

    public void addTileSelection(float x, float y) {
        battleground.findTile(x, y).ifPresent(tile -> selectedTiles.add(tile));
    }

    public void setTileSelection(float x1, float y1, float x2, float y2) {
        selectedTiles.clear();
        if (x1 > x2) { float tmp = x1; x1 = x2; x2 = tmp; }
        if (y1 > y2) { float tmp = y1; y1 = y2; y2 = tmp; }

        int tileX1 = (int) Math.floor(x1 / Battleground.TILE_SIZE);
        int tileY1 = (int) Math.floor(y1 / Battleground.TILE_SIZE);
        int tileX2 = (int) Math.floor(x2 / Battleground.TILE_SIZE);
        int tileY2 = (int) Math.floor(y2 / Battleground.TILE_SIZE);

        for (int tx = tileX1; tx < tileX2; tx++) {
            for (int ty = tileY1; ty < tileY2; ty++) {
                battleground.findTile(tx * Battleground.TILE_SIZE, ty * Battleground.TILE_SIZE)
                    .ifPresent(tile -> selectedTiles.add(tile));
            }
        }
    }

    public void applySurfaceType(SurfaceType surfaceType) {
        selectedTiles.forEach(tile -> tile.setSurfaceType(surfaceType));
    }

    public void stop() {
        this.stopped = true;
    }
}
