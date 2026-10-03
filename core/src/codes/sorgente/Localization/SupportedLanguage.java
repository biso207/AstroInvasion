/*
 * Astro Invasion - class SupportedLanguage -
 * Defines the languages supported by the localization system.
 *
 * Developed & Designed by BIGA ©2024-2026. All rights reserved.
 */

package sorgente.Localization;

import java.util.Locale;

public enum SupportedLanguage {
    ENGLISH("en", Locale.ENGLISH),
    ITALIAN("it", Locale.ITALIAN),
    FRENCH("fr", Locale.FRENCH),
    GERMAN("de", Locale.GERMAN),
    SPANISH("es", new Locale("es"));

    private final String code;
    private final Locale locale;

    SupportedLanguage(String code, Locale locale) {
        this.code = code;
        this.locale = locale;
    }

    public String getCode() {
        return code;
    }

    public Locale getLocale() {
        return locale;
    }

    public static SupportedLanguage fromCode(String code) {
        if (code != null) {
            for (SupportedLanguage language : values()) {
                if (language.code.equalsIgnoreCase(code.trim())) {
                    return language;
                }
            }
        }
        return ENGLISH;
    }
}
