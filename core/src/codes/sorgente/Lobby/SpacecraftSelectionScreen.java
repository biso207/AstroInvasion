/*
Astro Invasion - class SpacecraftSelectionScreen -
Gestisce grafica e input della pagina di selezione delle navicelle
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.Lobby;

// import librerie e codici
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import sorgente.DataUserManager;
import sorgente.Entities.Spacecraft;
import sorgente.Main;
import sorgente.ResourceLoader;
import java.util.*;

public class SpacecraftSelectionScreen implements Screen, InputProcessor, ResourceLoader {
    private final Main game;
    private final SpriteBatch screen;
    private Texture bg, selectionBox, scrollBar;
    private final List<SpacecraftData> spacecrafts;
    private final Map<Rectangle, Integer> clickableAreas;
    private int selectedId = -1; // id della navicella selezionata

    private float scrollY = 2300; // posizione iniziale dello scroll/pagina
    private final float maxScrollY = 2300; // Altezza massima della schermata scrollabile (immagine - altezza schermo)
    private final float scrollSpeed = 50f;  // Velocità dello scroll per rotellina
    private float dragStartY = -1;
    private boolean isDragging = false; // stato click prolungato del mouse

    // costruttore
    public SpacecraftSelectionScreen(Main game) {
        this.game = game;
        // init dello screen
        this.screen = game.screen;

        // caricamento dati navicelle
        spacecrafts = loadSpacecrafts();
        clickableAreas = new HashMap<>();

        // caricamento risorse grafiche
        loadImages();
        // caricamento font
        loadFont();
    }

    // ******************* //
    // CARICAMENTO RISORSE //
    // ******************* //

    // creazione grafica delle navicelle
    private List<SpacecraftData> loadSpacecrafts() {
        List<SpacecraftData> list = new ArrayList<>();

        // nomi delle navicelle
        String[] names = {"Omega", "Idra", "Pegaso", "Woka", "Beowulf", "Andvari", "Siko", "Fenixia", "Ares", "Asgard",
            "Galahad", "Malloc", "Orion", "Centauro", "Zephyr", "Phoenix", "Selen", "Scylla", "Keto", "Efron",
            "Drakar", "Rorik", "Astrid", "Alpha"};
        // missioni delle navicelle
        String[] missions = {"", "", "", "",
            "Complete Level 2", "Complete Level 5", "Complete Level 8", "Complete Level 10",
            "Complete Level 12", "Complete Level 15", "Complete Level 18", "Complete Level 20",
            "Complete Level 22", "Complete Level 25", "Complete Level 28", "Complete Level 30",
            "Complete Level 32", "Complete Level 35", "Complete Level 38", "Complete Level 40",
            "Win 100 SB", "Buy in the Market", "Buy in the Market", "Reach Task 100 in RTG"
        };
        // lore delle navicelle
        String[] lore = {"Inevitable End", "Shapeshifting Threat", "Legendary Flight", "Stellar rebel",
            "Ancestral Warrior", "Energy Thief", "Deadly Silence", "Blazing Rebirth",
            "Cosmic Rage", "Divine Fortress", "Invincible Purity", "Glitched Code",
            "Space Hunter", "Hybrid Fury", "Supersonic Wind", "Sacred Flame",
            "Lunar Light", "Shadow Tentacles", "Eternal Abyss", "Echo Of Time",
            "Stellar Longship", "Frost Dominator", "Rising star", "Absolute Origin"
        };

        // percorsi immagine navicelle
        String[] imagePaths = {"images/spacecrafts/_omega.png", "images/spacecrafts/_idra.png", "images/spacecrafts/_pegaso.png",
            "images/spacecrafts/_woka.png", "images/spacecrafts/_beowulf_basic.png", "images/spacecrafts/_andvari_basic.png",
            "images/spacecrafts/_siko_basic.png", "images/spacecrafts/_fenixia_basic.png", "images/spacecrafts/_ares_basic.png",
            "images/spacecrafts/_asgard_basic.png", "images/spacecrafts/_galahad_basic.png", "images/spacecrafts/_malloc_basic.png",
            "images/spacecrafts/_orion_basic.png", "images/spacecrafts/_centauro_basic.png", "images/spacecrafts/_zephyr_basic.png",
            "images/spacecrafts/_phoenix_basic.png", "images/spacecrafts/_selen_basic.png", "images/spacecrafts/_scylla_basic.png",
            "images/spacecrafts/_keto_basic.png", "images/spacecrafts/_efron_basic.png", "images/spacecrafts/_drakar.png",
            "images/spacecrafts/_rorik.png", "images/spacecrafts/_astrid.png", "images/spacecrafts/_alpha.png"};
        // potenze delle navicelle
        int[][] attributes = {
            {0, 1, 0}, {5, 0, 0}, {1, 0, 0}, {0, 1, 0}, {0, 2, 0}, {10, 0, 0}, {0, 0, 2}, {0, 0, 3}, {0, 3, 0},
            {15, 0, 0}, {0, 1, 1}, {10, 2, 0}, {0, 2, 1}, {20, 0, 0}, {0, 4, 1}, {0, 1, 2}, {0, 2, 2}, {30, 0, 0},
            {0, 1, 4}, {10, 0, 2}, {0, 5, 5}, {50, 5, 0}, {50, 0, 5}, {50, 5, 5}
        };

        // popolamento della mappa navicelle
        for (int i = 0; i < 24; i++) {
            list.add(new SpacecraftData((i+1), names[i], missions[i], lore[i], new Texture(imagePaths[i]), attributes[i][0], attributes[i][1], attributes[i][2]));
        }

        return list;
    }



    @Override
    public void loadImages() {
        bg = new Texture("lobby_screens/lobby (4).png");
        selectionBox = new Texture("images/rect_selected_SP.png");
        scrollBar = new Texture("images/scrollBar.png");
    }

    @Override
    public void loadFont() {
        System.out.println("ciaone dai font");
    }

    // **************** //
    // GESTIONE GRAFICA //
    // **************** //

    // metodo per disegnare le navicelle
    private void drawShips() {
        float x, y;
        int spacing = 410; // distanza tra due navicelle su una riga
        int spacing2 = 168; // distanza tra due navicelle in colonna nello stesso box

        // x e y della prima navicella del primo gruppo
        int startX = 120;
        int startY = 2580;

        for (int i = 0; i < 24; i++) {
            SpacecraftData ship = spacecrafts.get(i);
            int row = i % 2; // 12 righe
            int col = i / 2; // 2 navicelle per riga

            // incremento x e y dello stesso gruppo
            x = startX + row * spacing;
            y = startY - col * spacing2 - scrollY;

            System.out.println(i + ": " + x + " " + y + "\n");

            // disegno navicella
            screen.draw(ship.getImage(), x, y);

            // rettangolo cliccabile per selezionare una navicella
            Rectangle box = new Rectangle(x, y, 396, 150);
            clickableAreas.put(box, ship.id);

            // disegno rettangolo selezione navicella
            if (ship.id == selectedId) {
                screen.draw(selectionBox, x - 5, y - 5);
            }

            // setting distanza in Y
            if ((i+1)%4==0 || i%4==0) spacing2 = 230; // prime due del gruppo
            else spacing2 = 168; // passaggio alla riga successiva di uno stesso gruppo
        }
    }

    // ************************************** //
    // METODI DELL'INTERFACCIA InputProcessor //
    // ************************************** //

    // metodo per rilevare il click della tastiera
    public boolean keyDown(int keycode) {
        // click tasto 'esc' per tornare alla lobby
        if (keycode == Input.Keys.ESCAPE) {
            game.setScreen(new LobbyManager(game)); // back to lobby
            return true;
        }
        return false;
    }

    // metodo per controllare i click del mouse
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //System.out.println(screenX + " " + screenY);
        // chiusura pagina cliccando sulla X
        if ((screenX >= 900 && screenX <= 940) && (screenY >= 577 && screenY <= 617)) {
            game.setScreen(new LobbyManager(game)); // back to lobby
        }

        // avvio drugging del mouse in caso tenga cliccata la barra
        if (button == Input.Buttons.LEFT) {
            isDragging = true;
            dragStartY = screenY;
        }

        // controllo selezione navicella
        for (Map.Entry<Rectangle, Integer> entry : clickableAreas.entrySet()) {
            Rectangle area = entry.getKey();
            if (area.contains(screenX, screenY + scrollY)) {
                selectedId = entry.getValue(); // recupero id navicella selezionata
                // salvataggio navicella scelta
                DataUserManager.setProgress("spacecraft", selectedId);
                break;
            }
        }
        return true;
    }

    // cambio icona mouse
    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }

    // rilascio del mouse
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            isDragging = false;
        }
        return true;
    }

    // click continuato e movimento del mouse
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (isDragging) {
            float deltaY = dragStartY - screenY;
            scrollY += deltaY;
            clampScroll();
            dragStartY = screenY;
        }
        return true;
    }

    // controlla il margine di scorrimento del mouse
    private void clampScroll() {
        if (scrollY < 0) scrollY = 0;
        if (scrollY > maxScrollY) scrollY = maxScrollY;
    }

    // gestisce lo scorrimento del mouse sulla schermata
    @Override
    public boolean scrolled(float amountX, float amountY) {
        // diminuzione di Y => deve essere tra 2300 e 0 compresi
        scrollY -= amountY * scrollSpeed;
        // controllo posizione
        clampScroll();
        return true;
    }

    // altri metodi
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

    // ****************************** //
    // METODI DELL'INTERFACCIA Screen //
    // ****************************** //
    // aggiornamento grafica
    @Override
    public void render(float delta) {
        // attivazione controllo input
        Gdx.input.setInputProcessor(this);

        // init screen
        screen.begin();

        // disegno dello sfondo
        System.out.println(-scrollY);
        screen.draw(bg, 0, -scrollY); // l'immagine viene "scrollata"
        //screen.draw(bg, 0, -2300); // l'immagine viene "scrollata"
        // disegno delle navicelle e scritta dei loro attributi
        drawShips();

        // chiusura screen
        screen.end();

    }

    // rilascio risorse
    @Override public void dispose() {
        screen.dispose();
        bg.dispose();
        selectionBox.dispose();
    }

    // Altri metodi vuoti obbligatori dello Screen
    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
