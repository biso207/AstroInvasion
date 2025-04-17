/*
Astro Invasion - class InputManager -
Gestisce i metodi di controllo degli input utente
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.Lobby;

// import librerie e codici
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import sorgente.DataUserManager;
import sorgente.Entities.Avatar;
import sorgente.GameMods.ClassicGame;
import sorgente.GameMods.SpaceBattle;
import sorgente.GameMods.SpaceJourney.SpaceJourney;
import sorgente.LogInSignUp.LoginSignupManager;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static sorgente.Lobby.LobbyManager.selectedSp;

public class InputManager implements InputProcessor {
    // mappa dei range
    private final Map<Integer, HitBox> hitBoxes = new HashMap<>();

    // variabili per gestire certi input
    protected static boolean secondScreen=false, open22=false, open23=false, open24=false, open25=false;
    // lista delle pagine secondarie
    private final Set<Integer> listSecondPages = Set.of(10, 11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22, 23, 24);
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

    // oggetti negozio
    protected static int item1, item2, item3, item4, item5, item6;
    // prezzo finale negozio
    protected static int finalPrize=0;
    // crediti utente
    protected static int currentCredit;


    // costruttore
    public InputManager(int credits) {
        // definizione delle aree cliccabili
        hitAreas();

        // attivazione carte speciali se selezionata una navicella premium
        if (nameSp.equals("Alpha")) goldHeart = true;
        if (nameSp.equals("Astrid")) shield = true;
        if (nameSp.equals("Rorik")) superLaser = true;
        if (nameSp.equals("Drakar")) doublePoints = true;

        // init numero prodotti
        item1=item2=item3=item4=item5=item6=0;
        // recupero crediti
        currentCredit = credits;
    }

    // metodo per definire le aree di gioco cliccabili
    public void hitAreas() {
        hitBoxes.put(0, new HitBox(50, 182, 270, 200, 0, false));  // 'classic game'
        hitBoxes.put(1, new HitBox(50, 232, 270, 250, 1, false)); // 'space battle'
        hitBoxes.put(2, new HitBox(50, 285, 270, 303, 2, false)); // 'space journey'
        hitBoxes.put(3, new HitBox(50, 336, 270, 354, 3, false)); // 'road to glory'
        hitBoxes.put(4, new HitBox(50, 389, 270, 407, 4, false)); // 'spacecrafts 1'
        hitBoxes.put(10, new HitBox(50, 444, 270, 462, 10, true));  // 'missions 1'
        hitBoxes.put(18, new HitBox(50, 496, 270, 514, 17, false)); // 'marketplace'
        // le pagine seguenti hanno da memorizzare previousPage
        hitBoxes.put(24, new HitBox(862, 62, 950, 145, 20, true));  // 'profile infos'
        hitBoxes.put(28, new HitBox(50, 550, 270, 568, 24, true));   // 'instructions'
        hitBoxes.put(29, new HitBox(50, 600, 90, 630, 18, true));  // 'settings'
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    // metodo per rilevare il click della tastiera
    @Override public boolean keyDown(int keycode) {
        // click tasto esc per il logout
        if (keycode == Input.Keys.ESCAPE && (!listSecondPages.contains(page) && !open22 && !open23 && !open24 && !open25)) {
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
        if (!listSecondPages.contains(page) && !open22 && !open23 && !open24 && !open25) {
            // for-each per iterare i vari range e controllare i cambi pagina
            for (Map.Entry<Integer, HitBox> entry : hitBoxes.entrySet()) {
                HitBox hb = entry.getValue();
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

            // cambio pagina (19-23) => 'spacecraft 1->5'
            if ((page>=4 && page<9) && (screenX>=873 && screenX<=913) && (screenY>=553 && screenY<=593)) {
                page++;
            }

            // cambio pagina (23-19) => 'spacecraft 5->1'
            if ((page<=9 && page>4) && (screenX>=343 && screenX<=373) && (screenY>=553 && screenY<=593)) {
                page--;
            }

            // pagina 0 -> pagina 22 (difficulty infos classic game)
            if (page == 0 && (screenX>=623 && screenX<=703) && (screenY>=552 && screenY<=592)) {
                previousPage = page;
                page = 22;
            }

            // pagina 1 -> pagina 23 (difficulty infos space battle)
            if (page == 1 && (screenX>=623 && screenX<=703) && (screenY>=552 && screenY<=592)) {
                previousPage = page;
                page = 23;
            }

            // pagina 0/1 -> pagina 21 (cards infos)
            if ((page == 0 || page == 1) && (screenX>=883 && screenX<=913) && (screenY>=230 && screenY<=260)) {
                previousPage = page;
                page = 21;
            }
        }

        // ............... //
        // CHIUSURA PAGINE //
        // ............... //

        // chiusura pagina instruction/settings/profile info&difficulty/missions/avatar
        if ((listSecondPages.contains(page) && (screenX>=908 && screenX<=948) && (screenY>=84 && screenY<=124))) {
            if (page == 19) page = 20;
            else page = previousPage;
        }

        // chiusura software infos
        if ((secondScreen&&open22) && (screenX>=684 && screenX<=724) && (screenY>=206 && screenY<=246)) {
            secondScreen = open22 = false;
        }

        // chiusura (annullamento) logout
        if ((secondScreen&&open23) && (screenX>=519 && screenX<=719) && (screenY>=417 && screenY<=497)) {
            secondScreen = open23 = false;
        }

        // OK warning => close warning and back to lobby
        if ((secondScreen&&open24) && (screenX>=281 && screenX<=481) && (screenY>=417 && screenY<=497)) {
            secondScreen = open24 = false;
        }

        // PLAY warning => play classic game
        if ((secondScreen&&open24) && (screenX>=519 && screenX<=719) && (screenY>=417 && screenY<=497)) {
            secondScreen = open24 = false;
            LobbyManager.soundtrack.stop();
            LobbyManager.game.setScreen(new ClassicGame(LobbyManager.game, selectedSp)); // avvio classic game
        }

        // YES logout => back to the Authentication Page
        if ((secondScreen&&open23) && (screenX>=281 && screenX<=481) && (screenY>=417 && screenY<=497)) {
            secondScreen = open23 = false;
            LobbyManager.soundtrack.stop();
            LobbyManager.game.setScreen(new LoginSignupManager(LobbyManager.game));
        }

        // ........................ //
        // CAMBIO PAGINE SECONDARIE //
        // ........................ //

        // cambio pagina (10-17) => 'missions 1-7'
        if ((screenX>=885 && screenX<=925) && (screenY>=622 && screenY<=642)) {
            if ((page>=10 && page<16)) page++;
        }

        // cambio pagina (17-10) => 'missions 7-1'
        if ((screenX>=65 && screenX<=105) && (screenY>=622 && screenY<=642)) {
            if ((page<=16 && page>10)) page--;
        }

        // .................. //
        // CLICK NELLE PAGINE //
        // .................. //

        // apertura pagina 19 'avatar'
        if (page == 20 && (screenX>=453 && screenX<=537) && (screenY>=108 && screenY<=188)) {
            page = 19;
        }

        // controllo per avviare le modalità di gioco
        if ((screenX>=778 && screenX<=928) && (screenY>=552 && screenY<=592)) {
            // avvio modalità di gioco
            if (page == 0) {
                // controllo per l'avviso difficoltà
                if ((nameSp.equals("Omega") || nameSp.equals("Idra") || nameSp.equals("pegaso") || nameSp.equals("Woka")) && diffCG == 3d) {
                    secondScreen = open24 = true;
                } else {
                    LobbyManager.soundtrack.stop(); // interruzione musica
                    LobbyManager.game.setScreen(new ClassicGame(LobbyManager.game, selectedSp)); // avvio classic game
                }
            }
            else if (page == 1) {
                LobbyManager.soundtrack.stop(); // interruzione musica
                LobbyManager.game.setScreen(new SpaceBattle(LobbyManager.game, selectedSp)); // avvio space battle
            }
            else if (page == 2) {
                LobbyManager.soundtrack.stop(); // interruzione musica
                LobbyManager.game.setScreen(new SpaceJourney(LobbyManager.game)); // apertura mappa space journey
            }
        }
        // selezione navicella

        // selezione avatar
        if (page == 19) {
            int x = 133, y = 220; // posizione avatar 1
            for (int i = 0; i <= 19; i++) {
                if ((screenX >= x && screenX <= x + 66) && (screenY >= y && screenY <= y + 66)) {
                    if (Avatar.isAchieved(i)) {
                        DataUserManager.setProgress("avatar", i);
                    }
                }

                // aggiornamento posizione avatar per i possibili click
                x += 66 + 95; // 66 larghezza img, 95 distanza di x tra immagini
                if ((i + 1) % 5 == 0) {
                    x = 133;
                    y += 66 + 45; // 66 larghezza img, 45 distanza di y tra immagini
                }
            }
        }

        // cambio difficoltà classic game
        if (page == 0 && (screenX >= 710 && screenX <= 730) && (screenY >= 560 && screenY <= 584)) {
            if (diffCG < 3) {
                diffCG++;
                DataUserManager.setProgress("diff_classic_game", diffCG);
            }
        }
        if (page == 0 && (screenX >= 587 && screenX <= 607) && (screenY >= 560 && screenY <= 584)) {
            if (diffCG > 1) {
                diffCG--;
                DataUserManager.setProgress("diff_classic_game", diffCG);
            }
        }

        // cambio difficoltà space battle
        if (page == 1 && (screenX >= 710 && screenX <= 730) && (screenY >= 560 && screenY <= 584)) {
            if (diffSB < 3) {
                diffSB++;
                DataUserManager.setProgress("diff_space_battle", diffSB);
            }
        }
        if (page == 1 && (screenX >= 587 && screenX <= 607) && (screenY >= 560 && screenY <= 584)) {
            if (diffSB > 1) {
                diffSB--;
                DataUserManager.setProgress("diff_space_battle", diffSB);
            }
        }

        // setting impostazioni

        // selezione carte speciali //
        // gold heart
        if ((page == 0 || page == 1) && ((int) DataUserManager.getProgress("num_gold_heart") > 0) && (!nameSp.equals("Alpha")) && (screenX >= 705 && screenX <= 734) && (screenY >= 346 && screenY <= 368)) {
            goldHeart = !goldHeart;
            shield = superLaser = doublePoints = false;
        }
        // shield
        if ((page == 0 || page == 1) && (!nameSp.equals("Astrid")) && (screenX >= 867 && screenX <= 896) && (screenY >= 346 && screenY <= 368)) {
            if (page == 0 && ((int) DataUserManager.getProgress("num_shield") > 0)) {
                shield = !shield;
                superLaser = false;
            } else if (page == 1 && ((int) DataUserManager.getProgress("num_super_laser") > 0)) {
                superLaser = !superLaser;
                shield = false;
            }
            goldHeart = doublePoints = false;
        }
        // super laser
        if (page == 0 && ((int) DataUserManager.getProgress("num_super_laser") > 0) && (!nameSp.equals("Rorik")) && (screenX >= 705 && screenX <= 734) && (screenY >= 504 && screenY <= 526)) {
            superLaser = !superLaser;
            goldHeart = shield = doublePoints = false;
        }
        // double points
        if (page == 0 && ((int) DataUserManager.getProgress("num_double_points") > 0) && (!nameSp.equals("Drakar")) && (screenX >= 867 && screenX <= 896) && (screenY >= 504 && screenY <= 526)) {
            doublePoints = !doublePoints;
            goldHeart = shield = superLaser = false;
        }

        // claim reward del RTG
        if (page == 3 && ((boolean) DataUserManager.getProgress("completed_RTG")) && (screenX >= 762 && screenX <= 898) && (screenY >= 561 && screenY <= 595)) {
            // missione corrente
            int mission = (int) DataUserManager.getProgress("num_mission");
            // id missione corrente
            int missionID = (int) DataUserManager.getProgress("mission_id");

            // recupero numero carte e crediti
            int numGoldHeart = (int) DataUserManager.getProgress("num_gold_heart");
            int numShield = (int) DataUserManager.getProgress("num_shield");
            int numSuperLaser = (int) DataUserManager.getProgress("num_super_laser");
            int credits = (int) DataUserManager.getProgress("credits");

            switch (missionID) {
                case 1:
                    DataUserManager.setProgress("num_aliens_hit_RTG", 0); // progressi missione azzerati
                    DataUserManager.setProgress("num_gold_heart", numGoldHeart + 1); // aggiunta carta
                    missionID++;
                    break;
                case 2:
                    DataUserManager.setProgress("won_SB_RTG", 0); // progressi missione azzerati
                    DataUserManager.setProgress("num_shield", numShield + 1); // aggiunta carta
                    missionID++;
                    break;
                case 3:
                    DataUserManager.setProgress("points_RTG", 0); // progressi missione azzerati
                    DataUserManager.setProgress("credits", credits + 100);// aggiunta 100 crediti
                    DataUserManager.setProgress("total_credits", credits + 100);// aggiunta 100 crediti totali
                    missionID++;
                    break;
                case 4:
                    DataUserManager.setProgress("credits_RTG", 0); // progressi missione azzerati
                    DataUserManager.setProgress("num_super_laser", numSuperLaser + 1); // aggiunta carta
                    missionID = 1;
                    break;
            }

            DataUserManager.setProgress("num_mission", mission + 1);
            DataUserManager.setProgress("mission_id", missionID);
            DataUserManager.setProgress("completed_RTG", false); // RTG non più completata
        }

        // acquisti nel negozio
        if (page == 17 && !secondScreen) {
            // rimozione prodotto
            if ((screenX >= 344 && screenX <= 364 && screenY >= 385 && screenY <= 405) && item1>0) { // item 1
                item1--;
                currentCredit += 50;
            }
            if ((screenX >= 494 && screenX <= 514 && screenY >= 385 && screenY <= 405) && item2>0) { // item 2
                item2--;
                currentCredit += 75;
            }
            if ((screenX >= 644 && screenX <= 664 && screenY >= 385 && screenY <= 405) && item3>0) { // item 3
                item3--;
                currentCredit += 100;
            }
            if ((screenX >= 794 && screenX <= 814 && screenY >= 385 && screenY <= 405) && item4>0) { // item 4
                item4--;
                currentCredit += 200;
            }
            if ((screenX >= 442 && screenX <= 462 && screenY >= 538 && screenY <= 558) && item5>0) { // item 5
                item5--;
                currentCredit += 20000;
            }
            if ((screenX >= 688 && screenX <= 708 && screenY >= 538 && screenY <= 558) && item6>0) { // item 6
                item6--;
                currentCredit += 30000;
            }

            // aggiunta prodotto
            if ((screenX >= 409 && screenX <= 429 && screenY >= 385 && screenY <= 405) && (currentCredit-50>=0)) { // item 1
                item1++;
                currentCredit -= 50;
            }
            if ((screenX >= 559 && screenX <= 579 && screenY >= 385 && screenY <= 405) && (currentCredit-75>=0)) { // item 2
                item2++;
                currentCredit -= 75;
            }
            if ((screenX >= 709 && screenX <= 729 && screenY >= 385 && screenY <= 405) && (currentCredit-100>=0)) { // item 3
                item3++;
                currentCredit -= 100;
            }
            if ((screenX >= 859 && screenX <= 879 && screenY >= 385 && screenY <= 405) && (currentCredit-200>=0)) { // item 4
                item4++;
                currentCredit -= 200;
            }
            if ((screenX >= 506 && screenX <= 526 && screenY >= 538 && screenY <= 558) && (currentCredit-20000>=0 && item5<1 && !((boolean) DataUserManager.getProgress("state_product_5")))) { // item 5
                item5++;
                currentCredit -= 20000;
            }
            if ((screenX >= 752 && screenX <= 772 && screenY >= 538 && screenY <= 558) && (currentCredit-30000>=0 && item6<1 && !((boolean) DataUserManager.getProgress("state_product_6")))) { // item 6
                item6++;
                currentCredit -= 30000;
            }

            // pulsante reset
            if (screenX >= 790 && screenX <= 922 && screenY >= 580 && screenY <= 624) {
                item1=item2=item3=item4=item5=item6=0; // reset item 1-6
                finalPrize=0; // reset final prize
                currentCredit = ((int) DataUserManager.getProgress("credits")); // reset crediti
            }

            System.out.println(screenX + " " + screenY);
            // pulsante per confermare l'acquisto
            if ((screenX >= 503 && screenX <= 717 && screenY >= 580 && screenY <= 624) && finalPrize>0) {
                secondScreen=open25=true;
            }

            // prezzo finale
            finalPrize = ((int) DataUserManager.getProgress("credits")) - currentCredit;
        }

        // YES => conferma acquisto
        if ((secondScreen&&open25) && (screenX>=281 && screenX<=481) && (screenY>=417 && screenY<=497)) {
            // salvataggio crediti rimasti
            DataUserManager.setProgress("credits", currentCredit);

            // aggiornamento numero carte
            DataUserManager.setProgress("num_gold_heart", ((int)DataUserManager.getProgress("num_gold_heart")+item1));
            DataUserManager.setProgress("num_shield", ((int)DataUserManager.getProgress("num_shield")+item2));
            DataUserManager.setProgress("num_super_laser", ((int)DataUserManager.getProgress("num_super_laser")+item3));
            DataUserManager.setProgress("num_double_points", ((int)DataUserManager.getProgress("num_double_points")+item4));

            // aggiornamento stato prodotto 5
            if (item5 == 1) DataUserManager.setProgress("state_product_5", true);
            // aggiornamento stato prodotto 6
            if (item6 == 1) DataUserManager.setProgress("state_product_6", true);

            item1=item2=item3=item4=item5=item6=0; // reset item 1-6
            finalPrize=0; // reset final prize

            // chiusura schermata in sovra impressione
            secondScreen=open25=false;
        }

        // NO => annulla acquisto
        if ((secondScreen&&open25) && (screenX>=519 && screenX<=719) && (screenY>=417 && screenY<=497)) {
            secondScreen=open25=false;
        }

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
    | CLASSE INNER HitBox |
    +---------------------+
    */
    // classe inner per stabilire il range cliccabile, serve per controllare in maniera più pulita il click su un range
    private static class HitBox {
        int x1, y1, x2, y2;
        int targetPage;
        boolean remembersPrevious;

        // costruttore
        HitBox(int x1, int y1, int x2, int y2, int targetPage, boolean remembersPrevious) {
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
