/*
Astro Invasion - class LogInSignUp -
This class permits the users to create or login profiles
Developed by BIGA©. All rights reserved.
*/

package com.biga.astroinvasion;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.io.*;
import java.util.Scanner;

public class LogInSignUp extends ScreenAdapter {
    private SpriteBatch screen;
    private Texture img1, img2, img3, img4;
    private BitmapFont font;
    private final StringBuilder nicknameInput;
    private final StringBuilder passwordInput;
    private boolean enteringNickname;
    private boolean enteringPassword;
    public String nickname;
    private int state = 0; // 0 = LogIn, 1 = errore LogIn, 2 = SignUp, 3 = errore SignUp

    public LogInSignUp(Main game) {
        this.screen = game.screen;
        this.enteringNickname = true;
        this.enteringPassword = true;

        // Carica il font
        loadFont();

        // init stringhe
        nicknameInput = new StringBuilder();
        passwordInput = new StringBuilder();

        // init di MyInputProcessor per gestire l'input di nickname e password
        Gdx.input.setInputProcessor(new MyInputProcessor());

        userOperations();
    }

    // classe interna per gestire l'input di nickname e password
    private class MyInputProcessor extends InputAdapter {
        @Override
        public boolean keyTyped(char character) {
            if (enteringNickname) {
                if (character == '\n' || character == '\r' && nicknameInput.length()>=1) { // ENTER per terminare il nickname
                    enteringNickname = false;
                } else if (character == '\b' && nicknameInput.length() > 0) { // BACKSPACE per cancellare l'ultimo carattere
                    nicknameInput.deleteCharAt(nicknameInput.length() - 1);
                } else if (character >= 32 && character < 127 && nicknameInput.length()<=20) { // Controllo per caratteri validi
                    nicknameInput.append(character);
                }
            } else if (enteringPassword) {
                if (character == '\n' || character == '\r' && passwordInput.length()>=1) { // ENTER per terminare la password
                    enteringPassword = false;
                } else if (character == '\b' && passwordInput.length() > 0) { // BACKSPACE per cancellare l'ultimo carattere
                    passwordInput.deleteCharAt(passwordInput.length() - 1);
                } else if (character >= 32 && character < 127 && passwordInput.length()<=20) { // Controllo per caratteri validi
                    passwordInput.append(character);
                }
            }
            return true;
        }
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            // click pulsante "crea nuovo profilo" da pagina accesso
            if ((state==0 || state==1) && (screenX >= 288 && screenX <= 479) && (screenY >= 525 && screenY <= 565)) {
                state=2;
            }
            // click pulsante "avanti" da pagina "accesso" o "accesso con errore"
            if ((state==0 || state==1) && (nicknameInput.length()>=1&&passwordInput.length()>=1) && (screenX >= 520 && screenX <= 710) && (screenY >= 525 && screenY <= 565)) {
                state=4;
            }
            // click pulsante "avanti" da pagina "registrazione" o "registrazione con errore"
            if ((state==2 || state==3) && (nicknameInput.length()>=1&&passwordInput.length()>=1) && (screenX >= 385 && screenX <= 620) && (screenY >= 525 && screenY <= 565)) {
                state=4;
            }
            return true;
        }
    }


    // Metodo per direzionare l'utente alla pagina LogIn o SignUp
    public void userOperations() {
        FileHandle checkUser = Gdx.files.internal("data/is_user.txt");
        String isUser = checkUser.readString();

        if (isUser != null) {
            state = 0;
        } else {
            state = 2;
        }

        operationsScreen();
    }

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

                state=4;
            }
            else if ((generalFolder.exists() && dataFolder.exists()) && (nicknameInput.length()>=1 || passwordInput.length()>=1)) {
                state=3;
            }
        }
        catch (Exception ignored) {
        }
    }

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
        }catch(Exception ignored){
        }
        nickname = String.valueOf(nicknameInput);
    }

    private void loadFont() {
        try {
            font = new BitmapFont(Gdx.files.internal("font/inter/Inter-Regular.fnt"));
            font.setColor(Color.valueOf("#151A3B"));
        } catch (Exception e) {
            Gdx.app.log("Font Error", "Il font non è stato caricato correttamente: " + e.getMessage());
            font = new BitmapFont();
            font.setColor(Color.valueOf("#151A3B")); // Imposta il colore anche nel font di default
        }
    }

    public void operationsScreen() {
        img1 = new Texture("login_signup_pages/page_1_log_in_eng.png");
        img2 = new Texture("login_signup_pages/page_1_log_in_eng_error.png");
        img3 = new Texture("login_signup_pages/page_2_sign_up_eng.png");
        img4 = new Texture("login_signup_pages/page_2_sign_up_eng_error.png");
        img5 = new Texture("lobby_marketplace_ita.png");

        enteringNickname = true;
        Gdx.input.setInputProcessor(new MyInputProcessor()); // Riattiva l'InputProcessor
    }

    @Override
    public void show() {}

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
                screen.draw(img5, 0, 0);
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

    @Override
    public void dispose() {
        if (font != null) font.dispose();
        img1.dispose();
        img2.dispose();
        img3.dispose();
        img4.dispose();
    }
}
