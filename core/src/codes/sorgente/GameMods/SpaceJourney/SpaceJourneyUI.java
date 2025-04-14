/*
Astro Invasion - class SpaceJourneyUI -
Gestisce la grafica della singola galassia
Developed by BIGA©. All rights reserved.
*/

package sorgente.GameMods.SpaceJourney;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.DataUserManager;

public class SpaceJourneyUI {
    // immagini galassie
    private Texture imgGalaxy0, imgGalaxy1, imgGalaxy2, imgGalaxy3, imgGalaxy4;
    private Texture imgCompletedLevelG1, imgCompletedLevelG2, imgCompletedLevelG3, imgCompletedLevelG4;
    private Texture imgLockedLevelG1, imgLockedLevelG2, imgLockedLevelG3, imgLockedLevelG4;
    private Texture imgUnlockedLevelG1, imgUnlockedLevelG2, imgUnlockedLevelG3, imgUnlockedLevelG4;
    private Texture imgFlagSeat;

    private final int numGalaxy, numLevel;

    // costruttore
    SpaceJourneyUI(int numGalaxy) {
        // caricamento risorse in memoria
        loadResources();

        // numero della galassia corrente
        this.numGalaxy = numGalaxy;
        // numero livello raggiunto
        this.numLevel = (int) DataUserManager.getProgress("level");
    }

    // metodo per caricare le risorse
    public void loadResources() {
        // base galassie
        imgGalaxy0 = new Texture("images/space_journey_maps/galaxy0.png");
        imgGalaxy1 = new Texture("images/space_journey_maps/galaxy1.png");
        imgGalaxy2 = new Texture("images/space_journey_maps/galaxy2.png");
        imgGalaxy3 = new Texture("images/space_journey_maps/galaxy3.png");
        imgGalaxy4 = new Texture("images/space_journey_maps/galaxy4.png");

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
    }

    // metodo per creare la grafica di una galassia
    public void createGalaxyUI() {
        int start = numGalaxy*10-9;
        int finish = numGalaxy*10;
        for (int i = start; i <= finish; i++) {
            Level l = new Level(i);
            System.out.println("livello " + i + " stato: " + l.getState(i));
        }
    }

    // metodo per stampare le grafiche
    public void printUI(SpriteBatch screen) {
        switch (numGalaxy) {
            case 0:
                screen.draw(imgGalaxy0, 0,0);
                break;
            case 1:
                screen.draw(imgGalaxy1, 0,0);
                break;
            case 2:
                screen.draw(imgGalaxy2, 0,0);
                break;
            case 3:
                screen.draw(imgGalaxy3, 0,0);
                break;
            case 4:
                screen.draw(imgGalaxy4, 0,0);
                break;
        }

        if (numGalaxy != 0) createGalaxyUI();
    }
}
