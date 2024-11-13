/*
Astro Invasion - class LogInSignUp -
This class permits the users to create or login profiles
Developed by BIGA©. All rights reserved.
*/

package com.biga.astroinvasion;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.io.*;
import java.util.Scanner;

public class LogInSignUp extends ScreenAdapter {
    private SpriteBatch screen;
    private Texture img1, img2, img3, img4;
    private BitmapFont font; // Aggiungi BitmapFont per il testo
    private StringBuilder nicknameInput;
    private StringBuilder passwordInput;
    private boolean enteringNickname;
    private boolean enteringPassword;
    public String nickname;
    private int state = 0; // 0 = LogIn, 1 = errore LogIn, 2 = SignUp, 3 = errore SignUp
    private final Main game; // variabile di riferimento tipo gioco

    // costruttore
    public LogInSignUp(Main game) {
        this.game = game;
        this.screen = game.screen;
        this.enteringNickname = true;
        this.enteringPassword = true;

        // Carica il font
        loadFont();

        // Inizializza le stringhe
        nicknameInput = new StringBuilder();
        passwordInput = new StringBuilder();

        // chiama le operazioni iniziali per aprire la pagina di accesso o registrazione
        userOperations();
    }

    // LOGICA
    // metodo per direzionare l'utente alla pagina LogIn o SignUp
    public void userOperations() {
        // lettura presenza di almeno un utente
        FileHandle checkUser = Gdx.files.internal("data/is_user.txt");
        String isUser = checkUser.readString();
        //FileReader checkUser = new FileReader("data/is_user.txt");
        //boolean isUser = Boolean.parseBoolean(new Scanner(checkUser).nextLine());

        // "bivio" operazioni
        if (isUser!=null) { // almeno un utente presente => operazione accesso
            // set state a "accesso"
            state = 0;
        } else { // nessun utente presente => operazione registrazione
            // set state a "registrazione"
            state = 2;
        }

        operationsScreen();
    }

    // metodo per passare agli algoritmi
    private void processLoginOrSignup() {
        // chiamata algoritmo di controllo (0, 1 = accesso; 2, 3 = registrazione)
        if (state == 0 || state == 1) {
            LogInAlg(); // accesso
        } else {
            SignUpAlg(); // registrazione
        }

        // reset del testo digitato
        nicknameInput.setLength(0);
        passwordInput.setLength(0);
        enteringNickname = true;
        enteringPassword = true;
    }

    // algoritmo per la registrazione
    public void SignUpAlg() {
        // processo registrazione
        File generalFolder = new File("data/" + nicknameInput);
        File dataFolder = new File("data/" + nicknameInput + "/data_user");

        // cerca la cartella con nickname digitato
        if (!generalFolder.exists()) {
            // creazione cartella generale e dati utente
            generalFolder.mkdir();
            dataFolder.mkdir();

            // scrittura dati utente
            try {
                FileWriter writeNick = new FileWriter("data/" + nicknameInput + "/data_user/nickname.txt");
                writeNick.write(String.valueOf(nicknameInput)); // nickname utente
                writeNick.close();

                FileWriter writePass = new FileWriter("data/" + nicknameInput + "/data_user/password.txt");
                writePass.write(String.valueOf(passwordInput)); // password utente
                writePass.close();

                nickname = String.valueOf(nicknameInput); // assegnazione del valore di nicknameInput a nickname
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else { // utente già creato
            // set dello stato in "utente già creato"
            state = 3;
        }
    }

    // algoritmo per l'accesso
    public void LogInAlg() {
        // try-catch che prova la lettura file di registrazione
        try {
            // lettura password => se fallisce la creazione del file il nickname è errato e genera un'eccezione
            File readPass = new File("data/" + nicknameInput + "/data_user/password.txt");
            String readPasswordInput = new Scanner(readPass).nextLine();

            // controllo correttezza password
            if (!readPasswordInput.equals(String.valueOf(passwordInput))) {
                // set state a "nickname o password errati"
                state = 1;
            }
        } catch (IOException e) {
            // set state a "nickname o password errati"
            state = 1;
        }

        nickname = String.valueOf(nicknameInput); // setting della variabile nickname con quello digitato correttamente
    }

    // GRAFICA
    // metodo per caricare il font
    private void loadFont() {
        try {
            font = new BitmapFont(Gdx.files.internal("font/inter/Inter-Regular.fnt"));
        } catch (Exception e) {
            Gdx.app.log("Font Error", "Il font non è stato caricato correttamente: " + e.getMessage());
            font = new BitmapFont(); // Carica un font predefinito in caso di errore
        }
    }

    // metodo con switch per gestire le schermate LogIn e SignUp
    public void operationsScreen() {
        // creazione delle 4 possibili immagini da mostrare
        img1 = new Texture("login_signup_pages/page_1_log_in_eng.png");
        img2 = new Texture("login_signup_pages/page_1_log_in_eng_error.png");
        img3 = new Texture("login_signup_pages/page_2_sign_up_eng.png");
        img4 = new Texture("login_signup_pages/page_2_sign_up_eng_error.png");

        // controllo inserimento nickname o password
        enteringNickname = true;
        Gdx.input.setInputProcessor(null); // disattivato InputProcessor così da dare Invio con il mouse
    }

    // metodi dalla classe Screen
    @Override
    public void show() {}

    @Override
    public void render(float v) {
        handleInput();
        screen.begin();

        // stampa immagine in base allo stato dello switch
        switch (state) {
            case 0:
                screen.draw(img1, 0, 0); // stampa img accesso
                break;
            case 1:
                screen.draw(img2, 0, 0); // stampa img accesso con errore
                break;
            case 2:
                screen.draw(img3, 0, 0); // stampa img registrazione
                break;
            case 3:
                screen.draw(img4, 0, 0); // stampa img registrazione con errore
                break;
            default:
                break;
        }

        // stampa del testo digitato (nickname e password)
        if (enteringNickname) {
            font.draw(screen, nicknameInput, 100, 300); // stampa del nickname che si sta digitando
        }
        else if (enteringPassword) {
            font.draw(screen, nicknameInput, 100, 300); // stampa del nickname digitato
            font.draw(screen, passwordInput, 100, 250); // stampa della password che si sta digitando
        }
        else { // nickname e password digitati
            // apertura algoritmi di controllo nickname e password
            processLoginOrSignup();
        }
        screen.end();
    }

    /*
    metodo per la digitazione da tastiera di nickname e password,
    e controlla la pressione del tasto "Enter" per passare alla digitazione successiva
    */
    private void handleInput() {
        // Controlla se si sta digitando un nickname
        if (enteringNickname) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                enteringNickname = false; // Passa a digitare la password
            } else {
                // Aggiungi caratteri alla stringa finché non viene premuto 'ENTER'
                for (char c = 0; c < 128; c++) {
                    if (Gdx.input.isKeyJustPressed(c)) {
                        nicknameInput.append(c);
                    }
                }
            }
        } else {
            // Controlla per la password
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                enteringPassword = false;
            } else {
                for (char c = 0; c < 128; c++) {
                    if (Gdx.input.isKeyJustPressed(c)) {
                        passwordInput.append(c);
                    }
                }
            }
        }
    }

    // metodi classe Screen
    @Override public void resize(int i, int i1) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    // dispose per chiudere le "schermate"
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
        img1.dispose();
        img2.dispose();
        img3.dispose();
        img4.dispose();
    }
}
