/*
Astro Invasion - class Spacecraft -
Crea e gestisce gli oggetti Spacecraft che si rappresentano le navicelle in gioco
Developed by BIGA©. All rights reserved.
*/

package sorgente.Entities;

import com.badlogic.gdx.graphics.Texture;

public class Spacecraft {
    // attributi
    private final String name, pathImg;
    private final int bonusPoint, spSpeed, laserSpeed;
    private final Texture laserTexture;

    // costruttore
    public Spacecraft(String name, String pathImg, Texture laserTexture, int bonusPoint, int spSpeed, int laserSpeed) {
        this.name = name;
        this.pathImg = pathImg;
        this.laserTexture = laserTexture;
        this.bonusPoint = bonusPoint;
        this.spSpeed = spSpeed;
        this.laserSpeed = laserSpeed;
    }

    // GETTER //

    // getter del nome
    public String getName() {
        return name;
    }

    // getter del percorso dell'immagine
    public String getPathImg() {
        return pathImg;
    }

    // getter del percorso dell'immagine del laser
    public Texture getLaserTexture() {
        return laserTexture;
    }

    // getter della percentuale punti bonus
    public int getBonusPoint() {
        return bonusPoint;
    }

    // getter velocità aggiuntiva
    public int getSpSpeed() {
        return spSpeed;
    }

    // getter velocità laser aggiuntiva
    public int getLaserSpeed() {
        return laserSpeed;
    }
}
