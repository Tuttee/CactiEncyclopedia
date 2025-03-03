package com.CactiEncyclopedia.web;

import com.CactiEncyclopedia.domain.entities.Species;
import com.CactiEncyclopedia.domain.view.UserDetailsViewModel;
import com.CactiEncyclopedia.security.AuthenticationMetadata;
import com.CactiEncyclopedia.services.SpeciesService;
import com.CactiEncyclopedia.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/administration")
public class AdministrationController extends BaseController {

    private final SpeciesService speciesService;
    private final UserService userService;

    public AdministrationController(SpeciesService speciesService, UserService userService) {
        super();
        this.speciesService = speciesService;
        this.userService = userService;
    }

    @GetMapping("/species")
    public ModelAndView getSpeciesAdministration() {
        List<Species> approvedSpecies = speciesService.getAllApproved();
        List<Species> unapprovedSpecies = speciesService.getAllUnapproved();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("approvedSpecies", approvedSpecies);
        modelAndView.addObject("unapprovedSpecies", unapprovedSpecies);

        return super.view("species-administration", modelAndView);
    }

    @PatchMapping("/species/{id}")
    public ModelAndView approveSpecies(@PathVariable UUID id) {
        speciesService.approve(id);

        return super.redirect("/administration/species");
    }

    @DeleteMapping("/species/{id}")
    public ModelAndView deleteSpecies(@PathVariable UUID id) {
        speciesService.delete(id);

        return super.redirect("/administration/species");
    }

    @GetMapping("/users")
    public ModelAndView getUserAdministration(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        List<UserDetailsViewModel> users = userService.getAllUsersExceptLogged(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("users", users);

        return super.view("user-administration", modelAndView);

    }

    @PatchMapping("/users/{username}")
    public ModelAndView updateUser(@PathVariable String username, HttpSession session) {
        userService.updateUserRole(username);
        return super.redirect("/administration/users");
    }
}
