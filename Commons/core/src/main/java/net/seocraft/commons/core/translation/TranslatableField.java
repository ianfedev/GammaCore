package net.seocraft.commons.core.translation;

import com.google.inject.Inject;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class TranslatableField {

    private static final String BASE_LANG = "es";
    @Inject private Yaml parser;

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

    public String getField(String language, String field) {
        if (getUnspacedField(language, field).equalsIgnoreCase("null")) return field;
        return getUnspacedField(language, field) + " ";
    }

    private String getBaseField(String language, String field) {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("lang_" + language + ".yml");
        Map<String, Object> hashMap = parser.load(inputStream);
        return String.valueOf(hashMap.get(field));
    }

}

