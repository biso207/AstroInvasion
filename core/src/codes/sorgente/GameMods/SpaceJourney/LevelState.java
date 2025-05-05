// setta lo stato di un singolo livello

package sorgente.GameMods.SpaceJourney;

public enum LevelState {
    LOCKED,     // bloccato
    TO_BUY,    // sbloccato ma da pagare
    UNLOCKED,   // sbloccato e giocabile
    COMPLETED   // completato
}
