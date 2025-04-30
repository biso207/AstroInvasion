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

public class SpaceJourneyUI implements ResourceLoader {
    // immagini
    private Texture imgGalaxy0, imgGalaxy1, imgGalaxy2, imgGalaxy3, imgGalaxy4;
    private Texture imgCompletedLevelG1, imgCompletedLevelG2, imgCompletedLevelG3, imgCompletedLevelG4;
    private Texture imgLockedLevelG1, imgLockedLevelG2, imgLockedLevelG3, imgLockedLevelG4;
    private Texture imgUnlockedLevelG1, imgUnlockedLevelG2, imgUnlockedLevelG3, imgUnlockedLevelG4;
    private Texture imgFlagSeat, imgNumLevelSeat, closeButton;
    private Texture[] bgs;

    // font
    private BitmapFont fontBoldBlue20, fontBoldWhite60, fontBoldItalicWhite25;

    // testi per le pagine
    private final String[] titles = {"Space Journey", "Fenixia Galaxy", "Malloc Galaxy", "Seraphis Galaxy", "Efron Galaxy"};
    private final String[] subTitles = {
        "“Somewhere, something incredible is waiting to be known”",
        "“What we know is a drop, what we don’t know is an ocean”",
        "“Adventure is worthwhile in itself”",
        "“Per aspera ad astra”",
        "“Never stop exploring”"
    };

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

        // icona bandiera livello corrente
        imgFlagSeat = new Texture("images/space_journey_maps/flag_seat_marker.png");
        // cerchio per il numero del livello
        imgNumLevelSeat = new Texture("images/space_journey_maps/level_circle.png");
        // X per chiudere la pagina
        closeButton = new Texture("images/space_journey_maps/close_button.png");

    }

    // metodo per caricare e creare i font
    @Override
    public void loadFont() {
        // dichiarazione font
        try {
            // blue
            fontBoldBlue20 = new BitmapFont(Gdx.files.internal("font/inter/bold_blue_20.fnt")); // inter-bold blue 20
            // white
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
            fontBoldWhite70.draw(screen, titles[SpaceJourney.numGalaxy], 65, 628);
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
            Level l = new Level(i); // creazione oggetto livello
            //System.out.println(l.getState(numLevel));
            switch (l.getState(numLevel)) {
                case COMPLETED:
                    switch(SpaceJourney.numGalaxy) {
                        case 1 -> screen.draw(imgCompletedLevelG1, X, Y);
                        case 2 -> screen.draw(imgCompletedLevelG2, X, Y);
                        case 3 -> screen.draw(imgCompletedLevelG3, X, Y);
                        case 4 -> screen.draw(imgCompletedLevelG4, X, Y);
                    }
                    break;
                case LOCKED:
                    switch(SpaceJourney.numGalaxy) {
                        case 1 -> screen.draw(imgLockedLevelG1, X, Y);
                        case 2 -> screen.draw(imgLockedLevelG2, X, Y);
                        case 3 -> screen.draw(imgLockedLevelG3, X, Y);
                        case 4 -> screen.draw(imgLockedLevelG4, X, Y);
                    }
                    break;
                case UNLOCKED:
                    switch(SpaceJourney.numGalaxy) {
                        case 1 -> screen.draw(imgUnlockedLevelG1, X, Y);
                        case 2 -> screen.draw(imgUnlockedLevelG2, X, Y);
                        case 3 -> screen.draw(imgUnlockedLevelG3, X, Y);
                        case 4 -> screen.draw(imgUnlockedLevelG4, X, Y);
                    }
                    break;
            }

            // stampa numero livello
            if (SpaceJourney.numGalaxy!=0) {
                screen.draw(imgNumLevelSeat, X2, Y2);
                fontBoldBlue20.draw(screen, String.valueOf(i), X2+9, Y2+30);
            }

            X+=204;
            X2+=204;

            // reset posizione immagini
            if (i==(finish-5)) {
                X=50; Y-=224; // pianeti
                X2=40; Y2-=224; // numero livello
            }
        }
    }

    // metodo per stampare le grafiche
    public void printUI(SpriteBatch screen) {
        createGalaxyUI(screen);
    }
}
