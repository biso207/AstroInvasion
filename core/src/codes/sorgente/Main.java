/*
 * Astro Invasion - class Main -
 * Owns the main libGDX game lifecycle and shared rendering resources.
 *
 * Developed & Designed by BIGA ©2024-2026. All rights reserved.
 */

// package di appartenenza
package sorgente;

// import codici e librerie
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.UserData.SessionLockManager;
import sorgente.Localization.LocalizationManager;


public class Main extends Game {
    public SpriteBatch screen;

    @Override
    public void create() {
        screen = new SpriteBatch();
        LocalizationManager.getInstance().setLanguage("en");

        // chiamata alla schermata di caricamento
        this.setScreen(new LoadingScreen(this, true));

        // limite a 60 fps
        Gdx.graphics.setForegroundFPS(60);
    }

    @Override
    public void dispose() {
        SessionLockManager.shutdownAll(); // rilascia il lock
        screen.dispose(); // rimozione risorse
    }
}
