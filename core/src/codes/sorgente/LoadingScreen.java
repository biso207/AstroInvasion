/*
Astro Invasion - class LoadingScreen -
Crea la grafica della schermata di caricamento
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente;

// import codici e librerie
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import sorgente.Lobby.InputManager;
import sorgente.LogInSignUp.LoginSignupManager;

import java.util.Random;

public class LoadingScreen implements Screen {
    private final SpriteBatch screen;
    private float loadingProgress = 0; // Progresso di caricamento attuale
    public boolean loadingFinished = false;
    private final ShapeRenderer shapeRenderer;
    private final Main game;// variabile di riferimento tipo gioco

    // variabili per il background di sfondo
    private int bg; // numero sfondo di caricamento
    private Texture background; // immagine di sfondo

    // array dei colori della barra di caricamento
    private final String[] colorsLoader =  {"#FF3030", "#640414", "#033427", "#0B2353", "#0E2036"};


    public LoadingScreen(Main game) {
        this.game = game; // riferimento al game principale
        this.screen = game.screen; // riferimento allo screen creato nel main

        shapeRenderer = new ShapeRenderer();

        // selezione random del background di sfondo
        selectScreen();

        // musica di apertura
        Music openSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/soundtrack home 2023.mp3")); // file audio
        openSound.setLooping(false); // true=loop music; false=no loop
        openSound.play(); // avvio musica
    }

    @Override
    public void show() {
    }

    // metodo per renderizzare le grafiche
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // pulizia schermo

        // incrementa il progresso del caricamento in base al tempo trascorso
        loadingProgress += delta;
        float loadingTime = 1f; // 5 secondi // todo: impostare a 5 secondi, ora è a 1 per le prove
        if (loadingProgress >= loadingTime) {
            loadingProgress = loadingTime;
            loadingFinished = true; // caricamento completato
        }

        // stampa immagine sfondo
        screen.begin();
        screen.draw(background, 0, 0);
        screen.end();

        // larghezza barra
        float barWidth = (loadingProgress / loadingTime) * 390;

        // disegno barra
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.valueOf(colorsLoader[bg])); // colore
        drawRoundedRectangle(shapeRenderer, barWidth); // riempimento
        shapeRenderer.end(); // chiusura render disegno

        // apertura schermata successiva
        if (loadingFinished) {
            game.setScreen(new LoginSignupManager(game));
        }
    }

    // metodo per arrotondare gli angoli della barra di caricamento
    private void drawRoundedRectangle(ShapeRenderer shapeRenderer, float width) {
        float x = 305;
        float y = 48;
        float height = 20;
        float radius = 10;

        // disegno rettangolo con angoli arrotondati
        shapeRenderer.rect(x + radius, y, width - 2 * radius, height); // Corpo centrale
        shapeRenderer.arc(x + radius, y + radius, radius, 180, 90); // Angolo in alto a sinistra
        shapeRenderer.arc(x + width - radius, y + radius, radius, 270, 90); // Angolo in alto a destra
        shapeRenderer.arc(x + width - radius, y + height - radius, radius, 0, 90); // Angolo in basso a destra
        shapeRenderer.arc(x + radius, y + height - radius, radius, 90, 90); // Angolo in basso a sinistra
    }

    // metodo per selezionare la schermata di caricamento
    public void selectScreen() {
        // numero random della schermata
        Random r = new Random();
        bg = r.nextInt(5); // 0->4

        // array dei percorsi delle immagini di caricamento
        String[] bgPaths = {"loading_screens/loading_screen_0.png", "loading_screens/loading_screen_1.png", "loading_screens/loading_screen_2.png",
            "loading_screens/loading_screen_3.png", "loading_screens/loading_screen_4.png"};

        // immagine di sfondo
        background = new Texture(bgPaths[bg]);
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
