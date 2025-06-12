/*
Astro Invasion - class Missions -
Crea la missione corrente della modalità delle missioni di gioco
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.Missions;

// import librerie e codici
import sorgente.DataUserManager;

import java.text.NumberFormat;
import java.util.Locale;

public class Missions {
    public final String text1, text2, prize, path;
    private final int objMission;

    // costruttore
    public Missions(String text1, int objMission, String text2, String prize, String path) {
        this.text1 = text1;
        this.text2 = text2;
        this.prize = prize;
        this.path = path;
        this.objMission = objMission;
    }

    // calcolo numObjMission
    public int calcNumObjMission() {
        int numMission = (int) DataUserManager.getProgress("num_mission"); // recupero numero missione completate
        double molt = Math.ceil((double) numMission/4);  // moltiplicatore valore base missione con arrotondamento per eccesso

        return (int) (objMission * molt);  // return progresso per la missione corrente
    }

    // metodo per costruire la stringa della missione da completare
    public String printMission() {
        double numObjMission = calcNumObjMission();

        // formattazione risultato
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US); // formatter per la virgola delle migliaia
        String numObjMissionStr = formatter.format(numObjMission); // conversione in stringa

        return (text1 + " " + numObjMissionStr + " " + text2);
    }
}
