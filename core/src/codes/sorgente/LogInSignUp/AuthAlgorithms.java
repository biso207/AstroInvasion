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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import org.json.JSONObject;
import sorgente.DataUserManager;
import sorgente.SoundManager;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AuthAlgorithms implements InputProcessor {
    // variabili di controllo digitazione
    protected boolean enteringNickname, enteringPassword;
    // variabili per recuperare nick e psw utente
    public static String nickname, password, date;
    // variabili per comporre le stringhe digitate di nick e psw
    protected final StringBuilder nicknameInput, passwordInput;

    // variabile per nascondere/mostrare la password e cambiare stile pulsanti
    protected boolean showPS=false, isHover1=false, isHover2=false;
    // variabile per controllare l'errore nel nick o psw
    protected boolean error = false;

    /* pagina di riferimento
        0 = LogIn
        1 = SignUp
    */
    protected int state = 0;

    // mouse
    private final Pixmap mouse, mouseOver; // immagini
    private final Cursor cursor; // oggetto cursore

    // istanza classe per mandare la notifica di avvio
    private NotificaAvvio notify;

    // costruttore
    public AuthAlgorithms() {
        // digitazione attiva
        this.enteringNickname = true;
        this.enteringPassword = false;

        // dichiarazione dei stringBuilder
        nicknameInput = new StringBuilder();
        passwordInput = new StringBuilder();

        mouse = new Pixmap(Gdx.files.internal("images/cursor.png"));
        mouseOver = new Pixmap(Gdx.files.internal("images/mouse_over.png"));

        cursor = Gdx.graphics.newCursor(mouse, 0, 0);

        // creazione istanza notifica
        notify = new NotificaAvvio();
    }

    // ************************** //
    // PROCESSI DI AUTENTICAZIONE //
    // ************************** //

    // metodo per direzionare l'utente alla pagina LogIn o SignUp
    public void userOperations() {
        FileHandle checkUser = Gdx.files.local("data/is_user.txt"); // file di verifica della presenza di almeno un utente
        if (!checkUser.exists()) {
            state = 1; // apertura schermata di registrazione
            checkUser.writeString("exists", false); // creazione file
        } else {
            state = 0; // apertura schermata login
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
        try {
            // percorsi nuovo utente
            FileHandle generalFolder = Gdx.files.local("data/" + nicknameInput);

            if (!generalFolder.exists()) {
                generalFolder.mkdirs();

                // nickname e password NUOVO utente
                nickname = String.valueOf(nicknameInput);
                password = String.valueOf(passwordInput);

                // creazione file utente
                createFiles();

                // successo
                state = 2;

                // manda la notifica di apertura gioco
                //notify.sendMessage();
            }
            else if (generalFolder.exists() && (nicknameInput.length()>=1 || passwordInput.length()>=1)) {
                error = true;
            }
        }
        catch (Exception ignored) {
        }
    }

    // algoritmo di accesso
    public void LogInAlg() {
        try {
            System.out.println(nicknameInput);
            // lettura password nickname e password dai file
            String filePath = "data/" + nicknameInput + "/userData.json"; // percorso file json
            FileHandle fileHandle = Gdx.files.local(filePath); // creazione oggetto fileHandle

            String contenuto = fileHandle.readString();
            JSONObject json = new JSONObject(contenuto); // oggetto json per la lettura dei valori

            // lettura delle due chiavi - nick e psw
            String fileNickname = json.getString("nickname");
            String filePassword = json.getString("password");
            date = json.getString("date");

            /// Il pezzo di codice seguente diventa effettivo alla pubblicazione del gioco. Funziona già ed è stato testato
            /*

            // istanza di DataUserManager
            new DataUserManager(nickname);

            // lettura delle due chiavi - nick e psw
            String fileNickname = (String) DataUserManager.getProgress("nickname");
            String filePassword = (String) DataUserManager.getProgress("password");
            date = (String) DataUserManager.getProgress("date");

            */

            /// Il codice seguente è da lasciare invariato

            // controllo correttezza digitazione nick e psw
            if (!filePassword.equals(String.valueOf(passwordInput)) || !fileNickname.equals(String.valueOf(nicknameInput))) {
                error = true;
            }
            else {
                // assegnazione dei nick e psw (corretti) digitati da un utente già registrato
                nickname = String.valueOf(nicknameInput);
                password = String.valueOf(passwordInput);

                // passaggio alla lobby con tutti i progressi già caricati (classe LoginSignupManager.java, riga 108)
                state = 2;

                // manda la notifica di apertura gioco
                //notify.sendMessage();
            }
        }
        catch(Exception e){
            error = true;
            System.out.println("Errore: " + e.getMessage());
        }
    }

    // metodo per creare i file per i progressi utente
    public void createFiles() {
        // mappa per i dati utente
        JSONObject userData = new JSONObject();

        // aggiunta nick e psw del nuovo utente creato
        userData.put("nickname", nickname);
        userData.put("password", password);

        // oggetti per salvare giorno di creazione profilo
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // formato
        date = LocalDate.now().format(formatter); // recupero giorno creazione profilo
        userData.put("date", date);

        // scrittura del JSON
        Path filePath1 = Paths.get("data/" + nicknameInput + "/", "userData.json");
        try (FileWriter fileWriter = new FileWriter(filePath1.toString())) {
            // indentazione di 4
            fileWriter.write(userData.toString(4));
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del file: " + e.getMessage());
        }

        // -------------------------- //
        // mappa per i progressi utente
        JSONObject gameProgresses = new JSONObject();
        /// Le seguenti variabili possono avere dei valori alti per testare il gioco
        gameProgresses.put("avatar", 0);
        gameProgresses.put("credits", 0);
        gameProgresses.put("credits_missions", 0);
        gameProgresses.put("total_credits", 0);
        gameProgresses.put("completed_mission", false);
        gameProgresses.put("diff_classic_game", 1);
        gameProgresses.put("diff_space_battle", 1);
        gameProgresses.put("mission_id", 1);
        gameProgresses.put("level", 1);
        gameProgresses.put("movement_type", 1);
        gameProgresses.put("shot_type", 1);
        gameProgresses.put("spacecraft", 0);
        gameProgresses.put("num_double_points", 1);
        gameProgresses.put("num_gold_heart", 1);
        gameProgresses.put("num_shield", 1);
        gameProgresses.put("num_super_laser", 1);
        gameProgresses.put("num_mission", 1);
        gameProgresses.put("wins_SB_missions", 0);
        gameProgresses.put("num_aliens_hit", 0);
        gameProgresses.put("num_aliens_hit_missions", 0);
        gameProgresses.put("matches_CG", 0);
        gameProgresses.put("matches_SB", 0);
        gameProgresses.put("won_SB", 0);
        gameProgresses.put("cons_won_SB", 0);
        gameProgresses.put("points", 0);
        gameProgresses.put("points_missions", 0);
        gameProgresses.put("state_product_5", false);
        gameProgresses.put("state_product_6", false);
        gameProgresses.put("level_bought", false);
        gameProgresses.put("sound_volume", 0.5);
        gameProgresses.put("music_volume", 0.5);
        gameProgresses.put("alpha_fragments", 0);

        // scrittura del JSON
        Path filePath2 = Paths.get("data/" + nicknameInput + "/", "gameProgresses.json");
        try (FileWriter fileWriter = new FileWriter(filePath2.toString())) {
            // indentato di 4
            fileWriter.write(gameProgresses.toString(4));
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del file: " + e.getMessage());
        }


        /// Il pezzo di codice seguente diventa effettivo alla pubblicazione del gioco. Funziona già ed è stato testato
        /*

        // istanza di DataUserManager
        new DataUserManager(nickname);

        // data di registrazione utente
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // formato
        date = LocalDate.now().format(formatter); // recupero giorno creazione profilo

        // setting dati del nuovo utente
        DataUserManager.setProgress("nickname", nickname); // nickname
        DataUserManager.setProgress("password", password); // password
        DataUserManager.setProgress("date", date); // data di registrazione

        // setting progressi di base del nuovo utente
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
        DataUserManager.setProgress("win_streak_SB", 0); // todo: controllare dove veniva letta cons_won_SB
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

        */
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    // metodo per rilevare il click da tastiera
    @Override public boolean keyTyped(char character) {
        // riproduzione suono digitazione
        SoundManager.playDigitSound(50); // volume al 50% di default

        // scelta del campo da modificare
        StringBuilder currentInput = enteringNickname ? nicknameInput : passwordInput;

        // ENTER terminare la digitazione
        if ((character == '\n' || character == '\r')) {
            if (enteringNickname) {
                enteringPassword=true;
                enteringNickname=false;
            }
            else if ((nicknameInput.length() >= 1 && passwordInput.length() >= 1)) processLoginOrSignup();
        }
        // BACKSPACE per cancellare un carattere
        else if (character == '\b' && currentInput.length() > 0) currentInput.deleteCharAt(currentInput.length() - 1);
        // controllo digitazione caratteri validi
        else if (character >= 32 && character < 127 && currentInput.length() <= 18) currentInput.append(character);
        return true;
    }

    // metodo per controllare i click del mouse
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // riproduzione suono click
        SoundManager.playClickButton(50); // volume al 50% di default

        // cambio pagina - accesso => registrazione
        if (Gdx.files.local("data/is_user.txt").exists() && (screenX >= 425 && screenX <= 559) && (screenY >= 553 && screenY <= 595)) {
            if (state==0) state = 1;
            else state=0;
            error = false;
        }

        // click per avviare il gioco
        if ((nicknameInput.length() >= 1 && passwordInput.length() >= 1) && (screenX >= 415 && screenX <= 565) && (screenY >= 462 && screenY <= 512)) {
            processLoginOrSignup();
        }

        // click per nascondere/mostrare la password
        if ((screenX >= 682 && screenX <= 712) && (screenY >= 384 && screenY <= 404)) {
            showPS = !showPS;
        }
        System.out.println(screenX + " " + screenY);
        // click per attivare la digitazione della password/nickname
        if (!enteringNickname && ((screenX>=249 && screenX<=730) && (screenY>=277 && screenY<=319))) {
            enteringNickname=true;
            enteringPassword=false;
        }
        if (!enteringPassword && ((screenX>=249 && screenX<=730) && (screenY>=375 && screenY<=417)) &&
            !(screenX >= 682 && screenX <= 712) && (screenY >= 384 && screenY <= 404)) {
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
        if ((nicknameInput.length() >= 1 && passwordInput.length() >= 1) && (screenX >= 415 && screenX <= 565) && (screenY >= 462 && screenY <= 512)) {
            isHover1=true;
        }
        // pulsante cambio pagina
        if (Gdx.files.local("data/is_user.txt").exists() && (screenX >= 425 && screenX <= 559) && (screenY >= 553 && screenY <= 595)) {
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
