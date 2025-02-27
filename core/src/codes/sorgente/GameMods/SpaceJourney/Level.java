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

    public int getId() {
        return id;
    }

    public LevelState getState() {
        return state;
    }

    public void unlock() {
        if (state == LevelState.LOCKED) {
            state = LevelState.UNLOCKED;
        }
    }

    public void complete() {
        if (state == LevelState.UNLOCKED) {
            state = LevelState.COMPLETED;
        }
    }

    public boolean isUnlocked() {
        return state != LevelState.LOCKED;
    }
}

