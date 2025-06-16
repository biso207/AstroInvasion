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
import sorgente.UserData.CloudStorageManager;
import sorgente.Main;

public class LobbyManager implements Screen {
    protected static Main game; // variabile di riferimento tipo gioco
    // dichiarazione screen
    private final SpriteBatch screen;

    // soundtrack
    protected static Music soundtrack;

    // istanze altre classi
    private final UIManager ui;
    private final InputManager input;

    // costruttore
    public LobbyManager(Main game) {
        LobbyManager.game = game;
        // init dello screen
        this.screen = game.screen;

        // istanza di UIManager con caricamento risorse e creazione grafica
        ui = new UIManager();
        // istanza di InputManager
        input = new InputManager();

        try { CloudStorageManager.loadAllUserPoints(); }
        catch (Exception e) { System.out.println(e.getMessage()); }

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
        Gdx.input.setInputProcessor(input);

        // init screen
        screen.begin();

        // mostra elementi a schermo
        ui.showItems(screen);

        /*
        setting del volume di sottofondo
        fondamentale che sia qui perché in caso di cambiamento durante una sessione di gioco
        il volume deve cambiare dinamicamente
        */
        soundtrack.setVolume(InputManager.musicPercent); // volume musica

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
    // metodo per ricaricare le risorse in caso di minimize dello schermo
    @Override public void resume() {
        /* release delle risorse in memoria
        ui.disposeUI();
        // ricarica immagini e font
        ui.loadImages();
        ui.loadFont();

         */
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
}
