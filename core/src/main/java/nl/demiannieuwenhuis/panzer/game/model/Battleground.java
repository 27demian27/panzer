package nl.demiannieuwenhuis.panzer.game.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Battleground {

    private @Getter Tank playerTank;

    private final @Getter List<Tank> tanks = new ArrayList<>();
    private final @Getter List<Shell> shells = new ArrayList<>();

    public void addPlayerTank(Tank tank) {
        playerTank = tank;
        tanks.add(tank);
    }

    public void addTank(Tank tank) {
        tanks.add(tank);
    }

    public void addBullet(Shell shell) {
        shells.add(shell);
    }

}
