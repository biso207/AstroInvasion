/*
Astro Invasion - class SpacecraftData -
Gestisce e controlla i dati, come lo stato di sblocco, delle navicelle da selezionare
Developed by BIGA©. All rights reserved.
*/

package sorgente.Lobby;

import com.badlogic.gdx.graphics.Texture;
import sorgente.DataUserManager;

public class SpacecraftData {
    public int id;
    public String lore, mission;
    public int speed, laserSpeed, bonusPoints;

    public SpacecraftData(int id, String mission, String lore, int speed, int laserSpeed, int bonusPoints) {
        this.id = id;
        this.mission = mission;
        this.lore = lore;
        this.speed = speed;
        this.laserSpeed = laserSpeed;
        this.bonusPoints = bonusPoints;
    }

    // metodo per restituire lo stato di una navicella (sbloccata o meno)
    public static boolean isAchieved(int id) {
        // livello utente
        int level = (int) DataUserManager.getProgress("level");
        // stato elementi 5 e 6 del negozio
        boolean state5 = (boolean) DataUserManager.getProgress("state_product_5");
        boolean state6 = (boolean) DataUserManager.getProgress("state_product_6");
        // numero vittorie SB
        int winSB = (int) DataUserManager.getProgress("won_SB");
        // task raggiunto nel Missions
        int fragments = (int) DataUserManager.getProgress("alpha_fragments");

        // return stato avatar => true=sbloccato; false=bloccato
        return switch (id) {
            case 0, 1, 2, 3 -> true;
            case 4 -> level > 2;
            case 5 -> level > 4;
            case 6 -> level > 6;
            case 7 -> level > 8;
            case 8 -> level > 12;
            case 9 -> level > 14;
            case 10 -> level > 16;
            case 11 -> level > 18;
            case 12 -> level > 22;
            case 13 -> level > 24;
            case 14 -> level > 26;
            case 15 -> level > 28;
            case 16 -> level > 32;
            case 17 -> level > 34;
            case 18 -> level > 36;
            case 19 -> level > 38;
            case 20 -> state6;
            case 21 -> state5;
            case 22 -> winSB >= 200;
            case 23 -> fragments == 4;
            default -> false;
        };
    }

    // GETTER //
    // getter id
    public int getId() {
        return id;
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
    public int getBonusPoints() {
        return bonusPoints;
    }


}
