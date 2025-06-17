/*
Astro Invasion - class Main -
Classe principale del progetto AstroInvasion.
L'entrata del programma è in "lwjgl3/src/main/java/com.biga.astroinvasion.lwjgl3/Lwjgl3Launcher.java"
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente;

// import codici e librerie
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sorgente.LogInSignUp.AuthAlgorithms;
import sorgente.UserData.CloudStorageManager;
import sorgente.UserData.SessionLockManager;


public class Main extends Game {
    public SpriteBatch screen;

    @Override
    public void create() {
        screen = new SpriteBatch();

        // chiamata alla schermata di caricamento
        this.setScreen(new LoadingScreen(this, true));

        // limite a 60 fps
        Gdx.graphics.setForegroundFPS(60);
    }

    @Override
    public void dispose() {
        if (AuthAlgorithms.nickname!=null) SessionLockManager.shutdownAll(); // rilascia il lock

        screen.dispose(); // rimozione risorse
    }
}
