package com.biga.astroinvasion;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.Iterator;

public class ClassicGame implements Screen {
    private final Main game;
    private final SpriteBatch screen;
    private final Texture spaceshipTexture, backgroundTexture;
    private final Texture[] alienTextures;
    private final Rectangle spaceship;
    private float backgroundY1, backgroundY2;
    private final ArrayList<Rectangle> lasers = new ArrayList<>();
    private final ArrayList<Alien> aliens = new ArrayList<>();
    private final Array<TextureRegion> collisionFrames = new Array<>();
    private final ArrayList<CollisionAnimation> activeAnimations = new ArrayList<>();
    private final Pool<Rectangle> laserPool;
    private Texture blurTexture;

    // immagini delle vite
    private Texture life1, life2, life3, life4;


    // valori in gioco
    private float spacecraftSpeed, laserSpeed, alienSpeed;
    private float laserCooldownTimer = 0;
    private float laserCooldown;
    private float spawnTimer = 0;
    private float spawnInterval;

    // statistiche
    private int lives, aliensHit;

    // valori di incremento punti e crediti
    private int scoreInc, creditsInc;

    // quadrtree per le collisione
    private QuadTree quadTree;

    // dichiarazione font
    private BitmapFont font;

    // musica di sottofondo
    Music soundtrack;

    // costruttore
    public ClassicGame(Main game) {
        this.game = game;
        this.screen = game.screen;

        // immagine navicella
        spaceshipTexture = new Texture(Lobby.selectedSp.getPathImg());
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
        spaceship = new Rectangle(400, 20, 64, 64);

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

        // init alieni colpiti
        aliensHit = 0;

        // caricamento font
        loadFont();

        // caricamento zona sfocata per le scritte
        createBlurredBackground();

        // caricamento immagini
        loadImages();

        // creazione del quadtree per le collisioni
        quadTree = new QuadTree(0, new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        // musica di sottofondo
        soundtrack = Gdx.audio.newMusic(Gdx.files.internal("sounds/AstroInvasion_main_soundtrack.mp3")); // file audio
        soundtrack.setLooping(true); // true=loop music; false=no loop
        soundtrack.play(); // avvio musica
    }

    // -------------------- //
    // GRAFICA DELLA CLASSE //
    // -------------------- //

    // caricamento immagini
    private void loadImages() {
        life1 = new Texture("images/lives/vita1.png");
        life2 = new Texture("images/lives/vita2.png");
        life3 = new Texture("images/lives/vita3.png");
        life4 = new Texture("images/lives/vita4.png");
    }

    // caricamento e creazione font per le scritte
    private void loadFont() {
        // dichiarazione font
        try {
            font = new BitmapFont(Gdx.files.internal("font/inter/regular_black_25.fnt")); // inter regular black 25
        } catch (Exception e) {
            font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("000000")); // colore black
        }
    }

    // metodo per modificare gli attributi navicella/alieni in base alla difficoltà scelta
    private void setupGameParameters(int difficulty) {
        switch (difficulty) {
            case 1:
                spacecraftSpeed = 400;
                laserSpeed = 150;
                alienSpeed = 80;
                spawnInterval = 0.8f;
                laserCooldown = 0.4f;
                lives = 4;
                scoreInc = 50;
                creditsInc = 4;
                break;
            case 2:
                spacecraftSpeed = 450;
                laserSpeed = 200;
                alienSpeed = 150;
                spawnInterval = 0.7f;
                laserCooldown = 0.3f;
                lives = 3;
                scoreInc = 200;
                creditsInc = 7;
                break;
            case 3:
                spacecraftSpeed = 500;
                laserSpeed = 250;
                alienSpeed = 220;
                spawnInterval = 0.6f;
                laserCooldown = 0.2f;
                lives = 2;
                scoreInc = 200;
                creditsInc = 10;
                break;
        }

        spacecraftSpeed += Lobby.selectedSp.getSpSpeed();
        laserSpeed += Lobby.selectedSp.getLaserSpeed();
    }

