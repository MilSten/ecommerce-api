package com.ecommerce.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtil {

    private static final Pattern PATTERN = Pattern.compile("[^\\w-]+");

    public static String generate(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Нормализуем unicode символы
        String normalized = Normalizer
                .normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Переводим в нижний регистр
        normalized = normalized.toLowerCase(Locale.ENGLISH);

        // Заменяем пробелы и спецсимволы на дефисы
        normalized = PATTERN.matcher(normalized).replaceAll("-");

        // Убираем множественные дефисы
        normalized = normalized.replaceAll("-+", "-");

        // Убираем дефисы в начале и конце
        return normalized.replaceAll("^-|-$", "");
    }
}