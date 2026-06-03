package nl.demiannieuwenhuis.panzer;

import lombok.Getter;
import nl.demiannieuwenhuis.panzer.game.model.Bullet;
import nl.demiannieuwenhuis.panzer.game.model.Tank;

import java.util.ArrayList;
import java.util.List;

public class Battleground {

    private @Getter Tank playerTank;

    private final @Getter List<Tank> tanks = new ArrayList<>();
    private final @Getter List<Bullet> bullets = new ArrayList<>();

    public void addPlayerTank(Tank tank) {
        playerTank = tank;
        tanks.add(tank);
    }

    public void addTank(Tank tank) {
        tanks.add(tank);
    }

}
