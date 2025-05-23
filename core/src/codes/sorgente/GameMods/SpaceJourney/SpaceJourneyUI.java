/*
Astro Invasion - class SpaceJourneyUI -
Gestisce la grafica della singola galassia
Developed by BIGA©. All rights reserved.
*/

package sorgente.GameMods.SpaceJourney;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.DataUserManager;
import sorgente.Main;
import sorgente.ResourceLoader;

import javax.xml.crypto.Data;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SpaceJourneyUI implements ResourceLoader {
    private Texture imgFlagSeat, imgNumLevelSeat, closeButton, priceRect, buyLevelImg,
        diffCG1, diffCG2, diffCG3, diffSB1, diffSB2, diffSB3, btnHoverL, btnHoverR;
    private Texture[] bgs, infoLevels, imgButtonsStartHover;

    // font
    private BitmapFont fontBoldBlue20, fontBoldWhite20, fontBoldWhite25, fontBoldWhite35, fontBoldWhite60,
        fontBoldWhite60_1, fontBoldItalicWhite25, fontBoldGreen25, fontBoldRed25;

    // formatter per la virgola delle migliaia !in automatico converte l'intero in stringa
    private final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

    // testi per le pagine
    private final String[] titles = {"Space Journey", "Fenixia Galaxy", "Malloc Galaxy", "Seraphis Galaxy", "Efron Galaxy"};
    private final String[] subTitles = {
        "“Somewhere, something incredible is waiting to be known”",
        "“What we know is a drop, what we don’t know is an ocean”",
        "“Adventure is worthwhile in itself”",
        "“Per aspera ad astra”",
        "“Never stop exploring”"
    };

    // mappa per le immagini dei livelli
    private Map<LevelState, List<Texture>> imagesByState;

    // numero livello raggiunto
    private final int numLevel;

    // livelli in space battle
    private final List<Integer> listSB = List.of(2, 4, 6, 8,
        12, 14, 16, 18,
        22, 24, 26, 28,
        32, 34, 36, 38);

    // costruttore
    SpaceJourneyUI() {
        // caricamento risorse in memoria
        loadImages(); // immagini
        loadFont(); // font

        // numero livello raggiunto
        this.numLevel = (int) DataUserManager.getProgress("level");
    }

    @Override
    // metodo per caricare le risorse
    public void loadImages() {
        // base galassie
        Texture imgGalaxy0 = new Texture("images/space_journey_maps/galaxy0.png");
        Texture imgGalaxy1 = new Texture("images/space_journey_maps/galaxy1.png");
        Texture imgGalaxy2 = new Texture("images/space_journey_maps/galaxy2.png");
        Texture imgGalaxy3 = new Texture("images/space_journey_maps/galaxy3.png");
        Texture imgGalaxy4 = new Texture("images/space_journey_maps/galaxy4.png");
        // popolamento array
        bgs = new Texture[] {imgGalaxy0, imgGalaxy1, imgGalaxy2, imgGalaxy3, imgGalaxy4};

        // risorse rettangolo info livello //
        // immagini info livello
        Texture infoLevel1 = new Texture("images/space_journey_maps/info_levels/info_level_G1.png");
        Texture infoLevel2 = new Texture("images/space_journey_maps/info_levels/info_level_G2.png");
        Texture infoLevel3 = new Texture("images/space_journey_maps/info_levels/info_level_G3.png");
        Texture infoLevel4 = new Texture("images/space_journey_maps/info_levels/info_level_G4.png");
        infoLevels = new Texture[] {infoLevel1, infoLevel2, infoLevel3, infoLevel4};
        // pulsanti avvio hover
        Texture btnStartHover1 = new Texture ("images/space_journey_maps/info_levels/start_level_g1_hover.png");
        Texture btnStartHover2 = new Texture ("images/space_journey_maps/info_levels/start_level_g2_hover.png");
        Texture btnStartHover3 = new Texture ("images/space_journey_maps/info_levels/start_level_g3_hover.png");
        Texture btnStartHover4 = new Texture ("images/space_journey_maps/info_levels/start_level_g4_hover.png");
        imgButtonsStartHover = new Texture[] {btnStartHover1, btnStartHover2, btnStartHover3, btnStartHover4};

        // immagine per lo sblocco di un livello
        buyLevelImg = new Texture("images/space_journey_maps/info_levels/buy_level.png");

        // pulsanti hover per sbloccare il livello
        btnHoverL = new Texture("images/btns_hover/hover_btn8.png");
        btnHoverR = new Texture("images/btns_hover/hover_btn9.png");

        // livelli galassie //
        // completati
        Texture imgCompletedLevelG1 = new Texture("images/space_journey_maps/level_g1_completed.png");
        Texture imgCompletedLevelG2 = new Texture("images/space_journey_maps/level_g2_completed.png");
        Texture imgCompletedLevelG3 = new Texture("images/space_journey_maps/level_g3_completed.png");
        Texture imgCompletedLevelG4 = new Texture("images/space_journey_maps/level_g4_completed.png");
        // bloccati
        Texture imgLockedLevelG1 = new Texture("images/space_journey_maps/level_g1_locked.png");
        Texture imgLockedLevelG2 = new Texture("images/space_journey_maps/level_g2_locked.png");
        Texture imgLockedLevelG3 = new Texture("images/space_journey_maps/level_g3_locked.png");
        Texture imgLockedLevelG4 = new Texture("images/space_journey_maps/level_g4_locked.png");
        // sbloccati
        Texture imgUnlockedLevelG1 = new Texture("images/space_journey_maps/level_g1_unlocked.png");
        Texture imgUnlockedLevelG2 = new Texture("images/space_journey_maps/level_g2_unlocked.png");
        Texture imgUnlockedLevelG3 = new Texture("images/space_journey_maps/level_g3_unlocked.png");
        Texture imgUnlockedLevelG4 = new Texture("images/space_journey_maps/level_g4_unlocked.png");

        // caricamento mappa con le immagini dei livelli
        imagesByState = Map.of(
            LevelState.COMPLETED, List.of(imgCompletedLevelG1, imgCompletedLevelG2, imgCompletedLevelG3, imgCompletedLevelG4),
            LevelState.LOCKED, List.of(imgLockedLevelG1, imgLockedLevelG2, imgLockedLevelG3, imgLockedLevelG4),
            LevelState.UNLOCKED, List.of(imgUnlockedLevelG1, imgUnlockedLevelG2, imgUnlockedLevelG3, imgUnlockedLevelG4),
            LevelState.TO_BUY, List.of(imgUnlockedLevelG1, imgUnlockedLevelG2, imgUnlockedLevelG3, imgUnlockedLevelG4)
        );

        // icona difficoltà classic game
        diffCG1 = new Texture("images/space_journey_maps/info_levels/diff1_classicgame_mini.png");
        diffCG2 = new Texture("images/space_journey_maps/info_levels/diff2_classicgame_mini.png");
        diffCG3 = new Texture("images/space_journey_maps/info_levels/diff3_classicgame_mini.png");
        // icona difficoltà space battle
        diffSB1 = new Texture("images/space_journey_maps/info_levels/diff1_spacebattle_mini.png");
        diffSB2 = new Texture("images/space_journey_maps/info_levels/diff2_spacebattle_mini.png");
        diffSB3 = new Texture("images/space_journey_maps/info_levels/diff3_spacebattle_mini.png");

        // icona bandiera livello corrente
        imgFlagSeat = new Texture("images/space_journey_maps/flag_seat_marker.png");
        // cerchio per il numero del livello
        imgNumLevelSeat = new Texture("images/space_journey_maps/level_circle.png");
        // X per chiudere la pagina
        closeButton = new Texture("images/space_journey_maps/close_button.png");
        // rettangolo prezzo livello
        priceRect = new Texture("images/space_journey_maps/price_rect.png");
    }

    // metodo per caricare e creare i font
    @Override
    public void loadFont() {
        // dichiarazione font
        try {
            // blue
            fontBoldBlue20 = new BitmapFont(Gdx.files.internal("font/inter/bold_blue_20.fnt")); // inter-bold blue 20
            // white
            fontBoldWhite20 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_20.fnt")); // inter-bold white 20
            fontBoldWhite25 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_25.fnt")); // inter-bold white 20
            fontBoldWhite35 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_35.fnt")); // inter-bold white 25
            fontBoldWhite60 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_60.fnt")); // inter-bold white 60
            fontBoldWhite60_1 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_60_1.fnt")); // inter-bold white 60 NO Shadow
            fontBoldItalicWhite25 = new BitmapFont(Gdx.files.internal("font/inter/bold_italic_white_25.fnt")); // inter-bold-italic white 25
            // green
            fontBoldGreen25 = new BitmapFont(Gdx.files.internal("font/inter/bold_green_25.fnt")); // inter-bold green 25
            // red
            fontBoldRed25 = new BitmapFont(Gdx.files.internal("font/inter/bold_red_25.fnt")); // inter-bold red 25
        } catch (Exception e) {
            // dichiarazione font
            BitmapFont font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("#151A3B")); // colore blu
        }
    }

    // metodo per creare la grafica di una galassia
    public void createGalaxyUI(SpriteBatch screen) {
        int start = SpaceJourney.numGalaxy*10-9;
        int finish = SpaceJourney.numGalaxy*10;

        // stampa titolo e sotto-titolo
        screen.draw(bgs[SpaceJourney.numGalaxy], 0,0);
        if (SpaceJourney.numGalaxy!=0) {
            fontBoldWhite60.draw(screen, titles[SpaceJourney.numGalaxy], 65, 628);
            fontBoldItalicWhite25.draw(screen, subTitles[SpaceJourney.numGalaxy], 64, 535);
        }
        else {
            // icona bandiera galassia corrente
            switch ((int) Math.ceil((double) numLevel / 10)) {
                case 1 -> screen.draw(imgFlagSeat, 430, 170);
                case 2 -> screen.draw(imgFlagSeat, 775, 185);
                case 3 -> screen.draw(imgFlagSeat, 800, 455);
                case 4 -> screen.draw(imgFlagSeat, 300, 430);
            }
            // icona numero galassia
            screen.draw(imgNumLevelSeat, 350, 130); // g1
            screen.draw(imgNumLevelSeat, 700, 150); // g2
            screen.draw(imgNumLevelSeat, 725, 415); // g3
            screen.draw(imgNumLevelSeat, 230, 400); // g4
            // numero galassia
            fontBoldBlue20.draw(screen, "1", 366, 160);
            fontBoldBlue20.draw(screen, "2", 715, 180);
            fontBoldBlue20.draw(screen, "3", 740, 445);
            fontBoldBlue20.draw(screen, "4", 243, 430);
        }

        // pulsante per chiudere pagina
        screen.draw(closeButton, 908, 576);

        // posizione iniziale immagine livello e icona numero livello
        int X=50, Y=343;
        int X2=40, Y2=400;

        for (int i = start; i <= finish; i++) {
            Level l = new Level(i); // crea il livello
            LevelState state = l.getState(); // stato livello
            int galaxy = SpaceJourney.numGalaxy; // galassia corrente

            if (galaxy != 0) {
                // disegno immagine livello in base alla galassia e allo stato
                screen.draw(imagesByState.get(state).get(galaxy - 1), X, Y);

                // prezzo sblocco livello
                if (i==numLevel && l.getState()==LevelState.TO_BUY) { // raggiunto ma da pagare
                    // stampa rettangolo
                    screen.draw(priceRect, X2+40, Y2);
                    // stampa prezzo
                    fontBoldWhite20.draw(screen, formatter.format(numLevel* 100L), X2+50, Y2+28);
                }

                // stampa numero livello
                screen.draw(imgNumLevelSeat, X2, Y2);
                fontBoldBlue20.draw(screen, String.valueOf(i), X2 + 9, Y2 + 30);
            }

            // aggiorna posizione
            X += 204;
            X2 += 204;
            // seconda riga della pagina
            if (i == (finish - 5)) {
                X = 50; Y -= 224;
                X2 = 40; Y2 -= 224;
            }
        }
    }

    // metodo per stampare le info di un livello
    public void infoLevel(SpriteBatch screen) {
        // sfondo base
        screen.draw(infoLevels[SpaceJourney.numGalaxy-1], 143, 100);

        if (SpaceJourney.startLevelHover) screen.draw(imgButtonsStartHover[SpaceJourney.numGalaxy-1], 420, 169);

        // testi //
        fontBoldWhite60_1.draw(screen, "Level " + numLevel, 194, 560); // numero livello
        fontBoldWhite35.draw(screen, "START", 443, 208); // testo avvio partita

        // tipologia gioco
        if (listSB.contains(numLevel)) {
            fontBoldWhite25.draw(screen, "Space Battle", 223, 445);

            // icona difficoltà
            switch ((int) Math.ceil((double) numLevel / 10)) {
                case 1:
                    screen.draw(diffSB1, 404 ,421);
                    break;
                case 2, 3:
                    screen.draw(diffSB2, 404 ,421);
                    break;
                case 4:
                    screen.draw(diffSB3, 404 ,421);
                    break;
            }

            // missione livello
            fontBoldWhite25.draw(screen, "Hit him " + numLevel + " times", 560, 433);
        }
        else {
            fontBoldWhite25.draw(screen, "Classic Game", 223, 445);

            // icona difficoltà
            switch ((int) Math.ceil((double) numLevel / 10)) {
                case 1:
                    screen.draw(diffCG1, 404 ,421);
                    break;
                case 2, 3:
                    screen.draw(diffCG2, 404 ,421);
                    break;
                case 4:
                    screen.draw(diffCG3, 404 ,421);
                    break;
            }

            // missione livello
            fontBoldWhite25.draw(screen, "Kill " + (numLevel*10) + " aliens", 560, 433);
        }
    }

    // metodo per stampare le informazione di sblocco di un livello
    public void buyLevel(SpriteBatch screen) {
        int currentCredits = (int) DataUserManager.getProgress("credits");
        // sfondo base
        screen.draw(buyLevelImg, 250, 175);

        // prezzo livello
        boolean higherPrice = currentCredits>=numLevel* 100L;
        BitmapFont font = higherPrice ? fontBoldGreen25 : fontBoldRed25;
        font.draw(screen, currentCredits + "/" + formatter.format(numLevel* 100L), 390, 347);

        // stampa pulsanti hover
        int price = 100*numLevel; // prezzo del livello
        if (SpaceJourney.isBtnLHover && (currentCredits-price>=0)) screen.draw(btnHoverL, 277, 217);
        else if (SpaceJourney.isBtnRHover) screen.draw(btnHoverR, 519, 217);

        // scritte pulsanti
        fontBoldWhite60_1.draw(screen, "YES", 320, 280);
        fontBoldWhite60_1.draw(screen, "NO", 577, 280);
    }

    // metodo per stampare le grafiche
    public void printUI(SpriteBatch screen) {
        createGalaxyUI(screen);

        // info livello
        if (SpaceJourney.infoLevel) infoLevel(screen);
        // acquisto livello
        if (SpaceJourney.buyLevel) buyLevel(screen);
    }

    // metodo per rilasciare le risorse
    public void dispose() {
        // dispose background textures
        for (Texture texture : bgs) {
            texture.dispose();
        }

        // dispose info livelli
        for (Texture texture : infoLevels) {
            texture.dispose();
        }

        // dispose button hover textures
        for (Texture texture : imgButtonsStartHover) {
            texture.dispose();
        }

        // dispose level state textures
        for (List<Texture> textures : imagesByState.values()) {
            for (Texture texture : textures) {
                texture.dispose();
            }
        }

        // dispose individual textures
        imgFlagSeat.dispose();
        imgNumLevelSeat.dispose();
        closeButton.dispose();
        priceRect.dispose();

        // dispose fonts
        fontBoldBlue20.dispose();
        fontBoldWhite20.dispose();
        fontBoldWhite25.dispose();
        fontBoldWhite35.dispose();
        fontBoldWhite60.dispose();
        fontBoldWhite60_1.dispose();
        fontBoldItalicWhite25.dispose();
        fontBoldGreen25.dispose();
        fontBoldRed25.dispose();
    }
}
