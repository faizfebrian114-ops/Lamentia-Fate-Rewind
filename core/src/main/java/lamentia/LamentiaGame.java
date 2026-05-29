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
    
    OrthographicCamera camera;
    Viewport viewport;
    
    Texture backgroundTexture1;
    Texture currentBackground; 

    // --- STATE SYSTEM GAME ---
    enum GameState {
        ACT_1,
        ACT_2_BATTLE_INTRO, // Jembatan menuju gameplay kartu
        ACT_3_CASTLE,
        CHOICE_ENDING,      // Evaluasi Poin Takdir / Kewarasan Seta
        GOOD_ENDING,
        BAD_BAD_ENDING
    }
    GameState currentState = GameState.ACT_1;

    // --- MEKANIK UNTUK ENDING (Sesuai PDF) ---
    int fateRewindCount = 0; // Jika >= 4 akan memicu Bad Ending di akhir game

    // ================= DAFATAR DIALOG PER ACT =================
    
    // ACT 1: Kehilangan
    String[] act1Dialogues = {
        "Narrator: Tidak ada yang tersisa. Hanya hujan, abu, dan puing-puing desa yang hancur.",
        "Seta: \"Ibu... Ayah... Kalian ada di mana sekarang?\"",
        "Narrator: Tiba-tiba udara menjadi berat. Sesosok entitas ilahi muncul di hadapan Seta.",
        "Dewi: \"Kamu masih hidup, Seta. Yang menghancurkan desamu bukan bencana biasa, melainkan sang Raja.\"",
        "Seta: \"Kembalikan mereka! Kembalikan keluarga dan warga desaku!\"",
        "Dewi: \"Ambil artefak kartu takdir ini. Ia bisa memutarbalikkan waktu dan memberi kesempatan kedua. Tapi ingat, ada harga yang harus dibayar.\"",
        "Narrator: Seta menerima artefak tersebut. Tekadnya sudah bulat demi membalaskan dendam dan menyelamatkan jiwa yang hilang."
    };

    // ACT 2: Perjalanan dan Pertarungan Kartu
    String[] act2Dialogues = {
        "Narrator: ACT 2: PERJALANAN DAN PERTARUNGAN.",
        "Narrator: Seta berjalan melewati rute berkabut menuju istana, menghadapi 7 kesatria bayangan Raja.",
        "Prajurit Bayangan: \"Berhenti! Jiwamu akan menjadi milik persembahan Raja berikutnya!\"",
        "Seta: \"Aku tidak akan kalah di sini. Kekuatan kartu ini ada bersamaku!\"",
        "Narrator: [SISTEM TRIGER BATTLE]: Di bagian ini, game akan beralih ke arena pertempuran kartu.",
        "Narrator: Seta berhasil memenangkan pertarungan namun kelelahan. Tergoda menggunakan kekuatan pembalik waktu (Fate Rewind).",
        "Dewi: \"Gunakan artefaknya, Seta... Balikkan waktu agar lukamu sembuh. Jangan takut.\""
    };

    // ACT 3: Menuju Istana dan Boss Fight
    String[] act3Dialogues = {
        "Narrator: ACT 3: ISTANA RAJA ABADI.",
        "Narrator: Seta mendobrak gerbang ruang tahta. Di sana, Raja Abadi telah menunggu dengan ribuan kartu jiwa yang melayang.",
        "Raja: \"Anak kecil, kau hanyalah pion dari Dewi yang meratap itu. Kau tidak tahu apa-apa tentang beban keabadian!\"",
        "Seta: \"Cukup bicaranya! Kembalikan jiwa orang tuaku!\"",
        "Narrator: [BOSS FIGHT]: Pertempuran sengit penentu takdir dimulai!",
        "Narrator: Raja Abadi akhirnya berlutut, kalah oleh strategi kartu Seta. Namun, energi Seta terkuras habis.",
        "Narrator: Sekarang, keputusan terakhir ada di tangan Seta."
    };

    // JALUR GOOD ENDING
    String[] goodEndingDialogues = {
        "Narrator: [GOOD ENDING: Harapan Baru]",
        "Seta: \"Aku tidak akan menggunakan kekuatan pembalik waktu lagi. Aku menerima takdir ini dengan jiwaku sendiri!\"",
        "Narrator: Karena Seta menjaga kewarasannya (Fate Rewind < 4), retakan di jiwanya menyembuh.",
        "Raja: \"Kau... benar-benar kuat... bukan karena artefak itu...\"",
        "Narrator: Kartu jiwa terlepas, membebaskan ayah, ibu, dan seluruh penduduk desa. Cahaya kembali menyinari dunia Lamentia.",
        "Dewi: \"Kerja bagus, Seta. Kau berhasil memutus rantai kutukan ini.\"",
        "Narrator: TAMAT - GOOD ENDING."
    };

    // JALUR BAD ENDING
    String[] badEndingDialogues = {
        "Narrator: [BAD ENDING: Penguasa Kegelapan Baru]",
        "Narrator: Karena Seta terlalu sering memutar waktu (Fate Rewind >= 4), kewarasannya habis terkikis.",
        "Seta: \"Kekuatan... Aku butuh kekuatan lebih untuk menyerap semua energi ini!\"",
        "Raja: \"Kau... juga akan menjadi korban berikutnya...\"",
        "Narrator: Tangan Seta bergerak di luar kendalinya. Artefak menyerap sisa energi Raja dan menjadi hitam pekat.",
        "Dewi: \"Tidak... aku terlambat... Aku selalu terlambat...\"",
        "Narrator: Mata Seta berubah menjadi kosong dan gelap. Dia menggantikan posisi Raja, menjadi penguasa Lamentia yang baru di tengah hujan abadi.",
        "Narrator: TAMAT - BAD ENDING."
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

        backgroundTexture1 = new Texture("sumber_daya_background/Screenshot 2026-05-13 145716.png");
        currentBackground = backgroundTexture1;

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        // Input Handler Utama
        Gdx.input.setInputProcessor(new GameInputHandler(new GameInputHandler.InputCallback() {
            @Override
            public void onAdvanceText() {
                String[] currentDialogArray = getCurrentDialogArray();
                String fullText = currentDialogArray[dialogueIndex];

                if (charIndex < fullText.length()) {
                    currentDisplay = fullText;
                    charIndex = fullText.length();
                } else {
                    if (dialogueIndex < currentDialogArray.length - 1) {
                        dialogueIndex++;
                        resetTyping();
                    } else {
                        // Jika dialog pada Act tersebut habis, pindah ke Act berikutnya
                        advanceState();
                    }
                }
            }

            @Override
            public void onOpenMenu() {
                System.out.println("Buka Menu Pause");
            }
        }));
    }

    // Mengambil array dialog yang aktif berdasarkan Game State
    private String[] getCurrentDialogArray() {
        switch (currentState) {
            case ACT_1: return act1Dialogues;
            case ACT_2_BATTLE_INTRO: return act2Dialogues;
            case ACT_3_CASTLE: return act3Dialogues;
            case GOOD_ENDING: return goodEndingDialogues;
            case BAD_BAD_ENDING: return badEndingDialogues;
            default: return act1Dialogues;
        }
    }

    // Logika Perpindahan Cerita Antar-Act
    private void advanceState() {
        dialogueIndex = 0;
        resetTyping();
        
        switch (currentState) {
            case ACT_1:
                currentState = GameState.ACT_2_BATTLE_INTRO;
                break;
            case ACT_2_BATTLE_INTRO:
                // Simulasi: Berpura-pura pemain menggunakan Fate Rewind sebanyak 4 kali untuk testing Bad End
                // Ubah angka ini menjadi kurang dari 4 untuk melihat Good Ending
                fateRewindCount = 4; 
                currentState = GameState.ACT_3_CASTLE;
                break;
            case ACT_3_CASTLE:
                // Evaluasi Poin Takdir / Kewarasan Seta sesuai spesifikasi mekanik naskah
                if (fateRewindCount >= 4) {
                    currentState = GameState.BAD_BAD_ENDING;
                } else {
                    currentState = GameState.GOOD_ENDING;
                }
                break;
            default:
                System.out.println("Game Over. Kembali ke Main Menu.");
                currentState = GameState.ACT_1; // Loop kembali ke awal
                break;
        }
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

        // Gambar Background Aktif
        batch.begin();
        batch.draw(currentBackground, 0, 0, virtualWidth, virtualHeight);
        batch.end();

        // 1. Kotak Dialog UI
        float boxHeight = virtualHeight * 0.25f; 
        float boxMargin = 30; 
        float boxX = boxMargin;
        float boxY = boxMargin; 
        float boxWidth = virtualWidth - (boxMargin * 2); 

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.85f)); 
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();

        // 2. Efek Typewriter
        String[] currentDialogArray = getCurrentDialogArray();
        String fullText = currentDialogArray[dialogueIndex];
        
        if (charIndex < fullText.length()) {
            timer += Gdx.graphics.getDeltaTime();
            if (timer > 0.03f) {
                currentDisplay += fullText.charAt(charIndex);
                charIndex++;
                timer = 0;
            }
        }

        // 3. Render Teks Dialog
        batch.begin();
        font.setColor(Color.WHITE);
        float textX = boxX + 30; 
        float textY = boxY + boxHeight - 40; 
        float textWidthLimit = boxWidth - 60; 

        font.draw(batch, currentDisplay, textX, textY, textWidthLimit, Align.left, true);
        batch.end();

        // 4. Indikator Panah Berkedip
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
        if (backgroundTexture1 != null) backgroundTexture1.dispose();
    }
}