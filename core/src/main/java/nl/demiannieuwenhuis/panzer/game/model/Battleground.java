package nl.demiannieuwenhuis.panzer.game.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Battleground {


    private final @Getter List<Tank> playerTanks = new ArrayList<>();
    private final @Getter List<Tank> botTanks = new ArrayList<>();
    private final @Getter List<Tank> tanks = new ArrayList<>();
    private final @Getter List<Shell> shells = new ArrayList<>();


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
