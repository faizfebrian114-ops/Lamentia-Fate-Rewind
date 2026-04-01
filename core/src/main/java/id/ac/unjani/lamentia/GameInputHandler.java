package id.ac.unjani.lamentia;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class GameInputHandler extends InputAdapter {
    
    // Interface untuk menghubungkan input ke sistem dialog nanti
    public interface InputCallback {
        void onAdvanceText();
        void onOpenMenu();
    }

    private InputCallback callback;

    public GameInputHandler(InputCallback callback) {
        this.callback = callback;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.SPACE || keycode == Keys.ENTER) {
            callback.onAdvanceText();
            return true;
        }
        if (keycode == Keys.ESCAPE) {
            callback.onOpenMenu();
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Klik kiri mouse juga untuk lanjut teks
        callback.onAdvanceText();
        return true;
    }
}