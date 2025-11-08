package sorgente.GameMods;

// import codici e librerie
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import sorgente.*;
import sorgente.Entities.Spacecraft;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import sorgente.Lobby.InputManager;
import sorgente.UserData.DataUserManager;

import java.util.*;


public class SpaceBattle implements Screen, InputProcessor, ResourceLoader {
    private final Main game;
    private final SpriteBatch screen;
    private Texture backgroundTexture, playerTexture;
    private TextureRegion enemyTexture, laserTexture;
    private Array<Texture> enemyBaseTextures = new Array<>();
    private Array<Texture> laserBaseTextures = new Array<>();
    private final Rectangle playerShip, enemyShip;
    private final Array<Rectangle> playerLasers = new Array<>();
    private final Array<Rectangle> enemyLasers = new Array<>();
    private final Pool<Rectangle> laserPool;

    private float backgroundY1, backgroundY2;
    private float playerSpeed, laserSpeed, enemySpeed;
    private Array<TextureRegion> laserTextures;
    private Array<TextureRegion> enemyTextures;
    private final Map<Integer, TextureRegion> enemyTextureJourney = new HashMap<>();
    private final Map<Integer, TextureRegion> laserTextureJourney = new HashMap<>();
    private float playerCooldown = 0, enemyCooldown = 0;
    private float playerLaserCooldown, enemyLaserCooldown;

    private int playerLives, enemyLives;
    private boolean isPaused = false, quit = false, gameClosed = false;

    private BitmapFont font, fontBoldWhite60, fontBoldWhite20;
    private Texture topBar, stopImg, playImg, quitMatch, btnHoverR, btnHoverL;

    private Texture superLaserImg;
    private boolean superLaser=false, goldHeart=false;

    private String enemyPicture;

    // stato sparo del laser
    private boolean shootPressed = false;
    // movimento in gioco
    public int moveLeftKey, moveRightKey, shotType;

    private final Spacecraft selectedSp;
    // stato cambio stile mouse
    private boolean isBtnRHover=false, isBtnLHover=false;

    private float enemyDirection = 1;

    private int totalLives = 4;
    private int totalEnemyLives = 4;

    // istanza del soundManager per riprodurre i suoni
    private final SoundManager soundManager;

    private Texture gameOver2;

    // variabile controllo se il gioco è un livello
    private final boolean isLevel;

    Texture life1;
    Texture life2;
    Texture life3;
    Texture life4;
    Texture life5;
    Texture life6;
    Texture goldHeartImg;

    Texture[][] livesTextures;

    private int hit_level = 0;

    private int hits_level = 0;

    public int level = (int) DataUserManager.getProgress("level");

    public SpaceBattle(Main game, boolean isLevel) {

        this.game = game;
        this.screen = game.screen;
        this.isLevel = isLevel;

        // istanza del soundManager per riprodurre i suoni
        soundManager = new SoundManager(InputManager.soundPercent);

        // selezione navicella utente
        // navicella utente inizializzata
        int id = (int) DataUserManager.getProgress("spacecraft_SB");
        this.selectedSp = new Spacecraft(id);

        playerTexture = selectedSp.getImgTexture();

        playerShip = new Rectangle(400, 20, 70, 64);
        enemyShip = new Rectangle(400, 520, 70, 64);


        // setting difficoltà di gioco
        int difficulty;

        if (isLevel && level <= 10 && level >= 0){
            difficulty = 1;
            setupGameParameters(difficulty);
        }
        if (isLevel && level <= 20  && level >= 11){
            difficulty = 2;
            setupGameParameters(difficulty);
        }
        if (isLevel && level <= 30  && level >= 21){
            difficulty = 2;
            setupGameParameters(difficulty);
        }
        if (isLevel && level <= 40  && level >= 31){
            difficulty = 3;
            setupGameParameters(difficulty);
        }
        if (!isLevel){
            difficulty = (int) DataUserManager.getProgress("diff_space_battle");
            setupGameParameters(difficulty);
        }

        laserPool = new Pool<>() {
            @Override
            protected Rectangle newObject() {
                return new Rectangle();
            }
        };


        enemyTextures = new Array<TextureRegion>();

        Random random = new Random();
        int selection = random.nextInt(20) + 1;

        // caricamento risorse di gioco
        loadImages();
        loadFont();
        loadEnemyTextures();

        if(!isLevel){
            enemyPicture = enemySpacecraft(selection);
            enemyTexture = enemyTextures.get(selection);
            laserTexture = laserTextures.get(selection);
        }
        else{
            enemyPicture = enemySpacecraftlevel(level);
            enemyTexture = enemyTextureJourney.get(level);
            laserTexture = laserTextureJourney.get(level);
        }

        // attivazione carte utente
        if (selectedSp.getName().equals("Rorik")) superLaser = true;
        if (selectedSp.getName().equals("Alpha")) goldHeart = true;

        goldHeart = selectedSp.getName().equals("Alpha") || InputManager.goldHeart;
        superLaser = selectedSp.getName().equals("Rorik") || InputManager.superLaser;

        // recupero stato attivazione carta speciale dall'InputManager della Lobby
        if (InputManager.goldHeart) goldHeart = true;
        if (InputManager.superLaser) superLaser = true;

        // disattivazione generica in caso si stia giocando un livello
        if (isLevel && level < 30) goldHeart=superLaser=false;
        if (level > 30 && isLevel) superLaser = true;

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
        if (((int)DataUserManager.getProgress("shot_type")) == 1) shotType = 1;
        else shotType = 2;

        hits_level = level/2;
    }

