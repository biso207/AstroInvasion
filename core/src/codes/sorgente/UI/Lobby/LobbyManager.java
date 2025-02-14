/*
Astro Invasion - class Lobby -
This class manages and controls all the screens in the game's lobby
Developed by BIGA©. All rights reserved.
*/

package sorgente.UI.Lobby;

// import librerie

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.GameMods.ClassicGame;
import sorgente.GameMods.SpaceBattle;
import sorgente.GameMods.Spacecraft;
import sorgente.Main;

import java.util.*;

public class LobbyManager implements Screen {
    private final Main game; // variabile di riferimento tipo gioco
    // dichiarazione screen
    private final SpriteBatch screen;

    // soundtrack
    Music soundtrack;

    // creazione oggetto navicella generico
    public Spacecraft selectedSp;

    /*
     'previousPage' serve a memorizzare l'ultima pagina aperta.
     Ciò permette di ritornare alla pagina precedente dopo aver chiuso una pagina che occupa interamente lo schermo
    */
    private int page, previousPage;

    // arraylist delle pagine secondarie
    private final Set<Integer> listSecondPages = Set.of(5, 7, 8, 9, 14, 15, 16, 17, 18, 19, 20, 21, 22, 24);

    boolean secondScreen, open22, open23;

    // istanza classe grafica
    UIManager ui;

    // costruttore
    public LobbyManager(Main game) {
        this.game = game;
        // init dello screen
        this.screen = game.screen;

        // istanza di UIManager
        ui = new UIManager();

        // set immagine di default (classic game)
        page = previousPage = 6;

        // init del secondo "screen", dello screen software.infos e close.game a false
        secondScreen = open22 = open23 = false;

        // caricamento risorse
        ui.loadLobbyImages(); // schermate lobby
        ui.loadImages(); // altre immagini
        ui.loadFont(); // font
        this.selectedSp = ui.createSpacecrafts(); // navicelle e recupero navicella utente

        // musica di sottofondo
        soundtrack = Gdx.audio.newMusic(Gdx.files.internal("sounds/lobby_sound.ogg")); // file audio
        soundtrack.setLooping(true); // true=loop music; false=no loop
        soundtrack.play(); // avvio musica
    }

    /// TODO: spostare i metodi della gestione input nella classe InputManager del package Lobby
    // ************** //
    // GESTIONE INPUT //
    // ************** //

    // classe interna per gestire gli input da mouse e tastiera
    private class MyInputProcessor extends InputAdapter {
        @Override
        public boolean keyDown(int keycode) {
            // click tasto esc
            if (keycode == Input.Keys.ESCAPE && (page!=7 && page!=21 && !open22 && !open23 && page!=24 && page!=25)) {
                open23 = true;
                secondScreen = true;
            }

            // click tasto esc per annullare il logout
            else if (keycode == Input.Keys.ESCAPE && (secondScreen&&open23)) {
                secondScreen = open23 = false;
            }

            return true;
        }

        // metodo recuperare il click del mouse
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            /*
            'page' deve essere diverso da certe pagine per non generare l'apertura
            di altre pagine dove non è possibile e poter cambiare le schermate della Lobby.
            Esempio: l'utente NON può aprire la pagina 'classic game' dalla pagina 'instructions'
            */

            // ......................... //
            // CAMBIO PAGINE DALLA LOBBY //
            // ......................... //
            if (!listSecondPages.contains(page) && !open22 && !open23) {
                // pagina 6 => 'classic game'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 180 && screenY <= 220)) {
                    page = 6;
                }

                // pagina 13 => 'space battle'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 230 && screenY <= 270)) {
                    page = 13;
                }

