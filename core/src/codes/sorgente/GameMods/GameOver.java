/*
Astro Invasion - class Lobby -
This class creates the Game Over screens
Developed by BIGA©. All rights reserved.
*/

package sorgente.GameMods;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.text.NumberFormat;
import java.util.Locale;

public class GameOver implements Screen {
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

    // costruttore
    GameOver(Main game, int mod, int points, int credits, int aliensHit) {
        // set gioco
        this.game = game;

        // recupero progressi
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

    // -------------------- //
    // GRAFICA DELLA CLASSE //
    // -------------------- //

    // costruzione grafica
    private void graphic() {
        screen.begin();
        // switch delle modalità di gioco
        switch (mod) {
            case 0:
                writeFileCG();
                // schermata base
                screen.draw(gameOver0, 0, 0);

                // scritte progressi partita
                font.draw(screen, formatter.format(points), 300, 300);
                font.draw(screen, formatter.format(credits), 300, 400);
                font.draw(screen, formatter.format(aliensHit), 300, 500);
                break;
            case 1:
                System.out.println("space battle");
                break;
        }
        screen.end();
    }

    // caricamento schermate di base
    private void loadImages() {
        gameOver0 = new Texture(Gdx.files.internal("secondary_screens/lobby_game_over_cg_eng.png"));
    }

    // caricamento e creazione font per le scritte
    private void loadFont() {
        // dichiarazione font
        try {
            font = new BitmapFont(Gdx.files.internal("font/inter/regular_white_40.fnt")); // inter white 40
        } catch (Exception e) {
            font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("FFFFFF")); // colore white
        }
    }

    // ------------------- //
    // LOGICA DELLA CLASSE //
    // ------------------- //

    // metodo per controllare l'input
    private void handleInput() {

        // chiusura partita e ritorno alla lobby cliccando ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new Lobby(game));
        }

        // avvio nuova partita cliccando ENTER
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (mod) {
                case 0:
                    game.setScreen(new ClassicGame(game));
                    break;
                case 1:
                    System.out.println("space battle");
                    break;
            }
        }

        // chiusura o avvio nuova partita con il click del mouse
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // recupero x e y del click
            int screenX = Gdx.input.getX();
            int screenY = Gdx.input.getY();

            // I RANGE SONO SBAGLIATI! //

            // click NO => ritorno alla lobby
            if ((screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
                game.setScreen(new Lobby(game));
            }

            // click YES => avvio nuova partita
            if ((screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
                switch (mod) {
                    case 0:
                        game.setScreen(new ClassicGame(game));
                        break;
                    case 1:
                        System.out.println("space battle");
                        break;
                }
            }
        }
    }

    // salvataggio progressi utente
    public void writeFileCG() {
        // incremento progressi
        Lobby.numAliensHit += aliensHit;
        Lobby.points += points;
        Lobby.credits += credits;

        // file
        FileHandle f1 = new FileHandle("data/" + LogInSignUp.nickname + "/progresses/credits.txt");
        FileHandle f2 = new FileHandle("data/" + LogInSignUp.nickname + "/progresses/points.txt");
        FileHandle f3 = new FileHandle("data/" + LogInSignUp.nickname + "/progresses/num_aliens_hit.txt");

        // scrittura
        f1.writeString(String.valueOf(Lobby.credits), false);
        f2.writeString(String.valueOf(Lobby.points), false);
        f3.writeString(String.valueOf(Lobby.numAliensHit), false);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        handleInput();
        graphic();
    }

    // metodi classe screen
    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        // spegnimento controllo input
        Gdx.input.setInputProcessor(null);
    }

    @Override
    // rilascio risorse
    public void dispose() {
        screen.dispose();
    }
}
