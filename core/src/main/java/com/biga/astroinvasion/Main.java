package com.biga.astroinvasion;

import com.badlogic.gdx.Game;

public class Main extends Game {

    @Override
    public void create() {
        this.setScreen(new LoadingScreen(this));
    }
}
