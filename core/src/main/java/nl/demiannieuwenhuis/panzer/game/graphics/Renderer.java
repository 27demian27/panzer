package nl.demiannieuwenhuis.panzer.game.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.demiannieuwenhuis.panzer.game.WorldEditor;
import nl.demiannieuwenhuis.panzer.game.model.tank.Direction8;
import nl.demiannieuwenhuis.panzer.game.model.tank.Shell;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;
import nl.demiannieuwenhuis.panzer.game.model.world.Battleground;
import nl.demiannieuwenhuis.panzer.game.model.world.ContentType;
import nl.demiannieuwenhuis.panzer.game.model.world.SurfaceType;
import nl.demiannieuwenhuis.panzer.game.model.world.Tile;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Renderer {
    public static final Color SAND_COLOR = new Color(0.796f,0.741f,0.576f, 1.0f);
    public static final Color HULL_COLOR = new Color(0.427f, 0.439f, 0.310f, 1.0f);
    public static final Color TURRET_COLOR = new Color(HULL_COLOR).sub(0.05f, 0.05f, 0.05f, 0.0f);
    public static final Color TRACK_COLOR = new Color(Color.DARK_GRAY);
    public static final Color TRACK_RUTS_COLOR = new Color(SAND_COLOR).sub(0.05f, 0.05f, 0.05f, 0.0f);
    public static final Color WALL_COLOR = new Color(Color.GRAY);


    private final @Getter Cursor crosshairCursor;
    private final Pixmap crosshairPixmap;

    private final int MAX_RUTS;

    private @Getter final ShapeRenderer shapeRenderer;

    private final Queue<RectArgs> trackRuts;

    public Renderer(int tankCount) {
        this.shapeRenderer = new ShapeRenderer();
        this.trackRuts = new LinkedList<>();
        MAX_RUTS = 200 * tankCount;

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

    public void renderTank(Tank tank) {
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
        float rutY = y + tracksSpacingY + centerY - (y + tracksSpacingY);
        float rutLength = tracksWidth;


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

        float leftTrackX = x - tracksSpacingX;
        float leftTrackY = rutY;
        float rightTrackX = x + tank_width - tracksSpacingX;
        float rightTrackY = rutY;

        RectArgs leftTrackRut = new RectArgs(
            leftTrackX, leftTrackY,
            centerX - leftTrackX, centerY - leftTrackY,
            tracksWidth, rutLength,
            1f, 1f,
            Direction8.getRotation(tank.getVisualDirection())
        );
        RectArgs rightTrackRut = new RectArgs(
            rightTrackX, rightTrackY,
            centerX - rightTrackX, centerY - rightTrackY,
            tracksWidth, rutLength,
            1f, 1f,
            Direction8.getRotation(tank.getVisualDirection())
        );

        trackRuts.add(leftTrackRut);
        trackRuts.add(rightTrackRut);

        while (trackRuts.size() > MAX_RUTS * 2) {
            trackRuts.poll();
        }

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

        shapeRenderer.end();
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

    @AllArgsConstructor
    private class RectArgs {
        float x, y;
        float originX, originY;
        float width, height;
        float scaleX, scaleY;
        float angle;
    }

    public void renderTrackRuts() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float a = 0.0f;
        for (RectArgs rut : trackRuts) {
            shapeRenderer.setColor(TRACK_RUTS_COLOR.r, TRACK_RUTS_COLOR.g, TRACK_RUTS_COLOR.b, a);
            shapeRenderer.rect(
                rut.x, rut.y,
                rut.originX, rut.originY,
                rut.width, rut.height,
                rut.scaleX, rut.scaleY,
                rut.angle
            );
            a += 1f / MAX_RUTS;
        }
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
            (float) tile.getHitBox().getY(),
            (float) tile.getHitBox().getX(),
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
                shapeRenderer.setColor(getTerrainColor(tileGrid[i][j].getSurfaceType()));
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

    public void renderTileContents(Battleground battleground) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Tile[][] tileGrid = battleground.getTileGrid();
        for (int i = 0; i < tileGrid.length; i++) {
            for (int j = 0; j < tileGrid[i].length; j++) {
                renderTileContent(
                    j * Battleground.TILE_SIZE,
                    i * Battleground.TILE_SIZE,
                    Battleground.TILE_SIZE,
                    Battleground.TILE_SIZE,
                    tileGrid[i][j]
                );
            }
        }

        shapeRenderer.end();
    }

    private void renderTileContent(float x, float y, float width, float height, Tile tile) {
        if (tile.contentType == null) return;

        if (tile.contentType == ContentType.WALL) {
            renderWall(x, y, width, height);
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

    private static Color getTerrainColor(SurfaceType surfaceType) {
        return switch (surfaceType) {
            case GRASS -> new Color(0.553f, 0.702f, 0.427f, 1.0f);
            case SAND -> new Color(0.796f,0.741f,0.576f, 1.0f);
            case TARMAC -> new Color(0.549f, 0.549f, 0.549f, 1.0f);
            case null -> new Color(Color.WHITE);
        };
    }

    public void updateCrosshair() {
//        tank.isStatonairy();

    }

    public void dispose() {
        shapeRenderer.dispose();
        crosshairPixmap.dispose();
    }

}
