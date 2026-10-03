/*
 * Astro Invasion - class ProfanityFilter -
 * Loads multilingual moderation data and checks normalized usernames.
 *
 * Developed & Designed by BIGA ©2024-2026. All rights reserved.
 */
package sorgente;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class ProfanityFilter {
    private static final String[] LANGUAGES = {"it", "en", "fr", "de", "es"};
    private static final Set<String> BLACKLIST = new HashSet<>();
    private static boolean loaded;

    private ProfanityFilter() {}

    public static synchronized void loadBlacklists() {
        if (loaded) {
            return;
        }
        for (String language : LANGUAGES) {
            FileHandle file = Gdx.files.internal("badwords/" + language + ".txt");
            for (String word : file.readString("UTF-8").split("\\r?\\n")) {
                String normalized = normalize(word);
                if (!normalized.isBlank() && !normalized.startsWith("#")) {
                    BLACKLIST.add(normalized);
                }
            }
        }
        loaded = true;
    }

    public static boolean isValidNickname(String nickname) {
        loadBlacklists();
        String normalized = normalize(nickname);
        for (String bannedWord : BLACKLIST) {
            if ((bannedWord.length() <= 3 && normalized.equals(bannedWord))
                || (bannedWord.length() > 3 && normalized.contains(bannedWord))) {
                return false;
            }
        }
        return true;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);
        StringBuilder result = new StringBuilder(normalized.length());
        for (char character : normalized.toCharArray()) {
            switch (character) {
                case '4', '@' -> result.append('a');
                case '3', '€' -> result.append('e');
                case '1', '!' -> result.append('i');
                case '0' -> result.append('o');
                case '5', '$' -> result.append('s');
                default -> {
                    if (Character.isLetter(character)) {
                        result.append(character);
                    }
                }
            }
        }
        return result.toString().replaceAll("(.)\\1+", "$1");
    }
}
