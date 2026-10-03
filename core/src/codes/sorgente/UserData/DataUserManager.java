/*
 * Astro Invasion - class DataUserManager -
 * Loads, stores, and persists the active user's progress and preferences.
 *
 * Developed & Designed by BIGA ©2024-2026. All rights reserved.
 */

/*
 * This class loads user progress from the cloud once per session and keeps the
 * active values in memory. Changes are persisted whenever a value is updated.
 */

// package di appartenenza
package sorgente.UserData;

// import codici e librerie
import java.io.*;
import java.util.*;
import org.json.JSONObject;
import sorgente.Authentication.AuthAlgorithms;
import sorgente.Authentication.LoadingData.GlobalProgressManager;
import sorgente.Authentication.LoadingData.LoadCallback;
import sorgente.Localization.LocalizationManager;
import sorgente.Localization.SupportedLanguage;

import java.util.Base64;

public class DataUserManager implements LoadCallback {
    private static final String LANGUAGE_KEY = "language";
    private static final Map<String, Object> progress = new HashMap<>();

    // Load user progress by decoding Base64 and parsing JSON.
    public static void loadProgresses() throws IOException {
        // caricamento da cloud in remoto
        CloudStorageManager.downloadDatAsync(AuthAlgorithms.nickname, new LoadCallback() {
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

                    // Store downloaded progress and preferences in memory.
                    JSONObject json = new JSONObject(jsonText);
                    for (String key : json.keySet()) {
                        progress.put(key, json.get(key));
                    }
                    LocalizationManager.getInstance().setLanguage(getLanguage());
                } else {
                    System.err.println("Unable to download user progress: " + result);
                }
            }
        });
    }

    // Save progress to the remote server after encoding the JSON as Base64.
    public static void saveProgresses() {
        JSONObject json = new JSONObject(progress);
        String encoded = Base64.getEncoder().encodeToString(json.toString(4).getBytes());

        // salvataggio dati utente in cloud remoto
        CloudStorageManager.uploadDatAsync(AuthAlgorithms.nickname, encoded, new LoadCallback() {
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

    // Retrieve one progress value.
    public static Object getProgress(String name) {
        return progress.getOrDefault(name, null);
    }

    // Update one value and persist all progress.
    public static void setProgress(String name, Object value) {
        progress.put(name, value);
        saveProgresses();
    }

    public static String getLanguage() {
        Object value = progress.get(LANGUAGE_KEY);
        return SupportedLanguage.fromCode(value instanceof String ? (String) value : null).getCode();
    }

    public static void setLanguage(String languageCode) {
        SupportedLanguage language = SupportedLanguage.fromCode(languageCode);
        progress.put(LANGUAGE_KEY, language.getCode());
        LocalizationManager.getInstance().setLanguage(language);
        saveProgresses();
    }

    // Clear the in-memory session state after logout.
    public static void resetProgress() {
        progress.clear();
        LocalizationManager.getInstance().setLanguage(SupportedLanguage.ENGLISH);
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
