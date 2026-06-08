package nl.demiannieuwenhuis.panzer.game.ai;

import nl.demiannieuwenhuis.panzer.game.model.tank.Direction8;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;

import java.util.Random;

public class RandomizedBotScript extends BotScript{

    public RandomizedBotScript(Tank tank) {
        super(tank);
    }
    @Override
    protected BotInstruction generateInstruction() {
        Random rand = new Random();
        float duration = rand.nextFloat() + 0.2f;
        int index = rand.nextInt(Direction8.values().length + 1);

        Direction8 moveDirection;
        boolean stationary;
        if (index == Direction8.values().length)  {
            stationary = true;
            moveDirection = null;
        } else {
            moveDirection = Direction8.values()[index];
            stationary = false;
        }

        boolean shooting = rand.nextBoolean();
        short turretDirection = (short) (rand.nextInt(2) - 1);
        return new BotInstruction(duration, moveDirection, turretDirection, shooting, stationary);
    }
}
