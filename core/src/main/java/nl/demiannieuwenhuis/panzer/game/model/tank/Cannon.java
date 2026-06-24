package nl.demiannieuwenhuis.panzer.game.model.tank;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class Cannon {

    private @Getter final float width;
    private @Getter final float length;
    private @Getter @Setter float angle;
    private @Getter final float shell_size;
    private @Getter final float shell_speed = 1200.0f;
    private @Getter final float shell_damage = 50.0f;
    private final float reload_time = 2.0f;
    private final float rotation_speed = 180.0f; // degrees/sec


    private @Setter short rotating_direction = 0;  // <0 left, 0 none, >0 right
    private long last_shot = 0;
    private boolean shoot_request;

    public Cannon(float width, float length) {
        this.width = width;
        this.length = length;
        this.shell_size = width / 2.0f;
    }

    public boolean tryShoot() {
        if (System.nanoTime() - last_shot >= reload_time * 1_000_000_000) {
            shoot_request = true;
            last_shot = System.nanoTime();
            return true;
        }

        return false;
    }

    public void update(float dt) {
        if (rotating_direction < 0)
            angle = angle - rotation_speed * dt;
        else if (rotating_direction > 0)
            angle = angle + rotation_speed * dt;

    }

    public boolean hasShootRequest() {
        return shoot_request;
    }

    public void clearShotRequest() {
        shoot_request = false;
    }
}
