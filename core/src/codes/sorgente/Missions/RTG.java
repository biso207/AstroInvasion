/*
Astro Invasion - class RTG (RoadToGlory) -
Crea la missione corrente della modalità Road To Glory
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.Missions;

// import librerie e codici
import sorgente.DataUserManager;

import java.text.NumberFormat;
import java.util.Locale;

public class RTG {
    public String text1, text2, prize, path;
    static int objMission;
    double numObjMission;

    // costruttore
    public RTG(String text1, int objMission, String text2, String prize, String path) {
        this.text1 = text1;
        this.text2 = text2;
        this.prize = prize;
        this.path = path;
        RTG.objMission = objMission;
    }

    // calcolo numObjMission
    public static int calcNumObjMission() {
        int numMission = (int)DataUserManager.getProgress("num_mission");
        double molt = (double) numMission/4;  // moltiplicatore valore base missione
        molt = Math.ceil(molt); // arrotondamento per eccesso del risultato

        return (int) (objMission * molt);  // return progresso per la missione corrente
    }

    // metodo per costruire la stringa della missione da completare
    public String printMission() {
        numObjMission = calcNumObjMission();

        // formattazione risultato
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US); // formatter per la virgola delle migliaia
        String numObjMissionStr = formatter.format(numObjMission); // conversione in stringa

        return (text1 + " " + numObjMissionStr + " " + text2);
    }
}
