/*
Astro Invasion - class Alien -
Crea gli oggetti Alien per la modalità Classic Game
Developed by BIGA©. All rights reserved.
*/

package sorgente.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Alien {
    private Texture img;
    private Rectangle alienRect;

    // costruttore
    public Alien(Texture img, Rectangle alienRect) {
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
