/*
Astro Invasion - class GameOver -
Crea la schermata GameOver per tutte le modalità di gioco
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.GameMods;

// import librerie e codici
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.DataUserManager;
import sorgente.Main;
import sorgente.ResourceLoader;
import sorgente.UI.Lobby.LobbyManager;

import java.text.NumberFormat;
import java.util.Locale;

public class GameOver implements Screen, InputProcessor, ResourceLoader {
    // gioco principale
    private final Main game;
    // schermo
    private final SpriteBatch screen;

    // variabili per la stampa dei progressi partita
    private final int mod, points, credits, aliensHit;

    // font
    private BitmapFont font;
    // immagini
    private Texture gameOver0;
    // formatter per la virgola delle migliaia in automatico converte l'intero in stringa
    NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

    private final Spacecraft selectedSp;

    // costruttore
    public GameOver(Main game, Spacecraft selectedSp, int mod, int points, int credits, int aliensHit) {
        // set gioco
        this.game = game;

        // recupero progressi
        this.selectedSp = selectedSp;
        this.mod = mod;
        this.points = points;
        this.credits = credits;
        this.aliensHit = aliensHit;

        // init screen
        this.screen = game.screen;

        // caricamento font
        loadFont();

        // caricamento immagini di base
        loadImages();
    }

    // salvataggio progressi utente
    public void writeFileCG() {
        // salvataggio progressi
        DataUserManager.setProgress("num_aliens_hit", (int) DataUserManager.getProgress("num_aliens_hit")+aliensHit);
        DataUserManager.setProgress("points", (int) DataUserManager.getProgress("points")+points);
        DataUserManager.setProgress("credits", (int) DataUserManager.getProgress("credits")+credits);
    }

    // ************************************** //
    // GESTIONE GRAFICA + CARICAMENTO RISORSE //
    // ************************************** //

    // caricamento schermate di base
    @Override
    public void loadImages() {
        gameOver0 = new Texture(Gdx.files.internal("secondary_screens/game_over_cg_eng.png"));
    }

    // caricamento e creazione font per le scritte
    @Override
    public void loadFont() {
        // dichiarazione font
        try {
            font = new BitmapFont(Gdx.files.internal("font/inter/bold_white_25.fnt")); // inter bold white 25
        } catch (Exception e) {
            font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("FFFFFF")); // colore white
        }
    }

    // costruzione grafica
    public void graphic() {
        screen.begin();
        // switch delle modalità di gioco
        switch (mod) {
            case 0:
                // aggiornamento progressi di gioco
                writeFileCG();

                // schermata base
                screen.draw(gameOver0, 0, 0);

                // scritte progressi partita
                font.draw(screen, formatter.format(points), 200, 456);
                font.draw(screen, formatter.format(credits), 210, 397);
                font.draw(screen, formatter.format(aliensHit), 240, 339);
                break;
            case 1:
                System.out.println("space battle");
                break;
        }
        screen.end();
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    // metodo per rilevare il click da tastiera
    @Override public boolean keyDown(int character) {
        // click ESC => ritorno alla lobby
        if (character == Input.Keys.ESCAPE) {
            game.setScreen(new LobbyManager(game));
        }

        // click ENTER => avvio nuova partita
        if (character == (Input.Keys.ENTER)) {
            switch (mod) {
                case 0:
                    game.setScreen(new ClassicGame(game, selectedSp));
                    break;
                case 1:
                    game.setScreen(new SpaceBattle(game, selectedSp));
                    break;
            }
        }

        return true;
    }
    // metodo per controllare i click del mouse
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // click NO => ritorno alla Lobby
        if ((screenX >= 513 && screenX <= 713) && (screenY >= 573 && screenY <= 650)) {
            game.setScreen(new LobbyManager(game));
        }

        // click YES => avvio nuova partita
        if ((screenX >= 272 && screenX <= 472) && (screenY >= 573 && screenY <= 650)) {
            switch (mod) {
                case 0:
                    game.setScreen(new ClassicGame(game, selectedSp));
                    break;
                case 1:
                    game.setScreen(new SpaceBattle(game, selectedSp));
                    break;
            }
        }
        return true;
    }

    // altri metodi
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

    // ****************************** //
    // METODI DELL'INTERFACCIA Screen //
    // ****************************** //
    // aggiornamento risorse grafiche
    @Override public void render(float delta) {
        // attivazione controllo input
        Gdx.input.setInputProcessor(this);

        //handleInput();
        graphic();
    }
    // spegnimento controllo input
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    // rilascio risorse
    @Override public void dispose() {
        screen.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}

}
