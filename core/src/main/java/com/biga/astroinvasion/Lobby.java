/*
Astro Invasion - class Lobby -
This class manages and controls all the screens in the game's lobby
Developed by BIGA©. All rights reserved.
*/

package com.biga.astroinvasion;

// import librerie
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Lobby implements Screen {
    private final Main game; // variabile di riferimento tipo gioco
    // dichiarazione screen
    private final SpriteBatch screen;

    // dichiarazione font
    private BitmapFont font;
    private BitmapFont fontBlue20;
    private BitmapFont fontWhite20;
    private BitmapFont fontRed20;
    private BitmapFont fontBlue15;

    // stato carte speciale
    public static boolean goldHeart = false, superLaser = false, shield = false, doublePoints = false;

    // soundtrack
    Music soundtrack;

    // recupero nickname utente
    private final String nicknameInput = LogInSignUp.nickname;

    // dichiarazione immagini lobby
    private Texture img, img1, img2, img3, img4, img5, img6, img7,
        img8, img9,img10, img11, img12, img13, img14, img15,
        img16, img17, img18, img19, img20, img21, img22, img23, img24,
        img25, img26, img27, img28, img29, img30, img31, img32, img33, img34, img_special;

    // dichiarazione immagini secondarie
    private Texture tickImg, diffCG1, diffCG2, diffCG3, diffSB1, diffSB2, diffSB3;

    // dichiarazione immagini avatar
    Texture av1, av2, av3, av4, av5, av6, av7, av8, av9, av10,
        av11, av12, av13, av14, av15, av16, av17, av18, av19, av20;

    // dichiarazione variabili attributi utente
    private static int avatar, diffCG, diffSB, idMission, level,
    movType, shotType, spacecraft, numDoublePoints, numGoldHeart, numShield,
    numSuperLaser, mission, wonSbRtg, matchesCG, matchesSB, consWonSB, wonSB;

    public static int points, credits;

    // controllo completamento missione rtg
    boolean isRtgComplete;

    // creazione oggetto navicella generico
    public static Spacecraft selectedSp;

    /*
     'previousPage' serve a memorizzare l'ultima pagina aperta.
     Ciò permette di ritornare alla pagina precedente dopo aver chiuso una pagina che occupa interamente lo schermo
    */
    private int page, previousPage;

    // arraylist delle pagine secondarie
    ArrayList<Integer> listSecondPages = new ArrayList<>();

    // hashmap per mappare diversi elementi
    HashMap<Integer, Texture> mapLobby = new HashMap<>(); // hashmap schermate lobby
    HashMap<Integer, Texture> mapAvatar = new HashMap<>(); // hashmap immagini avatar
    HashMap<Integer, Spacecraft> mapSpacecrafts = new HashMap<>(); // hashmap immagini spacecrafts

    boolean secondScreen, open22, open23;

    // oggetto missione
    private Mission m;

    // oggetto logica per gestire la logica delle modalità di gioco
    private final Logic l = new Logic();

    // formatter per la virgola delle migliaia !in automatico converte l'intero in stringa
    NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

    // costruttore
    public Lobby(Main game) {
        this.game = game;
        // init dello screen
        this.screen = game.screen;

        // set immagine di default (classic game)
        page = previousPage = 6;

        // init del secondo "screen", dello screen software.infos e close.game a false
        secondScreen = open22 = open23 = false;

        // caricamento schermate lobby
        loadLobbyImages();

        // caricamento immagini secondarie
        loadImages();

        // caricamento font
        loadFont();

        // recupero progressi utente
        readFiles();

        // musica di sottofondo
        soundtrack = Gdx.audio.newMusic(Gdx.files.internal("sounds/lobby_sound.ogg")); // file audio
        soundtrack.setLooping(true); // true=loop music; false=no loop
        soundtrack.play(); // avvio musica

        // creazione oggetti "navicella"
        createSpacecrafts();

        // prova carte
        /*
        shield = true;
        superLaser = true;
        goldHeart = true;
        doublePoints = true;
        */
    }

    // -------------- //
    // GESTIONE INPUT //
    // -------------- //

    // classe interna per gestire gli input da mouse e tastiera
    private class MyInputProcessor extends InputAdapter {
        @Override
        public boolean keyDown(int keycode) {
            // click tasto esc
            if (keycode == Input.Keys.ESCAPE && (page!=7 && page!=21 && !open22 && !open23 && page!=24 && page!=25)) {
                open23 = true;
                secondScreen = true;
            }
            // click tasto esc per annullare il logout
            else if (keycode == Input.Keys.ESCAPE && (secondScreen&&open23)) {
                secondScreen = open23 = false;
            }

            return true;
        }

        // metodo recuperare il click del mouse, crea inizialmente costruttore hash map che contenga interi e Texture, quando poi carico le immagini le abbino con l'has map; 1--> img1;
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            /*
            'page' deve essere diverso da certe pagine per non generare l'apertura
            di altre pagine dove non è possibile e poter cambiare le schermate della Lobby.
            Esempio: l'utente NON può aprire la pagina 'classic game' dalla pagina 'instructions'
            */
            if (!listSecondPages.contains(page) && !open22 && !open23) {
                // pagina 6 => 'classic game'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 180 && screenY <= 220)) {
                    page = 6;
                }
                // pagina 13 => 'space battle'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 230 && screenY <= 270)) {
                    page = 13;
                }
                // pagina 14 => 'space journey'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 280 && screenY <= 320)) {
                    page = 14;
                }
                // pagina 12 => 'road to glory'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 330 && screenY <= 370)) {
                    page = 12;
                }
                // pagina 15 => 'spacecrafts 1'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 380 && screenY <= 420)) {
                    page = 15;
                }
                // pagina 26 => 'missions 1'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 430 && screenY <= 470)) {
                    previousPage = page;
                    page = 26;
                }
                // pagina 11 => 'marketplace'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 480 && screenY <= 520)) {
                    page = 11;
                }
                // cambio pagina (1-5) => 'avatar/spacecraft/ 1->5'
                if ((screenX >= 873 && screenX <=913) && (screenY >= 553 && screenY <=593)) {
                    if ((page >=1 && page < 5) || (page >= 15 && page < 20)) page++;
                }
                // cambio pagina (5-1) => 'avatar/spacecraft/ 5->1'
                if ((screenX >= 343 && screenX <=373) && (screenY >= 553 && screenY <=593)) {
                    if ((page <= 5 && page>1) || (page <= 20 && page>15)) page--;
                }
                // pagina 7 => 'instructions'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 530 && screenY <= 570)) {
                    previousPage = page;
                    page = 7;
                }
                // pagine 21 => 'settings'
                if ((screenX >= 50 && screenX <=90) && (screenY >= 580 && screenY <=620)) {
                    previousPage = page;
                    page = 21;
                }
                // pagina 24 => 'difficulty infos classic game'
                if (page == 6 && (screenX >= 623 && screenX <=703) && (screenY >= 552 && screenY <=592)) {
                    previousPage = page;
                    page = 24;
                }
                // pagina 33 => 'difficulty infos space battle'
                if (page == 13 && (screenX >= 623 && screenX <=703) && (screenY >= 552 && screenY <=592)) {
                    previousPage = page;
                    page = 33;
                }
                // pagina 34 => 'cards infos'
                if ((page == 13 || page == 6) && (screenX >= 883 && screenX <=913) && (screenY >= 230 && screenY <=260)) {
                    previousPage = page;
                    page = 34;
                }
                // pagina 25 => 'profile info'
                if ((screenX >= 870 && screenX <=950) && (screenY >= 66 && screenY <=146)) {
                    previousPage = page;
                    page = 25;
                }
                // pagina 22 => 'software infos'
                if ((screenX >= 110 && screenX <=150) && (screenY >= 580 && screenY <=620)) {
                    open22 = true;
                    secondScreen = true;
                }
                // pagina 23 => 'logout'
                if ((screenX >= 170 && screenX <=210) && (screenY >= 580 && screenY <=620)) {
                    open23 = true;
                    secondScreen = true;
                }

                // CONTROLLI PER AVVIARE LE MODALITÀ DI GIOCO //
                // avvio 'Classic Game'
                if (page == 6 && (screenX >= 778 && screenX <=928) && (screenY >= 552 && screenY <=592)) {
                    previousPage = page;
                    page=0;
                    soundtrack.stop();
                    game.setScreen(new ClassicGame(game)); // apertura nuovo screen
                }
            }

            // chiusura pagina instruction/settings/profile info&difficulty/missions
            if ((listSecondPages.contains(page) && (screenX >= 908 && screenX <= 948) && (screenY >= 84 && screenY <= 124))) {
                page = previousPage;
            }
            // cambio pagina (26-32) => 'missions 1-7'
            if ((screenX >= 885 && screenX <= 925) && (screenY >= 622 && screenY <=642)) {
                if ((page >=26 && page < 32)) page++;
            }
            // cambio pagina (32-26) => 'missions 7-1'
            if ((screenX >= 65 && screenX <= 105) && (screenY >= 622 && screenY <=642)) {
                if ((page <= 32 && page > 26)) page--;
            }
            // pagina 1 => 'avatar 1'
            if (page == 25 && (screenX >= 459 && screenX <=537) && (screenY >= 110 && screenY <=188)) {
                page = 1;
            }
            // chiusura software.infos
            if ((secondScreen&&open22) && (screenX >= 684 && screenX <= 724) && (screenY >= 206 && screenY <= 246)) {
                secondScreen = open22 = false;
            }
            // chiusura (annullamento) logout
            if ((secondScreen&&open23) && (screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
                secondScreen = open23 = false;
            }
            // back to LogInSignUp => YES logout
            if ((secondScreen&&open23) && (screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
                soundtrack.stop();
                game.setScreen(new LogInSignUp(game));
            }

            return true;
        }
    }

    // ------------------- //
    // LOGICA DELLA CLASSE //
    // ------------------- //

    // creazione oggetti missione
    public String createMissions() {
        // creazione oggetti
        Mission mission1 = new Mission("Hit", 10, "aliens in a Classic Game match.", "1 Gold Heart", "images/cards/cart1_gold_heart_eng.png");
        Mission mission2 = new Mission("Win", 1, "Space Battle matches.", "1 Shield", "images/cards/cart2_shield_eng.png");
        Mission mission3 = new Mission("Earn", 5000, "points in a single\nClassic Game match.", "100 Credits", "images/cards/card_100_coins.png");
        Mission mission4 = new Mission("Earn", 5, "credits.", "1 Super Laser", "images/cards/cart3_super_laser_eng.png");

        // missione default
        m=mission1;

        // selezione oggetto in base alla missione corrente
        switch (idMission) {
            case 1:
                break;
            case 2:
                m=mission2;
                break;
            case 3:
                m=mission3;
                break;
            case 4:
                m=mission4;
                break;
        }

        return m.printMission(mission);
    }

    // creazione oggetti navicella
    public void createSpacecrafts() {
        Spacecraft sp1 = new Spacecraft("Omega", "images/spacecrafts/_omega.png", new Texture("images/lasers/laser_omega.png"), 0, 1, 0);
        Spacecraft sp2 = new Spacecraft("Idra", "images/spacecrafts/_idra.png", new Texture("images/lasers/laser_idra.png"), 5, 0, 0);
        Spacecraft sp3 = new Spacecraft("Pegaso", "images/spacecrafts/_pegaso.png", new Texture("images/lasers/laser_pegaso.png"), 1, 0, 0);
        Spacecraft sp4 = new Spacecraft("Woka", "images/spacecrafts/_woka.png", new Texture("images/lasers/laser_woka.png"), 0, 1, 0);
        Spacecraft sp5 = new Spacecraft("Beowulf", "images/spacecrafts/_beowulf_basic.png", new Texture("images/lasers/laser_beowulf.png"), 0, 2, 0);
        Spacecraft sp6 = new Spacecraft("Andvari", "images/spacecrafts/_andvari_basic.png", new Texture("images/lasers/laser_andvari.png"), 10, 0, 0);
        Spacecraft sp7 = new Spacecraft("Siko", "images/spacecrafts/_siko_basic.png", new Texture("images/lasers/laser_siko.png"), 0, 0, 2);
        Spacecraft sp8 = new Spacecraft("Fenixia", "images/spacecrafts/_fenixia_basic.png", new Texture("images/lasers/laser_fenixia.png"), 0, 0, 3);
        Spacecraft sp9 = new Spacecraft("Ares", "images/spacecrafts/_ares_basic.png", new Texture("images/lasers/laser_ares.png"), 0, 3, 0);
        Spacecraft sp10 = new Spacecraft("Asgard", "images/spacecrafts/_asgard_basic.png", new Texture("images/lasers/laser_asgard.png"), 15, 0, 0);
        Spacecraft sp11 = new Spacecraft("Galahad", "images/spacecrafts/_galahad_basic.png", new Texture("images/lasers/laser_galahad.png"), 0, 1, 1);
        Spacecraft sp12 = new Spacecraft("Malloc", "images/spacecrafts/_malloc_basic.png", new Texture("images/lasers/laser_malloc.png"), 10, 2, 0);
        Spacecraft sp13 = new Spacecraft("Orion", "images/spacecrafts/_orion_basic.png", new Texture("images/lasers/laser_orion.png"), 0, 2, 1);
        Spacecraft sp14 = new Spacecraft("Centauro", "images/spacecrafts/_centauro_basic.png", new Texture("images/lasers/laser_centauro.png"), 20, 0, 0);
        Spacecraft sp15 = new Spacecraft("Zephyr", "images/spacecrafts/_zephyr_basic.png", new Texture("images/lasers/laser_centauro.png"), 0, 4, 1);
        Spacecraft sp16 = new Spacecraft("Phoenix", "images/spacecrafts/_phoenix_basic.png", new Texture("images/lasers/laser_phoenix.png"), 0, 1, 2);
        Spacecraft sp17 = new Spacecraft("Selen", "images/spacecrafts/_selen_basic.png", new Texture("images/lasers/laser_centauro.png"), 0, 2, 2);
        Spacecraft sp18 = new Spacecraft("Scylla", "images/spacecrafts/_scylla_basic.png", new Texture("images/lasers/laser_centauro.png"), 30, 0, 0);
        Spacecraft sp19 = new Spacecraft("Keto", "images/spacecrafts/_keto_basic.png", new Texture("images/lasers/laser_centauro.png"), 0, 1, 4);
        Spacecraft sp20 = new Spacecraft("Efron", "images/spacecrafts/_efron_basic.png", new Texture("images/lasers/laser_centauro.png"), 10, 0, 2);
        Spacecraft sp21 = new Spacecraft("Drakar", "images/spacecrafts/_drakar.png", new Texture("images/lasers/laser_centauro.png"), 0, 5, 5);
        Spacecraft sp22 = new Spacecraft("Rorik", "images/spacecrafts/_rorik.png", new Texture("images/lasers/laser_centauro.png"), 50, 5, 0);
        Spacecraft sp23 = new Spacecraft("Astrid", "images/spacecrafts/_astrid.png", new Texture("images/lasers/laser_centauro.png"), 50, 0, 5);
        Spacecraft sp24 = new Spacecraft("Alpha", "images/spacecrafts/_alpha.png", new Texture("images/lasers/laser_alpha.png"), 50, 5, 5);

        // array oggetti navicella
        Spacecraft[] sArray = {sp1, sp2, sp3, sp4, sp5, sp6, sp7, sp8, sp9, sp10,
            sp11, sp12, sp13, sp14, sp15, sp16, sp17, sp18, sp19, sp20,
            sp21, sp22, sp23, sp24};

        // popolamento mappa navicelle
        for (int i=1; i<24; i++) mapSpacecrafts.put(i, sArray[i]);

        // recupero navicella utente
        selectedSp = mapSpacecrafts.get(spacecraft);

        // attivazione carte in base alla navicella speciale
        if (spacecraft == 20) doublePoints = true;
        if (spacecraft == 21) superLaser = true;
        if (spacecraft == 22) shield = true;
        if (spacecraft == 23) goldHeart = true;
    }

    // metodo per recuperare i progressi utente
    public void readFiles() {
        System.out.println(nicknameInput);
        // avatar
        FileHandle readAvatar = Gdx.files.local("data/" + nicknameInput + "/progresses/avatar.txt");
        avatar = Integer.parseInt(readAvatar.readString());
        // monete
        FileHandle readCredits = Gdx.files.local("data/" + nicknameInput + "/progresses/credits.txt");
        credits = Integer.parseInt(readCredits.readString());
        // completamento missione RoadToGlory
        FileHandle readRTG = Gdx.files.local("data/" + nicknameInput + "/progresses/completed_rtg.txt");
        isRtgComplete = Boolean.parseBoolean(readRTG.readString());
        // difficoltà classic game
        FileHandle readDiffCG = Gdx.files.local("data/" + nicknameInput + "/progresses/diff_classic_game.txt");
        diffCG = Integer.parseInt(readDiffCG.readString());
        // difficoltà space battle
        FileHandle readDiffSB = Gdx.files.local("data/" + nicknameInput + "/progresses/diff_space_battle.txt");
        diffSB = Integer.parseInt(readDiffSB.readString());
        // id missione (1-4)
        FileHandle readID = Gdx.files.local("data/" + nicknameInput + "/progresses/mission_id.txt");
        idMission = Integer.parseInt(readID.readString());
        // livello
        FileHandle readLevel = Gdx.files.local("data/" + nicknameInput + "/progresses/level.txt");
        level = Integer.parseInt(readLevel.readString());
        // tipo di movimento
        FileHandle readMovement = Gdx.files.local("data/" + nicknameInput + "/progresses/movement_type.txt");
        movType = Integer.parseInt(readMovement.readString());
        // tipo di sparo
        FileHandle readShot = Gdx.files.local("data/" + nicknameInput + "/progresses/shot_type.txt");
        shotType = Integer.parseInt(readShot.readString());
        // navicella
        FileHandle readSpacecraft = Gdx.files.local("data/" + nicknameInput + "/progresses/spacecraft.txt");
        spacecraft = Integer.parseInt(readSpacecraft.readString());
        // numero carte double points
        FileHandle readNumCards1 = Gdx.files.local("data/" + nicknameInput + "/progresses/num_double_points.txt");
        numDoublePoints = Integer.parseInt(readNumCards1.readString());
        // numero carte gold heart
        FileHandle readNumCards2 = Gdx.files.local("data/" + nicknameInput + "/progresses/num_gold_heart.txt");
        numGoldHeart = Integer.parseInt(readNumCards2.readString());
        // numero carte shield
        FileHandle readNumCards3 = Gdx.files.local("data/" + nicknameInput + "/progresses/num_shield.txt");
        numShield = Integer.parseInt(readNumCards3.readString());
        // numero carte super laser
        FileHandle readNumCards4 = Gdx.files.local("data/" + nicknameInput + "/progresses/num_super_laser.txt");
        numSuperLaser = Integer.parseInt(readNumCards4.readString());
        // numero missione raggiunta
        FileHandle readMission = Gdx.files.local("data/" + nicknameInput + "/progresses/num_mission.txt");
        mission = Integer.parseInt(readMission.readString());
        // numero partite vinte a space battle consecutive per RTG
        FileHandle readWonSbRTG = Gdx.files.local("data/" + nicknameInput + "/progresses/won_SB_RTG.txt");
        wonSbRtg = Integer.parseInt(readWonSbRTG.readString());
        // partite classic game
        FileHandle readMatchesCG = Gdx.files.local("data/" + nicknameInput + "/progresses/matches_CG.txt");
        matchesCG = Integer.parseInt(readMatchesCG.readString());
        // partite space battle
        FileHandle readMatchesSB = Gdx.files.local("data/" + nicknameInput + "/progresses/matches_SB.txt");
        matchesSB = Integer.parseInt(readMatchesSB.readString());
        // vittorie space battle
        FileHandle readWonSB = Gdx.files.local("data/" + nicknameInput + "/progresses/won_SB.txt");
        wonSB = Integer.parseInt(readWonSB.readString());
        // vittorie consecutive space battle
        FileHandle readConsWonSB = Gdx.files.local("data/" + nicknameInput + "/progresses/cons_won_SB.txt");
        consWonSB = Integer.parseInt(readConsWonSB.readString());
        // punteggio utente
        FileHandle readPoints = Gdx.files.local("data/" + nicknameInput + "/progresses/points.txt");
        points = Integer.parseInt(readPoints.readString());
    }

    // -------------------- //
    // GRAFICA DELLA CLASSE //
    // -------------------- //

    // caricamento e creazione font per le scritte
    private void loadFont() {
        /*
        Il font utilizzato è Inter-Regular di dimensione base 20
        Il colore iniziale è il blu di sfondo (hex:151A3B)
        - per cambiare il colore usare font.setColor()
        - per cambiare la dimensione del font usare font.getData().setScale(n) dove n è la dimensione di ingrandimento
        */
        // dichiarazione font
        try {
            fontBlue20 = new BitmapFont(Gdx.files.internal("font/inter/regular_blue_20.fnt")); // inter regular blue 20
            fontWhite20 = new BitmapFont(Gdx.files.internal("font/inter/regular_white_20.fnt")); // inter regular white 20
            fontRed20 = new BitmapFont(Gdx.files.internal("font/inter/regular_red_20.fnt")); // inter regular red 20
            fontBlue15 = new BitmapFont(Gdx.files.internal("font/inter/regular_blue_15.fnt")); // inter regular blue 15
        } catch (Exception e) {
            font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("#151A3B")); // colore blu
        }
    }

    // metodo per caricare le immagini della Lobby
    public void loadLobbyImages(){
        img1 = new Texture("lobby_images/lobby_avatars_group1_eng.png");
        img2 = new Texture("lobby_images/lobby_avatars_group2_eng.png");
        img3 = new Texture("lobby_images/lobby_avatars_group3_eng.png");
        img4 = new Texture("lobby_images/lobby_avatars_group4_eng.png");
        img5 = new Texture("lobby_images/lobby_avatars_special_group_eng.png");
        img6 = new Texture("lobby_images/lobby_classic_game_eng.png");
        img7 = new Texture("lobby_images/lobby_instructions_eng.png");
        img8 = new Texture("lobby_images/lobby_level_up_info_eng.png");
        img9 = new Texture("lobby_images/lobby_level_up_upgrade1_eng.png");
        img10 = new Texture("lobby_images/lobby_level_up_upgrade2_eng.png");
        img11 = new Texture("lobby_images/lobby_marketplace_eng.png");
        img12 = new Texture("lobby_images/lobby_road_to_glory_eng.png");
        img13 = new Texture("lobby_images/lobby_space_battle_eng.png");
        img14 = new Texture("lobby_images/lobby_space_journey_eng.png");
        img15 = new Texture("lobby_images/lobby_spacecrafts_classic_group_eng.png");
        img16 = new Texture("lobby_images/lobby_spacecrafts_groupFenixia_eng.png");
        img17 = new Texture("lobby_images/lobby_spacecrafts_groupMalloc_eng.png");
        img18 = new Texture("lobby_images/lobby_spacecrafts_groupPhoenix_eng.png");
        img19 = new Texture("lobby_images/lobby_spacecrafts_groupEfron_eng.png");
        img20 = new Texture("lobby_images/lobby_spacecrafts_special_group_eng.png");
        img21 = new Texture("lobby_images/lobby_settings_eng.png");
        img22 = new Texture("lobby_images/lobby_software_info_eng.png");
        img23 = new Texture("lobby_images/lobby_close_game_eng.png");
        img24 = new Texture("lobby_images/lobby_difficulty_cg_info_eng.png");
        img25 = new Texture("lobby_images/lobby_profile_info_eng.png");
        img26 = new Texture("lobby_images/lobby_missions1_eng.png");
        img27 = new Texture("lobby_images/lobby_missions2_eng.png");
        img28 = new Texture("lobby_images/lobby_missions3_eng.png");
        img29 = new Texture("lobby_images/lobby_missions4_eng.png");
        img30 = new Texture("lobby_images/lobby_missions5_eng.png");
        img31 = new Texture("lobby_images/lobby_missions6_eng.png");
        img32 = new Texture("lobby_images/lobby_missions7_eng.png");
        img33 = new Texture("lobby_images/lobby_difficulty_sb_info_eng.png");
        img34 = new Texture("lobby_images/lobby_cards_info_eng.png");
        img_special = new Texture("lobby_images/_rect_claim_reward_eng.png");

        // immagini secondarie variabili

        // immagini avatar
        av1 = new Texture("images/avatars/av (1).png");
        av2 = new Texture("images/avatars/av (2).png");
        av3 = new Texture("images/avatars/av (3).png");
        av4 = new Texture("images/avatars/av (4).png");
        av5 = new Texture("images/avatars/av (5).png");
        av6 = new Texture("images/avatars/av (6).png");
        av7 = new Texture("images/avatars/av (7).png");
        av8 = new Texture("images/avatars/av (8).png");
        av9 = new Texture("images/avatars/av (9).png");
        av10 = new Texture("images/avatars/av (10).png");
        av11 = new Texture("images/avatars/av (11).png");
        av12 = new Texture("images/avatars/av (12).png");
        av13 = new Texture("images/avatars/av (13).png");
        av14 = new Texture("images/avatars/av (14).png");
        av15 = new Texture("images/avatars/av (15).png");
        av16 = new Texture("images/avatars/av (16).png");
        av17 = new Texture("images/avatars/av (17).png");
        av18 = new Texture("images/avatars/av (18).png");
        av19 = new Texture("images/avatars/av (19).png");
        av20 = new Texture("images/avatars/av (20).png");

        // mappatura mapLobby
        mapLobby.put(1, img1);
        mapLobby.put(2, img2);
        mapLobby.put(3, img3);
        mapLobby.put(4, img4);
        mapLobby.put(5, img5);
        mapLobby.put(6, img6);
        mapLobby.put(7, img7);
        mapLobby.put(8, img8);
        mapLobby.put(9, img9);
        mapLobby.put(10, img10);
        mapLobby.put(11, img11);
        mapLobby.put(12, img12);
        mapLobby.put(13, img13);
        mapLobby.put(14, img14);
        mapLobby.put(15, img15);
        mapLobby.put(16, img16);
        mapLobby.put(17, img17);
        mapLobby.put(18, img18);
        mapLobby.put(19, img19);
        mapLobby.put(20, img20);
        mapLobby.put(21, img21);
        mapLobby.put(24, img24);
        mapLobby.put(25, img25);
        mapLobby.put(26, img26);
        mapLobby.put(27, img27);
        mapLobby.put(28, img28);
        mapLobby.put(29, img29);
        mapLobby.put(30, img30);
        mapLobby.put(31, img31);
        mapLobby.put(32, img32);
        mapLobby.put(33, img33);
        mapLobby.put(34, img34);
        mapLobby.put(35, img_special);

        // inserimento pagine secondarie nell'arraylist
        listSecondPages.add(7);
        listSecondPages.add(21);
        listSecondPages.add(24);
        listSecondPages.add(25);
        listSecondPages.add(26);
        listSecondPages.add(27);
        listSecondPages.add(28);
        listSecondPages.add(29);
        listSecondPages.add(30);
        listSecondPages.add(31);
        listSecondPages.add(32);
        listSecondPages.add(33);
        listSecondPages.add(34);

        // mappatura avatar
        mapping(mapAvatar, av1, av2, av3, av4, av5, av6, av7, av8, av9, av10, av11, av12, av13, av14, av15, av16, av17, av18, av19, av20);
    }

    // metodo per caricare immagini generiche
    public void loadImages() {
        // icona difficoltà classic game
        diffCG1 = new Texture("images/diff1_classicgame.png");
        diffCG2 = new Texture("images/diff2_classicgame.png");
        diffCG3 = new Texture("images/diff3_classicgame.png");

        // icona difficoltà space battle
        diffSB1 = new Texture("images/diff1_spacebattle.png");
        diffSB2 = new Texture("images/diff2_spacebattle.png");
        diffSB3 = new Texture("images/diff3_spacebattle.png");

        // immagine spunta per completamento missione o selezione oggetti
        tickImg = new Texture("images/tick.png");
    }

    // metodo per stampare testi e immagini nelle pagine 'missions'
    public void printCompleteMission(int c) {
        // spostamento lungo y di scritte e immagini ripetitive
        int y=0, y2=0;


        // array per controllare il completamente delle missioni in pagine 'missions'
        boolean[] isCompleted = l.checkCompleted(page, c);
        for (int i=0; i<4; i++) {
            if (page!=31) fontBlue20.draw(screen, formatter.format(c), 620, 412+y);

            // spunta completamento missione
            if (isCompleted[i]) screen.draw(tickImg, 885, 430-y2);

            y2+=103;
            y+=30;
        }
    }

    // metodo per mappare le hashmap
    private void mapping(HashMap<Integer, Texture> map, Texture img1, Texture img2, Texture img3, Texture img4,
                 Texture img5, Texture img6, Texture img7, Texture img8, Texture img9, Texture img10,
                 Texture img11, Texture img12, Texture img13, Texture img14, Texture img15, Texture img16,
                 Texture img17, Texture img18, Texture img19, Texture img20) {
        map.put(1, img1);
        map.put(2, img2);
        map.put(3, img3);
        map.put(4, img4);
        map.put(5, img5);
        map.put(6, img6);
        map.put(7, img7);
        map.put(8, img8);
        map.put(9, img9);
        map.put(10, img10);
        map.put(11, img11);
        map.put(12, img12);
        map.put(13, img13);
        map.put(14, img14);
        map.put(15, img15);
        map.put(16, img16);
        map.put(17, img17);
        map.put(18, img18);
        map.put(19, img19);
        map.put(20, img20);
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void show() {}

    // metodo per aggiornare lo schermo
    @Override
    public void render(float delta) {

        // attivazione controllo input
        Gdx.input.setInputProcessor(new Lobby.MyInputProcessor());

        screen.begin();

        // SCHERMATE //
        // disegno schermo principale
        screen.draw(mapLobby.get(page), 0, 0);

        // stampa avatar
        if (!listSecondPages.contains(page)) {
            screen.draw(mapAvatar.get(avatar), 870, 557);
        }

        // stampa TESTI e IMMAGINI variabili //
        switch (page) {
            // pagina 'classic game'
            case 6:
                // testi //
                fontBlue20.draw(screen, formatter.format(points), 395, 410); // punti totali
                fontBlue20.draw(screen, formatter.format(matchesCG), 420, 380); // partite giocate
                fontWhite20.draw(screen, formatter.format(numGoldHeart), 715, 385); // numero 'gold heart'
                fontWhite20.draw(screen, formatter.format(numShield), 878, 385); // numero 'shield'
                fontWhite20.draw(screen, formatter.format(numSuperLaser), 715, 229); // numero 'super laser'
                fontWhite20.draw(screen, formatter.format(numDoublePoints), 878, 229); // numero 'double points'

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
                switch (diffCG) {
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

                break;

            // pagina 'space battle'
            case 13:
                // testi //
                fontBlue20.draw(screen, formatter.format(wonSB), 420, 410); // vittorie
                fontBlue20.draw(screen, formatter.format(consWonSB), 435, 380); // vittorie consecutive
                fontBlue20.draw(screen, formatter.format(matchesSB), 420, 350); // partite giocate

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
                switch (diffSB) {
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

                break;

            // pagina 'space journey'
            case 14:
                // testi //
                fontBlue20.draw(screen, String.valueOf(level), 385, 410); // livello
                fontBlue20.draw(screen, String.valueOf((level- 1) / 10 + 1), 475, 380); // galassia corrente

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
            case 12:
                // testi //
                fontBlue20.draw(screen, createMissions(), 515, 370); // missione da completare
                fontBlue20.draw(screen, formatter.format(mission), 565, 407); // numero missione raggiunta
                fontBlue20.draw(screen, m.prize, 725, 272); // premio missione

                // immagini //
                screen.draw(new Texture(m.path), 660, 100);
                break;

            // pagina 'marketplace'
            case 11:
                // testi //
                fontBlue20.draw(screen, formatter.format(credits), 540, 490); // numero totale crediti

                break;

            // pagina 'profile info'
            case 25:
                // testi //
                // scritte a sx
                fontBlue20.draw(screen, LogInSignUp.nickname, 172, 412); // nickname
                fontBlue20.draw(screen, LogInSignUp.password, 172, 372); // password

                // scritte a dx
                fontBlue20.draw(screen, formatter.format(points), 620, 412); // punti
                fontBlue20.draw(screen, formatter.format(level), 610, 372); // livello
                fontBlue20.draw(screen, formatter.format(mission), 630, 332); // numero missione
                fontBlue20.draw(screen, formatter.format(credits), 630, 292); // crediti
                fontBlue20.draw(screen, formatter.format(matchesCG), 690, 252); // partite classic game
                fontBlue20.draw(screen, formatter.format(matchesSB), 690, 212); // partite space battle
                fontBlue20.draw(screen, formatter.format(wonSB), 690, 172); // vittorie space battle

                // immagini //
                screen.draw(mapAvatar.get(avatar), 461, 513); // avatar
                break;


            // pagina 'missions 1'
            case 26:
                // testi e immagini //
                printCompleteMission(matchesCG);
                break;

            // pagina 'missions 2'
            case 27:
                // testi e immagini //
                printCompleteMission(100000000);
                break;

            // pagina 'missions 3'
            case 28:
                // testi e immagini //
                printCompleteMission(matchesSB);
                break;

            // pagina 'missions 4'
            case 29:
                printCompleteMission(wonSB);
                break;

            // pagina 'missions 5'
            case 30:
                printCompleteMission(points);
                break;

            // pagina 'missions 6'
            case 31:
                printCompleteMission(level);
                break;

            // pagina 'missions 7'
            case 32:
                printCompleteMission(credits);
                break;
        }

        // disegno schermo sovrapposto (chiusura gioco/software infos)
        if (secondScreen) {
            if (open22) screen.draw(img22, 250, 175);
            else if (open23) screen.draw(img23, 250, 175);
        }

        // chiusura screen
        screen.end();
    }

    // GETTER //
    public static int getDiffCG() {
        return diffCG;
    }

    // metodo per rilasciare le risorse
    @Override
    public void dispose() {
        screen.dispose();
        img.dispose();
    }
}
