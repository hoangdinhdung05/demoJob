package com.demoJob.demo.validator.EmailValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class CustomEmailValidator implements ConstraintValidator<ValidEmail, String> {

    private String[] allDomain;

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        this.allDomain = constraintAnnotation.allDomain();
    }

    /**
     * Implements the validation logic.
     * The state of {@code value} must not be altered.
     * <p>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || !value.contains("@")) return false;
        String domain = value.substring(value.indexOf("@") + 1);
        return Arrays.asList(allDomain).contains(domain);
    }
}