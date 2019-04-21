package net.seocraft.commons.core.translations;

import com.google.inject.Inject;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Map;

public class TranslatableField {

    @Inject Yaml parser;

    public String getUnspacedField(String language, String field) {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("lang_" + language + ".yml");
        Map<String, Object> hashMap = parser.load(inputStream);
        return String.valueOf(hashMap.get(field));
    }

    public String getField(String language, String field) {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("lang_" + language + ".yml");
        Map<String, Object> hashMap = parser.load(inputStream);
        return hashMap.get(field) + " ";
    }

}
