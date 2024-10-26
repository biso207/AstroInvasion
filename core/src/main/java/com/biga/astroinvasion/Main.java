package com.biga.astroinvasion;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class Main extends Game {

    @Override
    public void create() {
        Gdx.input.setInputProcessor(new MyInputProcessor()); // Imposta MyInputProcessor per gestire gli input
        this.setScreen(new LoadingScreen(this)); // Schermata di caricamento
    }
}
