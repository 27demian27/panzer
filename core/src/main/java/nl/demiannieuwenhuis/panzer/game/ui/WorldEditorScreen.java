package nl.demiannieuwenhuis.panzer.game.ui;


import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import nl.demiannieuwenhuis.panzer.game.Panzer;
import nl.demiannieuwenhuis.panzer.game.WorldEditor;
import nl.demiannieuwenhuis.panzer.game.graphics.Renderer;
import nl.demiannieuwenhuis.panzer.game.model.world.Battleground;
import nl.demiannieuwenhuis.panzer.game.model.world.SurfaceType;

public class WorldEditorScreen implements Screen {
    private static final float ZOOM_SPEED = 0.1f;
    private static final float MIN_ZOOM = 1.4f;
    private static final float MAX_ZOOM = 0.2f;

    private final Panzer game;
    private OrthographicCamera camera;
    private Stage stage;
    private Viewport uiViewport;
    private final Battleground battleground;
    private Renderer renderer;
    private WorldEditor worldEditor;
    private boolean panning, areaMultiSelecting, selectionDrawing;
    private float lastMouseX, lastMouseY;
    private float multiselectOriginX, multiselectOriginY;

    public WorldEditorScreen(Panzer game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        battleground = new Battleground(1600, 900);
        renderer = new Renderer(0);
        worldEditor = new WorldEditor(battleground, renderer);

        uiViewport = new ScreenViewport();
        stage = new Stage(uiViewport);

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, getEditorControls()));

        buildUI();
    }

    private void buildUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.bottom().left().pad(10);


        Skin skin = new Skin();

        BitmapFont font = game.assets.font;
        skin.add("default-font", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        skin.add("default", buttonStyle);

        for (SurfaceType surfaceType : SurfaceType.values()) {
            TextButton btn = new TextButton(surfaceType.name(), skin);
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    worldEditor.applySurfaceType(surfaceType);
                }
            });
            table.add(btn).pad(4).row();
        }

        stage.addActor(table);
    }

    private InputAdapter getEditorControls() {
        return new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {

                if (button == Input.Buttons.LEFT) {
                    Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
                    worldEditor.selectTile(worldPos.x, worldPos.y);
                    multiselectOriginX = worldPos.x;
                    multiselectOriginY = worldPos.y;
                    if (!areaMultiSelecting)
                        selectionDrawing = true;
                }

                if (button == Input.Buttons.RIGHT) {
                    panning = true;
                    lastMouseX = screenX;
                    lastMouseY = screenY;
                }

                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {

                if ( button == Input.Buttons.LEFT) {
                    selectionDrawing = false;
                }

                if (button == Input.Buttons.RIGHT) {
                    panning = false;
                }

                return true;
            }

            @Override
            public boolean keyDown(int keycode) {

                if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT) {
                    areaMultiSelecting = true;
                }

                return true;
            }

            @Override
            public boolean keyUp(int keycode) {

                if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT) {
                    areaMultiSelecting = false;
                }

                return true;
            }

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

            @Override
            public boolean touchDragged (int screenX, int screenY, int pointer) {

                if (panning) {
                    float dx = screenX - lastMouseX;
                    float dy = screenY - lastMouseY;
                    camera.position.add(-dx * camera.zoom, dy * camera.zoom, 0);
                    camera.update();

                    lastMouseX = screenX;
                    lastMouseY = screenY;
                }
                else if (selectionDrawing) {
                    Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
                    worldEditor.addTileSelection(worldPos.x, worldPos.y);
                }
                else if (areaMultiSelecting) {
                    Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
                    worldEditor.setTileSelection(multiselectOriginX, multiselectOriginY, worldPos.x, worldPos.y);
                }

                return true;
            }
        };
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.LIGHT_GRAY);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        renderer.getShapeRenderer().setProjectionMatrix(camera.combined);
        renderer.renderTileSurfaces(battleground);
        renderer.renderTileContents(battleground);
        renderer.renderTilesSelection(worldEditor);
        renderer.renderTileOutlines(battleground, panning);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void pause() {
        worldEditor.stop();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void resume() {}


}
