package com.CactiEncyclopedia.web;


import com.CactiEncyclopedia.domain.binding.UserRegisterDto;
import com.CactiEncyclopedia.exception.UsernameAlreadyExistsException;
import com.CactiEncyclopedia.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {
    private final UserService userService;

    @GetMapping("/register")
    public ModelAndView getRegister() {
        return super.view("register");
    }

    @PostMapping("/register")
    public ModelAndView postRegister(@Valid @ModelAttribute("userRegister") UserRegisterDto userRegisterDto,
                                     BindingResult bindingResult,
                                     ModelAndView modelAndView) {

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("userRegister", userRegisterDto);
            return super.view("register", modelAndView);
        }

        this.userService.register(userRegisterDto);

        return super.view("login");
    }

    @GetMapping("/login")
    public ModelAndView getLogin(@RequestParam(value = "error", required = false) String errorParam) {

        ModelAndView modelAndView = new ModelAndView();
        if (errorParam != null) {
            modelAndView.addObject("error", "Incorrect username or password!");
        }

        return super.view("login", modelAndView);
    }

    @ModelAttribute("userRegister")
    public UserRegisterDto userRegisterBindingModel() {
        return new UserRegisterDto();
    }
}
