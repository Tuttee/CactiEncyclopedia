package com.CactiEncyclopedia.validation.userAndPasswordMatch;

import com.CactiEncyclopedia.domain.binding.UserLoginBindingModel;
import com.CactiEncyclopedia.services.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UserAndPasswordMatcher implements ConstraintValidator<UserAndPasswordMatch, UserLoginBindingModel> {
    private final UserService userService;

    @Autowired
    public UserAndPasswordMatcher(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(UserAndPasswordMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserLoginBindingModel userLoginBindingModel, ConstraintValidatorContext constraintValidatorContext) {
        return this.userService.validateLogin(userLoginBindingModel);
    }
}
