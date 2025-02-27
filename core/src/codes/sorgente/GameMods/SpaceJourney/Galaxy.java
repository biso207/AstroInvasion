/*
Astro Invasion - class Galaxy -
Crea e gestisce gli oggetti che rappresentano una galassia in SpaceJourney
Developed by BIGA©. All rights reserved.
*/

package sorgente.GameMods.SpaceJourney;

import java.util.List;

public class Galaxy {
    private int id;
    private boolean isUnlocked;
    private List<Level> levels;

    public Galaxy(int id, boolean isUnlocked, List<Level> levels) {
        this.id = id;
        this.isUnlocked = isUnlocked;
        this.levels = levels;
    }

    public int getId() {
        return id;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void unlock() {
        isUnlocked = true;
    }

    public List<Level> getLevels() {
        return levels;
    }
}

