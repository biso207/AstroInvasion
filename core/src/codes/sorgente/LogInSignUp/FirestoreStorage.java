package sorgente.LogInSignUp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FirestoreStorage {

    private static final String PROJECT_ID = "astroinvasioncloud"; // <-- cambia col tuo project id
    private static final String DATABASE_URL = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + "/databases/(default)/documents/";

    private static String getAccessToken() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("../service-account.json"))
            .createScoped("https://www.googleapis.com/auth/cloud-platform");
        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }

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
        String url = DATABASE_URL + "astroData/" + username;

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
        System.out.println("Password saved: " + response.code());
        response.close();
    }


    // salva il file .dat => esegue tutto con un thread separato dal thread main di gioco
    public static void uploadDatAsync(String username, String datBase64, LoadCallback callback) {
        new Thread(() -> {
            try {
                callback.onProgress(10);

                String url = DATABASE_URL + "astroData/" + username;

                Map<String, Object> fields = new HashMap<>();
                Map<String, Object> dataField = new HashMap<>();
                dataField.put("stringValue", datBase64);
                fields.put("dat", dataField);
                Map<String, Object> document = new HashMap<>();
                document.put("fields", fields);
                Gson gson = new Gson();
                String json = gson.toJson(document);

                callback.onProgress(30);

                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
                Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + getAccessToken())
                    .patch(body)
                    .build();

                Response response = client.newCall(request).execute();
                System.out.println("UPLOAD: " + response.code());
                response.close();

                callback.onProgress(100);
                callback.onComplete(true, null);

            } catch (Exception e) {
                e.printStackTrace();
                callback.onComplete(false, e.getMessage());
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
