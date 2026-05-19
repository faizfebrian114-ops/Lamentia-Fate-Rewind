package lamentia.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

import lamentia.LamentiaGame;

public class SettingScreen implements Screen {

	private final LamentiaGame game;
	private final ShapeRenderer shapeRenderer;
	private final OrthographicCamera camera;
	private final Viewport viewport;
	private final Vector3 touchPoint;
	private final Rectangle backButton;
	private final GlyphLayout glyphLayout;

	private boolean backHovered;

	public SettingScreen(LamentiaGame game) {
		this.game = game;
		this.shapeRenderer = new ShapeRenderer();
		this.camera = new OrthographicCamera();
		this.viewport = new FitViewport(1280, 720, camera);
		this.touchPoint = new Vector3();
		this.backButton = new Rectangle();
		this.glyphLayout = new GlyphLayout();

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

				if (contains(backButton, screenX, screenY)) {
					game.setScreen(new HomeScreen(game));
				}

				return true;
			}

			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Input.Keys.ESCAPE) {
					game.setScreen(new HomeScreen(game));
					return true;
				}

				return false;
			}
		});
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0.09f, 0.09f, 0.11f, 1f);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);

		float virtualWidth = viewport.getWorldWidth();
		float virtualHeight = viewport.getWorldHeight();

		float buttonWidth = 240f;
		float buttonHeight = 68f;
		float buttonX = (virtualWidth - buttonWidth) / 2f;
		float buttonY = 120f;

		backButton.set(buttonX, buttonY, buttonWidth, buttonHeight);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(new Color(0.10f, 0.11f, 0.16f, 1f));
		shapeRenderer.rect(0f, 0f, virtualWidth, virtualHeight);

		shapeRenderer.setColor(backHovered ? new Color(0.28f, 0.44f, 0.74f, 1f) : new Color(0.22f, 0.24f, 0.30f, 1f));
		shapeRenderer.rect(backButton.x, backButton.y, backButton.width, backButton.height);
		shapeRenderer.end();

		game.batch.begin();
		game.font.setColor(Color.WHITE);

		drawCenteredText("SETTING", virtualWidth / 2f, virtualHeight - 90f);
		drawCenteredText("Placeholder untuk pengaturan game.", virtualWidth / 2f, virtualHeight / 2f + 10f);
		drawCenteredText("BACK", backButton.x + (backButton.width / 2f), backButton.y + 44f);

		game.batch.end();
	}

	private void drawCenteredText(String text, float centerX, float centerY) {
		glyphLayout.setText(game.font, text);
		game.font.draw(game.batch, glyphLayout, centerX - (glyphLayout.width / 2f), centerY + (glyphLayout.height / 2f));
	}

	private void updateHoverState(int screenX, int screenY) {
		viewport.unproject(touchPoint.set(screenX, screenY, 0f));
		backHovered = backButton.contains(touchPoint.x, touchPoint.y);
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
		shapeRenderer.dispose();
	}
}
