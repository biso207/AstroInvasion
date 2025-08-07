/*
Astro Invasion - class GameOver -
Crea la schermata GameOver per tutte le modalità di gioco
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.GameMods;

// import librerie e codici
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import sorgente.*;
import sorgente.Entities.Spacecraft;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.GameMods.SpaceJourney.SpaceJourney;
import sorgente.Lobby.InputManager;
import sorgente.Lobby.LobbyManager;
import sorgente.Lobby.UIManager;
import sorgente.LogInSignUp.AuthAlgorithms;
import sorgente.UserData.CloudStorageManager;
import sorgente.UserData.DataUserManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    private BitmapFont font, fontBoldWhite25, fontBoldWhite50, fontBoldWhite60;

    // numero livello
    private final int numLevel = (int) DataUserManager.getProgress("level");

    // stato cambio stile mouse
    private boolean isBtnRHover=false, isBtnLHover=false;

    // immagini
    private Texture gameOverCG, gameOverSB, victorySB, levelCompleted, levelDefeat, rectSelectCard,
    guardianG1, guardianG2, guardianG3, guardianG4, btnHoverL, btnHoverR, bannerMissions,
        diffCG1, diffCG2, diffCG3, diffSB1, diffSB2, diffSB3;

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

    // oggetto navicella utente
    private final Spacecraft selectedSp;

    // stato completamento missione Missions
    private boolean completedMissions=false;
    // tempo per mostrare la notifica di completamento Missions
    private float elapsedTime = 0;

    // stato suono completamento Missions
    private boolean completedMissionsSoundPlayed=false;

    // boolean per le carte speciali e disattivazione
    private boolean goldHeart=false, shield=false, superLaser=false, doublePoints=false;

    // stato livello e vittoria/sconfitta partita
    private final boolean win, isLevel;

    // istanza del soundManager per riprodurre i suoni
    private final SoundManager soundManager;

    // crediti utente vinti in space battle
    private int creditsSB, pointsSB;

    private final String textToAdd; // testo da aggiungere per le navicelle con il bonus punti

    // difficoltà classic game e space battle
    private int diffCG = (int) DataUserManager.getProgress("diff_classic_game");
    private int diffSB = (int) DataUserManager.getProgress("diff_space_battle");

    // costruttore
    public GameOver(Main game, Spacecraft selectedSp, int mod, int[] stats, boolean win, boolean isLevel) {
        // set gioco
        this.game = game;

        // assegnazione variabili
        this.selectedSp = selectedSp;
        this.mod = mod;
        this.win = win;
        this.isLevel = isLevel;

        // azzeramento statistiche in caso di livello
        if (isLevel) Arrays.fill(stats, 0);

        // recupero progressi partita
        this.points = stats[0];
        this.credits = stats[1];
        this.aliensHit = stats[2];

        // istanza del soundManager per riprodurre i suoni
        soundManager = new SoundManager(InputManager.soundPercent);

        // testo da aggiungere per le navicelle con bonus punti
        textToAdd = selectedSp.getBonusPoints()>0 ? " (with " + selectedSp.getBonusPoints() + "% bonus)" : "";

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

        // suono vittoria/sconfitta
        if (win) soundManager.playWin();
        else soundManager.playDefeat();

        // salvataggio progresso livello completato
        if (isLevel && win) saveLevelProgress();

        // aggiornamento progressi di gioco. DA NON METTERE DENTRO METODI CHE VENGONO RIPETUTI
        switch (mod) {
            case 0:
                writeFileCG(); // salvataggio progressi classic game
                break;
            case 1:
                if (!isLevel) {
                    calcProgressSB(); // calcolo crediti vinti in space battle
                    checkCompletedMissions(); // controllo completamento task Missions
                }
                writeFileSpaceBattle(); // salvataggio progresso space battle
                break;
        }
    }

    // metodo per calcolare i progressi vinti in space battle
    public void calcProgressSB() {
        // calcolo crediti e punti vinti
        int diff = (int) DataUserManager.getProgress("diff_space_battle");
        int streak = (int) DataUserManager.getProgress("win_streak_SB");

        if (win) {
            // calcolo crediti vinti
            if (streak>=1) {
                switch (diff) {
                    case 1 -> creditsSB = (int) (10 * (Math.pow(1.2, streak)));
                    case 2 -> creditsSB = (int) (20 * (Math.pow(1.2, streak)));
                    case 3 -> creditsSB = (int) (30 * (Math.pow(1.2, streak)));
                }
            }
            else creditsSB = diff*10;

            // calcolo punti vinti
            switch (diff) {
                case 1 -> pointsSB = 1000;
                case 2 -> pointsSB = 2000;
                case 3 -> pointsSB = 3000;
            }

            // aggiunta bonus punti
            if (!isLevel) pointsSB = pointsSB+((pointsSB*(selectedSp.getBonusPoints()))/100); // aggiunta percentuale di bonus
        }
        else creditsSB = 0;
    }

    // metodo per controllare il completamento della task del 'road to glory' (Missions)
    public void checkCompletedMissions() {
        // recupero id missione
        int missionID = (int) DataUserManager.getProgress("mission_id");

        // recupero partite da vincere e vinte
        int toWinMissions = UIManager.Missions[missionID-1].calcNumObjMission();
        int winsMissions = (int) DataUserManager.getProgress("wins_SB_missions");

        // recupero crediti vinti e da vincere
        int creditsToWin = UIManager.Missions[missionID-1].calcNumObjMission();
        int creditsWon = (int) DataUserManager.getProgress("credits_missions");

        // recupero punti fatti fin'ora e da fare
        int pointsToWin = UIManager.Missions[missionID-1].calcNumObjMission();
        int pointsWon = (int) DataUserManager.getProgress("points_missions");


        // controllo completamento task Missions
        if (!(boolean) DataUserManager.getProgress("completed_mission") && win) {
            // controlli in base al tipo di missione
            if (missionID == 2) { // vittorie in space battle
                winsMissions++; // incremento numero partite vinte

                if (winsMissions==toWinMissions) {
                    DataUserManager.setProgress("completed_mission", true); // stato task completata
                    completedMissions = true; // cambio stato per l'icona di notifica
                }
                DataUserManager.setProgress("wins_SB_missions", winsMissions); // aggiornamento partite vinte
            }
            else if (missionID==3) { // punti raccolti in space battle e classic game
                if (pointsWon+pointsSB>=pointsToWin) {
                    DataUserManager.setProgress("completed_mission", true); // stato task completata
                    completedMissions = true; // cambio stato per l'icona di notifica
                }
            }
            else if (missionID == 4) { // crediti raccolti in space battle e classic game
                if (creditsWon+creditsSB>=creditsToWin) {
                    DataUserManager.setProgress("completed_mission", true); // stato task completata
                    completedMissions = true; // cambio stato per l'icona di notifica
                }
            }
        }
    }

    // ****************************** //
    // SALVATAGGIO PROGRESSI DI GIOCO //
    // ****************************** //

    // salvataggio progressi utente in classic game
    public void writeFileCG() {
        // recupero id missione
        int missionID = (int) DataUserManager.getProgress("mission_id");

        // salvataggio progressi solo se si sono colpiti degli alieni
        if (aliensHit>=1) {
            // salvataggio punti in remoto nel loro apposito campo
            try { CloudStorageManager.setUserPoints(AuthAlgorithms.nickname, (int) DataUserManager.getProgress("points") + points); }
            catch (Exception e) { System.out.println(e.getMessage()); }

            DataUserManager.setProgress("num_aliens_hit", (int) DataUserManager.getProgress("num_aliens_hit") + aliensHit);
            DataUserManager.setProgress("points", (int) DataUserManager.getProgress("points") + points);
            DataUserManager.setProgress("credits", (int) DataUserManager.getProgress("credits") + credits);
            DataUserManager.setProgress("total_credits", (int) DataUserManager.getProgress("total_credits") + credits);
        }

        // aggiornamento progresso task Missions
        switch (missionID) {
            case 1:
                if ((boolean) DataUserManager.getProgress("completed_mission")) DataUserManager.setProgress("num_aliens_hit_missions", UIManager.Missions[missionID-1].calcNumObjMission());
                else DataUserManager.setProgress("num_aliens_hit_missions", aliensHit + (int) DataUserManager.getProgress("num_aliens_hit_missions"));
                break;
            case 3:
                if ((boolean) DataUserManager.getProgress("completed_mission")) DataUserManager.setProgress("points_missions", UIManager.Missions[missionID-1].calcNumObjMission());
                else DataUserManager.setProgress("points_missions", points + (int) DataUserManager.getProgress("points_missions"));
                break;
            case 4:
                if ((boolean) DataUserManager.getProgress("completed_mission")) DataUserManager.setProgress("credits_missions", UIManager.Missions[missionID-1].calcNumObjMission());
                else DataUserManager.setProgress("credits_missions", credits + (int) DataUserManager.getProgress("credits_missions"));
                break;
        }
    }

    // salvataggio progressi utente in space battle
    public void writeFileSpaceBattle(){
        // recupero id missione
        int missionID = (int) DataUserManager.getProgress("mission_id");

        // salvataggio progressi
        DataUserManager.setProgress("credits", (int) DataUserManager.getProgress("credits")+creditsSB);
        DataUserManager.setProgress("total_credits", (int) DataUserManager.getProgress("total_credits")+creditsSB);
        DataUserManager.setProgress("points",  (int) DataUserManager.getProgress("points")+pointsSB);

        // salvataggio punti in remoto nel loro apposito campo
        try { CloudStorageManager.setUserPoints(AuthAlgorithms.nickname, (int) DataUserManager.getProgress("points") + pointsSB); }
        catch (Exception e) { System.out.println(e.getMessage()); }

        // aggiornamento progressi task Missions dei crediti
        if (missionID==4) { // crediti cg + sb
            DataUserManager.setProgress("credits_missions", creditsSB + (int) DataUserManager.getProgress("credits_missions"));
        }
        else if (missionID==3) { // punti cg + sb
            DataUserManager.setProgress("points_missions", pointsSB + (int) DataUserManager.getProgress("points_missions"));
        }
    }

    // salvataggio progressi livelli
    public void saveLevelProgress() {
        // recupero numero carte => per semplificare la scrittura nell'assegnazione premi
        int numSuperLaser = (int) DataUserManager.getProgress("num_super_laser");
        int numDoublePoints = (int) DataUserManager.getProgress("num_double_points");
        int numFragments = (int) DataUserManager.getProgress("alpha_fragments");

        // assegnazione premi
        switch (numLevel) {
            case 1 -> DataUserManager.setProgress("num_super_laser", numSuperLaser+1);
            case 5 -> DataUserManager.setProgress("num_double_points", numDoublePoints+1);
            case 10, 20, 30, 40 -> DataUserManager.setProgress("alpha_fragments", numFragments+1);

            case 11 -> DataUserManager.setProgress("num_super_laser", numSuperLaser+2);
            case 15 -> DataUserManager.setProgress("num_double_points", numDoublePoints+2);

            case 21 -> DataUserManager.setProgress("num_super_laser", numSuperLaser+3);
            case 25 -> DataUserManager.setProgress("num_double_points", numDoublePoints+3);

            case 31 -> DataUserManager.setProgress("num_super_laser", numSuperLaser+4);
            case 35 -> DataUserManager.setProgress("num_double_points", numDoublePoints+4);
        }

        // incremento livello raggiunto
        DataUserManager.setProgress("level", (int) DataUserManager.getProgress("level")+1);

        // setting stato livello successivo da acquistare
        if (numLevel<40) DataUserManager.setProgress("level_bought", false);
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

        // icona difficoltà classic game
        diffCG1 = new Texture("images/diff1_classicgame.png");
        diffCG2 = new Texture("images/diff2_classicgame.png");
        diffCG3 = new Texture("images/diff3_classicgame.png");
        // icona difficoltà space battle
        diffSB1 = new Texture("images/diff1_spacebattle.png");
        diffSB2 = new Texture("images/diff2_spacebattle.png");
        diffSB3 = new Texture("images/diff3_spacebattle.png");

        // notifica completamente Missions
        bannerMissions = new Texture("images/completed_Missions_notification_eng.png");

        // pulsanti hover
        btnHoverL = new Texture("images/btns_hover/hover_btn8.png");
        btnHoverR = new Texture("images/btns_hover/hover_btn9.png");
    }

    // caricamento e creazione font per le scritte
    @Override
    public void loadFont() {
        // dichiarazione font
        try {
            font = new BitmapFont(Gdx.files.internal("font/inter/bold_white_20.fnt")); // inter bold white 20
            fontBoldWhite25 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_25.fnt")); // inter bold white 25
            fontBoldWhite50 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_50.fnt")); // inter bold white 50
            fontBoldWhite60 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_60_1.fnt")); // inter bold white 60
        } catch (Exception e) {
            font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("FFFFFF")); // colore white
        }
    }

    // costruzione grafica
    public void graphic(float delta) {
        screen.begin();
        // switch delle modalità di gioco
        switch (mod) {
            case 0:
                // grafica sconfitta Classic Game
                if (!isLevel) {
                    // schermata base
                    screen.draw(gameOverCG, 0, 0);

                    // scritte progressi partita
                    fontBoldWhite25.draw(screen, formatter.format(points) + textToAdd, 190, 457);
                    fontBoldWhite25.draw(screen, formatter.format(credits), 200, 397);
                    fontBoldWhite25.draw(screen, formatter.format(aliensHit), 230, 337);

                    // numero carte speciali
                    font.draw(screen, formatter.format((int) DataUserManager.getProgress("num_gold_heart")), 650, 375);
                    font.draw(screen, formatter.format((int) DataUserManager.getProgress("num_shield")), 730, 375);
                    font.draw(screen, formatter.format((int) DataUserManager.getProgress("num_super_laser")), 809, 375);
                    font.draw(screen, formatter.format((int) DataUserManager.getProgress("num_double_points")), 890, 375);

                    // stampa rettangolo selezione carta
                    if (goldHeart) screen.draw(rectSelectCard, 642, 387);
                    if (shield) screen.draw(rectSelectCard, 722, 387);
                    if (superLaser) screen.draw(rectSelectCard, 802, 387);
                    if (doublePoints) screen.draw(rectSelectCard, 882, 387);

                    // difficoltà
                    switch ((int) DataUserManager.getProgress("diff_classic_game")) {
                        case 1:
                            screen.draw(diffCG1, 840 ,259);
                            break;
                        case 2:
                            screen.draw(diffCG2, 840 ,259);
                            break;
                        case 3:
                            screen.draw(diffCG3, 840 ,259);
                            break;
                    }
                }
                // grafica completamento/sconfitta livello di tipo Classic Game
                else graphicLevel();
                break;
            case 1:
                // grafica sconfitta/vittoria Space Battle
                if (!isLevel) {
                    // schermata base
                    screen.draw(win ? victorySB : gameOverSB, 0, 0);

                    // scritta progressi partita
                    fontBoldWhite25.draw(screen, formatter.format(pointsSB) + textToAdd, 190, 457);
                    fontBoldWhite25.draw(screen, formatter.format(creditsSB), 200, 397);

                    // numero carte speciali
                    font.draw(screen, formatter.format((int) DataUserManager.getProgress("num_gold_heart")), 650, 375);
                    font.draw(screen, formatter.format((int) DataUserManager.getProgress("num_super_laser")), 809, 375);

                    // stampa rettangolo selezione carta
                    if (goldHeart) screen.draw(rectSelectCard, 642, 387);
                    if (superLaser) screen.draw(rectSelectCard, 802, 387);

                    // difficoltà
                    switch ((int) DataUserManager.getProgress("diff_space_battle")) {
                        case 1:
                            screen.draw(diffSB1, 842 ,259);
                            break;
                        case 2:
                            screen.draw(diffSB2, 842 ,259);
                            break;
                        case 3:
                            screen.draw(diffSB3, 842 ,259);
                            break;
                    }

                    // suono completamento Missions
                    if (!completedMissionsSoundPlayed && completedMissions) {
                        soundManager.playCompletedMissions(); // riproduzione suono
                        completedMissionsSoundPlayed=true; // evita di riprodurre infinite volte il suono
                    }
                    // stampa messaggio completamento task Missions
                    if (completedMissions && elapsedTime <= 4f) { // 4f = 4 secondi
                        elapsedTime += delta; // conteggio tempo per mostrare la notifica
                        screen.draw(bannerMissions, 400, 515); // banner di notifica
                    }
                }
                // grafica sconfitta/vittoria livello di tipo Space Battle
                else graphicLevel();
                break;
        }

        if (!isLevel || !win) {
            // pulsanti hover
            if (isBtnLHover) screen.draw(btnHoverL, 277, 48);
            if (isBtnRHover) screen.draw(btnHoverR, 519, 48);

            // scritte pulsanti
            fontBoldWhite60.draw(screen, "YES", 320, 110);
            fontBoldWhite60.draw(screen, "NO", 577, 110);
        }
        screen.end();
    }

    // metodo per la grafica dei livelli
    public void graphicLevel() {
        // sfondo di base
        screen.draw(win ? levelCompleted : levelDefeat, 0, 0);

        if (!win) { // sconfitta
            // immagine nemico
            switch ((int) Math.ceil((double) numLevel / 10)) {
                case 1 -> screen.draw(guardianG1, 450, 298);
                case 2 -> screen.draw(guardianG2, 450, 298);
                case 3 -> screen.draw(guardianG3, 450, 298);
                case 4 -> screen.draw(guardianG4, 450, 298);
            }
        }
        else { // vittoria
            // pulsanti hover //
            if (isBtnLHover) screen.draw(btnHoverL, 398, 48);
            // testo pulsante //
            fontBoldWhite50.draw(screen, "CLOSE", 420, 105);

            // livello corrente già aggiornato al successivo
            int currentLevel = (int) DataUserManager.getProgress("level");
            currentLevel-=2; // valore corretto per l'indice delle liste

            // posizione immagine del premio
            int xImg = 500-(listImgReward.get(currentLevel).getWidth()/2);
            int yImg = 350-(listImgReward.get(currentLevel).getHeight()/2);
            screen.draw(listImgReward.get(currentLevel), xImg, yImg); // immagine premio

            // testi al centro della pagina con lunghezza variabile //
            // testo LIVELLO COMPLETATO
            GlyphLayout layout = new GlyphLayout();
            String txtCompletedLevel = "LEVEL " + (currentLevel+1) + " COMPLETED";
            // calcola la dimensione reale del testo con il font
            layout.setText(fontBoldWhite50, txtCompletedLevel);

            // posizione x variabile
            float x = (1000 - layout.width) / 2f; // 1000 è la larghezza totale dello schermo
            // disegna il testo centrato orizzontalmente
            fontBoldWhite50.draw(screen, layout, x, 190);

            // testo NOME PREMIO
            GlyphLayout layout2 = new GlyphLayout();
            String txtNamePrize = listTextReward.get(currentLevel);
            layout2.setText(font, txtNamePrize);
            float x2 = (1000 - layout2.width) / 2f;
            // disegna il testo centrato orizzontalmente
            font.draw(screen, layout2, x2, 280);
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
            this.dispose(); // rilascio risorse
        }

        // click ENTER => avvio nuova partita
        if (character == (Input.Keys.ENTER)) {
            switch (mod) {
                case 0:
                    game.setScreen(new ClassicGame(game, isLevel));
                    break;
                case 1:
                    game.setScreen(new SpaceBattle(game, isLevel));
                    break;
            }
            this.dispose(); // rilascio risorse
        }

        return true;
    }

    // metodo per controllare i click del mouse
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // riavvio partita o chiusura sessione
        if (win && isLevel) { // vittoria nei livelli
            // 'back to galaxies' button
            if ((screenX >= 390 && screenX <= 595) && (screenY >= 573 && screenY <= 650)) {
                SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click

                game.setScreen(new SpaceJourney(game, selectedSp, (int) Math.ceil((double) numLevel / 10)));
                this.dispose(); // rilascio risorse
            }
        }
        else { // vittoria-sconfitta space battle + sconfitta livelli
            // click YES => avvio nuova partita
            if ((screenX >= 272 && screenX <= 472) && (screenY >= 573 && screenY <= 650)) {
                SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
                switch (mod) {
                    case 0:
                        // stato carte speciali
                        InputManager.goldHeart = goldHeart;
                        InputManager.shield = shield;
                        InputManager.superLaser = superLaser;
                        InputManager.doublePoints = doublePoints;

                        game.setScreen(new ClassicGame(game, isLevel));
                        break;
                    case 1:
                        // stato carte speciali
                        InputManager.goldHeart = goldHeart;
                        InputManager.superLaser = superLaser;

                        game.setScreen(new SpaceBattle(game, isLevel));
                        break;
                }
                this.dispose(); // rilascio risorse
            }

            // click NO => ritorno alla Lobby o mappa livelli
            if ((screenX >= 513 && screenX <= 713) && (screenY >= 573 && screenY <= 650)) {
                SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click

                // livello corrente
                if (!isLevel) game.setScreen(new LobbyManager(game));
                else game.setScreen(new SpaceJourney(game, selectedSp, (int) Math.ceil((double) numLevel / 10)));
                this.dispose(); // rilascio risorse
            }
        }

        // cambio difficoltà NON nei livelli
        if (!isLevel) {
            // incremento classic game
            if (mod == 0 && (screenX >= 900 && screenX <= 920) && (screenY >= 411 && screenY <= 431) && diffCG < 3) {
                SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
                diffCG++;
                DataUserManager.setProgress("diff_classic_game", diffCG);
            }
            // decremento classic game
            if (mod == 0 && (screenX >= 789 && screenX <= 809) && (screenY >= 411 && screenY <= 431) && diffCG > 1) {
                SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
                diffCG--;
                DataUserManager.setProgress("diff_classic_game", diffCG);
            }

            // incremento space battle
            if (mod == 1 && (screenX >= 900 && screenX <= 920) && (screenY >= 411 && screenY <= 431) && diffSB < 3) {
                SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
                diffSB++;
                DataUserManager.setProgress("diff_space_battle", diffSB);
            }
            // decremento space battle
            if (mod == 1 && (screenX >= 789 && screenX <= 809) && (screenY >= 411 && screenY <= 431) && diffSB > 1) {
                SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
                diffSB--;
                DataUserManager.setProgress("diff_space_battle", diffSB);
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
        if (win && isLevel) { // vittoria nei livelli
            // pulsante CLAIM/CLOSE
            if ((screenX >= 390 && screenX <= 595) && (screenY >= 573 && screenY <= 650)) {
                isBtnLHover=true;
            }
        }
        else { // vittoria-sconfitta space battle + sconfitta livelli
            // YES restart
            if ((screenX >= 272 && screenX <= 472) && (screenY >= 573 && screenY <= 650)) {
                isBtnLHover = true;
            }

            // NO restart
            if ((screenX >= 513 && screenX <= 713) && (screenY >= 573 && screenY <= 650)) {
                isBtnRHover = true;
            }
        }
        return true;
    }

    // metodo per controllare la selezione delle carte speciali
    public void selectCard(int screenX, int screenY) {
        // selezione carta speciale
        boolean canDisableGoldHeart = (((int) DataUserManager.getProgress("num_gold_heart")) > 0) && !selectedSp.getName().equals("Alpha");
        boolean canDisableShield = (((int) DataUserManager.getProgress("num_shield")) > 0) && !selectedSp.getName().equals("Astrid");
        boolean canDisableSuperLaser = (((int) DataUserManager.getProgress("num_super_laser")) > 0) && !selectedSp.getName().equals("Rorik");
        boolean canDisableDoublePoints = (((int) DataUserManager.getProgress("num_double_points")) > 0) && !selectedSp.getName().equals("Drakar");

        // gold heart
        if (canDisableGoldHeart && (screenX >= 634 && screenX <= 704) && (screenY >= 242 && screenY <= 309)) {
            SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
            goldHeart = !goldHeart;
            if (canDisableShield) shield = false;
            if (canDisableSuperLaser) superLaser = false;
            if (canDisableDoublePoints) doublePoints = false;
        }

        // shield
        if (mod==0 && (screenX >= 714 && screenX <= 784) && (screenY >= 242 && screenY <= 309)) {
            SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
            shield = !shield;
            // disattivazione altre carte speciali
            if (canDisableGoldHeart) goldHeart = false;
            if (canDisableSuperLaser) superLaser = false;
            if (canDisableDoublePoints) doublePoints = false;
        }

        // super laser
        if (canDisableSuperLaser && (screenX >= 794 && screenX <= 864) && (screenY >= 242 && screenY <= 309)) {
            SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
            superLaser = !superLaser;
            if (canDisableGoldHeart) goldHeart = false;
            if (canDisableShield) shield = false;
            if (canDisableDoublePoints) doublePoints = false;
        }

        // double points
        if (mod==0 && canDisableDoublePoints && (screenX >= 874 && screenX <= 944) && (screenY >= 242 && screenY <= 309)) {
            SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
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

        delta = Math.min(delta, 1 / 30f);

        //handleInput();
        graphic(delta);
    }
    // spegnimento controllo input
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    // rilascio risorse
    @Override public void dispose() {
        // dispose fonts
        font.dispose();
        fontBoldWhite25.dispose();
        fontBoldWhite50.dispose();
        fontBoldWhite60.dispose();

        // dispose textures
        gameOverCG.dispose();
        gameOverSB.dispose();
        victorySB.dispose();
        levelCompleted.dispose();
        levelDefeat.dispose();
        guardianG1.dispose();
        guardianG2.dispose();
        guardianG3.dispose();
        guardianG4.dispose();
        rectSelectCard.dispose();
        bannerMissions.dispose();
        btnHoverL.dispose();
        btnHoverR.dispose();

        // dispose reward textures list
        for (Texture texture : listImgReward) {
            texture.dispose();
        }

        // dispose sound manager
        soundManager.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}

}
