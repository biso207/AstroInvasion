/*
Astro Invasion - class Level -
Crea e gestisce gli oggetti che rappresentano un livello in SpaceJourney
Developed by BIGA©. All rights reserved.
*/

package sorgente.GameMods.SpaceJourney;

public class Level {
    private int id;
    private LevelState state;

    public Level(int id, LevelState state) {
        this.id = id;
        this.state = state;
    }

    // metodo per recuperare l'id del livello
    public int getId() {
        return id;
    }

    // metodo per recuperare lo stato di un livello
    public LevelState getState() {
        return state;
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

