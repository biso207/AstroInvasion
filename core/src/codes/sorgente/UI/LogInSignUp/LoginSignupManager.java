/*
Astro Invasion - class AuthManagerUI -
Gestisce le grafiche delle schermate di autenticazione e registrazione
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.UI.LogInSignUp;

// import codici e librerie
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.DataUserManager;
import sorgente.Main;
import sorgente.ResourceLoader;
import sorgente.UI.Lobby.LobbyManager;

public class LoginSignupManager extends ScreenAdapter implements ResourceLoader {
    // variabile di riferimento al gioco
    private final Main game;
    // screen di gioco
    private final SpriteBatch screen;

    // istanza classe algoritmi
    private final AuthAlgorithms alg;

    // font
    private BitmapFont font;
    // immagini
    private Texture img1, img2, img3, img4;

    /* pagina di riferimento
        0 = LogIn
        1 = errore LogIn
        2 = SignUp
        3 = errore SignUp
    */
    protected static int state = 0;

    // costruttore
    public LoginSignupManager(Main game) {
        this.game = game;
        this.screen = game.screen;

        // istanza classe degli algoritmi
        alg = new AuthAlgorithms();

        // caricamento font e immagini
        this.loadFont();
        this.loadImages();

        alg.userOperations();
    }

    // ******************* //
    // CARICAMENTO RISORSE //
    // ******************* //

    // metodo per caricare e creare i font
    @Override
    public void loadFont() {
        try {
            font = new BitmapFont(Gdx.files.internal("font/inter/Inter-Regular.fnt")); // font personalizzato (inter)
            font.setColor(Color.valueOf("#151A3B")); // colore blu
        } catch (Exception e) {
            font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("#151A3B")); // colore blu
        }
    }

    // metodo per caricare le immagini delle pagine di Accesso e Registrazione
    @Override
    public void loadImages() {
        img1 = new Texture("login_signup_screens/page_1_log_in_eng.png");
        img2 = new Texture("login_signup_screens/page_1_log_in_eng_error.png");
        img3 = new Texture("login_signup_screens/page_2_sign_up_eng.png");
        img4 = new Texture("login_signup_screens/page_2_sign_up_eng_error.png");

        alg.enteringNickname = true;
    }

    // ********************************* //
    // METODI DELLA CLASSE ScreenAdapter //
    // ********************************* //
    // metodo per aggiornare lo schermo
    @Override public void render(float delta) {
        // attivazione controllo input
        Gdx.input.setInputProcessor(alg);

        screen.begin();

        switch (state) {
            case 0:
                screen.draw(img1, 0, 0);
                break;
            case 1:
                screen.draw(img2, 0, 0);
                break;
            case 2:
                screen.draw(img3, 0, 0);
                break;
            case 3:
                screen.draw(img4, 0, 0);
                break;
            case 4:
                // caricamento risorse utente
                new DataUserManager("data/" + alg.nickname + "/progresses/progresses.json");
                // apertura lobby
                game.setScreen(new LobbyManager(game));
                break;
            default:
                break;
        }

        if (alg.enteringNickname) {
            font.draw(screen, alg.nicknameInput, 265, 358);
        } else if (alg.enteringPassword) {
            font.draw(screen, alg.nicknameInput, 265, 358);
            font.draw(screen, alg.passwordInput, 265, 260);
        } else {
            alg.processLoginOrSignup();
        }
        screen.end();
    }
    // spegnimento controllo input
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    // rilascio delle risorse
    @Override public void dispose() {
        if (font != null) font.dispose();
        img1.dispose();
        img2.dispose();
        img3.dispose();
        img4.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
}
