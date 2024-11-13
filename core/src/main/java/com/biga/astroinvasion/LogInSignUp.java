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
    private StringBuilder nicknameInput;
    private StringBuilder passwordInput;
    private boolean enteringNickname;
    private boolean enteringPassword;
    public String nickname;
    private int state = 0; // 0 = LogIn, 1 = errore LogIn, 2 = SignUp, 3 = errore SignUp
    private final Main game;

    public LogInSignUp(Main game) {
        this.game = game;
        this.screen = game.screen;
        this.enteringNickname = true;
        this.enteringPassword = true;

        // Carica il font
        loadFont();

        // initi stringhe
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
                if (character == '\n' || character == '\r') { // ENTER per terminare il nickname
                    enteringNickname = false;
                } else if (character == '\b' && nicknameInput.length() > 0) { // BACKSPACE per cancellare l'ultimo carattere
                    nicknameInput.deleteCharAt(nicknameInput.length() - 1);
                } else if (character >= 32 && character < 127 && nicknameInput.length()<=20) { // Controllo per caratteri validi
                    nicknameInput.append(character);
                }
            } else if (enteringPassword) {
                if (character == '\n' || character == '\r') { // ENTER per terminare la password
                    enteringPassword = false;
                } else if (character == '\b' && passwordInput.length() > 0) { // BACKSPACE per cancellare l'ultimo carattere
                    passwordInput.deleteCharAt(passwordInput.length() - 1);
                } else if (character >= 32 && character < 127 && passwordInput.length()<=20) { // Controllo per caratteri validi
                    passwordInput.append(character);
                }
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
        File generalFolder = new File("data/" + nicknameInput);
        File dataFolder = new File("data/" + nicknameInput + "/data_user");

        if (!generalFolder.exists()) {
            generalFolder.mkdir();
            dataFolder.mkdir();

            try {
                FileWriter writeNick = new FileWriter("data/" + nicknameInput + "/data_user/nickname.txt");
                writeNick.write(String.valueOf(nicknameInput));
                writeNick.close();

                FileWriter writePass = new FileWriter("data/" + nicknameInput + "/data_user/password.txt");
                writePass.write(String.valueOf(passwordInput));
                writePass.close();

                nickname = String.valueOf(nicknameInput);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            state = 3;
        }
    }

    public void LogInAlg() {
        try {
            File readPass = new File("data/" + nicknameInput + "/data_user/password.txt");
            String readPasswordInput = new Scanner(readPass).nextLine();

            if (!readPasswordInput.equals(String.valueOf(passwordInput))) {
                state = 1;
            }
        } catch (IOException e) {
            state = 1;
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
            font.setColor(Color.valueOf("#151A3B")); // Blu nostro
        }
    }

    public void operationsScreen() {
        img1 = new Texture("login_signup_pages/page_1_log_in_eng.png");
        img2 = new Texture("login_signup_pages/page_1_log_in_eng_error.png");
        img3 = new Texture("login_signup_pages/page_2_sign_up_eng.png");
        img4 = new Texture("login_signup_pages/page_2_sign_up_eng_error.png");

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
