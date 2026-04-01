package id.ac.unjani.lamentia;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LamentiaGame extends ApplicationAdapter {
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    BitmapFont font;
    
    // --- VARIABEL BARU UNTUK UI RESPONSIVE ---
    OrthographicCamera camera;
    Viewport viewport;
    
    // Daftar Dialog
    String[] dialogues = {
        "Faiz: 'Selamat datang di dunia Lamentia, tempat di mana takdir bisa ditulis ulang...'",
        "Faiz: 'Setiap kartu yang kau mainkan akan menentukan masa depanmu.'",
        "???: 'Hahaha... kau pikir semudah itu mengubah takdir?'",
        "Faiz: 'Siapa itu?! Tunjukkan dirimu!'"
    };
    int dialogueIndex = 0;
    
    String currentDisplay = "";
    float timer = 0;
    float blinkTimer = 0;
    int charIndex = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        // --- INISIALISASI CAMERA & VIEWPORT ---
        camera = new OrthographicCamera();
        // Kita gunakan kanvas virtual 1280x720 (16:9)
        viewport = new FitViewport(1280, 720, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        // Input Handler
        Gdx.input.setInputProcessor(new GameInputHandler(new GameInputHandler.InputCallback() {
            @Override
            public void onAdvanceText() {
                String fullText = dialogues[dialogueIndex];

                if (charIndex < fullText.length()) {
                    currentDisplay = fullText;
                    charIndex = fullText.length();
                } else {
                    if (dialogueIndex < dialogues.length - 1) {
                        dialogueIndex++;
                        resetTyping();
                    } else {
                        System.out.println("Cerita Selesai.");
                    }
                }
            }

            @Override
            public void onOpenMenu() {
                System.out.println("Buka Menu Pause");
            }
        }));
    }

    private void resetTyping() {
        currentDisplay = "";
        charIndex = 0;
        timer = 0;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // --- UPDATE CAMERA & VIEWPORT ---
        camera.update();
        // Beritahu batch dan shapeRenderer untuk menggunakan camera kita
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // --- VARIABEL DINAMIS BERDASARKAN UKURAN VIRTUAL ---
        float virtualWidth = viewport.getWorldWidth();
        float virtualHeight = viewport.getWorldHeight();
        
        // Hitungan kotak berdasarkan ukuran virtual 1280x720
        float boxHeight = virtualHeight * 0.25f; // Kotak tingginya 25% dari layar
        float boxMargin = 30; // Jarak dari pinggir
        float boxX = boxMargin;
        float boxY = boxMargin; // Jarak dari bawah
        float boxWidth = virtualWidth - (boxMargin * 2); // Lebar penuh dikurangi margin kiri-kanan

        // 1. Gambar Kotak Dialog
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.8f));
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();

        // 2. Logika Mengetik
        String fullText = dialogues[dialogueIndex];
        if (charIndex < fullText.length()) {
            timer += Gdx.graphics.getDeltaTime();
            if (timer > 0.04f) {
                currentDisplay += fullText.charAt(charIndex);
                charIndex++;
                timer = 0;
            }
        }

        // 3. Gambar Teks (Posisi disesuaikan dengan kotak virtual)
        batch.begin();
        font.setColor(Color.WHITE);
        
        float textX = boxX + 30; // Padding dalam kotak
        float textY = boxY + boxHeight - 40; // Mulai dari atas kotak
        float textWidthLimit = boxWidth - 60; // Batas lebar teks

        font.draw(batch, currentDisplay, textX, textY, textWidthLimit, Align.left, true);
        batch.end();

        // 4. Gambar Indikator Panah (Disesuaikan posisinya di kotak virtual)
        if (charIndex >= fullText.length()) {
            blinkTimer += Gdx.graphics.getDeltaTime();
            if ((int)(blinkTimer * 2) % 2 == 0) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.YELLOW);
                
                float arrowX = boxX + boxWidth - 50; // Pojok kanan kotak virtual
                float arrowY = boxY + 40;
                shapeRenderer.triangle(
                    arrowX, arrowY, 
                    arrowX + 20, arrowY, 
                    arrowX + 10, arrowY - 15
                );
                shapeRenderer.end();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // --- SANGAT PENTING: Update viewport saat ukuran jendela berubah ---
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}