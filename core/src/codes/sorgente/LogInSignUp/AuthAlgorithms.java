/*
Astro Invasion - class AuthAlgorithms -
Implementa i metodi di autenticazione per i processi di accesso e registrazione utente
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.LogInSignUp;

// import librerie e codici
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import sorgente.DataUserManager;
import sorgente.SoundManager;

import java.net.InetAddress;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static sorgente.UserDataPath.getIsUserFilePath;

public class AuthAlgorithms implements InputProcessor {
    // variabili di controllo digitazione
    protected boolean enteringNickname, enteringPassword;
    // variabili per recuperare nick e psw utente
    public static String nickname, password;
    // variabili per comporre le stringhe digitate di nick e psw
    protected final StringBuilder nicknameInput, passwordInput;

    // variabile per nascondere/mostrare la password e cambiare stile pulsanti
    protected boolean showPS=false, isHover1=false, isHover2=false;
    // variabile per controllare l'errore nel nick o psw
    protected boolean error = false;
    // variabile per controllare l'assenza di internet
    protected boolean error2 = false;

    /* pagina di riferimento
        0 = LogIn
        1 = SignUp
    */
    protected int state = 0;

    // mouse
    private final Pixmap mouse; // immagini
    private final Cursor cursor; // oggetto cursore

    // istanza classe per mandare la notifica di avvio
    private final NotificaAvvio notify;


    // costruttore
    public AuthAlgorithms() {
        // attivazione area di digitazione
        this.enteringNickname = checkInternetConnection();
        this.enteringPassword = false;

        // dichiarazione dei stringBuilder
        nicknameInput = new StringBuilder();
        passwordInput = new StringBuilder();

        mouse = new Pixmap(Gdx.files.internal("images/cursor.png"));

        cursor = Gdx.graphics.newCursor(mouse, 0, 0);

        // creazione istanza notifica
        notify = new NotificaAvvio();

        // apertura schermata login
        state = 0;
    }

    // metodo per il rilascio delle risorse
    public void dispose() {
        mouse.dispose();
    }

    // ************************** //
    // PROCESSI DI AUTENTICAZIONE //
    // ************************** //

    // metodo per pulire il nickname da caratteri non adatti alle cartelle
    public static String sanitizeNickname(String input) {
        return input.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    // metodo per verifica che il PC sia connesso ad internet, in caso negativo ogni operazione è bloccata
    public static boolean checkInternetConnection() {
        // esegue un ping su google.com, in caso di risposta positiva vuol dire che il PC è connesso a internet
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return address.isReachable(3000);
        } catch (Exception e) {
            return false;
        }
    }


    // metodo per direzione all'algoritmo di registrazione o accesso
    public void processLoginOrSignup() {
        if (state == 0) {
            LogInAlg(); // algoritmo di accesso
        } else {
            SignUpAlg(); // algoritmo di registrazione
        }

        nicknameInput.setLength(0);
        passwordInput.setLength(0);
        enteringNickname = true;
        enteringPassword = false;
    }

    // algoritmo di registrazione
    public void SignUpAlg() {
        // recupero nickname digitato con pulizia da caratteri non adatti
        nickname = sanitizeNickname(nicknameInput.toString());

        try {
            // controllo presenza utente
            if (!FirestoreStorage.checkUsernameExists(nickname)) {

                // assegnazione della psw digitata
                password = passwordInput.toString();

                // creazione file utente
                createFiles();

                // successo
                state = 2; // schermata lobby

                // manda la notifica di apertura gioco
                //notify.sendMessage();
            }
            else if (!nickname.isEmpty() && !passwordInput.isEmpty()) {
                error = true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // algoritmo di accesso
    public void LogInAlg() {
        // recupero nickname digitato con pulizia da caratteri non adatti
        nickname = sanitizeNickname(nicknameInput.toString());

        try {
            // controllo esistenza del nickname
            if (FirestoreStorage.checkUsernameExists(nickname)) {
                String psw = FirestoreStorage.getPassword(nickname);

                // controllo correttezza digitazione nick e psw
                if (!psw.contentEquals(passwordInput)) {
                    error = true;
                }
                else {
                    // assegnazione della psw corretta
                    password = passwordInput.toString();

                    // cambio schermata con tutti i progressi già caricati (classe LoginSignupManager.java, riga 113)
                    state = 2; // schermata lobby

                    // manda la notifica di apertura gioco
                    //notify.sendMessage();
                }
            }
            else {
                System.out.println("Nickname non trovato");
            }
        }
        catch(Exception e){
            error = true;
        }
    }

    // metodo per creare i file per i progressi utente
    public void createFiles() {
        // data di registrazione utente
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // formato
        String date = LocalDate.now().format(formatter); // recupero giorno creazione profilo

        // setting dati del nuovo utente
        DataUserManager.setProgress("nickname", nickname); // nickname
        DataUserManager.setProgress("password", password); // password
        DataUserManager.setProgress("date", date); // data di registrazione

        // init progressi del nuovo utente
        DataUserManager.setProgress("avatar", 0);
        DataUserManager.setProgress("credits", 0);
        DataUserManager.setProgress("credits_missions", 0);
        DataUserManager.setProgress("total_credits", 0);
        DataUserManager.setProgress("completed_mission", false);
        DataUserManager.setProgress("diff_classic_game", 1);
        DataUserManager.setProgress("diff_space_battle", 1);
        DataUserManager.setProgress("mission_id", 1);
        DataUserManager.setProgress("level", 1);
        DataUserManager.setProgress("movement_type", 1);
        DataUserManager.setProgress("shot_type", 1);
        DataUserManager.setProgress("spacecraft", 0);
        DataUserManager.setProgress("num_double_points", 1);
        DataUserManager.setProgress("num_gold_heart", 1);
        DataUserManager.setProgress("num_shield", 1);
        DataUserManager.setProgress("num_super_laser", 1);
        DataUserManager.setProgress("num_mission", 1);
        DataUserManager.setProgress("wins_SB_missions", 0);
        DataUserManager.setProgress("num_aliens_hit", 0);
        DataUserManager.setProgress("num_aliens_hit_missions", 0);
        DataUserManager.setProgress("matches_CG", 0);
        DataUserManager.setProgress("matches_SB", 0);
        DataUserManager.setProgress("won_SB", 0);
        DataUserManager.setProgress("win_streak_SB", 0);
        DataUserManager.setProgress("points", 0);
        DataUserManager.setProgress("points_missions", 0);
        DataUserManager.setProgress("state_product_5", false);
        DataUserManager.setProgress("state_product_6", false);
        DataUserManager.setProgress("level_bought", false);
        DataUserManager.setProgress("sound_volume", 0.5);
        DataUserManager.setProgress("music_volume", 0.5);
        DataUserManager.setProgress("alpha_fragments", 0);

        // salvataggio su file dei progressi e dati utente iniziali
        DataUserManager.saveProgresses();
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //

    // metodo per validare gli input
    private boolean isValidInput() {
        return !nicknameInput.isEmpty() && !passwordInput.isEmpty();
    }

    // metodo per rilevare il click da tastiera
    @Override public boolean keyTyped(char character) {
        // riproduzione suono digitazione
        SoundManager.playDigitSound(50); // suono del click

        // scelta del campo da modificare
        StringBuilder currentInput = enteringNickname ? nicknameInput : passwordInput;

        // ENTER terminare la digitazione
        if ((character == '\n' || character == '\r')) {
            if (enteringNickname) {
                enteringPassword = true;
                enteringNickname = false;
            } else if (isValidInput()) {
                if (!checkInternetConnection()) error2=true;
                else processLoginOrSignup();
            }
        }
        // BACKSPACE per cancellare un carattere
        else if (character == '\b' && !currentInput.isEmpty()) currentInput.deleteCharAt(currentInput.length() - 1);
            // controllo digitazione caratteri validi
        else if (character >= 32 && character < 127 && currentInput.length() <= 18) currentInput.append(character);
        return true;
    }

    // metodo per controllare i click del mouse
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // cambio pagina - accesso => registrazione
        if ((screenX >= 425 && screenX <= 559) && (screenY >= 553 && screenY <= 595)) {
            SoundManager.playClickButton(50); // suono del click
            if (state==0) state = 1;
            else state=0;
            error = false;
        }

        // click per avviare il gioco
        if (isValidInput() && (screenX >= 415 && screenX <= 565) && (screenY >= 462 && screenY <= 512)) {
            SoundManager.playClickButton(50); // suono del click

            if (!checkInternetConnection()) error2=true;
            else processLoginOrSignup();
        }

        // click per nascondere/mostrare la password
        if ((screenX >= 682 && screenX <= 712) && (screenY >= 384 && screenY <= 404)) {
            SoundManager.playClickButton(50); // suono del click
            showPS = !showPS;
        }

        // click per attivare la digitazione della password
        if (!enteringNickname && ((screenX>=249 && screenX<=730) && (screenY>=277 && screenY<=319))) {
            SoundManager.playClickButton(50); // suono del click
            enteringNickname=true;
            enteringPassword=false;
        }
        // click per attivare la digitazione del nickname
        if (!enteringPassword && ((screenX>=249 && screenX<=730) && (screenY>=375 && screenY<=417))) {
            SoundManager.playClickButton(50); // suono del click
            enteringPassword=true;
            enteringNickname=false;
        }
        return true;
    }

    // cambio icona mouse al passaggio sugli elementi
    @Override public boolean mouseMoved(int screenX, int screenY) {
        // finché si muove fuori dai pulsanti rimangono spenti, con le grafiche di base
        isHover1=isHover2=false;

        // schermo intero per icona mouse
        if ((screenX >= 0 && screenX <= 1000) && (screenY >= 0 && screenY <= 700)) {
            Gdx.graphics.setCursor(cursor);
        }
        // pulsante accesso gioco
        if (isValidInput() && (screenX >= 415 && screenX <= 565) && (screenY >= 462 && screenY <= 512)) {
            isHover1=true;
        }
        // pulsante cambio pagina
        if (checkInternetConnection() && (screenX >= 425 && screenX <= 559) && (screenY >= 553 && screenY <= 595)) {
            isHover2=true;
        }

        return true;
    }

    // altri metodi
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}
