/*
Astro Invasion - class GameOver -
Crea la schermata GameOver per tutte le modalità di gioco
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.GameMods;

// import librerie e codici
import sorgente.Entities.Spacecraft;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.DataUserManager;
import sorgente.Main;
import sorgente.ResourceLoader;
import sorgente.Lobby.InputManager;
import sorgente.Lobby.LobbyManager;
import sorgente.Lobby.UIManager;

import java.text.NumberFormat;
import java.util.Locale;

public class GameOver implements Screen, InputProcessor, ResourceLoader {
    // gioco principale
    private final Main game;
    // schermo
    private final SpriteBatch screen;

    // variabili per la stampa dei progressi partita
    private final int mod, points, credits, aliensHit;

    // font
    private BitmapFont font, font2;
    // immagini
    private Texture gameOver0, rectSelectCard;
    // formatter per la virgola delle migliaia in automatico converte l'intero in stringa
    NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

    private final Spacecraft selectedSp;

    // boolean per le carte speciali e disattivazione
    public boolean goldHeart=false, shield=false, superLaser=false, doublePoints=false;

    // costruttore
    public GameOver(Main game, Spacecraft selectedSp, int mod, int points, int credits, int aliensHit) {
        // set gioco
        this.game = game;

        // recupero progressi
        this.selectedSp = selectedSp;
        this.mod = mod;
        this.points = points;
        this.credits = credits;
        this.aliensHit = aliensHit;

        // attivazione carte delle navicelle premium
        if (selectedSp.getName().equals("Alpha")) goldHeart = true;
        if (selectedSp.getName().equals("Astrid")) shield = true;
        if (selectedSp.getName().equals("Rorik")) superLaser = true;
        if (selectedSp.getName().equals("Drakar")) doublePoints = true;

        // init screen
        this.screen = game.screen;

        // caricamento font
        loadFont();

        // caricamento immagini di base
        loadImages();

        // aggiornamento progressi di gioco. DA NON METTERE DENTRO METODI CHE VENGONO RIPETUTI
        switch (mod) {
            case 0:
                writeFileCG();
                break;
            case 1:
                System.out.println("salvataggio space battle");
                break;
        }
    }

    // salvataggio progressi utente
    public void writeFileCG() {
        // recupero id missione
        int missionID = (int) DataUserManager.getProgress("mission_id");

        // salvataggio progressi
        DataUserManager.setProgress("num_aliens_hit", (int) DataUserManager.getProgress("num_aliens_hit")+aliensHit);
        DataUserManager.setProgress("points", (int) DataUserManager.getProgress("points")+points);
        DataUserManager.setProgress("credits", (int) DataUserManager.getProgress("credits")+credits);
        DataUserManager.setProgress("total_credits", (int) DataUserManager.getProgress("total_credits")+credits);

        // aggiornamento progresso task RTG
        switch (missionID) {
            case 1:
                if ((boolean) DataUserManager.getProgress("completed_RTG")) DataUserManager.setProgress("num_aliens_hit_RTG", UIManager.RTGs[missionID-1].calcNumObjMission());
                else DataUserManager.setProgress("num_aliens_hit_RTG", aliensHit + (int) DataUserManager.getProgress("num_aliens_hit_RTG"));
                break;
            case 3:
                if ((boolean) DataUserManager.getProgress("completed_RTG")) DataUserManager.setProgress("points_RTG", UIManager.RTGs[missionID-1].calcNumObjMission());
                else DataUserManager.setProgress("points_RTG", points + (int) DataUserManager.getProgress("points_RTG"));
                break;
            case 4:
                if ((boolean) DataUserManager.getProgress("completed_RTG")) DataUserManager.setProgress("credits_RTG", UIManager.RTGs[missionID-1].calcNumObjMission());
                else DataUserManager.setProgress("credits_RTG", credits + (int) DataUserManager.getProgress("credits_RTG"));
                break;
        }
    }

    // ************************************** //
    // GESTIONE GRAFICA + CARICAMENTO RISORSE //
    // ************************************** //

    // caricamento schermate di base
    @Override
    public void loadImages() {
        gameOver0 = new Texture(Gdx.files.internal("secondary_screens/game_over_cg_eng.png"));
        rectSelectCard = new Texture(Gdx.files.internal("secondary_screens/active_card.png"));
    }

    // caricamento e creazione font per le scritte
    @Override
    public void loadFont() {
        // dichiarazione font
        try {
            font = new BitmapFont(Gdx.files.internal("font/inter/bold_white_20.fnt")); // inter bold white 20
            font2 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_25.fnt")); // inter bold white 25
        } catch (Exception e) {
            font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("FFFFFF")); // colore white
        }
    }

    // costruzione grafica
    public void graphic() {
        screen.begin();
        // switch delle modalità di gioco
        switch (mod) {
            case 0:
                // schermata base
                screen.draw(gameOver0, 0, 0);

                // scritte progressi partita
                font2.draw(screen, formatter.format(points), 195, 457);
                font2.draw(screen, formatter.format(credits), 205, 397);
                font2.draw(screen, formatter.format(aliensHit), 235, 337);

                // numero carte speciali
                font.draw(screen, formatter.format((int)DataUserManager.getProgress("num_gold_heart")), 702, 385);
                font.draw(screen, formatter.format((int)DataUserManager.getProgress("num_shield")), 837, 385);
                font.draw(screen, formatter.format((int)DataUserManager.getProgress("num_super_laser")), 702, 272);
                font.draw(screen, formatter.format((int)DataUserManager.getProgress("num_double_points")), 837, 272);

                // stampa rettangolo selezione carta
                if (goldHeart) screen.draw(rectSelectCard, 693, 398);
                if (shield) screen.draw(rectSelectCard, 831, 398);
                if (superLaser) screen.draw(rectSelectCard, 693, 285);
                if (doublePoints) screen.draw(rectSelectCard, 831, 285);

                break;
            case 1:
                System.out.println("space battle");
                break;
        }
        screen.end();
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    // metodo per rilevare il click da tastiera
    @Override public boolean keyDown(int character) {
        // click ESC => ritorno alla lobby
        if (character == Input.Keys.ESCAPE) {
            game.setScreen(new LobbyManager(game));
        }

        // click ENTER => avvio nuova partita
        if (character == (Input.Keys.ENTER)) {
            switch (mod) {
                case 0:
                    game.setScreen(new ClassicGame(game, selectedSp));
                    break;
                case 1:
                    game.setScreen(new SpaceBattle(game, selectedSp));
                    break;
            }
        }

        return true;
    }
    // metodo per controllare i click del mouse
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // click NO => ritorno alla Lobby
        if ((screenX >= 513 && screenX <= 713) && (screenY >= 573 && screenY <= 650)) {
            game.setScreen(new LobbyManager(game));
        }

        // click YES => avvio nuova partita
        if ((screenX >= 272 && screenX <= 472) && (screenY >= 573 && screenY <= 650)) {
            switch (mod) {
                case 0:
                    // stato carte speciali
                    InputManager.goldHeart = goldHeart;
                    InputManager.shield = shield;
                    InputManager.superLaser = superLaser;
                    InputManager.doublePoints = doublePoints;

                    game.setScreen(new ClassicGame(game, selectedSp));
                    break;
                case 1:
                    game.setScreen(new SpaceBattle(game, selectedSp));
                    break;
            }
        }

        // selezione carte speciali
        selectCard(screenX, screenY);
        return true;
    }

    // metodo per controllare la selezione delle carte speciali
    public void selectCard(int screenX, int screenY) {
        // selezione carta speciale
        boolean canDisableGoldHeart = (int) DataUserManager.getProgress("num_gold_heart") > 0 && !selectedSp.getName().equals("Alpha");
        boolean canDisableShield = (int) DataUserManager.getProgress("num_shield") > 0 && !selectedSp.getName().equals("Astrid");
        boolean canDisableSuperLaser = (int) DataUserManager.getProgress("num_super_laser") > 0 && !selectedSp.getName().equals("Rorik");
        boolean canDisableDoublePoints = (int) DataUserManager.getProgress("num_double_points") > 0 && !selectedSp.getName().equals("Drakar");

        // gold heart
        if (canDisableGoldHeart && screenX >= 685 && screenX <= 755 && screenY >= 232 && screenY <= 299) {
            goldHeart = !goldHeart;
            if (canDisableShield) shield = false;
            if (canDisableSuperLaser) superLaser = false;
            if (canDisableDoublePoints) doublePoints = false;
        }

        // shield
        if (canDisableShield && screenX >= 824 && screenX <= 892 && screenY >= 232 && screenY <= 299) {
            shield = !shield;
            if (canDisableGoldHeart) goldHeart = false;
            if (canDisableSuperLaser) superLaser = false;
            if (canDisableDoublePoints) doublePoints = false;
        }

        // super laser
        if (canDisableSuperLaser && screenX >= 685 && screenX <= 755 && screenY >= 344 && screenY <= 412) {
            superLaser = !superLaser;
            if (canDisableGoldHeart) goldHeart = false;
            if (canDisableShield) shield = false;
            if (canDisableDoublePoints) doublePoints = false;
        }

        // double points
        if (canDisableDoublePoints && screenX >= 824 && screenX <= 892 && screenY >= 344 && screenY <= 412) {
            doublePoints = !doublePoints;
            if (canDisableGoldHeart) goldHeart = false;
            if (canDisableShield) shield = false;
            if (canDisableSuperLaser) superLaser = false;
        }
    }

    // altri metodi
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

    // ****************************** //
    // METODI DELL'INTERFACCIA Screen //
    // ****************************** //
    // aggiornamento risorse grafiche
    @Override public void render(float delta) {
        // attivazione controllo input
        Gdx.input.setInputProcessor(this);

        //handleInput();
        graphic();
    }
    // spegnimento controllo input
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    // rilascio risorse
    @Override public void dispose() {
        screen.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}

}
