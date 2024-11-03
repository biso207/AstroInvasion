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
    private final SpriteBatch screen;
    private final Texture background; // Immagine di sfondo
    private float loadingProgress = 0; // Progresso di caricamento attuale
    public boolean loadingFinished = false;
    private final ShapeRenderer shapeRenderer;
    private final Main game; // variabile di riferimento tipo gioco

    public LoadingScreen(Main game) {
        this.game = game; // riferimento al game principale
        this.screen = game.screen; // riferimento allo screen creato nel main

        shapeRenderer = new ShapeRenderer();
        background = new Texture("loading_screen.png"); // immagine di sfondo

        // musica di apertura
        Music openSound = Gdx.audio.newMusic(Gdx.files.internal("soundtrack home 2023.mp3")); // file audio
        openSound.setLooping(false); // true=loop music; false=no loop
        openSound.play(); // avvio musica
    }

    @Override
    // inizializza la schermata di caricamento
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // pulizia schermo

        // incrementa il progresso del caricamento
        loadingProgress += delta; // incrementa il progresso basato sul tempo trascorso
        // tempo di caricamento totale in secondi (4.5)
        float loadingTime = 4.5f;
        if (loadingProgress >= loadingTime) {
            loadingProgress = loadingTime;
            loadingFinished = true; // caricamento completato
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

        // apertura schermata Accesso/Registrazione al completamente del caricamento
        if (loadingFinished) {
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
        background.dispose();
    }
}
