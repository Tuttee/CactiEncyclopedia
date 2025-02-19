package com.CactiEncyclopedia.validation.emailIsUnique;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = EmailIsUniqueValidator.class)
public @interface EmailIsUnique {
    String message() default "Email already exists!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
