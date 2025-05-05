package sorgente.GameMods;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ScreenUtils;
import sorgente.Entities.Spacecraft;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import sorgente.DataUserManager;
import sorgente.Main;
import sorgente.Lobby.InputManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

public class SpaceBattle implements Screen, InputProcessor {
    private final Main game;
    private final SpriteBatch screen;
    private final Texture backgroundTexture, enemyTexture, playerTexture, laserTexture;
    private final Rectangle playerShip, enemyShip;
    private final Array<Rectangle> playerLasers = new Array<>();
    private final Array<Rectangle> enemyLasers = new Array<>();
    private final Pool<Rectangle> laserPool;

    private float backgroundY1, backgroundY2;
    private float playerSpeed, laserSpeed, enemySpeed;
    private Array<Texture> laserTextures;
    private Array<Texture> enemyTextures;
    private float playerCooldown = 0, enemyCooldown = 0;
    private float playerLaserCooldown, enemyLaserCooldown;

    private int playerLives, enemyLives;
    private boolean isPaused = false, quit = false, gameClosed = false;

    private final Music soundtrack;
    private final Sound shotSound, hitSound;
    private final BitmapFont font;
    private Texture topBar, stopImg, playImg;

    private final Spacecraft selectedSp;

    private float enemyDirection = 1;

    private int totalLives = 4;
    private int totalEnemyLives = 4;

    private Texture  gameOver1, gameOver2;

    //Texture cuori
    Texture life1 = new Texture("images/lives/heart 100%.png");
    Texture life2 = new Texture("images/lives/heart 75%.png");
    Texture life3 = new Texture("images/lives/heart 66%.png");
    Texture life4 = new Texture("images/lives/heart 50%.png");
    Texture life5 = new Texture("images/lives/heart 33%.png");
    Texture life6 = new Texture("images/lives/heart 25%.png");

    Texture[][] livesTextures = new Texture[][]{
        {life4, life1}, // totalLives = 2
        {life5, life3, life1}, // totalLives = 3
        {life6, life4, life2, life1} // totalLives = 4
    };

