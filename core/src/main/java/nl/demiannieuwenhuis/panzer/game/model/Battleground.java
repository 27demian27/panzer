package nl.demiannieuwenhuis.panzer.game.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Getter
public class Battleground {

    private final float width;
    private final float height;

    private final List<Tank> playerTanks = new ArrayList<>();
    private final List<Tank> botTanks = new ArrayList<>();
    private final List<Tank> tanks = new ArrayList<>();
    private final List<Shell> shells = new ArrayList<>();

    public Battleground(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void removeOutOfBoundsShells() {
        ListIterator<Shell> iter = getShells().listIterator();
        while (iter.hasNext()) {
            Shell shell = iter.next();
            if (shell.hitbox.getX() < 0 || shell.hitbox.getX() >= width) {
                iter.remove();
            } else if (shell.hitbox.getY() < 0 || shell.hitbox.getY() >= height) {
                iter.remove();
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
