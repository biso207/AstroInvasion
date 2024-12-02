// Classe 'Mission' per creare e gestire le missioni del Road To Glory (RTG)

package com.biga.astroinvasion;

public class Mission {
    String text1, text2, prize, path;
    int objMission;

    Mission(String text1, int objMission, String text2, String prize, String path) {
        this.text1 = text1;
        this.text2 = text2;
        this.prize = prize;
        this.path = path;
        this.objMission = objMission;
    }

    public String printMission(double numMission) {
        double molt = numMission/4;  // moltiplicatore valore base missione
        molt=Math.ceil(molt); // arrotondamento per eccesso del risultato

        double numObjMission = objMission * molt;  // obiettivo missione

        return (text1 + " " + Math.round(numObjMission) + " " + text2);
    }
}
