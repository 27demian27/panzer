package nl.demiannieuwenhuis.panzer.game.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import nl.demiannieuwenhuis.panzer.game.GameLoop;
import nl.demiannieuwenhuis.panzer.game.Panzer;
import nl.demiannieuwenhuis.panzer.game.ai.AimBotScript;
import nl.demiannieuwenhuis.panzer.game.graphics.Renderer;
import nl.demiannieuwenhuis.panzer.game.io.BattleMap;
import nl.demiannieuwenhuis.panzer.game.model.tank.Shell;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;
import nl.demiannieuwenhuis.panzer.game.model.tank.TankInputType;
import nl.demiannieuwenhuis.panzer.game.model.world.Battleground;
import nl.demiannieuwenhuis.panzer.game.model.world.Tile;

public class GameScreen implements Screen {

    private final Panzer game;
    private BattleMap loadedBattleMap;

    private Battleground battleground;

    private Renderer renderer;

    private GameLoop gameLoop;
    private Thread gameThread;

    public GameScreen(Panzer game, BattleMap loadedBattleMap) {
        this.game = game;
        this.loadedBattleMap = loadedBattleMap;
        battleground = new Battleground(1600, 900);
        battleground.addTank(new Tank(100, 100, 30, 50, 1, TankInputType.PLAYER));
        battleground.addTank(new Tank(500, 500, 30, 50, 1, TankInputType.BOT));
        battleground.getBotTanks().getFirst().setBotScript(
            new AimBotScript(battleground.getBotTanks().getFirst(), battleground.getPlayerTanks().getFirst())
        );

        if (loadedBattleMap != null && loadedBattleMap.tileGrid.length > 0 && loadedBattleMap.tileGrid[0].length > 0)
            battleground.setTileGrid(loadedBattleMap.tileGrid);
        else
            battleground.setDefaultTileGrid();

        renderer = new Renderer(battleground.getTanks().size());
        gameLoop = new GameLoop(battleground, renderer);
        gameThread = Thread.ofPlatform().start(gameLoop);
    }

    @Override
    public void render(float delta) {
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

        if (gameLoop.isStopped()) {
            System.out.println("here");
            game.setScreen(new MainMenuScreen(game, loadedBattleMap));
            dispose();
        }
    }

    @Override
    public void pause() {
        gameLoop.stop();
    }

    @Override
    public void dispose() {
        renderer.dispose();

    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void hide() {}
    @Override public void resume() {}



}
