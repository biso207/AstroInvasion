package com.biga.astroinvasion;

import com.badlogic.gdx.math.Rectangle;

public class Alien {
    private String pathImg;
    private Rectangle alienRect;

    // costruttore
    Alien(String pathImg, Rectangle alienRect) {
        this.pathImg = pathImg;
        this.alienRect = alienRect;
    }

    // getter percorso immagine
    public String getPathImg() {
        return pathImg;
    }

    // getter rettangolo alieno
    public Rectangle getAlienRect() {
        return alienRect;
    }
}
