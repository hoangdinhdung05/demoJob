package com.demoJob.demo.validator.EmailValidator;

import jakarta.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CustomEmailValidator.class)
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {
    String message() default "Invalid email format";
    Class<?>[] groups() default {};
    Class<? extends jakarta.validation.Payload>[] payload() default {};
    String[] allDomain() default {"gmail.com", "yahoo.com"};
}
