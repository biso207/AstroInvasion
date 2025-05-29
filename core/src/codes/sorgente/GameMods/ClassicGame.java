/*
Astro Invasion - class ClassicGame -
Controlla e gestisce la modalità di gioco Classic Game
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.GameMods;

// import librerie e codici
import sorgente.Entities.Alien;
import sorgente.Entities.Spacecraft;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ScreenUtils;
import sorgente.DataUserManager;
import sorgente.Main;
import sorgente.Lobby.InputManager;
import sorgente.Lobby.UIManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class ClassicGame implements Screen, InputProcessor {
    private final Main game;
    private final SpriteBatch screen;
    private final Texture spaceshipTexture, backgroundTexture;
    private final Texture[] alienTextures;
    private final Rectangle spaceship;
    private float backgroundY1, backgroundY2;
    private final Array<Rectangle> lasers = new Array<>();
    private final Array<Alien> aliens = new Array<>();
    private final Array<TextureRegion> collisionFrames = new Array<>();
    private final ArrayList<CollisionAnimation> activeAnimations = new ArrayList<>();
    private final Pool<Rectangle> laserPool;

    private Texture goldHeartImg, shieldImg, superLaserImg, topBar, topBarLevel, shieldBanner, shieldIcon, playImg,
        stopImg, quitMatch, bannerRTG, btnHoverR, btnHoverL;

    // matrice per le immagini delle vite rimanenti
    private Texture[][] livesTextures;

    // formatter per la virgola delle migliaia in automatico converte l'intero in stringa
    private NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

    // valori in gioco
    private float spacecraftSpeed, laserSpeed, alienSpeed;
    private float laserCooldownTimer = 0;
    private float laserCooldown;
    private float spawnTimer = 0;
    private float spawnInterval;

    // statistiche
    private int totalLives, lives, aliensHit, points, credits;

    // valori di incremento punti e crediti
    private int scoreInc, creditsInc;

    // stato del gioco (pausa/in gioco)
    private boolean isPaused = false;
    // stato sparo del laser
    private boolean shootPressed = false;

    // tempo per mostrare la notifica di completamento RTG
    private float elapsedTime = 0;

    // stato quit match per la stampa dell'immagine
    private boolean quit = false;
    // stato game over per evitare doppie letture progressi
    private boolean gameClosed = false;
    // stato completamento missione RTG
    private boolean completedRTG = false;

    // dichiarazione font
    private BitmapFont font, fontGold, fontBoldWhite60;
    // stato cambio stile mouse
    private boolean isBtnRHover=false, isBtnLHover=false;

    // audio di gioco
    private final Sound creditSound, shotSound, hitSound, completedRTGSound;
    // stato suono completamento RTG
    private boolean completedRTGSoundPlayed=false;

    // movimento in gioco
    public int moveLeftKey, moveRightKey, shootKey;

    // modalità di gioco che definisce la schermata game over richiamata dalle diverse schermate delle diverse modalità
    private final int mod = 0;

    // boolean per le carte speciali
    private boolean doublePoints=false, superLaser=false, shield=false, usedShield=false, goldHeart=false;

    // navicella utente
    private final Spacecraft selectedSp;

    // booleano per controllare se si sta giocando un livello
    private final boolean isLevel;
    // livello attuale
    private final int numLevel;
    // colore alieno
    private int type;

    // tempo trascorso per lo scudo
    private float timePassed=0;
    private final float shieldTime=30f;

    // costruttore
    public ClassicGame(Main game, Spacecraft selectedSp, boolean isLevel) {
        this.game = game;
        this.screen = game.screen;
        this.isLevel = isLevel;

        numLevel = (int) DataUserManager.getProgress("level");
        System.out.println(numLevel);
        // navicella utente inizializzata
        this.selectedSp = selectedSp;

        // colore alieno di default
        type=0;

        // immagine navicella
        spaceshipTexture = new Texture(selectedSp.getPathImg());
        // sfondo in gioco
        backgroundTexture = new Texture("images/bgInGame.png");
        // init delle immagini degli alieni
        alienTextures = new Texture[] {
            new Texture("images/aliens/Alien_Blue.png"),
            new Texture("images/aliens/Alien_Green.png"),
            new Texture("images/aliens/Alien_Pink.png"),
            new Texture("images/aliens/Alien_Red.png"),
            new Texture("images/aliens/Alien_White.png"),
            new Texture("images/aliens/Alien_Yellow.png")
        };

        // rettangolo che rappresenta la navicella
        spaceship = new Rectangle(400, 20, 70, 64);

        // init parametri in base alla difficoltà di gioco
        int difficulty=1;
        if (!isLevel) difficulty = (int) DataUserManager.getProgress("diff_classic_game");
        else {
            switch ((int) Math.ceil((double) numLevel / 10)) {
                case 1 -> difficulty=1;
                case 2, 3 -> difficulty=2;
                case 4 -> difficulty=3;
            }
        }
        setupGameParameters(difficulty);

        // posizione y dello sfondo dinamico
        backgroundY1 = 0;
        backgroundY2 = backgroundTexture.getHeight();

        // pooling del laser
        laserPool = new Pool<>() {
            @Override
            protected Rectangle newObject() {
                return new Rectangle();
            }
        };

        // caricamento frame per l'animazione
        for (int i = 0; i <= 31; i++) {
            collisionFrames.add(new TextureRegion(new Texture("images/collision_explosion/expl_06_00" + i + ".png"), 80, 80));
        }

        // init alieni colpiti
        aliensHit = points = credits = 0;

        // caricamento font
        loadFont();
        // caricamento immagini
        loadImages();

        // SUONI
        // laser sparato
        shotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shot_sound.mp3"));
        // alieno colpito
        hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hit_sound.mp3"));
        // raccolta monete
        creditSound = Gdx.audio.newSound(Gdx.files.internal("sounds/credit_sound.wav"));
        // task RTG completata
        completedRTGSound = Gdx.audio.newSound(Gdx.files.internal("sounds/completed_rtg.mp3"));

        // setting comando di movimento
        if (((int) DataUserManager.getProgress("movement_type")) == 1) {
            moveLeftKey = Input.Keys.A;
            moveRightKey = Input.Keys.D;
        }
        else {
            moveLeftKey = Input.Keys.LEFT;
            moveRightKey = Input.Keys.RIGHT;
        }
        // setting comando di sparo
        if (((int)DataUserManager.getProgress("shot_type")) == 1) shootKey = Input.Buttons.LEFT;
        else shootKey = Input.Keys.SPACE;

        // attivazione carte utente
        if (selectedSp.getName().equals("Drakar")) doublePoints = true;
        if (selectedSp.getName().equals("Rorik")) superLaser = true;
        if (selectedSp.getName().equals("Astrid")) { shield = true; usedShield = true; }
        if (selectedSp.getName().equals("Alpha")) goldHeart = true;

        // recupero stato attivazione carta speciale dall'InputManager della Lobby
        if (InputManager.goldHeart) goldHeart = true;
        if (InputManager.shield) { shield = true; usedShield = true; }
        if (InputManager.superLaser) superLaser = true;
        if (InputManager.doublePoints) doublePoints = true;

        // disattivazione generica in caso si stia giocando un livello
        if (isLevel) goldHeart=shield=superLaser=doublePoints=false;

        /// Per provare le carte basta settare tutte le variabili a 'true'
    }

    // metodo per modificare gli attributi navicella/alieni in base alla difficoltà scelta
    private void setupGameParameters(int difficulty) {
        switch (difficulty) {
            case 1:
                spacecraftSpeed = 200;
                laserSpeed = 200;
                alienSpeed = 250;
                spawnInterval = 0.2f;
                laserCooldown = 0.3f;
                lives = totalLives = 2;
                scoreInc = 50;
                creditsInc = 1;
                break;
            case 2:
                spacecraftSpeed = 200;
                laserSpeed = 200;
                alienSpeed = 300;
                spawnInterval = 0.15f;
                laserCooldown = 0.3f;
                lives = totalLives = 2;
                scoreInc = 75;
                creditsInc = 2;
                break;
            case 3:
                spacecraftSpeed = 200;
                laserSpeed = 200;
                alienSpeed = 350;
                spawnInterval = 0.1f;
                laserCooldown = 0.3f;
                lives = totalLives = 2;
                scoreInc = 100;
                creditsInc = 3;
                break;
        }
        spacecraftSpeed += selectedSp.getSpSpeed()*100;
        laserSpeed += selectedSp.getLaserSpeed()*100;
    }

    // metodo per controllare il completamento della task del 'road to glory' (RTG)
    public void checkCompletedRTG() {
        // controllo completamento task rtg
        if (!(boolean) DataUserManager.getProgress("completed_RTG") && !gameClosed) {
            int missionID = (int) DataUserManager.getProgress("mission_id");
            int progress = switch (missionID) {
                case 1 -> aliensHit + ((int) DataUserManager.getProgress("num_aliens_hit_RTG"));
                case 3 -> points + (int) DataUserManager.getProgress("points_RTG");
                case 4 -> credits + (int) DataUserManager.getProgress("credits_RTG");
                default -> -1;
            };

            // setting stato task RTG a true (completato)
            if (progress >= UIManager.RTGs[missionID-1].calcNumObjMission() && progress != -1) {
                DataUserManager.setProgress("completed_RTG", true);
                completedRTG = true;
            }
        }
    }

    // ******************* //
    // CARICAMENTO RISORSE //
    // ******************* //

    // caricamento immagini
    private void loadImages() {
        // cuori delle vite
        Texture life1 = new Texture("images/lives/heart 100%.png");
        Texture life2 = new Texture("images/lives/heart 75%.png");
        Texture life3 = new Texture("images/lives/heart 66%.png");
        Texture life4 = new Texture("images/lives/heart 50%.png");
        Texture life5 = new Texture("images/lives/heart 33%.png");
        Texture life6 = new Texture("images/lives/heart 25%.png");
        goldHeartImg = new Texture("images/lives/gold heart.png");


        livesTextures = new Texture[][]{
            {life4, life1}, // totalLives = 2
            {life5, life3, life1}, // totalLives = 3
            {life6, life4, life2, life1} // totalLives = 4
        };

        // barra in alto alla schermata di gioco
        topBar = new Texture("images/top_bar_classic_game.png");
        topBarLevel = new Texture("images/top_bar_classic_game_levels.png");

        // pause/resume
        playImg = new Texture("images/play.png");
        stopImg = new Texture("images/stop.png");

        // quit match
        quitMatch = new Texture(Gdx.files.internal("lobby_screens/lobby (16).png"));

        // scudo
        shieldImg = new Texture("images/spacecrafts/_shield.png");
        // super laser
        superLaserImg = new Texture("images/spacecrafts/_super_laser.png");

        // banner scudo attivo
        shieldBanner = new Texture("images/shield_bar.png");
        // icona dello scudo
        shieldIcon = new Texture("images/lives/shield_icon.png");

        // notifica completamente RTG
        bannerRTG = new Texture("images/completed_rtg_notification_eng.png");

        // pulsanti hover
        btnHoverL = new Texture("images/btns_hover/hover_btn8.png");
        btnHoverR = new Texture("images/btns_hover/hover_btn9.png");
    }

    // caricamento e creazione font per le scritte
    private void loadFont() {
        // dichiarazione font
        try {
            font = new BitmapFont(Gdx.files.internal("font/inter/bold_white_35.fnt")); // inter-bold white 35
            fontGold = new BitmapFont(Gdx.files.internal("font/inter/bold_gold_35.fnt")); // inter-bold gold 35
            fontBoldWhite60 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_60_1.fnt")); // inter-bold white 60
        } catch (Exception e) {
            font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("FFFFFF")); // colore white
        }
    }

    // **************** //
    // GESTIONE GRAFICA //
    // **************** //

    // metodo per generare il laser
    private void spawnLaser() {
        Rectangle laser = laserPool.obtain();
        laser.set(spaceship.x + spaceship.width / 2 - 8, spaceship.y + spaceship.height, 30, 40);

        // TODO: fare diverse prove per verificare che non si verifichino lag o problemi con il super laser non limitato a 2.
        /*
        if (superLaser) {
            // Assicura che non ci siano più superLaser del previsto
            if (lasers.size < 2) {
                lasers.add(laser);
            } else {
                laserPool.free(laser); // Se ce ne sono già 2, evita di aggiungerne altri
            }
        } else {
            lasers.add(laser);
        }

         */
        lasers.add(laser);
    }

    // metodo per muovere i laser sparati
    private void updateLasers(float delta) {
        float step = laserSpeed * delta; // step di movimento => velocità frame x frame

        for (Iterator<Rectangle> iterator = lasers.iterator(); iterator.hasNext();) {
            Rectangle laser = iterator.next();
            laser.y += step; // aggiornamento posizione laser (movimento verso l'alto)

            // rimozione laser fuori dallo schermo
            if (laser.y > Gdx.graphics.getHeight()) {
                iterator.remove();
                laserPool.free(laser);
            }
        }
    }

    // metodo per aggiornare il movimento dello sfondo
    private void updateBackground(float delta) {
        backgroundY1 -= 50 * delta;
        backgroundY2 -= 50 * delta;

        if (backgroundY1 + backgroundTexture.getHeight() <= 0) {
            backgroundY1 = backgroundY2 + backgroundTexture.getHeight();
        }

        if (backgroundY2 + backgroundTexture.getHeight() <= 0) {
            backgroundY2 = backgroundY1 + backgroundTexture.getHeight();
        }
    }

    // metodo per il movimento degli alieni
    private void updateAliens(float delta) {
        spawnTimer += delta;

        // creazione alieno
        if (spawnTimer >= spawnInterval) {
            if (isLevel) {
                switch ((int) Math.ceil((double) numLevel / 10)) {
                    case 1 -> type = 3;
                    case 2 -> type = 1;
                    case 3 -> type = 2;
                    case 4 -> type = 0;
                }
            }
            else type = MathUtils.random(alienTextures.length - 1);

            Alien alien = new Alien(
                alienTextures[type],
                new Rectangle(MathUtils.random(20, Gdx.graphics.getWidth() - 84), 700, 64, 64)
            );
            aliens.add(alien);
            spawnTimer = 0;
        }

        for (int i = aliens.size - 1; i >= 0; i--) {
            Alien alien = aliens.get(i);
            alien.getAlienRect().y -= alienSpeed * delta;

            // collisione alieno-navicella
            if (alien.getAlienRect().overlaps(spaceship)) {

                aliens.removeIndex(i);
                if (!shield) lives--;

                if ((lives == 0 && !goldHeart) || (goldHeart && lives == -1)) {
                    // caso game over
                    gameOver(false);
                    gameClosed = true;
                    return; // uscita
                }
            }

            if (alien.getAlienRect().y < 0) {
                aliens.removeIndex(i);
            }
        }
    }

    // metodo per il controllo delle collisioni (by chatGPT)
    private void checkCollisions() {
        // Inizializza o pulisci il QuadTree
        QuadTree quadTree = new QuadTree(0, new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        quadTree.clear();

        // Popola il QuadTree con i rettangoli degli alieni
        for (int i=0; i<aliens.size; i++) {
            quadTree.insert(aliens.get(i).getAlienRect());
        }

        // Controlla le collisioni per ogni laser
        Iterator<Rectangle> laserIterator = lasers.iterator();
        while (laserIterator.hasNext()) {
            Rectangle laser = laserIterator.next();
            float previousY = laser.y; // Posizione precedente del laser
            float step = laserSpeed * Gdx.graphics.getDeltaTime(); // Movimento del laser

            laser.y += step;
            boolean removed = false; // flag di controllo rimozione laser

            // Ottieni i potenziali rettangoli in collisione
            Array<Rectangle> potentialCollisions = new Array<>();
            quadTree.retrieve(potentialCollisions, laser);

            // for per il controllare le collisioni
            for (int i = 0; i < potentialCollisions.size; i++) {
                if (laserPathIntersects(previousY, laser.y, laser.x, potentialCollisions.get(i))) {
                    hitSound.play(InputManager.soundPercent); // suono alieno colpito

                    // rimozione laser
                    if (!superLaser) {
                        laserIterator.remove();
                        laserPool.free(laser);
                        removed = true; // laser rimosso
                    }

                    // rimozione alieni colpiti/fuori dallo schermo
                    for (Iterator<Alien> alienIterator = aliens.iterator(); alienIterator.hasNext();) {
                        Alien alien = alienIterator.next();
                        if (alien.getAlienRect() == potentialCollisions.get(i)) {
                            alienIterator.remove();
                            break;
                        }
                    }

                    // aggiornamento statistiche partita //
                    aliensHit=350; // incremento alieni colpiti // todo: settare semplicemente aliensHit++ quando non si fanno prove
                    // controllo completamento livello
                    if (isLevel && aliensHit >= (numLevel*10)) gameOver(true);

                    if (!isLevel) {
                        points += (doublePoints ? scoreInc * 2 : scoreInc); // incremento punti
                        if (aliensHit % 5 == 0) {
                            creditSound.play(InputManager.soundPercent);
                            credits += creditsInc; // incremento crediti
                        }
                    }

                    if (i < potentialCollisions.size) { // check aggiunto
                        activeAnimations.add(new CollisionAnimation(
                            potentialCollisions.get(i).x, potentialCollisions.get(i).y - 5, collisionFrames));
                    }

                    break; // esci dal ciclo dei rettangoli vicini
                }
            }

            // rimozione laser fuori dallo schermo
            if (!removed && laser.y > Gdx.graphics.getHeight()) {
                laserIterator.remove();
                laserPool.free(laser);
            }
        }
    }

    // metodo per tracciare il percorso dei laser (by chatGPT)
    private boolean laserPathIntersects(float previousY, float currentY, float x, Rectangle alienRect) {
        // Aggiungi margini al percorso del laser per garantire la collisione
        float margin = 5; // Tolleranza aggiuntiva
        Rectangle laserPath = new Rectangle(
            x - margin,
            Math.min(previousY, currentY) - margin,
            30,
            Math.abs(currentY - previousY) + 2 * margin
        );
        return laserPath.overlaps(alienRect);
    }

    // metodo per richiamare la schermata del game over
    private void gameOver(boolean win) {
        // diminuzione carte speciali
        if (goldHeart && !selectedSp.getName().equals("Alpha")) DataUserManager.setProgress("num_gold_heart", (int) DataUserManager.getProgress("num_gold_heart")-1);
        if (usedShield && !selectedSp.getName().equals("Astrid")) DataUserManager.setProgress("num_shield", (int) DataUserManager.getProgress("num_shield")-1);
        if (superLaser && !selectedSp.getName().equals("Rorik")) DataUserManager.setProgress("num_super_laser", (int) DataUserManager.getProgress("num_super_laser")-1);
        if (doublePoints && !selectedSp.getName().equals("Drakar")) DataUserManager.setProgress("num_double_points", (int) DataUserManager.getProgress("num_double_points")-1);

        // incremento partite giocate
        if (aliensHit >= 20 && !isLevel) DataUserManager.setProgress("matches_CG", (int) DataUserManager.getProgress("matches_CG")+1);

        // aggiunta bonus punti
        if (!isLevel) points = points+((points*(selectedSp.getBonusPoints()))/100); // aggiunta percentuale di bonus

        // TODO: passare lo stato giusto e aggiungere parametro che specifichi che si arriva da un livello
        int[] stats = {points, credits, aliensHit};
        // apertura pagina di game over
        game.setScreen(new GameOver(game, selectedSp, mod, stats, win, isLevel));
    }

    // classe per le animazioni
    private static class CollisionAnimation {
        float x, y;
        float timer;
        final float duration = 0.5f; // Durata totale dell'animazione
        Array<TextureRegion> frames;

        public CollisionAnimation(float x, float y, Array<TextureRegion> frames) {
            this.x = x;
            this.y = y;
            this.timer = 0;
            this.frames = frames;
        }

        public boolean isFinished() {
            return timer >= duration;
        }

        public TextureRegion getCurrentFrame() {
            int frameIndex = (int) ((timer / duration) * frames.size);
            return frames.get(Math.min(frameIndex, frames.size - 1));
        }
    }

    // metodo per la stampa di tutte le grafiche aggiornate
    private void renderGame(float delta) {

        ScreenUtils.clear(0, 0, 0, 1);
        screen.begin();

        // stampa sfondo
        screen.draw(backgroundTexture, 0, backgroundY1, Gdx.graphics.getWidth(), backgroundTexture.getHeight());
        screen.draw(backgroundTexture, 0, backgroundY2, Gdx.graphics.getWidth(), backgroundTexture.getHeight());

        // aggiunta scudo
        if (shield) {
            // banner scudo attivo a lato con i secondi rimanenti
            screen.draw(shieldBanner, 810, 490);
            // secondi rimanenti scudo attivo
            font.draw(screen, String.valueOf(Math.round(shieldTime-timePassed)), 895, 547);

            // stampa immagine dello scudo sulla navicella
            screen.draw(shieldImg, spaceship.x - 25, spaceship.y);
        }

        // stampa navicella
        screen.draw(spaceshipTexture, spaceship.x, spaceship.y);

        // stampa laser
        for (Rectangle laser : lasers) {
            if (!superLaser) screen.draw(selectedSp.getLaserTexture(), laser.x, laser.y);
            else screen.draw(superLaserImg, laser.x, laser.y);
        }

        // stampa alieni
        for (Alien alien : aliens) {
            screen.draw(alien.getImg(), alien.getAlienRect().x, alien.getAlienRect().y);
        }

        // stampa animazioni di collisione
        Iterator<CollisionAnimation> iterator = activeAnimations.iterator();
        while (iterator.hasNext()) {
            CollisionAnimation animation = iterator.next();
            if (animation.isFinished()) {
                iterator.remove();
            } else {
                screen.draw(animation.getCurrentFrame(), animation.x, animation.y);
                animation.timer += Gdx.graphics.getDeltaTime();
            }
        }

        // stampa barra in alto
        screen.draw(isLevel ? topBarLevel : topBar, 20, 600);

        // stampa vite rimanenti
        if (totalLives >= 2 && totalLives <= 4 && lives >= 1 && lives <= totalLives) {
            if (shield) screen.draw(shieldIcon, 93, 640); // icona scudo al posto del cuore
            else screen.draw(livesTextures[totalLives - 2][lives - 1], 93, 640);
        }

        // stampa cuore d'oro se attivato
        if (goldHeart && lives == 0 && !isLevel) screen.draw(goldHeartImg, 93, 640);

        // stampa icona pausa
        if (isPaused) screen.draw(stopImg, 472, 637);
        else screen.draw(playImg, 472, 637);

        // stampa immagine per chiudere il gioco
        if (quit) {
            screen.draw(quitMatch, 250, 175);

            if (isBtnLHover) screen.draw(btnHoverL, 277, 217);
            else if (isBtnRHover) screen.draw(btnHoverR, 519, 217);

            // scritte pulsanti
            fontBoldWhite60.draw(screen, "YES", 320, 280);
            fontBoldWhite60.draw(screen, "NO", 577, 280);
        }

        // stampa statistiche
        if (!isLevel) {
            // crediti
            font.draw(screen, formatter.format(credits), 610, 671);
            // punti
            if (doublePoints) fontGold.draw(screen, formatter.format(points), 220, 671);
            else font.draw(screen, formatter.format(points), 220, 671);
        }
        // alieni colpiti
        if (isLevel) {
            font.draw(screen, formatter.format(aliensHit) + "/" + (numLevel*10), 610, 670);
        }
        else {
            font.draw(screen, formatter.format(aliensHit), 800, 670);
        }

        // stampa messaggio completamento task RTG
        if (!isLevel) {
            checkCompletedRTG(); // chiamata metodo per controllare il completamento della mission RTG
            if (!completedRTGSoundPlayed) { completedRTGSound.play(InputManager.soundPercent); completedRTGSoundPlayed=false; }
            if (completedRTG && elapsedTime <= 4f) { // 4f = 4 secondi
                // conteggio tempo per mostrare la notifica
                elapsedTime += delta;
                screen.draw(bannerRTG, 400, 515); // banner di notifica
            }
        }

        screen.end();
    }

    // TODO: implementare i metodi dell'interfaccia InputProcessor partendo dal metodo handleInput
    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    // metodo per controllare l'input
    private void handleInput(float delta) {
        laserCooldownTimer += delta;

        // click esc per chiudere la partita
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!isPaused) isPaused = true;
            quit = !quit;
        }

        // chiusura partita
        if (quit && (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))) {

            // recupero x e y del click
            int screenX = Gdx.input.getX();
            int screenY = Gdx.input.getY();

            // click NO => si continua a giocare
            if ((screenX >= 510 && screenX <= 715) && (screenY >= 403 && screenY <= 475)) {
                quit = !quit;
                isPaused = !isPaused;
            }

            // click YES => interruzione gioco
            if ((screenX >= 269 && screenX <= 484) && (screenY >= 403 && screenY <= 475)) {
                // perdita completa dei progressi di gioco
                points=credits=aliensHit=0;

                gameOver(false);
                gameClosed = true;
                return; // uscita
            }

        }

        // gioco in pausa
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) isPaused = !isPaused;

        // gioco in pausa => nessun altro input può essere preso
        if (isPaused) return;

        // movimento vs sx
        if (Gdx.input.isKeyPressed(moveLeftKey) && spaceship.x > 10) {
            spaceship.x -= spacecraftSpeed * delta;
        }

        // movimento vs dx
        if (Gdx.input.isKeyPressed(moveRightKey) && spaceship.x < 890) {
            spaceship.x += spacecraftSpeed * delta;
        }

        // sparo del laser
        if (shootPressed && laserCooldownTimer >= laserCooldown) {
            spawnLaser();
            shotSound.play(InputManager.soundPercent);
            laserCooldownTimer = 0;
        }
    }

    // metodo per cambiare lo stile dei pulsanti al passaggio del mouse sopra di essi
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        isBtnRHover=isBtnLHover=false;
        // CAMBIO STILE PULSANTI
        // YES quit
        if (isPaused && (screenX >= 269 && screenX <= 484) && (screenY >= 403 && screenY <= 475)) {
            isBtnLHover=true;
        }

        // NO quit
        if ((screenX >= 510 && screenX <= 715) && (screenY >= 403 && screenY <= 475)) {
            isBtnRHover=true;
        }
        return true;
    }

    // metodo per ascoltare il click del mouse
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) shootPressed = true;
        else if (button == Input.Keys.SPACE) shootPressed = true;

        return true;
    }

    // metodo per ascoltare il rilascio del mouse
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) shootPressed = false;
        else if (button == Input.Keys.SPACE) shootPressed = false;
        return true;
    }

    // altri metodi
    @Override public boolean keyTyped(char character) { return true; }
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

    // ****************************** //
    // METODI DELL'INTERFACCIA Screen //
    // ****************************** //

    // aggiornamento grafica
    @Override public void render(float delta) {
        Gdx.input.setInputProcessor(this);
        if (!isPaused) {
            delta = Math.min(delta, 1 / 30f);
            timePassed+=delta; // incremento durata scudo
        }
        else delta=0;

        // disattivazione "scudo" dopo 30 secondi contati tramite il deltaTime
        if (timePassed>=shieldTime) shield = false;

        handleInput(delta);
        updateBackground(delta);
        updateLasers(delta);
        updateAliens(delta);
        checkCollisions();

        // aggiornamento globale grafiche
        renderGame(delta);
    }

    // spegnimento controllo input
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    // rilascio risorse
    @Override public void dispose() {
        screen.dispose();
        spaceshipTexture.dispose();
        backgroundTexture.dispose();
        for (Texture texture : alienTextures) {
            texture.dispose();
        }
        font.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
