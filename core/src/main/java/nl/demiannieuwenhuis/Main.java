package nl.demiannieuwenhuis;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import nl.demiannieuwenhuis.panzer.Battleground;
import nl.demiannieuwenhuis.panzer.game.GameLoop;
import nl.demiannieuwenhuis.panzer.game.model.Tank;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private Battleground battleground;

    private ShapeRenderer shapeRenderer;

    private GameLoop gameLoop;
    private Thread gameThread;

    @Override
    public void create() {
        battleground = new Battleground();
        battleground.addPlayerTank(new Tank(100, 100, 30, 50, 9000));
        shapeRenderer = new ShapeRenderer();
        gameLoop = new GameLoop(battleground);
        gameThread = Thread.ofPlatform().start(gameLoop);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.796f,0.741f,0.576f, 1.0f);

        if (battleground.getTanks() != null) {
            for (Tank tank : battleground.getTanks()) {
                renderTank(tank);
            }
        }

    }

    private void renderTank(Tank tank) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.427f, 0.439f, 0.310f, 1.0f));
        shapeRenderer.rect(
            (float) tank.hitbox.getX(), (float) tank.hitbox.getY(),
            (float) (tank.hitbox.getWidth() / 2.0f), (float) (tank.hitbox.getHeight() / 2.0f),
            (float) tank.hitbox.getWidth(), (float) tank.hitbox.getHeight(),
            1.0f, 1.0f,
            tank.getRotation()
        );
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        try {
            gameLoop.stop();
            gameThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
