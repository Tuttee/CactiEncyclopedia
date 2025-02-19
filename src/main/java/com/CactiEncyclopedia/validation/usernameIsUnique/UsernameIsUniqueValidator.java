package com.CactiEncyclopedia.validation.usernameIsUnique;

import com.CactiEncyclopedia.services.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UsernameIsUniqueValidator implements ConstraintValidator<UsernameIsUnique, String> {
    private final UserService userService;

    @Autowired
    public UsernameIsUniqueValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(UsernameIsUnique constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        return !this.userService.existsByUsername(username);
    }
}