                // pagina 14 => 'space journey'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 280 && screenY <= 320)) {
                    page = 14;
                }

                // pagina 12 => 'road to glory'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 330 && screenY <= 370)) {
                    page = 12;
                }

                // pagina 15 => 'spacecrafts 1'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 380 && screenY <= 420)) {
                    page = 15;
                }

                // pagina 26 => 'missions 1'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 430 && screenY <= 470)) {
                    previousPage = page;
                    page = 26;
                }

                // pagina 11 => 'marketplace'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 480 && screenY <= 520)) {
                    page = 11;
                }

                // cambio pagina (1-5) => 'avatar/spacecraft/ 1->5'
                if ((screenX >= 873 && screenX <=913) && (screenY >= 553 && screenY <=593)) {
                    if ((page >=1 && page < 5) || (page >= 15 && page < 20)) page++;
                }

                // cambio pagina (5-1) => 'avatar/spacecraft/ 5->1'
                if ((screenX >= 343 && screenX <=373) && (screenY >= 553 && screenY <=593)) {
                    if ((page <= 5 && page>1) || (page <= 20 && page>15)) page--;
                }

                // pagina 7 => 'instructions'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 530 && screenY <= 570)) {
                    previousPage = page;
                    page = 7;
                }

                // pagine 21 => 'settings'
                if ((screenX >= 50 && screenX <=90) && (screenY >= 580 && screenY <=620)) {
                    previousPage = page;
                    page = 21;
                }

                // pagina 24 => 'difficulty infos classic game'
                if (page == 6 && (screenX >= 623 && screenX <=703) && (screenY >= 552 && screenY <=592)) {
                    previousPage = page;
                    page = 24;
                }

                // pagina 33 => 'difficulty infos space battle'
                if (page == 13 && (screenX >= 623 && screenX <=703) && (screenY >= 552 && screenY <=592)) {
                    previousPage = page;
                    page = 33;
                }

                // pagina 34 => 'cards infos'
                if ((page == 13 || page == 6) && (screenX >= 883 && screenX <=913) && (screenY >= 230 && screenY <=260)) {
                    previousPage = page;
                    page = 34;
                }

                // pagina 25 => 'profile info'
                if ((screenX >= 870 && screenX <=950) && (screenY >= 66 && screenY <=146)) {
                    previousPage = page;
                    page = 25;
                }

                // pagina 22 => 'software infos'
                if ((screenX >= 110 && screenX <=150) && (screenY >= 580 && screenY <=620)) {
                    open22 = true;
                    secondScreen = true;
                }

                // pagina 23 => 'logout'
                if ((screenX >= 170 && screenX <=210) && (screenY >= 580 && screenY <=620)) {
                    open23 = true;
                    secondScreen = true;
                }

                // CONTROLLI PER AVVIARE LE MODALITÀ DI GIOCO //
                // avvio 'Classic Game'
                if (page == 6 && (screenX >= 778 && screenX <=928) && (screenY >= 552 && screenY <=592)) {
                    previousPage = page;
                    page=0;
                    soundtrack.stop();
                    // avvio classic game
                    game.setScreen(new ClassicGame(game, selectedSp));
                }

                if (page==13 && (screenX >= 778 && screenX <=928) && (screenY >= 552 && screenY <=592)) {
                    previousPage = page;
                    page=0;
                    soundtrack.stop();
                    // avvio space battle
                    game.setScreen(new SpaceBattle(game));
                }
            }

            // ............... //
            // CHIUSURA PAGINE //
            // ............... //

            // chiusura pagina instruction/settings/profile info&difficulty/missions
            if ((listSecondPages.contains(page) && (screenX >= 908 && screenX <= 948) && (screenY >= 84 && screenY <= 124))) {
                page = previousPage;
            }

            // chiusura software.infos
            if ((secondScreen&&open22) && (screenX >= 684 && screenX <= 724) && (screenY >= 206 && screenY <= 246)) {
                secondScreen = open22 = false;
            }

            // chiusura (annullamento) logout
            if ((secondScreen&&open23) && (screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
                secondScreen = open23 = false;
            }

            // back to LogInSignUp => YES logout
            if ((secondScreen&&open23) && (screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
                soundtrack.stop();
                game.setScreen(new LogInSignUp(game));
            }

            // ........................ //
            // CAMBIO PAGINE SECONDARIE //
            // ........................ //

            // cambio pagina (26-32) => 'missions 1-7'
            if ((screenX >= 885 && screenX <= 925) && (screenY >= 622 && screenY <=642)) {
                if ((page >=26 && page < 32)) page++;
            }

            // cambio pagina (32-26) => 'missions 7-1'
            if ((screenX >= 65 && screenX <= 105) && (screenY >= 622 && screenY <=642)) {
                if ((page <= 32 && page > 26)) page--;
            }

            // APERTURA 'avatar page' DA 'profile infos' //
            // pagina 1 => 'avatar 1'
            if (page == 25 && (screenX >= 459 && screenX <=537) && (screenY >= 110 && screenY <=188)) {
                page = 1;
            }

            // .................. //
            // CLICK NELLE PAGINE //
            // .................. //

            // selezione/cambio navicella

            // selezione/cambio avatar

            // cambio difficoltà (classic game e space battle)

            // setting impostazioni

            // selezione galassia/livello

            // selezione carta speciale

            // acquisti nel negozio

            return true;
        }
    }

    // ****************************** //
    // METODI DELL'INTERFACCIA Screen //
    // ****************************** //
    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        // spegnimento controllo input
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void show() {}

    // metodo per aggiornare lo schermo
    @Override
    public void render(float delta) {

        // attivazione controllo input
        Gdx.input.setInputProcessor(new MyInputProcessor());

        screen.begin();

        // stampa degli elementi nelle singole pagine
        ui.showItems(screen, page, secondScreen, open22);

        // chiusura screen
        screen.end();
    }

    // metodo per rilasciare le risorse
    @Override
    public void dispose() {
        screen.dispose();
    }
}
