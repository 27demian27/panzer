package nl.demiannieuwenhuis.panzer.game.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import nl.demiannieuwenhuis.panzer.game.WorldEditor;
import nl.demiannieuwenhuis.panzer.game.model.tank.Direction8;
import nl.demiannieuwenhuis.panzer.game.model.tank.Shell;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;
import nl.demiannieuwenhuis.panzer.game.model.world.*;

import java.util.*;

public class Renderer {
    public static final Color HULL_COLOR = new Color(0.427f, 0.439f, 0.310f, 1.0f);
    public static final Color TURRET_COLOR = new Color(HULL_COLOR).sub(0.05f, 0.05f, 0.05f, 0.0f);
    public static final Color TRACK_COLOR = new Color(Color.DARK_GRAY);
    public static final Color WALL_COLOR = new Color(Color.GRAY);
    public static final Color BUSH_COLOR = new Color(0.153f, 0.478f, 0.110f, 0.8f);
    public static final Color EXPLOSIVE_BARREL_COLOR = new Color(0.612f, 0.09f, 0.09f, 1.0f);

    private static final float[] tileDiscoloration =
        new float[] {0.03f, 0.04f, -0.04f, 0.02f, 0.03f, 0.05f, 0.00f, -0.04f, -0.02f, -0.01f};

    public final Animator animator;


    private final @Getter Cursor crosshairCursor;
    private final Pixmap crosshairPixmap;


    private @Getter final ShapeRenderer shapeRenderer;

