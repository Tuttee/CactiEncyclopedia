package com.CactiEncyclopedia.validation.emailIsUnique;

import com.CactiEncyclopedia.services.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailIsUniqueValidator implements ConstraintValidator<EmailIsUnique, String> {
    private final UserService userService;

    @Autowired
    public EmailIsUniqueValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(EmailIsUnique constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return !this.userService.existsByEmail(email);
    }
}
