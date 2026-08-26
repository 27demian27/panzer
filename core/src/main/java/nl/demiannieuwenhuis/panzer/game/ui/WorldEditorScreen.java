package nl.demiannieuwenhuis.panzer.game.ui;


import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import nl.demiannieuwenhuis.panzer.game.Panzer;
import nl.demiannieuwenhuis.panzer.game.WorldEditor;
import nl.demiannieuwenhuis.panzer.game.graphics.Renderer;
import nl.demiannieuwenhuis.panzer.game.io.BattleMap;
import nl.demiannieuwenhuis.panzer.game.io.BattleMapWriter;
import nl.demiannieuwenhuis.panzer.game.model.world.Battleground;
import nl.demiannieuwenhuis.panzer.game.model.world.ContentType;
import nl.demiannieuwenhuis.panzer.game.model.world.SurfaceType;
import nl.demiannieuwenhuis.panzer.game.model.world.Tile;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class WorldEditorScreen implements Screen {
    private static final float ZOOM_SPEED = 0.1f;
    private static final float MIN_ZOOM = 1.4f;
    private static final float MAX_ZOOM = 0.2f;
    private static final int MAX_TANK_COUNT = 4;

    private final Panzer game;
    private OrthographicCamera camera;
    private Stage stage;
    private Viewport uiViewport;
    private final Battleground battleground;
    private final Renderer renderer;
    private final BattleMapWriter battleMapWriter;
    private WorldEditor worldEditor;
    private BattleMap loadedBattleMap;
    private boolean panning, areaMultiSelecting, selectionDrawing;
    private float lastMouseX, lastMouseY;
    private float multiselectOriginX, multiselectOriginY;
    private SelectionMode selectionMode;

    public WorldEditorScreen(Panzer game, BattleMap loadedBattleMap) {
        this.game = game;
        this.loadedBattleMap = loadedBattleMap;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        battleground = new Battleground(1600, 900);
        renderer = new Renderer();
        selectionMode = SelectionMode.NORMAL;
        battleMapWriter = new BattleMapWriter(battleground.getTileGrid());
        worldEditor = new WorldEditor(battleground, renderer);

        uiViewport = new ScreenViewport();
        stage = new Stage(uiViewport);

        if (loadedBattleMap != null && loadedBattleMap.tileDataGrid.length > 0 && loadedBattleMap.tileDataGrid[0].length > 0) {
            battleground.setTileGrid(BattleMap.tileDataGridToTileGrid(loadedBattleMap.tileDataGrid));
            for (Tile[] tiles : battleground.getTileGrid()) {
                for (Tile tile : tiles) {
                    if (tile.contentType.isSpawnPoint()) {
                        worldEditor.getSpawnPoints().add(tile.contentType);
                    }
                }
            }
        }
        else {
            Gdx.app.log("WorldEditorScreen", "loading default map...");
            battleground.setDefaultTileGrid();
        }


        buildUI();
    }

    private void buildUI() {
        Table surfaceTable = new Table();
        surfaceTable.setFillParent(true);
        surfaceTable.bottom().left().pad(10);

        Table contentTable = new Table();
        contentTable.setFillParent(true);
        contentTable.top().left().pad(10);

        Skin skin = new Skin();

        BitmapFont font = game.assets.font;
        skin.add("default-font", font);

        TextButton.TextButtonStyle defaultBtnStyle = new TextButton.TextButtonStyle();
        defaultBtnStyle.font = font;
        defaultBtnStyle.up = colorDrawable(new Color(1, 1, 1, 0.4f));


        for (SurfaceType surfaceType : SurfaceType.values()) {
            if (surfaceType.getId().equals("empty") || surfaceType.getId().equals("unknown")) continue;

            TextButton btn = new TextButton(surfaceType.name(), getSurfaceTextButtonStyle(surfaceType, font));
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    worldEditor.applySurfaceType(surfaceType);
                }
            });
            surfaceTable.add(btn).pad(4).minWidth(120).row();
        }

        for (ContentType contentType : ContentType.values()) {
            switch (contentType) {
                case EMPTY, UNKNOWN, SPAWN_POINT_A, SPAWN_POINT_B, SPAWN_POINT_C, SPAWN_POINT_D -> {}
                default -> {
                    TextButton btn = new TextButton(
                        contentType.name().replace('_', ' '), defaultBtnStyle
                    );
                    btn.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            worldEditor.applyTileContent(contentType);
                        }
                    });
                    contentTable.add(btn).pad(4).minWidth(140).row();
                }
            }

        }

        TextButton spawnBtn = new TextButton("SPAWN POINT", defaultBtnStyle);
        spawnBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                worldEditor.setSpawnPoint();
            }
        });
        contentTable.add(spawnBtn).pad(4).minWidth(140).row();

        TextButton clearBtn = new TextButton("CLEAR", defaultBtnStyle);
        clearBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                worldEditor.clearTileContent();
            }
        });
        contentTable.add(clearBtn).pad(4).minWidth(140).row();


        TextButton saveBtn = new TextButton("SAVE", defaultBtnStyle);
        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {


                javax.swing.SwingUtilities.invokeLater(() -> {
                    String result = JOptionPane.showInputDialog(
                        null,
                        "Map name:",
                        "Save Map",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        null,
                        loadedBattleMap.name
                    ).toString();

                    if (result != null && !result.trim().isEmpty()) {
                        Gdx.app.postRunnable(() -> {
                            try {
                                BattleMap battleMap = BattleMap.of(result.trim(), battleground.getTileGrid());
                                battleMapWriter.saveBattleMap(battleMap);
                                loadedBattleMap = battleMap;
                                Gdx.app.log("WorldEditorScreen", "Battle map saved as: " + result.trim());
                            } catch (IOException e) {
                                Gdx.app.error("WorldEditorScreen", "Failed to save battle map: " + e.getMessage());
                            }
                        });
                    }
                });
            }
        });
        Table actionsTable = new Table();
        actionsTable.setFillParent(true);
        actionsTable.top().right().pad(10);
        actionsTable.add(saveBtn).pad(4).row();


        TextButton normalDrawingModeButton = new TextButton("NORMAL", defaultBtnStyle);
        normalDrawingModeButton.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               selectionMode = SelectionMode.NORMAL;
               Gdx.app.log("WorldEditorScreen", "Switching selection mode to: " + selectionMode);
           }
        });

        TextButton fillDrawingModeButton = new TextButton("FILL", defaultBtnStyle);
        fillDrawingModeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectionMode = SelectionMode.FILL;
                Gdx.app.log("WorldEditorScreen", "Switching selection mode to: " + selectionMode);
            }
        });

        Table toolTable = new Table();
        toolTable.setFillParent(true);
        toolTable.bottom().right().pad(10);
        toolTable.add(normalDrawingModeButton).pad(4).row();
        toolTable.add(fillDrawingModeButton).pad(4).row();

        stage.addActor(toolTable);
        stage.addActor(actionsTable);
        stage.addActor(contentTable);
        stage.addActor(surfaceTable);
    }

    private Drawable colorDrawable(Color fill) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(fill);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(tex);
    }

    private TextButton.TextButtonStyle getSurfaceTextButtonStyle(SurfaceType surfaceType, BitmapFont font) {
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.up   = colorDrawable(surfaceType.getTerrainColor());
        return btnStyle;
    }

    private InputAdapter getEditorControls() {
        return new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {

                if (button == Input.Buttons.LEFT) {
                    Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
                    if (selectionMode == SelectionMode.NORMAL) {
                        worldEditor.selectTile(worldPos.x, worldPos.y);
                        multiselectOriginX = worldPos.x;
                        multiselectOriginY = worldPos.y;
                        if (!areaMultiSelecting)
                            selectionDrawing = true;
                    } else if (selectionMode == SelectionMode.FILL) {
                        worldEditor.selectFillTiles(worldPos.x, worldPos.y);
                    }

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

                if (keycode == Input.Keys.ESCAPE) {
                    game.setScreen(new MainMenuScreen(game, loadedBattleMap));
                    dispose();
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
                } else if (selectionMode == SelectionMode.FILL) {
                    return false;
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

    @Override public void show() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, getEditorControls()));
    }

    @Override public void hide() {}
    @Override public void resume() {}


}
