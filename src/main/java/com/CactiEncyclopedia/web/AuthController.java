package com.CactiEncyclopedia.web;


import com.CactiEncyclopedia.domain.binding.UserLoginBindingModel;
import com.CactiEncyclopedia.domain.binding.UserRegisterBindingModel;
import com.CactiEncyclopedia.domain.entities.User;
import com.CactiEncyclopedia.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
public class AuthController extends BaseController{
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public ModelAndView getRegister() {
        return super.view("register");
    }

    @PostMapping("/register")
    public ModelAndView postRegister(@Valid @ModelAttribute("userRegister") UserRegisterBindingModel userRegisterBindingModel,
                                     BindingResult bindingResult,
                                     ModelAndView modelAndView) {

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("userRegister", userRegisterBindingModel);
            return super.view("register", modelAndView);
        }

        this.userService.register(userRegisterBindingModel);

        return super.view("login");
    }

    @GetMapping("/login")
    public ModelAndView getLogin() {
        return super.view("login");
    }

    @PostMapping("/login")
    public ModelAndView postRegister(@Valid @ModelAttribute("userLogin") UserLoginBindingModel userLoginBindingModel,
                                     BindingResult bindingResult,
                                     HttpSession httpSession,
                                     ModelAndView modelAndView) {

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("userLogin", userLoginBindingModel);
            return super.view("login", modelAndView);
        }

        User user = this.userService.login(userLoginBindingModel);

        httpSession.setAttribute("user_id", user.getId());

        return super.redirect("/");
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session) {
        session.invalidate();
        return super.redirect("/");
    }

    @ModelAttribute("userRegister")
    public UserRegisterBindingModel userRegisterBindingModel() {
        return new UserRegisterBindingModel();
    }

    @ModelAttribute("userLogin")
    public UserLoginBindingModel userLoginBindingModel() {
        return new UserLoginBindingModel();
    }
}
