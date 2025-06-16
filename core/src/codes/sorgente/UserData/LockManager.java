/*
Astro Invasion - class LockManager -
Gestisce il lock sull'accesso dell'utente al server secondo un sistema di refresh del momento di accesso
Developed by BIGA©. All rights reserved.
*/

// package di appartenenza
package sorgente.UserData;

// import librerie e codici
import java.io.IOException;
import java.util.concurrent.*;

public class LockManager {

    private static ScheduledExecutorService heartbeatExecutor;
    private static ScheduledExecutorService recoveryExecutor;
    private static final int MAX_FAILS = 5;
    private static int heartbeatFails = 0;

    // avvio heartbeat - by ChatGPT
    public static void startHeartbeat(String username) {
        stopHeartbeat(); // sicurezza
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(createDaemonThreadFactory());
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            try {
                CloudStorageManager.setUserLock(username, true);
                heartbeatFails = 0;
            } catch (IOException e) {
                heartbeatFails++;
                System.out.println("Heartbeat fallito (" + heartbeatFails + ")");
                if (heartbeatFails >= MAX_FAILS) {
                    System.out.println("Connessione persa, avvio recovery");
                    stopHeartbeat();
                    startRecovery(username);
                }
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    // stop heartbeat - byChatGPT
    public static void stopHeartbeat() {
        if (heartbeatExecutor != null && !heartbeatExecutor.isShutdown()) {
            heartbeatExecutor.shutdownNow();
        }
    }

    // avvio recovery - by ChatGPT
    public static void startRecovery(String username) {
        stopRecovery(); // sicurezza
        recoveryExecutor = Executors.newSingleThreadScheduledExecutor(createDaemonThreadFactory());
        recoveryExecutor.scheduleAtFixedRate(() -> {
            try {
                if (!CloudStorageManager.isUserLocked(username)) {
                    System.out.println("Il lock è scaduto. Permettiamo rilogin.");
                    stopRecovery();
                    startHeartbeat(username);
                } else {
                    System.out.println("Lock ancora attivo, ma connessione ripristinata. Riprendo heartbeat.");
                    stopRecovery();
                    startHeartbeat(username);
                }
            } catch (IOException e) {
                System.out.println("Ancora problemi di connessione...");
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    // stop recovery - by ChatGPT
    public static void stopRecovery() {
        if (recoveryExecutor != null && !recoveryExecutor.isShutdown()) {
            recoveryExecutor.shutdownNow();
        }
    }

    // metodo per interrompere ogni thread => da chiamare nel dispose() del main - by ChatGPT
    public static void shutdownAll() {
        stopHeartbeat();
        stopRecovery();
    }

    // factory per creare thread daemon - by ChatGPT
    private static ThreadFactory createDaemonThreadFactory() {
        return runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        };
    }
}