    //Valori di inizio gioco
    private void setupGameParameters(int difficulty) {
        switch (difficulty){
            case 1:
                playerSpeed = 200 + selectedSp.getSpSpeed() * 100;
                //laserSpeed = 200 + selectedSp.getLaserSpeed() * 100;
                laserSpeed = 300;
                enemySpeed = 400;
                playerLaserCooldown = 0.3f;
                enemyLaserCooldown = 0.3f;
                totalLives = 4;
                totalEnemyLives = 4;
                playerLives = totalLives;
                enemyLives = totalEnemyLives;
                break;
            case 2:
                playerSpeed = 250 + selectedSp.getSpSpeed() * 100;
                //laserSpeed = 250 + selectedSp.getLaserSpeed() * 100;
                laserSpeed = 350;
                enemySpeed = 500;
                playerLaserCooldown = 0.3f;
                enemyLaserCooldown = 0.25f;
                totalLives = 3;
                totalEnemyLives = 4;
                playerLives = totalLives;
                enemyLives = totalEnemyLives;
                break;
            case 3:
                playerSpeed = 300 + selectedSp.getSpSpeed() * 100;
                //laserSpeed = 300 + selectedSp.getLaserSpeed() * 100;
                laserSpeed = 400 + selectedSp.getLaserSpeed() * 100;
                enemySpeed = 600;
                playerLaserCooldown = 0.3f;
                enemyLaserCooldown = 0.2f;
                totalLives = isLevel ? 3:2;
                totalEnemyLives = 4;
                playerLives = totalLives;
                enemyLives = totalEnemyLives;
                break;
        }
    }

