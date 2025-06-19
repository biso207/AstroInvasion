/*
Astro Invasion - class Avatar -
Crea gli oggetti Avatar e implementa metodi per controllare lo stato di sblocco
Developed by BIGA©. All rights reserved.
*/

package sorgente.Entities;

import sorgente.UserData.DataUserManager;

public class Avatar {
    // nome e missione dell'avatar
    private final String nome, missione;

    // costruttore
    public Avatar(String nome, String missione) {
        this.nome = nome;
        this.missione = missione;
    }

    // metodo per restituire lo stato di un avatar (sbloccato o meno)
    public static boolean isAchieved(int id) {
        // livello utente
        int level = (int) DataUserManager.getProgress("level");
        // crediti totali raccolti
        int total_credits = (int) DataUserManager.getProgress("total_credits");
        // punti
        int  points = (int) DataUserManager.getProgress("points");
        // vittorie space battle
        int winSB = (int) DataUserManager.getProgress("won_SB");
        // numero missione
        int numMission = (int) DataUserManager.getProgress("num_mission");

        // return stato avatar => true=sbloccato; false=bloccato
        return switch (id) {
            case 0, 1, 2, 3 -> true;
            case 4 -> level > 3;
            case 5 -> level > 7;
            case 6 -> level > 9;
            case 7 -> level > 13;
            case 8 -> level > 17;
            case 9 -> level > 19;
            case 10 -> level > 23;
            case 11 -> level > 27;
            case 12 -> level > 29;
            case 13 -> level > 37;
            case 14 -> level > 36;
            case 15 -> level > 39;
            case 16 -> total_credits >= 50000;
            case 17 -> winSB >= 200;
            case 18 -> points >= 5000000;
            case 19 -> numMission > 50;
            default -> false;
        };
    }

    // getter nome
    public String getName() {
        return nome;
    }

    // getter missione
    public String getMissione() {
        return missione;
    }
}
