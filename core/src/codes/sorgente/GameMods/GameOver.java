/*
Astro Invasion - class GameOver -
Crea la schermata GameOver per tutte le modalità di gioco
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.GameMods;

// import librerie e codici
import com.badlogic.gdx.graphics.Texture3D;
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
import sorgente.GameMods.SpaceJourney.Level;
import sorgente.Main;
import sorgente.ResourceLoader;
import sorgente.Lobby.InputManager;
import sorgente.Lobby.LobbyManager;
import sorgente.Lobby.UIManager;

import javax.xml.crypto.Data;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GameOver implements Screen, InputProcessor, ResourceLoader {
    // gioco principale
    private final Main game;
    // schermo
    private final SpriteBatch screen;

    // variabili per la stampa dei progressi partita
    private final int mod, points, credits, aliensHit;

    // font
    private BitmapFont font, font2, font3;

    // stato cambio stile mouse
    private boolean isBtnRHover=false, isBtnLHover=false;

    // immagini
    private Texture gameOverCG, gameOverSB, victorySB, levelCompleted, levelDefeat, rectSelectCard,
    guardianG1, guardianG2, guardianG3, guardianG4, btnHoverL, btnHoverR;
    // lista immagine premi
    private final List<Texture> listImgReward = new ArrayList<>();
    // lista testi premi
    private final List<String> listTextReward = List.of(
        "1x Super Laser",
        "Spacecraft Ares",
        "Avatar Cooper",
        "Spacecraft Andvari",
        "1x Double Points",
        "Spacecraft Siko",
        "Avatar Jessica",
        "Spacecraft Fenixia",
        "Avatar Scott",
        "1x Alpha Fragment",

        "2x Super Laser",
        "Spacecraft Selen",
        "Avatar Stephanie",
        "Spacecraft Centauro",
        "2x Double Points",
        "Spacecraft Zephyr",
        "Avatar Amin",
        "Spacecraft Malloc",
        "Avatar Samira",
        "1x Alpha Fragment",

        "3x Super Laser",
        "Spacecraft Orion",
        "Avatar Adbul",
        "Spacecraft Asgard",
        "3x Double Points",
        "Spacecraft Galahad",
        "Avatar Dorothy",
        "Spacecraft Seraphis",
        "Avatar Chen",
        "1x Alpha Fragment",

        "4x Super Laser",
        "Spacecraft Bewoulf",
        "Avatar Lin",
        "Spacecraft Scylla",
        "4x Double Points",
        "Spacecraft Keto",
        "Avatar Marcus",
        "Spacecraft Efron",
        "Avatar Sarah",
        "1x Alpha Fragment"
    );

    // formatter per la virgola delle migliaia in automatico converte l'intero in stringa
    private final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

    private final Spacecraft selectedSp;

    // boolean per le carte speciali e disattivazione
    private boolean goldHeart=false, shield=false, superLaser=false, doublePoints=false;

    // stato livello e vittoria/sconfitta partita
    private final boolean win, isLevel;
    // stato premio raccolto
    private boolean isRewardClaimed;

    /// TODO:
    /// aggiungere la raccolta del premio, con essa cambia il testo da CLAIM e CLOSE e il range cambia funzione
    /// da click per raccogliere il premio a click per tornare alla mappa dei livelli.
    /// una volta che si completa il livello, da qua, bisogna cambiare lo stato del livello corrente a COMPLETED e mettere
    /// il successivo a TO_BUY. Aggiungere la grafica corretta in base ai premi per la schermata di vittoria
    /// e assegnare il premio corretto. per assegnare il premio non bisogna fare nulla! basta aggiornare i progressi
    /// con il valore di "level" incremento di 1 e il gioco farà i suoi controlli dove deve e sarà tutto già disponibile.
    /// i premi mostrati qua sono solo da mostrare, così come l'immagine del guardiano e il testo di completamento del livello.
    /// impostare bene i testi dei pulsanti e i pulsanti "hover" quando ci passa sopra.
    /// aggiungere il controllo per riavviare il livello in cui si è perso direttamente da qua.

    // costruttore
    public GameOver(Main game, Spacecraft selectedSp, int mod, int[] stats, boolean win, boolean isLevel) {
        // set gioco
        this.game = game;

        // recupero progressi
        this.selectedSp = selectedSp;
        this.mod = mod;
        this.points = stats[0];
        this.credits = stats[1];
        this.aliensHit = stats[2];
        this.win = win;
        this.isLevel = isLevel;

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
                writeFileSpaceBattle();
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

    public void writeFileSpaceBattle(){
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
        // game over / vittoria modalità classiche
        gameOverCG = new Texture(Gdx.files.internal("secondary_screens/game_over_cg_eng.png"));
        gameOverSB = new Texture(Gdx.files.internal("secondary_screens/game_over_sb_eng.png"));
        victorySB = new Texture(Gdx.files.internal("secondary_screens/victory_sb_eng.png"));

        // livelli
        levelCompleted = new Texture(Gdx.files.internal("secondary_screens/completed_level.png"));
        levelDefeat = new Texture(Gdx.files.internal("secondary_screens/defeat_level.png"));

        // guardians
        guardianG1 = new Texture(Gdx.files.internal("secondary_screens/guardianG1.png"));
        guardianG2 = new Texture(Gdx.files.internal("secondary_screens/guardianG2.png"));
        guardianG3 = new Texture(Gdx.files.internal("secondary_screens/guardianG3.png"));
        guardianG4 = new Texture(Gdx.files.internal("secondary_screens/guardianG4.png"));

        // rettangolo selezione carta speciale
        rectSelectCard = new Texture(Gdx.files.internal("secondary_screens/active_card.png"));

        // caricamento immagine premi
        for (int i=1; i<=40; i++) {
            listImgReward.add(new Texture(Gdx.files.internal("images/levels_rewards/reward" + i + ".png")));
        }

        // pulsanti hover
        btnHoverL = new Texture("images/btns_hover/hover_btn10.png");
        btnHoverR = new Texture("images/btns_hover/hover_btn7.png");
    }

    // caricamento e creazione font per le scritte
    @Override
    public void loadFont() {
        // dichiarazione font
        try {
            font = new BitmapFont(Gdx.files.internal("font/inter/bold_white_20.fnt")); // inter bold white 20
            font2 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_25.fnt")); // inter bold white 25
            font3 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_30.fnt")); // inter bold white 30
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
                // grafica sconfitta Classic Game
                if (!isLevel) {
                    // schermata base
                    screen.draw(gameOverCG, 0, 0);

                    // scritte progressi partita
                    font2.draw(screen, formatter.format(points), 195, 457);
                    font2.draw(screen, formatter.format(credits), 205, 397);
                    font2.draw(screen, formatter.format(aliensHit), 235, 337);

                    // numero carte speciali
                    font.draw(screen, formatter.format((int) DataUserManager.getProgress("num_gold_heart")), 702, 375);
                    font.draw(screen, formatter.format((int) DataUserManager.getProgress("num_shield")), 837, 375);
                    font.draw(screen, formatter.format((int) DataUserManager.getProgress("num_super_laser")), 702, 262);
                    font.draw(screen, formatter.format((int) DataUserManager.getProgress("num_double_points")), 837, 262);

                    // stampa rettangolo selezione carta
                    if (goldHeart) screen.draw(rectSelectCard, 693, 388);
                    if (shield) screen.draw(rectSelectCard, 831, 388);
                    if (superLaser) screen.draw(rectSelectCard, 693, 275);
                    if (doublePoints) screen.draw(rectSelectCard, 831, 275);

                    // pulsanti hover
                    if (isBtnLHover) screen.draw(btnHoverL, 281, 417);
                    if (isBtnRHover) screen.draw(btnHoverR, 519, 417);
                }
                // grafica completamento/sconfitta livello di tipo Classic Game
                else graphicLevel();
                break;
            case 1:
                if (!isLevel) {
                    // schermata base
                    screen.draw(win ? victorySB : gameOverSB, 0, 0);

                    // scritte progressi partita
                    font2.draw(screen, formatter.format(points), 195, 457);
                    font2.draw(screen, formatter.format(credits), 205, 397);
                    font2.draw(screen, formatter.format(aliensHit), 235, 337);

                    // numero carte speciali
                    font.draw(screen, formatter.format((int) DataUserManager.getProgress("num_gold_heart")), 702, 365);
                    font.draw(screen, formatter.format((int) DataUserManager.getProgress("num_super_laser")), 702, 252);

                    // stampa rettangolo selezione carta
                    if (goldHeart) screen.draw(rectSelectCard, 693, 388);
                    if (superLaser) screen.draw(rectSelectCard, 831, 388);

                    // pulsanti hover
                    if (isBtnLHover) screen.draw(btnHoverL, 281, 417);
                    if (isBtnRHover) screen.draw(btnHoverR, 519, 417);
                }
                else graphicLevel();
                break;
        }
        screen.end();
    }

    // metodo per la grafica dei livelli
    public void graphicLevel() {
        // sfondo di base
        screen.draw(win ? levelCompleted : levelDefeat, 0, 0);

        if (!win) { // sconfitta
            // immagine nemico
            int numLevel = (int) DataUserManager.getProgress("level");
            switch ((int) Math.ceil((double) numLevel / 10)) {
                case 1 -> screen.draw(guardianG1, 430, 170);
                case 2 -> screen.draw(guardianG2, 775, 185);
                case 3 -> screen.draw(guardianG3, 800, 455);
                case 4 -> screen.draw(guardianG4, 300, 430);
            }

            // testi
            font2.draw(screen, "RESTART", 350, 200);
            font2.draw(screen, "CLOSE", 550, 200);
        }
        else { // vittoria
            // testo pulsante
            font2.draw(screen, isRewardClaimed ? "CLOSE" : "CLAIM", 450, 200);

            // stampa immagine premio + testo descrittivo
            screen.draw(listImgReward.get((int) DataUserManager.getProgress("level")), 300, 150);
            font.draw(screen, listTextReward.get((int) DataUserManager.getProgress("level")), 300, 200);

            // incremento livello
            DataUserManager.setProgress("level", (int) DataUserManager.getProgress("level")+1);
            // setting stato livello da acquistare
            DataUserManager.setProgress("level_bought", false);
        }
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
                    game.setScreen(new ClassicGame(game, selectedSp, isLevel));
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

                    game.setScreen(new ClassicGame(game, selectedSp, isLevel));
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

    // metodo per cambiare stile pulsanti al passaggio del mouse sopra di essi
    @Override public boolean mouseMoved(int screenX, int screenY) {
        isBtnRHover=isBtnLHover=false;
        // CAMBIO STILE PULSANTI
        /// TODO: cambiare i range x e y..
        // YES restart
        if ((screenX >= 272 && screenX <= 472) && (screenY >= 573 && screenY <= 650)) {
            isBtnLHover=true;
        }

        // NO restart
        if ((screenX >= 513 && screenX <= 713) && (screenY >= 573 && screenY <= 650)) {
            isBtnRHover=true;
        }
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
        if (canDisableGoldHeart && (screenX >= 685 && screenX <= 755) && (screenY >= 242 && screenY <= 309)) {
            goldHeart = !goldHeart;
            if (canDisableShield) shield = false;
            if (canDisableSuperLaser) superLaser = false;
            if (canDisableDoublePoints) doublePoints = false;
        }

        // shield
        if ((screenX >= 824 && screenX <= 892) && (screenY >= 242 && screenY <= 309)) {
            if (mod==0 && canDisableShield) shield = !shield; // selezione shield
            else if (mod==1 && canDisableSuperLaser) superLaser = !superLaser; // selezione super laser
            else return; // uscita

            // disattivazione altre carte speciali
            if (canDisableGoldHeart) goldHeart = false;
            if (canDisableSuperLaser) superLaser = false;
            if (canDisableDoublePoints) doublePoints = false;
        }

        // super laser
        if (mod==0 && canDisableSuperLaser && (screenX >= 685 && screenX <= 755) && (screenY >= 354 && screenY <= 422)) {
            superLaser = !superLaser;
            if (canDisableGoldHeart) goldHeart = false;
            if (canDisableShield) shield = false;
            if (canDisableDoublePoints) doublePoints = false;
        }

        // double points
        if (mod == 0 && canDisableDoublePoints && (screenX >= 824 && screenX <= 892) && (screenY >= 354 && screenY <= 422)) {
            doublePoints = !doublePoints;
            if (canDisableGoldHeart) goldHeart = false;
            if (canDisableShield) shield = false;
            if (canDisableSuperLaser) superLaser = false;
        }
    }

    // altri metodi
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
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
