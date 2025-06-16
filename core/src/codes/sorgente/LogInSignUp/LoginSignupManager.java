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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.checkerframework.checker.units.qual.A;
import sorgente.DataUserManager;
import sorgente.LoadingScreen;
import sorgente.Main;
import sorgente.ResourceLoader;
import sorgente.Lobby.LobbyManager;

import java.io.IOException;

public class LoginSignupManager extends ScreenAdapter implements ResourceLoader {
    // variabile di riferimento al gioco
    private final Main game;
    // screen di gioco
    private final SpriteBatch screen;

    // istanza classe algoritmi
    private final AuthAlgorithms alg;

    // font
    private BitmapFont font, fontBoldYellow20;
    // immagini
    private Texture img1, img2, digitAreaON, digitAreaOFF, showPS, coverPS, loginPageBtnHover,
        signupPageBtnHover, continueBtnHover, noInternet;

    // costruttore
    public LoginSignupManager(Main game) {
        this.game = game;
        this.screen = game.screen;

        // istanza classe degli algoritmi
        alg = new AuthAlgorithms();

        // caricamento font e immagini
        this.loadFont();
        this.loadImages();
    }

    // ******************* //
    // CARICAMENTO RISORSE //
    // ******************* //

    // metodo per caricare e creare i font
    @Override
    public void loadFont() {
        try {
            font = new BitmapFont(Gdx.files.internal("font/inter/bold_white_30.fnt")); // inter-bold white 30
            fontBoldYellow20 = new BitmapFont(Gdx.files.internal("font/inter/bold_yellow_20.fnt")); // inter-regular red 20
        } catch (Exception e) {
            font = new BitmapFont(); // font di default (arial)
        }
    }

    // metodo per caricare le immagini delle pagine di Accesso e Registrazione
    @Override
    public void loadImages() {
        // sfondi
        img1 = new Texture("login_signup_screens/page_1_log_in_eng.png");
        img2 = new Texture("login_signup_screens/page_2_sign_up_eng.png");
        // icona mostra/nascondi password
        showPS = new Texture("login_signup_screens/showPS.png");
        coverPS = new Texture("login_signup_screens/coverPS.png");
        // aree di digitazione
        digitAreaON = new Texture("login_signup_screens/digit_area_on.png");
        digitAreaOFF = new Texture("login_signup_screens/digit_area_off.png");
        // pulsanti
        loginPageBtnHover = new Texture("images/btns_hover/new_profile_button_hover.png");
        signupPageBtnHover = new Texture("images/btns_hover/login_button_hover.png");
        continueBtnHover = new Texture("images/btns_hover/continue_button_hover.png");
        // icona internet assente
        noInternet = new Texture("login_signup_screens/no_internet.png");

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
                if (alg.error) fontBoldYellow20.draw(screen, "Password wrong",362,72);
                if (alg.error1) fontBoldYellow20.draw(screen, "Nickname not found",362,72);
                if (alg.error3) fontBoldYellow20.draw(screen, "Your session is already open on another device",330,72);
                break;
            case 1:
                screen.draw(img2, 0, 0);
                if (alg.error) fontBoldYellow20.draw(screen, "Nickname already in use",388,72);
                break;
            case 2:
                // caricamento risorse utente
                try {
                    // recupero progressi utente
                    DataUserManager.loadProgresses();
                    // salvataggio password in remoto
                    FirestoreStorage.savePassword(AuthAlgorithms.nickname, AuthAlgorithms.password);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // schermata di caricamento per upload/download dati
                LoadingScreen loadingScreen = new LoadingScreen(game, false);
                GlobalProgressManager.setListener(loadingScreen);
                game.setScreen(loadingScreen);
                break;
            default:
                break;
        }

        // messaggio "internet assente"
        if (alg.error2) {
            screen.draw(noInternet, 364, 50);
            fontBoldYellow20.draw(screen, "No Internet Connection",406,72);
        }

        // aree di testo
        screen.draw(alg.enteringNickname ? digitAreaON : digitAreaOFF, 257, 379);
        screen.draw(alg.enteringPassword ? digitAreaON : digitAreaOFF, 257, 281);

        if (alg.showPS) screen.draw(showPS, 690,288);
        else screen.draw(coverPS, 690,288);

        if (alg.isHover1) screen.draw(continueBtnHover, 422, 185);
        if (alg.isHover2) {
            if (alg.state==1) screen.draw(signupPageBtnHover, 428, 99);
            else screen.draw(loginPageBtnHover, 428, 99);
        }

        font.draw(screen, alg.nicknameInput, 265, 414); // testo password

        // la password può essere visibile o meno, l'utente deve solo cliccare l'icona a dx
        if (!alg.showPS) font.draw(screen, "*".repeat(alg.passwordInput.length()), 265, 310);
        else font.draw(screen, alg.passwordInput, 265, 315);
        screen.end();
    }

    // spegnimento controllo input
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    // rilascio delle risorse
    @Override public void dispose() {
        if (font != null) font.dispose();
        if (fontBoldYellow20 != null) fontBoldYellow20.dispose();
        img1.dispose();
        img2.dispose();
        digitAreaON.dispose();
        digitAreaOFF.dispose();
        showPS.dispose();
        coverPS.dispose();
        loginPageBtnHover.dispose();
        signupPageBtnHover.dispose();
        continueBtnHover.dispose();
        noInternet.dispose();

        alg.dispose(); // rilascio risorse della classe degli algoritmi
    }
}
