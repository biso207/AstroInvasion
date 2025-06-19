package sorgente.UserData;

import sorgente.Lobby.UIManager;

import java.io.IOException;
import java.util.concurrent.*;

import static sorgente.LogInSignUp.AuthAlgorithms.checkInternetConnection;

public class SessionLockManager {

    private static ScheduledExecutorService heartbeatExecutor;
    private static ScheduledExecutorService recoveryExecutor;

    private static final long HEARTBEAT_INTERVAL_MS = 5_000;  // 5 secondi => tempo di aggiornamento del timestamp
    private static final long RECOVERY_INTERVAL_MS = 2_500;    // 2.5 secondi
    private static String currentUsername;
    private static int cont_hb=0;

    // metodo per iniziare ad aggiornare il timestamp ogni 10 secondi
    public static void startHeartbeat(String username) {
        stopHeartbeat(); // sicurezza
        stopRecovery();

        currentUsername = username;

        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(createDaemonThreadFactory());
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            // internet assente
            if (!checkInternetConnection()) UIManager.isConnected=false; // connessione off
            else {
                UIManager.isConnected=true; // connessione on

                // blocco try-catch per settare il lock
                try {
                    LockStatusManager.setLockStatus(username, true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // debug
                cont_hb++; // numero di heartbeats
                System.out.println("Heartbeat " + cont_hb + " OK");
            }
        }, 0, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    // metodo per rilasciare il lock e fermare l'heartbeat => va chiamato nel dispose() del Main del core
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

