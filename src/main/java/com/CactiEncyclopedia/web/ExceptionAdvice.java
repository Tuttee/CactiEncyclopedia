package com.CactiEncyclopedia.web;

import com.CactiEncyclopedia.domain.binding.UserRegisterDto;
import com.CactiEncyclopedia.exception.EmailAlreadyExistsException;
import com.CactiEncyclopedia.exception.UsernameAlreadyExistsException;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
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
    public String handleUsernameAlreadyExistsException(RedirectAttributes redirectAttributes,
                                                       HttpServletRequest request,
                                                       UsernameAlreadyExistsException e) {
        UserRegisterDto userRegisterDto = getUserRegisterDtoFromHttpRequest(request);

        redirectAttributes.addFlashAttribute("usernameAlreadyExistsMessage", e.getMessage());
        redirectAttributes.addFlashAttribute("userRegister", userRegisterDto);

        return "redirect:/auth/register";
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailAlreadyExistsException(RedirectAttributes redirectAttributes,
                                                    HttpServletRequest request,
                                                    EmailAlreadyExistsException e) {
        UserRegisterDto userRegisterDto = getUserRegisterDtoFromHttpRequest(request);

        redirectAttributes.addFlashAttribute("emailAlreadyExistsMessage", e.getMessage());
        redirectAttributes.addFlashAttribute("userRegister", userRegisterDto);


        return "redirect:/auth/register";
    }

    @ExceptionHandler(FeignException.class)
    public String handleEmailAlreadyExistsException(FeignException duplicateFactException,
                                                    RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("error", "Fact cannot be added! " + duplicateFactException.getMessage());

        return "redirect:/add-fact";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            AccessDeniedException.class,
            NoResourceFoundException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class,
            NoSuchElementException.class})
    public ModelAndView handleNotFoundException() {
        return new ModelAndView("not-found");
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public ModelAndView handleAnyException(Exception e) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("internal-server-error");
        modelAndView.addObject("error", e.getClass().getSimpleName());

        return modelAndView;
    }

    private UserRegisterDto getUserRegisterDtoFromHttpRequest(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = "";
        String confirmPassword = "";
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        return new UserRegisterDto(username, password, confirmPassword, email, firstName, lastName);

    }
}
