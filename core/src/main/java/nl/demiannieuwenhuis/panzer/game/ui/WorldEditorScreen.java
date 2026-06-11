package nl.demiannieuwenhuis.panzer.game.ui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import nl.demiannieuwenhuis.panzer.game.Panzer;
import nl.demiannieuwenhuis.panzer.game.WorldEditor;
import nl.demiannieuwenhuis.panzer.game.graphics.Renderer;
import nl.demiannieuwenhuis.panzer.game.model.tank.Shell;
import nl.demiannieuwenhuis.panzer.game.model.tank.Tank;
import nl.demiannieuwenhuis.panzer.game.model.world.Battleground;

public class WorldEditorScreen implements Screen {
    private static final float ZOOM_SPEED = 0.1f;
    private static final float MIN_ZOOM = 1.4f;
    private static final float MAX_ZOOM = 0.2f;

    private final Panzer game;

    private OrthographicCamera camera;

    private final Battleground battleground;
    private Renderer renderer;

    private WorldEditor worldEditor;

    private Thread worldEditorThread;

    public WorldEditorScreen(Panzer game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        battleground = new Battleground(1600, 900);
        renderer = new Renderer(0);
        worldEditor = new WorldEditor(battleground, renderer);
        worldEditorThread = Thread.ofPlatform().start(worldEditor);


        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                Vector3 before = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

                camera.zoom = MathUtils.clamp(camera.zoom + amountY * ZOOM_SPEED, MAX_ZOOM, MIN_ZOOM);
                camera.update();

                Vector3 after = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

                camera.position.add(before.x - after.x, before.y - after.y, 0);
                camera.update();
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.LIGHT_GRAY);
        renderer.getShapeRenderer().setProjectionMatrix(camera.combined);

        renderer.renderTileSurfaces(battleground);
        renderer.renderTileContents(battleground);
        renderer.renderTileOutlines(battleground, worldEditor);


    }

    @Override
    public void pause() {
        worldEditor.stop();
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
