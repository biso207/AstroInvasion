/*
Astro Invasion - class NotificaAvvio -
Permette di mandare una notifica di avvio del gioco a un bot telegram
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.LogInSignUp;

// import librerie e codici
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

public class NotificaAvvio {

    // metodo per inviare il messaggio
    public void sendMessage() {
        String token = "8105519085:AAEcmzhYSLOmn0qSASe8YCD_UfYi_eDsTM8"; // token del bot
        String chatId = "5191176873"; // chat_id

        try {
            //String message = AuthAlgorithms.nickname + " ha avviato il gioco\nIP: " + InetAddress.getLocalHost(); // messaggio
            String message = AuthAlgorithms.nickname + " ha avviato il gioco"; // messaggio
            String urlString = "https://api.telegram.org/bot" + token + "/sendMessage" +
                "?chat_id=" + chatId +
                "&text=" + URLEncoder.encode(message, StandardCharsets.UTF_8);

            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            System.out.println("Messaggio Telegram inviato. Codice: " + responseCode); // messaggio di debug
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
