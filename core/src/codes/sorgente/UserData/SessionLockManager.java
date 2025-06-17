package sorgente.UserData;

import java.io.IOException;
import java.util.concurrent.*;

public class SessionLockManager {

    private static ScheduledExecutorService heartbeatExecutor;
    private static ScheduledExecutorService recoveryExecutor;

    private static final long HEARTBEAT_INTERVAL_MS = 5_000;  // 5 secondi => tempo di aggiornamento del timestamp
    private static final long RECOVERY_INTERVAL_MS = 2_500;    // 2.5 secondi
    private static final int MAX_FAILS = 3;

    private static int heartbeatFails = 0;
    private static String currentUsername;

    // metodo per iniziare ad aggiornare il timestamp ogni 10 secondi
    public static void startHeartbeat(String username) {
        stopHeartbeat(); // sicurezza
        stopRecovery();

        currentUsername = username;

        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(createDaemonThreadFactory());
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            try {
                LockStatusManager.setLockStatus(username, true);
                heartbeatFails = 0;
                System.out.println("Heartbeat OK");
            } catch (IOException e) {
                heartbeatFails++;
                System.out.println("Heartbeat fallito (" + heartbeatFails + ")");
                if (heartbeatFails >= MAX_FAILS) {
                    System.out.println("Connessione persa, avvio recovery");
                    stopHeartbeat();
                    startRecovery(username);
                }
            }
        }, 0, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    // serve per rilasciare il lock in caso di crash
    private static void startRecovery(String username) {
        stopRecovery(); // sicurezza
        recoveryExecutor = Executors.newSingleThreadScheduledExecutor(createDaemonThreadFactory());

        recoveryExecutor.scheduleAtFixedRate(() -> {
            boolean expired;
            try {
                expired = LockStatusManager.isSessionExpired(username);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // sessione scaduta => l'utente può effettuare l'accesso
            if (expired) {
                System.out.println("Lock scaduto. Permettiamo nuova sessione.");
                stopRecovery();
                startHeartbeat(username);
            }
        }, 0, RECOVERY_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    // metodo per rilasciare il lock e fermare l'heartbeat => va chiamato nel dispose() del Main
    public static void shutdownAll() {
        try {
            stopHeartbeat();
            stopRecovery();
            if (currentUsername != null) {
                LockStatusManager.setLockStatus(currentUsername, false);
                System.out.println("Sessione terminata, lock rilasciato");
            }
        } catch (IOException e) {
            System.out.println("Errore durante il rilascio del lock: " + e.getMessage());
        }
    }

    // metodo per interrompere l'heartbeat
    private static void stopHeartbeat() {
        if (heartbeatExecutor != null && !heartbeatExecutor.isShutdown()) {
            heartbeatExecutor.shutdownNow();
        }
    }
    // metodo per interrompere il rilascio del lock
    private static void stopRecovery() {
        if (recoveryExecutor != null && !recoveryExecutor.isShutdown()) {
            recoveryExecutor.shutdownNow();
        }
    }

    private static ThreadFactory createDaemonThreadFactory() {
        return runnable -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            return thread;
        };
    }
}

