/*
Astro Invasion - class LogInSignUp -
This class permits the users to create or login profiles
Developed by BIGA©. All rights reserved.
*/

package com.biga.astroinvasion;

// import librerie
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.io.*;
import java.util.Scanner;

public class LogInSignUp extends ScreenAdapter implements InputProcessor {
    private SpriteBatch batch;
    private Texture img1, img2, img3, img4;
    private StringBuilder nicknameInput, passwordInput;
    private boolean enteringNickname;
    public String nickname;
    private int state = 0; // 0 = LogIn, 1 = errore LogIn, 2 = SignUp, 3 = errore SignUp
    private final Game game; // variabile di riferimento tipo gioco

    // costruttore
    public LogInSignUp(Game game) {
        this.game = game;
        this.enteringNickname = true;
        Gdx.input.setInputProcessor(this); // Imposta l'input processor per rilevare caratteri
    }

    // metodo per direzionare l'utente alla pagina LogIn o SignUp
    public void userOperations() {
        // lettura presenza di almeno un utente
        try {
            FileReader checkUser = new FileReader("data/is_user.txt");
            boolean isUSer = Boolean.parseBoolean(new Scanner(checkUser).nextLine());

            // "bivio" operazioni
            if (isUSer) { // almeno un utente presente => operazione accesso
                // set state a "accesso"
                state = 0;
            }
            else { // nessun utente presente => operazione registrazione
                // set state a "registrazione"
                state = 2;
            }
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        operationsScreen();
    }

    // algoritmo per la registrazione
    public void SignUpAlg() {

        //processo registrazione
        File generalFolder = new File("data/" + nicknameInput);
        File dataFolder = new File("data/" + nicknameInput + "/data_user");

        // cerca la cartella con nickname digitato
        if (!generalFolder.exists()) {
            // creazione cartella generale e dati utente
            generalFolder.mkdir();
            dataFolder.mkdir();

            // chiamata metodo per creare i file di base

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
        }
        else { // utente già creato
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

    // metodo con switch per gestire le schermate LogIn e SignUp
    public void operationsScreen() {
        batch = new SpriteBatch();

        // creazione delle 4 possibili immagini da mostrare
        img1 = new Texture("login_signup_pages/page_1_log_in_eng.png");
        img2 = new Texture("login_signup_pages/page_1_log_in_eng_error.png");
        img3 = new Texture("login_signup_pages/page_1_sign_up_eng.png");
        img4 = new Texture("login_signup_pages/page_1_sign_up_eng_error.png");

        // creazione delle variabili di tipo StringBuilder per nickname e password
        nicknameInput = new StringBuilder(); // nickname
        passwordInput = new StringBuilder(); // password

        // controllo inserimento nickname o password
        enteringNickname = true;
        Gdx.input.setInputProcessor(null); // disattivato InputProcessor così da dare Invio con il mouse
    }

    // metodi dalla classe Screen
    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        batch.begin();

        // stampa immagine in base allo stato dello switch
        switch(state) {
            case 1:
                batch.draw(img1, 0, 0); // stampa img accesso
                break;
            case 2:
                batch.draw(img2, 0, 0); // stampa img accesso con errore
                break;
            case 3:
                batch.draw(img3, 0, 0); // stampa img registrazione
                break;
            case 4:
                batch.draw(img4, 0, 0); // stampa img registrazione con errore
                break;
            default:
                break;
        }

        // stampa del testo digitato (nickname e password)
        if (enteringNickname) {
            // stampa nickname in digitazione
            font.draw(batch, nicknameInput.toString(), 100, 50);
        } else {
            // stampa del nickname già digitato e password in digitazione
            font.draw(batch, nicknameInput.toString(), 100, 50);
            font.draw(batch, passwordInput.toString(), 100, 30);
        }

        batch.end();

    }

    /*
    metodo per la digitazione da tastiera di nickname e password,
    e controlla la pressione del tasto "Enter" per passare alla digitazione successiva
    */
    public boolean keyTyped(char character) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (character == '\r' || character == '\n') {
                enteringNickname = false; // inserimento password
            } else {
                // passaggio agli algoritmi una volta completate le digitazioni di nickname e password
                processLoginOrSignup();
            }
        } else if (character == '\b') { // controllo backspace (invio)
            if (enteringNickname && nicknameInput.length() > 0) {
                nicknameInput.setLength(nicknameInput.length() - 1);
            } else if (!enteringNickname && passwordInput.length() > 0) {
                passwordInput.setLength(passwordInput.length() - 1);
            }
        } else {
            // aggiunta dei caratteri digitati a nickname e password
            if (Character.isLetterOrDigit(character) || character == ' ') {
                if (enteringNickname) {
                    nicknameInput.append(character);
                } else {
                    passwordInput.append(character);
                }
            }
        }
        return true;
    }

    // metodo per passare agli algoritmi
    private void processLoginOrSignup() {
        // chiamata algoritmo di controllo (1, 2 = accesso; 3, 4 = registrazione)
        if (state == 1 || state == 2) {
            LogInAlg(); // accesso
        } else {
            SignUpAlg(); // registrazione
        }

        // reset del testo digitato
        nicknameInput.setLength(0);
        passwordInput.setLength(0);
        enteringNickname = true;
    }

    // metodi classe Screen
    @Override public void resize(int i, int i1) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    // dispose per chiudere le "schermate"
    public void dispose() {
        batch.dispose();
        img1.dispose();
        img2.dispose();
        img3.dispose();
        img4.dispose();
    }

    // implementazione vuota per gli altri metodi di InputProcessor
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int i, int i1, int i2, int i3) {return false;}
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
