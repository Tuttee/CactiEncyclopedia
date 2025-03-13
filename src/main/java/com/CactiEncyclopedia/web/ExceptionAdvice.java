package com.CactiEncyclopedia.web;

import com.CactiEncyclopedia.domain.binding.UserRegisterDto;
import com.CactiEncyclopedia.exception.EmailAlreadyExistsException;
import com.CactiEncyclopedia.exception.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public String handleUsernameAlreadyExistsException(RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("usernameAlreadyExistsMessage", "Username is already in use!");

        return "redirect:/auth/register";
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailAlreadyExistsException(RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("emailAlreadyExistsMessage", "Email is already in use!");

        return "redirect:/auth/register";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            AccessDeniedException.class,
            NoResourceFoundException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class})
    public ModelAndView handleNotFoundException() {
        return new ModelAndView("not-found");
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public ModelAndView handleAnyException(Exception e) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("internal-server-error");
        modelAndView.addObject("error-message", e.getClass().getSimpleName());

        return modelAndView;
    }
}
