package com.CactiEncyclopedia.validation.passwordMatch;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class PasswordMatcher implements ConstraintValidator<PasswordMatch, Object> {
    private String password;
    private String confirmPassword;
    private String message;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
        this.password = constraintAnnotation.password();
        this.confirmPassword = constraintAnnotation.confirmPassword();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);
        Object passwordValue = beanWrapper.getPropertyValue(password);
        Object confirmPasswordValue = beanWrapper.getPropertyValue(confirmPassword);

        if (passwordValue != null && passwordValue.equals(confirmPasswordValue)) {
            return true;
        }

        constraintValidatorContext.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(confirmPassword)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;
    }
}
