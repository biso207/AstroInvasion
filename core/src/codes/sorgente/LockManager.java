package sorgente;

import java.io.IOException;
import java.util.concurrent.*;
import com.google.gson.*;
import okhttp3.*;
import sorgente.LogInSignUp.FirestoreStorage;

public class LockManager {

    private static ScheduledExecutorService heartbeatExecutor;
    private static ScheduledExecutorService recoveryExecutor;
    private static final int MAX_FAILS = 5;
    private static int heartbeatFails = 0;

    // Avvio heartbeat
    public static void startHeartbeat(String username) {
        stopHeartbeat(); // sicurezza
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(createDaemonThreadFactory());
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            try {
                FirestoreStorage.setUserLock(username, true);
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

    // Stop heartbeat
    public static void stopHeartbeat() {
        if (heartbeatExecutor != null && !heartbeatExecutor.isShutdown()) {
            heartbeatExecutor.shutdownNow();
        }
    }

    // Avvio recovery
    public static void startRecovery(String username) {
        stopRecovery(); // sicurezza
        recoveryExecutor = Executors.newSingleThreadScheduledExecutor(createDaemonThreadFactory());
        recoveryExecutor.scheduleAtFixedRate(() -> {
            try {
                if (!FirestoreStorage.isUserLocked(username)) {
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

    // Stop recovery
    public static void stopRecovery() {
        if (recoveryExecutor != null && !recoveryExecutor.isShutdown()) {
            recoveryExecutor.shutdownNow();
        }
    }

    // Chiamare questo in uscita dal gioco per killare tutto
    public static void shutdownAll() {
        stopHeartbeat();
        stopRecovery();
    }

    // Factory per creare thread daemon
    private static ThreadFactory createDaemonThreadFactory() {
        return runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        };
    }
}

