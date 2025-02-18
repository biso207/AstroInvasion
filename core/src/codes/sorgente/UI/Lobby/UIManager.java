/*
Astro Invasion - class AuthManagerUI -
Gestisce le grafiche delle schermate della lobby
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.UI.Lobby;

// import codici e librerie
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.DataUserManager;
import sorgente.Missions.CheckMissions;
import sorgente.Missions.RTG;
import sorgente.ResourceLoader;
import sorgente.GameMods.Spacecraft;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class UIManager implements ResourceLoader {
    // istanza della classe missioni RTG
    private RTG m;

    // dichiarazione icone difficoltà e spunta completamento
    private Texture tickImg, diffCG1, diffCG2, diffCG3, diffSB1, diffSB2, diffSB3;

    // immagini in sovra impressione
    private Texture closeGame, softInfos;

    private BitmapFont fontBlue15;
    private BitmapFont fontBlue20;
    private BitmapFont fontWhite20;
    //private BitmapFont fontRed20;

    // hashmap per le diverse texture
    private final HashMap<Integer, Texture> mapLobby; // schermate lobby
    private final HashMap<Integer, Texture> mapAvatar; // immagini avatar
    private final HashMap<Integer, Spacecraft> mapSpacecrafts; // oggetti navicella

    // arraylist delle pagine secondarie
    private final Set<Integer> listSecondPages = Set.of(10, 11, 12, 13, 14, 15, 16, 17, 24, 25, 26, 27, 28, 29);

    // navicella utente
    Spacecraft selectedSp;

    // formatter per la virgola delle migliaia !in automatico converte l'intero in stringa
    NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

    // costruttore
    public UIManager() {
        this.mapLobby = new HashMap<>();
        this.mapAvatar = new HashMap<>();
        this.mapSpacecrafts = new HashMap<>();
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
            fontWhite20 = new BitmapFont(Gdx.files.internal("font/inter/regular_white_20.fnt")); // inter regular white 20
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

        // immagine spunta per completamento missione o selezione oggetti
        tickImg = new Texture("images/tick.png");
    }

    // metodo per caricare le immagini della Lobby
    public void loadLobbyImages(){
        // "pulsante" raccolta premio
        Texture img_special = new Texture("lobby_screens/_rect_claim_reward_eng.png");
        mapLobby.put(35, img_special);

        // popolamento mappa lobby
        for (int i = 0; i < 30; i++) mapLobby.put(i, new Texture("lobby_screens/lobby (" + i + ").png"));
        // popolamento mappa avatar
        for (int i = 0; i < 20; i++) mapAvatar.put(i, new Texture("images/avatars/av (" + i + ").png"));

        // immagini in sovra impressione
        closeGame = new Texture("secondary_screens/lobby_close_game_eng.png");
        softInfos = new Texture("secondary_screens/lobby_software_info_eng.png");
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
            "images/lasers/laser_centauro.png", "images/lasers/laser_alpha.png"};
        // potenze delle navicelle
        int[][] attributes = {
            {0, 1, 0}, {5, 0, 0}, {1, 0, 0}, {0, 1, 0}, {0, 2, 0}, {10, 0, 0}, {0, 0, 2}, {0, 0, 3}, {0, 3, 0},
            {15, 0, 0}, {0, 1, 1}, {10, 2, 0}, {0, 2, 1}, {20, 0, 0}, {0, 4, 1}, {0, 1, 2}, {0, 2, 2}, {30, 0, 0},
            {0, 1, 4}, {10, 0, 2}, {0, 5, 5}, {50, 5, 0}, {50, 0, 5}, {50, 5, 5}
        };

        // popolamento della mappa navicelle
        for (int i = 0; i < 23; i++) {
            mapSpacecrafts.put(i, new Spacecraft(names[i], imagePaths[i], new Texture(laserPaths[i]), attributes[i][0], attributes[i][1], attributes[i][2]));
        }

        // recupero navicella utente
        Object spacecraft = DataUserManager.getProgress("spacecraft");
        selectedSp = mapSpacecrafts.get((int) spacecraft); // navicella utente

        return selectedSp;
    }

    // **************** //
    // GESTIONE GRAFICA //
    // **************** //

    public String createMissions() {
        // creazione oggetti
        RTG RTG1 = new RTG("Hit", 10, "aliens in a Classic Game match.", "1 Gold Heart", "images/cards/cart1_gold_heart_eng.png");
        RTG RTG2 = new RTG("Win", 1, "Space Battle matches.", "1 Shield", "images/cards/cart2_shield_eng.png");
        RTG RTG3 = new RTG("Earn", 5000, "points in a single\nClassic Game match.", "100 Credits", "images/cards/card_100_coins.png");
        RTG RTG4 = new RTG("Earn", 5, "credits.", "1 Super Laser", "images/cards/cart3_super_laser_eng.png");

        // missione default
        m = RTG1;

        // selezione oggetto in base alla missione corrente
        switch ((int)DataUserManager.getProgress("mission_id")) {
            case 1:
                break;
            case 2:
                m = RTG2;
                break;
            case 3:
                m = RTG3;
                break;
            case 4:
                m = RTG4;
                break;
        }

        return m.printMission((int)DataUserManager.getProgress("num_mission"));
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
        //screen.draw(mapLobby.get(InputManager.page), 0, 0);
        screen.draw(mapLobby.get(29), 0, 0);

        // stampa avatar
        if (!listSecondPages.contains(InputManager.page)) {
            // stampa avatar
            screen.draw(mapAvatar.get((int) DataUserManager.getProgress("avatar")), 870, 557);
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
                // testi //
                fontBlue20.draw(screen, createMissions(), 515, 370); // missione da completare
                fontBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("num_mission")), 565, 407); // numero missione raggiunta
                fontBlue20.draw(screen, m.prize, 725, 272); // premio missione

                // immagini //
                screen.draw(new Texture(m.path), 660, 100);
                break;

            // pagina 'marketplace'
            case 18:
                // testi //
                fontBlue20.draw(screen, formatter.format((int)DataUserManager.getProgress("credits")), 540, 490); // numero totale crediti

                break;

            // pagina 'profile info'
            case 24:
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
                screen.draw(mapAvatar.get((int)DataUserManager.getProgress("avatar")), 461, 513); // avatar
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
        }

        // disegno eventuale schermo sovrapposto (chiusura gioco/software infos)
        if (InputManager.secondScreen) {
            screen.draw(InputManager.open22 ? softInfos : closeGame, 250, 175);
        }
    }
}
