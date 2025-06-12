package sorgente;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserDataPath {

    private static final String GAME_FOLDER = "AstroInvasion";

    // metodo per recuperare la cartella utente
    public static String getBaseUserPath() {
        String userHome = System.getProperty("user.home");
        return userHome + File.separator + ".astroinvasion" + File.separator + "data" + File.separator;
    }

    // metodo per recuperare il percorso dati utente in base all'os
    public static String getUserPath(String username) {
        return getBaseUserPath() + username + File.separator;
    }

    // metodo per recuperare il percorso del file di verifica presenza utenti
    public static Path getIsUserFilePath() {
        return Path.of(UserDataPath.getBaseUserPath() + "is_user.txt");
    }
}
