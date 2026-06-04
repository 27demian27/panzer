package nl.demiannieuwenhuis.panzer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import nl.demiannieuwenhuis.panzer.game.graphics.Renderer;
import nl.demiannieuwenhuis.panzer.game.model.Battleground;
import nl.demiannieuwenhuis.panzer.game.GameLoop;
import nl.demiannieuwenhuis.panzer.game.model.Shell;
import nl.demiannieuwenhuis.panzer.game.model.Tank;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private Battleground battleground;

    private Renderer renderer;

    private GameLoop gameLoop;
    private Thread gameThread;

    @Override
    public void create() {
        battleground = new Battleground();
        battleground.addPlayerTank(new Tank(100, 100, 30, 50, 9000));
        renderer = new Renderer();
        gameLoop = new GameLoop(battleground);
        gameThread = Thread.ofPlatform().start(gameLoop);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.796f,0.741f,0.576f, 1.0f);
        
        if (battleground.getShells() != null) {
            for (Shell shell : battleground.getShells()) {
                renderer.renderShell(shell);
            }
        }

        if (battleground.getTanks() != null) {
            for (Tank tank : battleground.getTanks()) {
                renderer.renderTank(tank);
            }
        }


    }


    @Override
    public void dispose() {
        renderer.dispose();
        try {
            gameLoop.stop();
            gameThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
