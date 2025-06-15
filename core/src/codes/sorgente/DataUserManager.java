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

// import codici e librerie
import java.io.*;
import java.util.*;
import org.json.JSONObject;
import sorgente.LogInSignUp.AuthAlgorithms;
import sorgente.LogInSignUp.FirestoreStorage;
import sorgente.LogInSignUp.GlobalProgressManager;
import sorgente.LogInSignUp.LoadCallback;

import java.util.Base64;

public class DataUserManager implements LoadCallback {
    private static final Map<String, Object> progressi = new HashMap<>();; // hashmap per i dati

    // carica i progressi utente (decodifica Base64 + parsing JSON)
    public static void loadProgresses() throws IOException {
        // caricamento da cloud in remoto
        FirestoreStorage.downloadDatAsync(AuthAlgorithms.nickname, new LoadCallback() {
            @Override
            public void onProgress(int progress) {
                // aggiorna la barra di caricamento
                GlobalProgressManager.notifyProgress(progress);
            }

            @Override
            public void onComplete(boolean success, String result) {
                if (success) {
                    // decrypt della stringa passata
                    byte[] decodedBytes = Base64.getDecoder().decode(result);
                    String jsonText = new String(decodedBytes);

                    // salvataggio dati nella mappa dei progressi utente
                    JSONObject json = new JSONObject(jsonText);
                    for (String key : json.keySet()) {
                        progressi.put(key, json.get(key));
                    }
                } else {
                    System.out.println("Errore nel download dei progressi utente: " + result);
                }
            }
        });
    }

    // salva i progressi su file (JSON → Base64 → scrittura)
    public static void saveProgresses() {
        JSONObject json = new JSONObject(progressi);
        String encoded = Base64.getEncoder().encodeToString(json.toString(4).getBytes());

        // salvataggio dati utente in cloud remoto
        FirestoreStorage.uploadDatAsync(AuthAlgorithms.nickname, encoded, new LoadCallback() {
            @Override
            public void onProgress(int progress) {
                // aggiorna la barra di caricamento
                if (GlobalProgressManager.isInitialLoading) {
                    GlobalProgressManager.notifyProgress(progress);
                }
            }

            @Override
            public void onComplete(boolean success, String result) {}
        });
    }

    // recupera un progresso specifico
    public static Object getProgress(String nome) {
        return progressi.getOrDefault(nome, null);
    }

    // aggiorna un valore e salva tutto
    public static void setProgress(String nome, Object valore) {
        progressi.put(nome, valore);
        saveProgresses();
    }

    // metodo extra per resettare i progressi
    public static void resetProgress() {
        progressi.clear();
        saveProgresses();
    }

    // ************************************ //
    // METODI DELL'INTERFACCIA LoadCallback //
    // ************************************ //
    // da lasciare vuoti per non creare errori
    @Override
    public void onProgress(int progress) {}
    @Override
    public void onComplete(boolean success, String result) {}
}
