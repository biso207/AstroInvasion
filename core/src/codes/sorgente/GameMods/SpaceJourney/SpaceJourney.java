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
import sorgente.DataUserManager;
import sorgente.Lobby.InputManager;
import sorgente.Main;
import sorgente.Lobby.LobbyManager;

import javax.crypto.spec.PSource;
import java.util.ArrayList;
import java.util.List;

public class SpaceJourney implements Screen, InputProcessor {
    private final Main game;
    // dichiarazione screen
    private final SpriteBatch screen;

    // soundtrack
    private Music soundtrack;

    protected static List<Galaxy> galaxies;
    protected static int numGalaxy = 0;

    private final ProgressManager progressManager = new ProgressManager();

    // istanza classe della grafica
    private final SpaceJourneyUI ui;

    // livello raggiunto
    private final int numLevel = (int) DataUserManager.getProgress("level");

    // costruttore
    public SpaceJourney(Main game) {
        this.game = game;
        // init dello screen
        this.screen = game.screen;

        // init lista galassie
        galaxies = new ArrayList<>();

        // init galassie e livelli
        setupGalaxies();

        // setup grafica
        ui = new SpaceJourneyUI();

        // musica di sottofondo
        soundtrack = Gdx.audio.newMusic(Gdx.files.internal("sounds/space_journey_sound.mp3")); // file audio
        soundtrack.setLooping(true); // true=loop music; false=no loop
        soundtrack.setVolume(InputManager.musicPercent);
        soundtrack.play(); // avvio musica
    }

    // metodo per inizializzare livelli e galassie
    private void setupGalaxies() {
        for (int i = 1; i <= 4; i++) {
            // lista per 10 livelli per ogni galassia
            List<Level> levels = new ArrayList<>();
            for (int j = 1; j <= 10; j++) {
                int levelId = (i - 1) * 10 + j; // id da 1 a 40
                // aggiunta di 10 livelli alla lista
                levels.add(new Level(levelId));
            }
            // alla mappa viene aggiunto un oggetto Galaxy con un'ID e la sua lista di 10 livelli
            galaxies.add(new Galaxy(i, levels));
        }
    }

    // ********************** //
    // METODI CONTROLLO INPUT //
    // ********************** //
    // metodo per controllare se l'area di una galassia è stata cliccata
    private boolean isGalaxyClicked(Galaxy g, double screenX, double screenY) {
        return switch (g.getId()) {
            case 1 -> ((screenX >= 360 && screenX <= 449) && (screenY >= 93 && screenY <= 185));
            case 2 -> ((screenX >= 707 && screenX <= 811) && (screenY >= 127 && screenY <= 201));
            case 3 -> ((screenX >= 736 && screenX <= 833) && (screenY >= 371 && screenY <= 471));
            case 4 -> ((screenX >= 241 && screenX <= 344) && (screenY >= 361 && screenY <= 445));
            default -> false;
        };
    }

    // metodo per controllare se l'area di un livello è stato cliccato
    private boolean isLevelClicked(Level l, double screenX, double screenY) {
        System.out.println(l.getId() + ": " + l.getState());
        return false;
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    // metodo per rilevare il click da tastiera
    @Override public boolean keyTyped(char character) {
        return true;
    }

    // metodo per controllare i click del mouse
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenY = Gdx.graphics.getHeight() - screenY;

        System.out.println("x:" + screenX + " y:" + screenY);
        // click per apertura di una galassia
        for (Galaxy galaxy : galaxies) {
            if (isGalaxyClicked(galaxy, screenX, screenY)) {
                if ((numLevel/10+1)>=galaxy.getId()) numGalaxy = galaxy.getId();
            }
        }

        // click su un livello => il for è solo per i 10 livelli della galassia aperta e se ci si trova in una galassia
        if (numGalaxy>0) {
            for (Level level : galaxies.get(numGalaxy - 1).getLevels()) {
                if (isLevelClicked(level, screenX, screenY)) {
                    if (level.isUnlocked()) System.out.println("level unlocked: " + level.getId());
                }
            }
        }

        // back to lobby cliccando l'icona della terra
        if (((screenX >= 25 && screenX <= 122) && (screenY >= 182 && screenY <= 283)) && numGalaxy == 0) {
            soundtrack.stop(); // stop della musica
            game.setScreen(new LobbyManager(game)); // back to lobby
        }

        // controllo click della X: da 0 a back to lobby; da 4<=numGalaxy<=1 a mapGalaxies (0)
        if (((screenX >= 900 && screenX <= 940) && (screenY >= 577 && screenY <= 617))) {
            if (numGalaxy == 0) {
                soundtrack.stop(); // stop della musica
                game.setScreen(new LobbyManager(game)); // back to lobby
            }
            else numGalaxy = 0;
        }

        return true;
    }

    // altri metodi
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

    // ****************************** //
    // METODI DELL'INTERFACCIA Screen //
    // ****************************** //
    // aggiornamento grafica
    @Override public void render(float delta) {
        Gdx.input.setInputProcessor(this);
        // init screen
        screen.begin();

        // mostra elementi a schermo
        ui.printUI(screen);

        // chiusura screen
        screen.end();
    }

    // spegnimento controllo input
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    // rilascio risorse
    @Override public void dispose() {
        ui.dispose();
    }

    // altri metodi
    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
}

