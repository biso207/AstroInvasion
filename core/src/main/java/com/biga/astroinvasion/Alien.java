/*
Astro Invasion - class Alien -
This class creates the objects Alien for the game mode Classic Game
Developed by BIGA©. All rights reserved.
*/

package com.biga.astroinvasion;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Alien {
    private Texture img;
    private Rectangle alienRect;

    // costruttore
    Alien(Texture img, Rectangle alienRect) {
        this.img = img;
        this.alienRect = alienRect;
    }

    // getter percorso immagine
    public Texture getImg() {
        return img;
    }

    // getter rettangolo alieno
    public Rectangle getAlienRect() {
        return alienRect;
    }
}
