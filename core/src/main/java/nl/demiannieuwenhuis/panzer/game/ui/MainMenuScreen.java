package nl.demiannieuwenhuis.panzer.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import nl.demiannieuwenhuis.panzer.game.Panzer;

public class MainMenuScreen implements Screen {

    private final Panzer game;
    private final Stage stage;
    private final Skin skin;

    public MainMenuScreen(Panzer game) {
        this.game = game;

        this.stage = new Stage(new ScreenViewport(), game.batch);
        Gdx.input.setInputProcessor(stage);

        stage.addListener(new InputListener() {

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    game.setScreen(new GameScreen(game));
                    dispose();
                    return true;
                }

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

        TextButton battleBtn = new TextButton("Battle!", skin);
        TextButton editBtn = new TextButton("Edit Stage", skin);

        battleBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });

        editBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new WorldEditorScreen(game));
                dispose();
            }
        });

        root.add(title).padBottom(20);
        root.row();
        root.add(sub).padBottom(40);
        root.row();
        root.add(battleBtn).width(200).height(60).padBottom(10);
        root.row();
        root.add(editBtn).width(200).height(60);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.553f, 0.702f, 0.427f, 1f);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
