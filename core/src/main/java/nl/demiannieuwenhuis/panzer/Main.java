package nl.demiannieuwenhuis.panzer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import nl.demiannieuwenhuis.panzer.game.graphics.Renderer;
import nl.demiannieuwenhuis.panzer.game.model.world.Battleground;
import nl.demiannieuwenhuis.panzer.game.GameLoop;
import nl.demiannieuwenhuis.panzer.game.model.tank.Shell;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;
import nl.demiannieuwenhuis.panzer.game.model.tank.TankInputType;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private Battleground battleground;

    private Renderer renderer;

    private GameLoop gameLoop;
    private Thread gameThread;

    @Override
    public void create() {
        battleground = new Battleground(1600, 900);
        battleground.addTank(new Tank(100, 100, 30, 50, 1, TankInputType.PLAYER));
        battleground.addTank(new Tank(500, 500, 30, 50, 1, TankInputType.BOT));
        renderer = new Renderer(battleground.getTanks().size());
        gameLoop = new GameLoop(battleground, renderer);
        gameThread = Thread.ofPlatform().start(gameLoop);
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.WHITE);

        renderer.renderTileSurfaces(battleground);
        if (battleground.getShells() != null) {
            for (Shell shell : battleground.getShells()) {
                renderer.renderShell(shell);
            }
        }

        renderer.renderTileContents(battleground);

        // Big performance hit
//        renderer.renderTrackRuts();

        if (battleground.getTanks() != null) {
            for (Tank tank : battleground.getTanks()) {
                renderer.renderTank(tank);
            }
        }

        renderer.updateCrosshair();
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
