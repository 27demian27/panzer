package nl.demiannieuwenhuis.panzer.game.ai;

import nl.demiannieuwenhuis.panzer.game.model.Direction8;

public record BotInstruction(
    float duration,
    Direction8 moveDirection,
    short turretDirection,
    boolean shooting,
    boolean stationary
) {}
