package nl.demiannieuwenhuis.panzer.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import nl.demiannieuwenhuis.panzer.game.Panzer;
import nl.demiannieuwenhuis.panzer.game.io.BattleMap;
import nl.demiannieuwenhuis.panzer.game.io.BattleMapLoader;
import nl.demiannieuwenhuis.panzer.game.io.BattleMapWriter;
import nl.demiannieuwenhuis.panzer.game.net.GameRoom;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;

public class MainMenuScreen implements Screen {

    private final Panzer game;
    private final Stage stage;
    private final Skin skin;

    private BattleMap loadedBattleMap;
    private Label mapNameLabel;

    public MainMenuScreen(Panzer game, BattleMap loadedBattleMap) {
        this.game = game;

        this.stage = new Stage(new ScreenViewport(), game.batch);
        this.loadedBattleMap = loadedBattleMap;

        stage.addListener(new InputListener() {

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
                    Gdx.app.exit();
                    return true;
                }

                return false;
            }
        });

        this.skin = new Skin();

        BitmapFont font = game.assets.font;
        skin.add("default-font", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;

        skin.add("default", buttonStyle);

        buildUI();
    }

    private void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label title = new Label("Panzer", skin);
        title.setFontScale(2.5f);
        title.setColor(0.95f, 0.90f, 0.65f, 1f);

        Label sub = new Label("Select your action.", skin);
        sub.setColor(0.88f, 0.88f, 0.78f, 1f);

        mapNameLabel = new Label("map: " + getMapName(), skin);
        mapNameLabel.setFontScale(0.7f);
        mapNameLabel.setColor(Color.GRAY);

        TextButton battleBtn = new TextButton("Battle!", skin);
        TextButton editBtn = new TextButton("Edit BattleMap", skin);
        TextButton loadBattleMapBtn = new TextButton("Load BattleMap", skin);
        TextButton createRoomBtn = new TextButton("Create Room", skin);
        TextButton joinRoomBtn = new TextButton("Join Room", skin);

        loadBattleMapBtn.setColor(Color.WHITE);

        battleBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                startVSAiBattle();
//                startOnlineBattle();
            }
        });

        editBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new WorldEditorScreen(game, loadedBattleMap));
            }
        });

        loadBattleMapBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SwingUtilities.invokeLater(() -> {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("Load Battle Map");
                    chooser.setCurrentDirectory(new File(Gdx.files.getLocalStoragePath() + BattleMapWriter.SAVES_DIR));
                    chooser.setFileFilter(new FileNameExtensionFilter(
                        "Battle Map files (.map)", "map"
                    ));

                    int result = chooser.showOpenDialog(null);
                    if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        Gdx.app.postRunnable(() -> {
                            try {
                                loadedBattleMap = BattleMapLoader.loadBattleMap(file);
                                mapNameLabel.setText("map: " + getMapName());
                            } catch (IOException e) {
                                Gdx.app.error("LoadBattleMap", "Failed: " + e.getMessage());
                            }
                        });
                    }
                });
            }
        });

        createRoomBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Create Room pressed");
                    GameRoom gameRoom = new GameRoom(loadedBattleMap);
                    gameRoom.startControlServer();
                    gameRoom.startBattleServer();
                    joinOnlineBattle(gameRoom.code);
            }
        });

        joinRoomBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SwingUtilities.invokeLater(() -> {
                    String roomCode = JOptionPane.showInputDialog(null, "Enter room code:", "Join Room", JOptionPane.PLAIN_MESSAGE);
                    if (roomCode != null && !roomCode.trim().isEmpty()) {
                        Gdx.app.postRunnable(() -> {
                            Gdx.app.log("MainMenuScreen", "Joining Room with code: " + roomCode.trim() + "...");
                            joinOnlineBattle(roomCode.trim());
                        });
                    }
                });
            }
        });

        Table actionsTable = new Table();
        actionsTable.setFillParent(true);
        actionsTable.top().right().pad(10);
        actionsTable.add(loadBattleMapBtn).pad(4).row();
        stage.addActor(actionsTable);

        Table roomTable = new Table();
        roomTable.setFillParent(true);
        roomTable.top().left().pad(10);
        roomTable.add(createRoomBtn).pad(4).row();
        roomTable.add(joinRoomBtn).pad(4).row();
        stage.addActor(roomTable);

        root.add(title).padBottom(20);
        root.row();
        root.add(sub).padBottom(20);
        root.row();
        root.add(battleBtn).width(200).height(30).padBottom(0);
        root.row();
        root.add(mapNameLabel).padBottom(20);
        root.row();
        root.add(editBtn).width(200).height(60);
    }

    private void startVSAiBattle() {
        game.setScreen(new GameScreen(game, loadedBattleMap, false, ""));
        dispose();
    }

    private void joinOnlineBattle(String roomCode) {
        game.setScreen(new GameScreen(game, loadedBattleMap, true, roomCode));
        dispose();
    }

    private String getMapName() {
        return loadedBattleMap == null ? "default" : loadedBattleMap.name;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.553f, 0.702f, 0.427f, 1f);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override public void hide() {}
    @Override public void pause() {}

    @Override public void resume() {}
}
