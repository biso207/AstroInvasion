/*
Astro Invasion - class Level -
Crea e gestisce gli oggetti che rappresentano un livello in SpaceJourney
Developed by BIGA©. All rights reserved.
*/

package sorgente.GameMods.SpaceJourney;

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
    public LevelState getState(int numLevel) {
        if (id == numLevel) {
            return LevelState.UNLOCKED; // il livello corrente è Unlocked
        } else if (id < numLevel) {
            return LevelState.COMPLETED; // i livelli precedenti sono Completed
        } else {
            return LevelState.LOCKED; // i livelli successivi sono Locked
        }
    }

    // metodo per settare il livello ad sbloccato
    public void unlock() {
        if (state == LevelState.LOCKED) {
            state = LevelState.UNLOCKED;
        }
    }

    // metodo per settare ad completato un livello
    public void complete() {
        if (state == LevelState.UNLOCKED) {
            state = LevelState.COMPLETED;
        }
    }

    // metodo per recuperare controllare che un livello sia sbloccato
    public boolean isUnlocked() {
        return state != LevelState.LOCKED;
    }
}

