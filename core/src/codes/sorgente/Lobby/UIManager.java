/*
Astro Invasion - class AuthManagerUI -
Gestisce le grafiche delle schermate della lobby
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.Lobby;

// import codici e librerie
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sorgente.Entities.Avatar;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.UserData.DataUserManager;
import sorgente.LogInSignUp.AuthAlgorithms;
import sorgente.UserData.CloudStorageManager;
import sorgente.Missions.CheckRTG;
import sorgente.Missions.Missions;
import sorgente.ResourceLoader;
import sorgente.Entities.Spacecraft;

import java.text.NumberFormat;
import java.util.*;
import java.util.TreeMap;

public class UIManager implements ResourceLoader {
    // dichiarazione immagini delle schermate
    private Texture tickImg, diffCG1, diffCG2, diffCG3, diffSB1, diffSB2, diffSB3, rectSelectCard,
        claimPrize, progressMissions, notifyCompletedMissions, txtSoldOut, soundOn, soundOff, musicOn, musicOff, selectedSetting,
        volumeState, bgSpacecraftSelection, spacecraftSelectionBox, topBanner, topBanner2, btnResetActive, noInternetIcon,
        noInternetMessage, wiseManMessage, showPS, coverPS;

    // textureRegione per definire l'area di completamento della task corrente in Missions
    private TextureRegion progressBarRegion;

    // texture per gli avatar
    private Texture[] avatars;
    private Texture[] avatarsCovered;
    private Texture selectedAvatar;

    // premi Missions
    private Texture[] MissionsPrizes;
    public static Missions[] Missions;

    // immagine navicella + immagine space battle bloccato
    private Texture spImg, infoBanner;

    // pulsanti + scuri al passaggio del mouse
    private final Texture[] buttonsOver, alphaFragments, badgesRTG;

    private BitmapFont fontBlue20, fontMediumBlue15, fontMediumBlue20, fontBoldBlue20, fontMediumWhite20,
        fontMediumWhite16, fontBoldWhite15, fontBoldWhite18, fontBoldWhite20, fontBoldItalicWhite20, fontBoldWhite25,
        fontBoldWhite27, fontBoldWhite30, fontBoldWhite35, fontBoldWhite40, fontBoldItalicWhite15, fontBoldWhite60,
        fontSemiboldYellow25, fontBoldItalicYellow20;

    // hashmap/liste per diverse texture
    private final HashMap<Integer, Texture> mapLobby; // schermate lobby
    private final HashMap<Integer, Texture> mapAvatarImg; // immagini avatar
    private final HashMap<Integer, Avatar> mapAvatars; // oggetti avatar
    private static HashMap<Integer, Spacecraft> mapSpacecrafts; // oggetti navicella
    private final List<Spacecraft> spacecrafts;

    // arraylist delle pagine secondarie
    private final Set<Integer> listSecondPages = Set.of(4, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);

    // creazione oggetto navicella per il package Lobby
    protected static Spacecraft selectedSp;

    // formatter per la virgola delle migliaia !in automatico converte l'intero in stringa
    private final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

    // mouse
    protected Pixmap mouse; // immagini
    protected static Cursor cursor;

    // variabile per lo stato della connessione a internet
    public static boolean isConnected = true;

    // costruttore
    public UIManager() {
        this.mapLobby = new HashMap<>();
        this.mapAvatarImg = new HashMap<>();
        this.mapAvatars = new HashMap<>();
        mapSpacecrafts = new HashMap<>();
        spacecrafts = new ArrayList<>();
        this.buttonsOver = new Texture[14];
        this.alphaFragments = new Texture[5];
        this.badgesRTG = new Texture[5];

        // caricamento navicella utente
        // popolamento della mappa navicelle
        for (int i = 0; i < 24; i++) {
            // ordine potenze: 0:bonus punti, 1:velocità navicella, 2:vel laser
            mapSpacecrafts.put(i, new Spacecraft(i));
        }
        // selezione navicella utente
        selectedSp = selectSpacecraft();

        // caricamento risorse
        createMissions();
        loadImages(); // immagini lobby
        loadFont(); // font

        // popolamento della mappa navicelle
        for (int i = 0; i < 24; i++) {
            spacecrafts.add(new Spacecraft(i));
        }
        createAvatars(); // creazione oggetti avatar

        // il caricamento delle navicelle avviene in LobbyManager così da passargli la navicella selezionata
    }

    // ******************* //
    // CARICAMENTO RISORSE //
    // ******************* //

    // metodo per caricare e creare i font
    @Override
    public void loadFont() {
        // dichiarazione font
        try {
            // blue
            fontBlue20 = new BitmapFont(Gdx.files.internal("font/inter/regular_blue_20.fnt")); // inter-regular blue 20
            fontMediumBlue15 = new BitmapFont(Gdx.files.internal("font/inter/medium_blue_15.fnt")); // inter-medium blue 15
            fontMediumBlue20 = new BitmapFont(Gdx.files.internal("font/inter/medium_blue_20.fnt")); // inter-medium blue 20
            fontBoldBlue20 = new BitmapFont(Gdx.files.internal("font/inter/bold_blue_20.fnt")); // inter-regular blue 20
            // white
            fontMediumWhite20 = new BitmapFont(Gdx.files.internal("font/inter/medium_white_20.fnt")); // inter-medium white 20
            fontMediumWhite16 = new BitmapFont(Gdx.files.internal("font/inter/medium_white_16.fnt")); // inter-medium white 16
            fontBoldWhite15 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_15.fnt")); // inter-bold white 15
            fontBoldWhite18 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_18.fnt")); // inter-bold white 18
            fontBoldWhite20 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_20.fnt")); // inter-bold white 20
            fontBoldWhite25 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_25.fnt")); // inter-bold white 25
            fontBoldWhite27 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_27.fnt")); // inter-bold white 27
            fontBoldWhite30 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_30.fnt")); // inter-bold white 30
            fontBoldWhite35 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_35.fnt")); // inter-bold white 35
            fontBoldWhite40 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_40.fnt")); // inter-bold white 40
            fontBoldItalicWhite15 = new BitmapFont(Gdx.files.internal("font/inter/bold_italic_white_15.fnt")); // inter-italic-bold white 15
            fontBoldItalicWhite20 = new BitmapFont(Gdx.files.internal("font/inter/bold_italic_white_20.fnt")); // inter-italic-bold white 20
            fontBoldWhite60 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_60_1.fnt"));
            // yellow
            fontSemiboldYellow25 = new BitmapFont(Gdx.files.internal("font/inter/semibold_yellow_25.fnt")); // inter-semibold yellow 25
            fontBoldItalicYellow20 = new BitmapFont(Gdx.files.internal("font/inter/bold_italic_yellow_20.fnt")); // inter-italic-bold yellow 20
        } catch (Exception e) {
            // dichiarazione font
            BitmapFont font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("#151A3B")); // colore blu
        }
    }

    // metodo per caricare le immagini della Lobby
    @Override
    public void loadImages() {
        // mouse
        mouse = new Pixmap(Gdx.files.internal("images/cursor.png"));
        cursor = Gdx.graphics.newCursor(mouse, 0, 0);

        // popolamento mappa lobby
        for (int i = 0; i < 24; i++) mapLobby.put(i, new Texture("lobby_screens/lobby (" + i + ").png"));
        // popolamento mappa avatar
        for (int i = 0; i <= 19; i++) mapAvatarImg.put(i, new Texture("images/avatars/av (" + i + ").png"));

        // "pulsante" raccolta premio Missions
        Texture img_special = new Texture("images/rect_claim_reward_eng.png");
        mapLobby.put(35, img_special);

        // testo "SOLD OUT" per il marketplace
        txtSoldOut = new Texture("images/sold_item_txt.png");
        // pulsante reset elementi attivo
        btnResetActive = new Texture("images/btns_hover/active_reset_btn.png");

        // immagini badges RTG
        for (int i=0; i<5; i++) {
            badgesRTG[i] = new Texture("images/badges_rtg/badge" + (i+1) + ".png");
        }

        // immagini frammenti alpha
        for (int i=0; i<4; i++) {
            alphaFragments[i] = new Texture("images/spacecrafts/alpha/alpha_frag" + i + ".png");
        }
        alphaFragments[4] = new Texture("images/spacecrafts/alpha/alpha.png"); // imm alpha completa

        // testo informativo space battle bloccato
        infoBanner = new Texture("images/warning_txt_space_battle.png");

        // icone suoni
        soundOn = new Texture("images/icoSoundOn.png");
        soundOff = new Texture("images/icoSoundOff.png");
        musicOn = new Texture("images/icoMusicOn.png");
        musicOff = new Texture("images/icoMusicOff.png");
        // volume suoni
        volumeState = new Texture("images/barAudioValue.png");
        // selezione impostazione
        selectedSetting = new Texture("images/selected_setting.png");

        // pulsanti "hover"
        for (int i = 0; i < 14; i++) {
            buttonsOver[i] = new Texture("images/btns_hover/hover_btn" + (i+1) + ".png");
        }

        // icona difficoltà classic game
        diffCG1 = new Texture("images/diff1_classicgame.png");
        diffCG2 = new Texture("images/diff2_classicgame.png");
        diffCG3 = new Texture("images/diff3_classicgame.png");
        // icona difficoltà space battle
        diffSB1 = new Texture("images/diff1_spacebattle.png");
        diffSB2 = new Texture("images/diff2_spacebattle.png");
        diffSB3 = new Texture("images/diff3_spacebattle.png");

        // immagini dei premi del Missions
        MissionsPrizes = new Texture[4];
        // load texture
        for (int i=0; i<4; i++) {
            MissionsPrizes[i] = new Texture("images/cards/mini_card" + (i+1) + ".png");
        }

        // immagine per raccogliere il premio Missions
        claimPrize = new Texture("images/rect_claim_reward_eng.png");
        // icona di notifica del completamento della missione Missions
        notifyCompletedMissions = new Texture("images/notify_completed_Missions.png");

        // immagine spunta per completamento missione o selezione oggetti
        tickImg = new Texture("images/tick_selection.png");
        // rettangolo selezione carta
        rectSelectCard = new Texture(Gdx.files.internal("secondary_screens/active_card.png"));

        // immagine di progresso missione Missions
        progressMissions = new Texture("images/progress.png");
        progressBarRegion = new TextureRegion(progressMissions);

        // immagini per la pagina di selezione delle navicelle
        bgSpacecraftSelection = new Texture("lobby_screens/lobby (4).png");
        spacecraftSelectionBox = new Texture("images/rect_selected_SP.png");

        // array per gli avatar
        avatars = new Texture[21];
        avatarsCovered = new Texture[21];

        // caricamento avatar base
        for (int i=0; i<=20; i++) {
            avatars[i] = new Texture("images/avatars/av (" + i + ") mini.png");
        }
        // caricamento avatar nascosti
        for (int i=0; i<4; i++) {
            avatarsCovered[i] = null; // null per i primi 4 avatar
        }
        for (int i=4; i<=20; i++ ) {
            avatarsCovered[i] = new Texture("images/avatars/av (" + i + ") mini covered.png");
        }

        // quadrato avatar selezionato
        selectedAvatar = new Texture("images/avatars/selected_avatar.png");

        // X chiusura pagina 'spacecrafts'
        topBanner = new Texture("images/top_banner.png");
        // X chiusura pagina 'how to play'
        topBanner2 = new Texture("images/top_banner2.png");

        // icona internet assente
        noInternetIcon = new Texture("login_signup_screens/no_internet.png");
        noInternetMessage = new Texture("images/alert_message_noInternet.png");

        // messaggio info wise man
        wiseManMessage = new Texture("images/info_secret_message.png");

        // icona mostra/nascondi password
        showPS = new Texture("login_signup_screens/showPS.png");
        coverPS = new Texture("login_signup_screens/coverPS.png");
    }

    // **************** //
    // GESTIONE GRAFICA //
    // **************** //
    // metodo per creare gli avatar
    public void createAvatars() {
        mapAvatars.put(0, new Avatar("Cap. Omega", null));
        mapAvatars.put(1, new Avatar("Cap. Idra", null));
        mapAvatars.put(2, new Avatar("Cap. Pegaso", null));
        mapAvatars.put(3, new Avatar("Cap. Woka", null));
        mapAvatars.put(4, new Avatar("Cooper", "Complete Level 3"));
        mapAvatars.put(5, new Avatar("Jessica", "Complete Level 7"));
        mapAvatars.put(6, new Avatar("Scott", "Complete Level 9"));
        mapAvatars.put(7, new Avatar("Stephanie", "Complete Level 13"));
        mapAvatars.put(8, new Avatar("Amin", "Complete Level 17"));
        mapAvatars.put(9, new Avatar("Samira", "Complete Level 19"));
        mapAvatars.put(10, new Avatar("Abdul", "Complete Level 23"));
        mapAvatars.put(11, new Avatar("Dorothy", "Complete Level 27"));
        mapAvatars.put(12, new Avatar("Chen", "Complete Level 29"));
        mapAvatars.put(13, new Avatar("Lin", "Complete Level 33"));
        mapAvatars.put(14, new Avatar("MarcusG", "Complete Level 37"));
        mapAvatars.put(15, new Avatar("Sarah", "Complete Level 39"));
        mapAvatars.put(16, new Avatar("Matthew", "Claim 50K Credits"));
        mapAvatars.put(17, new Avatar("Kiara", "Win 200 SB Matches"));
        mapAvatars.put(18, new Avatar("Luke", "Reach 5M Points"));
        mapAvatars.put(19, new Avatar("Emma", "Complete Task 50"));
    }

    // metodo per selezionare la navicella: viene chiamato alla creazione della Lobby e al cambio navicella
    public static Spacecraft selectSpacecraft() {
        // recupero navicella utente
        int spacecraft = (int) DataUserManager.getProgress("spacecraft");
        return mapSpacecrafts.get(spacecraft); // return oggetto navicella
    }

    public void createMissions() {
        Missions = new Missions[4];

        // creazione oggetti
        Missions Missions0 = new Missions("Hit", 100, "aliens in Classic Game matches.", "1 Gold Heart", "images/cards/cart1_gold_heart_eng.png");
        Missions Missions1 = new Missions("Win", 1, "Space Battle matches.", "1 Shield", "images/cards/cart2_shield_eng.png");
        Missions Missions2 = new Missions("Earn", 10000, "points through Space Battle\nand/or Classic Game matches.", "100 Credits", "images/cards/card_100_coins.png");
        Missions Missions3 = new Missions("Earn", 100, "credits through Space Battle\nand/or Classic Game matches.", "1 Super Laser", "images/cards/cart3_super_laser_eng.png");

        Missions[0] = Missions0;
        Missions[1] = Missions1;
        Missions[2] = Missions2;
        Missions[3] = Missions3;
    }

    // metodo per disegnare la barra di progresso della task corrente del Missions
    public void drawMissionsPage(SpriteBatch screen, int missionID) {
        // immagine premio //
        screen.draw(MissionsPrizes[missionID-1], 660, 100);

        int progress, maxProgress;

        progress = switch (missionID) {
            case 1 -> (int) DataUserManager.getProgress("num_aliens_hit_missions");
            case 2 -> (int) DataUserManager.getProgress("wins_SB_missions");
            case 3 -> (int) DataUserManager.getProgress("points_missions");
            case 4 -> (int) DataUserManager.getProgress("credits_missions");
            default -> 0;
        };

        // progresso totale da compiere
        maxProgress = Missions[missionID-1].calcNumObjMission();

        // pulsante raccolta premio //
        if ((boolean) DataUserManager.getProgress("completed_mission")) {
            screen.draw(claimPrize, 767, 100);

            // button claim hover
            if (InputManager.isBtnClaimHover) screen.draw(buttonsOver[3], 767, 100);

            fontBoldWhite30.draw(screen, "CLAIM", 785, 133); // testo CLAIM
            progress=maxProgress;
        }

        // lunghezza barra riempita
        float filledWidth = (progress / (float) maxProgress) * 380;

        if (filledWidth > 0.0) {
            screen.draw(progressBarRegion, 519, 276, filledWidth, 20);
        }

        // progresso road to glory
        fontBoldWhite20.draw(screen, formatter.format(progress) + "/" + formatter.format(maxProgress), 525, 294);
    }

    // metodo per disegnare la pagina delle impostazioni
    public void drawSettingsPage(SpriteBatch screen) {
        screen.draw(mapLobby.get(16), 175, 25);

        // comandi movimento navicella
        if (InputManager.movementType==1) screen.draw(selectedSetting, 268, 427);
        else screen.draw(selectedSetting, 268, 332);
        // comando sparo laser
        if (InputManager.shotType==1) screen.draw(selectedSetting, 558, 427);
        else screen.draw(selectedSetting, 558, 332);

        // disegno icone musica/suono
        if (InputManager.soundPercent==0f) screen.draw(soundOff, 230, 215);
        else screen.draw(soundOn, 230, 215);
        if (InputManager.musicPercent==0f) screen.draw(musicOff, 230, 145);
        else screen.draw(musicOn, 230, 145);

        // disegno barre volume
        float filledWidth1 = (InputManager.soundPercent) * 390;
        float filledWidth2 = (InputManager.musicPercent) * 390;
        if (filledWidth1 > 0.0) screen.draw(volumeState, 300, 228, filledWidth1, 25);
        if (filledWidth2 > 0.0) screen.draw(volumeState, 300, 157, filledWidth2, 25);

        // testo percentuale volumi
        fontBoldWhite20.draw(screen, Math.round(InputManager.soundPercent*100)+"%", 705, 250);
        fontBoldWhite20.draw(screen, Math.round(InputManager.musicPercent*100)+"%", 705, 177);
    }

    // metodo per stampare i testi e le immagini
    public void drawSPSelectionPage(SpriteBatch screen) {
        // immagine di sfondo
        screen.draw(mapLobby.get(4), 0, -InputManager.scrollY);

        int spID=0; // id navicella per recuperarne gli attributi
        int X, x1=266, x2= 678, y=2645; // x e y della prima scritta della prima navicella

        // iterazione con 2 for per dividere i gruppi delle navicelle
        for (int i=0; i<6; i++) {
            for (int j=0; j<4; j++) {
                Spacecraft s = spacecrafts.get(spID); // oggetto navicella

                // x delle scritte (x1 è la prima colonna, x2 è la seconda)
                X = (j == 0 || j == 2) ? x1 : x2;

                // attributi mostrati se la navicella è sbloccata altrimenti sono nascosti
                if (Spacecraft.isAchieved(spID)) {
                    if (s.getSpSpeed()>=1) fontBoldWhite18.draw(screen, "+" + s.getSpSpeed(), X, y-InputManager.scrollY);
                    if (s.getLaserSpeed()>=1) fontBoldWhite18.draw(screen, "+" + s.getLaserSpeed(), X, (y-37)-InputManager.scrollY);
                    if (s.getBonusPoints()>=1) fontBoldWhite18.draw(screen, "+" + s.getBonusPoints() + "%", X, (y-74)-InputManager.scrollY);
                    fontBoldWhite18.draw(screen, s.getLore(), X, (y-107)-InputManager.scrollY);

                    // disegno rettangolo di selezione
                    if (spID == InputManager.numSelectedSP) screen.draw(spacecraftSelectionBox, X-178, (y-145)-InputManager.scrollY);

                    // disegno di alpha completata
                    if (i*j==15) screen.draw(alphaFragments[4], X-125, (y-79)-InputManager.scrollY);
                }
                else {
                    fontBoldWhite18.draw(screen, "?", X, y-InputManager.scrollY);
                    fontBoldWhite18.draw(screen, "?", X, (y-37)-InputManager.scrollY);
                    fontBoldWhite18.draw(screen, "?", X, (y-74)-InputManager.scrollY);
                    // stampa immagine+missione di alpha
                    if (i*j==15) {
                        int fragAchieved = (int) DataUserManager.getProgress("alpha_fragments");
                        fontBoldWhite18.draw(screen, "Fragments: " + fragAchieved + "/4", X, (y-107)-InputManager.scrollY);
                        screen.draw(alphaFragments[fragAchieved], X-132, (y-92)-InputManager.scrollY);
                    }
                    else fontBoldWhite18.draw(screen, s.getMission(), X, (y-107)-InputManager.scrollY);
                }

                if (j==1)  y-=168; // passaggio alla riga seguente

                // passaggio alla navicella successiva
                spID++;
            }
            // passaggio al gruppo successivo
            y-= 257;
        }

        // banner in alto fisso
        screen.draw(topBanner, 14, 514); // DEVE STARE QUI PERCHÈ STA SOPRA OGNI ALTRO ELEMENTO DELLA PAGINA
    }

    // metodo per la grafica delle missioni utente
    public void drawGloryPage(SpriteBatch screen) {
        // background base
        screen.draw(mapLobby.get(19), 250, 125);

        int cont = 0; // numero task completate

        // controllo completamento missioni
        int x=334, y=417;
        for (int i=0; i<5; i++) {
            // stampa ultimo badge
            if (cont==4) screen.draw(badgesRTG[4], x, y);

            // stampa badge completamento
            if (i<4 && CheckRTG.checkMission(i)) {
                cont++;
                screen.draw(badgesRTG[i], x, y);
            }
            y-=62; // decremento y per il badge successivo
        }
    }

    // metodo per scrivere i dati sulla leaderboard
    public void drawLeaderboard(SpriteBatch screen) {
        // background base
        screen.draw(mapLobby.get(20), 200, 70);

        // ordinamento mappa secondo i punti
        TreeMap<String, Integer> sortedMap = new TreeMap<>(
            (s1, s2) -> CloudStorageManager.userPointsMap.get(s2).compareTo(CloudStorageManager.userPointsMap.get(s1))
        );
        sortedMap.putAll(CloudStorageManager.userPointsMap);

        // stampa tutto a monitor
        int y=450, index=1;
        for (String name : sortedMap.keySet()) {
            int points = sortedMap.get(name);

            // stampa nome e punti
            fontBoldWhite27.draw(screen, name, 330, y);
            fontBoldWhite27.draw(screen, formatter.format(points), 575, y);

            y-=73; // decremento y per le scritte
            index++; // passaggio al prossimo utente

            if (index==6) break; // stampa solo la top 5
        }
    }

    // metodo per mostrare i contenuti nelle pagine (testi, immagini, icone)
    public void showItems(SpriteBatch screen) {
        selectedSp = selectSpacecraft(); // oggetto navicella
        spImg = selectedSp.getImgTexture(); // immagine navicella

        // background principale (NO la pagina 4 e 12 che sono scrollabili e gestite diversamente)
        if (InputManager.page !=4 && InputManager.page!=12) screen.draw(mapLobby.get(InputManager.page), 0, 0);

        // stampa immagini SOLO della Lobby
        if (!listSecondPages.contains(InputManager.page)) {
            // avatar
            screen.draw(mapAvatarImg.get((int) DataUserManager.getProgress("avatar")), 870, 557);

            // icona notifica completamento Missions
            if ((boolean) DataUserManager.getProgress("completed_mission")) screen.draw(notifyCompletedMissions, 27, 285);

            // crediti
            fontBoldWhite20.draw(screen, formatter.format((int) DataUserManager.getProgress("credits")), 61, 516);

            // icona No Internet Connected
            if (!isConnected) screen.draw(noInternetIcon, 818, 581);
            if (InputManager.isHoverIconNoInternet) screen.draw(noInternetMessage, 601, 555);

            // crediti
            fontMediumWhite16.draw(screen, "BIGA Games", 53, 45); // firma al gioco
            fontMediumWhite16.draw(screen, "September 2025", 820, 45); // versione di gioco
        }

        int level = ((int) DataUserManager.getProgress("level"));

        // switch delle pagine per stampare i vari elementi
        switch (InputManager.page) {
            // pagina 'classic game'
            case 0:
                // titolo e sottotitolo pagina
                fontSemiboldYellow25.draw(screen, "CLASSIC GAME", 323, 494);
                fontMediumWhite20.draw(screen, "The new Space Invaders", 323, 457);

                // testi //
                fontMediumWhite20.draw(screen, "Points: " + formatter.format((int) DataUserManager.getProgress("points")), 332, 410); // punti totali
                fontMediumWhite20.draw(screen, "Matches: " + formatter.format((int) DataUserManager.getProgress("matches_CG")), 332, 381); // partite giocate
                fontBoldWhite20.draw(screen, formatter.format((int) DataUserManager.getProgress("num_gold_heart")), 695, 362); // numero 'gold heart'
                fontBoldWhite20.draw(screen, formatter.format((int) DataUserManager.getProgress("num_super_laser")), 695, 248); // numero 'super laser'
                fontBoldWhite20.draw(screen, formatter.format((int) DataUserManager.getProgress("num_shield")), 808, 362); // numero 'shield'
                fontBoldWhite20.draw(screen, formatter.format((int) DataUserManager.getProgress("num_double_points")), 808, 248); // numero 'double points'

                // NAVICELLA //
                // pulsante apertura pagina 'spacecrafts'
                if (InputManager.isOpenSpHover) screen.draw(buttonsOver[6], 318, 101);
                // immagine
                screen.draw(spImg, 330, 130);
                // nome
                fontMediumBlue15.draw(screen, selectedSp.getName(), 415, 226);
                // bonus velocità
                if (selectedSp.getSpSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getSpSpeed(), 480, 215);
                // bonus v. laser
                if (selectedSp.getLaserSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getLaserSpeed(), 480, 180);
                // bonus punti
                if (selectedSp.getBonusPoints()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getBonusPoints() + "%", 450, 145);

                // difficoltà
                switch ((int) DataUserManager.getProgress("diff_classic_game")) {
                    case 1:
                        screen.draw(diffCG1, 640 ,108);
                        break;
                    case 2:
                        screen.draw(diffCG2, 640 ,108);
                        break;
                    case 3:
                        screen.draw(diffCG3, 640 ,108);
                        break;
                }

                // selezione carta speciale
                if (InputManager.goldHeart) screen.draw(rectSelectCard, 688, 375);
                if (InputManager.superLaser) screen.draw(rectSelectCard, 688, 261);
                if (InputManager.shield) screen.draw(rectSelectCard, 801, 375);
                if (InputManager.doublePoints) screen.draw(rectSelectCard, 801, 261);

                // button start hover
                if (InputManager.isBtnStartHover) screen.draw(buttonsOver[0], 777, 105);

                break;

            // pagina 'space battle'
            case 1:
                // titolo e sottotitolo pagina
                fontSemiboldYellow25.draw(screen, "SPACE BATTLE", 323, 494);
                fontMediumWhite20.draw(screen, "Enjoy a 1v1 Battle", 323, 457);

                // testi //
                fontMediumWhite20.draw(screen, "Victories: " + formatter.format((int) DataUserManager.getProgress("won_SB")), 332, 411); // vittorie
                fontMediumWhite20.draw(screen, "Win Streak: " + formatter.format((int) DataUserManager.getProgress("win_streak_SB")), 332, 381); // vittorie consecutive
                fontMediumWhite20.draw(screen, "Matches: " + formatter.format((int) DataUserManager.getProgress("matches_SB")), 332, 351); // partite giocate
                fontBoldWhite20.draw(screen, formatter.format((int) DataUserManager.getProgress("num_gold_heart")), 695, 362); // numero 'gold heart'
                fontBoldWhite20.draw(screen, formatter.format((int) DataUserManager.getProgress("num_super_laser")), 808, 362); // numero 'super laser'

                // testo "crediti X vittoria"
                // calcolo crediti utente vinti
                int diff = (int) DataUserManager.getProgress("diff_space_battle");
                int streak = (int) DataUserManager.getProgress("win_streak_SB");
                int creditsSB=0;

                // calcolo crediti vinti
                if (streak>=1) {
                    switch (diff) {
                        case 1 -> creditsSB = (int) (10 * (Math.pow(1.2, streak)));
                        case 2 -> creditsSB = (int) (20 * (Math.pow(1.2, streak)));
                        case 3 -> creditsSB = (int) (30 * (Math.pow(1.2, streak)));
                    }
                }
                else creditsSB = diff*10;

                fontBoldWhite20.draw(screen, "+" + formatter.format(creditsSB), 691, 275);

                // NAVICELLA //
                // pulsante apertura pagina 'spacecrafts'
                if (InputManager.isOpenSpHover) screen.draw(buttonsOver[6], 318, 101);
                // immagine
                screen.draw(spImg, 330, 130);
                // nome
                fontMediumBlue15.draw(screen, selectedSp.getName(), 415, 226);
                // bonus velocità
                if (selectedSp.getSpSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getSpSpeed(), 480, 215);
                // bonus v. laser
                if (selectedSp.getLaserSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getLaserSpeed(), 480, 180);
                // bonus punti
                if (selectedSp.getBonusPoints()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getBonusPoints() + "%", 450, 145);

                // difficoltà
                switch ((int) DataUserManager.getProgress("diff_space_battle")) {
                    case 1:
                        screen.draw(diffSB1, 640 ,108);
                        break;
                    case 2:
                        screen.draw(diffSB2, 640 ,108);
                        break;
                    case 3:
                        screen.draw(diffSB3, 640 ,108);
                        break;
                }

                // selezione carta speciale
                if (InputManager.goldHeart) screen.draw(rectSelectCard, 688, 375);
                if (InputManager.superLaser) screen.draw(rectSelectCard, 801, 375);

                // testo informativo gioco bloccato
                if ((int) DataUserManager.getProgress("level")<11) screen.draw(infoBanner, 590, 165);

                // button fight hover
                if (InputManager.isBtnStartHover) screen.draw(buttonsOver[1], 777, 105);

                break;

            // pagina 'space journey'
            case 2:
                // titolo e sottotitolo pagina
                fontSemiboldYellow25.draw(screen, "SPACE JOURNEY", 323, 494);
                fontMediumWhite20.draw(screen, "Explore and conquire all the space's galaxies", 323, 457);

                // testi //
                if (level==41) level = 40;
                fontMediumWhite20.draw(screen, "Level: " + level, 332, 410); // livello
                fontMediumWhite20.draw(screen, "Current Galaxy: " + ((level) / 10 + 1), 332, 381); // galassia corrente

                // NAVICELLA //
                // pulsante apertura pagina 'spacecrafts'
                if (InputManager.isOpenSpHover) screen.draw(buttonsOver[6], 318, 101);
                // immagine
                screen.draw(spImg, 330, 130);
                // nome
                fontMediumBlue15.draw(screen, selectedSp.getName(), 415, 226);
                // bonus velocità
                if (selectedSp.getSpSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getSpSpeed(), 480, 215);
                // bonus v. laser
                if (selectedSp.getLaserSpeed()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getLaserSpeed(), 480, 180);
                // bonus punti
                if (selectedSp.getBonusPoints()>=1) fontBlue20.draw(screen, "+ " + selectedSp.getBonusPoints() + "%", 450, 145);

                // button map hover
                if (InputManager.isBtnStartHover) screen.draw(buttonsOver[2], 777, 105);

                break;

            // pagina 'Missions'
            case 3:
                // titolo e sottotitolo pagina
                fontSemiboldYellow25.draw(screen, "MISSIONS", 323, 494);
                fontMediumWhite20.draw(screen, "Complete different tasks to receive prizes", 323, 457);

                // recupero missione corrente
                int missionID = (int) DataUserManager.getProgress("mission_id");
                Missions m = Missions[missionID-1];

                // testi //
                fontMediumBlue20.draw(screen, "Task " + formatter.format((int) DataUserManager.getProgress("num_mission")), 514, 407); // numero missione raggiunta
                fontMediumBlue20.draw(screen, m.printMission(), 516, 365); // missione da completare
                fontMediumBlue20.draw(screen, "Prize: x" + m.prize, 660, 231); // premio missione

                // progresso completamento task corrente
                drawMissionsPage(screen, missionID);

                break;

            // pagina "spacecraft"
            case 4:
                drawSPSelectionPage(screen);
                break;
            // pagina 'marketplace'
            case 5:
                // titolo e sottotitolo pagina
                fontSemiboldYellow25.draw(screen, "MARKETPLACE", 323, 494);
                fontMediumWhite20.draw(screen, "Use your credits to buy cards and unique objects", 323, 457);

                // testi //
                fontBoldWhite25.draw(screen, formatter.format(InputManager.currentCredit), 700, 495); // numero totale crediti

                // numero prodotti da acquistare
                fontBoldWhite15.draw(screen, formatter.format(InputManager.item1), 384, 310); // item 1
                fontBoldWhite15.draw(screen, formatter.format(InputManager.item2), 533, 310); // item 2
                fontBoldWhite15.draw(screen, formatter.format(InputManager.item3), 683, 310); // item 3
                fontBoldWhite15.draw(screen, formatter.format(InputManager.item4), 833, 310); // item 4
                fontBoldWhite15.draw(screen, formatter.format(InputManager.item5), 487, 157); // item 5
                fontBoldWhite15.draw(screen, formatter.format(InputManager.item6), 730, 157); // item 6

                // testi "sold out" per specificare che i prodotti non sono disponibili
                if ((boolean) DataUserManager.getProgress("state_product_5")) screen.draw(txtSoldOut, 435, 160);
                if ((boolean) DataUserManager.getProgress("state_product_6")) screen.draw(txtSoldOut, 680, 160);

                // pulsante reset attivo
                if (InputManager.finalPrize>0) screen.draw(btnResetActive, 799, 76);
                // button confirm-purchase and reset hover
                if (InputManager.isBtnBuyHover) screen.draw(buttonsOver[4], 510, 74);
                if (InputManager.isBtnResetHover) screen.draw(buttonsOver[5], 799, 76);

                // prezzo finale
                fontBoldWhite25.draw(screen, formatter.format(InputManager.finalPrize), 530, 108); // prezzo d'acquisto finale
                break;

            // pagina 'profile info'
            case 6:
                // correzione scrittura livello raggiunto
                if (level==41) level = 40;

                // testi //
                // SCRITTE A SX
                fontMediumWhite20.draw(screen, "Nickname: " + AuthAlgorithms.nickname, 67, 340); // nickname
                fontMediumWhite20.draw(screen, "Password: " + AuthAlgorithms.password, 67, 300); // password
                fontMediumWhite20.draw(screen, "Creation ID: " + DataUserManager.getProgress("date"), 67, 260); // data registrazione

                // SCRITTE A DX
                fontMediumWhite20.draw(screen, "Points: " + formatter.format((int) DataUserManager.getProgress("points")), 540, 412); // punti
                fontMediumWhite20.draw(screen, "Level: " + level, 540, 372); // livello
                fontMediumWhite20.draw(screen, "Mission: " + formatter.format((int) DataUserManager.getProgress("num_mission")), 540, 332); // numero missione
                fontMediumWhite20.draw(screen, "Aliens Hit: " + formatter.format((int) DataUserManager.getProgress("num_aliens_hit")), 540, 292); // alieni colpiti
                fontMediumWhite20.draw(screen, "Credits: " + formatter.format((int) DataUserManager.getProgress("credits")), 540, 252); // crediti
                fontMediumWhite20.draw(screen, "All Time Credits: " + formatter.format((int) DataUserManager.getProgress("total_credits")), 540, 212); // crediti totali
                fontMediumWhite20.draw(screen, "C.G. Matches: " + formatter.format((int) DataUserManager.getProgress("matches_CG")), 540, 172); // partite classic game
                fontMediumWhite20.draw(screen, "S.B. Matches: " + formatter.format((int) DataUserManager.getProgress("matches_SB")), 540, 132); // partite space battle
                fontMediumWhite20.draw(screen, "S.B. Victories: " + formatter.format((int) DataUserManager.getProgress("won_SB")), 540, 92); // vittorie space battle

                // immagini //
                screen.draw(mapAvatarImg.get((int) DataUserManager.getProgress("avatar")), 461, 513); // avatar

                // avatar cap alpha
                screen.draw(InputManager.isGameCompleted ? avatars[20] : avatarsCovered[20], 93, 417);

                // pulsanti schiariti al passaggio
                if (InputManager.isBtnWiseManHover && InputManager.isGameCompleted) screen.draw(buttonsOver[13], 93, 417); // open wise man
                if (InputManager.isBtnGloryHover) screen.draw(buttonsOver[9], 178, 417); // rtg
                if (InputManager.isDeleteAccountHover) screen.draw(buttonsOver[11], 432, 354); // delete profile

                // messaggio di info wise man
                if (InputManager.isBtnWiseManHover && !InputManager.isGameCompleted) screen.draw(wiseManMessage, 24, 511);
                break;

            // pagina avatars
            case 7:
                // stampa immagini
                int x=145; int y=410; // x e y del primo avatar
                for (int i=0; i<=19; i++) {
                    // stampa immagine avatar
                    if (!Avatar.isAchieved(i)) {
                        fontBoldItalicWhite15.draw(screen, mapAvatars.get(i).getMissione(), x, y-15);
                        screen.draw(avatarsCovered[i], x, y);
                    }
                    else {
                        fontBoldWhite15.draw(screen, mapAvatars.get(i).getName(), x, y-15);
                        screen.draw(avatars[i], x, y);
                    }

                    // riquadro selezione
                    if (InputManager.selectedAvatar == i) screen.draw(selectedAvatar, x-5, y-5);

                    // posizione oggetti
                    x+=161;
                    if ((i+1)%5==0) { x=145; y-=111; } // reset posizione alla nuova riga
                }
                break;

            // pagina 'how to play'
            case 12:
                // immagine di sfondo
                screen.draw(mapLobby.get(12), 0, -InputManager.scrollY2);
                // banner in alto fisso
                screen.draw(topBanner2, 14, 514); // DEVE STARE QUI PERCHÈ STA SOPRA OGNI ALTRO ELEMENTO DELLA PAGINA
                break;
        }

        // disegno eventuale schermo sovrapposto
        if (InputManager.secondScreen) {
            if (InputManager.open17) screen.draw(mapLobby.get(17), 250, 175); // info software
            else if (InputManager.open13) { // chiusura gioco
                screen.draw(mapLobby.get(13), 250, 175);

                // button GREEN and RED hover
                if (InputManager.isBtnLHover) screen.draw(buttonsOver[7], 277, 217); // pulsante verde
                if (InputManager.isBtnRHover) screen.draw(buttonsOver[8], 519, 217); // pulsante rosso

                // scritte pulsanti
                fontBoldWhite60.draw(screen, "YES", 320, 280);
                fontBoldWhite60.draw(screen, "NO", 577, 280);
            }
            else if (InputManager.open18) { // avviso difficoltà elevata
                screen.draw(mapLobby.get(18), 250, 175);

                if (InputManager.isTickSelected) screen.draw(tickImg, 391, 330); // tick disattivazione messaggio

                // button GREEN and RED hover
                if (InputManager.isBtnLHover) screen.draw(buttonsOver[7], 277, 217); // pulsante verde
                if (InputManager.isBtnRHover) screen.draw(buttonsOver[8], 519, 217); // pulsante rosso

                // scritte pulsanti
                fontBoldWhite60.draw(screen, "OK", 339, 280);
                fontBoldWhite60.draw(screen, "PLAY", 548, 280);
            }
            else if (InputManager.open14) { // conferma acquisto
                screen.draw(mapLobby.get(14), 250, 175);

                // button GREEN and RED hover
                if (InputManager.isBtnLHover) screen.draw(buttonsOver[7], 277, 217); // pulsante verde
                if (InputManager.isBtnRHover) screen.draw(buttonsOver[8], 519, 217); // pulsante rosso

                // scritte pulsanti
                fontBoldWhite60.draw(screen, "YES", 320, 280);
                fontBoldWhite60.draw(screen, "NO", 577, 280);

                // scritte pulsanti
                // testo prezzo totale
                fontBoldWhite25.draw(screen, formatter.format(InputManager.finalPrize), 390, 347);
            }
            else if (InputManager.open16) drawSettingsPage(screen); // impostazioni di gioco
            else if (InputManager.open19) drawGloryPage(screen); // road to glory
            else if (InputManager.open20) drawLeaderboard(screen); // leaderboard
            else if (InputManager.open21) { // password change
                screen.draw(mapLobby.get(21), 250, 165);

                // button hover
                if (InputManager.isBtnChangePSWHover) screen.draw(buttonsOver[12], 424, 207);

                // testo "DELETE"
                fontBoldWhite40.draw(screen, "SAVE", 449, 249);

                // password digitata + icona mostra/nascondi psw
                if (!InputManager.showPS) {
                    fontBoldWhite30.draw(screen, "*".repeat(InputManager.passwordInput.length()), 290, 325);
                    screen.draw(coverPS, 685,303);
                }
                else {
                    fontBoldWhite30.draw(screen, InputManager.passwordInput, 290, 328);
                    screen.draw(showPS, 685,303);
                }
            }
            else if (InputManager.open22) { // delete profile
                screen.draw(mapLobby.get(22), 250, 165);

                // button hover
                if (InputManager.isBtnDeleteHover) screen.draw(buttonsOver[10], 423, 207);

                // testo "DELETE"
                fontBoldWhite35.draw(screen, "DELETE", 437, 247);
            }
            else if (InputManager.open23) { // wise man
                screen.draw(mapLobby.get(23), 145, 165);

                // messaggio cap alpha in giallo
                fontBoldItalicYellow20.draw(screen, "“The key to true success lies in patience and perseverance.\n" +
                    "And remember, never stop exploring.”", 208, 357);
                // testi sotto in bianco
                fontBoldItalicWhite20.draw(screen, "— Cap. Alpha", 208, 305);
                fontBoldWhite20.draw(screen, "Congratulations " + AuthAlgorithms.nickname + ", the journey has come to an end.", 208, 272);
                fontBoldWhite20.draw(screen, "Your glory will be remembered forever.", 208, 245);
            }
        }
    }

    // metodo per liberare la memoria
    public void disposeUI() {
        for (int i = 0; i < 20; i++) {
            avatars[i].dispose();
        }
        for (int i=4; i<=19; i++) {
            avatarsCovered[i].dispose();
        }
        selectedAvatar.dispose();

        for (Texture t : mapAvatarImg.values()) t.dispose();
        for (Texture t : mapLobby.values()) t.dispose();

        tickImg.dispose();
        diffCG1.dispose();
        diffCG2.dispose();
        diffCG3.dispose();
        diffSB1.dispose();
        diffSB2.dispose();
        diffSB3.dispose();
        bgSpacecraftSelection.dispose();
        spacecraftSelectionBox.dispose();
        topBanner.dispose();
        topBanner2.dispose();
        infoBanner.dispose();
        noInternetIcon.dispose();
        noInternetMessage.dispose();

        rectSelectCard.dispose();
        claimPrize.dispose();
        progressMissions.dispose();
        notifyCompletedMissions.dispose();
        txtSoldOut.dispose();

        soundOn.dispose();
        soundOff.dispose();
        musicOn.dispose();
        musicOff.dispose();
        volumeState.dispose();
        selectedSetting.dispose();

        for (Texture btn : buttonsOver) {
            btn.dispose();
        }

        for (Texture prize : MissionsPrizes) {
            prize.dispose();
        }

        for (Texture fragment : alphaFragments) {
            fragment.dispose();
        }

        fontBlue20.dispose();
        fontMediumBlue15.dispose();
        fontMediumBlue20.dispose();
        fontBoldBlue20.dispose();
        fontMediumWhite20.dispose();
        fontBoldWhite15.dispose();
        fontBoldWhite18.dispose();
        fontBoldWhite20.dispose();
        fontBoldWhite25.dispose();
        fontBoldItalicWhite15.dispose();
        fontBoldWhite60.dispose();

        spImg.dispose();
        infoBanner.dispose();

        mouse.dispose();
    }
}
