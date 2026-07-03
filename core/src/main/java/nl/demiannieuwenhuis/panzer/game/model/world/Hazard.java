package nl.demiannieuwenhuis.panzer.game.model.world;

import lombok.AllArgsConstructor;
import nl.demiannieuwenhuis.physics.rigidbody.Body;

@AllArgsConstructor
public class Hazard<T extends Body> {
    public final Tile tile;
    public final T hitbox;
}
