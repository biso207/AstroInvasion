/*
Astro Invasion - class Galaxy -
Crea e gestisce gli oggetti che rappresentano una galassia in SpaceJourney
Developed by BIGA©. All rights reserved.
*/

package sorgente.GameMods.SpaceJourney;

import java.util.List;

public class Galaxy {
    private int id;
    private List<Level> levels;

    public Galaxy(int id, List<Level> levels) {
        this.id = id;
        this.levels = levels;
    }

    public int getId() {
        return id;
    }

    public List<Level> getLevels() {
        return levels;
    }
}

