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
import sorgente.Main;
import sorgente.ResourceLoader;
import java.util.*;

public class SpacecraftSelectionManager implements Screen, InputProcessor, ResourceLoader {
    private final Main game;
    private final SpriteBatch screen;
    private Texture bg, selectionBox;
    private final List<SpacecraftData> spacecrafts;
    private final Map<Rectangle, Integer> clickableAreas;
    private int selectedId = -1; // id della navicella selezionata

    private float scrollY = 2300; // posizione iniziale dello scroll/pagina
    private final float maxScrollY = 2300; // Altezza massima della schermata scrollabile (immagine - altezza schermo)
    private final float scrollSpeed = 50f;  // Velocità dello scroll per rotellina

    private BitmapFont fontBoldWhite18;

    // costruttore
    public SpacecraftSelectionManager(Main game) {
        this.game = game;
        // init dello screen
        this.screen = game.screen;

        clickableAreas = new HashMap<>();

        // caricamento dati navicelle
        spacecrafts = loadSpacecrafts();

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

        // missioni delle navicelle
        String[] missions = {"", "", "", "",
            "Complete Level 2", "Complete Level 5", "Complete Level 8", "Complete Level 10",
            "Complete Level 12", "Complete Level 15", "Complete Level 18", "Complete Level 20",
            "Complete Level 22", "Complete Level 25", "Complete Level 28", "Complete Level 30",
            "Complete Level 32", "Complete Level 35", "Complete Level 38", "Complete Level 40",
            "Buy in the Market", "Buy in the Market", "Win 100 SB", "Reach Task 100 in RTG"
        };
        // lore delle navicelle
        String[] lore = {"Inevitable End", "Shapeshifting Threat", "Legendary Flight", "Stellar Rebel",
            "Ancestral Warrior", "Energy Thief", "Deadly Silence", "Blazing Rebirth",
            "Cosmic Rage", "Divine Fortress", "Invincible Purity", "Glitched Code",
            "Space Hunter", "Hybrid Fury", "Supersonic Wind", "Sacred Flame",
            "Lunar Light", "Shadow Tentacles", "Eternal Abyss", "Echo Of Time",
            "Stellar Longship", "Frost Dominator", "Rising star", "Absolute Origin"
        };
        // potenze delle navicelle => ordine potenze: 0:vel navicella, 1:vel laser, 2:bonus punti
        int[][] attributes = {
            {1, 0, 0}, {0, 0, 5}, {0, 1, 0}, {1, 0, 0},
            {2, 0, 0}, {0, 0, 10}, {0, 2, 0}, {0, 3, 0},
            {3, 0, 0}, {0, 0, 15}, {1, 1, 0}, {2, 0, 10},
            {2, 1, 0}, {0, 0, 20}, {4, 1, 0}, {1, 2, 0},
            {2, 2, 0}, {0, 0, 30}, {1, 4, 0}, {0, 2, 10},
            {5, 5, 0}, {5, 0, 50}, {5, 0, 50}, {5, 5, 50}
        };

        // popolamento della mappa navicelle
        for (int i = 0; i < 24; i++) {
            list.add(new SpacecraftData(i, missions[i], lore[i], attributes[i][0], attributes[i][1], attributes[i][2]));
        }

        // popolamento mappa range per la selezione delle navicelle
        int x1=82, x2=471, x3=493, x4=882, y1=342, y2=486;
        int spID=0; // id della navicella
        Rectangle area; // rappresenta l'area cliccabile

        for (int i=0; i<6; i++) {
            for (int j=0; j<4; j++) {
                if (j==0 || j==2) area = new Rectangle(x1, y1, x2-x1, y2-y1);
                else area = new Rectangle(x3, y1, x4-x3, y2-y1);

                // aggiunta del range solo se la navicella è cliccabile
                if (SpacecraftData.isAchieved(spID)) clickableAreas.put(area, spID);
                if (j==1)  { y1+=168; y2+=168; } // passaggio alla riga seguente

                // passaggio alla navicella successiva
                spID++;
            }
            // passaggio al gruppo successivo
            y1+= 257; y2+=257;
        }

        return list;
    }



    @Override
    public void loadImages() {
        bg = new Texture("lobby_screens/lobby (4).png");
        selectionBox = new Texture("images/rect_selected_SP.png");
    }

    @Override
    public void loadFont() {
        fontBoldWhite18 = new BitmapFont(Gdx.files.internal("font/inter/bold_white_18.fnt")); // inter-bold white 18
    }

    // **************** //
    // GESTIONE GRAFICA //
    // **************** //
    // metodo per stampare i testi e le immagini
    public void createGraphic() {
        int spID=0; // id navicella per recuperare gli attributi
        int X, x1=258, x2= 670, y=2645; // x e y della prima scritta della prima navicella
        // iterazione con 2 for per dividere i gruppi delle navicelle
        for (int i=0; i<6; i++) {
            for (int j=0; j<4; j++) {
                SpacecraftData s = spacecrafts.get(spID); // oggetto navicella

                // x delle scritte (x1 è la prima colonna, x2 è la seconda)
                X = (j == 0 || j == 2) ? x1 : x2;

                // attributi mostrati se la navicella è sbloccata altrimenti sono nascosti
                if (SpacecraftData.isAchieved(spID)) {
                    if (s.getSpeed()>=1) fontBoldWhite18.draw(screen, "+" + s.getSpeed(), X, y-scrollY);
                    if (s.getLaserSpeed()>=1) fontBoldWhite18.draw(screen, "+" + s.getLaserSpeed(), X, (y-37)-scrollY);
                    if (s.getBonusPoints()>=1) fontBoldWhite18.draw(screen, "+" + s.getBonusPoints() + "%", X, (y-74)-scrollY);
                    fontBoldWhite18.draw(screen, s.getLore(), X, (y-107)-scrollY);

                    // disegno rettangolo di selezione
                    if (spID == (int)DataUserManager.getProgress("spacecraft")) screen.draw(selectionBox, X-180, (y-145)-scrollY);
                }
                else {
                    fontBoldWhite18.draw(screen, "?", X, y-scrollY);
                    fontBoldWhite18.draw(screen, "?", X, (y-37)-scrollY);
                    fontBoldWhite18.draw(screen, "?", X, (y-74)-scrollY);
                    fontBoldWhite18.draw(screen, s.getMission(), X, (y-107)-scrollY);
                }

                if (j==1)  y-=168; // passaggio alla riga seguente

                // passaggio alla navicella successiva
                spID++;
            }
            // passaggio al gruppo successivo
            y-= 257;
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
        // chiusura pagina cliccando sulla X
        if ((screenX >= 906 && screenX <= 947) && (screenY >= 83 && screenY <= 122)) {
            game.setScreen(new LobbyManager(game)); // back to lobby
        }

        // controllo selezione navicella
        for (Map.Entry<Rectangle, Integer> entry : clickableAreas.entrySet()) {
            Rectangle area = entry.getKey();
            System.out.println(screenY + " " + scrollY);
            if (area.contains(screenX, screenY+(maxScrollY-scrollY))) {
                selectedId = entry.getValue(); // recupero id navicella selezionata
                // salvataggio navicella scelta se sbloccata
                if (SpacecraftData.isAchieved(selectedId)) DataUserManager.setProgress("spacecraft", selectedId);
                break;
            }
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
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
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
        screen.draw(bg, 0, -scrollY); // l'immagine di sfondo viene "scrollata"

        // scrittura testi e stampa immagini
        createGraphic();

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
