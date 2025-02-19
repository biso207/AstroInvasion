/*
Astro Invasion - class AuthAlgorithms -
Implementa i metodi di autenticazione per i processi di accesso e registrazione utente
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.UI.LogInSignUp;

// import librerie
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;

public class AuthAlgorithms implements InputProcessor {
    // variabili di controllo digitazione
    protected boolean enteringNickname, enteringPassword;
    // variabili per recuperare nick e psw utente
    public String nickname, password;
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
            FileHandle dataFolder = Gdx.files.local("data/" + nicknameInput + "/data_user");

            if (!generalFolder.exists() && !dataFolder.exists()) {
                generalFolder.mkdirs();
                dataFolder.mkdirs();

                // scrittura del nickname digitato
                FileHandle writeNick = Gdx.files.local("data/" + nicknameInput + "/data_user/nickname.txt");
                writeNick.writeString(String.valueOf(nicknameInput), false);  // `false` sovrascrive il file se già esiste

                // scrittura della password digitata
                FileHandle writePass = Gdx.files.local("data/" + nicknameInput + "/data_user/password.txt");
                writePass.writeString(String.valueOf(passwordInput), false);  // `false` sovrascrive il file se già esiste

                nickname = String.valueOf(nicknameInput);
                password = String.valueOf(passwordInput);

                // creazione file utente
                createFiles();

                state = 2;
            }
            else if ((generalFolder.exists() && dataFolder.exists()) && (nicknameInput.length()>=1 || passwordInput.length()>=1)) {
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
            FileHandle readPass = Gdx.files.internal("data/" + nicknameInput + "/data_user/password.txt");
            FileHandle readNick = Gdx.files.internal("data/" + nicknameInput + "/data_user/nickname.txt");
            String filePassword = readPass.readString();
            String fileNickname = readNick.readString();

            if (!filePassword.equals(String.valueOf(passwordInput)) || !fileNickname.equals(String.valueOf(nicknameInput))) {
                error = true;
            }
            else {
                state = 2;
            }
        }
        catch(Exception e){
            error = true;
        }

        nickname = String.valueOf(nicknameInput);
        password = String.valueOf(passwordInput);
    }

    /// TODO: implementare il nuovo metodo per creare i file alla creazione utente. Deve creare un json.
    // metodo per creare i file per i progressi utente
    public void createFiles() {
        // avatar
        FileHandle fileAvatar = Gdx.files.local("data/" + nicknameInput + "/progresses/avatar.txt");
        fileAvatar.writeString("1", false);
        // monete
        FileHandle writeCredits = Gdx.files.local("data/" + nicknameInput + "/progresses/credits.txt");
        writeCredits.writeString("100", false);
        // completamento missione RoadToGlory
        FileHandle writeRTG = Gdx.files.local("data/" + nicknameInput + "/progresses/completed_rtg.txt");
        writeRTG.writeString("false", false);
        // difficoltà classic game
        FileHandle writeDiffCG = Gdx.files.local("data/" + nicknameInput + "/progresses/diff_classic_game.txt");
        writeDiffCG.writeString("1", false);
        // difficoltà space battle
        FileHandle writeDiffSB = Gdx.files.local("data/" + nicknameInput + "/progresses/diff_space_battle.txt");
        writeDiffSB.writeString("1", false);
        // id missione (1-4)
        FileHandle writeID = Gdx.files.local("data/" + nicknameInput + "/progresses/mission_id.txt");
        writeID.writeString("1", false);
        // livello
        FileHandle writeLevel = Gdx.files.local("data/" + nicknameInput + "/progresses/level.txt");
        writeLevel.writeString("1", false);
        // tipo di movimento
        FileHandle writeMovement = Gdx.files.local("data/" + nicknameInput + "/progresses/movement_type.txt");
        writeMovement.writeString("1", false);
        // tipo di sparo
        FileHandle writeShot = Gdx.files.local("data/" + nicknameInput + "/progresses/shot_type.txt");
        writeShot.writeString("1", false);
        // navicella
        FileHandle writeSpacecraft = Gdx.files.local("data/" + nicknameInput + "/progresses/spacecraft.txt");
        writeSpacecraft.writeString("1", false);
        // numero carte double points
        FileHandle writeNumCards1 = Gdx.files.local("data/" + nicknameInput + "/progresses/num_double_points.txt");
        writeNumCards1.writeString("3", false);
        // numero carte gold heart
        FileHandle writeNumCards2 = Gdx.files.local("data/" + nicknameInput + "/progresses/num_gold_heart.txt");
        writeNumCards2.writeString("3", false);
        // numero carte shield
        FileHandle writeNumCards3 = Gdx.files.local("data/" + nicknameInput + "/progresses/num_shield.txt");
        writeNumCards3.writeString("3", false);
        // numero carte super laser
        FileHandle writeNumCards4 = Gdx.files.local("data/" + nicknameInput + "/progresses/num_super_laser.txt");
        writeNumCards4.writeString("3", false);
        // numero missione raggiunta
        FileHandle writeMission = Gdx.files.local("data/" + nicknameInput + "/progresses/num_mission.txt");
        writeMission.writeString("1", false);
        // numero partite vinte a space battle consecutive per RTG
        FileHandle writeWonSbRTG = Gdx.files.local("data/" + nicknameInput + "/progresses/won_SB_RTG.txt");
        writeWonSbRTG.writeString("0", false);
        // numero alieni colpiti in classic game
        FileHandle writeNumAliensHit = Gdx.files.local("data/" + nicknameInput + "/progresses/num_aliens_hit.txt");
        writeWonSbRTG.writeString("0", false);
        // partite classic game
        FileHandle writeMatchesCG = Gdx.files.local("data/" + nicknameInput + "/progresses/matches_CG.txt");
        writeMatchesCG.writeString("0", false);
        // partite space battle
        FileHandle writeMatchesSB = Gdx.files.local("data/" + nicknameInput + "/progresses/matches_SB.txt");
        writeMatchesSB.writeString("0", false);
        // vittorie space battle
        FileHandle writeWonSB = Gdx.files.local("data/" + nicknameInput + "/progresses/won_SB.txt");
        writeWonSB.writeString("0", false);
        // vittorie consecutive space battle
        FileHandle writeConsWonSB = Gdx.files.local("data/" + nicknameInput + "/progresses/cons_won_SB.txt");
        writeConsWonSB.writeString("0", false);
        // punteggio utente
        FileHandle writePoints = Gdx.files.local("data/" + nicknameInput + "/progresses/points.txt");
        writePoints.writeString("0", false);

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
