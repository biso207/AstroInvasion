package sorgente;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import sorgente.Lobby.LobbyManager;
import sorgente.LogInSignUp.*;

import java.util.Random;

public class LoadingScreen implements Screen, ProgressListener {
    private final SpriteBatch screen;
    private float loadingProgress = 0;
    private int targetProgress = 0;
    public boolean loadingFinished = false;
    private final ShapeRenderer shapeRenderer;
    private final Main game;

    private int bg;
    private Texture background;
    private final String[] colorsLoader = {"#FF3030", "#640414", "#033427", "#0B2353", "#0E2036", "#3A0150"};

    private Music openSound;
    private final boolean playMusic;

    public LoadingScreen(Main game, boolean playMusic) {
        this.game = game;
        this.screen = game.screen;
        this.shapeRenderer = new ShapeRenderer();
        this.playMusic = playMusic;
        selectScreen();

        if (playMusic) {
            // musica di apertura
            openSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/soundtrack home 2023.mp3"));
            openSound.setLooping(false);
            openSound.play();

            setProgress(100); // tempo di 1 secondo per aprire la pagina iniziale
        } else {
            // pagina di caricamento dati utente o creazione id
            new Thread(() -> {
                for (int i = 0; i <= 100; i++) {
                    try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                    final int progress = i;
                    Gdx.app.postRunnable(() -> setProgress(progress));
                }
            }).start();
        }
    }

    public void setProgress(int progress) {
        targetProgress = progress;
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (loadingProgress < targetProgress) {
            loadingProgress += delta * 50;
            if (loadingProgress > targetProgress)
                loadingProgress = targetProgress;
        }

        screen.begin();
        screen.draw(background, 0, 0);
        screen.end();

        float barWidth = (loadingProgress / 100f) * 390;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.valueOf(colorsLoader[bg]));
        drawRoundedRectangle(shapeRenderer, barWidth);
        shapeRenderer.end();

        if (loadingProgress >= 100 && !loadingFinished) {
            loadingFinished = true;

            // accesso alla schermata successiva
            if (playMusic) game.setScreen(new LoginSignupManager(game)); // schermata di autenticazione
            else {
                GlobalProgressManager.isInitialLoading = false; // stato di caricamento iniziale dei dati
                LockManager.startHeartbeat(AuthAlgorithms.nickname); // avvio setting temporale dello stato di lock

                // apertura lobby
                game.setScreen(new LobbyManager(game));
            }
        }
    }

    private void drawRoundedRectangle(ShapeRenderer shapeRenderer, float width) {
        float x = 305;
        float y = 48;
        float height = 20;
        float radius = 10;

        shapeRenderer.rect(x + radius, y, width - 2 * radius, height);
        shapeRenderer.arc(x + radius, y + radius, radius, 180, 90);
        shapeRenderer.arc(x + width - radius, y + radius, radius, 270, 90);
        shapeRenderer.arc(x + width - radius, y + height - radius, radius, 0, 90);
        shapeRenderer.arc(x + radius, y + height - radius, radius, 90, 90);
    }

    public void selectScreen() {
        Random r = new Random();
        if (playMusic) bg = r.nextInt(5);
        else bg = 5;

        String[] bgPaths = {
            "loading_screens/loading_screen_0.png",
            "loading_screens/loading_screen_1.png",
            "loading_screens/loading_screen_2.png",
            "loading_screens/loading_screen_3.png",
            "loading_screens/loading_screen_4.png",
            "loading_screens/loading_screen_5.png"
        };
        background = new Texture(bgPaths[bg]);
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        background.dispose();
        if (openSound != null) {
            openSound.dispose();
        }

        // sblocco stato di accesso
        try { FirestoreStorage.setUserLock(AuthAlgorithms.nickname, false); }
        catch (Exception e) { System.out.println(e.getMessage()); }

        LockManager.shutdownAll();
    }

    // metodo che "ascolta" il progresso di caricamento durante upload/download dati
    @Override
    public void onProgress(int progress) {
        setProgress(progress);
    }
}