    public SpaceBattle(Main game, Spacecraft selectedSp) {
        this.game = game;
        this.screen = game.screen;
        this.selectedSp = selectedSp;

        backgroundTexture = new Texture("images/bgInGame.png");
        playerTexture = new Texture(selectedSp.getPathImg());

        playerShip = new Rectangle(400, 20, 70, 64);
        enemyShip = new Rectangle(400, 520, 70, 64);

        backgroundY1 = 0;
        backgroundY2 = backgroundTexture.getHeight();

        int difficulty = (int) DataUserManager.getProgress("diff_space_battle");
        setupGameParameters(difficulty);

        laserPool = new Pool<>() {
            @Override
            protected Rectangle newObject() {
                return new Rectangle();
            }
        };

        font = new BitmapFont();
        font.setColor(Color.WHITE);

        topBar = new Texture("images/top_bar_classic_game.png");

        playImg = new Texture("images/play.png");
        stopImg = new Texture("images/stop.png");

        soundtrack = Gdx.audio.newMusic(Gdx.files.internal("sounds/AstroInvasion_main_soundtrack.mp3"));
        soundtrack.setLooping(true);
        soundtrack.play();

        shotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shot_sound.mp3"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hit_sound.mp3"));

        Gdx.input.setInputProcessor(this);

        gameOver1 = new Texture(Gdx.files.internal("secondary_screens/game_over_sb_eng.png"));
        gameOver2 = new Texture(Gdx.files.internal("secondary_screens/victory_sb_eng.png"));

        enemyTextures = new Array<Texture>();

        Random random = new Random();
        int selection = random.nextInt(20) + 1;

        loadEnemyTextures();

        System.out.println(selection);
        System.out.println(laserTextures.get(selection));

        enemyTexture = enemyTextures.get(selection);
        laserTexture = laserTextures.get(selection);


    }

    //Valori di inizio gioco
    private void setupGameParameters(int difficulty) {
        switch (difficulty){
            case 1:
                playerSpeed = 200 + selectedSp.getSpSpeed() * 100;
                laserSpeed = 200 + selectedSp.getLaserSpeed() * 100;
                enemySpeed = 300;
                playerLaserCooldown = 0.3f;
                enemyLaserCooldown = 0.3f;
                totalLives = 4;
                totalEnemyLives = 4;
                playerLives = totalLives;
                enemyLives = totalEnemyLives;
                break;
            case 2:
                playerSpeed = 300 + selectedSp.getSpSpeed() * 100;
                laserSpeed = 200 + selectedSp.getLaserSpeed() * 100;
                enemySpeed = 400;
                playerLaserCooldown = 0.3f;
                enemyLaserCooldown = 0.3f;
                totalLives = 3;
                totalEnemyLives = 4;
                playerLives = totalLives;
                enemyLives = totalEnemyLives;
                break;
            case 3:
                playerSpeed = 400 + selectedSp.getSpSpeed() * 100;
                laserSpeed = 200 + selectedSp.getLaserSpeed() * 100;
                enemySpeed = 500;
                playerLaserCooldown = 0.3f;
                enemyLaserCooldown = 0.3f;
                totalLives = 2;
                totalEnemyLives = 4;
                playerLives = totalLives;
                enemyLives = totalEnemyLives;
                break;
        }
    }

    //Laser
    private void spawnLaser(Rectangle origin, Array<Rectangle> targetArray) {
        Rectangle laser = laserPool.obtain();
        laser.set(origin.x + origin.width / 2 - 5, origin.y + (origin == playerShip ? origin.height : -20), 10, 20);
        targetArray.add(laser);
    }

    //Aggiorna i laser
    private void updateLasers(float delta) {
        float step = laserSpeed * delta;
        for (Iterator<Rectangle> it = playerLasers.iterator(); it.hasNext(); ) {
            Rectangle laser = it.next();
            laser.y += step;
            if (laser.overlaps(enemyShip)) {
                enemyLives--;
                it.remove();
                hitSound.play();
                if (enemyLives <= 0) gameOver(true);
            } else if (laser.y > Gdx.graphics.getHeight()) {
                it.remove();
                laserPool.free(laser);
            }
        }
        for (Iterator<Rectangle> it = enemyLasers.iterator(); it.hasNext(); ) {
            Rectangle laser = it.next();
            laser.y -= step;
            if (laser.overlaps(playerShip)) {
                playerLives--;
                it.remove();
                hitSound.play();
                if (playerLives <= 0) gameOver(false);
            } else if (laser.y < 0) {
                it.remove();
                laserPool.free(laser);
            }
        }
    }


    //By chatGPT
    private void updateEnemyAI(float delta) {
        // Movimento automatico a zig-zag
        enemyShip.x += enemyDirection * enemySpeed * delta;

        // Inversione ai bordi
        if (enemyShip.x <= 10) {
            enemyShip.x = 10;
            enemyDirection = 1;
        } else if (enemyShip.x >= 890) {
            enemyShip.x = 890;
            enemyDirection = -1;
        }

        // Sparo a intervalli regolari
        enemyCooldown += delta;
        if (enemyCooldown >= enemyLaserCooldown) {
            spawnLaser(enemyShip, enemyLasers);
            shotSound.play();
            enemyCooldown = 0;
        }
    }


    private void loadEnemyTextures() {
        enemyTextures = new Array<>();
        laserTextures = new Array<>();
        for (int i = 1; i < 22; i++) enemyTextures.add(new Texture("images/spacecrafts/enemies/enemy" + i + ".png"));
        for (int i = 1; i < 22; i++) laserTextures.add(new Texture("images/spacecrafts/enemies/laser" + i + ".png"));
    }






    //Movimento sfondo
    private void updateBackground(float delta) {
        backgroundY1 -= 50 * delta;
        backgroundY2 -= 50 * delta;
        if (backgroundY1 + backgroundTexture.getHeight() <= 0)
            backgroundY1 = backgroundY2 + backgroundTexture.getHeight();
        if (backgroundY2 + backgroundTexture.getHeight() <= 0)
            backgroundY2 = backgroundY1 + backgroundTexture.getHeight();
    }

    private void handleInput(float delta) {
        playerCooldown += delta;

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
            if ((screenX >= 513 && screenX <= 713) && (screenY >= 405 && screenY <= 480)) {
                quit = !quit;
                isPaused = !isPaused;
            }

            // click YES => interruzione gioco
            if ((screenX >= 270 && screenX <= 470) && (screenY >= 405 && screenY <= 480)) {
                gameOver(true);
                gameClosed = true;
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
        if (Gdx.input.isKeyPressed(Input.Keys.A) && playerShip.x > 10) {
            playerShip.x -= playerSpeed * delta;
        }
        // movimento vs dx
        if (Gdx.input.isKeyPressed(Input.Keys.D) && playerShip.x < 890) {
            playerShip.x += playerSpeed * delta;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && playerCooldown >= playerLaserCooldown) {
            spawnLaser(playerShip, playerLasers);
            shotSound.play();
            playerCooldown = 0;
        }

    }

    private void renderGame() {
        ScreenUtils.clear(0, 0, 0, 1);
        screen.begin();
        screen.draw(backgroundTexture, 0, backgroundY1);
        screen.draw(backgroundTexture, 0, backgroundY2);
        screen.draw(playerTexture, playerShip.x, playerShip.y);
        screen.draw(enemyTexture, enemyShip.x, enemyShip.y);
        for (Rectangle laser : playerLasers)
            screen.draw(selectedSp.getLaserTexture(), laser.x, laser.y);
        for (Rectangle laser : enemyLasers)
            screen.draw(laserTexture, laser.x, laser.y);
        screen.draw(topBar, 20, 600);
        // stampa vite rimanenti
        if (totalLives >= 2 && totalLives <= 4 && playerLives >= 1 && playerLives <= totalLives) {
            screen.draw(livesTextures[totalLives - 2][playerLives - 1], 93, 640);
        }
        // stampa vite rimanenti
        if (totalEnemyLives >= 2 && totalEnemyLives <= 4 && enemyLives >= 1 && enemyLives <= totalEnemyLives) {
            screen.draw(livesTextures[totalEnemyLives - 2][enemyLives - 1], 700, 640);
        }
        // stampa icona pausa
        if (isPaused) screen.draw(stopImg, 472, 637);
        else screen.draw(playImg, 472, 637);
        screen.end();
    }

    // metodo per constatare vittoria/sconfitta
    private void gameOver(boolean win) {
        // diminuzione carte speciali
        //if (goldHeart && !selectedSp.getName().equals("Alpha")) DataUserManager.setProgress("num_gold_heart", (int) DataUserManager.getProgress("num_gold_heart")-1);
        //if (superLaser && !selectedSp.getName().equals("Rorik")) DataUserManager.setProgress("num_super_laser", (int) DataUserManager.getProgress("num_super_laser")-1);

        soundtrack.stop();
        game.setScreen(new GameOver(game, selectedSp, 1, 0, 0, 0));


    }


    @Override public void render(float delta) {
        if (!isPaused) {
            delta = Math.min(delta, 1 / 30f);
        } else delta = 0;

        handleInput(delta);
        updateBackground(delta);
        updateEnemyAI(delta);
        updateLasers(delta);
        renderGame();
    }

    @Override public void hide() { Gdx.input.setInputProcessor(null); }
    @Override public void dispose() {
        screen.dispose();
        playerTexture.dispose();
        enemyTexture.dispose();
        backgroundTexture.dispose();
        font.dispose();
        for (Texture t : enemyTextures) {
            t.dispose();
        }
        for (Texture t : laserTextures) {
            t.dispose();
        }
    }

    @Override public boolean keyTyped(char character) { return true; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
