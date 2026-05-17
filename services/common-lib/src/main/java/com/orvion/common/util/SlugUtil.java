package com.orvion.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class SlugUtil {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern HYPHENS = Pattern.compile("-{2,}");

    private SlugUtil() {
    }

    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        String slug = withoutAccents.toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("[\\s]", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");

        return slug;
    }

    public static String toSlug(String input, String defaultValue) {
        String slug = toSlug(input);
        return slug.isEmpty() ? defaultValue : slug;
    }
}
