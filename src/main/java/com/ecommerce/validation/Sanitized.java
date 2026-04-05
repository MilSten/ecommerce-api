package com.ecommerce.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SanitizedValidator.class)
@Documented
public @interface Sanitized {
    String message() default "Invalid input format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}