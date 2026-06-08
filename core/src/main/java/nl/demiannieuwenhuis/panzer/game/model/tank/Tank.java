package nl.demiannieuwenhuis.panzer.game.model.tank;

import lombok.Getter;
import lombok.Setter;
import nl.demiannieuwenhuis.panzer.game.ai.BotScript;
import nl.demiannieuwenhuis.panzer.game.ai.RandomizedBotScript;
import nl.demiannieuwenhuis.physics.rigidbody.shapes.Circle;
import nl.demiannieuwenhuis.physics.rigidbody.shapes.Rect;
import nl.demiannieuwenhuis.physics.util.Vector2D;

import static nl.demiannieuwenhuis.panzer.game.model.tank.Direction8.*;

public class Tank {
    public final Rect hitbox;
    public final Cannon cannon;

    private float max_health = 100.0f;
    private float current_health = max_health;

    private final @Getter TankInputType inputType;
    private @Getter BotScript botScript;

    private final @Getter float movement_speed = 78;
    private final float diag_components_speed = (float) Math.sqrt(Math.pow(movement_speed, 2) / 2.0);

    private @Getter @Setter Direction8 moveDirection;
    private @Getter @Setter Direction8 visualDirection;

    private @Getter @Setter boolean stationary;

    public Tank(float x, float y, float width, float height, double mass, TankInputType inputType) {
        this.inputType = inputType;
        this.hitbox = new Rect(mass, x, y, width, height);
        this.cannon = new Cannon(width / 6.0f, height / 2.0f);
        this.moveDirection = null;
        this.visualDirection = moveDirection;
        this.stationary = true;

        if (inputType.equals(TankInputType.BOT))
            botScript = new RandomizedBotScript(this);
    }

    public void update(float dt) {
        if (!stationary) {
            switch (moveDirection) {
                case W -> {
                    if (visualDirection == E || visualDirection == NE || visualDirection == SE)
                        visualDirection = E;
                    else
                        visualDirection = W;

                    hitbox.setX(hitbox.getX() - movement_speed * dt);
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
                    hitbox.setY(hitbox.getY() + movement_speed * dt);
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
                    hitbox.setX(hitbox.getX() + movement_speed * dt);
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
                    hitbox.setY(hitbox.getY() - movement_speed * dt);
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

    private Vector2D getCannonDirection() {
        return new Vector2D(
                Math.cos(Math.toRadians(cannon.getAngle() + 90.0f)),
                Math.sin(Math.toRadians(cannon.getAngle() + 90.0f))
            )
            .normalized();
    }

    public void resolveShellHit(Shell shell) {
        System.out.println(this + " HIT!");
        current_health -= shell.getDamage();
        System.out.println("new health: " + current_health);
    }

}
