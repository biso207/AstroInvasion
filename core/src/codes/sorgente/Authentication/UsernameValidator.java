/*
 * Astro Invasion - class UsernameValidator -
 * Applies shared username format, reservation, and multilingual moderation rules.
 *
 * Developed & Designed by BIGA ©2024-2026. All rights reserved.
 */
package sorgente.Authentication;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import sorgente.ProfanityFilter;

public final class UsernameValidator {
    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 16;
    private static final Pattern ALLOWED_CHARACTERS = Pattern.compile("(?!.*__)[A-Za-z0-9_]+");
    private static final Set<String> RESERVED_NAMES = Set.of(
        "admin", "administrator", "astroinvasion", "biga", "bigagames", "developer",
        "moderator", "moderation", "owner", "root", "server", "support", "system"
    );

    private UsernameValidator() {}

    public static UsernameValidationResult validate(String username) {
        if (username == null || username.isBlank()) {
            return UsernameValidationResult.of(UsernameValidationResult.Status.EMPTY);
        }
        if (username.length() < MIN_LENGTH) {
            return UsernameValidationResult.of(UsernameValidationResult.Status.TOO_SHORT);
        }
        if (username.length() > MAX_LENGTH) {
            return UsernameValidationResult.of(UsernameValidationResult.Status.TOO_LONG);
        }
        if (!ALLOWED_CHARACTERS.matcher(username).matches()
            || username.startsWith("_") || username.endsWith("_")) {
            return UsernameValidationResult.of(UsernameValidationResult.Status.INVALID_CHARACTERS);
        }

        String normalized = normalize(username);
        if (RESERVED_NAMES.contains(normalized)) {
            return UsernameValidationResult.of(UsernameValidationResult.Status.RESERVED);
        }
        if (!ProfanityFilter.isValidNickname(username)) {
            return UsernameValidationResult.of(UsernameValidationResult.Status.OFFENSIVE);
        }
        return UsernameValidationResult.of(UsernameValidationResult.Status.VALID);
    }

    public static String normalize(String username) {
        return Normalizer.normalize(username, Normalizer.Form.NFKC)
            .toLowerCase(Locale.ROOT)
            .replaceAll("[_-]+", "");
    }
}
