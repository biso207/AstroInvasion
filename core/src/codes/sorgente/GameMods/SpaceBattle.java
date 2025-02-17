/*
Astro Invasion - class Lobby -
This class manages and controls the game mode Classic Game
Developed by BIGA©. All rights reserved.
*/

package sorgente.GameMods;

// import librerie
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.Pool;
import sorgente.Main;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class SpaceBattle implements Screen {
    private final Main game;
    private final SpriteBatch screen;
    private final Texture spaceshipTexture, backgroundTexture, enemyTexture;
    private final Rectangle enemySpaceship;
    private final Rectangle spaceship;
    private float backgroundY1, backgroundY2;
    private final Array<Rectangle> lasers = new Array<>();
    private final Array<TextureRegion> collisionFrames = new Array<>();
    private final ArrayList<CollisionAnimation> activeAnimations = new ArrayList<>();
    private final Pool<Rectangle> laserPool;


    // Proprietà del giocatore
    float playerX, playerY; // Posizione del giocatore
    float playerWidth, playerHeight; // Dimensioni della navicella del giocatore
    int playerHealth; // Salute del giocatore
    boolean playerAlive = true; // Stato del giocatore

    // Proprietà del nemico
    float enemyX, enemyY; // Posizione del nemico
    float enemyWidth, enemyHeight; // Dimensioni della navicella del nemico
    int enemyHealth; // Salute del nemico
    boolean enemyAlive = true; // Stato del nemico

    // Proprietà dei laser
    float playerLaserX, playerLaserY; // Posizione del laser del giocatore
    float enemyLaserX, enemyLaserY; // Posizione del laser del nemico
    float laserWidth, laserHeight; // Dimensioni del laser
    float laserSpeed; // Velocità dei laser
    boolean playerLaserActive = false; // Stato del laser del giocatore
    boolean enemyLaserActive = false; // Stato del laser del nemico

    // Dimensioni dello schermo
    float screenHeight;

    // immagini delle vite
    private Texture life1, life2, life3, life4, goldHeartImg, shieldImg, brokenShieldImg,
        superLaserImg, topBar, playImg, stopImg, quitMatch;

    // formatter per la virgola delle migliaia in automatico converte l'intero in stringa
    NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

    // valori in gioco
    private float spacecraftSpeed;
    private float laserCooldownTimer = 0;
    private float laserCooldown;

    // statistiche
    private int lives, points, credits;

    // valori di incremento punti e crediti
    private int scoreInc, creditsInc;

    // stato del gioco (pausa/in gioco)
    private boolean isPaused = false;

    // stato quit match per la stampa dell'immagine
    private boolean quit = false;

    // dichiarazione font
    private BitmapFont font, fontGold;

    // musiche
    private final Music soundtrack; // sottofondo
    private final Sound creditSound, shotSound, hitSound; // suoni

    /* modalità di gioco
       la modalità di gioco definisce la schermata game over richiamata dalle diverse schermate delle diverse modalità
    */
    private final int mod = 0;

    // navicella utente
    private Spacecraft selectedSp;

    // costruttore
    public SpaceBattle(Main game, Spacecraft selectedSp) {
        this.game = game;
        this.screen = game.screen;
        this.enemySpaceship = new Rectangle(400, 600, 70, 64); // Posizionata in alto

        this.selectedSp = selectedSp;

        // immagine navicella
        spaceshipTexture = new Texture(selectedSp.getPathImg());
        // sfondo in gioco
        backgroundTexture = new Texture("images/bgInGame.png");

        //Navicella nemico
        enemyTexture = new Texture("images/spacecrafts/_alpha.png");

        // rettangolo che rappresenta la navicella
        spaceship = new Rectangle(400, 20, 70, 64);

        // init parametri in base alla difficoltà di gioco
        int difficulty = Lobby.getDiffCG();
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

        // caricamento font
        loadFont();

        // caricamento immagini
        loadImages();

        // musica di sottofondo
        soundtrack = Gdx.audio.newMusic(Gdx.files.internal("sounds/AstroInvasion_main_soundtrack.mp3")); // file audio
        soundtrack.setLooping(true); // true=loop music; false=no loop
        soundtrack.play(); // avvio musica

        // laser sparato
        shotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shot_sound.mp3"));
        // alieno colpito
        hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hit_sound.mp3"));
        // raccolta monete
        creditSound = Gdx.audio.newSound(Gdx.files.internal("sounds/credit_sound.wav"));
    }

    // -------------------- //
    // GRAFICA DELLA CLASSE //
    // -------------------- //

    // caricamento immagini
    private void loadImages() {
        // cuori delle vite
        life1 = new Texture("images/lives/heart 100%.png");
        life2 = new Texture("images/lives/heart 75%.png");
        life3 = new Texture("images/lives/heart 50%.png");
        life4 = new Texture("images/lives/heart 25%.png");
        goldHeartImg = new Texture("images/lives/gold heart.png");

        // barra in alto alla schermata di gioco
        topBar = new Texture("images/top_bar_classic_game.png");

        // pause/resume
        playImg = new Texture("images/play.png");
        stopImg = new Texture("images/stop.png");

        // quit match
        quitMatch = new Texture(Gdx.files.internal("secondary_screens/lobby_quit_match_eng.png"));

        // scudo
        shieldImg = new Texture("images/spacecrafts/_shield.png");
        brokenShieldImg = new Texture("images/spacecrafts/_broken_shield.png");
        // super laser
        superLaserImg = new Texture("images/spacecrafts/_super_laser.png");
    }

    // caricamento e creazione font per le scritte
    private void loadFont() {
        // dichiarazione font
        try {
            font = new BitmapFont(Gdx.files.internal("font/inter/bold_white_35.fnt")); // inter bold white 35
            fontGold = new BitmapFont(Gdx.files.internal("font/inter/bold_gold_35.fnt")); // inter bold gold 35
        } catch (Exception e) {
            font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("FFFFFF")); // colore white
        }
    }

    // ------------------- //
    // LOGICA DELLA CLASSE //
    // ------------------- //

    // metodo per modificare gli attributi navicella/alieni in base alla difficoltà scelta
    private void setupGameParameters(int difficulty) {
        switch (difficulty) {
            case 1:
                spacecraftSpeed = 300;
                laserSpeed = 100;
                laserCooldown = 0.3f;
                lives = 4;
                scoreInc = 50;
                creditsInc = 4;
                break;
            case 2:
                spacecraftSpeed = 400;
                laserSpeed = 150;
                laserCooldown = 0.2f;
                lives = 4;
                scoreInc = 100;
                creditsInc = 7;
                break;
            case 3:
                spacecraftSpeed = 500;
                laserSpeed = 200;
                laserCooldown = 0.1f;
                lives = 4;
                scoreInc = 200;
                creditsInc = 10;
                break;
        }

        spacecraftSpeed += selectedSp.getSpSpeed()*100;
        laserSpeed += selectedSp.getLaserSpeed()*100;
    }

    // -------------- //
    // GESTIONE INPUT //
    // -------------- //

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
            if ((screenX >= 519 && screenX <= 719) && (screenY >= 417 && screenY <= 497)) {
                quit = !quit;
                isPaused = !isPaused;
            }

            // click YES => interruzione gioco
            if ((screenX >= 281 && screenX <= 481) && (screenY >= 417 && screenY <= 497)) {
                gameOver();
                return; // uscita
            }

        }

        // gioco in pausa
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            isPaused = !isPaused;
        }

        // gioco in pausa => nessun altro input può essere preso
        if (isPaused) return;

        // movimento vs sx
        if (Gdx.input.isKeyPressed(Input.Keys.A) && spaceship.x > 10) {
            spaceship.x -= spacecraftSpeed * delta;
        }

        // movimento vs dx
        if (Gdx.input.isKeyPressed(Input.Keys.D) && spaceship.x < 890) {
            spaceship.x += spacecraftSpeed * delta;
        }

        // sparo del laser
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && laserCooldownTimer >= laserCooldown) {
            spawnLaser();
            shotSound.play();
            laserCooldownTimer = 0;
        }
    }

    // metodo per generare il laser
    private void spawnLaser() {
        Rectangle laser = laserPool.obtain();
        laser.set(spaceship.x + spaceship.width / 2 - 8, spaceship.y + spaceship.height, 30, 40);
        lasers.add(laser);
    }

    // metodo per muovere i laser sparati
    private void updateLasers(float deltaTime) {
        // Aggiorna il laser del giocatore
        if (playerLaserActive) {
            playerLaserY += laserSpeed * deltaTime; // Movimento del laser
            if (playerLaserY > screenHeight) {
                playerLaserActive = false; // Disattiva il laser se esce dallo schermo
            }
            // Controllo collisione con il nemico
            if (playerLaserX < enemyX + enemyWidth &&
                playerLaserX + laserWidth > enemyX &&
                playerLaserY < enemyY + enemyHeight &&
                playerLaserY + laserHeight > enemyY) {

                enemyHealth--; // Riduci la salute del nemico
                playerLaserActive = false; // Disattiva il laser dopo l'impatto
                if (enemyHealth <= 0) {
                    enemyAlive = false; // Il nemico è sconfitto
                }
            }
        }

        // Aggiorna il laser del nemico
        if (enemyLaserActive) {
            enemyLaserY -= laserSpeed * deltaTime; // Movimento del laser
            if (enemyLaserY < 0) {
                enemyLaserActive = false; // Disattiva il laser se esce dallo schermo
            }
            // Controllo collisione con il giocatore
            if (enemyLaserX < playerX + playerWidth &&
                enemyLaserX + laserWidth > playerX &&
                enemyLaserY < playerY + playerHeight &&
                enemyLaserY + laserHeight > playerY) {

                playerHealth--; // Riduci la salute del giocatore
                enemyLaserActive = false; // Disattiva il laser dopo l'impatto
                if (playerHealth <= 0) {
                    playerAlive = false; // Il giocatore è sconfitto
                }
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
    private void updateEnemySpaceship(float delta) {
        // Movimento automatico della navicella nemica
        if (MathUtils.randomBoolean(0.02f)) { // Cambia direzione casualmente
            enemySpaceship.x += MathUtils.random(-100, 100) * delta;
        }

        // Mantieni la navicella nemica entro i limiti dello schermo
        if (enemySpaceship.x < 0) enemySpaceship.x = 0;
        if (enemySpaceship.x > 930) enemySpaceship.x = 930; // Limite destro
    }


    // metodo per il controllo delle collisioni (by chatGPT)
    private void checkCollisions() {
        // Inizializza o pulisci il QuadTree
        QuadTree quadTree = new QuadTree(0, new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        quadTree.clear();

        /* Popola il QuadTree con i rettangoli degli alieni
        for (int i=0; i<aliens.size; i++) {
            quadTree.insert(aliens.get(i).getAlienRect());
        }

         */

        // Controlla le collisioni per ogni laser
        Iterator<Rectangle> laserIterator = lasers.iterator();
        while (laserIterator.hasNext()) {
            Rectangle laser = laserIterator.next();
            float previousY = laser.y; // Posizione precedente del laser
            float step = laserSpeed * Gdx.graphics.getDeltaTime(); // Movimento del laser

            laser.y += step;

            // Ottieni i potenziali rettangoli in collisione
            Array<Rectangle> potentialCollisions = new Array<>();
            quadTree.retrieve(potentialCollisions, laser);

            // for per il controllare le collisioni
            for (int i = 0; i < potentialCollisions.size; i++) {
                if (laserPathIntersects(previousY, laser.y, laser.x, potentialCollisions.get(i))) {
                    hitSound.play(); // suono alieno colpito

                    // rimozione laser
                    if (!Lobby.superLaser) {
                        laserIterator.remove();
                        laserPool.free(laser);
                    }

                    /* rimozione alieni colpiti/fuori dallo schermo
                    for (Iterator<Alien> alienIterator = aliens.iterator(); alienIterator.hasNext();) {
                        Alien alien = alienIterator.next();
                        if (alien.getAlienRect() == potentialCollisions.get(i)) {
                            alienIterator.remove();
                            break;
                        }
                    }

                    // aggiornamento statistiche partita
                    points += (Lobby.doublePoints ? scoreInc * 2 : scoreInc);
                    aliensHit++;
                    if (aliensHit % 5 == 0) {
                        creditSound.play();
                        credits += creditsInc;
                    }
                     */

                    if (i < potentialCollisions.size) { // Check aggiunto
                        activeAnimations.add(new CollisionAnimation(
                            potentialCollisions.get(i).x, potentialCollisions.get(i).y - 5, collisionFrames));
                    }

                    break; // esci dal ciclo dei rettangoli vicini
                }
            }

            // Rimuovi il laser se è uscito dallo schermo
            if (laser.y > Gdx.graphics.getHeight()) {
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
    private void gameOver() {

        // aggiunta bonus punti
        int bonusPoints = selectedSp.getBonusPoint();
        if (bonusPoints > 0) points = (points*bonusPoints)/100; // aggiunta percentuale di bonus

        soundtrack.stop();
        game.setScreen(new GameOver(game, selectedSp, mod, points, credits, 0));
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
    private void renderGame() {

        ScreenUtils.clear(0, 0, 0, 1);
        screen.begin();

        // Stampa sfondo
        screen.draw(backgroundTexture, 0, backgroundY1, Gdx.graphics.getWidth(), backgroundTexture.getHeight());
        screen.draw(backgroundTexture, 0, backgroundY2, Gdx.graphics.getWidth(), backgroundTexture.getHeight());

        // aggiunta scudo
        if (Lobby.shield) screen.draw(shieldImg, spaceship.x-25, spaceship.y);
        if (Lobby.shield) screen.draw(brokenShieldImg, spaceship.x-25, spaceship.y);

        // Stampa navicella
        screen.draw(spaceshipTexture, spaceship.x, spaceship.y);

        // Stampa laser
        for (Rectangle laser : lasers) {
            if (!Lobby.superLaser) screen.draw(selectedSp.getLaserTexture(), laser.x, laser.y);
            else screen.draw(superLaserImg, laser.x, laser.y);
        }

        //Stampa enemy

        screen.draw(enemyTexture, enemySpaceship.x, enemySpaceship.y);

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
        screen.draw(topBar, 20, 600);

        // stampa vite rimanenti
        switch(lives) {
            case 1:
                screen.draw(life4, 93, 640);
                break;
            case 2:
                screen.draw(life3, 93, 640);
                break;
            case 3:
                screen.draw(life2, 93, 640);
                break;
            case 4:
                screen.draw(life1, 93, 640);
                break;
        }

        if (Lobby.goldHeart && lives == 0) {
            screen.draw(goldHeartImg, 93, 640);
        }

        // stampa icona pausa
        if (isPaused) screen.draw(stopImg, 472, 637);
        else screen.draw(playImg, 472, 637);

        // stampa immagine per chiudere il gioco
        if (quit) screen.draw(quitMatch, 250, 175);

        // Stampa statistiche
        // crediti
        font.draw(screen, formatter.format(credits), 610, 670);
        // punti
        if (Lobby.doublePoints) fontGold.draw(screen, formatter.format(points), 220, 670);
        else font.draw(screen, formatter.format(points), 220, 670);
        // alieni colpiti

        screen.end();
    }


    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        if (!isPaused) {
            delta = Math.min(delta, 1 / 30f);
        }
        else delta=0;

        handleInput(delta);
        updateBackground(delta);
        updateLasers(delta);
        updateEnemySpaceship(delta);
        checkCollisions();

        renderGame();
    }

    @Override
    public void resize(int width, int height) {}

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
    // rilascio risorse
    public void dispose() {
        screen.dispose();
        spaceshipTexture.dispose();
        backgroundTexture.dispose();
        font.dispose();
    }
}
