/*
Astro Invasion - class SpaceJourney -
Controlla e gestisce la modalità a livelli
Developed by BIGA©. All rights reserved.
*/

package sorgente.GameMods.SpaceJourney;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.UserData.DataUserManager;
import sorgente.Entities.Spacecraft;
import sorgente.GameMods.ClassicGame;
import sorgente.GameMods.SpaceBattle;
import sorgente.Lobby.InputManager;
import sorgente.Main;
import sorgente.Lobby.LobbyManager;
import sorgente.SoundManager;

import java.util.ArrayList;
import java.util.List;

public class SpaceJourney implements Screen, InputProcessor {
    private final Main game;
    // dichiarazione screen
    private final SpriteBatch screen;

    // soundtrack
    private final Music soundtrack;

    protected static List<Galaxy> galaxies;
    protected static int numGalaxy;

    // info livello
    protected static boolean infoLevel=false, buyLevel=false;

    // pulsante avvio livello
    protected static boolean startLevelHover=false, isBtnRHover=false, isBtnLHover=false;

    // istanza classe della grafica
    private final SpaceJourneyUI ui;

    // livello raggiunto
    private final int numLevel = (int) DataUserManager.getProgress("level");

    // livelli in space battle
    private final List<Integer> listSB = List.of(2, 4, 6, 8,
        12, 14, 16, 18,
        22, 24, 26, 28,
        32, 34, 36, 38);

    // navicella utente
    private final Spacecraft selectedSp;

    // costruttore
    public SpaceJourney(Main game, Spacecraft selectedSp, int numGalaxy) {
        this.game = game;
        // init dello screen
        this.screen = game.screen;
        // navicella utente
        this.selectedSp = selectedSp;

        // init lista galassie
        galaxies = new ArrayList<>();

        // galassia corrente
        SpaceJourney.numGalaxy = numGalaxy;

        // init galassie e livelli
        setupGalaxies();

        // setup grafica
        ui = new SpaceJourneyUI();

        // musica di sottofondo
        soundtrack = Gdx.audio.newMusic(Gdx.files.internal("sounds/space_journey_sound.mp3")); // file audio
        soundtrack.setLooping(true); // true=loop music; false=no loop
        soundtrack.setVolume(InputManager.musicPercent); // volume musica
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
            case 1 -> ((screenX >= 360 && screenX <= 449) && (screenY >= 513 && screenY <= 610));
            case 2 -> ((screenX >= 707 && screenX <= 811) && (screenY >= 501 && screenY <= 572));
            case 3 -> ((screenX >= 736 && screenX <= 833) && (screenY >= 229 && screenY <= 327));
            case 4 -> ((screenX >= 241 && screenX <= 344) && (screenY >= 258 && screenY <= 339));
            default -> false;
        };
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //

    // metodo per controllare i click del mouse
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // click per apertura di una galassia
        if (numGalaxy==0) {
            for (Galaxy galaxy : galaxies) {
                if (isGalaxyClicked(galaxy, screenX, screenY)) {

                    // selezione galassia solo se raggiunta
                    if ((int) Math.ceil((double) numLevel / 10) >= galaxy.getId()) {
                        SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
                        numGalaxy = galaxy.getId();
                    }
                }
            }
        }

        // click su un livello => il for è solo per i 10 livelli della galassia aperta e se ci si trova in una galassia
        if (numGalaxy > 0) {
            int startX = 42;
            int startY = 278;
            int spacingX = 205;
            int spacingY = 224;
            int levelSize = 78;

            List<Level> levels = galaxies.get(numGalaxy - 1).getLevels();

            // controllo click su un livello
            for (int i = 0; i < levels.size(); i++) {
                int col = i % 5; // colonna da 0 a 4
                int row = i / 5; // riga 0 (prima), 1 (seconda)

                // x e y dell'i-esimo livello
                int x = startX + col * spacingX;
                int y = startY + row * spacingY;

                // controllo click su un singolo livello
                if (screenX >= x && screenX <= x + levelSize &&
                    screenY <= y + levelSize && screenY >= y) {
                    SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
                    if (levels.get(i).getState() == LevelState.UNLOCKED) infoLevel=true; // apertura info livello
                    else if (levels.get(i).getState() == LevelState.TO_BUY) buyLevel=true; // apertura schermata sblocco livello
                }
            }

            int numLevel = (int) DataUserManager.getProgress("level"); // livello attuale utente

            // prezzo del livello
            int price;
            if (numLevel/10+1==1) price = numLevel*50;
            else price = numLevel*100;

            int currentCredits = (int) DataUserManager.getProgress("credits"); // crediti attuali dell'utente

            // CLICK NELLE PAGINE IN SOVRAIMPRESSIONE //
            // chiusura info level
            if (infoLevel && (screenX >= 760 && screenX <= 800) && (screenY >= 139 && screenY <= 180)) {
                SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
                infoLevel=false;
            }

            // NO sblocco livello
            if (buyLevel && (screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
                SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
                buyLevel=false;
            }

            // SI sblocco livello
            if (buyLevel && (currentCredits-price>=0) && (screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
                SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click

                int diffCredits = currentCredits-price;
                // sblocco livello
                new Level(numLevel).unlock(diffCredits);

                // chiusura pagina in sovra impressione
                buyLevel=false;
            }
        }


        // back to lobby cliccando l'icona della terra
        if (((screenX >= 25 && screenX <= 122) && (screenY >= 418 && screenY <= 518)) && numGalaxy == 0) {
            SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
            soundtrack.stop(); // stop della musica

            // rilascio risorse
            dispose();

            // apertura nuovo screen - lobby
            game.setScreen(new LobbyManager(game));
        }

        // controllo click della X: da 0 a back to lobby; da 4<=numGalaxy<=1 a mapGalaxies (0)
        if (!infoLevel && !buyLevel && (screenX >= 898 && screenX <= 940) && (screenY >= 84 && screenY <= 124)) {
            SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
            if (numGalaxy == 0) {
                soundtrack.stop(); // stop della musica

                // rilascio risorse
                dispose();

                // apertura nuovo screen - lobby
                game.setScreen(new LobbyManager(game));
            }
            else numGalaxy = 0;
        }

        // avvio livello
        if ((screenX >= 417 && screenX <= 567) && (screenY >= 483 && screenY <= 523) && infoLevel) {
            SoundManager.playClickButton(InputManager.soundPercent); // riproduzione suono click
            infoLevel=false; // chiusura pagina in sovra impressione
            soundtrack.stop(); // interruzione musica
            if (listSB.contains(numLevel)) game.setScreen(new SpaceBattle(game, selectedSp, true));
            else game.setScreen(new ClassicGame(game, selectedSp, true));
        }

        return true;
    }

    // metodo per cambiare grafiche al passaggio del mouse
    @Override public boolean mouseMoved(int screenX, int screenY) {
        startLevelHover=isBtnLHover=isBtnRHover=false;
        // YES purchase
        if ((screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
            isBtnLHover=true;
        }

        // NO purchase
        if ((screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
            isBtnRHover=true;
        }


        startLevelHover = (screenX >= 417 && screenX <= 567) && (screenY >= 483 && screenY <= 523);
        return true;
    }

    // altri metodi
    // metodo per rilevare il click da tastiera
    @Override public boolean keyTyped(char character) { return true; }
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

