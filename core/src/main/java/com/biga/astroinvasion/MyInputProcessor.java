package com.biga.astroinvasion;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class MyInputProcessor extends InputAdapter {

    @Override
    public boolean keyDown(int keycode) {
        // Verifica quale tasto è stato premuto
        if (keycode == Input.Keys.LEFT) {
            System.out.println("Tasto sinistro premuto!");
        } else if (keycode == Input.Keys.RIGHT) {
            System.out.println("Tasto destro premuto!");
        } else if (keycode == Input.Keys.SPACE) {
            System.out.println("Spazio premuto!");
        } else {
            System.out.println("Altro tasto premuto: " + keycode);
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        System.out.println("Tocco nello schermo alle coordinate: X=" + screenX + " Y=" + screenY);
        return true;
    }

    // Aggiungi altri metodi se necessario (keyUp, touchUp, ecc.)
}