    public Renderer() {
        this(1);
    }
    public Renderer(int tankCount) {
        this.shapeRenderer = new ShapeRenderer();
        this.animator = new Animator(shapeRenderer);

        this.crosshairPixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        this.crosshairCursor = createCrosshair();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private Cursor createCrosshair() {
        int centerX = crosshairPixmap.getWidth() / 2;
        int centerY = crosshairPixmap.getHeight() / 2;
        int radius = 4;

        crosshairPixmap.setColor(Color.BLACK);
        crosshairPixmap.drawCircle(centerX, centerY, radius);
        crosshairPixmap.drawLine(centerX, 12, centerX, 20);
        crosshairPixmap.drawLine(12, centerY, 20, centerY);
        return Gdx.graphics.newCursor(
            crosshairPixmap,
            crosshairPixmap.getWidth() / 2,
            crosshairPixmap.getHeight() / 2
        );
    }

    public void renderTank(Tank tank, float dt) {
        float x = (float) tank.hitbox.getX();
        float y = (float) tank.hitbox.getY();
        float tank_width = (float) tank.hitbox.getWidth();
        float tank_length = (float) tank.hitbox.getHeight();
        float centerX = x + tank_width / 2.0f;
        float centerY = y + tank_length / 2.0f;

        float tracksSpacingX = tank_width / 6.0f;
        float tracksSpacingY = tank_length / 12.0f;
        float tracksWidth = tank_width / 3.0f;
        float tracksLength = tank_length - 2 * tracksSpacingY;

        float turretWidth = tank_width / 1.25f;
        float turretLength = turretWidth;
        float cannonWidth = tank.cannon.getWidth();
        float cannonLength = tank.cannon.getLength();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // LEFT TRACK
        shapeRenderer.setColor(TRACK_COLOR);
        shapeRenderer.rect(
            x - tracksSpacingX , y + tracksSpacingY,
            centerX - (x - tracksSpacingX), centerY - (y + tracksSpacingY),
            tracksWidth, tracksLength,
            1.0f, 1.0f,
            Direction8.getRotation(tank.getVisualDirection())
        );

        // RIGHT TRACK
        shapeRenderer.setColor(TRACK_COLOR);
        shapeRenderer.rect(
            x + tank_width - tracksSpacingX , y + tracksSpacingY,
            centerX - (x + tank_width - tracksSpacingX), centerY - (y + tracksSpacingY),
            tracksWidth, tracksLength,
            1.0f, 1.0f,
            Direction8.getRotation(tank.getVisualDirection())
        );

        // HULL
        shapeRenderer.setColor(HULL_COLOR);
        shapeRenderer.rect(
            x, y,
            tank_width / 2.0f, tank_length / 2.0f,
            tank_width, tank_length,
            1.0f, 1.0f,
            Direction8.getRotation(tank.getVisualDirection())
        );
        shapeRenderer.setColor(new Color(HULL_COLOR).add(0.05f, 0.05f, 0.05f, 1.0f));
        shapeRenderer.rect(
            x + tank_width / 8.0f, y + tank_width / 10.0f,
            tank_width / 2.0f - tank_width / 8.0f, tank_length / 2.0f - tank_width / 8.0f,
            tank_width - 2 * (tank_width / 8.0f), tank_length / 6.0f,
            1.0f, 1.0f,
            Direction8.getRotation(tank.getVisualDirection())
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

        renderTankHealthBar(tank);

        animator.tankAnimations(tank, dt);

        shapeRenderer.end();
    }

    public void renderTankHealthBar(Tank tank) {
        float centerX = (float) (tank.hitbox.getX() + tank.hitbox.getWidth() / 2.0f);
        float centerY = (float) (tank.hitbox.getY() + tank.hitbox.getHeight() / 2.0f);
        float width = (float) (tank.hitbox.getWidth() * 3.0f);
        float height = 8.0f;
        float y = (float) (centerY - tank.hitbox.getHeight());
        float x = centerX - width / 2;

        shapeRenderer.setColor(new Color(0.573f, 0.122f, 0.122f, 1.0f));
        shapeRenderer.rect(x, y, width, height);

        float currentHealthBarWidth = Math.max(0, width * (tank.getCurrentHealth() / tank.getMaxHealth()));
        shapeRenderer.setColor(Color.SCARLET);
        shapeRenderer.rect(x, y, currentHealthBarWidth, height);
    }

    public void renderShell(Shell shell) {
        float x = (float) shell.hitbox.getX();
        float y = (float) shell.hitbox.getY();
        float width = (float) shell.hitbox.radius;
        float angle = (float) Math.toDegrees(shell.getDirection().angle()) + 90.0f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(x, y, 0, 0, width, width * 2.0f, 1.0f, 1.0f, angle);
        shapeRenderer.end();
    }

    public void renderTileOutlines(Battleground battleground, boolean panning) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(
            panning ?
                new Color(Color.BLACK).sub(0, 0, 0, 0.95f) :
                new Color(Color.BLACK).sub(0, 0, 0, 0.9f)
        );

        Tile[][] tileGrid = battleground.getTileGrid();
        for (int i = 0; i < tileGrid.length; i++) {
            for (int j = 0; j < tileGrid[i].length; j++) {
                shapeRenderer.rect(
                    i * Battleground.TILE_SIZE,
                    j * Battleground.TILE_SIZE,
                    Battleground.TILE_SIZE,
                    Battleground.TILE_SIZE
                );
            }
        }
        shapeRenderer.end();
    }

    public void renderTilesSelection(WorldEditor worldEditor) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Set<Tile> selectedTiles = worldEditor.getSelectedTiles();

        if (!selectedTiles.isEmpty())
            shapeRenderer.setColor(new Color(Color.BLUE).sub(0, 0, 0, 0.5f));

        selectedTiles.forEach(tile -> shapeRenderer.rect(
            (float) tile.getHitBox().getX(),
            (float) tile.getHitBox().getY(),
            Battleground.TILE_SIZE,
            Battleground.TILE_SIZE
        ));
        shapeRenderer.end();
    }

    public void renderTileSurfaces(Battleground battleground) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Tile[][] tileGrid = battleground.getTileGrid();
        for (int i = 0; i < tileGrid.length; i++) {
            for (int j = 0; j < tileGrid[i].length; j++) {
                renderTile(i * Battleground.TILE_SIZE, j * Battleground.TILE_SIZE, tileGrid[i][j]);
            }
        }


