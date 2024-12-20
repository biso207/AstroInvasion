package com.biga.astroinvasion;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.*;

import java.util.ArrayList;

public class ClassicGame implements Screen {
    private final Main game; // variabile di riferimento tipo gioco
    // dichiarazione screen
    private final SpriteBatch screen;
    private final Texture spaceshipTexture;
    private final Texture laserTexture;
    private Texture alienTexture;
    private final Rectangle spaceship, laser;
    private final ArrayList<Rectangle> aliens;
    private final ArrayList<Rectangle> lasers;

    // velocità di movimento oggetti
    private int spacecraftSpeed = 0; // navicella
    private int laserSpeed = 0; // laser
    private int alienSpeed = 0; // alieni

    // tempi per il laser
    private float laserCooldownTimer = 0; // timer per il cooldown dello sparo
    private float laserCooldown = 0.3f; // tempo minimo tra due spari

    // bottini in gioco
    private int lifes = 0; // vite in gioco
    private int incPoints = 0; // incremento punti
    private int incCredits = 0; // incremento crediti
    private int bonusPoints = 0; // bonus di punti

    // controllo per lo spawn del nuovo alieno
    private boolean laserFired = false; // spawn alieni

    private float spawnTimer = 0; // timer per lo spawn di nuovi alieni
    private double spawnInterval = 0; // tempo di spawn del singolo alieno
    private final String[] alienTextures; // array per le immagini degli alieni

    // costruttore
    ClassicGame(Main game) {
        this.game = game;
        // init dello screen
        this.screen = game.screen;

        // navicella utente
        Spacecraft spacecraft = Lobby.selectedSp;
        // navicella (immagine)
        spaceshipTexture = new Texture(spacecraft.getPathImg());
        // laser navicella (immagine)
        laserTexture = new Texture(spacecraft.getPathLaser());

        // array per le immagini degli alieni
        alienTextures = new String[]{"images/aliens/Alien_Blue.png", "images/aliens/Alien_Green.png", "images/aliens/Alien_Pink.png",
        "images/aliens/Alien_Red.png", "images/aliens/Alien_White.png", "images/aliens/Alien_Yellow.png"};

        // stampa iniziale della navicella
        spaceship = new Rectangle(400, 20, 64, 64);
        // init del laser (invisibile)
        laser = new Rectangle(0, 0, 16, 32);
        // init arraylist per gli alieni
        aliens = new ArrayList<>();
        // init arraylist per i laser
        lasers = new ArrayList<>();

        // difficoltà in gioco
        int d = Lobby.getDiffCG();
        // valori di base di movimento in base alla difficoltà
        switch (d) {
            case 1:
                spacecraftSpeed = 12;
                laserSpeed = 7;
                alienSpeed = 3;
                spawnInterval = 0.7;
                lifes = 4;
                incPoints = 50;
                incCredits = 4;
                break;
            case 2:
                spacecraftSpeed = 14;
                laserSpeed = 8;
                alienSpeed = 4;
                spawnInterval = 0.6;
                lifes = 3;
                incPoints = 100;
                incCredits = 7;
                break;
            case 3:
                spacecraftSpeed = 16;
                laserSpeed = 10;
                alienSpeed = 4;
                spawnInterval = 0.5;
                lifes = 2;
                incPoints = 200;
                incCredits = 10;
                break;
        }

        // moltiplicazione di 100 per i movimenti così da renderli più fluidi, naturali e veloci
        spacecraftSpeed*=100;
        laserSpeed*=100;
        alienSpeed*=100;

        // incremento valori di movimento in base alla navicella scelta
        spacecraftSpeed += spacecraft.getSpSpeed(); // velocità navicella
        laserSpeed += spacecraft.getLaserSpeed(); // velocità laser
        bonusPoints += spacecraft.getBonusPoint(); // bonus punti
    }

    // metodo per controllare gli input utente (movimento navicella + sparo laser)
    private void handleInput(float delta) {
        // movimento navicella verso sx
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            spaceship.x -= spacecraftSpeed * delta;
        }

