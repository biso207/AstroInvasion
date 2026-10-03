/*
 * Astro Invasion - class LocalizationManager -
 * Centralizes the loading and retrieval of localized game texts.
 *
 * Developed & Designed by BIGA ©2024-2026. All rights reserved.
 */

package sorgente.Localization;

import com.badlogic.gdx.Gdx;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class LocalizationManager {
    private static final String RESOURCE_PREFIX = "localization/messages_";
    private static final String RESOURCE_SUFFIX = ".properties";
    private static final SupportedLanguage FALLBACK_LANGUAGE = SupportedLanguage.ENGLISH;
    private static final LocalizationManager INSTANCE = new LocalizationManager();

    private SupportedLanguage activeLanguage = FALLBACK_LANGUAGE;
    private Map<String, String> activeTranslations = Collections.emptyMap();
    private Map<String, String> fallbackTranslations = Collections.emptyMap();

    private LocalizationManager() {
        setLanguage(FALLBACK_LANGUAGE);
    }

    public static LocalizationManager getInstance() {
        return INSTANCE;
    }

    public synchronized void setLanguage(SupportedLanguage language) {
        SupportedLanguage selectedLanguage = language == null ? FALLBACK_LANGUAGE : language;
        fallbackTranslations = loadTranslations(FALLBACK_LANGUAGE);
        activeTranslations = loadTranslations(selectedLanguage);
        activeLanguage = selectedLanguage;
    }

    public synchronized void setLanguage(String languageCode) {
        setLanguage(SupportedLanguage.fromCode(languageCode));
    }

    public synchronized SupportedLanguage getLanguage() {
        return activeLanguage;
    }

    public synchronized String get(String key) {
        if (key == null || key.isBlank()) {
            return "";
        }
        String value = activeTranslations.get(key);
        if (value == null) {
            value = fallbackTranslations.get(key);
        }
        return value == null ? key : value;
    }

    public synchronized String get(String key, Object... arguments) {
        return MessageFormat.format(get(key), arguments);
    }

    private Map<String, String> loadTranslations(SupportedLanguage language) {
        Properties properties = new Properties();
        String path = RESOURCE_PREFIX + language.getCode() + RESOURCE_SUFFIX;
        try (InputStream stream = Gdx.files.internal(path).read()) {
            properties.load(new java.io.InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (IOException | RuntimeException exception) {
            if (language != FALLBACK_LANGUAGE) {
                return loadTranslations(FALLBACK_LANGUAGE);
            }
        }
        Map<String, String> translations = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            translations.put(key, properties.getProperty(key));
        }
        return translations;
    }
}
