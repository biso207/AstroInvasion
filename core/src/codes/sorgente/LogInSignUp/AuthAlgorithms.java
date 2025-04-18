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

    // variabile per nascondere/mostrare la password
    protected boolean showPS = false;
    // variabile per controllare l'errore nel nick o psw
    protected boolean error = false;

    /* pagina di riferimento
        0 = LogIn
        1 = SignUp
    */
    protected int state = 0;

    // mouse
    private final Pixmap mouse, mouseOver; // immagini
    private final Cursor cursor, cursorOver; // oggetto cursore

    // costruttore
    public AuthAlgorithms() {
        // digitazione attiva
        this.enteringNickname = true;
        this.enteringPassword = true;

        // dichiarazione dei stringBuilder
        nicknameInput = new StringBuilder();
        passwordInput = new StringBuilder();

        mouse = new Pixmap(Gdx.files.internal("images/cursor.png"));
        mouseOver = new Pixmap(Gdx.files.internal("images/mouse_over.png"));

        cursor = Gdx.graphics.newCursor(mouse, 0, 0);
        cursorOver = Gdx.graphics.newCursor(mouseOver, 0, 0);
    }

    // ************************** //
    // PROCESSI DI AUTENTICAZIONE //
    // ************************** //

    // metodo per direzionare l'utente alla pagina LogIn o SignUp
    public void userOperations() {
        FileHandle checkUser = Gdx.files.local("data/is_user.txt");
        if (!checkUser.exists()) {
            checkUser.writeString("exists", false);
            state = 1; // apertura schermata di registrazione
        } else {
            String isUser = checkUser.readString();
            state = 0; // apertura schermata login
        }
    }

    // metodo per direzione all'algoritmo di registrazione o accesso
    public void processLoginOrSignup() {
        if (state == 0) {
            LogInAlg();
        } else {
            SignUpAlg();
        }

        nicknameInput.setLength(0);
        passwordInput.setLength(0);
        enteringNickname = true;
        enteringPassword = true;
    }

    // algoritmo di registrazione
    public void SignUpAlg() {
        try {
            // percorsi nuovo utente
            FileHandle generalFolder = Gdx.files.local("data/" + nicknameInput);

            if (!generalFolder.exists()) {
                generalFolder.mkdirs();

                nickname = String.valueOf(nicknameInput);
                password = String.valueOf(passwordInput);

                // creazione file utente
                createFiles();

                state = 2;
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
            // lettura password nickname e password dai file
            String filePath = "data/" + nicknameInput + "/userData.json"; // percorso file json
            FileHandle fileHandle = Gdx.files.local(filePath); // creazione oggetto fileHandle

            String contenuto = fileHandle.readString();
            JSONObject json = new JSONObject(contenuto); // oggetto json per la lettura dei valori

            // lettura delle due chiavi - nick e psw
            String fileNickname = json.getString("nickname");
            String filePassword = json.getString("password");
            date = json.getString("date");

            // controllo correttezza digitazione nick e psw
            if (!filePassword.equals(String.valueOf(passwordInput)) || !fileNickname.equals(String.valueOf(nicknameInput))) {
                error = true;
            }
            else {
                state = 2;
            }
        }
        catch(Exception e){
            error = true;
            System.out.println("Errore: " + e.getMessage());
        }

        nickname = String.valueOf(nicknameInput);
        password = String.valueOf(passwordInput);
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
        gameProgresses.put("credits", 100);
        gameProgresses.put("credits_RTG", 0);
        gameProgresses.put("total_credits", 100);
        gameProgresses.put("completed_RTG", false);
        gameProgresses.put("diff_classic_game", 1);
        gameProgresses.put("diff_space_battle", 1);
        gameProgresses.put("mission_id", 1);
        gameProgresses.put("level", 1);
        gameProgresses.put("movement_type", 1);
        gameProgresses.put("shot_type", 1);
        gameProgresses.put("spacecraft", 1);
        gameProgresses.put("num_double_points", 3);
        gameProgresses.put("num_gold_heart", 3);
        gameProgresses.put("num_shield", 3);
        gameProgresses.put("num_super_laser", 3);
        gameProgresses.put("num_mission", 1);
        gameProgresses.put("won_SB_RTG", 0);
        gameProgresses.put("num_aliens_hit", 0);
        gameProgresses.put("num_aliens_hit_RTG", 0);
        gameProgresses.put("matches_CG", 0);
        gameProgresses.put("matches_SB", 0);
        gameProgresses.put("won_SB", 0);
        gameProgresses.put("cons_won_SB", 0);
        gameProgresses.put("points", 0);
        gameProgresses.put("points_RTG", 0);
        gameProgresses.put("state_product_5", false);
        gameProgresses.put("state_product_6", false);
        gameProgresses.put("sound_volume", 0.5);
        gameProgresses.put("music_volume", 0.5);

        // scrittura del JSON
        Path filePath2 = Paths.get("data/" + nicknameInput + "/", "gameProgresses.json");
        try (FileWriter fileWriter = new FileWriter(filePath2.toString())) {
            // indentato di 4
            fileWriter.write(gameProgresses.toString(4));
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del file: " + e.getMessage());
        }
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    // metodo per rilevare il click da tastiera
    @Override public boolean keyTyped(char character) {
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
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        System.out.println(screenX + " " + screenY);
        // cambio pagina - accesso => registrazione
        if (state == 0 && (screenX >= 275 && screenX <= 480) && (screenY >= 525 && screenY <= 565)) {
            state = 1;
            error = false;
        }
        // cambio pagina - registrazione => accesso
        if (state == 1 && (screenX >= 495 && screenX <= 700) && (screenY >= 525 && screenY <= 565)) {
            state = 0;
            error = false;
        }
        // click per accedere o registrarsi
        if ((nicknameInput.length() >= 1 && passwordInput.length() >= 1) && (screenX >= 495 && screenX <= 700) && (screenY >= 525 && screenY <= 565)) {
            processLoginOrSignup();
        }
        // click per nascondere/mostrare la password
        if ((screenX >= 692 && screenX <= 722) && (screenY >= 435 && screenY <= 465)) {
            showPS = !showPS;
        }
        return true;
    }

    // cambio icona mouse al passaggio sugli elementi
    @Override public boolean mouseMoved(int screenX, int screenY) {
        if ((screenX >= 0 && screenX <= 1000) && (screenY >= 0 && screenY <= 700)) {
            Gdx.graphics.setCursor(cursor);
        }
        if ((screenX >= 275 && screenX <= 480) && (screenY >= 525 && screenY <= 565)) {
            Gdx.graphics.setCursor(cursorOver);
        }
        if ((screenX >= 495 && screenX <= 700) && (screenY >= 525 && screenY <= 565)) {
            Gdx.graphics.setCursor(cursorOver);
        }
        if ((screenX >= 692 && screenX <= 722) && (screenY >= 435 && screenY <= 465)) {
            Gdx.graphics.setCursor(cursorOver);
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
