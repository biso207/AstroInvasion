/*
Astro Invasion - class LobbyManager -
Gestisce e controlla le schermate della Lobby di gioco
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.UI.Lobby;

// import librerie e codici
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.GameMods.Spacecraft;
import sorgente.Main;

public class LobbyManager implements Screen {
    protected static Main game; // variabile di riferimento tipo gioco
    // dichiarazione screen
    private final SpriteBatch screen;

    // soundtrack
    protected static Music soundtrack;

    // creazione oggetto navicella generico
    protected static Spacecraft selectedSp;

    // istanze altre classi
    private final UIManager ui;

    // costruttore
    public LobbyManager(Main game) {
        LobbyManager.game = game;
        // init dello screen
        this.screen = game.screen;

        // istanza di UIManager
        ui = new UIManager();

        // caricamento risorse
        ui.loadLobbyImages(); // schermate lobby
        ui.loadImages(); // altre immagini
        ui.loadFont(); // font
        selectedSp = ui.createSpacecrafts(); // navicelle e recupero navicella utente

        // musica di sottofondo
        soundtrack = Gdx.audio.newMusic(Gdx.files.internal("sounds/lobby_sound.ogg")); // file audio
        soundtrack.setLooping(true); // true=loop music; false=no loop
        soundtrack.play(); // avvio musica
    }

    // ****************************** //
    // METODI DELL'INTERFACCIA Screen //
    // ****************************** //

    // metodo per aggiornare lo schermo
    @Override public void render(float delta) {
        // attivazione controllo input
        Gdx.input.setInputProcessor(new InputManager());

        screen.begin();

        // stampa degli elementi nelle singole pagine
        ui.showItems(screen);

        // chiusura screen
        screen.end();
    }
    // spegnimento controllo input
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }
    // metodo per rilasciare le risorse
    @Override public void dispose() {
        screen.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
}
