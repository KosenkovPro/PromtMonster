package pro.kosenkov.promtmonster;

import java.util.Map;

public class Photo {

    private final String filename;
    private final String uri;
    private final String promptEn;
    private final Map<String, String> translations;

    public Photo(String filename, String uri, String promptEn, Map<String, String> translations) {
        this.filename = filename;
        this.uri = uri;
        this.promptEn = promptEn != null ? promptEn : "";
        this.translations = translations;
    }

    public String getFilename() { return filename; }
    public String getUri()      { return uri; }
    public String getPromptEn() { return promptEn; }

    /**
     * Returns the prompt for the given language code.
     * Falls back to English if translation is missing.
     *
     * @param langCode e.g. "ru", "zh", "hi", "de" …
     */
    public String getPromptForLocale(String langCode) {
        if (langCode == null || langCode.isEmpty() || langCode.equals("en")) {
            return promptEn;
        }
        // Android uses "in" for Indonesian locale; map to "id" used in JSON
        String key = langCode.equals("in") ? "id" : langCode;
        if (translations != null) {
            String result = translations.get(key);
            if (result != null && !result.isEmpty()) return result;
            // Try base language (e.g. "zh-rCN" -> "zh")
            if (key.length() > 2) {
                result = translations.get(key.substring(0, 2));
                if (result != null && !result.isEmpty()) return result;
            }
        }
        return promptEn;
    }
}
