/*
Astro Invasion - class Level -
Crea e gestisce gli oggetti che rappresentano un livello in SpaceJourney
Developed by BIGA©. All rights reserved.
*/

package sorgente.GameMods.SpaceJourney;

import sorgente.UserData.DataUserManager;

public class Level {
    private final int id;
    private LevelState state;

    // costruttore
    public Level(int id) {
        this.id = id;
    }

    // metodo per recuperare l'id del livello
    public int getId() {
        return id;
    }

    // metodo per recuperare lo stato di un livello
    public LevelState getState() {
        // livello raggiunto
        int currentLevel = (int) DataUserManager.getProgress("level");
        // livello acquistato per essere giocato
        boolean levelBought = (boolean) DataUserManager.getProgress("level_bought");

        // controlli per restituire lo stato
        if (id == currentLevel) {
            if (levelBought) return LevelState.UNLOCKED; // il livello corrente è sbloccato e giocato
            else return LevelState.TO_BUY; // il livello corrente è sbloccato ma da "acquistare" per essere giocato
        } else if (id < currentLevel) {
            return LevelState.COMPLETED; // i livelli precedenti sono completati
        } else {
            return LevelState.LOCKED; // i livelli successivi sono bloccati
        }
    }

    // metodo per settare il livello ad sbloccato
    public void unlock(int credits) {
        DataUserManager.setProgress("level_bought", true); // cambio stato "livello acquistato"
        DataUserManager.setProgress("credits", credits); // aggiornamento crediti utente
    }
}

