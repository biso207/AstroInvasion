// Classe 'RTG' per creare e gestire le missioni del Road To Glory (RTG)

package sorgente.Missions;

import java.text.NumberFormat;
import java.util.Locale;

public class RTG {
    public String text1, text2, prize, path;
    int objMission;

    // costruttore
    public RTG(String text1, int objMission, String text2, String prize, String path) {
        this.text1 = text1;
        this.text2 = text2;
        this.prize = prize;
        this.path = path;
        this.objMission = objMission;
    }

    public String printMission(double numMission) {
        double molt = numMission/4;  // moltiplicatore valore base missione
        molt = Math.ceil(molt); // arrotondamento per eccesso del risultato

        double numObjMission = objMission * molt;  // obiettivo missione

        // formattazione risultato
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US); // formatter per la virgola delle migliaia
        String numObjMissionStr = formatter.format(numObjMission); // conversione in stringa

        return (text1 + " " + numObjMissionStr + " " + text2);
    }
}
