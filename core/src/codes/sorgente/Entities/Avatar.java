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

        // return dello sblocco avatar
        switch (id) {
            case 0:
            case 1:
            case 2:
            case 3: return true;
            case 4: return level > 12;
            case 5: return level > 14;
            case 6: return level > 16;
            case 7: return level > 18;
            case 8: return level > 21;
            case 9: return level > 22;
            case 10: return level > 25;
            case 11: return level > 28;
            case 12: return level > 34;
            case 13: return level > 36;
            case 14: return level > 38;
            case 15: return level > 40;
            case 16: return total_credits >= 1000;
            case 17: return total_credits >= 5000;
            case 18: return points >= 1000000;
            case 19: return winSB >= 100;
        }
        return false;
    }
}
