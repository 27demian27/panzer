package nl.demiannieuwenhuis.panzer.game;

import lombok.Getter;
import nl.demiannieuwenhuis.panzer.game.graphics.Renderer;
import nl.demiannieuwenhuis.panzer.game.model.world.*;

import java.util.*;

public class WorldEditor {

    private final Battleground battleground;

    private final Renderer renderer;

    private @Getter boolean running;

    private @Getter boolean stopped;

    private @Getter Set<Tile> selectedTiles;

    private @Getter Set<ContentType> spawnPoints;

    public WorldEditor(Battleground battleground, Renderer renderer) {
        this.battleground = battleground;
        this.renderer = renderer;
        selectedTiles = new HashSet<>();
        spawnPoints = new HashSet<>();
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

    public void selectFillTiles(float x, float y) {
        selectedTiles.clear();
        battleground.findTile(x, y).ifPresent(
            tile -> {
                Tile[][] tiles = battleground.getTileGrid();
                int i = (int) Math.floor(x / Battleground.TILE_SIZE);
                int j = (int) Math.floor(y / Battleground.TILE_SIZE);
                fillSelection(tiles, i, j, new HashSet<>(), tile.getSurfaceType());
            }
        );
    }

    public void fillSelection(Tile[][] tiles, int i, int j, Set<Tile> visited, SurfaceType surfaceType) {
        Tile center = tiles[i][j];

        if (visited.contains(center))
            return;

        if (center.getSurfaceType() == surfaceType) {
            selectedTiles.add(center);
            visited.add(center);
        } else {
            return;
        }

        if (i > 0)
            fillSelection(tiles, i - 1, j, visited, surfaceType);
        if (i < tiles.length - 1)
            fillSelection(tiles, i + 1, j, visited, surfaceType);
        if (j > 0)
            fillSelection(tiles, i, j - 1, visited, surfaceType);
        if (j < tiles[i].length - 1)
            fillSelection(tiles, i, j + 1, visited, surfaceType);
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

        for (int tx = tileX1; tx <= tileX2; tx++) {
            for (int ty = tileY1; ty <= tileY2; ty++) {
                battleground.findTile(tx * Battleground.TILE_SIZE, ty * Battleground.TILE_SIZE)
                    .ifPresent(tile -> selectedTiles.add(tile));
            }
        }
    }

    public void applySurfaceType(SurfaceType surfaceType) {
        selectedTiles.forEach(tile -> tile.setSurfaceType(surfaceType));
    }

    public void applyTileContent(ContentType contentType) {
        selectedTiles.forEach(tile -> {
            tile.setContentType(contentType);
            if (tile.contentType == ContentType.EXPLOSIVE_BARREL) {
                battleground.addHazard(ExplosiveBarrel.create(tile, 80.0f));
            }
        });
    }

    public void setSpawnPoint() {
        if (selectedTiles.size() != 1) return;

        Tile tile = selectedTiles.stream().findFirst().orElseThrow();

        if (tile.contentType.isSpawnPoint()) return;

        if (!spawnPoints.contains(ContentType.SPAWN_POINT_A)) {
            tile.setContentType(ContentType.SPAWN_POINT_A);
            spawnPoints.add(ContentType.SPAWN_POINT_A);
        }
        else if (!spawnPoints.contains(ContentType.SPAWN_POINT_B)) {
            tile.setContentType(ContentType.SPAWN_POINT_B);
            spawnPoints.add(ContentType.SPAWN_POINT_B);
        }
        else if (!spawnPoints.contains(ContentType.SPAWN_POINT_C)) {
            tile.setContentType(ContentType.SPAWN_POINT_C);
            spawnPoints.add(ContentType.SPAWN_POINT_C);
        }
        else if (!spawnPoints.contains(ContentType.SPAWN_POINT_D)) {
            tile.setContentType(ContentType.SPAWN_POINT_D);
            spawnPoints.add(ContentType.SPAWN_POINT_D);
        }
    }

    public void clearTileContent() {
        selectedTiles.forEach(tile -> {
            if (tile.contentType.isSpawnPoint()) {
                spawnPoints.remove(tile.contentType);
            }
            tile.setContentType(ContentType.EMPTY);
        });
    }

    public void stop() {
        this.stopped = true;
    }
}
