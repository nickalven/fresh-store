package com.nikos.freshstore.catalog.utils;

import java.text.Normalizer;

public class SlugUtils {

    public static String slugify(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Cannot slugify a null or blank string");
        }

        return Normalizer
                // Step 1: decompose accented characters into base + accent
                // "é" becomes "e" + combining accent character
                .normalize(input, Normalizer.Form.NFD)
                // Step 2: remove the combining accent characters (Unicode category M)
                // "e" + combining accent → "e"
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replace("&", "and")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }
}