/*
 * Astro Invasion - class PinValidator -
 * Validates the format of user authentication PINs.
 *
 * Developed & Designed by BIGA ©2024-2026. All rights reserved.
 */
package sorgente.Authentication;

import java.util.regex.Pattern;

public final class PinValidator {
    public static final int MIN_LENGTH = 4;
    public static final int MAX_LENGTH = 8;
    private static final Pattern PIN_PATTERN = Pattern.compile("\\d{" + MIN_LENGTH + "," + MAX_LENGTH + "}");

    private PinValidator() {}

    public static boolean isValid(String pin) {
        return pin != null && PIN_PATTERN.matcher(pin).matches();
    }
}
