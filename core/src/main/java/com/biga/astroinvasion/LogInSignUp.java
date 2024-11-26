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
import com.badlogic.gdx.graphics.Color;

public class LogInSignUp extends ScreenAdapter {
    private final SpriteBatch screen;
    private Texture img1, img2, img3, img4;
    private BitmapFont font;
    private final StringBuilder nicknameInput;
    private final StringBuilder passwordInput;
    private boolean enteringNickname;
    private boolean enteringPassword;
    public static String nickname;
    public static String password;
    private int state = 0; // 0 = LogIn, 1 = errore LogIn, 2 = SignUp, 3 = errore SignUp
    private final Main game; // variabile di riferimento tipo gioco

    // costruttore
    public LogInSignUp(Main game) {
        this.game = game;
        this.screen = game.screen;
        this.enteringNickname = true;
        this.enteringPassword = true;

        // carica il font
        loadFont();

        // init stringhe
        nicknameInput = new StringBuilder();
        passwordInput = new StringBuilder();

        // init di MyInputProcessor per gestire l'input di nickname e password
        Gdx.input.setInputProcessor(new MyInputProcessor());

        userOperations();
    }

    // -------------- //
    // GESTIONE INPUT //
    // -------------- //

    // classe interna per gestire gli input da mouse e tastiera
    private class MyInputProcessor extends InputAdapter {
        @Override
        public boolean keyTyped(char character) {
            if (enteringNickname) {
                if ((character == '\n' || character == '\r') && nicknameInput.length()>=1) { // ENTER per terminare il nickname
                    enteringNickname = false;
                } else if (character == '\b' && nicknameInput.length() > 0) { // BACKSPACE per cancellare l'ultimo carattere
                    nicknameInput.deleteCharAt(nicknameInput.length() - 1);
                } else if (character >= 32 && character < 127 && nicknameInput.length()<=20) { // controllo digitazione caratteri validi
                    nicknameInput.append(character);
                }
            } else if (enteringPassword) {
                if ((character == '\n' || character == '\r') && passwordInput.length()>=1) { // ENTER per terminare la password
                    enteringPassword = false;
                } else if (character == '\b' && passwordInput.length() > 0) { // BACKSPACE per cancellare l'ultimo carattere
                    passwordInput.deleteCharAt(passwordInput.length() - 1);
                } else if (character >= 32 && character < 127 && passwordInput.length()<=20) { // controllo digitazione caratteri validi
                    passwordInput.append(character);
                }
            }
            return true;
        }

        // metodo per recuperare il click del mouse
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            // cambio pagina - accesso => registrazione
            if ((state==0 || state==1) && (screenX >= 288 && screenX <= 479) && (screenY >= 525 && screenY <= 565)) {
                state=2;
            }
            // cambio pagina - registrazione => accesso
            if ((state==2 || state==3) && (screenX >= 520 && screenX <= 710) && (screenY >= 525 && screenY <= 565)) {
                state = 0; // cambio stato per pagina di accesso
            }
            // proseguimento da accesso - click pulsante "accedi"
            if ((state==0 || state==1) && (nicknameInput.length()>=1&&passwordInput.length()>=1) && (screenX >= 520 && screenX <= 710) && (screenY >= 525 && screenY <= 565)) {
                processLoginOrSignup();
            }
            // proseguimento da registrazione - click pulsante "registrati"
            if ((state==2 || state==3) && (nicknameInput.length()>=1&&passwordInput.length()>=1) && (screenX >= 520 && screenX <= 710) && (screenY >= 525 && screenY <= 565)) {
                processLoginOrSignup();
            }
            return true;
        }
    }

    // ------------------- //
    // LOGICA DELLA CLASSE //
    // ------------------- //

    // metodo per direzionare l'utente alla pagina LogIn o SignUp
    public void userOperations() {
        FileHandle checkUser = Gdx.files.local("data/is_user.txt");
        String isUser = checkUser.readString();

        // scrittura di un valore nel file così da aprire la schermata login alla prossima apertura
        if (isUser != null && !isUser.isEmpty()) {
            state = 0; // LogIn
        } else {
            state = 2; // SignUp
            checkUser.writeString("exists", false);
        }


        operationsScreen();
    }

    // metodo per aprire l'algoritmo di accesso o registrazione
    private void processLoginOrSignup() {
        if (state == 0 || state == 1) {
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

                state=4;
            }
            else if ((generalFolder.exists() && dataFolder.exists()) && (nicknameInput.length()>=1 || passwordInput.length()>=1)) {
                state=3;
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
                state = 1;
            } else state = 4;
        }catch(Exception e){
            state = 1;
        }
        nickname = String.valueOf(nicknameInput);
        password = String.valueOf(passwordInput);
    }

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
        writeNumCards1.writeString("1", false);
        // numero carte gold heart
        FileHandle writeNumCards2 = Gdx.files.local("data/" + nicknameInput + "/progresses/num_gold_heart.txt");
        writeNumCards2.writeString("1", false);
        // numero carte shield
        FileHandle writeNumCards3 = Gdx.files.local("data/" + nicknameInput + "/progresses/num_shield.txt");
        writeNumCards3.writeString("1", false);
        // numero carte super laser
        FileHandle writeNumCards4 = Gdx.files.local("data/" + nicknameInput + "/progresses/num_super_laser.txt");
        writeNumCards4.writeString("1", false);
        // numero missione raggiunta
        FileHandle writeMission = Gdx.files.local("data/" + nicknameInput + "/progresses/num_mission.txt");
        writeMission.writeString("1", false);
        // numero partite vinte a space battle consecutive per RTG
        FileHandle writeWonSbRTG = Gdx.files.local("data/" + nicknameInput + "/progresses/won_SB_RTG.txt");
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

    // -------------------- //
    // GRAFICA DELLA CLASSE //
    // -------------------- //

    // caricamento e creazione font per le scritte
    private void loadFont() {
        try {
            font = new BitmapFont(Gdx.files.internal("font/inter/Inter-Regular.fnt")); // font personalizzato (inter)
            font.setColor(Color.valueOf("#151A3B")); // colore blu
        } catch (Exception e) {
            font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("#151A3B")); // colore blu
        }
    }

    // metodo per caricare le immagini Accesso e Registrazione
    public void operationsScreen() {
        img1 = new Texture("login_signup_pages/page_1_log_in_eng.png");
        img2 = new Texture("login_signup_pages/page_1_log_in_eng_error.png");
        img3 = new Texture("login_signup_pages/page_2_sign_up_eng.png");
        img4 = new Texture("login_signup_pages/page_2_sign_up_eng_error.png");

        enteringNickname = true;
        Gdx.input.setInputProcessor(new MyInputProcessor()); // riattiva l'InputProcessor
    }

    @Override
    public void show() {}

    // metodo per aggiornare lo schermo
    @Override
    public void render(float delta) {
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
                game.setScreen(new Lobby(game));
                break;
            default:
                break;
        }

        if (enteringNickname) {
            font.draw(screen, nicknameInput, 265, 358);
        } else if (enteringPassword) {
            font.draw(screen, nicknameInput, 265, 358);
            font.draw(screen, passwordInput, 265, 260);
        } else {
            processLoginOrSignup();
        }
        screen.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    // metodo per rilasciare le risorse
    @Override
    public void dispose() {
        if (font != null) font.dispose();
        img1.dispose();
        img2.dispose();
        img3.dispose();
        img4.dispose();
    }
}
