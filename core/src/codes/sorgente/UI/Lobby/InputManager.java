/*
Astro Invasion - class InputManager -
Gestisce i metodi di controllo degli input utente
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.UI.Lobby;

// import librerie e codici
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import sorgente.DataUserManager;
import sorgente.GameMods.ClassicGame;
import sorgente.GameMods.SpaceBattle;
import sorgente.UI.LogInSignUp.LoginSignupManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static sorgente.UI.Lobby.LobbyManager.selectedSp;

public class InputManager implements InputProcessor {
    // mappa dei range
    private final Map<Integer, Hitbox> hitBoxes = new HashMap<>();

    // variabili per gestire certi input
    protected static boolean secondScreen=false, open22=false, open23=false;
    // lista delle pagine secondarie
    private final Set<Integer> listSecondPages = Set.of(10, 11, 12, 13, 14, 15, 16, 17, 24, 25, 26, 27, 28, 29);
    // 'previousPage' serve a memorizzare l'ultima pagina aperta. //
    protected static int page = 0;
    private int previousPage;

    // boolean per le carte speciali
    public static boolean goldHeart=false, shield=false, superLaser=false, doublePoints=false;
    // nome navicella
    private final String nameSp = selectedSp.getName();

    // difficoltà classic game e space battle
    private int diffCG = (int)DataUserManager.getProgress("diff_classic_game");
    private int diffSB = (int)DataUserManager.getProgress("diff_space_battle");


    // costruttore
    public InputManager() {
        // definizione delle aree cliccabili
        hitAreas();

        // attivazione carte speciali se selezionata navicelle premium
        if (nameSp.equals("Alpha")) goldHeart = true;
        if (nameSp.equals("Astrid")) shield = true;
        if (nameSp.equals("Rorik")) superLaser = true;
        if (nameSp.equals("Drakar")) doublePoints = true;
    }

    // metodo per definire le aree di gioco cliccabili
    public void hitAreas() {
        hitBoxes.put(0, new Hitbox(50, 182, 270, 200, 0, false));  // 'classic game'
        hitBoxes.put(1, new Hitbox(50, 232, 270, 250, 1, false)); // 'space battle'
        hitBoxes.put(2, new Hitbox(50, 285, 270, 303, 2, false)); // 'space journey'
        hitBoxes.put(3, new Hitbox(50, 336, 270, 354, 3, false)); // 'road to glory'
        hitBoxes.put(4, new Hitbox(50, 389, 270, 407, 4, false)); // 'spacecrafts 1'
        hitBoxes.put(10, new Hitbox(50, 444, 270, 462, 10, true));  // 'missions 1'
        hitBoxes.put(18, new Hitbox(50, 496, 270, 514, 18, false)); // 'marketplace'
        // le pagine seguenti hanno da memorizzare previousPage
        hitBoxes.put(24, new Hitbox(870, 65, 950, 145, 24, true));  // 'profile infos'
        hitBoxes.put(28, new Hitbox(50, 550, 270, 568, 28, true));   // 'instructions'
        hitBoxes.put(29, new Hitbox(50, 600, 90, 630, 29, true));  // 'settings'
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    // metodo per rilevare il click della tastiera
    @Override public boolean keyDown(int keycode) {
        // click tasto esc per il logout
        if (keycode == Input.Keys.ESCAPE && (!listSecondPages.contains(page) && !open22 && !open23)) {
            open23 = true;
            secondScreen = true;
            return true;
        }

        // click tasto esc per annullare il logout
        if (keycode == Input.Keys.ESCAPE && (secondScreen&&open23)) {
            open23 = false;
            secondScreen = false;
        }

        return true;
    }

    /// TODO: capire perché quando si chiude una pagina secondaria si passa sempre alla page 0 e non a quella precedente...
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
            // for-each per iterare i vari range e controllare i cambi pagina
            for (Map.Entry<Integer, Hitbox> entry : hitBoxes.entrySet()) {
                Hitbox hb = entry.getValue();
                if (hb.isInside(screenX, screenY)) {
                    if (hb.remembersPrevious) previousPage = page;
                    page = hb.targetPage;
                    break;
                }
            }

            // pagina 'software infos'
            if ((screenX>=105 && screenX<=135) && (screenY>=600 && screenY<=630)) {
                open22 = true;
                secondScreen = true;
            }

            // pagina 'logout'
            if ((screenX>=162 && screenX<=192) && (screenY>=600 && screenY<=630)) {
                open23 = true;
                secondScreen = true;
            }

            // cambio pagina (19-23) => 'avatar/spacecraft/ 1->5'
            if ((screenX>=873 && screenX<=913) && (screenY>=553 && screenY<=593)) {
                if ((page>=19 && page<23) || (page>=4 && page<9)) page++;
            }

            // cambio pagina (23-19) => 'avatar/spacecraft/ 5->1'
            if ((screenX>=343 && screenX<=373) && (screenY>=553 && screenY<=593)) {
                if ((page<=23 && page>19) || (page<=9 && page>4)) page--;
            }

            // pagina 0 -> pagina 26 (difficulty infos classic game)
            if (page == 0 && (screenX>=623 && screenX<=703) && (screenY>=552 && screenY<=592)) {
                previousPage = page;
                page = 26;
            }

            // pagina 1 -> pagina 27 (difficulty infos space battle)
            if (page == 1 && (screenX>=623 && screenX<=703) && (screenY>=552 && screenY<=592)) {
                previousPage = page;
                page = 27;
            }

            // pagina 0/1 -> pagina 25 (cards infos)
            if ((page == 0 || page == 1) && (screenX>=883 && screenX<=913) && (screenY>=230 && screenY<=260)) {
                previousPage = page;
                page = 25;
            }
        }

        // ............... //
        // CHIUSURA PAGINE //
        // ............... //

        // chiusura pagina instruction/settings/profile info&difficulty/missions
        if ((listSecondPages.contains(page) && (screenX>=908 && screenX<=948) && (screenY>=84 && screenY<=124))) {
            page = previousPage;
        }

        // chiusura software infos
        if ((secondScreen&&open22) && (screenX>=684 && screenX<=724) && (screenY>=206 && screenY<=246)) {
            secondScreen = open22 = false;
        }

        // chiusura (annullamento) logout
        if ((secondScreen&&open23) && (screenX>=519 && screenX<=719) && (screenY>=417 && screenY<=497)) {
            secondScreen = open23 = false;
        }

        // YES logout => back to Authentication Page
        if ((secondScreen&&open23) && (screenX>=281 && screenX<=481) && (screenY>=417 && screenY<=497)) {
            LobbyManager.soundtrack.stop();
            LobbyManager.game.setScreen(new LoginSignupManager(LobbyManager.game));
        }

        // ........................ //
        // CAMBIO PAGINE SECONDARIE //
        // ........................ //

        // cambio pagina (10-17) => 'missions 1-7'
        if ((screenX>=885 && screenX<=925) && (screenY>=622 && screenY<=642)) {
            if ((page>=10 && page<17)) page++;
        }

        // cambio pagina (17-10) => 'missions 7-1'
        if ((screenX>=65 && screenX<=105) && (screenY>=622 && screenY<=642)) {
            if ((page<=17 && page>10)) page--;
        }

        // .................. //
        // CLICK NELLE PAGINE //
        // .................. //

        // pagina 24 -> pagina 19 (avatar 1)
        if (page == 24 && (screenX>=459 && screenX<=537) && (screenY>=110 && screenY<=188)) {
            page = 19;
        }

        // controllo per avviare le modalità di gioco
        if ((page == 0 || page == 1) && (screenX>=778 && screenX<=928) && (screenY>=552 && screenY<=592)) {
            LobbyManager.soundtrack.stop();

            // avvio modalità di gioco
            if (page==0) LobbyManager.game.setScreen(new ClassicGame(LobbyManager.game, selectedSp)); // avvio classic LobbyManager.game
            else LobbyManager.game.setScreen(new SpaceBattle(LobbyManager.game, selectedSp)); // avvio space battle
        }

        // selezione navicella

        // selezione avatar

        // cambio difficoltà classic game
        System.out.println(screenX + " " + screenY);
        if (page==0 && (screenX>=710 && screenX<=730) && (screenY>=560 && screenY<=584)) {
            if (diffCG<3) {
                diffCG++;
                DataUserManager.setProgress("diff_classic_game", diffCG);
            }
        }
        if (page==0 && (screenX>=587 && screenX<=607) && (screenY>=560 && screenY<=584)) {
            if (diffCG>1) {
                diffCG--;
                DataUserManager.setProgress("diff_classic_game", diffCG);
            }
        }

        // cambio difficoltà space battle
        if (page==1 && (screenX>=710 && screenX<=730) && (screenY>=560 && screenY<=584)) {
            if (diffSB<3) {
                diffSB++;
                DataUserManager.setProgress("diff_space_battle", diffSB);
            }
        }
        if (page==1 && (screenX>=587 && screenX<=607) && (screenY>=560 && screenY<=584)) {
            if (diffSB>1) {
                diffSB--;
                DataUserManager.setProgress("diff_space_battle", diffSB);
            }
        }

        // setting impostazioni

        // selezione galassia/livello

        // selezione carte speciali //
        // gold heart
        if ((page==0 || page==1) && (!nameSp.equals("Alpha")) && (screenX>=712 && screenX<=734) && (screenY>=346 && screenY<=368)) {
            goldHeart = !goldHeart;
            shield = false;
            superLaser = false;
            doublePoints = false;
        }
        // shield
        if ((page==0 || page==1) && (!nameSp.equals("Astrid")) && (screenX>=874 && screenX<=896) && (screenY>=346 && screenY<=368)) {
            goldHeart = false;
            if (page==0) { shield = !shield; superLaser = false; }
            else { superLaser = !superLaser; shield = false; }
            doublePoints = false;
        }
        // super laser
        if (page==0 && (!nameSp.equals("Rorik")) && (screenX>=712 && screenX<=734) && (screenY>=504 && screenY<=526)) {
            goldHeart = false;
            shield = false;
            superLaser = !superLaser;
            doublePoints = false;
        }
        // double points
        if (page==0 && (!nameSp.equals("Drakar")) && (screenX>=874 && screenX<=896) && (screenY>=504 && screenY<=526)) {
            goldHeart = false;
            shield = false;
            superLaser = false;
            doublePoints = !doublePoints;
        }


        // acquisti nel negozio

        return true;
    }

    // cambio icona mouse al passaggio sugli elementi
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // cursore normale se non è sopra un pulsante
        Gdx.graphics.setCursor(UIManager.cursor);
        return false;
    }

    // altri metodi
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

    /*
    +---------------------+
    | CLASSE INNER Hitbox |
    +---------------------+
    */
    // classe inner per stabilire il range cliccabile, serve per controllare in maniera più pulita il click su un range
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
            return x>=x1 && x<=x2 && y>=y1 && y<=y2;
        }
    }
}
