package com.biga.astroinvasion;
import java.util.ArrayList;
import java.util.HashMap;

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

    // soundtrack
    Music soundtrack;

    // recupero nickname utente
    private final String nicknameInput = LogInSignUp.nickname;

    // dichiarazione immagini lobby
    Texture img, img1, img2, img3, img4, img5, img6, img7,
        img8, img9,img10, img11, img12, img13, img14, img15,
        img16, img17, img18, img19, img20, img21, img22, img23, img24, img25, img_special;

    // dichiarazione immagini avatar
    Texture av1, av2, av3, av4, av5, av6, av7, av8, av9, av10,
        av11, av12, av13, av14, av15, av16, av17, av18, av19, av20;

    // dichiarazione variabili attributi utente
    int avatar, credits, diffCG, diffSB, idMission, level,
    movType, shotType, spacecraft, numDoublePoints, numGoldHeart, numShield,
    numSuperLaser, mission, wonSbRtg, matchesCG, matchesSB, consWonSB, wonSB, points;

    boolean isRtgComplete;

    /*
     previousState serve a memorizzare l'ultima pagina aperta.
     Ciò permette di ritornare alla pagina precedente dopo aver chiuso la pagina delle istruzioni/impostazioni
    */
    int state, previousState;

    // arraylist delle pagine secondarie
    ArrayList<Integer> listSecondPages = new ArrayList<>();

    // hashmap per mappare diversi elementi
    HashMap<Integer, Texture> mapLobby = new HashMap<>(); // hashmap schermate lobby
    HashMap<Integer, Texture> mapAvatar = new HashMap<>(); // hashmap immagini avatar

    boolean secondScreen, open22, open23;

    // costruttore
    public Lobby(Main game) {
        this.game = game;
        // init dello screen
        this.screen = game.screen;

        // set immagine di default (classic game)
        state = previousState = 6;

        // init del secondo "screen", dello screen software.infos e close.game a false
        secondScreen = open22 = open23 = false;

        // caricamento immagini
        loadImages();

        // caricamento font
        loadFont();

        // recupero progressi utente
        readFiles();

        // musica di sottofondo
        soundtrack = Gdx.audio.newMusic(Gdx.files.internal("sounds/lobby_sound.ogg")); // file audio
        soundtrack.setLooping(true); // true=loop music; false=no loop
        soundtrack.play(); // avvio musica
    }

    // -------------- //
    // GESTIONE INPUT //
    // -------------- //

    // classe interna per gestire gli input da mouse e tastiera
    private class MyInputProcessor extends InputAdapter {
        @Override
        public boolean keyDown(int keycode) {
            // click tasto esc
            if (keycode == Input.Keys.ESCAPE && (state!=7 && state!=21 && !open22 && !open23 && state!=24 && state!=25)) {
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
            Lo state deve essere diverso da certe pagine per non generare l'apertura
            di altre pagine dove non è possibile e poter cambiare le schermate della Lobby.
            Esempio: l'utente NON può aprire la pagina 'classic game' dalla pagina 'instructions'
            */
            if (state!=7 && state!=21 && state!=24 && state!=25 && !open22 && !open23) {
                // pagina 6 => 'classic game'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 180 && screenY <= 220)) {
                    state = 6;
                }
                // pagina 13 => 'space battle'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 230 && screenY <= 270)) {
                    state = 13;
                }
                // pagina 14 => 'space journey'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 280 && screenY <= 320)) {
                    state = 14;
                }
                // pagina 12 => 'road to glory'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 330 && screenY <= 370)) {
                    state = 12;
                }
                // pagina 15 => 'navicelle 1'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 380 && screenY <= 420)) {
                    state = 15;
                }
                // pagina 11 => 'marketplace'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 480 && screenY <= 520)) {
                    state = 11;
                }
                // cambio pagina (1-5) => 'avatar/spacecraft/ 1->5'
                if ((screenX >= 873 && screenX <=913) && (screenY >= 553 && screenY <=593)) {
                    if ((state >=1 && state < 5) || (state >= 15 && state < 20)) state++;
                }
                // cambio pagina (5-1) => 'avatar/spacecraft/ 5->1'
                if ((screenX >= 343 && screenX <=373) && (screenY >= 553 && screenY <=593)) {
                    if ((state <= 5 && state>1) || (state <= 20 && state>15)) state--;
                }
                // pagina 7 => 'instructions'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 530 && screenY <= 570)) {
                    previousState = state;
                    state = 7;
                }
                // pagine 21 => 'settings'
                if ((screenX >= 50 && screenX <=90) && (screenY >= 580 && screenY <=620)) {
                    previousState = state;
                    state = 21;
                }
                // pagina 24 => 'difficulty info classic game'
                if ((screenX >= 887 && screenX <=917) && (screenY >= 203 && screenY <=233)) {
                    previousState = state;
                    state = 24;
                }
                // pagina 25 => 'profile info'
                if ((screenX >= 870 && screenX <=950) && (screenY >= 66 && screenY <=146)) {
                    previousState = state;
                    state = 25;
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
            }

            // chiusura pagina instruction/settings/profile info&difficulty/
            if ((state==7 || state==21 || state==24 || state==25) && (screenX >= 908 && screenX <= 948) && (screenY >= 84 && screenY <= 124)) {
                state = previousState;
            }
            // pagina 1 => 'avatar 1'
            if (state == 25 && (screenX >= 459 && screenX <=537) && (screenY >= 110 && screenY <=188)) {
                state = 1;
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
        } catch (Exception e) {
            font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("#151A3B")); // colore blu
        }
    }

    // metodo per caricare le immagini che rappresentano lo schermo
    public void loadImages(){
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
        img16 = new Texture("lobby_images/lobby_spacecrafts_groupEfron_eng.png");
        img17 = new Texture("lobby_images/lobby_spacecrafts_groupFenixia_eng.png");
        img18 = new Texture("lobby_images/lobby_spacecrafts_groupMalloc_eng.png");
        img19 = new Texture("lobby_images/lobby_spacecrafts_groupPhoenix_eng.png");
        img20 = new Texture("lobby_images/lobby_spacecrafts_special_group_eng.png");
        img21 = new Texture("lobby_images/lobby_settings_eng.png");
        img22 = new Texture("lobby_images/lobby_software_info_eng.png");
        img23 = new Texture("lobby_images/lobby_close_game_eng.png");
        img24 = new Texture("lobby_images/lobby_difficulty_info_eng.png");
        img25 = new Texture("lobby_images/lobby_profile_info_eng.png");
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
        mapping(mapLobby, img1, img2, img3, img4, img5, img6, img7, img8, img9, img10, img11, img12, img13, img14, img15, img16, img17, img18, img19, img20);
        mapLobby.put(21, img21);
        mapLobby.put(24, img24);
        mapLobby.put(25, img25);
        mapLobby.put(30, img_special);

        // inserimento pagine secondarie nell'arraylist
        listSecondPages.add(7);
        listSecondPages.add(21);
        listSecondPages.add(24);
        listSecondPages.add(25);

        // mappatura avatar
        mapping(mapAvatar, av1, av2, av3, av4, av5, av6, av7, av8, av9, av10, av11, av12, av13, av14, av15, av16, av17, av18, av19, av20);
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
        screen.draw(mapLobby.get(state), 0, 0);

        // disegno schermo sovrapposto (chiusura gioco/software infos)
        if (secondScreen) {
            if (open22) screen.draw(img22, 250, 175);
            else if (open23) screen.draw(img23, 250, 175);
        }

        // TESTI //
        // scritte pagina info profilo
        if (state==25) {
            // scritte a sx
            fontBlue20.draw(screen, LogInSignUp.nickname, 172, 412); // nickname
            fontBlue20.draw(screen, LogInSignUp.password, 172, 372); // password

            // scritte a dx
            fontBlue20.draw(screen, String.valueOf(points), 620, 412);// punti
            fontBlue20.draw(screen, String.valueOf(level), 610, 372);// livello
            fontBlue20.draw(screen, String.valueOf(mission), 630, 332);// numero missione
            fontBlue20.draw(screen, String.valueOf(credits), 630, 292);// crediti
            fontBlue20.draw(screen, String.valueOf(matchesCG), 690, 252); // partite classic game
            fontBlue20.draw(screen, String.valueOf(matchesSB), 690, 212); // partite space battle
            fontBlue20.draw(screen, String.valueOf(wonSB), 690, 172);// vittorie space battle

            // stampa avatar
            screen.draw(mapAvatar.get(avatar), 461, 513);
        }
        else if (!listSecondPages.contains(state)) {
            // stampa avatar
            screen.draw(mapAvatar.get(avatar), 870, 557);
        }

        screen.end();
    }

    // metodo per rilasciare le risorse
    @Override
    public void dispose() {
        screen.dispose();
        img.dispose();
    }
}
