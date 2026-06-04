package nl.demiannieuwenhuis.panzer.game.model;

import lombok.Getter;
import nl.demiannieuwenhuis.physics.util.CollisionData;
import nl.demiannieuwenhuis.physics.util.Collisions;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Battleground {

    private final float width;
    private final float height;

    private final List<Tank> playerTanks = new CopyOnWriteArrayList<>();
    private final List<Tank> botTanks = new CopyOnWriteArrayList<>();
    private final List<Tank> tanks = new CopyOnWriteArrayList<>();
    private final List<Shell> shells = new CopyOnWriteArrayList<>();

    public Battleground(float width, float height) {
        this.width = width;
        this.height = height;
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
