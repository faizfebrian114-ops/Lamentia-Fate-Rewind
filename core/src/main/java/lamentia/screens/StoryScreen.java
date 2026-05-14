package lamentia.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import lamentia.GameInputHandler;
import lamentia.LamentiaGame;

public class StoryScreen implements Screen {

    private LamentiaGame game;

    private ShapeRenderer shapeRenderer;

    // Camera & Viewport
    private OrthographicCamera camera;
    private Viewport viewport;

    // Background
    private Texture backgroundTexture1;
    private Texture backgroundTexture2;
    private Texture currentBackground;

    // Dialog
    private String[] dialogues = {
        "Faiz: 'Selamat datang di dunia Lamentia, tempat di mana takdir bisa ditulis ulang...'",
        "Faiz: 'Setiap kartu yang kau mainkan akan menentukan masa depanmu.'",
        "???: 'Hahaha... kau pikir semudah itu mengubah takdir?'",
        "Faiz: 'Siapa itu?! Tunjukkan dirimu!'"
    };

    private int dialogueIndex = 0;

    // Typing Effect
    private String currentDisplay = "";
    private float timer = 0;
    private float blinkTimer = 0;
    private int charIndex = 0;

    public StoryScreen(LamentiaGame game) {

        this.game = game;

        shapeRenderer = new ShapeRenderer();

        // Load Background
        backgroundTexture1 = new Texture(
            "sumber_daya_background/Screenshot 2026-05-13 145716.png"
        );

        backgroundTexture2 = new Texture(
            "sumber_daya_background/Screenshot 2026-05-13 133833.png"
        );

        currentBackground = backgroundTexture1;

        // Camera & Viewport
        camera = new OrthographicCamera();

        viewport = new FitViewport(1280, 720, camera);

        viewport.apply();

        camera.position.set(
            camera.viewportWidth / 2,
            camera.viewportHeight / 2,
            0
        );

        // Input Handler
        Gdx.input.setInputProcessor(
            new GameInputHandler(
                new GameInputHandler.InputCallback() {

                    @Override
                    public void onAdvanceText() {

                        String fullText = dialogues[dialogueIndex];

                        // Skip typing
                        if (charIndex < fullText.length()) {

                            currentDisplay = fullText;
                            charIndex = fullText.length();

                        } else {

                            // Next dialog
                            if (dialogueIndex < dialogues.length - 1) {

                                dialogueIndex++;
                                resetTyping();

                            } else {

                                System.out.println("Cerita selesai");

                            }
                        }
                    }

                    @Override
                    public void onOpenMenu() {

                        System.out.println("Buka menu pause");

                    }
                }
            )
        );
    }

    private void resetTyping() {

        currentDisplay = "";
        charIndex = 0;
        timer = 0;

    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(0, 0, 0, 1);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        float virtualWidth = viewport.getWorldWidth();
        float virtualHeight = viewport.getWorldHeight();

        // Ganti Background
        if (dialogueIndex == 3) {

            currentBackground = backgroundTexture2;

        } else {

            currentBackground = backgroundTexture1;

        }

        // Draw Background
        game.batch.begin();

        game.batch.draw(
            currentBackground,
            0,
            0,
            virtualWidth,
            virtualHeight
        );

        game.batch.end();

        // Dialog Box
        float boxHeight = virtualHeight * 0.25f;
        float boxMargin = 30;

        float boxX = boxMargin;
        float boxY = boxMargin;

        float boxWidth = virtualWidth - (boxMargin * 2);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(new Color(0, 0, 0, 0.8f));

        shapeRenderer.rect(
            boxX,
            boxY,
            boxWidth,
            boxHeight
        );

        shapeRenderer.end();

        // Typing Effect
        String fullText = dialogues[dialogueIndex];

        if (charIndex < fullText.length()) {

            timer += Gdx.graphics.getDeltaTime();

            if (timer > 0.04f) {

                currentDisplay += fullText.charAt(charIndex);

                charIndex++;

                timer = 0;

            }
        }

        // Draw Text
        game.batch.begin();

        game.font.setColor(Color.WHITE);

        float textX = boxX + 30;
        float textY = boxY + boxHeight - 40;

        float textWidthLimit = boxWidth - 60;

        game.font.draw(
            game.batch,
            currentDisplay,
            textX,
            textY,
            textWidthLimit,
            Align.left,
            true
        );

        game.batch.end();

        // Next Arrow
        if (charIndex >= fullText.length()) {

            blinkTimer += Gdx.graphics.getDeltaTime();

            if ((int)(blinkTimer * 2) % 2 == 0) {

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

                shapeRenderer.setColor(Color.YELLOW);

                float arrowX = boxX + boxWidth - 50;
                float arrowY = boxY + 40;

                shapeRenderer.triangle(
                    arrowX,
                    arrowY,
                    arrowX + 20,
                    arrowY,
                    arrowX + 10,
                    arrowY - 15
                );

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

        shapeRenderer.dispose();

        if (backgroundTexture1 != null)
            backgroundTexture1.dispose();

        if (backgroundTexture2 != null)
            backgroundTexture2.dispose();
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
