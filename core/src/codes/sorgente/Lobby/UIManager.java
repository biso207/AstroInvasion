/*
Astro Invasion - class AuthManagerUI -
Gestisce le grafiche delle schermate della lobby
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.Lobby;

// import codici e librerie
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import sorgente.Entities.Avatar;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.DataUserManager;
import sorgente.LogInSignUp.AuthAlgorithms;
import sorgente.Missions.CheckMissions;
import sorgente.Missions.RTG;
import sorgente.ResourceLoader;
import sorgente.Entities.Spacecraft;

import javax.xml.crypto.Data;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class UIManager implements ResourceLoader {
    // dichiarazione icone difficoltà, spunta completamento, premi RTG
    private Texture tickImg, diffCG1, diffCG2, diffCG3, diffSB1, diffSB2, diffSB3, rectSelectCard,
        claimPrize, progressRTG, notifyCompletedRTG, txtSoldOut, soundOn, soundOff, musicOn, musicOff, selectedSetting,
        volumeState;

    // textureRegione per definire l'area di completamento della task corrente in RTG
    private TextureRegion progressBarRegion;

    // texture per gli avatar
    private Texture[] avatars;
    private Texture[] avatarsCovered;
    private Texture selectedAvatar;

    // premi RTG
    private Texture[] RTGPrizes;
    public static RTG[] RTGs;

    // immagini in sovra impressione
    private Texture closeGame, softInfos, warning, confirmBuy, settings;
    // immagine navicella + immagine space battle bloccato
    private Texture spImg, infoBanner;

    // pulsanti + scuri al passaggio del mouse
    private Texture[] buttonsOver;

    private BitmapFont fontBlue15, fontBlue20, fontMediumBlue15, fontMediumBlue20, fontBoldBlue20, fontBoldDarkRed25, fontWhite20, fontMediumWhite20, fontBoldWhite15, fontBoldWhite20, fontBoldWhite25, fontItalicBoldWhite15;

    // hashmap per le diverse texture
    private final HashMap<Integer, Texture> mapLobby; // schermate lobby
    private final HashMap<Integer, Texture> mapAvatarsImgs; // immagini avatar
    private final HashMap<Integer, Avatar> mapAvatars; // oggetti avatar
    private final HashMap<Integer, Spacecraft> mapSpacecrafts; // oggetti navicella

    // arraylist delle pagine secondarie
    private final Set<Integer> listSecondPages = Set.of(10, 11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22, 23, 24);

    // creazione oggetto navicella per il package Lobby
    protected static Spacecraft selectedSp;

    // formatter per la virgola delle migliaia !in automatico converte l'intero in stringa
    private NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

    // mouse
    protected static Pixmap mouse, mouseOver; // immagini
    protected static Cursor cursor, cursorOver; // oggetto cursore

    // costruttore
    public UIManager() {
        this.mapLobby = new HashMap<>();
        this.mapAvatarsImgs = new HashMap<>();
        this.mapAvatars = new HashMap<>();
        this.mapSpacecrafts = new HashMap<>();
        this.buttonsOver = new Texture[10];

        // mouse
        mouse = new Pixmap(Gdx.files.internal("images/cursor.png"));
        mouseOver = new Pixmap(Gdx.files.internal("images/mouse_over.png"));

        cursor = Gdx.graphics.newCursor(mouse, 0, 0);
        cursorOver = Gdx.graphics.newCursor(mouseOver, 0, 0);


        // caricamento navicella utente
        selectedSp = createSpacecrafts(); // navicelle e recupero navicella utente

        // caricamento risorse
        createMissions();
        loadImages(); // immagini lobby
        loadFont(); // font
        createAvatars(); // creazione oggetti avatar

        // il caricamento delle navicelle avviene in LobbyManager così da passargli la navicella selezionata
    }

    // ******************* //
    // CARICAMENTO RISORSE //
    // ******************* //

    // metodo per caricare e creare i font
    @Override
    public void loadFont() {
        // dichiarazione font
        try {
            // blue
            fontBlue15 = new BitmapFont(Gdx.files.internal("font/inter/regular_blue_15.fnt")); // inter-regular blue 15
            fontBlue20 = new BitmapFont(Gdx.files.internal("font/inter/regular_blue_20.fnt")); // inter-regular blue 20
            fontMediumBlue15 = new BitmapFont(Gdx.files.internal("font/inter/medium_blue_15.fnt")); // inter-medium blue 15
            fontMediumBlue20 = new BitmapFont(Gdx.files.internal("font/inter/medium_blue_20.fnt")); // inter-medium blue 20
            fontBoldBlue20 = new BitmapFont(Gdx.files.internal("font/inter/bold_blue_20.fnt")); // inter-regular blue 20
            // red
            fontBoldDarkRed25 = new BitmapFont(Gdx.files.internal("font/inter/bold_darkRed_25.fnt")); // inter-regular blue 20
            // white
            fontWhite20 = new BitmapFont(Gdx.files.internal("font/inter/regular_white_20.fnt")); // inter-regular white 20
            fontMediumWhite20 = new BitmapFont(Gdx.files.internal("font/inter/medium_white_20.fnt")); // inter-medium white 20
            fontBoldWhite15 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_15.fnt")); // inter-bold white 15
            fontBoldWhite20 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_20.fnt")); // inter-bold white 20
            fontBoldWhite25 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_25.fnt")); // inter-bold white 25
            fontItalicBoldWhite15 = new BitmapFont(Gdx.files.internal("font/inter/bold_italic_white_15.fnt")); // inter-italic-bold white 15
            //fontRed20 = new BitmapFont(Gdx.files.internal("font/inter/regular_red_20.fnt")); // inter regular red 20
        } catch (Exception e) {
            // dichiarazione font
            BitmapFont font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("#151A3B")); // colore blu
        }
    }

    // metodo per caricare le immagini della Lobby
    @Override
    public void loadImages() {
        // popolamento mappa lobby
        for (int i = 0; i < 24; i++) mapLobby.put(i, new Texture("lobby_screens/lobby (" + i + ").png"));
        // popolamento mappa avatar
        for (int i = 0; i <= 19; i++) mapAvatarsImgs.put(i, new Texture("images/avatars/av (" + i + ").png"));

        // "pulsante" raccolta premio RTG
        Texture img_special = new Texture("images/rect_claim_reward_eng.png");
        mapLobby.put(35, img_special);

        // immagini in sovra impressione
        closeGame = new Texture("secondary_screens/lobby_close_game_eng.png");
        softInfos = new Texture("secondary_screens/lobby_software_info_eng.png");
        warning = new Texture("secondary_screens/lobby_warning_eng.png");
        confirmBuy = new Texture("secondary_screens/lobby_confirm_buy_eng.png");
        settings = new Texture("secondary_screens/lobby_settings_eng.png");

        // testo "SOLD OUT" per il marketplace
        txtSoldOut = new Texture("images/sold_item_txt.png");

        // immagine navicella
        spImg = new Texture(selectedSp.getPathImg());

        // testo informativo space battle bloccato
        infoBanner = new Texture("images/warning_txt_space_battle.png");

        // icone suoni
        soundOn = new Texture("images/icoSoundOn.png");
        soundOff = new Texture("images/icoSoundOff.png");
        musicOn = new Texture("images/icoMusicOn.png");
        musicOff = new Texture("images/icoMusicOff.png");
        // volume suoni
        volumeState = new Texture("images/barAudioValue.png");
        // selezione impostazione
        selectedSetting = new Texture("images/selected_setting.png");

        // pulsanti "hover"
        for (int i = 0; i < 10; i++) {
            buttonsOver[i] = new Texture("images/btns_hover/hover_btn" + (i+1) + ".png");
        }

        // icona difficoltà classic game
        diffCG1 = new Texture("images/diff1_classicgame.png");
        diffCG2 = new Texture("images/diff2_classicgame.png");
        diffCG3 = new Texture("images/diff3_classicgame.png");
        // icona difficoltà space battle
        diffSB1 = new Texture("images/diff1_spacebattle.png");
        diffSB2 = new Texture("images/diff2_spacebattle.png");
        diffSB3 = new Texture("images/diff3_spacebattle.png");

        // immagini dei premi del RTG
        RTGPrizes = new Texture[4];
        // load texture
        for (int i=0; i<4; i++) {
            RTGPrizes[i] = new Texture("images/cards/mini_card" + (i+1) + ".png");
        }

        // immagine per raccogliere il premio RTG
        claimPrize = new Texture("images/rect_claim_reward_eng.png");
        // icona di notifica del completamento della missione RTG
        notifyCompletedRTG = new Texture("images/notify_completed_RTG.png");

        // immagine spunta per completamento missione o selezione oggetti
        tickImg = new Texture("images/tick2.png");
        // rettangolo selezione carta
        rectSelectCard = new Texture(Gdx.files.internal("secondary_screens/active_card.png"));

        // immagine di progresso missione RTG
        progressRTG = new Texture("images/progress.png");
        progressBarRegion = new TextureRegion(progressRTG);

        // array per gli avatar
        avatars = new Texture[20];
        avatarsCovered = new Texture[20];

        // caricamento avatar base
        for (int i = 0; i < 20; i++) {
            avatars[i] = new Texture("images/avatars/av (" + i + ") mini.png");
        }
        // caricamento avatar nascosti
        for (int i=0; i<4; i++) {
            avatarsCovered[i] = null; // null per i primi 4 avatar
        }
        for (int i=4; i<=19; i++ ) {
            avatarsCovered[i] = new Texture("images/avatars/av (" + i + ") mini covered.png");
        }

        // quadrato avatar selezionato
        selectedAvatar = new Texture("images/avatars/selected_avatar.png");
    }

    // **************** //
    // GESTIONE GRAFICA //
    // **************** //
    // metodo per creare gli avatar
    public void createAvatars() {
        mapAvatars.put(0, new Avatar("Cap. Omega", null));
        mapAvatars.put(1, new Avatar("Cap. Idra", null));
        mapAvatars.put(2, new Avatar("Cap. Pegaso", null));
        mapAvatars.put(3, new Avatar("Cap. Woka", null));
        mapAvatars.put(4, new Avatar("Cooper", "Complete Level 12"));
        mapAvatars.put(5, new Avatar("Jessica", "Complete Level 14"));
        mapAvatars.put(6, new Avatar("Scot", "Complete Level 16"));
        mapAvatars.put(7, new Avatar("Stephanie", "Complete Level 18"));
        mapAvatars.put(8, new Avatar("Amin", "Complete Level 21"));
        mapAvatars.put(9, new Avatar("Samira", "Complete Level 22"));
        mapAvatars.put(10, new Avatar("Abdul", "Complete Level 25"));
        mapAvatars.put(11, new Avatar("Dorothy", "Complete Level 28"));
        mapAvatars.put(12, new Avatar("Chen", "Complete Level 34"));
        mapAvatars.put(13, new Avatar("Lin", "Complete Level 36"));
        mapAvatars.put(14, new Avatar("Marcus", "Complete Level 38"));
        mapAvatars.put(15, new Avatar("Sarah", "Complete Level 40"));
        mapAvatars.put(16, new Avatar("Matthew", "Claim 10K Credits"));
        mapAvatars.put(17, new Avatar("Kiara", "Claim 50K Credits"));
        mapAvatars.put(18, new Avatar("Luke", "Reach 3M Points"));
        mapAvatars.put(19, new Avatar("Emma", "Reach 5M Points"));
    }

    // metodo per creare le navicelle
    public Spacecraft createSpacecrafts() {
        // nomi delle navicelle
        String[] names = {"Omega", "Idra", "Pegaso", "Woka", "Beowulf", "Andvari", "Siko", "Fenixia", "Ares", "Asgard",
            "Galahad", "Malloc", "Orion", "Centauro", "Zephyr", "Phoenix", "Selen", "Scylla", "Keto", "Efron",
            "Drakar", "Rorik", "Astrid", "Alpha"};
        // percorsi immagine navicelle
        String[] imagePaths = {"images/spacecrafts/_omega.png", "images/spacecrafts/_idra.png", "images/spacecrafts/_pegaso.png",
            "images/spacecrafts/_woka.png", "images/spacecrafts/_beowulf_basic.png", "images/spacecrafts/_andvari_basic.png",
            "images/spacecrafts/_siko_basic.png", "images/spacecrafts/_fenixia_basic.png", "images/spacecrafts/_ares_basic.png",
            "images/spacecrafts/_asgard_basic.png", "images/spacecrafts/_galahad_basic.png", "images/spacecrafts/_malloc_basic.png",
            "images/spacecrafts/_orion_basic.png", "images/spacecrafts/_centauro_basic.png", "images/spacecrafts/_zephyr_basic.png",
            "images/spacecrafts/_phoenix_basic.png", "images/spacecrafts/_selen_basic.png", "images/spacecrafts/_scylla_basic.png",
            "images/spacecrafts/_keto_basic.png", "images/spacecrafts/_efron_basic.png", "images/spacecrafts/_drakar.png",
            "images/spacecrafts/_rorik.png", "images/spacecrafts/_astrid.png", "images/spacecrafts/_alpha.png"};
        // percorsi immagine laser
        String[] laserPaths = {"images/lasers/laser_omega.png", "images/lasers/laser_idra.png", "images/lasers/laser_pegaso.png",
            "images/lasers/laser_woka.png", "images/lasers/laser_beowulf.png", "images/lasers/laser_andvari.png",
            "images/lasers/laser_siko.png", "images/lasers/laser_fenixia.png", "images/lasers/laser_ares.png",
            "images/lasers/laser_asgard.png", "images/lasers/laser_galahad.png", "images/lasers/laser_malloc.png",
            "images/lasers/laser_orion.png", "images/lasers/laser_centauro.png", "images/lasers/laser_centauro.png",
            "images/lasers/laser_phoenix.png", "images/lasers/laser_centauro.png", "images/lasers/laser_centauro.png",
            "images/lasers/laser_centauro.png", "images/lasers/laser_centauro.png", "images/lasers/laser_centauro.png",
            "images/lasers/laser_centauro.png", "images/lasers/laser_centauro.png", "images/lasers/laser_alpha.png"};
        // potenze delle navicelle
        int[][] attributes = {
            {0, 1, 0}, {5, 0, 0}, {1, 0, 0}, {0, 1, 0}, {0, 2, 0}, {10, 0, 0}, {0, 0, 2}, {0, 0, 3}, {0, 3, 0},
            {15, 0, 0}, {0, 1, 1}, {10, 2, 0}, {0, 2, 1}, {20, 0, 0}, {0, 4, 1}, {0, 1, 2}, {0, 2, 2}, {30, 0, 0},
            {0, 1, 4}, {10, 0, 2}, {0, 5, 5}, {50, 5, 0}, {50, 0, 5}, {50, 5, 5}
        };

        // popolamento della mappa navicelle
        for (int i = 0; i < 24; i++) {
            mapSpacecrafts.put(i, new Spacecraft(names[i], imagePaths[i], new Texture(laserPaths[i]), attributes[i][0], attributes[i][1], attributes[i][2]));
        }

        // recupero navicella utente
        Object spacecraft = DataUserManager.getProgress("spacecraft");
        selectedSp = mapSpacecrafts.get((int) spacecraft); // navicella utente

        return selectedSp;
    }

    public void createMissions() {
        RTGs = new RTG[4];

        // creazione oggetti
        RTG RTG0 = new RTG("Hit", 100, "aliens in Classic Game matches.", "1 Gold Heart", "images/cards/cart1_gold_heart_eng.png");
        RTG RTG1 = new RTG("Win", 1, "Space Battle matches.", "1 Shield", "images/cards/cart2_shield_eng.png");
        RTG RTG2 = new RTG("Earn", 2000, "points through\nthe Classic Game.", "100 Credits", "images/cards/card_100_coins.png");
        RTG RTG3 = new RTG("Earn", 100, "credits through Space Battle\nand/or Classic Game matches.", "1 Super Laser", "images/cards/cart3_super_laser_eng.png");

        RTGs[0] = RTG0;
        RTGs[1] = RTG1;
        RTGs[2] = RTG2;
        RTGs[3] = RTG3;
    }

    // metodo per disegnare la barra di progresso della task corrente del RTG
    public void drawRTGPage(SpriteBatch screen, int missionID) {
        // immagine premio //
        screen.draw(RTGPrizes[missionID-1], 660, 100);

        // pulsante raccolta premio //
        if ((boolean) DataUserManager.getProgress("completed_RTG")) screen.draw(claimPrize, 767, 100);

        int progress, maxProgress;

        progress = switch (missionID) {
            case 1 -> (int) DataUserManager.getProgress("num_aliens_hit_RTG");
            case 2 -> (int) DataUserManager.getProgress("won_SB_RTG");
            case 3 -> (int) DataUserManager.getProgress("points_RTG");
            case 4 -> (int) DataUserManager.getProgress("credits_RTG");
            default -> 0;
        };

        // progresso totale da compiere
        maxProgress = RTGs[missionID-1].calcNumObjMission();

        // lunghezza barra riempita
        float filledWidth = (progress / (float) maxProgress) * 380;

        if (filledWidth > 0.0) {
            screen.draw(progressBarRegion, 519, 276, filledWidth, 20);
        }

        // progresso road to glory => percentuale o stampa progresso?
        int percentage = (int) Math.ceil((progress / (float) maxProgress)*100);
        //fontBoldWhite20.draw(screen, percentage+"%", 525, 294);
        fontBoldWhite20.draw(screen, formatter.format(progress) + "/" + formatter.format(maxProgress), 525, 294);

        // button start hover
        if (InputManager.isBtnClaimHover) screen.draw(buttonsOver[3], 767, 100);
    }

    // metodo per disegnare la pagina delle impostazioni
    public void drawSettingsPage(SpriteBatch screen) {
        screen.draw(settings, 175, 25);

        // comandi movimento navicella
        if ((int)DataUserManager.getProgress("movement_type")==1) screen.draw(selectedSetting, 268, 427);
        else screen.draw(selectedSetting, 268, 332);
        // comando sparo laser
        if ((int)DataUserManager.getProgress("shot_type")==1) screen.draw(selectedSetting, 558, 427);
        else screen.draw(selectedSetting, 558, 332);

        // disegno icone musica/suono
        //System.out.println(InputManager.musicPercent);
        if (InputManager.soundPercent==0f) screen.draw(soundOff, 230, 215);
        else screen.draw(soundOn, 230, 215);
        if (InputManager.musicPercent==0f) screen.draw(musicOff, 230, 145);
        else screen.draw(musicOn, 230, 145);

        // disegno barre volume
        float filledWidth1 = (InputManager.soundPercent) * 390;
        float filledWidth2 = (InputManager.musicPercent) * 390;
        if (filledWidth1 > 0.0) screen.draw(volumeState, 300, 228, filledWidth1, 25);
        if (filledWidth2 > 0.0) screen.draw(volumeState, 300, 157, filledWidth2, 25);

        // testo percentuale volumi
        fontBoldWhite20.draw(screen, Math.round(InputManager.soundPercent*100)+"%", 705, 250);
        fontBoldWhite20.draw(screen, Math.round(InputManager.musicPercent*100)+"%", 705, 177);
    }


    // metodo per stampare testi e immagini nelle pagine 'missions'
    public void printCompleteMission(SpriteBatch screen, int page, int c) {
        // spostamento lungo y di scritte e immagini ripetitive
        //int y=0;
        int y2=0;

        // array per controllare il completamente delle missioni in pagine 'missions'
        boolean[] isCompleted = CheckMissions.getInstance().checkCompleted(page, c);
        for (int i=0; i<4; i++) {
            // testo obiettivo missione
            //if (page!=31) fontBlue20.draw(screen, formatter.format(c), 380, 435+y);

            // spunta completamento missione
            if (isCompleted[i]) screen.draw(tickImg, 885, 430-y2);

            y2+=103;
        }
    }

    // metodo per mostrare i contenuti nelle pagine (testi, immagini, icone)
    public void showItems(SpriteBatch screen) {
        // background principale
        screen.draw(mapLobby.get(InputManager.page), 0, 0);

        // stampa immagini SOLO della Lobby
        if (!listSecondPages.contains(InputManager.page)) {
            // avatar
            screen.draw(mapAvatarsImgs.get((int) DataUserManager.getProgress("avatar")), 870, 557);
            // icona notifica completamento RTG
            if ((boolean) DataUserManager.getProgress("completed_RTG")) screen.draw(notifyCompletedRTG, 27, 338);
        }

        // switch delle pagine per stampare i vari elementi
        switch (InputManager.page) {
            // pagina 'classic game'
            case 0:
                // testi //
                fontMediumWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("points")), 402, 400); // punti totali
                fontMediumWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("matches_CG")), 425, 370); // partite giocate
                fontBoldBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_gold_heart")), 695, 362); // numero 'gold heart'
                fontBoldBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_super_laser")), 695, 248); // numero 'super laser'
                fontBoldBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_shield")), 808, 362); // numero 'shield'
                fontBoldBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_double_points")), 808, 248); // numero 'double points'

                // NAVICELLA //
                // immagine
                screen.draw(spImg, 330, 130);
                // nome
                fontMediumBlue15.draw(screen, selectedSp.getName(), 415, 226);
                // bonus velocità
                if (selectedSp.getSpSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getSpSpeed(), 480, 215);
                // bonus v. laser
                if (selectedSp.getLaserSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getLaserSpeed(), 480, 180);
                // bonus punti
                if (selectedSp.getBonusPoint()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getBonusPoint() + "%", 450, 145);

                // difficoltà
                switch ((int)DataUserManager.getProgress("diff_classic_game")) {
                    case 1:
                        screen.draw(diffCG1, 640 ,108);
                        break;
                    case 2:
                        screen.draw(diffCG2, 640 ,108);
                        break;
                    case 3:
                        screen.draw(diffCG3, 640 ,108);
                        break;
                }

                // selezione carta speciale
                if (InputManager.goldHeart) screen.draw(rectSelectCard, 688, 375);
                if (InputManager.superLaser) screen.draw(rectSelectCard, 688, 261);
                if (InputManager.shield) screen.draw(rectSelectCard, 801, 375);
                if (InputManager.doublePoints) screen.draw(rectSelectCard, 801, 261);

                // button start hover
                if (InputManager.isBtnStartHover) screen.draw(buttonsOver[0], 773, 101);

                break;

            // pagina 'space battle'
            case 1:
                // testi //
                fontMediumWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("won_SB")), 425, 415); // vittorie
                fontMediumWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("cons_won_SB")), 445, 385); // vittorie consecutive
                fontMediumWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("matches_SB")), 425, 355); // partite giocate
                fontBoldBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_gold_heart")), 695, 362); // numero 'gold heart'
                fontBoldBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_super_laser")), 808, 362); // numero 'super laser'

                // NAVICELLA //
                // immagine
                screen.draw(spImg, 330, 130);
                // nome
                fontMediumBlue15.draw(screen, selectedSp.getName(), 415, 226);
                // bonus velocità
                if (selectedSp.getSpSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getSpSpeed(), 480, 215);
                // bonus v. laser
                if (selectedSp.getLaserSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getLaserSpeed(), 480, 180);
                // bonus punti
                if (selectedSp.getBonusPoint()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getBonusPoint() + "%", 450, 145);

                // difficoltà
                switch ((int)DataUserManager.getProgress("diff_space_battle")) {
                    case 1:
                        screen.draw(diffSB1, 640 ,108);
                        break;
                    case 2:
                        screen.draw(diffSB2, 640 ,108);
                        break;
                    case 3:
                        screen.draw(diffSB3, 640 ,108);
                        break;
                }

                // selezione carta speciale
                if (InputManager.goldHeart) screen.draw(rectSelectCard, 688, 375);
                if (InputManager.superLaser) screen.draw(rectSelectCard, 801, 375);

                // testo informativo gioco bloccato
                if ((int)DataUserManager.getProgress("level")<11) screen.draw(infoBanner, 588, 175);

                // button fight hover
                if (InputManager.isBtnStartHover) screen.draw(buttonsOver[1], 773, 101);

                break;

            // pagina 'space journey'
            case 2:
                // testi //
                fontMediumWhite20.draw(screen, String.valueOf((int)DataUserManager.getProgress("level")), 385, 410); // livello
                fontMediumWhite20.draw(screen, String.valueOf((((int)DataUserManager.getProgress("level"))) / 10 + 1), 475, 380); // galassia corrente

                // navicella //
                // immagine
                screen.draw(spImg, 330, 130);
                // nome
                fontMediumBlue15.draw(screen, selectedSp.getName(), 415, 226);
                // bonus velocità
                if (selectedSp.getSpSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getSpSpeed(), 480, 215);
                // bonus v. laser
                if (selectedSp.getLaserSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getLaserSpeed(), 480, 180);
                // bonus punti
                if (selectedSp.getBonusPoint()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getBonusPoint() + "%", 450, 145);

                // button map hover
                if (InputManager.isBtnStartHover) screen.draw(buttonsOver[2], 773, 101);

                break;

            // pagina 'rtg'
            case 3:
                // recupero missione corrente
                int missionID = (int) DataUserManager.getProgress("mission_id");
                RTG m = RTGs[missionID-1];

                // testi //
                fontMediumBlue20.draw(screen, formatter.format((int) DataUserManager.getProgress("num_mission")), 565, 407); // numero missione raggiunta
                fontMediumBlue20.draw(screen, m.printMission(), 516, 365); // missione da completare
                fontMediumBlue20.draw(screen, "x" + m.prize, 720, 231); // premio missione

                // progresso completamento task corrente
                drawRTGPage(screen, missionID);

                break;

            // pagina 'marketplace'
            case 17:
                // testi //
                fontBoldWhite25.draw(screen, formatter.format(InputManager.currentCredit), 700, 495); // numero totale crediti

                // numero prodotti da acquistare
                fontBoldWhite15.draw(screen, formatter.format(InputManager.item1), 384, 310); // item 1
                fontBoldWhite15.draw(screen, formatter.format(InputManager.item2), 533, 310); // item 2
                fontBoldWhite15.draw(screen, formatter.format(InputManager.item3), 683, 310); // item 3
                fontBoldWhite15.draw(screen, formatter.format(InputManager.item4), 833, 310); // item 4
                fontBoldWhite15.draw(screen, formatter.format(InputManager.item5), 487, 157); // item 5
                fontBoldWhite15.draw(screen, formatter.format(InputManager.item6), 730, 157); // item 6

                // testi "sold out" per specificare che i prodotti non sono disponibili
                if ((boolean) DataUserManager.getProgress("state_product_5")) screen.draw(txtSoldOut, 435, 160);
                if ((boolean) DataUserManager.getProgress("state_product_6")) screen.draw(txtSoldOut, 680, 160);

                // button confirm-purchase and reset hover
                if (InputManager.isBtnBuyHover) screen.draw(buttonsOver[4], 504, 68);
                if (InputManager.isBtnResetHover) screen.draw(buttonsOver[5], 791, 68);

                // prezzo finale
                fontBoldWhite25.draw(screen, formatter.format(InputManager.finalPrize), 530, 108); // prezzo d'acquisto finale
                break;

            // pagina 'profile info'
            case 19:
                // testi //
                // SCRITTE A SX
                fontMediumWhite20.draw(screen, AuthAlgorithms.nickname, 172, 387); // nickname
                fontMediumWhite20.draw(screen, AuthAlgorithms.password, 172, 347); // password
                fontMediumWhite20.draw(screen, AuthAlgorithms.date, 185, 308); // data registrazione

                // SCRITTE A DX
                fontMediumWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("points")), 615, 412); // punti
                fontMediumWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("level")), 605, 372); // livello
                fontMediumWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_mission")), 625, 332); // numero missione
                fontMediumWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_aliens_hit")), 645, 292); // alieni colpiti
                fontMediumWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("credits")), 625, 252); // crediti
                fontMediumWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("total_credits")), 680, 212); // crediti totali
                fontMediumWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("matches_CG")), 680, 172); // partite classic game
                fontMediumWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("matches_SB")), 680, 132); // partite space battle
                fontMediumWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("won_SB")), 680, 92); // vittorie space battle

                // immagini //
                screen.draw(mapAvatarsImgs.get((int)DataUserManager.getProgress("avatar")), 461, 513); // avatar
                break;

            // pagina 'missions 1'
            case 10:
                // testi e immagini //
                printCompleteMission(screen, InputManager.page, (int)DataUserManager.getProgress("matches_CG"));
                break;

            // pagina 'missions 2'
            case 11:
                // testi e immagini //
                printCompleteMission(screen, InputManager.page, 100000000);
                break;

            // pagina 'missions 3'
            case 12:
                // testi e immagini //
                printCompleteMission(screen, InputManager.page, (int)DataUserManager.getProgress("matches_SB"));
                break;

            // pagina 'missions 4'
            case 13:
                printCompleteMission(screen, InputManager.page, (int)DataUserManager.getProgress("won_SB"));
                break;

            // pagina 'missions 5'
            case 14:
                printCompleteMission(screen, InputManager.page, (int)DataUserManager.getProgress("points"));
                break;

            // pagina 'missions 6'
            case 15:
                printCompleteMission(screen, InputManager.page, (int)DataUserManager.getProgress("level"));
                break;

            // pagina 'missions 7'
            case 16:
                printCompleteMission(screen, InputManager.page, (int)DataUserManager.getProgress("credits"));
                break;

            // pagina avatars
            case 18:
                // stampa immagini
                int x=145; int y=410; // x e y del primo avatar
                for (int i=0; i<=19; i++) {
                    // stampa immagine avatar
                    if (!Avatar.isAchieved(i)) {
                        fontItalicBoldWhite15.draw(screen, mapAvatars.get(i).getMissione(), x, y-15);
                        screen.draw(avatarsCovered[i], x, y);
                    }
                    else {
                        fontBoldWhite15.draw(screen, mapAvatars.get(i).getNome(), x, y-15);
                        screen.draw(avatars[i], x, y);
                    }

                    // riquadro selezione
                    if ((int) DataUserManager.getProgress("avatar") == i) screen.draw(selectedAvatar, x-5, y-5);

                    // posizione oggetti
                    x+=161;
                    if ((i+1)%5==0) { x=145; y-=111; } // reset posizione alla nuova riga
                }
        }

        // disegno eventuale schermo sovrapposto
        if (InputManager.secondScreen) {
            if (InputManager.open22) screen.draw(softInfos, 250, 175); // info software
            else if (InputManager.open23) { // chiusura gioco
                screen.draw(closeGame, 250, 175);

                // button YES and NO hover
                if (InputManager.isBtnLHover) screen.draw(buttonsOver[9], 271, 211);
                if (InputManager.isBtnRHover) screen.draw(buttonsOver[6], 513, 211);
            }
            else if (InputManager.open24) { // avviso difficoltà elevata
                screen.draw(warning, 250, 175);

                // button OK and PLAY hover
                if (InputManager.isBtnLHover) screen.draw(buttonsOver[7], 271, 211);
                if (InputManager.isBtnRHover) screen.draw(buttonsOver[8], 513, 211);
            }
            else if (InputManager.open25) { // conferma acquisto
                screen.draw(confirmBuy, 250, 175);
                // testo prezzo totale
                fontBoldWhite25.draw(screen, formatter.format(InputManager.finalPrize), 390, 347);

                // button YES and NO hover
                if (InputManager.isBtnLHover) screen.draw(buttonsOver[9], 271, 211);
                if (InputManager.isBtnRHover) screen.draw(buttonsOver[6], 513, 211);
            }
            else if (InputManager.open26) drawSettingsPage(screen); // impostazioni di gioco
        }
    }

    // metodo per liberare la memoria
    public void disposeUI() {
        for (int i = 0; i < 20; i++) {
            avatars[i].dispose();
        }
        for (int i=4; i<=19; i++) {
            avatarsCovered[i].dispose();
        }
        selectedAvatar.dispose();

        for (Texture t : mapAvatarsImgs.values()) t.dispose();
        for (Texture t : mapLobby.values()) t.dispose();

        tickImg.dispose();
        diffCG1.dispose();
        diffCG2.dispose();
        diffCG3.dispose();
        diffSB1.dispose();
        diffSB2.dispose();
        diffSB3.dispose();
        closeGame.dispose();
        softInfos.dispose();
        warning.dispose();
        confirmBuy.dispose();
        settings.dispose();
        selectedSetting.dispose();
    }
}
