/*
Astro Invasion - class AuthManagerUI -
Gestisce le grafiche delle schermate di autenticazione e registrazione
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.LogInSignUp;

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
import sorgente.Lobby.LobbyManager;

public class LoginSignupManager extends ScreenAdapter implements ResourceLoader {
    // variabile di riferimento al gioco
    private final Main game;
    // screen di gioco
    private final SpriteBatch screen;

    // istanza classe algoritmi
    private final AuthAlgorithms alg;

    // font
    private BitmapFont font, fontRed20;
    // immagini
    private Texture img1, img2, showPS, coverPS;

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
            font = new BitmapFont(Gdx.files.internal("font/inter/Inter-Regular.fnt")); // inter regular blue 20
            fontRed20 = new BitmapFont(Gdx.files.internal("font/inter/regular_red_20.fnt")); // inter regular red 20
        } catch (Exception e) {
            font = new BitmapFont(); // font di default (arial)
        }
        font.setColor(Color.valueOf("#151A3B")); // colore blu
    }

    // metodo per caricare le immagini delle pagine di Accesso e Registrazione
    @Override
    public void loadImages() {
        img1 = new Texture("login_signup_screens/page_1_log_in_eng.png");
        img2 = new Texture("login_signup_screens/page_2_sign_up_eng.png");
        showPS = new Texture("images/showPS.png");
        coverPS = new Texture("images/coverPS.png");

        alg.enteringNickname = true; // digitazione nickname attivata
    }

    // ********************************* //
    // METODI DELLA CLASSE ScreenAdapter //
    // ********************************* //
    // metodo per aggiornare lo schermo
    @Override public void render(float delta) {
        // attivazione controllo input
        Gdx.input.setInputProcessor(alg);

        screen.begin();

        switch (alg.state) {
            case 0:
                screen.draw(img1, 0, 0);
                if (alg.error) fontRed20.draw(screen, "Nickname or Password wrong",362,72);
                break;
            case 1:
                screen.draw(img2, 0, 0);
                if (alg.error) fontRed20.draw(screen, "Nickname already in use",388,72);
                break;
            case 2:
                // caricamento risorse utente
                new DataUserManager("data/" + alg.nickname + "/gameProgresses.json");
                // apertura lobby
                game.setScreen(new LobbyManager(game));
                break;
            default:
                break;
        }

        if (alg.showPS) screen.draw(showPS, 700,233);
        else screen.draw(coverPS, 700,233);

        if (alg.enteringNickname) {
            font.draw(screen, alg.nicknameInput, 265, 358);
        } else if (alg.enteringPassword) {
            font.draw(screen, alg.nicknameInput, 265, 358);

            // la password può essere visibile o meno, l'utente deve solo cliccare l'icona a dx
            if (!alg.showPS) font.draw(screen, "*".repeat(alg.passwordInput.length()), 265, 260);
            else font.draw(screen, alg.passwordInput, 265, 260);
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
        if (fontRed20 != null) fontRed20.dispose();
        img1.dispose();
        img2.dispose();
    }
}
