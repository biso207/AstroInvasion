/*
Astro Invasion - class Avatar -
Crea gli oggetti Avatar e implementa metodi per controllare lo stato di sblocco
Developed by BIGA©. All rights reserved.
*/

package sorgente.Entities;

import sorgente.DataUserManager;

public class Avatar {
    // nome e missione dell'avatar
    private String nome, missione;

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

        // return stato avatar => true=sbloccato; false=bloccato
        return switch (id) {
            case 0, 1, 2, 3 -> true;
            case 4 -> level > 12;
            case 5 -> level > 14;
            case 6 -> level > 16;
            case 7 -> level > 18;
            case 8 -> level > 21;
            case 9 -> level > 22;
            case 10 -> level > 25;
            case 11 -> level > 28;
            case 12 -> level > 34;
            case 13 -> level > 36;
            case 14 -> level > 38;
            case 15 -> level == 40;
            case 16 -> total_credits >= 10000;
            case 17 -> total_credits >= 50000;
            case 18 -> points >= 3000000;
            case 19 -> points >= 5000000l;
            default -> false;
        };
    }

    // getter nome
    public String getNome() {
        return nome;
    }

    // getter missione
    public String getMissione() {
        return missione;
    }
}
