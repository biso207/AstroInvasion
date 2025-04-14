/*
Astro Invasion - class SpaceJourney -
Controlla e gestisce la modalità a livelli
Developed by BIGA©. All rights reserved.
*/

package sorgente.GameMods.SpaceJourney;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import sorgente.Main;
import sorgente.Lobby.LobbyManager;
import java.util.ArrayList;
import java.util.List;

public class SpaceJourney implements Screen {
    private final Main game;
    // dichiarazione screen
    private final SpriteBatch screen;

    // soundtrack
    private Music soundtrack;

    private final List<Galaxy> galaxies;
    private int currentGalaxy = 0;
    private int[] unlockCosts = {1000, 3000, 5000, 7000};

    private final ProgressManager progressManager = new ProgressManager();

    private final SpaceJourneyUI ui;

    // costruttore
    public SpaceJourney(Main game) {
        this.game = game;
        // init dello screen
        this.screen = game.screen;

        this.galaxies = new ArrayList<>();

        // init galassie e livelli
        setupGalaxies();

        // setup grafica
        ui = new SpaceJourneyUI(currentGalaxy); /// TODO: recuperare la galassia corrente...

        // musica di sottofondo
        soundtrack = Gdx.audio.newMusic(Gdx.files.internal("sounds/space_journey_sound.mp3")); // file audio
        soundtrack.setLooping(true); // true=loop music; false=no loop
        soundtrack.play(); // avvio musica
    }

    // metodo per inizializzare livelli e galassie
    private void setupGalaxies() {
        for (int i = 1; i <= 4; i++) {
            List<Level> levels = new ArrayList<>();
            for (int j = 1; j <= 10; j++) {
                int levelId = (i - 1) * 10 + j; // livelli da 1 a 40
                levels.add(new Level(levelId)); // tutti i livelli bloccati
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

        // controllo pagina per tornare indietro: 0 => Lobby; 4<=numGalaxy<=1 => currentGalaxy=0
        if (isBackButtonClicked(touchPos)) {
            if (currentGalaxy == 0) {
                soundtrack.stop(); // stop della musica
                game.setScreen(new LobbyManager(game)); // back to lobby
            }
            else currentGalaxy = 0;
        }

        if (isHomeIconClicked(touchPos) && currentGalaxy == 0) {
            soundtrack.stop(); // stop della musica
            game.setScreen(new LobbyManager(game)); // back to lobby
        }
    }

    // metodo per controllare il click della X per chiudere la pagina corrente
    private boolean isBackButtonClicked(Vector2 touchPos) {
        double screenX = touchPos.x;
        double screenY = touchPos.y;

        System.out.println(screenX + " " + screenY);

        return ((screenX >= 904 && screenX <= 936) && (screenY >= 582 && screenY <= 615));
    }

    // metodo per tornare alla lobby cliccando sull'icona della terra
    private boolean isHomeIconClicked(Vector2 touchPos) {
        double screenX = touchPos.x;
        double screenY = touchPos.y;

        return ((screenX >= 25 && screenX <= 122) && (screenY >= 182 && screenY <= 283));
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

    // ****************************** //
    // METODI DELL'INTERFACCIA Screen //
    // ****************************** //

    // aggiornamento grafica
    @Override public void render(float delta) {
        // init screen
        screen.begin();

        // mostra elementi a schermo
        ui.printUI(screen);

        // chiusura screen
        screen.end();
    }

    // metodo usato per il controllo degli input
    @Override public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                float correctedY = Gdx.graphics.getHeight() - screenY;
                Vector2 touchPos = new Vector2(screenX, correctedY);
                handleInput(touchPos);
                return true;
            }
        });
    }

    // spegnimento controllo input
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    // rilascio risorse
    @Override public void dispose() {}

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
}

