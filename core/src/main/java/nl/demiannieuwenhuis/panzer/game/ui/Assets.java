package nl.demiannieuwenhuis.panzer.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Assets {
    public final BitmapFont font;
    public final BitmapFont titleFont;

    public Assets() {
        FreeTypeFontGenerator gen =
            new FreeTypeFontGenerator(Gdx.files.internal("fonts/BlackOpsOne-Regular.ttf"));

        FreeTypeFontParameter p = new FreeTypeFontParameter();

        p.size = 72;
        titleFont = gen.generateFont(p);

        p.size = 24;
        font = gen.generateFont(p);

        gen.dispose();
    }

    public void dispose() {
        font.dispose();
        titleFont.dispose();
    }
}
