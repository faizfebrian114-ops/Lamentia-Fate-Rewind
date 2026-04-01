package id.ac.unjani.lamentia;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class LamentiaGame extends ApplicationAdapter {
    SpriteBatch batch;
    BitmapFont font;
    
    String fullText = "Faiz: 'Selamat datang di dunia Lamentia, tempat di mana takdir bisa ditulis ulang...'";
    String currentDisplay = "";
    float timer = 0;
    int charIndex = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Logika Mengetik
        if (charIndex < fullText.length()) {
            timer += Gdx.graphics.getDeltaTime();
            if (timer > 0.05f) { // Kecepatan ketik (makin kecil makin cepet)
                currentDisplay += fullText.charAt(charIndex);
                charIndex++;
                timer = 0;
            }
        }

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, currentDisplay, 50, 200);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}