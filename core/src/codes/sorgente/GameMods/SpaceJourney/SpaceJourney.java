/*
Astro Invasion - class SpaceJourney -
Controlla e gestisce la modalità a livelli
Developed by BIGA©. All rights reserved.
*/

package sorgente.GameMods.SpaceJourney;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import sorgente.Main;
import sorgente.UI.Lobby.LobbyManager;
import java.util.ArrayList;
import java.util.List;

public class SpaceJourney {
    private Main game;
    private List<Galaxy> galaxies;
    private int currentGalaxy = 0;
    private int[] unlockCosts = {1000, 3000, 5000, 7000};

    private ProgressManager progressManager;

    // costruttore
    public SpaceJourney(Main game, ProgressManager progressManager) {
        this.game = game;
        this.progressManager = progressManager;
        this.galaxies = new ArrayList<>();

        // init galassie e livelli
        setupGalaxies();
    }

    // metodo per inizializzare livelli e galassie
    private void setupGalaxies() {
        for (int i = 1; i <= 4; i++) {
            List<Level> levels = new ArrayList<>();
            for (int j = 1; j <= 10; j++) {
                int levelId = (i - 1) * 10 + j; // livelli da 1 a 40
                levels.add(new Level(levelId, LevelState.LOCKED)); // tutti i livelli bloccati
            }
            // alla mappa viene aggiunto un'ID e la sua lista di livelli
            galaxies.add(new Galaxy(i, progressManager.isGalaxyUnlocked(i), levels));
        }
    }

    // ********************** //
    // METODI CONTROLLO INPUT //
    // ********************** //

    // metodo per controllare i click del mouse
    public void handleInput(Vector2 touchPos) {
        if (Gdx.input.justTouched()) {
            for (Galaxy galaxy : galaxies) {
                if (isGalaxyClicked(galaxy, touchPos)) {
                    if (galaxy.isUnlocked()) System.out.println("sei entrato nella galassia " + galaxy.getId());
                }
            }

            // controllo dei livelli
            for (Galaxy galaxy : galaxies) {
                for (Level level : galaxy.getLevels()) {
                    if (isLevelClicked(level, touchPos)) {
                        if (level.isUnlocked()) System.out.println("hai cliccato sul livello " + level.getId());
                    }
                }
            }
        }

        // controllo pagina per tornare indietro
        if (isBackButtonClicked(touchPos)) {
            if (currentGalaxy == -1) game.setScreen(new LobbyManager(game));
            else currentGalaxy = -1;
        }
    }

    // metodo per controllare il click della X per chiudere la pagina corrente
    private boolean isBackButtonClicked(Vector2 touchPos) {
        double screenX = touchPos.x;
        double screenY = touchPos.y;

        return (screenX >= 908 && screenX <= 948) && (screenY >= 84 && screenY <= 124);
    }

    // metodo per controllare se l'area di una galassia è stata cliccata
    private boolean isGalaxyClicked(Galaxy g, Vector2 touchPos) {
        // Implementare logica di clic sui range delle galassie
        return false;
    }

    // metodo per controllare se l'area di un livello è stato cliccato
    private boolean isLevelClicked(Level l, Vector2 touchPos) {
        // Implementare logica di clic sui livelli
        return false;
    }
}

