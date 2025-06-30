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
import sorgente.Missions.CheckRTG;
import sorgente.UserData.DataUserManager;
import sorgente.Entities.Avatar;
import sorgente.Entities.Spacecraft;
import sorgente.GameMods.ClassicGame;

import sorgente.LogInSignUp.AuthAlgorithms;
import sorgente.UserData.CloudStorageManager;
import sorgente.SoundManager;
import sorgente.GameMods.SpaceBattle;
import sorgente.GameMods.SpaceJourney.SpaceJourney;
import sorgente.LogInSignUp.LoginSignupManager;
import sorgente.UserData.SessionLockManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static sorgente.LogInSignUp.AuthAlgorithms.checkInternetConnection;

public class InputManager implements InputProcessor {
    // mappa dei range
    private final Map<Integer, HitBox> hitBoxes = new HashMap<>();
    // mappa range navicelle
    private final Map<Rectangle, Integer> clickableAreas = new HashMap<>();

    // variabili per mostrare le schermate in sovra impressione
    protected static boolean secondScreen=false, open13=false, open14=false, open16=false,
        open17=false, open18=false, open19=false, open20=false, open21=false, open22=false, open23=false;
    // variabili per cambiare lo stile dei pulsanti
    protected static boolean isBtnStartHover=false, isBtnClaimHover=false, isBtnBuyHover=false, isBtnResetHover=false,
        isBtnLHover=false, isBtnRHover=false, isOpenSpHover=false, isBtnGloryHover=false, isTickSelected=false,
        isHoverIconNoInternet=false, isDeleteAccountHover=false, isBtnDeleteHover=false,
        isBtnChangePSWHover=false, showPS=false, isBtnWiseManHover=false, isGameCompleted=false;

    // lista delle pagine secondarie
    private final Set<Integer> listSecondPages = Set.of(4, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
    // 'previousPage' serve a memorizzare l'ultima pagina aperta. //
    protected static int page=0; // settato a zero alla primissima istanza
    private int previousPage;

    // variabili per comporre la stringa della nuova password
    protected static StringBuilder passwordInput;

    // boolean per le carte speciali
    public static boolean goldHeart, shield, superLaser, doublePoints;
    // nome navicella
    private String nameSp = UIManager.selectedSp.getName();
    // stato cambio navicella
    protected static boolean isSPChanged=false, isAVChanged=false;

    // recupero difficoltà classic game e space battle
    private int diffCG = (int) DataUserManager.getProgress("diff_classic_game");
    private int diffSB = (int) DataUserManager.getProgress("diff_space_battle");

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
    // comandi di gioco
    public static int movementType, shotType;

    // elementi selezionati cambiati => verranno aggiornati sul server non a ogni click
    public static int numSelectedSP, selectedAvatar;

    protected static float scrollY = 2300, scrollY2 = 2800; // posizioni iniziali delle pagine scrollabili
    private final float maxScrollY = 2300;

    // istanza di UIManager
    private final UIManager ui;

    // costruttore
    public InputManager(UIManager ui) {
        this.ui = ui;
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

        passwordInput = new StringBuilder();

        // recupero volume audio
        soundPercent = ((Number) DataUserManager.getProgress("sound_volume")).floatValue();
        musicPercent = ((Number) DataUserManager.getProgress("music_volume")).floatValue();
        // recupero comandi
        movementType = (int) DataUserManager.getProgress("movement_type");
        shotType = (int) DataUserManager.getProgress("shot_type");
        // recupero navicella e avatar
        selectedAvatar = (int) DataUserManager.getProgress("avatar");
        numSelectedSP = (int) DataUserManager.getProgress("spacecraft");
    }

    // metodo per definire le aree di gioco cliccabili
    public void hitAreas() {
        hitBoxes.put(0, new HitBox(42, 235, 247, 253, 0, false));  // 'classic game'
        hitBoxes.put(1, new HitBox(44, 285, 241, 303, 1, false)); // 'space battle'
        hitBoxes.put(2, new HitBox(42, 338, 260, 356, 2, false)); // 'space journey'
        hitBoxes.put(3, new HitBox(43, 389, 194, 407, 3, false)); // 'missions'
        hitBoxes.put(4, new HitBox(44, 442, 241, 460, 4, true)); // 'spacecrafts'
        hitBoxes.put(5, new HitBox(44, 494, 247, 516, 5, false)); // 'marketplace'
        hitBoxes.put(12, new HitBox(45, 546, 236, 567, 12, true));  // 'how to play'

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
                if (Spacecraft.isAchieved(spID)) clickableAreas.put(area, spID);
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
            // altezza massima delle schermate scrollabili
            float maxScrollY2 = 2800;
            if (scrollY2 > maxScrollY2) scrollY2 = maxScrollY2;
        }
    }

