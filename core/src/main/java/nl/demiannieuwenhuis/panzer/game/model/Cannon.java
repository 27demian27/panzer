package nl.demiannieuwenhuis.panzer.game.model;

import lombok.Getter;
import lombok.Setter;

public class Cannon {

    private @Getter float angle;
    private final float bullet_size = 10.0f;
    private final float bullet_speed = 1.0f;
    private final float bullet_damage = 10.0f;
    private final float reload_time = 2.0f;

    private final float rotation_speed = 180.0f; // degrees/sec
    private @Setter short rotating_direction = 0;  // <0 left, 0 none, >0 right

    public Cannon() {}

    public void shoot() {
        System.out.println("BOOM!!!");
    }

    public void update(float dt) {
        if (rotating_direction < 0)
            angle = angle - rotation_speed * dt;
        else if (rotating_direction > 0)
            angle = angle + rotation_speed * dt;

    }
}
