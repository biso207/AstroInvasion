/*
Astro Invasion - class Avatar -
Crea gli oggetti Avatar e implementa metodi per controllare lo stato di sblocco
Developed by BIGA©. All rights reserved.
*/

package Entities;

public class Avatar {
    // boolean di sblocco avatar
    private boolean achieved;
    // nome e missione dell'avatar
    private String nome, missione;

    // costruttore
    public Avatar(String nome, String missione) {
        this.nome = nome;
        this.missione = missione;
    }

    // metodo per restituire lo stato di un avatar (sbloccato o meno)
    public boolean isAchieved(int id) {

        return achieved;
    }
}
