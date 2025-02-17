/*
Astro Invasion - class DataUserManager -
Gestisce i progressi utente
Developed by BIGA©. All rights reserved.
*/

/*
Questa classe gestisce i metodi di scrittura e lettura dei progressi utente.
Il metodo loadProgresses carica i progressi utente dal json dei progressi utente. I progressi sono mappati in un
HashMap con una key String che fa riferimento al tipo di progresso e un value Object che prende i valori
dei progressi indipendentemente dal loro tipo, verrà poi eseguito un casting dal chiamante per recuperare
il tipo necessario.
La scrittura sul json è eseguita a modifica o progresso compiuto mentre, la lettura, solo all'avvio di una sessione
utente, le uniche cose che si modificano sono le value della HashMap. Così facendo non occupiamo memoria per salvare
i progressi in diverse variabili e viene effettuata una scrittura ogni tanto leggendo una sola volta
numerosi dati di progressi utente.
*/

// package di appartenenza
package sorgente;

// import librerie e codici
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.JSONObject;

public class DataUserManager {
    // percorso dei file
    private static String percorsoFile;
    // hashmap dei progressi
    private static final Map<String, Object> progressi = new HashMap<>();

    // costruttore
    public DataUserManager(String percorsoFile) {
        DataUserManager.percorsoFile = percorsoFile;
        loadProgresses();
    }

    // metodo per caricare tutti i progressi in memoria (HashMap)
    private void loadProgresses() {
        try {
            String contenuto = Files.readString(Path.of(percorsoFile));
            JSONObject json = new JSONObject(contenuto); // istanza di un json

            // for-each per recuperare i vari valori
            for (String key : json.keySet()) {
                progressi.put(key, json.get(key)); // qualunque tipo di dato è ammesso
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + percorsoFile);
        }
    }

    // metodo per recuperare un progresso specifico
    public static Object getProgress(String nome) {
        return progressi.getOrDefault(nome, null);
    }

    // metodo per aggiornare un singolo progresso nell'HashMap e aggiornare il json
    public static void setProgress(String nome, Object valore) {
        progressi.put(nome, valore); // aggiornamento HashMap
        saveProgresses(); // aggiornamento del json
    }

    // metodo per scrivere i progressi sul json
    private static void saveProgresses() {
        try (FileWriter file = new FileWriter(percorsoFile)) {
            file.write(new JSONObject(progressi).toString(4)); // indenta per leggibilità
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio del file: " + percorsoFile);
        }
    }
}

