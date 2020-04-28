package net.seocraft.commons.core.translation;

import com.google.inject.Inject;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Translation system used to get default language fields. Every end-user message MUST be obtained trough translations.
 */
public class TranslatableField {

    /**
     * Remember to work in a base language called lang_xx.yml according to i18n translation pattern,
     * it will be used in case to be missing one language, otherwise any of the requested objects will return the base field.
     */
    private static final String BASE_LANG = "es";
    @Inject private Yaml parser;
    private final Map<String, String> cache = new HashMap<>();

    /**
     * Obtain translated string without any space after it
     * @param language obtained from user
     * @param field to be translated
     * @return translated string
     */
    public String getUnspacedField(String language, String field) {
        String response = getBaseField(language, field);
        if (response.equalsIgnoreCase("null")) {
            if (language.equalsIgnoreCase(BASE_LANG)) {
                response = getBaseField(BASE_LANG, field);
                if (response.equalsIgnoreCase("null")) return field;
            } else {
                return field;
            }
        }
        return response;
    }

    /**
     * Obtain translated string an space after it
     * @param language obtained from user
     * @param field to be translated
     * @return translated string
     */
    public String getField(String language, String field) {
        if (getUnspacedField(language, field).equalsIgnoreCase("null")) return field;
        return getUnspacedField(language, field) + " ";
    }

    private String getBaseField(String language, String field) {

        if(cache.containsKey(field)) {
            return cache.get(field);
        }

        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("lang_" + language + ".yml");
        Map<String, Object> hashMap = parser.load(inputStream);
        String value = String.valueOf(hashMap.get(field));

        cache.put(field, value);

        return value;
    }

}
