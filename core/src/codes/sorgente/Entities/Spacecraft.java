/*
Astro Invasion - class Spacecraft -
Crea e gestisce gli oggetti Spacecraft che si rappresentano le navicelle in gioco
Developed by BIGA©. All rights reserved.
*/

package sorgente.Entities;

import com.badlogic.gdx.graphics.Texture;
import sorgente.UserData.DataUserManager;

public class Spacecraft {
    // attributi
    private String name, lore, mission;
    private int bonusPoints, spSpeed, laserSpeed;
    private Texture imgTexture, laserTexture;

    // costruttore
    public Spacecraft(int id) {
        // init delle navicelle
        setParams(id);
    }

    // metodo per controllare lo stato di sblocco di una navicella
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

    // metodo per settare in automatico i parametri
    public void setParams(int id) {
        switch (id) {
            case 0 -> {
                name = "Omega";
                lore = "Inevitable End";
                mission = "";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 1;
                laserSpeed = 0;
                bonusPoints = 0;
            }
            case 1 -> {
                name = "Idra";
                lore = "Shapeshifting Threat";
                mission = "";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 0;
                laserSpeed = 0;
                bonusPoints = 5;
            }
            case 2 -> {
                name = "Woka";
                lore = "Legendary Flight";
                mission = "";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 0;
                laserSpeed = 0;
                bonusPoints = 5;
            }
            case 3 -> {
                name = "Pegaso";
                lore = "Stellar Rebel";
                mission = "";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 0;
                laserSpeed = 1;
                bonusPoints = 0;
            }
            case 4 -> {
                name = "Ares";
                lore = "Ancestral Warrior";
                mission = "Complete Level 2";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 1;
                laserSpeed = 1;
                bonusPoints = 0;
            }
            case 5 -> {
                name = "Andvari";
                lore = "Energy Thief";
                mission = "Complete Level 4";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 0;
                laserSpeed = 1;
                bonusPoints = 10;
            }
            case 6 -> {
                name = "Siko";
                lore = "Deadly Silence";
                mission = "Complete Level 6";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 1;
                laserSpeed = 0;
                bonusPoints = 10;
            }
            case 7 -> {
                name = "Fenixia";
                lore = "Blazing Rebirth";
                mission = "Complete Level 8";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 1;
                laserSpeed = 1;
                bonusPoints = 0;
            }
            case 8 -> {
                name = "Selen";
                lore = "Cosmic Rage";
                mission = "Complete Level 12";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 2;
                laserSpeed = 1;
                bonusPoints = 0;
            }
            case 9 -> {
                name = "Centauro";
                lore = "Divine Fortress";
                mission = "Complete Level 14";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 0;
                laserSpeed = 2;
                bonusPoints = 20;
            }
            case 10 -> {
                name = "Zephyr";
                lore = "Invincible Purity";
                mission = "Complete Level 16";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 2;
                laserSpeed = 0;
                bonusPoints = 20;
            }
            case 11 -> {
                name = "Malloc";
                lore = "Glitched Code";
                mission = "Complete Level 18";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 1;
                laserSpeed = 2;
                bonusPoints = 0;
            }
            case 12 -> {
                name = "Orion";
                lore = "Space Hunter";
                mission = "Complete Level 22";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 2;
                laserSpeed = 2;
                bonusPoints = 0;
            }
            case 13 -> {
                name = "Asgard";
                lore = "Hybrid Fury";
                mission = "Complete Level 24";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 0;
                laserSpeed = 2;
                bonusPoints = 30;
            }
            case 14 -> {
                name = "Galahad";
                lore = "Supersonic Wind";
                mission = "Complete Level 26";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 2;
                laserSpeed = 0;
                bonusPoints = 30;
            }
            case 15 -> {
                name = "Seraphis";
                lore = "Sacred Flame";
                mission = "Complete Level 28";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 1;
                laserSpeed = 3;
                bonusPoints = 0;
            }
            case 16 -> {
                name = "Beowulf";
                lore = "Lunar Light";
                mission = "Complete Level 32";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 3;
                laserSpeed = 2;
                bonusPoints = 0;
            }
            case 17 -> {
                name = "Scylla";
                lore = "Shadow Tentacles";
                mission = "Complete Level 34";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 0;
                laserSpeed = 3;
                bonusPoints = 40;
            }
            case 18 -> {
                name = "Keto";
                lore = "Eternal Abyss";
                mission = "Complete Level 36";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 3;
                laserSpeed = 0;
                bonusPoints = 40;
            }
            case 19 -> {
                name = "Efron";
                lore = "Echo Of Time";
                mission = "Complete Level 38";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 2;
                laserSpeed = 3;
                bonusPoints = 0;
            }
            case 20 -> {
                name = "Drakar";
                lore = "Stellar Longship";
                mission = "Buy in the marketplace";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 3;
                laserSpeed = 3;
                bonusPoints = 0;
            }
            case 21 -> {
                name = "Rorik";
                lore = "Frost Dominator";
                mission = "Buy in the marketplace";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 0;
                laserSpeed = 3;
                bonusPoints = 50;
            }
            case 22 -> {
                name = "Astrid";
                lore = "Rising Star";
                mission = "Win 200 S.B. matches";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 3;
                laserSpeed = 0;
                bonusPoints = 50;
            }
            case 23 -> {
                name = "Alpha";
                lore = "Absolute Origin";
                mission = "Collect all 4 fragments";
                imgTexture = new Texture("images/spacecrafts/sp" + (id+1) + ".png");
                laserTexture = new Texture("images/lasers/laser (" + (id+1) + ").png");
                spSpeed = 3;
                laserSpeed = 3;
                bonusPoints = 10;
            }
        }
    }

    // GETTER //
    // getter del nome
    public String getName() {
        return name;
    }

    // getter lore
    public String getLore() {
        return lore;
    }

    // getter mission
    public String getMission() {
        return mission;
    }

    // getter del percorso dell'immagine
    public Texture getImgTexture() {
        return imgTexture;
    }

    // getter del percorso dell'immagine del laser
    public Texture getLaserTexture() {
        return laserTexture;
    }

    // getter velocità aggiuntiva
    public int getSpSpeed() {
        return spSpeed;
    }

    // getter velocità laser aggiuntiva
    public int getLaserSpeed() {
        return laserSpeed;
    }

    // getter della percentuale punti bonus
    public int getBonusPoints() {
        return bonusPoints;
    }
}
