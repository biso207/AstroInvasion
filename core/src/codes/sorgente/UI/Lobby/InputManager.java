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
import sorgente.UI.LogInSignUp.LoginSignupManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InputManager implements InputProcessor {
    // attributi
    private final Map<Integer, Hitbox> hitBoxes = new HashMap<>();

    // variabili per gestire certi input
    protected static boolean secondScreen, open22, open23;
    // lista delle pagine secondarie
    private final Set<Integer> listSecondPages = Set.of(10, 11, 12, 13, 14, 15, 16, 17, 24, 25, 26, 27, 28, 29);
    // 'previousPage' serve a memorizzare l'ultima pagina aperta. //
    protected static int page = 0;
    private int previousPage = 6;

    // costruttore
    public InputManager() {
        secondScreen = open22 = open23 = false;

        // definizione delle aree cliccabili
        hitAreas();
    }

    // metodo per definire le aree di gioco cliccabili
    public void hitAreas() {
        hitBoxes.put(0, new Hitbox(50, 180, 270, 220, 0, false));  // 'classic game'
        hitBoxes.put(1, new Hitbox(50, 230, 270, 270, 1, false)); // 'space battle'
        hitBoxes.put(2, new Hitbox(50, 280, 270, 320, 2, false)); // 'space journey'
        hitBoxes.put(3, new Hitbox(50, 330, 270, 370, 3, false)); // 'road to glory'
        hitBoxes.put(4, new Hitbox(50, 380, 270, 420, 4, false)); // 'spacecrafts 1'
        hitBoxes.put(10, new Hitbox(50, 430, 270, 470, 10, true));  // 'missions 1'
        hitBoxes.put(18, new Hitbox(50, 480, 270, 520, 18, false)); // 'marketplace'
        // le pagine seguenti hanno da memorizzare previousPage
        hitBoxes.put(24, new Hitbox(870, 65, 950, 145, 24, true));  // 'instructions'
        hitBoxes.put(28, new Hitbox(50, 270, 530, 570, 28, true));   // 'profile infos'
        hitBoxes.put(29, new Hitbox(50, 90, 580, 620, 29, true));  // 'settings'
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    // metodo per rilevare il click della tastiera
    @Override public boolean keyTyped(char character) {
        // click tasto esc per il logout
        if (character == Input.Keys.ESCAPE && (!listSecondPages.contains(page) && !open22 && !open23)) {
            open23 = true;
            secondScreen = true;
        }

        // click tasto esc per annullare il logout
        if (character == Input.Keys.ESCAPE && (secondScreen&&open23)) {
            secondScreen = open23 = false;
        }

        return true;
    }
    // metodo per controllare i click del mouse
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        /*
        'page' deve essere diverso da certe pagine per non generare l'apertura
        di altre pagine dove non è possibile e poter cambiare le schermate della Lobby.
        Esempio: l'utente NON può aprire la pagina 'classic LobbyManager.game' dalla pagina 'instructions'
        */

        // ......................... //
        // CAMBIO PAGINE DALLA LOBBY //
        // ......................... //
        if (!listSecondPages.contains(page) && !open22 && !open23) {
            System.out.println("sono qui");
            for (Map.Entry<Integer, Hitbox> entry : hitBoxes.entrySet()) {
                Hitbox hb = entry.getValue();
                if (hb.isInside(screenX, screenY)) {
                    if (hb.remembersPrevious) previousPage = page;
                    page = hb.targetPage;
                    break;
                }
            }

            // pagina 26 => 'difficulty infos classic game'
            if (page == 0 && (screenX >= 623 && screenX <=703) && (screenY >= 552 && screenY <=592)) {
                previousPage = page;
                page = 26;
            }

            // pagina 27 => 'difficulty infos space battle'
            if (page == 1 && (screenX >= 623 && screenX <=703) && (screenY >= 552 && screenY <=592)) {
                previousPage = page;
                page = 27;
            }

            // pagina 25 => 'cards infos'
            if ((page == 0 || page == 1) && (screenX >= 883 && screenX <=913) && (screenY >= 230 && screenY <=260)) {
                previousPage = page;
                page = 25;
            }

            // pagina 'software infos'
            if ((screenX >= 110 && screenX <=150) && (screenY >= 580 && screenY <=620)) {
                open22 = true;
                secondScreen = true;
            }

            // pagina 'logout'
            if ((screenX >= 170 && screenX <=210) && (screenY >= 580 && screenY <=620)) {
                open23 = true;
                secondScreen = true;
            }

            // cambio pagina (19-23) => 'avatar/spacecraft/ 1->5'
            if ((screenX >= 873 && screenX <=913) && (screenY >= 553 && screenY <=593)) {
                if ((page>=19 && page<23) || (page>=4 && page<9)) page++;
            }

            // cambio pagina (23-19) => 'avatar/spacecraft/ 5->1'
            if ((screenX >= 343 && screenX <=373) && (screenY >= 553 && screenY <=593)) {
                if ((page<=23 && page>19) || (page<=9 && page>4)) page--;
            }

            // controllo per avviare le modalità di gioco
            if ((page == 0 || page == 1) && (screenX >= 778 && screenX <=928) && (screenY >= 552 && screenY <=592)) {
                LobbyManager.soundtrack.stop();

                // avvio modalità di gioco
                if (page==0) LobbyManager.game.setScreen(new ClassicGame(LobbyManager.game, LobbyManager.selectedSp)); // avvio classic LobbyManager.game
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

        // cambio pagina (10-17) => 'missions 1-7'
        if ((screenX >= 885 && screenX <= 925) && (screenY >= 622 && screenY <=642)) {
            if ((page >=10 && page<17)) page++;
        }

        // cambio pagina (17-10) => 'missions 7-1'
        if ((screenX >= 65 && screenX <= 105) && (screenY >= 622 && screenY <=642)) {
            if ((page <=17 && page>10)) page--;
        }

        // APERTURA 'avatar page' DA 'profile infos' //
        // pagina 19 => 'avatar 1'
        if (page == 24 && (screenX >= 459 && screenX <=537) && (screenY >= 110 && screenY <=188)) {
            page = 19;
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

    // altri metodi
    @Override public boolean keyDown(int var1) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
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
