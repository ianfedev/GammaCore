package net.seocraft.commons.core.translation;

import com.google.inject.Inject;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Map;

public class TranslatableField {

    @Inject private Yaml parser;

    public String getUnspacedField(String language, String field) {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("lang_" + language + ".yml");
        Map<String, Object> hashMap = parser.load(inputStream);
        String response = String.valueOf(hashMap.get(field));
        if (response.equalsIgnoreCase("null")) return field;
        return response;
    }

    public String getField(String language, String field) {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("lang_" + language + ".yml");
        Map<String, Object> hashMap = parser.load(inputStream);
        String response = String.valueOf(hashMap.get(field));
        if (response.equalsIgnoreCase("null")) return field;
        return hashMap.get(field) + " ";
    }

}
