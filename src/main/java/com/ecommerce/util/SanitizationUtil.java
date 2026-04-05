// util/SanitizationUtil.java
package com.ecommerce.util;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class SanitizationUtil {

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.LINKS);

    /**
     * Удалить опасные HTML теги
     */
    public static String sanitizeHtml(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return POLICY.sanitize(input);
    }

    /**
     * Удалить специальные символы
     */
    public static String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[<>\"'%;()&+]", "");
    }

    /**
     * Обезопасить SQL строку (дополнительная защита)
     */
    public static String sanitizeSql(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("([';\"\\\\])", "\\\\$1");
    }
}