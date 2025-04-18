/*
Astro Invasion - class LobbyManager -
Gestisce e controlla le schermate della Lobby di gioco
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.Lobby;

// import librerie e codici
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.DataUserManager;
import sorgente.Entities.Spacecraft;
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
    private final InputManager input;

    // costruttore
    public LobbyManager(Main game) {
        LobbyManager.game = game;
        // init dello screen
        this.screen = game.screen;

        // istanza di UIManager
        ui = new UIManager();
        // caricamento navicella utente
        selectedSp = ui.createSpacecrafts(); // navicelle e recupero navicella utente

        // istanza di InputManager
        input = new InputManager();

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
        //handleInput();?
        Gdx.input.setInputProcessor(input);

        // init screen
        screen.begin();

        // mostra elementi a schermo
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
        ui.disposeUI();
        screen.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
}
