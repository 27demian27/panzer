package nl.demiannieuwenhuis.panzer;

import lombok.Getter;
import nl.demiannieuwenhuis.panzer.game.model.Bullet;
import nl.demiannieuwenhuis.panzer.game.model.Tank;

import java.util.ArrayList;
import java.util.List;

public class Battleground {

    private @Getter List<Tank> tanks = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();

    public void addTank(Tank tank) {
        tanks.add(tank);
    }

}
