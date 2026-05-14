package lamentia;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import lamentia.screens.StoryScreen;

public class LamentiaGame extends Game {

    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {

        batch = new SpriteBatch();

        font = new BitmapFont();
        font.getData().setScale(1.5f);

        setScreen(new StoryScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}