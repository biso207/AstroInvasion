/*
Astro Invasion - class InputManager -
Gestisce i metodi di controllo degli input utente
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.UI.Lobby;

// import librerie e codici
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import sorgente.GameMods.ClassicGame;
import sorgente.GameMods.SpaceBattle;
import sorgente.InputHandler;
import sorgente.UI.LogInSignUp.LoginSignupManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InputManager implements InputHandler, InputProcessor {
    // attributi
    private final Map<Integer, Hitbox> hitBoxes = new HashMap<>();

    // variabili per gestire certi input
    protected static boolean secondScreen, open22, open23;
    // lista delle pagine secondarie
    private final Set<Integer> listSecondPages = Set.of(5, 7, 8, 9, 14, 15, 16, 17, 18, 19, 20, 21, 22, 24);
    // 'previousPage' serve a memorizzare l'ultima pagina aperta. //
    protected static int page, previousPage;

    // costruttore
    public InputManager() {
        secondScreen = open22 = open23 = false;
        // set immagine di default
        page = previousPage = 6;

        // definizione delle aree cliccabili
        hitAreas();
    }

    // metodo per definire le aree di gioco cliccabili
    public void hitAreas() {
        hitBoxes.put(6, new Hitbox(50, 180, 270, 220, 6, false));  // 'classic LobbyManager.game'
        hitBoxes.put(13, new Hitbox(50, 230, 270, 270, 13, false)); // 'space battle'
        hitBoxes.put(14, new Hitbox(50, 280, 270, 320, 14, false)); // 'space journey'
        hitBoxes.put(12, new Hitbox(50, 330, 270, 370, 12, false)); // 'road to glory'
        hitBoxes.put(15, new Hitbox(50, 380, 270, 420, 15, false)); // 'spacecrafts 1'
        hitBoxes.put(26, new Hitbox(50, 430, 270, 470, 26, true));  // 'missions 1'
        hitBoxes.put(11, new Hitbox(50, 480, 270, 520, 11, false)); // 'marketplace'
        // le pagine seguenti hanno da memorizzare previousPage
        hitBoxes.put(7, new Hitbox(50, 270, 530, 570, 7, true));   // 'instructions'
        hitBoxes.put(21, new Hitbox(50, 90, 580, 620, 21, true));  // 'settings'
        hitBoxes.put(25, new Hitbox(870, 950, 66, 146, 25, true));  // 'profile info'
    }

    // ************************************ //
    // METODI DELL'INTERFACCIA InputHandler //
    // ************************************ //

    // metodo per controllare gli input da tastiera
    @Override
    public boolean keyTyped(char character) {
        // click tasto esc
        if ((int) character == Input.Keys.ESCAPE && (page!=7 && page!=21 && !open22 && !open23 && page!=24 && page!=25)) {
            open23 = true;
            secondScreen = true;
        }

        // click tasto esc per annullare il logout
        else if ((int) character == Input.Keys.ESCAPE && (secondScreen&&open23)) {
            secondScreen = open23 = false;
        }
        return false;
    }
    // metodo per controllare i click del mouse
    @Override
    public boolean mouseClick(int screenX, int screenY) {
        /*
        'page' deve essere diverso da certe pagine per non generare l'apertura
        di altre pagine dove non è possibile e poter cambiare le schermate della Lobby.
        Esempio: l'utente NON può aprire la pagina 'classic LobbyManager.game' dalla pagina 'instructions'
        */

        // ......................... //
        // CAMBIO PAGINE DALLA LOBBY //
        // ......................... //
        if (!listSecondPages.contains(page) && !open22 && !open23) {
            for (Map.Entry<Integer, Hitbox> entry : hitBoxes.entrySet()) {
                Hitbox hb = entry.getValue();
                if (hb.isInside(screenX, screenY)) {
                    if (hb.remembersPrevious) previousPage = page;
                    page = hb.targetPage;
                    return true;
                }
                System.out.println("ciao");
            }

            // pagina 24 => 'difficulty infos classic LobbyManager.game'
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

            // cambio pagina (1-5) => 'avatar/spacecraft/ 1->5'
            if ((screenX >= 873 && screenX <=913) && (screenY >= 553 && screenY <=593)) {
                if ((page >=1 && page < 5) || (page >= 15 && page < 20)) page++;
            }

            // cambio pagina (5-1) => 'avatar/spacecraft/ 5->1'
            if ((screenX >= 343 && screenX <=373) && (screenY >= 553 && screenY <=593)) {
                if ((page <= 5 && page>1) || (page <= 20 && page>15)) page--;
            }

            // controllo per avviare le modalità di gioco
            if ((page == 6 || page == 13) && (screenX >= 778 && screenX <=928) && (screenY >= 552 && screenY <=592)) {
                previousPage = page;
                page=0;
                LobbyManager.soundtrack.stop();

                if (page==6) LobbyManager.game.setScreen(new ClassicGame(LobbyManager.game, LobbyManager.selectedSp)); // avvio classic LobbyManager.game
                else LobbyManager.game.setScreen(new SpaceBattle(LobbyManager.game, LobbyManager.selectedSp)); // avvio space battle
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

        // back to Authenticator Page => YES logout
        if ((secondScreen&&open23) && (screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
            LobbyManager.soundtrack.stop();
            LobbyManager.game.setScreen(new LoginSignupManager(LobbyManager.game));
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

        // cambio difficoltà (classic LobbyManager.game e space battle)

        // setting impostazioni

        // selezione galassia/livello

        // selezione carta speciale

        // acquisti nel negozio

        return true;
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

    /*
    +---------------------+
    | CLASSE INNER Hitbox |
    +---------------------+
    */
    // classe inner per stabilire il range cliccabile
    private static class Hitbox {
        int x1, y1, x2, y2;
        int targetPage;
        boolean remembersPrevious;

        // costruttore
        Hitbox(int x1, int y1, int x2, int y2, int targetPage, boolean remembersPrevious) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.targetPage = targetPage;
            this.remembersPrevious = remembersPrevious;
        }

        // metodo per controllare che una coordinata sia in un range
        boolean isInside(int x, int y) {
            return x >= x1 && x <= x2 && y >= y1 && y <= y2;
        }
    }
}
