package com.biga.astroinvasion;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    public SpriteBatch screen;

    @Override
    public void create() {
        screen = new SpriteBatch();

        this.setScreen(new LoadingScreen(this)); // schermata di caricamento
    }

    @Override
    public void dispose() {
        screen.dispose(); // rimozione risorse
    }
}
