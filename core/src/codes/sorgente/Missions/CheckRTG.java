/*
Astro Invasion - class CheckRTG -
Controlla il completamento delle missioni del Road To Glory
Developed by BIGA©. All rights reserved.
*/

package sorgente.Missions;

import sorgente.DataUserManager;

public class CheckRTG {

    // metodo per controllare il completamento delle missioni RTG
    public static boolean checkMission(int numMission) {
        // recupero progressi di gioco
        int points = (int) DataUserManager.getProgress("points");
        int winsSB = (int) DataUserManager.getProgress("won_SB");
        int numTask = (int) DataUserManager.getProgress("num_mission");
        int credits = (int) DataUserManager.getProgress("total_credits");

        // switch per i controlli
        return switch(numMission) {
            case 0 -> points>=5000000;
            case 1 -> winsSB>=100;
            case 2 -> numTask>=100;
            case 3 -> credits>=50000;
            default -> false;
        };
    }
}
