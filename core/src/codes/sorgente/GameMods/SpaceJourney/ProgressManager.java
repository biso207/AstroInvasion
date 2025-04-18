/*
Astro Invasion - class ProgressManager -
Gestisce i progressi compiuti nella modalità a livelli (galassie sbloccate, accessibilità livelli)
Developed by BIGA©. All rights reserved.
*/

package sorgente.GameMods.SpaceJourney;

import sorgente.DataUserManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProgressManager {
    private final Set<Integer> completedLevels = new HashSet<>(); // contiene i livelli completati
    private final Set<Integer> unlockedGalaxies = new HashSet<>(); // contiene le galassie sbloccate
    private final Map<Integer, LevelState> levelStates = new HashMap<>();
    private final int credits;

    // costruttore
    public ProgressManager() {
        this.credits = (int) DataUserManager.getProgress("credits");
    }

    /// TODO: recuperare una variabile di vittoria partita per settare a COMPLETED il livello appena giocato...
    // metodo per controllare se un livello è stato completato
    public boolean completeLevel(int levelId) {
        if (getLevelState(levelId) == LevelState.UNLOCKED) {
            levelStates.put(levelId, LevelState.COMPLETED);
            return true;
        }
        return false;
    }

    /// TODO: dove si usano i tre metodi sotto bastano delle lambda expressions...
    // metodo per controllare se un livello è sbloccato
    public boolean isLevelUnlocked(int level) {
        return completedLevels.contains(level); // livello 1 è sempre sbloccato
    }

    // metodo per controllare se una galassia è sbloccata
    public boolean isGalaxyUnlocked(int galaxy) {
        return unlockedGalaxies.contains(galaxy);
    }

    // metodo per recuperare lo stato di accessibilità di un livello
    public LevelState getLevelState(int levelId) {
        return levelStates.getOrDefault(levelId, LevelState.LOCKED);
    }

    // metodo per sbloccare una galassia
    public boolean unlockGalaxy(int galaxy) {
        // prezzo sblocco galassia
        int cost = switch (galaxy) {
            case 2 -> 1000;
            case 3 -> 3000;
            case 4 -> 5000;
            case 5 -> 7000;
            default -> 0;
        };

        // sblocco galassia
        if (credits >= cost && !unlockedGalaxies.contains(galaxy)) {
            DataUserManager.setProgress("credits", credits - cost); // aggiornamento crediti utente
            unlockedGalaxies.add(galaxy); // aggiunta alla lista delle galassie sbloccate quella nuova
            return true;
        }

        return false;
    }

    // metodo per sbloccare un livello
    public boolean unlockLevel(int levelId) {
        // costo di sblocco
        int cost = (levelId % 10 == 0) ? 700 : 200; // 200 primi 9, 700 il finale

        // sblocco livello
        if (credits >= cost && getLevelState(levelId) == LevelState.LOCKED) {
            DataUserManager.setProgress("credits", credits - cost); // aggiornamento crediti utente
            levelStates.put(levelId, LevelState.UNLOCKED); // cambio stato accessibilità del livello
            return true;
        }
        return false;
    }
}

