package net.seocraft.api.core.utils;

public class StringUtils {
    public static String capitalizeString(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }
}
