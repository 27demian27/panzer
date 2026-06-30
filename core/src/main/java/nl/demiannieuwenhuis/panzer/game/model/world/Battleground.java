package nl.demiannieuwenhuis.panzer.game.model.world;

import lombok.Getter;
import lombok.Setter;
import nl.demiannieuwenhuis.panzer.game.model.tank.TankInputType;
import nl.demiannieuwenhuis.panzer.game.model.tank.Shell;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;
import nl.demiannieuwenhuis.panzer.game.net.dto.TankUpdate;
import nl.demiannieuwenhuis.panzer.game.net.dto.WorldUpdate;
import nl.demiannieuwenhuis.physics.util.CollisionData;
import nl.demiannieuwenhuis.physics.util.Collisions;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Battleground {

    private final float width;
    private final float height;

    public static final float TILE_SIZE = 20;
    public static final float WALL_THICKNESS = TILE_SIZE;
    private @Setter Tile[][] tileGrid;

    private final List<Tank> playerTanks = new CopyOnWriteArrayList<>();
    private final List<Tank> botTanks = new CopyOnWriteArrayList<>();
    private final List<Tank> tanks = new CopyOnWriteArrayList<>();
    private final List<Shell> shells = new CopyOnWriteArrayList<>();

    private long snapshotSequenceNumber = 0;

    public Battleground(float width, float height) {
        this.width = width;
        this.height = height;

        int rows = (int) Math.ceil(width / TILE_SIZE);
        int cols = (int) Math.ceil(height / TILE_SIZE);

        this.tileGrid = new Tile[rows][cols];
    }

    public WorldUpdate getUpdateSnapshot() {
        List<TankUpdate> tankUpdates = tanks.stream().map(Tank::getUpdateSnapshot).toList();
        return new WorldUpdate(++snapshotSequenceNumber, tankUpdates);
    }

    public Optional<Tank> findTankByUID(int UID) {
        return tanks.stream().filter(tank -> tank.UID == UID).findFirst();
    }

    public void setDefaultTileGrid() {
        for (int i = 0; i < tileGrid.length; i++) {
            for (int j = 0; j < tileGrid[i].length; j++) {
                    tileGrid[i][j] = new Tile(i * TILE_SIZE, j * TILE_SIZE, SurfaceType.SAND, null, null);
            }
        }
    }

    public Optional<Tile> findTile(float x, float y) {
        int i = (int) Math.floor(x / TILE_SIZE);
        int j = (int) Math.floor(y / TILE_SIZE);

        if (i < 0 || j < 0 || i >= tileGrid.length || j>= tileGrid[i].length)
            return Optional.empty();

        return Optional.of(tileGrid[i][j]);
    }

    public void resolveShellHits() {
        for (Tank tank : tanks) {
            shells.removeIf(shell -> {
                if (shell.getShooter() == tank) return false;
                CollisionData collisionData =  Collisions.rectCircle(tank.hitbox, shell.hitbox);
                if (collisionData.colliding) tank.damage(shell.getDamage());
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
                if (tiles == null) continue;

                for (Tile tile : List.of(tiles)) {
                    if (tile.contentType == ContentType.WALL) {
                        CollisionData collisionData = Collisions.rectRect(tank.hitbox, tile.getHitBox());
                        Collisions.correctPosition(tank.hitbox, tile.getHitBox(), collisionData);
                    }
                    else if (tile.contentType == ContentType.HEDGEHOG) {
                        CollisionData collisionData = Collisions.rectRect(tank.hitbox, tile.getHitBox());
                        Collisions.correctPosition(tank.hitbox, tile.getHitBox(), collisionData);
                        if (collisionData.colliding) tank.damage(0.2f);
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
