/*
Astro Invasion - class AuthManagerUI -
Gestisce le grafiche delle schermate della lobby
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.UI.Lobby;

// import codici e librerie
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import sorgente.Entities.Avatar;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.DataUserManager;
import sorgente.Missions.CheckMissions;
import sorgente.Missions.RTG;
import sorgente.ResourceLoader;
import sorgente.Entities.Spacecraft;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class UIManager implements ResourceLoader {
    // renderer per la barra di progresso della task corrente RTG
    private ShapeRenderer shapeRenderer;

    // dichiarazione icone difficoltà, spunta completamento, premi RTG
    private Texture tickImg, diffCG1, diffCG2, diffCG3, diffSB1, diffSB2, diffSB3, claimPrize, progressRTG;

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
    private Texture closeGame, softInfos, warning;

    private BitmapFont fontBlue15, fontBlue20, fontBoldBlue20, fontWhite20, fontBoldWhite15, fontBoldWhite20, fontItalicBoldWhite15;
    //private BitmapFont fontRed20;

    // hashmap per le diverse texture
    private final HashMap<Integer, Texture> mapLobby; // schermate lobby
    private final HashMap<Integer, Texture> mapAvatarsImgs; // immagini avatar
    private final HashMap<Integer, Avatar> mapAvatars; // oggetti avatar
    private final HashMap<Integer, Spacecraft> mapSpacecrafts; // oggetti navicella

    // arraylist delle pagine secondarie
    private final Set<Integer> listSecondPages = Set.of(10, 11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22, 23, 24);

    // navicella utente
    private Spacecraft selectedSp;

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

        // mouse
        mouse = new Pixmap(Gdx.files.internal("images/cursor.png"));
        mouseOver = new Pixmap(Gdx.files.internal("images/mouse_over.png"));

        cursor = Gdx.graphics.newCursor(mouse, 0, 0);
        cursorOver = Gdx.graphics.newCursor(mouseOver, 0, 0);

        shapeRenderer = new ShapeRenderer();

        // caricamento risorse
        createMissions();
        loadLobbyImages(); // schermate lobby
        loadImages(); // altre immagini
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
            fontBlue15 = new BitmapFont(Gdx.files.internal("font/inter/regular_blue_15.fnt")); // inter regular blue 15
            fontBlue20 = new BitmapFont(Gdx.files.internal("font/inter/regular_blue_20.fnt")); // inter regular blue 20
            fontBoldBlue20 = new BitmapFont(Gdx.files.internal("font/inter/bold_blue_20.fnt")); // inter regular blue 20
            fontWhite20 = new BitmapFont(Gdx.files.internal("font/inter/regular_white_20.fnt")); // inter regular white 20
            fontBoldWhite15 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_15.fnt")); // inter bold white 15
            fontBoldWhite20 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_20.fnt")); // inter bold white 20
            fontItalicBoldWhite15 = new BitmapFont(Gdx.files.internal("font/inter/bold_italic_white_15.fnt")); // inter italic bold white 15
            //fontRed20 = new BitmapFont(Gdx.files.internal("font/inter/regular_red_20.fnt")); // inter regular red 20
        } catch (Exception e) {
            // dichiarazione font
            BitmapFont font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("#151A3B")); // colore blu
        }
    }

    // metodo per caricare le immagini delle pagine di Accesso e Registrazione
    @Override
    public void loadImages() {
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

        // immagine spunta per completamento missione o selezione oggetti
        tickImg = new Texture("images/tick.png");

        // immagine di progresso missione RTG
        progressRTG = new Texture("images/progress.png");
        progressBarRegion = new TextureRegion(progressRTG);

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

        selectedAvatar = new Texture("images/avatars/selected_avatar.png");
    }

    // metodo per caricare le immagini della Lobby
    public void loadLobbyImages(){
        // popolamento mappa lobby
        for (int i = 0; i < 25; i++) mapLobby.put(i, new Texture("lobby_screens/lobby (" + i + ").png"));
        // popolamento mappa avatar
        for (int i = 0; i <= 19; i++) mapAvatarsImgs.put(i, new Texture("images/avatars/av (" + i + ").png"));

        // "pulsante" raccolta premio
        Texture img_special = new Texture("images/rect_claim_reward_eng.png");
        mapLobby.put(35, img_special);

        // immagini in sovra impressione
        closeGame = new Texture("secondary_screens/lobby_close_game_eng.png");
        softInfos = new Texture("secondary_screens/lobby_software_info_eng.png");
        warning = new Texture("secondary_screens/lobby_warning_eng.png");
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
        mapAvatars.put(16, new Avatar("Matthew", "Claim 1K Credits"));
        mapAvatars.put(17, new Avatar("Kiara", "Claim 5K Credits"));
        mapAvatars.put(18, new Avatar("Luke", "Reach 1M Points"));
        mapAvatars.put(19, new Avatar("Emma", "Win 100 Space Battles"));
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
        RTG RTG0 = new RTG("Hit", 50, "aliens in Classic Game matches.", "1 Gold Heart", "images/cards/cart1_gold_heart_eng.png");
        RTG RTG1 = new RTG("Win", 1, "Space Battle matches.", "1 Shield", "images/cards/cart2_shield_eng.png");
        RTG RTG2 = new RTG("Earn", 2000, "points through\nthe Classic Game.", "100 Credits", "images/cards/card_100_coins.png");
        RTG RTG3 = new RTG("Earn", 5, "credits through Space Battle\nand/or Classic Game matches.", "1 Super Laser", "images/cards/cart3_super_laser_eng.png");

        RTGs[0] = RTG0;
        RTGs[1] = RTG1;
        RTGs[2] = RTG2;
        RTGs[3] = RTG3;
    }

    // metodo per disegnare la barra di progresso della task corrente del RTG
    public void drawPageRTG(SpriteBatch screen, int missionID) {
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

        // progresso in percentuale
        int percentage = (int) Math.ceil((progress / (float) maxProgress)*100);
        fontBoldWhite20.draw(screen, percentage+"%", 525, 294);
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
            //y+=0;
        }
    }

    // metodo per mostrare i contenuti nelle pagine (testi, immagini, icone)
    public void showItems(SpriteBatch screen) {
        // background principale
        screen.draw(mapLobby.get(InputManager.page), 0, 0);
        //screen.draw(mapLobby.get(29), 0, 0);

        // stampa avatar
        if (!listSecondPages.contains(InputManager.page)) {
            // stampa avatar
            screen.draw(mapAvatarsImgs.get((int) DataUserManager.getProgress("avatar")), 870, 557);
        }

        // switch delle pagine per stampare i vari elementi
        switch (InputManager.page) {
            // pagina 'classic game'
            case 0:
                // testi //
                fontBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("points")), 395, 410); // punti totali
                fontBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("matches_CG")), 420, 380); // partite giocate
                fontWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_gold_heart")), 715, 385); // numero 'gold heart'
                fontWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_shield")), 878, 385); // numero 'shield'
                fontWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_super_laser")), 715, 229); // numero 'super laser'
                fontWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_double_points")), 878, 229); // numero 'double points'

                // navicella //
                // immagine
                screen.draw(new Texture(selectedSp.getPathImg()), 330, 130);
                // nome
                fontBlue15.draw(screen, selectedSp.getName(), 413, 226);
                // bonus velocità
                if (selectedSp.getSpSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getSpSpeed(), 480, 215);
                // bonus v. laser
                if (selectedSp.getLaserSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getLaserSpeed(), 480, 180);
                // bonus punti
                if (selectedSp.getBonusPoint()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getBonusPoint() + "%", 450, 145);

                // difficoltà
                switch ((int)DataUserManager.getProgress("diff_classic_game")) {
                    case 1:
                        screen.draw(diffCG1, 646 ,108);
                        break;
                    case 2:
                        screen.draw(diffCG2, 646 ,108);
                        break;
                    case 3:
                        screen.draw(diffCG3, 646 ,108);
                        break;
                }

                // spunta selezione carta speciale
                if (InputManager.goldHeart) screen.draw(tickImg, 712, 330);
                if (InputManager.shield) screen.draw(tickImg, 874, 330);
                if (InputManager.superLaser) screen.draw(tickImg, 712, 174);
                if (InputManager.doublePoints) screen.draw(tickImg, 874, 174);

                break;

            // pagina 'space battle'
            case 1:
                // testi //
                fontBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("won_SB")), 420, 410); // vittorie
                fontBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("cons_won_SB")), 435, 380); // vittorie consecutive
                fontBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("matches_SB")), 420, 350); // partite giocate
                fontWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_gold_heart")), 715, 385); // numero 'gold heart'
                fontWhite20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_super_laser")), 878, 385); // numero 'super laser'

                // navicella //
                // immagine
                screen.draw(new Texture(selectedSp.getPathImg()), 330, 130);
                // nome
                fontBlue15.draw(screen, selectedSp.getName(), 413, 226);
                // bonus velocità
                if (selectedSp.getSpSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getSpSpeed(), 480, 215);
                // bonus v. laser
                if (selectedSp.getLaserSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getLaserSpeed(), 480, 180);
                // bonus punti
                if (selectedSp.getBonusPoint()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getBonusPoint() + "%", 450, 145);

                // difficoltà
                switch ((int)DataUserManager.getProgress("diff_space_battle")) {
                    case 1:
                        screen.draw(diffSB1, 646 ,108);
                        break;
                    case 2:
                        screen.draw(diffSB2, 646 ,108);
                        break;
                    case 3:
                        screen.draw(diffSB3, 646 ,108);
                        break;
                }

                // spunta selezione carta speciale
                if (InputManager.goldHeart) screen.draw(tickImg, 712, 330);
                if (InputManager.superLaser) screen.draw(tickImg, 874, 330);

                break;

            // pagina 'space journey'
            case 2:
                // testi //
                fontBlue20.draw(screen, String.valueOf((int)DataUserManager.getProgress("level")), 385, 410); // livello
                fontBlue20.draw(screen, String.valueOf((((int)DataUserManager.getProgress("level"))) / 10 + 1), 475, 380); // galassia corrente

                // navicella //
                // immagine
                screen.draw(new Texture(selectedSp.getPathImg()), 330, 130);
                // nome
                fontBlue15.draw(screen, selectedSp.getName(), 413, 226);
                // bonus velocità
                if (selectedSp.getSpSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getSpSpeed(), 480, 215);
                // bonus v. laser
                if (selectedSp.getLaserSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getLaserSpeed(), 480, 180);
                // bonus punti
                if (selectedSp.getBonusPoint()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getBonusPoint() + "%", 450, 145);

                break;

            // pagina 'rtg'
            case 3:
                // recupero missione corrente
                int missionID = (int) DataUserManager.getProgress("mission_id");
                RTG m = RTGs[missionID-1];

                // testi //
                fontBlue20.draw(screen, formatter.format((int) DataUserManager.getProgress("num_mission")), 565, 403); // numero missione raggiunta
                fontBlue20.draw(screen, m.printMission(), 516, 365); // missione da completare
                fontBlue20.draw(screen, m.prize, 720, 231); // premio missione

                // progresso completamento task corrente
                drawPageRTG(screen, missionID);

                break;

            // pagina 'marketplace'
            case 17:
                // testi //
                fontBoldBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("credits")), 550, 490); // numero totale crediti

                break;

            // pagina 'profile info'
            case 20:
                // testi //
                // scritte a sx
                /// TODO: recuperare i valori di nick e psw utente qui sotto e togliere i commenti
                //fontBlue20.draw(screen, LogInSignUp.nickname, 172, 412); // nickname
                //fontBlue20.draw(screen, LogInSignUp.password, 172, 372); // password

                // scritte a dx
                fontBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("points")), 620, 412); // punti
                fontBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("level")), 610, 372); // livello
                fontBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_mission")), 630, 332); // numero missione
                fontBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("credits")), 630, 292); // crediti
                fontBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("matches_CG")), 690, 252); // partite classic game
                fontBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("matches_SB")), 690, 212); // partite space battle
                fontBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("won_SB")), 690, 172); // vittorie space battle

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
            case 19:
                // stampa immagini
                int x=143; int y=410;
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
                    if ((int) DataUserManager.getProgress("avatar") == i) screen.draw(selectedAvatar, x-2, y-2);

                    // posizione oggetti
                    x+=161;
                    if ((i+1)%5==0) { x=143; y-=111; }
                }
        }

        // disegno eventuale schermo sovrapposto (chiusura gioco/software infos)
        if (InputManager.secondScreen) {
            if (InputManager.open23) screen.draw(closeGame, 250, 175);
            else if (InputManager.open22) screen.draw(softInfos, 250, 175);
            else if (InputManager.open24) screen.draw(warning, 250, 175);
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
    }
}
