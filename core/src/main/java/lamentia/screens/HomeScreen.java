package lamentia.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Texture;

import lamentia.LamentiaGame;

public class HomeScreen implements Screen {

	private final LamentiaGame game;
	private final ShapeRenderer shapeRenderer;
	private final OrthographicCamera camera;
	private final Viewport viewport;
	private final Vector3 touchPoint;
	private final Rectangle playButton;
	private final Rectangle continueButton;
	private final Rectangle settingButton;
	private final Rectangle inventoryButton;
	private final GlyphLayout glyphLayout;
	private final Texture background;

	private int hoveredButton = -1;

	//membuat objek
	public HomeScreen(LamentiaGame game) {
		this.game = game;
		this.shapeRenderer = new ShapeRenderer();
		this.camera = new OrthographicCamera();
		this.viewport = new FitViewport(1280, 720, camera);
		this.touchPoint = new Vector3();
		this.playButton = new Rectangle();
		this.continueButton = new Rectangle();
		this.settingButton = new Rectangle();
		this.inventoryButton = new Rectangle();
		this.glyphLayout = new GlyphLayout();
		this.background = new Texture("sumber_daya_background/bg.jpeg");

		viewport.apply();
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f);
		camera.update();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				updateHoverState(screenX, screenY);
				return true;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				updateHoverState(screenX, screenY);

				if (contains(playButton, screenX, screenY)) {
					game.setScreen(new StoryScreen(game));
					return true;
				}

				if (contains(settingButton, screenX, screenY)) {
					game.setScreen(new SettingScreen(game));
					return true;
				}

				return true;
			}
		});
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0.07f, 0.08f, 0.12f, 1f);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);

		float virtualWidth = viewport.getWorldWidth();
		float virtualHeight = viewport.getWorldHeight();

		// TODO: Gambar background di sini, contoh:
		game.batch.begin();
		game.batch.draw(background, 0f, 0f, virtualWidth, virtualHeight);
		game.batch.end();

		float buttonWidth = 320f;
		float buttonHeight = 65f;
		float buttonGap = 18f;
		float buttonStartX = (virtualWidth - buttonWidth) / 2f;
		float buttonStartY = (virtualHeight / 2f) - buttonHeight;

		playButton.set(buttonStartX, buttonStartY, buttonWidth, buttonHeight);
		continueButton.set(buttonStartX, buttonStartY - (buttonHeight + buttonGap), buttonWidth, buttonHeight);
		settingButton.set(buttonStartX, buttonStartY - ((buttonHeight + buttonGap) * 2f), buttonWidth, buttonHeight);
		inventoryButton.set(buttonStartX, buttonStartY - ((buttonHeight + buttonGap) * 3f), buttonWidth, buttonHeight);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		// shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.3f));
		// shapeRenderer.rect(0f, 0f, virtualWidth, virtualHeight);

		drawButton(playButton, hoveredButton == 0, new Color(0.22f, 0.36f, 0.62f, 1f));
		drawButton(continueButton, hoveredButton == 1, new Color(0.24f, 0.24f, 0.26f, 1f));
		drawButton(settingButton, hoveredButton == 2, new Color(0.22f, 0.36f, 0.62f, 1f));
		drawButton(inventoryButton, hoveredButton == 3, new Color(0.22f, 0.36f, 0.62f, 1f));
		shapeRenderer.end();

		game.batch.begin();
		game.font.setColor(Color.WHITE);

		drawCenteredText("LAMENTIA", virtualWidth / 2f, virtualHeight - 90f);
		drawCenteredText("FATE REWIND", virtualWidth / 2f, virtualHeight - 130f);

		drawCenteredText("MULAI", playButton.x + (playButton.width / 2f), playButton.y + 40f);
		drawCenteredText("LANJUT", continueButton.x + (continueButton.width / 2f), continueButton.y + 40f);
		drawCenteredText("PENGATURAN", settingButton.x + (settingButton.width / 2f), settingButton.y + 40f);
		drawCenteredText("PENYIMPANAN", inventoryButton.x + (inventoryButton.width / 2f), inventoryButton.y + 40f);

		game.batch.end();
	}

	private void drawButton(Rectangle button, boolean hovered, Color baseColor) {
		if (hovered) {
			shapeRenderer.setColor(baseColor.r + 0.08f, baseColor.g + 0.08f, baseColor.b + 0.08f, 1f);
		} else {
			shapeRenderer.setColor(baseColor);
		}

		shapeRenderer.rect(button.x, button.y, button.width, button.height);
	}

	private void drawCenteredText(String text, float centerX, float centerY) {
		glyphLayout.setText(game.font, text);
		game.font.draw(game.batch, glyphLayout, centerX - (glyphLayout.width / 2f), centerY + (glyphLayout.height / 2f));
	}

	private void updateHoverState(int screenX, int screenY) {
		viewport.unproject(touchPoint.set(screenX, screenY, 0f));

		if (playButton.contains(touchPoint.x, touchPoint.y)) {
			hoveredButton = 0;
		} else if (continueButton.contains(touchPoint.x, touchPoint.y)) {
			hoveredButton = 1;
		} else if (settingButton.contains(touchPoint.x, touchPoint.y)) {
			hoveredButton = 2;
		} else if (inventoryButton.contains(touchPoint.x, touchPoint.y)) {
			hoveredButton = 3;
		}
		else {
			hoveredButton = -1;
		}
	}

	private boolean contains(Rectangle rectangle, int screenX, int screenY) {
		viewport.unproject(touchPoint.set(screenX, screenY, 0f));
		return rectangle.contains(touchPoint.x, touchPoint.y);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {
		background.dispose();
		shapeRenderer.dispose();
	}
}
