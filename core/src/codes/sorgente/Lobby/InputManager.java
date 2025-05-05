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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InputManager implements InputProcessor {
    // mappa dei range
    private final Map<Integer, HitBox> hitBoxes = new HashMap<>();

    // variabili per gestire certi input
    protected static boolean secondScreen=false, open17=false, open13=false, open18=false, open14=false, open16=false;
    // variabili per cambiare lo stile dei pulsanti
    protected static boolean isBtnStartHover=false, isBtnClaimHover=false, isBtnBuyHover=false, isBtnResetHover=false, isBtnLHover=false, isBtnRHover=false;
    // lista delle pagine secondarie
    private final Set<Integer> listSecondPages = Set.of(6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18);
    // 'previousPage' serve a memorizzare l'ultima pagina aperta. //
    protected static int page = 0;
    private int previousPage;

    // boolean per le carte speciali
    public static boolean goldHeart, shield, superLaser, doublePoints;
    // nome navicella
    private final String nameSp = UIManager.selectedSp.getName();

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
        hitBoxes.put(0, new HitBox(50, 182, 270, 200, 0, false));  // 'classic game'
        hitBoxes.put(1, new HitBox(50, 232, 270, 250, 1, false)); // 'space battle'
        hitBoxes.put(2, new HitBox(50, 285, 270, 303, 2, false)); // 'space journey'
        hitBoxes.put(3, new HitBox(50, 336, 270, 354, 3, false)); // 'road to glory'
        hitBoxes.put(5, new HitBox(50, 496, 270, 514, 5, false)); // 'marketplace'
        // le pagine seguenti hanno da memorizzare previousPage
        hitBoxes.put(6, new HitBox(862, 62, 950, 145, 6, true));  // 'profile infos'
        hitBoxes.put(11, new HitBox(50, 550, 270, 568, 11, true));   // 'instructions'
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    // metodo per rilevare il click della tastiera
    @Override public boolean keyDown(int keycode) {
        // click tasto esc per il logout
        if (keycode == Input.Keys.ESCAPE && (!listSecondPages.contains(page) && !open17 && !open13 && !open18 && !open14 && !open16)) {
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

        // **************************************** //
        // CAMBIO PAGINE LOBBY + CLICK NELLE PAGINE //
        // **************************************** //
        if (!listSecondPages.contains(page) && !open17 && !open13 && !open18 && !open14 && !open16) {

            // CAMBIO PAGINE LOBBY
            // for-each per iterare i vari range e controllare i cambi pagina
            for (Map.Entry<Integer, HitBox> entry : hitBoxes.entrySet()) {
                HitBox hb = entry.getValue();
                if (hb.isInside(screenX, screenY)) {
                    if (hb.remembersPrevious) previousPage = page; // memorizzazione pagina precedente
                    page = hb.targetPage; // cambio pagina
                    break;
                }
            }

            // APERTURA PAGINA NAVICELLE => viene creata una classe a parte che gestisce tutto (grafiche+input)
            if ((screenX>=50 && screenX<=270) && (screenY>=389 && screenY<=407)) {
                // apertura pagina navicelle
                /// Non ha bisogno di memorizzare la pagina precedente perché si crea una nuova lobby dalla pagina
                LobbyManager.game.setScreen(new SpacecraftSelectionScreen(LobbyManager.game));
            }

            // CLICK NELLE PAGINE
            // apertura pagina 'software infos'
            if ((screenX>=105 && screenX<=135) && (screenY>=600 && screenY<=630)) {
                open17 = true;
                secondScreen = true;
            }
            // apertura pagina 'logout'
            if ((screenX>=162 && screenX<=192) && (screenY>=600 && screenY<=630)) {
                open13 = true;
                secondScreen = true;
            }
            // apertura pagina 'settings'
            if ((screenX>=50 && screenX<=90) && (screenY>=600 && screenY<=630)) {
                open16 = true;
                secondScreen = true;
            }
            // apertura pagina 9/10 (difficulty infos classic game/space battle)
            if ((screenX>=614 && screenX<=694) && (screenY>=551 && screenY<=592)) {
                if (page == 0) { previousPage = page; page = 9; }
                else if (page == 1) { previousPage = page; page = 10; }
            }
            // apertura pagina 20 (cards infos)
            if ((page == 0 || page == 1) && (screenX>=880 && screenX<=905) && (screenY>=255 && screenY<=280)) {
                previousPage = page;
                page = 8;
            }

            // controllo per avviare le modalità di gioco
            if ((screenX >= 778 && screenX <= 928) && (screenY >= 552 && screenY <= 592)) {
                // avvio modalità di gioco
                if (page == 0) {
                    // controllo per l'avviso difficoltà
                    if ((nameSp.equals("Omega") || nameSp.equals("Idra") || nameSp.equals("Pegaso") || nameSp.equals("Woka")) && diffCG == 3d) {
                        secondScreen = open18 = true;
                    } else {
                        LobbyManager.soundtrack.stop(); // interruzione musica
                        LobbyManager.game.setScreen(new ClassicGame(LobbyManager.game, UIManager.selectedSp)); // avvio classic game
                    }
                } else if (page == 1 && ((int)DataUserManager.getProgress("level") > 10)) {
                    LobbyManager.soundtrack.stop(); // interruzione musica
                    LobbyManager.game.setScreen(new SpaceBattle(LobbyManager.game, UIManager.selectedSp)); // avvio space battle
                } else if (page == 2) {
                    LobbyManager.soundtrack.stop(); // interruzione musica
                    LobbyManager.game.setScreen(new SpaceJourney(LobbyManager.game)); // apertura mappa space journey
                }
            }

            // cambio difficoltà classic game
            if (page == 0 && (screenX >= 700 && screenX <= 720) && (screenY >= 560 && screenY <= 584)) {
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
            if (page == 1 && (screenX >= 700 && screenX <= 720) && (screenY >= 560 && screenY <= 584)) {
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
            if ((page == 0 || page == 1) && ((int) DataUserManager.getProgress("num_gold_heart") > 0) && (!nameSp.equals("Alpha")) && ((screenX >= 680 && screenX <= 750) && (screenY >= 253 && screenY <= 323))) {
                goldHeart = !goldHeart;
                // disattivazione altre carte
                if (!nameSp.equals("Astrid")) shield = false;
                if (!nameSp.equals("Rorik")) superLaser = false;
                if (!nameSp.equals("Drakar")) doublePoints = false;
            }
            // shield
            if (((screenX >= 793 && screenX <= 863) && (screenY >= 253 && screenY <= 323))) {
                if (page == 0 && ((int) DataUserManager.getProgress("num_shield") > 0)) {
                    shield = !shield;
                    if (!nameSp.equals("Rorik")) superLaser = false;
                } else if (page == 1 && ((int) DataUserManager.getProgress("num_super_laser") > 0)) {
                    superLaser = !superLaser;
                    if (!nameSp.equals("Astrid")) shield = false;
                }

                // disattivazione altre carte
                if (!nameSp.equals("Alpha")) goldHeart = false;
                if (!nameSp.equals("Drakar")) doublePoints = false;
            }
            // super laser
            if (page == 0 && ((int) DataUserManager.getProgress("num_super_laser") > 0) && (!nameSp.equals("Rorik")) && ((screenX >= 680 && screenX <= 750) && (screenY >= 368 && screenY <= 438))) {
                superLaser = !superLaser;

                // disattivazione altre carte
                if (!nameSp.equals("Alpha")) goldHeart = false;
                if (!nameSp.equals("Astrid")) shield = false;
                if (!nameSp.equals("Drakar")) doublePoints = false;
            }
            // double points
            if (page == 0 && ((int) DataUserManager.getProgress("num_double_points") > 0) && (!nameSp.equals("Drakar")) && ((screenX >= 793 && screenX <= 863) && (screenY >= 368 && screenY <= 438))) {
                doublePoints = !doublePoints;

                // disattivazione altre carte
                if (!nameSp.equals("Alpha")) goldHeart = false;
                if (!nameSp.equals("Rorik")) superLaser = false;
                if (!nameSp.equals("Astrid")) shield = false;
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
                        DataUserManager.setProgress("total_credits", (int) DataUserManager.getProgress("total_credits") + 100);// aggiunta 100 crediti totali
                        currentCredit+=100;
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
            if (page == 5) {
                // rimozione prodotto
                if ((screenX >= 344 && screenX <= 364 && screenY >= 385 && screenY <= 405) && item1 > 0) { // item 1
                    item1--;
                    currentCredit += 50;
                }
                if ((screenX >= 494 && screenX <= 514 && screenY >= 385 && screenY <= 405) && item2 > 0) { // item 2
                    item2--;
                    currentCredit += 100;
                }
                if ((screenX >= 644 && screenX <= 664 && screenY >= 385 && screenY <= 405) && item3 > 0) { // item 3
                    item3--;
                    currentCredit += 150;
                }
                if ((screenX >= 794 && screenX <= 814 && screenY >= 385 && screenY <= 405) && item4 > 0) { // item 4
                    item4--;
                    currentCredit += 200;
                }
                if ((screenX >= 442 && screenX <= 462 && screenY >= 538 && screenY <= 558) && item5 > 0) { // item 5
                    item5--;
                    currentCredit += 20000;
                }
                if ((screenX >= 688 && screenX <= 708 && screenY >= 538 && screenY <= 558) && item6 > 0) { // item 6
                    item6--;
                    currentCredit += 30000;
                }

                // aggiunta prodotto
                if ((screenX >= 409 && screenX <= 429 && screenY >= 385 && screenY <= 405) && (currentCredit - 50 >= 0)) { // item 1
                    item1++;
                    currentCredit -= 50;
                }
                if ((screenX >= 559 && screenX <= 579 && screenY >= 385 && screenY <= 405) && (currentCredit - 100 >= 0)) { // item 2
                    item2++;
                    currentCredit -= 100;
                }
                if ((screenX >= 709 && screenX <= 729 && screenY >= 385 && screenY <= 405) && (currentCredit - 150 >= 0)) { // item 3
                    item3++;
                    currentCredit -= 150;
                }
                if ((screenX >= 859 && screenX <= 879 && screenY >= 385 && screenY <= 405) && (currentCredit - 200 >= 0)) { // item 4
                    item4++;
                    currentCredit -= 200;
                }
                if ((screenX >= 506 && screenX <= 526 && screenY >= 538 && screenY <= 558) && (currentCredit - 20000 >= 0 && item5 < 1 && !((boolean) DataUserManager.getProgress("state_product_5")))) { // item 5
                    item5++;
                    currentCredit -= 20000;
                }
                if ((screenX >= 752 && screenX <= 772 && screenY >= 538 && screenY <= 558) && (currentCredit - 30000 >= 0 && item6 < 1 && !((boolean) DataUserManager.getProgress("state_product_6")))) { // item 6
                    item6++;
                    currentCredit -= 30000;
                }

                // pulsante reset
                if (screenX >= 790 && screenX <= 922 && screenY >= 580 && screenY <= 624) {
                    item1 = item2 = item3 = item4 = item5 = item6 = 0; // reset item 1-6
                    finalPrize = 0; // reset final prize
                    currentCredit = ((int) DataUserManager.getProgress("credits")); // reset crediti
                }

                // pulsante per confermare l'acquisto
                if ((screenX >= 503 && screenX <= 717 && screenY >= 580 && screenY <= 624) && finalPrize > 0) {
                    secondScreen = open14 = true;
                }

                // prezzo finale
                finalPrize = ((int) DataUserManager.getProgress("credits")) - currentCredit;
            }
        }

        // *************************************************** //
        // CLICK NELLE PAGINE IN SOVRAIMPRESSIONE E SECONDARIE //
        // *************************************************** //
        else {
            // apertura pagina 7 'avatar'
            if (page == 6 && ((screenX >= 453 && screenX <= 537) && (screenY >= 108 && screenY <= 188))) page = 7;

            // selezione avatar
            if (page == 7) {
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

            // chiusura pagina istruzioni; info profilo-difficoltà; missioni; avatar
            if ((screenX >= 908 && screenX <= 948) && (screenY >= 84 && screenY <= 124)) {
                if (page == 7) page = 6; // evita di tornare alla lobby dalla pagina degli avatar
                else page = previousPage;
            }

            // chiusura software infos
            if ((secondScreen && open17) && (screenX >= 684 && screenX <= 724) && (screenY >= 206 && screenY <= 246)) {
                secondScreen = open17 = false;
            }

            // NO logout => si continua nella sessione di gioco
            if ((secondScreen && open13) && (screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
                secondScreen = open13 = false;
            }

            // YES logout => back to the Authentication Page
            if ((secondScreen && open13) && (screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
                secondScreen = open13 = false;
                LobbyManager.soundtrack.stop();
                LobbyManager.game.setScreen(new LoginSignupManager(LobbyManager.game));
            }

            // OK warning => close warning and back to lobby
            if ((secondScreen && open18) && (screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
                secondScreen = open18 = false;
            }

            // PLAY warning => play classic game
            if ((secondScreen && open18) && (screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
                secondScreen = open18 = false;
                LobbyManager.soundtrack.stop();
                LobbyManager.game.setScreen(new ClassicGame(LobbyManager.game, UIManager.selectedSp)); // avvio classic game
            }

            // YES => conferma acquisto
            if ((secondScreen && open14) && (screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
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
                secondScreen = open14 = false;
            }

            // NO => annulla acquisto
            if ((secondScreen && open14) && (screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
                secondScreen = open14 = false;
            }

            // chiusura pagina impostazioni
            if ((secondScreen && open16) && (screenX >= 720 && screenX <= 760) && (screenY >= 78 && screenY <= 118)) {
                secondScreen = open16 = false;
            }

            // setting impostazioni
            if ((secondScreen && open16)) {
                // tipo di movimento
                if ((screenX >= 259 && screenX <= 421) && (screenY >= 195 && screenY <= 272)) DataUserManager.setProgress("movement_type", 1);
                if ((screenX >= 259 && screenX <= 421) && (screenY >= 290 && screenY <= 367)) DataUserManager.setProgress("movement_type", 2);

                // tipo di sparo
                if ((screenX >= 549 && screenX <= 711) && (screenY >= 195 && screenY <= 272)) DataUserManager.setProgress("shot_type", 1);
                if ((screenX >= 549 && screenX <= 711) && (screenY >= 290 && screenY <= 367)) DataUserManager.setProgress("shot_type", 2);

                // cambio volume suoni
                if ((screenY >= 459 - 10 && screenY <= 459 + 10) &&
                    (screenX >= 285 && screenX <= 685)) {
                    draggingSound = true;
                }
                // cambio volume musica
                if ((screenY >= 530 - 10 && screenY <= 530 + 10) &&
                    (screenX >= 285 && screenX <= 685)) {
                    draggingMusic = true;
                }

            }
        }

        return true;
    }

    // cambio icona mouse
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // finché si muove fuori dai pulsanti rimangono spenti, con le grafiche di base
        isBtnStartHover=isBtnClaimHover=isBtnBuyHover=isBtnResetHover=isBtnLHover=isBtnRHover=false;
        // cambio cursore
        Gdx.graphics.setCursor(UIManager.cursor);

        // CAMBIO STILE PULSANTI
        // YES logout / OK warning / YES purchase
        if ((secondScreen && (open13 || open18 || open14)) && (screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
            isBtnLHover=true;
        }

        // NO logout / PLAY warning / NO purchase
        if ((secondScreen && (open13 || open18 || open14)) && (screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
            isBtnRHover=true;
        }

        if (!listSecondPages.contains(page) && !open17 && !open13 && !open18 && !open14 && !open16) {
            // rtg
            if (page == 3 && ((boolean) DataUserManager.getProgress("completed_RTG")) && (screenX >= 762 && screenX <= 898) && (screenY >= 561 && screenY <= 595)) isBtnClaimHover=true;

            // market
            if (page==5) {
                // pulsante reset
                if ((screenX >= 790 && screenX <= 922 && screenY >= 580 && screenY <= 624) && finalPrize > 0) isBtnResetHover=true;
                // pulsante per confermare l'acquisto
                if ((screenX >= 503 && screenX <= 717 && screenY >= 580 && screenY <= 624) && finalPrize > 0) isBtnBuyHover=true;
            }

            // avvio modalità di gioco
            if ((page==0 || page==2 || (page==1 && ((int)DataUserManager.getProgress("level") > 10))) && (screenX >= 778 && screenX <= 928) && (screenY >= 552 && screenY <= 592)) {
                isBtnStartHover=true;
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
                DataUserManager.setProgress("sound_volume", soundPercent); // salvataggio volume audio
            }

            if (draggingMusic) {
                musicPercent = Math.min(1f, Math.max(0f, (screenX - 285) / (float) (685 - 285)));
                DataUserManager.setProgress("music_volume", musicPercent); // salvataggio volume musica
            }
        }
        return true;
    }

    // altri metodi
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
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
