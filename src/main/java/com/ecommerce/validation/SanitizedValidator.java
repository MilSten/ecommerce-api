package com.ecommerce.validation;

import com.ecommerce.util.SanitizationUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SanitizedValidator implements ConstraintValidator<Sanitized, String> {

    @Override
    public void initialize(Sanitized annotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String sanitized = SanitizationUtil.sanitizeString(value);
        return sanitized.equals(value);
    }
}