package com.biga.astroinvasion;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.Pool;

import java.util.Iterator;

public class ClassicGame implements Screen {
    private final Main game;
    private final SpriteBatch screen;
    private final Texture spaceshipTexture, backgroundTexture;
    private final Texture[] alienTextures;
    private final Rectangle spaceship;
    private float backgroundY1, backgroundY2;
    private final Array<Rectangle> lasers = new Array<>();
    private final Array<Alien> aliens = new Array<>();
    private final Pool<Rectangle> laserPool;

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

    // dichiarazione font
    private BitmapFont font;
    private BitmapFont fontWhite20;

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

        // init alieni colpiti
        aliensHit = 0;

        // caricamento font
        loadFont();

        // musica di sottofondo
        soundtrack = Gdx.audio.newMusic(Gdx.files.internal("sounds/AstroInvasion_main_soundtrack.mp3")); // file audio
        soundtrack.setLooping(true); // true=loop music; false=no loop
        soundtrack.play(); // avvio musica
    }

    // -------------------- //
    // GRAFICA DELLA CLASSE //
    // -------------------- //

    // caricamento e creazione font per le scritte
    private void loadFont() {
        // dichiarazione font
        try {
            fontWhite20 = new BitmapFont(Gdx.files.internal("font/inter/regular_white_20.fnt")); // inter regular white 20
        } catch (Exception e) {
            font = new BitmapFont(); // font di default (arial)
            font.setColor(Color.valueOf("#151A3B")); // colore blu
        }
    }

    // metodo per modificare gli attributi navicella/alieni in base alla difficoltà scelta
    private void setupGameParameters(int difficulty) {
        switch (difficulty) {
            case 1:
                spacecraftSpeed = 1000;
                laserSpeed = 400;
                alienSpeed = 200;
                spawnInterval = 0.7f;
                laserCooldown = 0.4f;
                lives = 4;
                scoreInc = 50;
                creditsInc = 4;
                break;
            case 2:
                spacecraftSpeed = 1000;
                laserSpeed = 400;
                alienSpeed = 200;
                spawnInterval = 0.7f;
                laserCooldown = 0.3f;
                lives = 3;
                scoreInc = 200;
                creditsInc = 7;
                break;
            case 3:
                spacecraftSpeed = 700;
                laserSpeed = 300;
                alienSpeed = 200;
                spawnInterval = 0.7f;
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
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && spaceship.x > 10) {
            spaceship.x -= spacecraftSpeed * delta;
        }

        // movimento vs dx
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && spaceship.x < 890) {
            spaceship.x += spacecraftSpeed * delta;
        }

        // sparo del laser
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && laserCooldownTimer >= laserCooldown) {
            spawnLaser();
            laserCooldownTimer = 0;
        }
    }

    // metodo per generare il laser
    private void spawnLaser() {
        Rectangle laser = laserPool.obtain();
        laser.set(spaceship.x + spaceship.width / 2 - 8, spaceship.y + spaceship.height, 16, 32);
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
        backgroundY1 -= 100 * delta;
        backgroundY2 -= 100 * delta;

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

        if (spawnTimer >= spawnInterval) {
            Alien alien = new Alien(
                alienTextures[MathUtils.random(alienTextures.length - 1)],
                new Rectangle(MathUtils.random(0, Gdx.graphics.getWidth() - 100), Gdx.graphics.getHeight(), 100, 100)
            );
            aliens.add(alien);
            spawnTimer = 0;
        }

        for (Iterator<Alien> iterator = aliens.iterator(); iterator.hasNext();) {
            Alien alien = iterator.next();
            alien.getAlienRect().y -= alienSpeed * delta;
            if (alien.getAlienRect().y < 0) {
                iterator.remove();
            }
        }
    }

    // metodo per il controllo delle collisioni
    private void checkCollisions() {
        Array<Rectangle> lasersToRemove = new Array<>();
        Array<Alien> aliensToRemove = new Array<>();

        for (Rectangle laser : lasers) {
            for (Alien alien : aliens) {
                if (laser.overlaps(alien.getAlienRect())) {
                    lasersToRemove.add(laser);
                    aliensToRemove.add(alien);

                    // Update stats
                    Lobby.points += scoreInc; // incremento punteggio ogni alieno colpito
                    aliensHit++; // incremento alieni colpiti

                    if (aliensHit%5==0) Lobby.credits += creditsInc; // incremento crediti ogni 5 alieni colpiti
                    break;
                }
            }
        }

        lasers.removeAll(lasersToRemove, true);
        aliens.removeAll(aliensToRemove, true);

        for (Rectangle laser : lasersToRemove) {
            laserPool.free(laser);
        }
    }

    // metodo per la stampa a monitor di tutte le grafiche aggiornate
    private void renderGame() {
        ScreenUtils.clear(0, 0, 0, 1);
        screen.begin();

        // stampa sfondo dinamico
        screen.draw(backgroundTexture, 0, backgroundY1, Gdx.graphics.getWidth(), backgroundTexture.getHeight());
        screen.draw(backgroundTexture, 0, backgroundY2, Gdx.graphics.getWidth(), backgroundTexture.getHeight());

        // stampa della navicella
        screen.draw(spaceshipTexture, spaceship.x, spaceship.y);

        // stampa laser sparati
        for (Rectangle laser : lasers) {
            screen.draw(Lobby.selectedSp.getLaserTexture(), laser.x, laser.y);
        }

        // stampa alieni
        for (Alien alien : aliens) {
            screen.draw(alien.getImg(), alien.getAlienRect().x, alien.getAlienRect().y);
        }

        // scritta statistiche
        fontWhite20.draw(screen, "Lives: " + lives, 10, Gdx.graphics.getHeight() - 10);
        fontWhite20.draw(screen, "Credits: " + Lobby.credits, 10, Gdx.graphics.getHeight() - 30);
        fontWhite20.draw(screen, "Score: " + Lobby.points, 10, Gdx.graphics.getHeight() - 50);
        fontWhite20.draw(screen, "Aliens Hit: " + aliensHit, 10, Gdx.graphics.getHeight() - 70);

        screen.end();
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
