package com.biga.astroinvasion;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    public static SpriteBatch screen;

    @Override
    public void create() {
        screen = new SpriteBatch();
        Gdx.input.setInputProcessor(new MyInputProcessor()); // Imposta MyInputProcessor per gestire gli input
        this.setScreen(new LoadingScreen(this)); // Schermata di caricamento
    }

    @Override
    public void dispose() {
        screen.dispose(); // Assicurati di liberare le risorse quando non sono più necessarie
    }
}