        // movimento navicella verso dx
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            spaceship.x += spacecraftSpeed * delta;
        }

        // sparo del laser
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && laserCooldownTimer >= laserCooldown) {
            // Aggiungi un nuovo laser
            Rectangle laser = new Rectangle(spaceship.x + spaceship.width / 2 - 8, spaceship.y + spaceship.height, 16, 32);
            lasers.add(laser);
            laserCooldownTimer = 0; // Resetta il timer
        }

        // movimento laser
        if (laserFired) {
            laser.y += laserSpeed * delta;
            if (laser.y > 800) {
                laserFired = false; // reset laser quando esce dallo schermo
            }
        }
    }

    // metodo per la stampa del laser
    private void updateLasers(float delta) {
        // Muove i laser verso l'alto
        for (Rectangle laser : lasers) {
            laser.y += laserSpeed * delta;
        }
        // Rimuove i laser fuori dallo schermo
        lasers.removeIf(laser -> laser.y > 800);
    }

    // metodo per la stampa degli alieni
    private void updateAliens(float delta) {
        Rectangle a = new Rectangle((float) Math.random() * 736, 800, 64, 64);
        spawnTimer += delta;

        // stampa alieno
        if (spawnTimer >= spawnInterval) {
            // spawn alieno in x casuale
            a = new Rectangle((float) Math.random() * 736, 800, 64, 64);

            // aggiunta alieno all'array
            aliens.add(a);

            // azzeramento tempo di spawn per l'alieno successivo
            spawnTimer = 0;
        }

        // movimento degli alieni
        for (Rectangle alien : aliens) {
            alien.y -= alienSpeed * delta;
        }

        // rimozione alieni che superano la fine dello schermo
        Rectangle finalA = a;
        aliens.removeIf(alien -> finalA.y < 0);
    }

    // metodo per controllare le collisioni (navicella-alieno)
    private void checkCollisions() {
        // arraylist per i laser e alieni da rimuovere
        ArrayList<Rectangle> lasersToRemove = new ArrayList<>();
        ArrayList<Rectangle> aliensToRemove = new ArrayList<>();

        // doppio for per controllare la collisione di un determinato laser con uno specifico alieno
        for (Rectangle laser : lasers) {
            for (Rectangle alien : aliens) {
                if (laser.overlaps(alien)) {
                    lasersToRemove.add(laser); // laser da rimuovere
                    aliensToRemove.add(alien); // alieno da rimuovere
                    break; // uscita ciclo alieni per il singolo laser
                }
            }
        }

        // rimozione laser e alieni che si sono colpiti
        lasers.removeAll(lasersToRemove);
        aliens.removeAll(aliensToRemove);
    }

    // metodo per l'aggiornamento della schermata
    private void renderGame() {
        // sfondo nero per la pulizia
        ScreenUtils.clear(0, 0, 0, 1);
        screen.begin();

        // stampa immagine di sfondo
        screen.draw(new Texture("images/bgInGame.png"), 0, 0);

        // stampa dinamica della navicella
        screen.draw(spaceshipTexture, spaceship.x, spaceship.y, 99, 66);

        // stampa laser allo sparo
        if (laserFired) {
            // stampa del laser sparato
            screen.draw(laserTexture, laser.x, laser.y);
        }

        // immagine alieno casuale
        int numTexture = (int) (Math.random() * 6);
        // stampa degli alieni generati
        for (Rectangle alien : aliens) {
            // texture dell'immagine alieno
            alienTexture = new Texture(alienTextures[numTexture]);

            // disegno dall'alieno a schermo
            screen.draw(alienTexture, alien.x, alien.y, alien.width, alien.height);
        }
        screen.end();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        updateAliens(delta);
        updateLasers(delta);
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
    public void dispose() {
        screen.dispose();
        spaceshipTexture.dispose();
        laserTexture.dispose();
        alienTexture.dispose();
    }
}

