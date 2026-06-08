package nl.demiannieuwenhuis.panzer.game.ai;

import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;

import java.util.LinkedList;
import java.util.Queue;

public abstract class BotScript {

    protected Tank tank;
    protected Queue<BotInstruction> instructions;
    protected float timeInCurrentInstruction;

    protected BotScript(Tank tank) {
        this.tank = tank;
        instructions = new LinkedList<>();
        timeInCurrentInstruction = 0.0f;
    }


    public void execute(float dt) {
        if (instructions.isEmpty())
            instructions.add(generateInstruction());


        if (instructions.peek() == null)
            throw new RuntimeException("Error: instructions queue still empty after generation");


        BotInstruction currentInstruction;
        if ((timeInCurrentInstruction += dt) >= instructions.peek().duration()) {
            currentInstruction = instructions.poll();
            timeInCurrentInstruction = 0.0f;
        } else {
            currentInstruction = instructions.peek();
        }

        tank.setMoveDirection(currentInstruction.moveDirection());
        tank.cannon.setRotating_direction(currentInstruction.turretDirection());
        if (currentInstruction.shooting())
            tank.cannon.tryShoot();
        tank.setStationary(currentInstruction.stationary());
    }

    protected abstract BotInstruction generateInstruction();


}