        shapeRenderer.end();
    }

    private void renderTile(float x, float y, Tile tile) {
        int subTilesSize = 5;
        int subTilesCount = (int) (Battleground.TILE_SIZE / 5);
        for (int i = 0; i < subTilesCount; i++) {
            for (int j = 0; j < subTilesCount; j++) {

                int i1 = (i * j + j + i) % tileDiscoloration.length;

                Color subTileColor = new Color(tile.getSurfaceType().getTerrainColor())
                    .add(tileDiscoloration[i1],
                        tileDiscoloration[i1],
                        tileDiscoloration[i1],
                        0);

                shapeRenderer.setColor(subTileColor);
                shapeRenderer.rect(
                    x + i * subTilesSize,
                    y + j * subTilesSize,
                    subTilesSize,
                    subTilesSize
                );
            }
        }
    }

    public void renderTileContents(Battleground battleground) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Tile[][] tileGrid = battleground.getTileGrid();
        for (int i = 0; i < tileGrid.length; i++) {
            for (int j = 0; j < tileGrid[i].length; j++) {
                renderTileContent(
                    i * Battleground.TILE_SIZE,
                    j * Battleground.TILE_SIZE,
                    Battleground.TILE_SIZE,
                    Battleground.TILE_SIZE,
                    tileGrid[i][j],
                    battleground.getHazards()
                );
            }
        }

        shapeRenderer.end();
    }

    private void renderTileContent(float x, float y, float width, float height, Tile tile, List<Hazard<?>> hazards) {
        if (tile.contentType == null) return;

        switch (tile.contentType) {
            case WALL -> renderWall(x, y, width, height);
            case BUSH -> renderBush(x, y, width, height);
            case HEDGEHOG -> renderHedgehog(x, y, width, height);
            case EXPLOSIVE_BARREL -> {
                Optional<ExplosiveBarrel> barrel = hazards.stream()
                    .filter(hazard -> hazard.tile == tile && hazard instanceof ExplosiveBarrel)
                    .map(hazard -> (ExplosiveBarrel) hazard)
                    .findFirst();
                barrel.ifPresent(this::renderExplosiveBarrel);
            }
        }
    }

    private void renderWall(float x, float y, float width, float height) {
        float centerX = x + width / 2.0f;
        float centerY = y + height / 2.0f;
        shapeRenderer.setColor(WALL_COLOR);
        shapeRenderer.rect(
            centerX - Battleground.WALL_THICKNESS / 2.0f,
            centerY - Battleground.WALL_THICKNESS / 2.0f,
            Battleground.WALL_THICKNESS,
            Battleground.WALL_THICKNESS
        );
    }

    private void renderBush(float x, float y, float width, float height) {
        float centerX = x + width / 2.0f;
        float centerY = y + height / 2.0f;
        shapeRenderer.setColor(BUSH_COLOR);
        shapeRenderer.circle(centerX, centerY, width);
    }

    private void renderHedgehog(float x, float y, float width, float height) {
        float centerX = x + width / 2.0f;
        float centerY = y + height / 2.0f;
        float hedgehogThickness = 5.0f;

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(
            x, centerY - hedgehogThickness / 2.0f,
            Battleground.TILE_SIZE / 2.0f, hedgehogThickness / 2.0f,
            Battleground.TILE_SIZE, hedgehogThickness,
            1.0f, 1.0f,
            -45.0f
        );

        shapeRenderer.rect(
            x, centerY - hedgehogThickness / 2.0f,
            Battleground.TILE_SIZE / 2.0f, hedgehogThickness / 2.0f,
            Battleground.TILE_SIZE, hedgehogThickness,
            1.0f, 1.0f,
            45.0f
        );
    }

    private void renderExplosiveBarrel(ExplosiveBarrel barrel) {
        if (barrel.isExploded()) return;

        shapeRenderer.setColor(EXPLOSIVE_BARREL_COLOR);
        shapeRenderer.circle(
            (float) (barrel.hitbox.getX() + barrel.hitbox.radius),
            (float) (barrel.hitbox.getY() + barrel.hitbox.radius),
            (Battleground.TILE_SIZE / 2.0f) * 0.8f
        );
    }

    public void updateCrosshair() {
//        tank.isStatonairy();

    }

    public void dispose() {
        shapeRenderer.dispose();
        crosshairPixmap.dispose();
    }

}
