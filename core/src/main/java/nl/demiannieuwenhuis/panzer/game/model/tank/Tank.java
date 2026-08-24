package nl.demiannieuwenhuis.panzer.game.model.tank;

import lombok.Getter;
import lombok.Setter;
import nl.demiannieuwenhuis.panzer.game.ai.BotScript;
import nl.demiannieuwenhuis.panzer.game.net.data.udp.TankUpdate;
import nl.demiannieuwenhuis.panzer.game.physics.rigidbody.Circle;
import nl.demiannieuwenhuis.panzer.game.physics.rigidbody.Rect;
import nl.demiannieuwenhuis.panzer.game.physics.util.Vector2D;

import static nl.demiannieuwenhuis.panzer.game.model.tank.Direction8.*;

public class Tank {
    public static final float STATIONARY_RPM = 600;
    public static final float MOVING_RPM = 2200;

    public final int UID;

    public final Rect hitbox;
    public final Cannon cannon;

    private @Getter float maxHealth = 100.0f;
    private @Getter @Setter float currentHealth = maxHealth;

    private final @Getter TankInputType inputType;
    private @Getter @Setter BotScript botScript;

    private final @Getter float movementSpeed = 78;
    private final @Getter float diag_components_speed = (float) Math.sqrt(Math.pow(movementSpeed, 2) / 2.0);
    private @Getter float engineRpm;

    private @Getter @Setter Direction8 moveDirection;
    private @Getter @Setter Direction8 visualDirection;

    private @Getter @Setter boolean stationary;
    private @Getter @Setter boolean disabled;

    public Tank(int UID, float x, float y, float width, float height, double mass, TankInputType inputType) {
        this.UID = UID;
        this.inputType = inputType;
        this.hitbox = new Rect(mass, x, y, width, height);
        this.cannon = new Cannon(width / 6.0f, height / 2.0f);
        this.moveDirection = NONE;
        this.visualDirection = moveDirection;
        this.engineRpm = STATIONARY_RPM;
        this.stationary = true;
        this.disabled = false;
    }

    public TankUpdate getUpdateSnapshot() {
        return new TankUpdate(
            UID,
            (float) hitbox.getX(),
            (float) hitbox.getY(),
            currentHealth,
            moveDirection,
            visualDirection,
            cannon.getAngle(),
            cannon.hasShootRequest(),
            stationary,
            disabled
        );
    }

    public void update(float dt) {
        this.engineRpm = (stationary ? STATIONARY_RPM : MOVING_RPM);

        if (!stationary) {
            switch (moveDirection) {
                case NONE -> {}
                case W -> {
                    if (visualDirection == E || visualDirection == NE || visualDirection == SE)
                        visualDirection = E;
                    else
                        visualDirection = W;

                    hitbox.setX(hitbox.getX() - movementSpeed * dt);
                }
                case NW -> {
                    if (visualDirection == SE || visualDirection == S || visualDirection == E)
                        visualDirection = SE;
                    else
                        visualDirection = NW;
                    hitbox.setX(hitbox.getX() - diag_components_speed * dt);
                    hitbox.setY(hitbox.getY() + diag_components_speed * dt);
                }
                case N -> {
                    if (visualDirection == S || visualDirection == SW || visualDirection == SE)
                        visualDirection = S;
                    else
                        visualDirection = N;
                    hitbox.setY(hitbox.getY() + movementSpeed * dt);
                }
                case NE -> {
                    if (visualDirection == SW || visualDirection == S || visualDirection == W)
                        visualDirection = SW;
                    else
                        visualDirection = NE;
                    hitbox.setX(hitbox.getX() + diag_components_speed * dt);
                    hitbox.setY(hitbox.getY() + diag_components_speed * dt);
                }
                case E -> {
                    if (visualDirection == W || visualDirection == NW || visualDirection == SW)
                        visualDirection = W;
                    else
                        visualDirection = E;
                    hitbox.setX(hitbox.getX() + movementSpeed * dt);
                }
                case SE -> {
                    if (visualDirection == NW || visualDirection == N || visualDirection == W)
                        visualDirection = NW;
                    else
                        visualDirection = SE;
                    hitbox.setX(hitbox.getX() + diag_components_speed * dt);
                    hitbox.setY(hitbox.getY() - diag_components_speed * dt);
                }
                case S -> {
                    if (visualDirection == N || visualDirection == NW || visualDirection == NE)
                        visualDirection = N;
                    else
                        visualDirection = S;
                    hitbox.setY(hitbox.getY() - movementSpeed * dt);
                }
                case SW -> {
                    if (visualDirection == NE || visualDirection == N || visualDirection == E)
                        visualDirection = NE;
                    else
                        visualDirection = SW;
                    hitbox.setX(hitbox.getX() - diag_components_speed * dt);
                    hitbox.setY(hitbox.getY() - diag_components_speed * dt);
                }
            }
        }

        cannon.update(dt);
    }


