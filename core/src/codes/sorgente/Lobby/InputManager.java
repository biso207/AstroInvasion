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
import com.badlogic.gdx.math.Rectangle;
import sorgente.DataUserManager;
import sorgente.Entities.Avatar;
import sorgente.Entities.Spacecraft;
import sorgente.GameMods.ClassicGame;
import sorgente.SoundManager;
import sorgente.GameMods.SpaceBattle;
import sorgente.GameMods.SpaceJourney.SpaceJourney;
import sorgente.LogInSignUp.LoginSignupManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InputManager implements InputProcessor {
    // mappa dei range
    private final Map<Integer, HitBox> hitBoxes = new HashMap<>();
    // mappa range navicelle
    private final Map<Rectangle, Integer> clickableAreas = new HashMap<>();

    // variabili per mostrare le schermate in sovra impressione
    protected static boolean secondScreen=false, open14=false, open16=false, open17=false,
        open18=false, open19=false, open20=false;
    // variabili per cambiare lo stile dei pulsanti
    protected static boolean isBtnStartHover=false, isBtnClaimHover=false, isBtnBuyHover=false, isBtnResetHover=false,
        isBtnLHover=false, isBtnRHover=false, isOpenSpHover=false, isBtnGloryHover=false;
    // lista delle pagine secondarie
    private final Set<Integer> listSecondPages = Set.of(4, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
    // 'previousPage' serve a memorizzare l'ultima pagina aperta. //
    protected static int page=0; // settato a zero alla primissima istanza
    private int previousPage;

    // boolean per le carte speciali
    public static boolean goldHeart, shield, superLaser, doublePoints;
    // nome navicella
    private String nameSp = UIManager.selectedSp.getName();
    // stato cambio navicella
    protected static boolean isSPChanged = false;

    // difficoltà classic game e space battle
    private int diffCG = (int)DataUserManager.getProgress("diff_classic_game");
    private int diffSB = (int)DataUserManager.getProgress("diff_space_battle");

    // oggetti negozio
    protected static int item1, item2, item3, item4, item5, item6;
    // prezzo finale negozio
    protected static int finalPrize=0;
    // crediti utente
    protected static int currentCredit;

    // stato attivo per sapere se si sta trascinando il volume
    private boolean draggingSound = false;
    private boolean draggingMusic = false;

    // percentuale audio
    public static float soundPercent, musicPercent;

    protected static float scrollY = 2300, scrollY2 = 2800; // posizioni iniziali delle pagine scrollabili
    private final float maxScrollY = 2300, maxScrollY2 = 2800; // altezza massima delle schermate scrollabili

    // costruttore
    public InputManager() {
        // definizione delle aree cliccabili
        hitAreas();

        // reset stato carte speciali
        goldHeart=shield=superLaser=doublePoints=false;

        // attivazione carte speciali se selezionata una navicella premium
        if (nameSp.equals("Alpha")) goldHeart = true;
        if (nameSp.equals("Astrid")) shield = true;
        if (nameSp.equals("Rorik")) superLaser = true;
        if (nameSp.equals("Drakar")) doublePoints = true;

        // init numero prodotti
        item1=item2=item3=item4=item5=item6=0;
        // recupero crediti
        currentCredit = (int) DataUserManager.getProgress("credits");
        // recupero volume audio
        soundPercent = ((Number) DataUserManager.getProgress("sound_volume")).floatValue();
        musicPercent = ((Number) DataUserManager.getProgress("music_volume")).floatValue();
    }

    // metodo per definire le aree di gioco cliccabili
    public void hitAreas() {
        hitBoxes.put(0, new HitBox(42, 182, 247, 200, 0, false));  // 'classic game'
        hitBoxes.put(1, new HitBox(44, 232, 241, 250, 1, false)); // 'space battle'
        hitBoxes.put(2, new HitBox(42, 285, 260, 303, 2, false)); // 'space journey'
        hitBoxes.put(3, new HitBox(43, 336, 194, 354, 3, false)); // 'missions'
        hitBoxes.put(4, new HitBox(44, 389, 241, 407, 4, true)); // 'spacecrafts'
        hitBoxes.put(5, new HitBox(44, 441, 247, 463, 5, false)); // 'marketplace'
        hitBoxes.put(12, new HitBox(45, 493, 236, 514, 12, true));  // 'instructions'
        // le pagine seguenti hanno da memorizzare previousPage
        hitBoxes.put(6, new HitBox(858, 62, 943, 145, 6, true));  // 'profile infos'
    }

    // metodo per popolare le aree cliccabili nella selezione delle navicelle
    private void selectSPAreas() {
        // popolamento mappa range per la selezione delle navicelle
        int x1=82, x2=471, x3=493, x4=882, y1=342, y2=486;
        int spID=0; // id della navicella
        Rectangle area; // rappresenta l'area cliccabile

        for (int i=0; i<6; i++) {
            for (int j=0; j<4; j++) {
                if (j==0 || j==2) area = new Rectangle(x1, y1, x2-x1, y2-y1);
                else area = new Rectangle(x3, y1, x4-x3, y2-y1);

                // aggiunta del range solo se la navicella è cliccabile
                if (SpacecraftData.isAchieved(spID)) clickableAreas.put(area, spID);
                if (j==1)  { y1+=168; y2+=168; } // passaggio alla riga seguente

                // passaggio alla navicella successiva
                spID++;
            }
            // passaggio al gruppo successivo
            y1+= 257; y2+=257;
        }
    }

    // controlla il margine di scorrimento del mouse
    private void clampScroll() {
        if (page==4) { // scroll pagina navicelle
            if (scrollY < 0) scrollY = 0;
            if (scrollY > maxScrollY) scrollY = maxScrollY;
        }
        else { // scroll pagina info di gioco
            if (scrollY2 < 0) scrollY2 = 0;
            if (scrollY2 > maxScrollY2) scrollY2 = maxScrollY2;
        }
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    // metodo per rilevare il click della tastiera
    @Override public boolean keyDown(int keycode) {
        // click tasto esc per il logout
        if (keycode == Input.Keys.ESCAPE && (!listSecondPages.contains(page) && !open14 && !open16 && !open17 && !open18 && !open19 && !open20)) {
            open14 = true;
            secondScreen = true;
            return true;
        }

        // click tasto esc per annullare il logout
        if (keycode == Input.Keys.ESCAPE && (secondScreen&&open14)) {
            open14 = false;
            secondScreen = false;
        }

        return true;
    }

    // metodo per controllare i click del mouse
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //SoundManager.playClickButton(soundPercent); // riproduzione suono click

        /*
        'page' deve essere diverso da certe pagine per non generare l'apertura
        di altre pagine dove non è possibile e poter cambiare le schermate della lobby.
        esempio: l'utente NON può avviare il 'classic game' da una pagina in sovra impressione o esterna che riempe lo schermo
        */

        // **************************************** //
        // CAMBIO PAGINE LOBBY + CLICK NELLE PAGINE //
        // **************************************** //
        if (!listSecondPages.contains(page) && !open14 && !open16 && !open17 && !open18 && !open19 && !open20) {

            // CAMBIO PAGINE LOBBY
            // for-each per iterare i vari range e controllare i cambi pagina
            for (Map.Entry<Integer, HitBox> entry : hitBoxes.entrySet()) {
                HitBox hb = entry.getValue();
                if (hb.isInside(screenX, screenY)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    if (hb.targetPage==4) scrollY = 2300; // reset altezza pagina navicelle, si apre partendo dall'alto
                    if (hb.targetPage==12) scrollY2 = 2800;// reset altezza pagina info di gioco, si apre partendo dall'alto
                    if (hb.remembersPrevious) previousPage = page; // memorizzazione pagina precedente
                    page = hb.targetPage; // cambio pagina
                    break;
                }
            }

            // CLICK NELLE PAGINE
            // apertura pagina 'spacecrafts' cliccando sulla navicella
            if ((page==0||page==1||page==2) && (screenX>=314 && screenX<=538) && (screenY>=461 && screenY<=594)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                scrollY = 2300; // reset altezza pagina navicelle, si apre partendo dall'alto
                previousPage = page;
                page=4;
            }
            // apertura pagina 'software infos'
            if ((screenX>=99 && screenX<=126) && (screenY>=600 && screenY<=630)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                open18 = true;
                secondScreen = true;
            }
            // apertura pagina 'logout'
            if ((screenX>=154 && screenX<=183) && (screenY>=600 && screenY<=630)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                open14 = true;
                secondScreen = true;
            }
            // apertura pagina 'settings'
            if ((screenX>=44 && screenX<=71) && (screenY>=600 && screenY<=630)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                open17 = true;
                secondScreen = true;
            }
            // apertura pagina 10/11 (difficulty infos classic game/space battle)
            if ((screenX>=614 && screenX<=694) && (screenY>=551 && screenY<=592)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                if (page == 0) { previousPage = page; page = 10; }
                else if (page == 1) { previousPage = page; page = 11; }
            }
            // apertura pagina 20 (cards infos)
            if ((page == 0 || page == 1) && (screenX>=880 && screenX<=905) && (screenY>=255 && screenY<=280)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                previousPage = page;
                page = 8;
            }
            // apertura pagina 21 (credits x win infos)
            if (page==1 && (screenX>=880 && screenX<=905) && (screenY>=397 && screenY<=422)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                previousPage = page;
                page = 9;
            }

            // controllo per avviare le modalità di gioco
            if ((screenX >= 769 && screenX <= 920) && (screenY >= 550 && screenY <= 593)) {
                // avvio modalità di gioco
                if (page == 0) {
                    // controllo per l'avviso difficoltà
                    if ((nameSp.equals("Omega") || nameSp.equals("Idra") || nameSp.equals("Pegaso") || nameSp.equals("Woka")) && diffCG == 3d) {
                        secondScreen = open19 = true;
                    } else {
                        SoundManager.playClickButton(soundPercent); // riproduzione suono click
                        LobbyManager.soundtrack.stop(); // interruzione musica
                        LobbyManager.game.setScreen(new ClassicGame(LobbyManager.game, UIManager.selectedSp, false)); // avvio classic game
                    }
                } else if (page == 1 && ((int)DataUserManager.getProgress("level") > 10)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    LobbyManager.soundtrack.stop(); // interruzione musica
                    LobbyManager.game.setScreen(new SpaceBattle(LobbyManager.game, UIManager.selectedSp, false)); // avvio space battle
                } else if (page == 2) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    LobbyManager.soundtrack.stop(); // interruzione musica
                    LobbyManager.game.setScreen(new SpaceJourney(LobbyManager.game, UIManager.selectedSp, 0)); // apertura mappa space journey
                }
            }

            // cambio difficoltà classic game
            if (page == 0 && (screenX >= 700 && screenX <= 720) && (screenY >= 560 && screenY <= 584) && diffCG < 3) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                diffCG++;
                DataUserManager.setProgress("diff_classic_game", diffCG);
            }
            if (page == 0 && (screenX >= 587 && screenX <= 607) && (screenY >= 560 && screenY <= 584) && diffCG > 1) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                diffCG--;
                DataUserManager.setProgress("diff_classic_game", diffCG);
            }

            // cambio difficoltà space battle
            if (page == 1 && (screenX >= 700 && screenX <= 720) && (screenY >= 560 && screenY <= 584) && diffSB < 3) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                diffSB++;
                DataUserManager.setProgress("diff_space_battle", diffSB);
            }
            if (page == 1 && (screenX >= 587 && screenX <= 607) && (screenY >= 560 && screenY <= 584) && diffSB > 1) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                diffSB--;
                DataUserManager.setProgress("diff_space_battle", diffSB);
            }

            // selezione carte speciali //
            // gold heart
            if ((page == 0 || page == 1) && ((int) DataUserManager.getProgress("num_gold_heart") > 0) && (!nameSp.equals("Alpha")) && ((screenX >= 680 && screenX <= 750) && (screenY >= 253 && screenY <= 323))) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                goldHeart = !goldHeart;

                // disattivazione altre carte
                if (!nameSp.equals("Astrid")) shield = false;
                if (!nameSp.equals("Rorik")) superLaser = false;
                if (!nameSp.equals("Drakar")) doublePoints = false;
            }
            // shield
            if (((screenX >= 793 && screenX <= 863) && (screenY >= 253 && screenY <= 323))) {
                if (page == 0 && ((int) DataUserManager.getProgress("num_shield") > 0) && !nameSp.equals("Astrid")) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    shield = !shield;

                    // disattivazione altre carte
                    if (!nameSp.equals("Alpha")) goldHeart = false;
                    if (!nameSp.equals("Drakar")) doublePoints = false;
                    if (!nameSp.equals("Rorik")) superLaser = false;
                } else if (page == 1 && ((int) DataUserManager.getProgress("num_super_laser") > 0) && !nameSp.equals("Rorik")) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    superLaser = !superLaser;

                    // disattivazione altre carte
                    if (!nameSp.equals("Alpha")) goldHeart = false;
                    if (!nameSp.equals("Drakar")) doublePoints = false;
                    if (!nameSp.equals("Astrid")) shield = false;
                }
            }
            // super laser
            if (page == 0 && ((int) DataUserManager.getProgress("num_super_laser") > 0) && (!nameSp.equals("Rorik")) && ((screenX >= 680 && screenX <= 750) && (screenY >= 368 && screenY <= 438))) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                superLaser = !superLaser;

                // disattivazione altre carte
                if (!nameSp.equals("Alpha")) goldHeart = false;
                if (!nameSp.equals("Astrid")) shield = false;
                if (!nameSp.equals("Drakar")) doublePoints = false;
            }
            // double points
            if (page == 0 && ((int) DataUserManager.getProgress("num_double_points") > 0) && (!nameSp.equals("Drakar")) && ((screenX >= 793 && screenX <= 863) && (screenY >= 368 && screenY <= 438))) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                doublePoints = !doublePoints;

                // disattivazione altre carte
                if (!nameSp.equals("Alpha")) goldHeart = false;
                if (!nameSp.equals("Rorik")) superLaser = false;
                if (!nameSp.equals("Astrid")) shield = false;
            }

            // claim reward delle missions
            if (page == 3 && ((boolean) DataUserManager.getProgress("completed_mission")) && (screenX >= 762 && screenX <= 898) && (screenY >= 561 && screenY <= 595)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click

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
                        DataUserManager.setProgress("num_aliens_hit_missions", 0); // progressi missione azzerati
                        DataUserManager.setProgress("num_gold_heart", numGoldHeart + 1); // aggiunta carta
                        missionID++;
                        break;
                    case 2:
                        DataUserManager.setProgress("wins_SB_missions", 0); // azzeramento partite vinte
                        DataUserManager.setProgress("num_shield", numShield + 1); // aggiunta carta
                        missionID++;
                        break;
                    case 3:
                        DataUserManager.setProgress("points_missions", 0); // progressi missione azzerati
                        DataUserManager.setProgress("credits", credits + 100);// aggiunta 100 crediti
                        DataUserManager.setProgress("total_credits", (int) DataUserManager.getProgress("total_credits") + 100);// aggiunta 100 crediti totali
                        currentCredit+=100;
                        missionID++;
                        break;
                    case 4:
                        DataUserManager.setProgress("credits_missions", 0); // progressi missione azzerati
                        DataUserManager.setProgress("num_super_laser", numSuperLaser + 1); // aggiunta carta
                        missionID = 1;
                        break;
                }

                DataUserManager.setProgress("num_mission", mission + 1);
                DataUserManager.setProgress("mission_id", missionID);
                DataUserManager.setProgress("completed_mission", false); // Missions non più completata
            }

            // acquisti nel negozio
            if (page == 5) {
                // rimozione prodotto
                if ((screenX >= 344 && screenX <= 364 && screenY >= 385 && screenY <= 405) && item1 > 0) { // item 1
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    item1--;
                    currentCredit += 50;
                }
                if ((screenX >= 494 && screenX <= 514 && screenY >= 385 && screenY <= 405) && item2 > 0) { // item 2
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    item2--;
                    currentCredit += 100;
                }
                if ((screenX >= 644 && screenX <= 664 && screenY >= 385 && screenY <= 405) && item3 > 0) { // item 3
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    item3--;
                    currentCredit += 200;
                }
                if ((screenX >= 794 && screenX <= 814 && screenY >= 385 && screenY <= 405) && item4 > 0) { // item 4
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    item4--;
                    currentCredit += 300;
                }
                if ((screenX >= 442 && screenX <= 462 && screenY >= 538 && screenY <= 558) && item5 > 0) { // item 5
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    item5--;
                    currentCredit += 20000;
                }
                if ((screenX >= 688 && screenX <= 708 && screenY >= 538 && screenY <= 558) && item6 > 0) { // item 6
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    item6--;
                    currentCredit += 30000;
                }

                // aggiunta prodotto
                if ((screenX >= 409 && screenX <= 429 && screenY >= 385 && screenY <= 405) && (currentCredit - 50 >= 0)) { // item 1
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    item1++;
                    currentCredit -= 50;
                }
                if ((screenX >= 559 && screenX <= 579 && screenY >= 385 && screenY <= 405) && (currentCredit - 100 >= 0)) { // item 2
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    item2++;
                    currentCredit -= 100;
                }
                if ((screenX >= 709 && screenX <= 729 && screenY >= 385 && screenY <= 405) && (currentCredit - 200 >= 0)) { // item 3
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    item3++;
                    currentCredit -= 200;
                }
                if ((screenX >= 859 && screenX <= 879 && screenY >= 385 && screenY <= 405) && (currentCredit - 300 >= 0)) { // item 4
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    item4++;
                    currentCredit -= 300;
                }
                if ((screenX >= 506 && screenX <= 526 && screenY >= 538 && screenY <= 558) && (currentCredit - 20000 >= 0 && item5 < 1 && !((boolean) DataUserManager.getProgress("state_product_5")))) { // item 5
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    item5++;
                    currentCredit -= 20000;
                }
                if ((screenX >= 752 && screenX <= 772 && screenY >= 538 && screenY <= 558) && (currentCredit - 30000 >= 0 && item6 < 1 && !((boolean) DataUserManager.getProgress("state_product_6")))) { // item 6
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    item6++;
                    currentCredit -= 30000;
                }

                // prezzo finale
                finalPrize = ((int) DataUserManager.getProgress("credits")) - currentCredit;

                // pulsante reset
                if (screenX >= 790 && screenX <= 922 && screenY >= 580 && screenY <= 624 && finalPrize>0) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    item1 = item2 = item3 = item4 = item5 = item6 = 0; // reset item 1-6
                    finalPrize = 0; // reset final prize
                    currentCredit = ((int) DataUserManager.getProgress("credits")); // reset crediti
                }

                // pulsante per confermare l'acquisto
                if ((screenX >= 503 && screenX <= 717 && screenY >= 580 && screenY <= 624) && finalPrize > 0) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    secondScreen = open16 = true;
                }
            }
        }

        // *************************************************** //
        // CLICK NELLE PAGINE IN SOVRAIMPRESSIONE E SECONDARIE //
        // *************************************************** //
        else {
            // controllo selezione navicella
            if (page == 4) {
                // posto qui per aggiornare la mappa in caso di nuovo sblocco quando si rimane nella stessa istanza
                selectSPAreas(); // definizione aree navicelle cliccabili

                int selectedId; // id navicella
                for (Map.Entry<Rectangle, Integer> entry : clickableAreas.entrySet()) {
                    Rectangle area = entry.getKey();
                    if (area.contains(screenX, screenY+(maxScrollY - scrollY))) {
                        selectedId = entry.getValue(); // recupero id navicella selezionata


                        // cambio navicella e salvataggio navicella scelta se sbloccata
                        if (SpacecraftData.isAchieved(selectedId)) {
                            SoundManager.playClickButton(soundPercent); // riproduzione suono click

                            DataUserManager.setProgress("spacecraft", selectedId);
                            // cambio stato selezione a true
                            isSPChanged = true;
                            // cambio nome navicella con creazione nuovo oggetto
                            Spacecraft s = UIManager.selectSpacecraft();
                            nameSp = s.getName();

                            // reset stato carte per sicurezza
                            goldHeart=shield=superLaser=doublePoints=false;

                            // riattivazione carte speciali se selezionata una navicella premium
                            if (nameSp.equals("Alpha")) goldHeart = true;
                            if (nameSp.equals("Astrid")) shield = true;
                            if (nameSp.equals("Rorik")) superLaser = true;
                            if (nameSp.equals("Drakar")) doublePoints = true;
                        }
                        break;
                    }
                }
            }

            // apertura pagina 7 'avatar'
            if (!open20 && page == 6 && ((screenX >= 453 && screenX <= 537) && (screenY >= 108 && screenY <= 188))) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                page = 7;
            }
            // apertura pagina "gloria utente"
            if (page == 6 && ((screenX>=30 && screenX<=484) && (screenY>=488 && screenY<=555))) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open20 = true;
            }

            // selezione avatar
            if (page == 7) {
                int x = 133, y = 220; // posizione avatar 1
                for (int i = 0; i <= 19; i++) {
                    if ((screenX >= x && screenX <= x + 66) && (screenY >= y && screenY <= y + 66)) {

                        // selezione avatar e salvataggio scelta in memoria
                        if (Avatar.isAchieved(i)) {
                            SoundManager.playClickButton(soundPercent); // riproduzione suono click
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

            // X sp => chiusura pagina navicelle/info di gioco
            if ((page==4 || page==12)  && (screenX >= 908 && screenX <= 948) && (screenY >= 67 && screenY <= 107)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                page = previousPage;
            }

            //((screenY+(maxScrollY2 - scrollY2)) >= 67 && (screenY+(maxScrollY2 - scrollY2)) <= 107))

            // X glory => chiusura pagina glory
            if ((secondScreen && open20) && (screenX>=670 && screenX<=710) && (screenY>=165 && screenY<=205)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open20 = false;
            }

            // X others => chiusura pagina info profilo-difficoltà-carte; avatar
            if (!open20 && !((page==4 || page==12)) && (screenX >= 905 && screenX <= 945) && (screenY >= 83 && screenY <= 123)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                if (page == 7) page = 6; // evita di tornare alla lobby dalla pagina degli avatar
                else page = previousPage;
            }

            // chiusura software infos
            if ((secondScreen && open18) && (screenX >= 684 && screenX <= 724) && (screenY >= 206 && screenY <= 246)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open18 = false;
            }

            // NO logout => si continua nella sessione di gioco
            if ((secondScreen && open14) && (screenX >= 510 && screenX <= 714) && (screenY >= 404 && screenY <= 480)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open14 = false;
            }

            // YES logout => back to the Authentication Page
            if ((secondScreen && open14) && (screenX >= 270 && screenX <= 472) && (screenY >= 404 && screenY <= 480)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open14 = false;
                LobbyManager.soundtrack.stop();
                LobbyManager.game.setScreen(new LoginSignupManager(LobbyManager.game));
            }

            // OK warning => close warning and back to lobby
            if ((secondScreen && open19) && (screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open19 = false;
            }

            // PLAY warning => play classic game
            if ((secondScreen && open19) && (screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open19 = false;
                LobbyManager.soundtrack.stop();
                LobbyManager.game.setScreen(new ClassicGame(LobbyManager.game, UIManager.selectedSp, false)); // avvio classic game
            }

            // YES => conferma acquisto
            if ((secondScreen && open16) && (screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                // salvataggio crediti rimasti
                DataUserManager.setProgress("credits", currentCredit);

                // aggiornamento numero carte
                DataUserManager.setProgress("num_gold_heart", ((int) DataUserManager.getProgress("num_gold_heart") + item1));
                DataUserManager.setProgress("num_shield", ((int) DataUserManager.getProgress("num_shield") + item2));
                DataUserManager.setProgress("num_super_laser", ((int) DataUserManager.getProgress("num_super_laser") + item3));
                DataUserManager.setProgress("num_double_points", ((int) DataUserManager.getProgress("num_double_points") + item4));

                // aggiornamento stato prodotto 5
                if (item5 == 1) DataUserManager.setProgress("state_product_5", true);
                // aggiornamento stato prodotto 6
                if (item6 == 1) DataUserManager.setProgress("state_product_6", true);

                item1 = item2 = item3 = item4 = item5 = item6 = 0; // reset item 1-6
                finalPrize = 0; // reset final prize

                // chiusura schermata in sovra impressione
                secondScreen = open16 = false;
            }

            // NO => annulla acquisto
            if ((secondScreen && open16) && (screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open16 = false;
            }

            // chiusura pagina impostazioni
            if ((secondScreen && open17) && (screenX >= 720 && screenX <= 760) && (screenY >= 78 && screenY <= 118)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open17 = false;
            }

            // setting impostazioni
            if ((secondScreen && open17)) {
                // tipo di movimento
                if ((screenX >= 259 && screenX <= 421) && (screenY >= 195 && screenY <= 272)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    DataUserManager.setProgress("movement_type", 1);
                }
                if ((screenX >= 259 && screenX <= 421) && (screenY >= 290 && screenY <= 367)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    DataUserManager.setProgress("movement_type", 2);
                }

                // tipo di sparo
                if ((screenX >= 549 && screenX <= 711) && (screenY >= 195 && screenY <= 272)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    DataUserManager.setProgress("shot_type", 1);
                }
                if ((screenX >= 549 && screenX <= 711) && (screenY >= 290 && screenY <= 367)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    DataUserManager.setProgress("shot_type", 2);
                }

                // cambio volume suoni
                if ((screenY >= 459 - 10 && screenY <= 459 + 10) &&
                    (screenX >= 285 && screenX <= 685)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    draggingSound = true;
                }
                // cambio volume musica
                if ((screenY >= 530 - 10 && screenY <= 530 + 10) &&
                    (screenX >= 285 && screenX <= 685)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    draggingMusic = true;
                }

                // azzeramento volume musica/suoni al click sulle icone
                if (screenX>=224 && screenX<=269 && screenY>=505 && screenY<=550) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    musicPercent=0;
                    DataUserManager.setProgress("music_volume", musicPercent); // salvataggio volume musica
                }
                if (screenX>=224 && screenX<=269 && screenY>=435 && screenY<=480) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    soundPercent=0;
                    DataUserManager.setProgress("sound_volume", soundPercent); // salvataggio volume audio
                }
            }
        }

        return true;
    }

    // cambio icona mouse
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // finché si muove fuori dai pulsanti rimangono spenti, con le grafiche di base
        isBtnStartHover=isBtnClaimHover=isBtnBuyHover=isBtnResetHover=isBtnLHover=isBtnRHover=isOpenSpHover=isBtnGloryHover=false;
        // cambio cursore
        Gdx.graphics.setCursor(UIManager.cursor);

        // CAMBIO STILE PULSANTI
        // YES logout / OK warning / YES purchase
        if ((secondScreen && (open14 || open19 || open16)) && (screenX >= 270 && screenX <= 472) && (screenY >= 404 && screenY <= 480)) {
            isBtnLHover=true;
        }

        // NO logout / PLAY warning / NO purchase
        if ((secondScreen && (open14 || open19 || open16)) && (screenX >= 510 && screenX <= 714) && (screenY >= 404 && screenY <= 480)) {
            isBtnRHover=true;
        }

        // profile info
        if (page==6 && !open20 && (screenX>=30 && screenX<=484) && (screenY>=488 && screenY<=555)) isBtnGloryHover=true;

        if (!listSecondPages.contains(page) && !open14 && !open16 && !open17 && !open18 && !open19 && !open20) {
            // Missions
            if (page == 3 && ((boolean) DataUserManager.getProgress("completed_mission")) && (screenX >= 762 && screenX <= 898) && (screenY >= 561 && screenY <= 595)) isBtnClaimHover=true;

            // market
            if (page==5) {
                // pulsante reset
                if ((screenX >= 790 && screenX <= 922 && screenY >= 580 && screenY <= 624) && finalPrize > 0) isBtnResetHover=true;
                // pulsante per confermare l'acquisto
                if ((screenX >= 503 && screenX <= 717 && screenY >= 580 && screenY <= 624) && finalPrize > 0) isBtnBuyHover=true;
            }

            // avvio modalità di gioco
            if ((page==0 || page==2 || (page==1 && ((int)DataUserManager.getProgress("level") > 10))) && (screenX >= 769 && screenX <= 920) && (screenY >= 550 && screenY <= 593)) {
                isBtnStartHover=true;
            }

            // apertura pagina 'spacecrafts' dalle pagine classic game/space battle/space journey
            if ((page==0||page==1||page==2) && (screenX>=314 && screenX<=538) && (screenY>=461 && screenY<=594)) {
                isOpenSpHover=true;
            }
        }

        return true;
    }

    // rilascio del mouse
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // stato di false per il movimento delle barre volume
        if (secondScreen && open17) {
            draggingSound = false;
            draggingMusic = false;
        }
        return true;
    }

    // click continuato e movimento del mouse
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) {
        // controlli per il movimento delle barre volume
        if (secondScreen && open17) {
            if (draggingSound) {
                soundPercent = Math.min(1f, Math.max(0f, (screenX - 285) / (float) (685 - 285)));
                DataUserManager.setProgress("sound_volume", soundPercent); // salvataggio volume audio
            }

            if (draggingMusic) {
                musicPercent = Math.min(1f, Math.max(0f, (screenX - 285) / (float) (685 - 285)));
                DataUserManager.setProgress("music_volume", musicPercent); // salvataggio volume musica
            }
        }
        return true;
    }

    // gestisce lo scorrimento del mouse sulla schermata
    @Override
    public boolean scrolled(float amountX, float amountY) {
        // diminuzione di Y per la pagina delle navicelle => deve essere tra 2300 e 0 compresi
        scrollY -= amountY * 50f;
        // diminuzione di Y per la pagina delle info => deve essere tra 3500 e 0 compresi
        scrollY2 -= amountY * 700f;

        // controllo posizione
        clampScroll();
        return true;
    }

    // altri metodi
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
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
