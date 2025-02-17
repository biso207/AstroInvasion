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
import sorgente.InputHandler;
import sorgente.Main;
import sorgente.ResourceLoader;
import sorgente.UI.Lobby.LobbyManager;
import sorgente.UI.LogInSignUp.LoginSignupManager;

import java.text.NumberFormat;
import java.util.Locale;

public class GameOver implements Screen, InputHandler, InputProcessor, ResourceLoader {
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
    GameOver(Main game, Spacecraft selectedSp, int mod, int points, int credits, int aliensHit) {
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

    // ************************************** //
    // GESTIONE GRAFICA + CARICAMENTO RISORSE //
    // ************************************** //

    // caricamento schermate di base
    @Override
    public void loadImages() {
        gameOver0 = new Texture(Gdx.files.internal("secondary_screens/lobby_game_over_cg_eng.png"));
    }

    // caricamento e creazione font per le scritte
    @Override
    public void loadFont() {
        // dichiarazione font
        try {
            font = new BitmapFont(Gdx.files.internal("font/inter/regular_white_40.fnt")); // inter white 40
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

    // ************** //
    // GESTIONE INPUT //
    // ************** //

    // metodo per controllare l'input
    public void handleInput() {

        // chiusura partita e ritorno alla lobby cliccando ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new LobbyManager(game));
        }

        // avvio nuova partita cliccando ENTER
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (mod) {
                case 0:
                    game.setScreen(new ClassicGame(game, selectedSp));
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

            /// TODO: correggere i range che sono sbagliati.

            // click NO => ritorno alla lobby
            if ((screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
                game.setScreen(new LobbyManager(game));
            }

            // click YES => avvio nuova partita
            if ((screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
                switch (mod) {
                    case 0:
                        game.setScreen(new ClassicGame(game, selectedSp));
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
        // recupero progressi
        int a = (int) DataUserManager.getProgress("num_aliens_hit");
        int p = (int) DataUserManager.getProgress("points");
        int c = (int) DataUserManager.getProgress("credits");

        // incremento progressi
        a += aliensHit;
        p += points;
        c += credits;

        // salvataggio progressi
        DataUserManager.setProgress("num_aliens_hit", a);
        DataUserManager.setProgress("points", p);
        DataUserManager.setProgress("credits", c);
    }

    /// TODO: implementare i metodi dell'interfaccia InputHandler in base al metodo handleInput sopra.

    // ************************************ //
    // METODI DELL'INTERFACCIA InputHandler //
    // ************************************ //

    // metodo per controllare gli input da tastiera
    @Override
    public boolean keyTyped(char character) {
        // scelta del campo da modificare
        StringBuilder currentInput = enteringNickname ? nicknameInput : passwordInput;

        // ENTER terminare la digitazione
        if ((character == '\n' || character == '\r') && currentInput.length() >= 1) {
            if (enteringNickname) enteringNickname = false;
            else enteringPassword = false;
        }
        // BACKSPACE per cancellare un carattere
        else if (character == '\b' && currentInput.length() > 0) currentInput.deleteCharAt(currentInput.length() - 1);
            // controllo digitazione caratteri validi
        else if (character >= 32 && character < 127 && currentInput.length() <= 20) currentInput.append(character);

        // aggiornamento nickname e password
        nickname = nicknameInput.toString();
        password = passwordInput.toString();

        return true;
    }
    // metodo per controllare i click del mouse
    @Override
    public boolean touchDown(int screenX, int screenY) {
        // cambio pagina - accesso => registrazione
        if ((LoginSignupManager.state == 0 || LoginSignupManager.state == 1) && (screenX >= 288 && screenX <= 479) && (screenY >= 525 && screenY <= 565)) {
            LoginSignupManager.state = 2;
        }
        // cambio pagina - registrazione => accesso
        if ((LoginSignupManager.state == 2 || LoginSignupManager.state == 3) && (screenX >= 520 && screenX <= 710) && (screenY >= 525 && screenY <= 565)) {
            LoginSignupManager.state = 0; // cambio stato per pagina di accesso
        }
        // click per accedere o registrarsi
        if ((nicknameInput.length() >= 1 && passwordInput.length() >= 1) && (screenX >= 520 && screenX <= 710) && (screenY >= 525 && screenY <= 565)) {
            processLoginOrSignup();
        }
        return true;
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
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