    // metodo per completare il completamento del gioco
    public void checkCompleteGame() {
        int cont=0;
        for (int i=0; i<5; i++) {
            cont++;
        }
        if (cont==4) isGameCompleted=true;
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    // metodo per rilevare il click della tastiera
    @Override public boolean keyDown(int keycode) {
        // click tasto esc per il logout
        if (keycode == Input.Keys.ESCAPE && (!listSecondPages.contains(page) && !open13 && !open14 && !open16 && !open17
            && !open18 && !open19 && !open20 && !open21 && !open22 && !open23)) {
            open13 = true;
            secondScreen = true;
            return true;
        }

        // click tasto esc per annullare il logout
        if (keycode == Input.Keys.ESCAPE && (secondScreen&&open13)) {
            open13 = false;
            secondScreen = false;
        }

        return true;
    }

    // metodo per controllare i click del mouse
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        /*
        'page' deve essere diverso da certe pagine per non generare l'apertura
        di altre pagine dove non è possibile e poter cambiare le schermate della lobby.
        esempio: l'utente NON può avviare il 'classic game' da una pagina in sovra impressione o esterna che riempe lo schermo
        */

        System.out.println("screenX: "+screenX+" screenY: "+screenY);

        // **************************************** //
        // CAMBIO PAGINE LOBBY + CLICK NELLE PAGINE //
        // **************************************** //
        if (!listSecondPages.contains(page) && !open13 && !open14 && !open16 && !open17 && !open18 && !open19 &&
            !open20 && !open21 && !open22 && !open23) {

            // CAMBIO PAGINE LOBBY
            // for-each per iterare i vari range e controllare i cambi pagina
            for (Map.Entry<Integer, HitBox> entry : hitBoxes.entrySet()) {
                HitBox hb = entry.getValue();
                if (hb.isInside(screenX, screenY)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    if (hb.targetPage==4) scrollY = 2300; // reset altezza pagina navicelle, si apre partendo dall'alto
                    if (hb.targetPage==12) scrollY2 = 2800; // reset altezza pagina info di gioco, si apre partendo dall'alto
                    if (hb.targetPage==20) { open20 = true; secondScreen = true; } // pagina leaderboard
                    if (hb.targetPage==6) checkCompleteGame(); // controllo completamento missioni del RTG (completamento gioco)

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
            // apertura leaderboard
            if ((screenX>=45 && screenX<=75) && (screenY>=597 && screenY<=627)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click

                // recupero dal cloud di tutti i punti utente
                try { CloudStorageManager.loadAllUserPoints(); }
                catch (Exception e) { System.out.println(e.getMessage()); }

                open20 = true;
                secondScreen = true;
            }
            // apertura pagina 'settings'
            if ((screenX>=102 && screenX<=132) && (screenY>=597 && screenY<=627)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                open16 = true;
                secondScreen = true;
            }
            // apertura pagina 'software infos'
            if ((screenX>=154 && screenX<=184) && (screenY>=597 && screenY<=627)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                open17 = true;
                secondScreen = true;
            }
            // apertura pagina 'logout'
            if ((screenX>=202 && screenX<=232) && (screenY>=597 && screenY<=627)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                open13 = true;
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
                    if ((nameSp.equals("Omega") || nameSp.equals("Idra") || nameSp.equals("Pegaso") || nameSp.equals("Woka"))
                        && diffCG == 3d && (boolean)DataUserManager.getProgress("show_warning")) {
                        secondScreen = open18 = true;
                    } else {
                        SoundManager.playClickButton(soundPercent); // riproduzione suono click
                        LobbyManager.soundtrack.stop(); // interruzione musica

                        LobbyManager.game.setScreen(new ClassicGame(LobbyManager.game, false)); // avvio classic game
                        ui.disposeUI(); // rilascio risorse
                    }
                } else if (page == 1 && ((int) DataUserManager.getProgress("level") > 10)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    LobbyManager.soundtrack.stop(); // interruzione musica

                    LobbyManager.game.setScreen(new SpaceBattle(LobbyManager.game,false)); // avvio space battle
                    ui.disposeUI(); // rilascio risorse
                } else if (page == 2) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    LobbyManager.soundtrack.stop(); // interruzione musica

                    LobbyManager.game.setScreen(new SpaceJourney(LobbyManager.game, UIManager.selectedSp, 0)); // apertura mappa space journey
                    ui.disposeUI(); // rilascio risorse
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

                DataUserManager.setProgress("completed_mission", false); // mission non più completata
                DataUserManager.setProgress("num_mission", mission + 1);
                DataUserManager.setProgress("mission_id", missionID);
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
                    secondScreen = open14 = true;
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
                        numSelectedSP = selectedId;

                        // cambio navicella e salvataggio navicella scelta se sbloccata
                        if (Spacecraft.isAchieved(selectedId)) {
                            SoundManager.playClickButton(soundPercent); // riproduzione suono click

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

            // pagine secondarie dalla pagina 6 (profile infos)
            if (page==6) {
                boolean nothingOpen = (!open19 && !open21 && !open22 && !open23);
                // apertura pagina 7 'avatar'
                if (!open19 && nothingOpen && ((screenX >= 453 && screenX <= 537) && (screenY >= 108 && screenY <= 188))) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    page = 7;
                }
                // apertura pagina "gloria utente"
                if (nothingOpen && ((screenX >= 170 && screenX <= 426) && (screenY >= 217 && screenY <= 280))) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    secondScreen = open19 = true;
                }
                // apertura password change
                if (nothingOpen && (screenX >= 434 && screenX <= 454) && (screenY >= 398 && screenY <= 416)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    secondScreen = open21 = true;
                }
                // apertura delete profile
                if (nothingOpen && (screenX >= 424 && screenX <= 460) && (screenY >= 307 && screenY <= 343)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    secondScreen = open22 = true;
                }
                // apertura wise man
                if (isGameCompleted && nothingOpen && (screenX>=84 && screenX<=147) && (screenY>=218 && screenY<=281)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    secondScreen = open23 = true;
                }
            }


            // eliminazione profilo
            if (open22 && (screenX>=415 && screenX<=565) && (screenY>=438 && screenY<=488)) {
                SessionLockManager.shutdownAll(); // rilascio del lock => altrimenti il documento si ricrea anche se eliminato
                try {
                    CloudStorageManager.deleteUserProfile(AuthAlgorithms.nickname); // richiamo metodo per eliminazione profilo
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                ui.disposeUI(); // rilascio risorse
                // apertura pagina autenticazione
                LobbyManager.game.setScreen(new LoginSignupManager(LobbyManager.game));
            }

            // mostra/nascondi password
            if (open21 && (screenX>=674 && screenX<=704) && (screenY>=364 && screenY<=394)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                showPS=!showPS;
            }
            // salvataggio nuova password
            if (open21 && !passwordInput.isEmpty() && (screenX>=415 && screenX<=565) && (screenY>=438 && screenY<=488) && (!AuthAlgorithms.password.contentEquals(passwordInput))) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                // cambio valori variabili
                String newPassword = passwordInput.toString();
                AuthAlgorithms.password = newPassword;

                // sovrascrittura in remoto della nuova password
                try {
                    CloudStorageManager.savePassword(AuthAlgorithms.nickname, newPassword);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                passwordInput.setLength(0); // reset lunghezza per futuri cambi
                secondScreen = open21 = false; // chiusura pagina cambio password
            }

            // selezione avatar
            if (page == 7) {
                int x = 133, y = 220; // posizione avatar 1
                for (int i = 0; i <= 19; i++) {
                    if ((screenX >= x && screenX <= x + 66) && (screenY >= y && screenY <= y + 66)) {

                        // selezione avatar e salvataggio scelta in memoria
                        if (Avatar.isAchieved(i)) {
                            SoundManager.playClickButton(soundPercent); // riproduzione suono click
                            selectedAvatar = i;
                            isAVChanged = true;
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
                // salvataggio navicella selezionata
                if (page==4) {
                    DataUserManager.setProgress("spacecraft", numSelectedSP);
                    isSPChanged = false;
                }

                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                page = previousPage;
            }

            // X glory => chiusura pagina glory
            if ((secondScreen && open19) && (screenX>=670 && screenX<=710) && (screenY>=165 && screenY<=205)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open19 = false;
            }
            // X delete/change psw => chiusura pagina delete profile/cambio password
            if (secondScreen && (open22 || open21) && (screenX >= 670 && screenX <= 710) && (screenY >= 205 && screenY <= 245)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open22 = open21 = false;
            }
            // X wise man => chiusura pagina wise man
            if (secondScreen && open23 && (screenX >= 761 && screenX <= 801) && (screenY >= 204 && screenY <= 244)) {
                secondScreen = open23 = false;
            }

            // X others => chiusura pagina info profilo-difficoltà-carte; avatar
            if (!(open19 || open20 || open21 || open22) && !((page==4 || page==12)) && (screenX >= 905 && screenX <= 945) && (screenY >= 83 && screenY <= 123)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                if (page == 7) {
                    page = 6; // evita di tornare alla lobby dalla pagina degli avatar

                    // salvataggio avatar selezionato
                    if (isAVChanged) DataUserManager.setProgress("avatar", selectedAvatar);
                }
                else page = previousPage;
            }

            // chiusura software infos
            if ((secondScreen && open17) && (screenX >= 675 && screenX <= 715) && (screenY >= 198 && screenY <= 238)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open17 = false;
            }

            // chiusura pagina leaderboard
            if ((secondScreen && open20) && ((screenX >= 715 && screenX <= 755) && (screenY >= 122 && screenY <= 162))) {
                SoundManager.playClickButton(soundPercent);
                page = previousPage;
                secondScreen = open20 = false;
            }

            // NO logout => si continua nella sessione di gioco
            if ((secondScreen && open13) && (screenX >= 510 && screenX <= 714) && (screenY >= 404 && screenY <= 480)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open13 = false;
            }

            // YES logout => back to the Authentication Page
            if ((secondScreen && open13) && (screenX >= 270 && screenX <= 472) && (screenY >= 404 && screenY <= 480)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open13 = false;
                LobbyManager.soundtrack.stop();

                SessionLockManager.shutdownAll(); // rilascia il lock

                DataUserManager.resetProgress(); // pulizia mappa dei progressi

                // apertura schermata di autenticazione
                LobbyManager.game.setScreen(new LoginSignupManager(LobbyManager.game));
                ui.disposeUI(); // rilascio risorse
            }

            // not show again waring
            if ((secondScreen && open18) && (screenX >= 375 && screenX <= 608) && (screenY >= 330 && screenY <= 375)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                isTickSelected = !isTickSelected;
            }

            // OK warning => close warning and back to lobby
            if ((secondScreen && open18) && (screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                if (isTickSelected) DataUserManager.setProgress("show_warning", false); // disattivazione warning
                secondScreen = open18 = false; // chiusura audio
            }

            // PLAY warning => play classic game
            if ((secondScreen && open18) && (screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                if (isTickSelected) DataUserManager.setProgress("show_warning", false); // disattivazione warning
                secondScreen = open18 = false; // chiusura schermata
                LobbyManager.soundtrack.stop(); // interruzione audio

                // avvio cg
                LobbyManager.game.setScreen(new ClassicGame(LobbyManager.game, false));
                ui.disposeUI(); // rilascio risorse
            }

            // YES => conferma acquisto
            if ((secondScreen && open14) && (screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                // salvataggio crediti rimasti
                DataUserManager.setProgress("credits", currentCredit);

                // aggiornamento numero carte
                if (item1  >= 1) DataUserManager.setProgress("num_gold_heart", ((int) DataUserManager.getProgress("num_gold_heart") + item1));
                if (item2  >= 1) DataUserManager.setProgress("num_shield", ((int) DataUserManager.getProgress("num_shield") + item2));
                if (item3  >= 1) DataUserManager.setProgress("num_super_laser", ((int) DataUserManager.getProgress("num_super_laser") + item3));
                if (item4  >= 1) DataUserManager.setProgress("num_double_points", ((int) DataUserManager.getProgress("num_double_points") + item4));

                // aggiornamento stato prodotto 5
                if (item5 == 1) DataUserManager.setProgress("state_product_5", true);
                // aggiornamento stato prodotto 6
                if (item6 == 1) DataUserManager.setProgress("state_product_6", true);

                item1 = item2 = item3 = item4 = item5 = item6 = 0; // reset item 1-6
                finalPrize = 0; // reset final prize

                // chiusura schermata in sovra impressione
                secondScreen = open14 = false;
            }

            // NO => annulla acquisto
            if ((secondScreen && open14) && (screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open14 = false;
            }

            // chiusura pagina impostazioni
            if ((secondScreen && open16) && (screenX >= 720 && screenX <= 760) && (screenY >= 78 && screenY <= 118)) {
                SoundManager.playClickButton(soundPercent); // riproduzione suono click
                secondScreen = open16 = false;
                // salvataggio impostazioni //
                // comandi
                DataUserManager.setProgress("movement_type", movementType);
                DataUserManager.setProgress("shot_type", shotType);
                // audio
                DataUserManager.setProgress("sound_volume", soundPercent); // salvataggio volume audio
                DataUserManager.setProgress("music_volume", musicPercent); // salvataggio volume musica
            }

            // setting impostazioni
            if ((secondScreen && open16)) {
                // tipo di movimento
                if ((screenX >= 259 && screenX <= 421) && (screenY >= 195 && screenY <= 272)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    movementType=1;
                }
                if ((screenX >= 259 && screenX <= 421) && (screenY >= 290 && screenY <= 367)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    movementType=2;
                }

                // tipo di sparo
                if ((screenX >= 549 && screenX <= 711) && (screenY >= 195 && screenY <= 272)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    shotType=1;
                }
                if ((screenX >= 549 && screenX <= 711) && (screenY >= 290 && screenY <= 367)) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    shotType=2;
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
                }
                if (screenX>=224 && screenX<=269 && screenY>=435 && screenY<=480) {
                    SoundManager.playClickButton(soundPercent); // riproduzione suono click
                    soundPercent=0;
                }
            }
        }

        return true;
    }

    // cambio icona mouse
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // finché si muove fuori dai pulsanti rimangono spenti, con le grafiche di base
        isBtnStartHover=isBtnClaimHover=isBtnBuyHover=isBtnResetHover=isBtnLHover=isBtnRHover=isOpenSpHover=
            isBtnGloryHover=isHoverIconNoInternet=isDeleteAccountHover=isBtnDeleteHover=
                isBtnChangePSWHover=isBtnWiseManHover=false;
        // cambio cursore
        Gdx.graphics.setCursor(UIManager.cursor);

        // CAMBIO STILE PULSANTI
        // YES logout / OK warning / YES purchase
        if ((secondScreen && (open13 || open18 || open14)) && (screenX >= 270 && screenX <= 472) && (screenY >= 404 && screenY <= 480)) {
            isBtnLHover=true;
        }

        // NO logout / PLAY warning / NO purchase
        if ((secondScreen && (open13 || open18 || open14)) && (screenX >= 510 && screenX <= 714) && (screenY >= 404 && screenY <= 480)) {
            isBtnRHover=true;
        }

        // rtg (road to glory)
        if (page==6 && !open19 && !open21 && !open22 && (screenX>=170 && screenX<=430) && (screenY>=217 && screenY<=280)) isBtnGloryHover=true;
        // password change action button
        if (open21 && !passwordInput.isEmpty() && (screenX>=415 && screenX<=565) && (screenY>=438 && screenY<=488) && (!AuthAlgorithms.password.contentEquals(passwordInput))) isBtnChangePSWHover=true;
        // open delete the account
        if (page==6 && !open19 && !open21 && !open22 && (screenX>=424 && screenX<=460) && (screenY>=307 && screenY<=343)) isDeleteAccountHover=true;
        // delete account action button
        if (open22 && (screenX>=415 && screenX<=565) && (screenY>=438 && screenY<=488)) isBtnDeleteHover=true;
        // open wise man
        if (isGameCompleted && !open23 && (screenX>=84 && screenX<=147) && (screenY>=218 && screenY<=281)) isBtnWiseManHover=true;

        // pagine della lobby
        if (!listSecondPages.contains(page) && !open13 && !open14 && !open16 && !open17 && !open18 && !open19 && !open20) {
            // messaggio no internet
            if (!UIManager.isConnected && (screenX>=811 && screenX<=841 && screenY>=90 && screenY<=120)) isHoverIconNoInternet=true;

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
            if ((page==0 || page==2 || (page==1 && ((int) DataUserManager.getProgress("level") > 10))) && (screenX >= 769 && screenX <= 920) && (screenY >= 550 && screenY <= 593)) {
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
        if (secondScreen && open16) {
            draggingSound = false;
            draggingMusic = false;
        }
        return true;
    }

    // click continuato e movimento del mouse
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) {
        // controlli per il movimento delle barre volume
        if (secondScreen && open16) {
            if (draggingSound) {
                soundPercent = Math.min(1f, Math.max(0f, (screenX - 285) / (float) (685 - 285)));
            }

            if (draggingMusic) {
                musicPercent = Math.min(1f, Math.max(0f, (screenX - 285) / (float) (685 - 285)));
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

    // metodo per rilevare la digitazione della tastiera
    @Override public boolean keyTyped(char character) {
        // riproduzione suono digitazione
        SoundManager.playDigitSound(soundPercent); // suono del click

        if (open21) {
            if (character == '\b' && !passwordInput.isEmpty()) passwordInput.deleteCharAt(passwordInput.length() - 1);
                // controllo digitazione caratteri validi
            else if (character >= 32 && character < 127 && passwordInput.length() <= 10) passwordInput.append(character);
        }

        return true;
    }

    // altri metodi
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
