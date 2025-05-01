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
    // immagini
    private Texture imgGalaxy0, imgGalaxy1, imgGalaxy2, imgGalaxy3, imgGalaxy4;
    private Texture imgCompletedLevelG1, imgCompletedLevelG2, imgCompletedLevelG3, imgCompletedLevelG4;
    private Texture imgLockedLevelG1, imgLockedLevelG2, imgLockedLevelG3, imgLockedLevelG4;
    private Texture imgUnlockedLevelG1, imgUnlockedLevelG2, imgUnlockedLevelG3, imgUnlockedLevelG4;
    private Texture imgFlagSeat, imgNumLevelSeat, closeButton, priceRect;
    private Texture[] bgs;

    // font
    private BitmapFont fontBoldBlue20, fontBoldWhite20, fontBoldWhite60, fontBoldItalicWhite25;

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
    private final String[] nameGuardians = {
        "Kaelor — Guardian of Fenixia",
        "Drelor — Guardian of Malloc",
        "Varyn — Guardian of Seraphis",
        "Tessar — Guardian of Efron"
    };
    private final String[] loreGuardians = {
        "He who burns with the fire of primordial stars. Keeper of cosmic rebirth.",
        "Ancient sentinel of a forgotten world, he watches over the hidden forces that stir beneath Malloc’s silence.",
        "A forgotten angel among the stars, he brings both light and judgment. Beautiful and deadly.",
        "The weaver of orbits. No light escapes his control."
    };
    private final String[] messageGuardians = {
        "From ashes you came, to ashes you shall return. Face the fire of eternity.",
        "You walk a path buried beneath centuries. Speak your purpose, or turn away.",
        "You seek light, but light burns those unworthy. Step into judgment.",
        "I do not strike with rage... I erase with purpose."
    };

    // mappa per le immagini dei livelli
    private Map<LevelState, List<Texture>> imagesByState;

    // numero livello raggiunto
    private final int numLevel;

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
        imgGalaxy0 = new Texture("images/space_journey_maps/galaxy0.png");
        imgGalaxy1 = new Texture("images/space_journey_maps/galaxy1.png");
        imgGalaxy2 = new Texture("images/space_journey_maps/galaxy2.png");
        imgGalaxy3 = new Texture("images/space_journey_maps/galaxy3.png");
        imgGalaxy4 = new Texture("images/space_journey_maps/galaxy4.png");
        bgs = new Texture[] {imgGalaxy0, imgGalaxy1, imgGalaxy2, imgGalaxy3, imgGalaxy4};

        // icone livelli galassie
        imgCompletedLevelG1 = new Texture("images/space_journey_maps/level_g1_completed.png");
        imgCompletedLevelG2 = new Texture("images/space_journey_maps/level_g2_completed.png");
        imgCompletedLevelG3 = new Texture("images/space_journey_maps/level_g3_completed.png");
        imgCompletedLevelG4 = new Texture("images/space_journey_maps/level_g4_completed.png");

        imgLockedLevelG1 = new Texture("images/space_journey_maps/level_g1_locked.png");
        imgLockedLevelG2 = new Texture("images/space_journey_maps/level_g2_locked.png");
        imgLockedLevelG3 = new Texture("images/space_journey_maps/level_g3_locked.png");
        imgLockedLevelG4 = new Texture("images/space_journey_maps/level_g4_locked.png");

        imgUnlockedLevelG1 = new Texture("images/space_journey_maps/level_g1_unlocked.png");
        imgUnlockedLevelG2 = new Texture("images/space_journey_maps/level_g2_unlocked.png");
        imgUnlockedLevelG3 = new Texture("images/space_journey_maps/level_g3_unlocked.png");
        imgUnlockedLevelG4 = new Texture("images/space_journey_maps/level_g4_unlocked.png");

        // caricamento mappa con le immagini dei livelli
        imagesByState = Map.of(
            LevelState.COMPLETED, List.of(imgCompletedLevelG1, imgCompletedLevelG2, imgCompletedLevelG3, imgCompletedLevelG4),
            LevelState.LOCKED, List.of(imgLockedLevelG1, imgLockedLevelG2, imgLockedLevelG3, imgLockedLevelG4),
            LevelState.UNLOCKED, List.of(imgUnlockedLevelG1, imgUnlockedLevelG2, imgUnlockedLevelG3, imgUnlockedLevelG4)
        );

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
            fontBoldWhite60 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_60.fnt")); // inter-bold white 60
            fontBoldItalicWhite25 = new BitmapFont(Gdx.files.internal("font/inter/bold_italic_white_25.fnt")); // inter-bold-italic white 25
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
            switch (numLevel/10+1) {
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
            LevelState state = l.getState(numLevel); // stato livello
            int galaxy = SpaceJourney.numGalaxy; // galassia corrente

            if (galaxy != 0) {
                // disegno immagine livello in base alla galassia e allo stato
                screen.draw(imagesByState.get(state).get(galaxy - 1), X, Y);

                // prezzo sblocco livello
                if (i==numLevel && state==LevelState.TO_BUY) { // raggiunto ma da pagare
                    // stampa rettangolo
                    screen.draw(priceRect, X2+50, Y2);
                    // stampa prezzo
                    fontBoldWhite20.draw(screen, formatter.format(numLevel* 100L), X2+60, Y2+28);
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

    // metodo per stampare le grafiche
    public void printUI(SpriteBatch screen) {
        createGalaxyUI(screen);
    }
}
