package sorgente.LogInSignUp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FirestoreStorage {
    private static ScheduledExecutorService heartbeatExecutor;
    private static int heartbeatFails = 0;
    private static final int MAX_FAILS = 5;

    private static final String PROJECT_ID = "astroinvasioncloud"; // <-- cambia col tuo project id
    private static final String DATABASE_URL = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + "/databases/(default)/documents/";

    private static String getAccessToken() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("../service-account.json"))
            .createScoped("https://www.googleapis.com/auth/cloud-platform");
        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }

    // metodo per controllare dell'esistenza del nickname sul server
    public static boolean checkUsernameExists(String username) throws IOException {
        String url = DATABASE_URL + "astroData/" + username;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", "Bearer " + getAccessToken())
            .get()
            .build();

        Response response = client.newCall(request).execute();
        int responseCode = response.code();
        response.close();

        return responseCode == 200;
    }

    // LOCK SULL'ACCESSO AL SERVER //
    // metodo per settare lo stato di accesso dell'utente al gioco
    public static void setUserLock(String username, boolean locked) throws IOException {
        String url = DATABASE_URL + "astroData/" + username;

        long timestamp = System.currentTimeMillis();

        Map<String, Object> fields = new HashMap<>();
        Map<String, Object> lockFields = new HashMap<>();
        lockFields.put("locked", Map.of("booleanValue", locked));
        lockFields.put("timestamp", Map.of("integerValue", timestamp));

        Map<String, Object> lockMap = new HashMap<>();
        lockMap.put("mapValue", Map.of("fields", lockFields));

        fields.put("lock", lockMap);
        Map<String, Object> document = new HashMap<>();
        document.put("fields", fields);

        Gson gson = new Gson();
        String json = gson.toJson(document);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", "Bearer " + getAccessToken())
            .patch(body)
            .build();

        Response response = client.newCall(request).execute();
        response.close();
    }

    // metodo per controllare se l'utente ha già effettuato l'accesso
    public static boolean isUserLocked(String username) throws IOException {
        String url = DATABASE_URL + "astroData/" + username;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", "Bearer " + getAccessToken())
            .get()
            .build();

        Response response = client.newCall(request).execute();
        String body = response.body().string();
        response.close();

        Map responseMap = new Gson().fromJson(body, Map.class);
        Map fields = (Map) responseMap.get("fields");

        if (fields == null || !fields.containsKey("lock")) {
            return false; // non esiste il campo lock, quindi non bloccato
        }

        try {
            Map lockMap = (Map) fields.get("lock");
            Map mapValue = (Map) lockMap.get("mapValue");
            Map lockFields = (Map) mapValue.get("fields");

            Map lockedMap = (Map) lockFields.get("locked");
            boolean locked = (boolean) lockedMap.get("booleanValue");

            Map timestampMap = (Map) lockFields.get("timestamp");
            long timestamp = Long.parseLong((String) timestampMap.get("integerValue"));

            long currentTimestamp = System.currentTimeMillis();
            long lockDurationMillis = 10000; // 10 secondi, regola come vuoi

            if (!locked) {
                return false;
            }

            // Se il lock è vecchio, consideriamo la sessione scaduta
            return (currentTimestamp - timestamp) < lockDurationMillis;

        } catch (Exception e) {
            e.printStackTrace();
            return false; // in caso di errore consideriamo l'utente non lockato per sicurezza
        }
    }

    public static void clearUserLock(String username) throws IOException {
        String url = DATABASE_URL + "astroData/" + username;

        Map<String, Object> fields = new HashMap<>();
        fields.put("lock", null);  // cancella il campo lock

        Map<String, Object> document = new HashMap<>();
        document.put("fields", fields);

        Gson gson = new Gson();
        String json = gson.toJson(document);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", "Bearer " + getAccessToken())
            .patch(body)
            .build();

        Response response = client.newCall(request).execute();
        response.close();
    }

    // PASSWORD //
    // metodo per recuperare la password utente
    public static String getPassword(String username) throws IOException {
        String url = DATABASE_URL + "astroData/" + username;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", "Bearer " + getAccessToken())
            .get()
            .build();

        Response response = client.newCall(request).execute();
        assert response.body() != null;
        String body = response.body().string();
        response.close();

        Map responseMap = new Gson().fromJson(body, Map.class);
        Map fields = (Map) responseMap.get("fields");
        Map pswField = (Map) fields.get("psw");
        String password = (String) pswField.get("stringValue");

        return password;
    }

    // metodo per salvare la password utente in cloud
    public static void savePassword(String username, String password) throws IOException {
        // URL con updateMask per aggiornare solo il campo "psw"
        String url = DATABASE_URL + "astroData/" + username + "?updateMask.fieldPaths=psw";

        Map<String, Object> fields = new HashMap<>();
        Map<String, Object> pswField = new HashMap<>();
        pswField.put("stringValue", password);
        fields.put("psw", pswField);

        Map<String, Object> document = new HashMap<>();
        document.put("fields", fields);

        Gson gson = new Gson();
        String json = gson.toJson(document);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", "Bearer " + getAccessToken())
            .patch(body)
            .build();

        Response response = client.newCall(request).execute();
        response.close();
    }

    // DATI //
    // salva il file .dat => esegue tutto con un thread separato dal thread main di gioco
    public static void uploadDatAsync(String username, String datBase64, LoadCallback callback) {
        new Thread(() -> {
            try {
                if (callback != null) callback.onProgress(10);

                // URL con updateMask per aggiornare solo il campo "dat"
                String url = DATABASE_URL + "astroData/" + username + "?updateMask.fieldPaths=dat";

                Map<String, Object> fields = new HashMap<>();
                Map<String, Object> dataField = new HashMap<>();
                dataField.put("stringValue", datBase64);
                fields.put("dat", dataField);

                Map<String, Object> document = new HashMap<>();
                document.put("fields", fields);

                Gson gson = new Gson();
                String json = gson.toJson(document);

                if (callback != null) callback.onProgress(30);

                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
                Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + getAccessToken())
                    .patch(body)
                    .build();

                Response response = client.newCall(request).execute();
                response.close();

                if (callback != null) {
                    callback.onProgress(100);
                    callback.onComplete(true, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) callback.onComplete(false, e.getMessage());
            }
        }).start();
    }

    // legge il file .dat => esegue tutto con un thread separato dal thread main di gioco
    public static void downloadDatAsync(String username, LoadCallback callback) {
        new Thread(() -> {
            try {
                callback.onProgress(10);

                String url = DATABASE_URL + "astroData/" + username;

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + getAccessToken())
                    .get()
                    .build();

                callback.onProgress(30);

                Response response = client.newCall(request).execute();
                assert response.body() != null;
                String body = response.body().string();
                response.close();

                callback.onProgress(60);

                Map responseMap = new Gson().fromJson(body, Map.class);
                Map fields = (Map) responseMap.get("fields");
                Map datField = (Map) fields.get("dat");
                String datBase64 = (String) datField.get("stringValue");

                callback.onProgress(100);
                callback.onComplete(true, datBase64);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onComplete(false, e.getMessage());
            }
        }).start();
    }
}
