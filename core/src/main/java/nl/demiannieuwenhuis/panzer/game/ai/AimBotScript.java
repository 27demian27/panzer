package nl.demiannieuwenhuis.panzer.game.ai;

import lombok.Getter;
import nl.demiannieuwenhuis.panzer.game.model.tank.Direction8;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;
import nl.demiannieuwenhuis.physics.util.Vector2D;

import java.util.Random;

import static nl.demiannieuwenhuis.panzer.game.model.tank.Direction8.*;
import static nl.demiannieuwenhuis.panzer.game.model.tank.Direction8.E;
import static nl.demiannieuwenhuis.panzer.game.model.tank.Direction8.N;
import static nl.demiannieuwenhuis.panzer.game.model.tank.Direction8.NE;
import static nl.demiannieuwenhuis.panzer.game.model.tank.Direction8.NW;
import static nl.demiannieuwenhuis.panzer.game.model.tank.Direction8.S;
import static nl.demiannieuwenhuis.panzer.game.model.tank.Direction8.SE;
import static nl.demiannieuwenhuis.panzer.game.model.tank.Direction8.SW;
import static nl.demiannieuwenhuis.panzer.game.model.tank.Direction8.W;

public class AimBotScript extends BotScript{

    private Tank targetTank;
    private @Getter Vector2D futurePos;

    public AimBotScript(Tank tank, Tank targetTank) {
        super(tank);
        this.targetTank = targetTank;
    }

    @Override
    protected BotInstruction generateInstruction() {
        float duration = 0.05f;

        short turretDirection = calculateTurretDirection(duration);
        return new BotInstruction(duration, null, turretDirection, true, true);
    }

    private short calculateTurretDirection(float duration) {
        float targetSpeed = targetTank.getMovement_speed();
        float targetDiagComponentsSpeed = targetTank.getDiag_components_speed();
        Direction8 targetTankMoveDirection = targetTank.getMoveDirection();
        Vector2D targetCurrentPos = targetTank.hitbox.getCenterOfMass();
        float shellFlyingTime =
            (float) (targetCurrentPos.subtract(tank.hitbox.getCenterOfMass()).length() /  tank.cannon.getShell_speed());

        duration += shellFlyingTime;

        this.futurePos = switch (targetTankMoveDirection) {
            case null -> targetCurrentPos;
            case W -> new Vector2D(targetCurrentPos.x - targetSpeed * duration, targetCurrentPos.y);
            case NW -> new Vector2D(
                targetCurrentPos.x - targetDiagComponentsSpeed * duration,
                targetCurrentPos.y + targetDiagComponentsSpeed * duration
            );
            case N -> new Vector2D(targetCurrentPos.x, targetCurrentPos.y + targetSpeed * duration);
            case NE -> new Vector2D(
                targetCurrentPos.x + targetDiagComponentsSpeed * duration,
                targetCurrentPos.y + targetDiagComponentsSpeed * duration
            );
            case E -> new Vector2D(targetCurrentPos.x + targetSpeed * duration, targetCurrentPos.y);
            case SE -> new Vector2D(
                targetCurrentPos.x + targetDiagComponentsSpeed * duration,
                targetCurrentPos.y - targetDiagComponentsSpeed * duration
            );
            case S -> new Vector2D(targetCurrentPos.x, targetCurrentPos.y - targetSpeed * duration);
            case SW -> new Vector2D(
                targetCurrentPos.x - targetDiagComponentsSpeed * duration,
                targetCurrentPos.y - targetDiagComponentsSpeed * duration
            );
        };

        return Tank.computeCannonRotationDirection(tank, futurePos);
    }

}
