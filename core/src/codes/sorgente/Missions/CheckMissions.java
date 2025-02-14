// Classe 'CheckMissions' per controllare il completamento delle missioni nella pagina Missions

package sorgente.Missions;

public class CheckMissions {
    public int contCM=0;

    public CheckMissions() {}

    // istanza di CheckMissions
    public static CheckMissions getInstance() {
        return new CheckMissions();
    }

    // metodo per contare il completamento delle missioni nella pagina 'Missions'
    public boolean[] checkCompleted(int state, int check) {
        boolean[] isCompleted = {false, false, false, false};
        switch (state) {
            // pagina 'missions 1'
            case 26:
                if (check >= 10) isCompleted[0] = true;
                if (check >= 40) isCompleted[1] = true;
                if (check >= 70) isCompleted[2] = true;
                if (check >= 100) isCompleted[3] = true;


                // conteggio missioni completate
                for(int i = 0; i<4; i++) if(isCompleted[i]) contCM++;
                break;

            // pagina 'missions 2'
            case 27:
                if (check >= 400) isCompleted[0] = true;
                if (check >= 600) isCompleted[1] = true;
                if (check >= 800) isCompleted[2] = true;
                if (check >= 1000) isCompleted[3] = true;
                // conteggio missioni completate
                for(int i = 0; i<4; i++) if(isCompleted[i]) contCM++;
                break;

            // pagina 'missions 3'
            case 28:
                if (check >= 10) isCompleted[0] = true;
                if (check >= 40) isCompleted[1] = true;
                if (check >= 70) isCompleted[2] = true;
                if (check >= 100) isCompleted[3] = true;
                // conteggio missioni completate
                for(int i = 0; i<4; i++) if(isCompleted[i]) contCM++;
                break;

            // pagina 'missions 4'
            case 29:
                if (check >= 10) isCompleted[0] = true;
                if (check >= 40) isCompleted[1] = true;
                if (check >= 70) isCompleted[2] = true;
                if (check >= 100) isCompleted[3] = true;
                // conteggio missioni completate
                for(int i = 0; i<4; i++) if(isCompleted[i]) contCM++;
                break;

            // pagina 'missions 5'
            case 30:
                if (check >= 100000) isCompleted[0] = true;
                if (check >= 400000) isCompleted[1] = true;
                if (check >= 700000) isCompleted[2] = true;
                if (check >= 1000000) isCompleted[3] = true;
                // conteggio missioni completate
                for(int i = 0; i<4; i++) if(isCompleted[i]) contCM++;
                break;

            // pagina 'missions 6'
            case 31:
                if (check >= 10) isCompleted[0] = true;
                if (check >= 20) isCompleted[1] = true;
                if (check >= 30) isCompleted[2] = true;
                if (check >= 40) isCompleted[3] = true;
                // conteggio missioni completate
                for(int i = 0; i<4; i++) if(isCompleted[i]) contCM++;
                break;

            // pagina 'missions 7'
            case 32:
                if (check >= 1000) isCompleted[0] = true;
                if (check >= 5000) isCompleted[1] = true;
                if (check >= 10000) isCompleted[2] = true;
                // conteggio missioni completate
                for(int i = 0; i<4; i++) if(isCompleted[i]) contCM++;
                if (contCM==27) isCompleted[3] = true;
                break;
        }

        return isCompleted;
    }
}
