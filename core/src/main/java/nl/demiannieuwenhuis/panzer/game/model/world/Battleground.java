package nl.demiannieuwenhuis.panzer.game.model.world;

import lombok.Getter;
import nl.demiannieuwenhuis.panzer.game.model.tank.Direction8;
import nl.demiannieuwenhuis.panzer.game.model.tank.TankInputType;
import nl.demiannieuwenhuis.panzer.game.model.tank.Shell;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;
import nl.demiannieuwenhuis.physics.util.CollisionData;
import nl.demiannieuwenhuis.physics.util.Collisions;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Battleground {

    private final float width;
    private final float height;

    public static final float TILE_SIZE = 20;
    public static final float WALL_THICKNESS = TILE_SIZE;
    private final Tile[][] tileGrid;

    private final List<Tank> playerTanks = new CopyOnWriteArrayList<>();
    private final List<Tank> botTanks = new CopyOnWriteArrayList<>();
    private final List<Tank> tanks = new CopyOnWriteArrayList<>();
    private final List<Shell> shells = new CopyOnWriteArrayList<>();

    public Battleground(float width, float height) {
        this.width = width;
        this.height = height;

        int rows = (int) Math.ceil(width / TILE_SIZE);
        int cols = (int) Math.ceil(height / TILE_SIZE);

        this.tileGrid = new Tile[rows][cols];
        initializeTileGrid();
    }

    private void initializeTileGrid() {
        ContentType contentType = null;
        for (int i = 0; i < tileGrid.length; i++) {
            for (int j = 0; j < tileGrid[i].length; j++) {
                contentType = null;
                if (i == 2 || j == 2) contentType = ContentType.WALL;
                if (i < 2 * tileGrid.length / 5 || i > 3 * tileGrid.length / 5)
                    tileGrid[i][j] = new Tile(j * TILE_SIZE, i * TILE_SIZE, SurfaceType.SAND, contentType, null);
                else
                    tileGrid[i][j] = new Tile(j * TILE_SIZE, i * TILE_SIZE, SurfaceType.TARMAC,contentType,  null);
            }
        }
    }

    public void resolveShellHits() {
        for (Tank tank : tanks) {
            shells.removeIf(shell -> {
                if (shell.getShooter() == tank) return false;
                CollisionData collisionData =  Collisions.rectCircle(tank.hitbox, shell.hitbox);
                if (collisionData.colliding) tank.resolveShellHit(shell);
                return collisionData.colliding;
            });
        }
    }

    public void removeOutOfBoundsShells() {
        shells.removeIf(
            shell -> shell.hitbox.getX() < 0 ||
                shell.hitbox.getX() >= width ||
                shell.hitbox.getY() < 0 ||
                shell.hitbox.getY() >= height
        );
    }

    public void resolveWallCollisions() {
        for (Tank tank : tanks) {
            for (Tile[] tiles : List.of(tileGrid)) {
                for (Tile tile : List.of(tiles)) {
                    if (tile.contentType == ContentType.WALL) {
                        CollisionData collisionData = Collisions.rectRect(tank.hitbox, tile.getHitBox());
                        Collisions.correctPosition(tank.hitbox, tile.getHitBox(), collisionData);
                    }
                }
            }
        }
    }

    public void clampTankPos(Tank tank) {
        if (tank.hitbox.getX() < 0)
            tank.hitbox.setX(0);
        if (tank.hitbox.getX() + tank.hitbox.getWidth() >= width)
            tank.hitbox.setX(width - tank.hitbox.getWidth() -0.01);
        if (tank.hitbox.getY() < 0)
            tank.hitbox.setY(0);
        if (tank.hitbox.getY() + tank.hitbox.getHeight() >= height)
            tank.hitbox.setY(height - tank.hitbox.getHeight() -0.01);
    }

    public void addTank(Tank tank) {
        if (tank.getInputType().equals(TankInputType.PLAYER))
            playerTanks.add(tank);
        else if (tank.getInputType().equals(TankInputType.BOT))
            botTanks.add(tank);
        tanks.add(tank);
    }

    public void addBullet(Shell shell) {
        shells.add(shell);
    }



}