    /**
     * Spawn new Shell from middle of tank.
     * @return Shell to be added to the Battlefield
     */
    public Shell shoot() {
        return new Shell(
            cannon.getShell_speed(),
            cannon.getShell_damage(),
            this,
            getCannonDirection(),
            new Circle(10, hitbox.getCenterOfMass().x, hitbox.getCenterOfMass().y, cannon.getShell_size())
        );

    }

    public static short computeCannonRotationDirection(Tank tank, Vector2D desiredPoint) {
        Vector2D cannonOrigin = new Vector2D(tank.hitbox.getCenterOfMass());
        float angle = (float) (desiredPoint.subtract(cannonOrigin).angle() * (180 / Math.PI)) - 90.0f;

        float diff = angle - tank.cannon.getAngle();

        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;


        if (diff > -1.6f && diff < 1.6f)
            return 0;

        if (diff > 0)
            return 1;
        if (diff < 0)
            return -1;

        return 0;
    }

    public Rect getLeftTrackHitbox() {
        float tank_width = (float) hitbox.getWidth(); float tank_length = (float) hitbox.getHeight();
        float x = (float) hitbox.getX(); float y = (float) hitbox.getY();
        float centerX = x + tank_width / 2.0f;
        float centerY = y + tank_length / 2.0f;

        // bad hardcoding
        float tracksSpacingX = tank_width / 6.0f;
        float tracksSpacingY = tank_length / 12.0f;
        float tracksWidth = tank_width / 6.0f;
        float tracksLength = tank_length - 2 * tracksSpacingY;
        float leftTrackX = x - tracksSpacingX;
        float leftTrackY = y + tracksSpacingY;

        // moet draaien om middelpunt
        Rect rect = new Rect(1, leftTrackX, leftTrackY + tracksLength / 2.0f, tracksWidth, tracksLength / 2.0f);
        double oldRotation = rect.getRotation();
        rect.setRotation(Direction8.getRotation(moveDirection));
        rect.rotateAroundPoint(new Vector2D(centerX, centerY),  rect.getRotation() - oldRotation);

        return rect;
    }

    public Rect getRightTrackHitbox() {
        float tank_width = (float) hitbox.getWidth(); float tank_length = (float) hitbox.getHeight();
        float x = (float) hitbox.getX(); float y = (float) hitbox.getY();
        float centerX = x + tank_width / 2.0f;
        float centerY = y + tank_length / 2.0f;

        // bad hardcoding
        float tracksSpacingX = tank_width / 6.0f;
        float tracksSpacingY = tank_length / 12.0f;
        float tracksWidth = tank_width / 6.0f;
        float tracksLength = tank_length - 2 * tracksSpacingY;
        float rightTrackX = x + tank_width - tracksSpacingX;
        float rightTrackY = y + tracksSpacingY;

        // moet draaien om middelpunt
        Rect rect = new Rect(1, rightTrackX, rightTrackY + tracksLength / 2.0f, tracksWidth, tracksLength / 2.0f);
        rect.setRotation(Direction8.getRotation(moveDirection));
        rect.rotateAroundPoint(new Vector2D(centerX, centerY), rect.getRotation());


        return rect;
    }

    private Vector2D getCannonDirection() {
        return new Vector2D(
                Math.cos(Math.toRadians(cannon.getAngle() + 90.0f)),
                Math.sin(Math.toRadians(cannon.getAngle() + 90.0f))
            )
            .normalized();
    }

    public void damage(float damage) {
        currentHealth = Math.max(0.0f, currentHealth -damage);

        if (currentHealth <= 0.00f) {
            disable();
        }
    }

    private void disable() {
        cannon.setRotating_direction((short) 0);
        disabled = true;
    }

}
