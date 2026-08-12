package com.BackEnd.Master.GYM.util;

import java.text.Normalizer;
import java.util.Locale;

public final class UsernameNormalizer {

    private UsernameNormalizer() {
    }

    // "Mohamed Ben Salah" -> "mohamed.ben.salah"
    public static String normalize(String displayName) {
        String noAccents = Normalizer.normalize(displayName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String lower = noAccents.toLowerCase(Locale.ROOT);
        String collapsed = lower.replaceAll("[^a-z0-9]+", ".");
        String trimmed = collapsed.replaceAll("^\\.+|\\.+$", "");
        return trimmed.isEmpty() ? "user" : trimmed;
    }
}
