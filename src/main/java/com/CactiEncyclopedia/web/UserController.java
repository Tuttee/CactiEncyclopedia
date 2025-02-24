package com.CactiEncyclopedia.web;

import com.CactiEncyclopedia.domain.view.UserDetailsViewModel;
import com.CactiEncyclopedia.services.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController extends BaseController {
    private final UserService userService;

    @GetMapping("/my-profile")
    public ModelAndView getMyProfile(HttpSession session) {
        UserDetailsViewModel userDetailsViewModel = userService.getLoggedUserDetails(session.getAttribute("user_id").toString());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("userProfile", userDetailsViewModel);

        return super.view("my-profile", modelAndView);
    }
}
