/*
Astro Invasion - Interface InputMethods -
Implementa i metodi astratti per il controllo degli input nelle schermate grafiche
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente;

public interface InputHandler {
    boolean keyTyped(char character);  // gestisce la digitazione da tastiera
    boolean touchDown(int screenX, int screenY);  // gestisce il click del mouse
}
