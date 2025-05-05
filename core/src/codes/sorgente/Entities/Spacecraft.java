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
    private final int bonusPoints, spSpeed, laserSpeed;
    private final Texture laserTexture;

    // costruttore
    public Spacecraft(String name, String pathImg, Texture laserTexture, int spSpeed, int laserSpeed, int bonusPoints) {
        this.name = name;
        this.pathImg = pathImg;
        this.laserTexture = laserTexture;
        this.bonusPoints = bonusPoints;
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
    public int getBonusPoints() {
        return bonusPoints;
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
