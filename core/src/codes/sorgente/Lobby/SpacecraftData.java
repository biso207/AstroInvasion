/*
Astro Invasion - class SpacecraftData -
Crea oggetti SpacecraftData per la pagina di selezione delle navicelle
Developed by BIGA©. All rights reserved.
*/

package sorgente.Lobby;

import com.badlogic.gdx.graphics.Texture;

public class SpacecraftData {
    public int id;
    public String name, lore, mission;
    public int speed,laserSpeed, bonusPoint;
    private Texture spImg;

    public SpacecraftData(int id, String name, String mission, String lore, Texture spImg, int speed, int laserSpeed, int bonusPoint) {
        this.id = id;
        this.name = name;
        this.mission = mission;
        this.lore = lore;
        this.spImg = spImg;
        this.speed = speed;
        this.laserSpeed = laserSpeed;
        this.bonusPoint = bonusPoint;
    }

    // GETTER //
    // getter id
    public int getId() {
        return id;
    }

    // getter name
    public String getName() {
        return name;
    }

    // getter imagePath
    public Texture getImage() {
        return spImg;
    }

    // getter lore
    public String getLore() {
        return lore;
    }

    // getter mission
    public String getMission() {
        return mission;
    }

    // getter speed
    public int getSpeed() {
        return speed;
    }

    // getter laserSpeed
    public int getLaserSpeed() {
        return laserSpeed;
    }

    // getter bonusPoint
    public int getBonusPoint() {
        return bonusPoint;
    }


}
