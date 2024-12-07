// Classe "Spacecraft" per creare e gestire gli oggetti delle navicelle

package com.biga.astroinvasion;

public class Spacecraft {
    // attributi
    private final String name, pathImg;
    private final int bonusPoint, spSpeed, laserSpeed;

    // costruttore
    Spacecraft(String name, String pathImg, int bonusPoint, int spSpeed, int laserSpeed) {
        this.name = name;
        this.pathImg = pathImg;
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
