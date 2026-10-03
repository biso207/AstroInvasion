/*
 * Astro Invasion - class PinSecurityService -
 * Hashes and verifies authentication PINs without exposing plaintext values.
 *
 * Developed & Designed by BIGA ©2024-2026. All rights reserved.
 */
package sorgente.Authentication;

import org.mindrot.jbcrypt.BCrypt;

public final class PinSecurityService {
    private PinSecurityService() {}

    public static String hash(String pin) {
        requireValidPin(pin);
        return BCrypt.hashpw(pin, BCrypt.gensalt());
    }

    public static boolean matches(String pin, String storedHash) {
        return PinValidator.isValid(pin)
            && storedHash != null
            && !storedHash.isBlank()
            && BCrypt.checkpw(pin, storedHash);
    }

    private static void requireValidPin(String pin) {
        if (!PinValidator.isValid(pin)) {
            throw new IllegalArgumentException("PIN must contain 4 to 8 digits");
        }
    }
}
