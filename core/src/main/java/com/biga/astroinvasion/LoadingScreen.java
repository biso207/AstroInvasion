package com.biga.astroinvasion;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class LoadingScreen implements Screen {
    private SpriteBatch screen;
    private final Texture background; // Immagine di sfondo
    private final Texture loadingBar; // Texture per la barra di caricamento
    private float loadingProgress = 0; // Progresso di caricamento attuale
    private boolean loadingFinished = false;
    private final ShapeRenderer shapeRenderer;
    private final Game game; // variabile di riferimento tipo gioco

    public LoadingScreen(Game game) {
        this.game = game; // Assegna il riferimento
        this.screen = Main.screen;
        shapeRenderer = new ShapeRenderer();
        background = new Texture("loading_screen.png"); // Carica l'immagine di sfondo
        loadingBar = new Texture("loading_screen.png"); // Carica un'immagine per la barra di caricamento

        // musica di apertura
        Music openSound = Gdx.audio.newMusic(Gdx.files.internal("soundtrack home 2023.mp3")); // Carica il file audio
        openSound.setLooping(false); // true=loop music; false=no loop
        openSound.play(); // Avvia la musica
    }

    @Override
    // inizializza la schermata di caricamento
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Pulisce lo schermo

        // incrementa il progresso del caricamento
        loadingProgress += delta; // Incrementa il progresso basato sul tempo trascorso
        // tempo di caricamento totale in secondi
        float loadingTime = 4.5f;
        if (loadingProgress >= loadingTime) {
            loadingProgress = loadingTime; // Assicura che non superi il tempo totale
            loadingFinished = true; // Imposta il caricamento come completato
        }

        // disegna lo sfondo
        screen.begin();
        screen.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // stampa immagine sfondo
        screen.end();

        // dimensione e posizione barra
        int loadingBarWidth = 390; // larghezza barra di caricamento
        int barX = 305;
        int barY = 48;
        float barWidth = (loadingProgress / loadingTime) * loadingBarWidth; // Calcola la larghezza della barra

        // disegno barra
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED); // colore rosso
        drawRoundedRectangle(shapeRenderer, barX, barY, barWidth, 20, 10); // barra rossa di riempimento
        shapeRenderer.end(); // chiusura render disegno

        if (loadingFinished) {
            // creazione oggetto per registrazione/accesso e chiamata metodo di controllo
            LogInSignUp logInSignUp = new LogInSignUp(game);
            logInSignUp.userOperations();
            game.setScreen(new LogInSignUp(game));
        }
    }

    // metodo per arrotondare gli angoli della barra di caricamento
    private void drawRoundedRectangle(ShapeRenderer shapeRenderer, float x, float y, float width, int height, int radius) {
        // disegno rettangolo con angoli arrotondati
        shapeRenderer.rect(x + radius, y, width - 2 * radius, height); // Corpo centrale
        shapeRenderer.arc(x + radius, y + radius, radius, 180, 90); // Angolo in alto a sinistra
        shapeRenderer.arc(x + width - radius, y + radius, radius, 270, 90); // Angolo in alto a destra
        shapeRenderer.arc(x + width - radius, y + height - radius, radius, 0, 90); // Angolo in basso a destra
        shapeRenderer.arc(x + radius, y + height - radius, radius, 90, 90); // Angolo in basso a sinistra
    }

    // metodi dalla classe Screen
    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        loadingBar.dispose();
    }
}
