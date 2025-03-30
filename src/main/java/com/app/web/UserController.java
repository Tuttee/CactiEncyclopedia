package com.app.web;

import com.app.domain.view.UserDetailsViewModel;
import com.app.security.AuthenticationMetadata;
import com.app.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ModelAndView getMyProfile(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        UserDetailsViewModel userDetailsViewModel = userService.getLoggedUserDetails(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("userProfile", userDetailsViewModel);

        return super.view("my-profile", modelAndView);
    }
}
