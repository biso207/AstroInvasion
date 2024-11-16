package com.biga.astroinvasion;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Lobby implements Screen {
    private final SpriteBatch screen;
    Texture img, img1, img2, img3, img4, img5, img6, img7,
        img8, img9,img10, img11, img12, img13, img14, img15,
        img16, img17, img18, img19, img20, img21, img22, img_special;
    int state, previousState;
    HashMap<Integer, Texture> hashMap = new HashMap<>();

    // costruttore
    public Lobby() {

        // set immagine di default (classic game)
        state = previousState = 6;

        // init dello screen
        screen = new SpriteBatch();

        // caricamento immagini
        loadImages();

        // musica di sottofondo
        Music openSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/lobby_sound.ogg")); // file audio
        openSound.setLooping(true); // true=loop music; false=no loop
        openSound.play(); // avvio musica
    }

    // -------------- //
    // GESTIONE INPUT //
    // -------------- //

    // classe interna per gestire gli input da mouse e tastiera
    private class MyInputProcessor extends InputAdapter {
        /*@Override
        public boolean keyTyped(char character) {
            if (enteringNickname) {
                if (character == '\n' || character == '\r' && nicknameInput.length()>=1) { // ENTER per terminare il nickname
                    enteringNickname = false;
                } else if (character == '\b' && nicknameInput.length() > 0) { // BACKSPACE per cancellare l'ultimo carattere
                    nicknameInput.deleteCharAt(nicknameInput.length() - 1);
                } else if (character >= 32 && character < 127) { // controllo digitazione caratteri validi
                    nicknameInput.append(character);
                }
            } else if (enteringPassword) {
                if (character == '\n' || character == '\r' && passwordInput.length()>=1) { // ENTER per terminare la password
                    enteringPassword = false;
                } else if (character == '\b' && passwordInput.length() > 0) { // BACKSPACE per cancellare l'ultimo carattere
                    passwordInput.deleteCharAt(passwordInput.length() - 1);
                } else if (character >= 32 && character < 127) { // controllo digitazione caratteri validi
                    passwordInput.append(character);
                }
            }
            return true;
        }
         */

        // metodo recuperare il click del mouse, crea inizialmente costruttore hash map che contenga interi e Texture, quando poi carico le immagini le abbino con l'has map; 1--> img1;
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            /*
            Lo state deve essere diverso da 7 (instructions) per non generare l'apertura
            di altre pagine dove non è possibile e poter cambiare le schermate della Lobby.
            Esempio: l'utente NON può aprire la pagina 'classic game' dalla pagina 'instructions'
            */
            if (state!=7) {
                // pagina 6 => 'classic game'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 180 && screenY <= 220)) {
                    state = 6;
                }
                // pagina 13 => 'space battle'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 230 && screenY <= 270)) {
                    state = 13;
                }
                // pagina 14 => 'space journey'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 280 && screenY <= 320)) {
                    state = 14;
                }
                // pagina 12 => 'road to glory'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 330 && screenY <= 370)) {
                    state = 12;
                }
                // pagina 15 => 'navicelle 1'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 380 && screenY <= 420)) {
                    state = 15;
                }
                // pagina 11 => 'marketplace'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 480 && screenY <= 520)) {
                    state = 11;
                }
                // pagina 7 => 'instructions'
                if ((screenX >= 50 && screenX <= 270) && (screenY >= 530 && screenY <= 570)) {
                    /*
                     Memorizzazione pagina aperta.
                     Ciò permette di ritornare alla pagina precedente dopo aver chiuso la pagina delle istruzioni
                    */
                    previousState = state;
                    state = 7;
                }
                // pagina 1 => 'avatar 1'
                if ((screenX >= 870 && screenX <=950) && (screenY >= 66 && screenY <=146)) {
                    state = 1;
                }
                // cambio pagina (1-5) => 'avatar/spacecraft/ 1->5'
                if ((screenX >= 873 && screenX <=913) && (screenY >= 553 && screenY <=593)) {
                    if (state < 5) state++;
                }
                // cambio pagina (5-1) => 'avatar/spacecraft/ 5->1'
                if ((screenX >= 343 && screenX <=373) && (screenY >= 553 && screenY <=593)) {
                    if (state <= 5 && state>1) state--;
                }
                // pagine 21 => 'instructions'
                if ((screenX >= 50 && screenX <=90) && (screenY >= 580 && screenY <=620)) {
                    previousState = state;
                    state = 21;
                }
                // pagina 22 => 'software infos'
                if ((screenX >= 150 && screenX <=190) && (screenY >= 580 && screenY <=620)) {
                    previousState = state;
                    state = 22;
                }
            }

            // chiusura pagina instruction/settings
            if ((screenX >= 908 && screenX <= 948) && (screenY >= 84 && screenY <= 124)) {
                state = previousState;
            }
            // chiusura software.infos
            if (state == 22 && (screenX >= 0 && screenX <= 1000) && (screenY >= 0 && screenY <= 700)) {
                state = previousState;
            }
            return true;
        }
    }

    // -------------------- //
    // GRAFICA DELLA CLASSE //
    // -------------------- //

    // metodo per caricare le immagini che rappresentano lo schermo
    public void loadImages(){
        img1 = new Texture("lobby_images/lobby_avatars_group1_eng.png");
        img2 = new Texture("lobby_images/lobby_avatars_group2_eng.png");
        img3 = new Texture("lobby_images/lobby_avatars_group3_eng.png");
        img4 = new Texture("lobby_images/lobby_avatars_group4_eng.png");
        img5 = new Texture("lobby_images/lobby_avatars_special_group_eng.png");
        img6 = new Texture("lobby_images/lobby_classic_game_eng.png");
        img7 = new Texture("lobby_images/lobby_instructions_eng.png");
        img8 = new Texture("lobby_images/lobby_level_up_info_eng.png");
        img9 = new Texture("lobby_images/lobby_level_up_upgrade1_eng.png");
        img10 = new Texture("lobby_images/lobby_level_up_upgrade2_eng.png");
        img11 = new Texture("lobby_images/lobby_marketplace_eng.png");
        img12 = new Texture("lobby_images/lobby_road_to_glory_eng.png");
        img13 = new Texture("lobby_images/lobby_space_battle_eng.png");
        img14 = new Texture("lobby_images/lobby_space_journey_eng.png");
        img15 = new Texture("lobby_images/lobby_spacecrafts_classic_group_eng.png");
        img16 = new Texture("lobby_images/lobby_spacecrafts_groupEfron_eng.png");
        img17 = new Texture("lobby_images/lobby_spacecrafts_groupFenixia_eng.png");
        img18 = new Texture("lobby_images/lobby_spacecrafts_groupMalloc_eng.png");
        img19 = new Texture("lobby_images/lobby_spacecrafts_groupPhoenix_eng.png");
        img20 = new Texture("lobby_images/lobby_spacecrafts_special_group_eng.png");
        img21 = new Texture("lobby_images/lobby_settings_eng.png");
        img22 = new Texture("lobby_images/lobby_software_info_eng.png");
        img_special = new Texture("lobby_images/_rect_claim_reward_eng.png");

        // mappatura hashmap
        hashMap.put(1, img1);
        hashMap.put(2, img2);
        hashMap.put(3, img3);
        hashMap.put(4, img4);
        hashMap.put(5, img5);
        hashMap.put(6, img6);
        hashMap.put(7, img7);
        hashMap.put(8, img8);
        hashMap.put(9, img9);
        hashMap.put(10, img10);
        hashMap.put(11, img11);
        hashMap.put(12, img12);
        hashMap.put(13, img13);
        hashMap.put(14, img14);
        hashMap.put(15, img15);
        hashMap.put(16, img16);
        hashMap.put(17, img17);
        hashMap.put(18, img18);
        hashMap.put(19, img19);
        hashMap.put(20, img20);
        hashMap.put(21, img21);
        hashMap.put(22, img22);
        hashMap.put(30, img_special);
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void show() {}

    // metodo per aggiornare lo schermo
    @Override
    public void render(float delta) {
        Gdx.input.setInputProcessor(new Lobby.MyInputProcessor()); // riattiva l'InputProcessor

        screen.begin();
        screen.draw(hashMap.get(state), 0, 0);
        screen.end();
    }

    // metodo per rilasciare le risorse
    @Override
    public void dispose() {
        screen.dispose();
        img.dispose();
    }
}
