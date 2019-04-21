package net.seocraft.api.shared.serialization;

public class StringUtils {
    public static String capitalizeString(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }
}
