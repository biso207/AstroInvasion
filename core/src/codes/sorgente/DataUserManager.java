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
import com.badlogic.gdx.Gdx;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.JSONObject;
import java.util.Base64;

public class DataUserManager {

    private static String filePath; // percorso utente
    private static final Map<String, Object> progressi = new HashMap<>(); // hashmap per i dati

    // costruttore
    public DataUserManager(String username) {
        // percorso cartella utente
        filePath = getUserProgressPath(username);

        // debug percorso
        System.out.println("Percorso file user_data.dat -> " + filePath);

        // caricamento progressi utente
        loadProgresses();
    }

    // costruisce il percorso assoluto del file dell'utente
    private String getUserProgressPath(String username) {
        String basePath = UserDataPath.getBaseUserPath();
        return basePath + username + File.separator + "user_data.dat";
    }

    // carica i progressi da file (decodifica Base64 + parsing JSON)
    private void loadProgresses() {
        try {
            Path path = Path.of(filePath);
            if (!Files.exists(path)) return;

            String encoded = Files.readString(path);
            byte[] decodedBytes = Base64.getDecoder().decode(encoded);
            String jsonText = new String(decodedBytes);

            JSONObject json = new JSONObject(jsonText);
            for (String key : json.keySet()) {
                progressi.put(key, json.get(key));
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura dei progressi: " + e.getMessage());
        }
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

    // salva i progressi su file (JSON → Base64 → scrittura)
    public static void saveProgresses() {
        try {
            JSONObject json = new JSONObject(progressi);
            String encoded = Base64.getEncoder().encodeToString(json.toString(4).getBytes());
            Files.writeString(Path.of(filePath), encoded);
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio dei progressi: " + e.getMessage());
        }
    }

    // metodo extra per resettare i progressi
    public static void resetProgress() {
        progressi.clear();
        saveProgresses();
    }
}