    // metodo per controllare l'input
    private void handleInput(float delta) {
        laserCooldownTimer += delta;

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
            laserCooldownTimer = 0;
        }
    }

    // metodo per generare il laser
    private void spawnLaser() {
        Rectangle laser = laserPool.obtain();
        laser.set(spaceship.x + spaceship.width / 2 - 8, spaceship.y + spaceship.height, 16, 40);
        lasers.add(laser);
    }

    // metodo per muovere i laser sparati
    private void updateLasers(float delta) {
        for (Iterator<Rectangle> iterator = lasers.iterator(); iterator.hasNext();) {
            Rectangle laser = iterator.next();
            laser.y += laserSpeed * delta;
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

        // spawn alieni di colore casuale
        if (spawnTimer >= spawnInterval) {
            Alien alien = new Alien(
                alienTextures[MathUtils.random(5)],
                new Rectangle(MathUtils.random(0, Gdx.graphics.getWidth() - 40), Gdx.graphics.getHeight(), 40, 40)
            );
            // aggiunta alieno creato all'arraylist
            aliens.add(alien);
            spawnTimer = 0;
        }

        // iterazione per muovere gli alieni
        for (Iterator<Alien> iterator = aliens.iterator(); iterator.hasNext();) {
            Alien alien = iterator.next();
            alien.getAlienRect().y -= alienSpeed * delta;
            if (alien.getAlienRect().y < 0) {
                iterator.remove();
                lives-=1;
            }
        }
    }

    // metodo per il controllo delle collisioni
    private void checkCollisions() {
        // Inizializza o pulisci il QuadTree
        QuadTree quadTree = new QuadTree(0, new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        quadTree.clear();

        // Popola il QuadTree con i rettangoli degli alieni
        for (Alien alien : aliens) {
            quadTree.insert(alien.getAlienRect());
        }

        // Controlla le collisioni per ogni laser
        Iterator<Rectangle> laserIterator = lasers.iterator();
        while (laserIterator.hasNext()) {
            Rectangle laser = laserIterator.next();

            // Ottieni i potenziali rettangoli in collisione
            Array<Rectangle> potentialCollisions = new Array<>();
            quadTree.retrieve(potentialCollisions, laser);

            for (Rectangle alienRect : potentialCollisions) {
                if (laser.overlaps(alienRect)) {
                    // Rimuovi il laser
                    laserIterator.remove();
                    laserPool.free(laser);

                    // Rimuovi l'alieno
                    for (Iterator<Alien> alienIterator = aliens.iterator(); alienIterator.hasNext();) {
                        Alien alien = alienIterator.next();
                        if (alien.getAlienRect() == alienRect) {
                            alienIterator.remove();
                            break;
                        }
                    }

                    // Aggiorna statistiche
                    Lobby.points += scoreInc;
                    aliensHit++;
                    if (aliensHit % 5 == 0) {
                        Lobby.credits += creditsInc;
                    }

                    // Avvia l'animazione di collisione
                    activeAnimations.add(new CollisionAnimation(
                        alienRect.x, alienRect.y - 5, collisionFrames));

                    break; // Esci dal ciclo dei rettangoli vicini
                }
            }
        }
    }

    private void createBlurredBackground() {
        Pixmap pixmap = new Pixmap(300, 100, Pixmap.Format.RGBA8888);
        pixmap.setColor(255, 255, 255, 0.5f); // semi-trasparente
        pixmap.fillRectangle(0, 0, 300, 50);
        blurTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private void renderBlurredBackground() {
        screen.draw(blurTexture, 0, Gdx.graphics.getHeight() - 120);
    }


    // metodo per la stampa a monitor di tutte le grafiche aggiornate
    private void renderGame() {

        ScreenUtils.clear(0, 0, 0, 1);
        screen.begin();

        // Stampa sfondo
        screen.draw(backgroundTexture, 0, backgroundY1, Gdx.graphics.getWidth(), backgroundTexture.getHeight());
        screen.draw(backgroundTexture, 0, backgroundY2, Gdx.graphics.getWidth(), backgroundTexture.getHeight());

        // Stampa navicella
        screen.draw(spaceshipTexture, spaceship.x, spaceship.y);

        // Stampa laser
        for (Rectangle laser : lasers) {
            screen.draw(Lobby.selectedSp.getLaserTexture(), laser.x, laser.y);
        }

        // Stampa alieni
        for (Alien alien : aliens) {
            screen.draw(alien.getImg(), alien.getAlienRect().x, alien.getAlienRect().y);
        }

        // Stampa animazioni di collisione
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

        renderBlurredBackground();

        // stampa vite rimanenti
        switch(Lobby.getDiffCG()) {
            case 1:
                switch(lives) {
                    case 1:
                        screen.draw(life4, 20, 600, 50, 90);
                        break;
                    case 2:
                        screen.draw(life3, 20, 600, 50, 90);
                        break;
                    case 3:
                        screen.draw(life2, 20, 600, 50, 90);
                        break;
                    case 4:
                        screen.draw(life1, 20, 600, 50, 90);
                        break;
                }
                break;
            case 2:
                switch(lives) {
                    case 1:
                        screen.draw(life4, 20, 600, 50, 90);
                        break;
                    case 2:
                        screen.draw(life3, 20, 600, 50, 90);
                        break;
                    case 3:
                        screen.draw(life1, 20, 600, 50, 90);
                        break;
                }
                break;
            case 3:
                switch(lives) {
                    case 1:
                        screen.draw(life3, 20, 600, 50, 90);
                        break;
                    case 2:
                        screen.draw(life1, 20, 600, 50, 90);
                        break;
                }
                break;

        }

        // Stampa statistiche
        font.draw(screen, "Credits: " + Lobby.credits, 10, Gdx.graphics.getHeight() - 30);
        font.draw(screen, "Score: " + Lobby.points, 10, Gdx.graphics.getHeight() - 50);
        font.draw(screen, "Aliens Hit: " + aliensHit, 10, Gdx.graphics.getHeight() - 70);

        screen.end();
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


    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        handleInput(delta);
        updateBackground(delta);
        updateLasers(delta);
        updateAliens(delta);
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
    public void hide() {}

    @Override
    // rilascio risorse
    public void dispose() {
        screen.dispose();
        spaceshipTexture.dispose();
        backgroundTexture.dispose();
        for (Texture texture : alienTextures) {
            texture.dispose();
        }
        font.dispose();
    }
}
