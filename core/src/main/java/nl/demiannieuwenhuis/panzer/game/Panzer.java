package nl.demiannieuwenhuis.panzer.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import nl.demiannieuwenhuis.panzer.game.model.world.Tile;
import nl.demiannieuwenhuis.panzer.game.ui.Assets;
import nl.demiannieuwenhuis.panzer.game.ui.MainMenuScreen;

public class Panzer extends Game {
    public SpriteBatch batch;
    public Assets assets;
    public FitViewport viewport;
    public ScreenViewport uiViewport ;

    public Stage uiStage;

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(16, 9);
        uiViewport = new ScreenViewport();
        uiStage = new Stage(new ScreenViewport(), batch);
        assets = new Assets();

        this.setScreen(new MainMenuScreen(this, null));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
        uiStage.dispose();
    }

}
