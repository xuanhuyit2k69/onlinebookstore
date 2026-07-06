package vn.edu.xyz.olms.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class TextUtils {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");

    private TextUtils() {}

    public static String removeAccents(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return DIACRITICS.matcher(normalized).replaceAll("").toLowerCase();
    }
}