    //Laser
    private void spawnLaser(Rectangle origin, Array<Rectangle> targetArray) {
        Rectangle laser = laserPool.obtain();
        //if (superLaser) screen.draw(superLaserImg, laser.x, laser.y);
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
                it.remove();
                soundManager.playHit();

                if (!isLevel) {
                    enemyLives--;
                    if (enemyLives <= 0) {
                        gameOver(true);
                    }
                } else {
                    hit_level++;
                    if (hit_level >= hits_level) {
                        gameOver(true);
                    }
                }

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
                soundManager.playHit();
                if ((playerLives <= 0 && !goldHeart) || (goldHeart && playerLives == -1)){
                    gameOver(false);
                }
            } else if (laser.y < 0) {
                it.remove();
                laserPool.free(laser);
            }
        }

        for (Iterator<Rectangle> it1 = playerLasers.iterator(); it1.hasNext(); ) {
            Rectangle pLaser = it1.next();
            for (Iterator<Rectangle> it2 = enemyLasers.iterator(); it2.hasNext(); ) {
                Rectangle eLaser = it2.next();
                if (pLaser.overlaps(eLaser)) {
                    if (!superLaser) it1.remove();
                    it2.remove();
                    if (!superLaser) laserPool.free(pLaser);
                    laserPool.free(eLaser);
                    break;
                }
            }
        }





    }

    @Override
    public void loadImages(){
        //Texture cuori
        life1 = new Texture("images/lives/heart 100%.png");
        life2 = new Texture("images/lives/heart 75%.png");
        life3 = new Texture("images/lives/heart 66%.png");
        life4 = new Texture("images/lives/heart 50%.png");
        life5 = new Texture("images/lives/heart 33%.png");
        life6 = new Texture("images/lives/heart 25%.png");
        goldHeartImg = new Texture("images/lives/gold heart.png");


        livesTextures = new Texture[][]{
            {life4, life1}, // totalLives = 2
            {life5, life3, life1}, // totalLives = 3
            {life6, life4, life2, life1} // totalLives = 4
        };

        if (isLevel)topBar = new Texture("images/top_bar_space_battle_level.png");
        else topBar = new Texture("images/top_bar_space_battle.png");

        playImg = new Texture("images/play.png");
        stopImg = new Texture("images/stop.png");

        // quit match
        quitMatch = new Texture(Gdx.files.internal("lobby_screens/lobby (16).png"));
        // pulsanti hover
        btnHoverL = new Texture("images/btns_hover/hover_btn8.png");
        btnHoverR = new Texture("images/btns_hover/hover_btn9.png");

        backgroundTexture = new Texture("images/bgInGame.png");

        backgroundY1 = 0;
        backgroundY2 = backgroundTexture.getHeight();

        superLaserImg = new Texture("images/spacecrafts/_super_laser.png");

    }

    @Override
    public void loadFont(){
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        fontBoldWhite60 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_60_1.fnt")); // inter-bold white 60
        fontBoldWhite20 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_35.fnt")); // inter-bold white 35
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
            soundManager.playLaser();
            enemyCooldown = 0;
        }
    }


    private void loadEnemyTextures() {
        enemyTextures = new Array<>();
        laserTextures = new Array<>();


        if (!isLevel){
            for (int i = 1; i < 25; i++) {
                Texture texture = new Texture("images/spacecrafts/sp" + i + ".png");
                enemyBaseTextures.add(texture);
                TextureRegion flipped = new TextureRegion(texture);
                flipped.flip(false, true);
                enemyTextures.add(flipped);

            }

            for (int i = 0; i < 24; i++) {
                Texture texture = new Texture("images/lasers/laser (" + (i+1) + ").png");
                laserBaseTextures.add(texture);
                TextureRegion flipped = new TextureRegion(texture);
                flipped.flip(false, true);
                laserTextures.add(flipped);

            }
        }

        else {
            Set<Integer> flippedLevels = Set.of(2, 4, 6, 8, 12, 14, 16, 18, 22, 24, 26, 28, 32, 34, 36, 38);

            TextureRegion enemyRegion, laserRegion;
            int index = 5;
            for (int lvl = 1; lvl <= 40; lvl++) {
                if (flippedLevels.contains(lvl)) {
                    Texture enemyTexture = new Texture("images/spacecrafts/sp" + index + ".png");
                    enemyRegion = new TextureRegion(enemyTexture);

                    Texture laserTexture = new Texture("images/lasers/laser (" + index + ").png");
                    laserRegion = new TextureRegion(laserTexture);

                    enemyRegion.flip(false, true);
                    laserRegion.flip(false, true);

                    enemyTextureJourney.put(lvl, enemyRegion);
                    laserTextureJourney.put(lvl, laserRegion);

                    index++;
                }
            }



        }

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
                gameOver(false);
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
        if (Gdx.input.isKeyPressed(moveLeftKey) && playerShip.x > 10) {
            playerShip.x -= playerSpeed * delta;
        }
        // movimento vs dx
        if (Gdx.input.isKeyPressed(moveRightKey) && playerShip.x < 890) {
            playerShip.x += playerSpeed * delta;
        }

        if (shootPressed && playerCooldown >= playerLaserCooldown) {
            spawnLaser(playerShip, playerLasers);
            soundManager.playLaser();
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
        for (Rectangle laser : playerLasers) {
            screen.draw(selectedSp.getLaserTexture(), laser.x, laser.y);
            if (superLaser) screen.draw(superLaserImg, laser.x, laser.y);
        }
        for (Rectangle laser : enemyLasers)
            screen.draw(laserTexture, laser.x, laser.y);
        screen.draw(topBar, 20, 600);
        // stampa vite rimanenti
        if (totalLives >= 2 && totalLives <= 4 && playerLives >= 1 && playerLives <= totalLives) {
            screen.draw(livesTextures[totalLives - 2][playerLives - 1], 75, 640);
        }
        // stampa cuore d'oro se attivato
        if (goldHeart && playerLives == 0 && !isLevel) screen.draw(goldHeartImg, 75, 640);

        // stampa vite rimanenti
        if (!isLevel) {
            if (totalEnemyLives >= 2 && totalEnemyLives <= 4 && enemyLives >= 1 && enemyLives <= totalEnemyLives) {
                screen.draw(livesTextures[totalEnemyLives - 2][enemyLives - 1], 873, 640);
            }
        }
        else if (isLevel){
            fontBoldWhite20.draw(screen, hit_level + "/" + hits_level, 825, 671);

        }

        // stampa nome utente
        fontBoldWhite20.draw(screen, selectedSp.getName(), 155, 671);

        //Stampa nome nemico
        if (!isLevel)fontBoldWhite20.draw(screen, enemyPicture, 605, 671);
        if (isLevel)fontBoldWhite20.draw(screen, enemyPicture, 560, 671);

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

        screen.end();
    }

    // metodo per constatare vittoria/sconfitta
    private void gameOver(boolean win) {

        if (!isLevel) {
            // diminuzione carte speciali
            if (goldHeart && !selectedSp.getName().equals("Alpha"))
                DataUserManager.setProgress("num_gold_heart", (int) DataUserManager.getProgress("num_gold_heart") - 1);
            if (superLaser && !selectedSp.getName().equals("Rorik"))
                DataUserManager.setProgress("num_super_laser", (int) DataUserManager.getProgress("num_super_laser") - 1);

            if (win) {
                DataUserManager.setProgress("won_SB", (int) DataUserManager.getProgress("won_SB") + 1);
                DataUserManager.setProgress("win_streak_SB", (int) DataUserManager.getProgress("win_streak_SB") + 1);
            }
            else {
                DataUserManager.setProgress("win_streak_SB", 0);
            }

            // aggiornamento partite giocate
            DataUserManager.setProgress("matches_SB", (int) DataUserManager.getProgress("matches_SB") + 1);
        }

        int[] stats = {0, 0, 0};
        game.setScreen(new GameOver(game, selectedSp, 1, stats, win, isLevel));
        this.dispose(); // rilascio risorse
    }

    private String enemySpacecraft(int i){
        i++;
        return switch (i) {
            case 1 -> "Omega";
            case 2 -> "Idra";
            case 3 -> "Woka";
            case 4 -> "Pegaso";
            case 5 -> "Ares";
            case 6 -> "Andvari";
            case 7 -> "Siko";
            case 8 -> "Fenixia";
            case 9 -> "Selen";
            case 10 -> "Centauro";
            case 11 -> "Zephyr";
            case 12 -> "Malloc";
            case 13 -> "Orion";
            case 14 -> "Asgard";
            case 15 -> "Galahad";
            case 16 -> "Seraphis";
            case 17 -> "Beowulf";
            case 18 -> "Scylla";
            case 19 -> "Keto";
            case 20 -> "Efron";
            case 21 -> "Drakar";
            case 22 -> "Rorik";
            case 23 -> "Astrid";
            case 24 -> "Alpha";
            default -> " ";
        };


    }

    private String enemySpacecraftlevel(int i){
        return switch (i) {
            case 2 -> "Ares";
            case 4 -> "Andvari";
            case 6 -> "Siko";
            case 8 -> "Fenixia";
            case 12 -> "Selen";
            case 14 -> "Centauro";
            case 16 -> "Zephyr";
            case 18 -> "Malloc";
            case 22 -> "Orion";
            case 24 -> "Asgard";
            case 26 -> "Galahad";
            case 28 -> "Seraphis";
            case 32 -> "Beowulf";
            case 34 -> "Scylla";
            case 36 -> "Keto";
            case 38 -> "Efron";
            default -> " ";
        };
    }


    @Override public void render(float delta) {
        Gdx.input.setInputProcessor(this); // attiva l'ascolto degli input per l'interfaccia InputProcessor
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
        playerTexture.dispose();
        backgroundTexture.dispose();
        font.dispose();
        for (Texture t : enemyBaseTextures) {
            t.dispose();
        }
        for (Texture t : laserBaseTextures) {
            t.dispose();
        }
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //
    // TASTIERA
    // metodo per ascoltare il click della tastiera
    @Override public boolean keyDown(int keycode) {
        // click barra spaziatrice per sparare
        if (keycode == Input.Keys.SPACE && shotType==2) shootPressed = true;
        return true;
    }
    // metodo per ascoltare il rilascio di una tasto della tastiera
    @Override public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.SPACE && shotType==2) shootPressed = false;
        return true;
    }

    // MOUSE //
    // metodo per ascoltare il click del mouse
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && shotType==1) shootPressed = true;
        return true;
    }
    // metodo per ascoltare il rilascio del mouse
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) shootPressed = false;
        else if (button == Input.Keys.SPACE) shootPressed = false;
        return true;
    }
    // metodo per cambiare lo stile dei pulsanti al passaggio del mouse sopra di essi
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        isBtnRHover=isBtnLHover=false;
        // YES quit
        if (isPaused && (screenX >= 269 && screenX <= 484) && (screenY >= 403 && screenY <= 475)) isBtnLHover=true;

        // NO quit
        if ((screenX >= 510 && screenX <= 715) && (screenY >= 403 && screenY <= 475)) isBtnRHover=true;
        return true;
    }

    @Override public boolean keyTyped(char character) { return true; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
