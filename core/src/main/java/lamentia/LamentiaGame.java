package lamentia;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture; 
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
    
    // --- VARIABEL MULTI-BACKGROUND ---
    Texture backgroundTexture1;
    Texture backgroundTexture2;
    Texture currentBackground; // Menentukan mana yang sedang digambar

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

        // --- LOAD BACKGROUNDS DARI FOLDER ---
        // Menggunakan nama file sesuai screenshot kamu di image_5578fe.png
        backgroundTexture1 = new Texture("sumber_daya_background/Screenshot 2026-05-13 145716.png");
        backgroundTexture2 = new Texture("sumber_daya_background/Screenshot 2026-05-13 133833.png");
        
        // Default awal pakai background 1
        currentBackground = backgroundTexture1;

        // --- INISIALISASI CAMERA & VIEWPORT ---
        camera = new OrthographicCamera();
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
        ScreenUtils.clear(0, 0, 0, 1);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        float virtualWidth = viewport.getWorldWidth();
        float virtualHeight = viewport.getWorldHeight();
        
        // --- LOGIKA PERPINDAHAN BACKGROUND ---
        // Jika dialog sudah sampai indeks ke-3 ("Siapa itu?!...")
        if (dialogueIndex == 3) {
            currentBackground = backgroundTexture2;
        } else {
            currentBackground = backgroundTexture1;
        }

        // --- GAMBAR BACKGROUND AKTIF ---
        batch.begin();
        batch.draw(currentBackground, 0, 0, virtualWidth, virtualHeight);
        batch.end();

        // 1. Gambar Kotak Dialog
        float boxHeight = virtualHeight * 0.25f; 
        float boxMargin = 30; 
        float boxX = boxMargin;
        float boxY = boxMargin; 
        float boxWidth = virtualWidth - (boxMargin * 2); 

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

        // 3. Gambar Teks
        batch.begin();
        font.setColor(Color.WHITE);
        float textX = boxX + 30; 
        float textY = boxY + boxHeight - 40; 
        float textWidthLimit = boxWidth - 60; 

        font.draw(batch, currentDisplay, textX, textY, textWidthLimit, Align.left, true);
        batch.end();

        // 4. Gambar Indikator Panah
        if (charIndex >= fullText.length()) {
            blinkTimer += Gdx.graphics.getDeltaTime();
            if ((int)(blinkTimer * 2) % 2 == 0) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.YELLOW);
                float arrowX = boxX + boxWidth - 50; 
                float arrowY = boxY + 40;
                shapeRenderer.triangle(arrowX, arrowY, arrowX + 20, arrowY, arrowX + 10, arrowY - 15);
                shapeRenderer.end();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        // Hapus semua background dari memori
        if (backgroundTexture1 != null) backgroundTexture1.dispose();
        if (backgroundTexture2 != null) backgroundTexture2.dispose();
    }
}