package com.CactiEncyclopedia.web;

import com.CactiEncyclopedia.domain.binding.AddFamilyBindingModel;
import com.CactiEncyclopedia.domain.binding.AddSpeciesBindingModel;
import com.CactiEncyclopedia.domain.entities.Family;
import com.CactiEncyclopedia.domain.entities.Species;
import com.CactiEncyclopedia.security.AuthenticationMetadata;
import com.CactiEncyclopedia.services.FamilyService;
import com.CactiEncyclopedia.services.SpeciesService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/catalog")
public class CatalogController extends BaseController {
    private final FamilyService familyService;
    private final SpeciesService speciesService;

    public CatalogController(FamilyService familyService, SpeciesService speciesService) {
        super();
        this.familyService = familyService;
        this.speciesService = speciesService;
    }

    @GetMapping
    public ModelAndView catalog() {
        List<Family> allFamilies = familyService.getAllFamiliesWithSpecies();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("families", allFamilies);

        return super.view("catalog", modelAndView);
    }

    @GetMapping("/{familyName}")
    public ModelAndView getFamily(@PathVariable String familyName) {
        List<Species> speciesByFamily = speciesService.getApprovedSpeciesByFamily(familyName);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("species", speciesByFamily);

        return super.view("species", modelAndView);
    }

    @GetMapping("/add-family")
    public ModelAndView getAddFamily() {

        return super.view("add-family");
    }

    @PostMapping("/add-family")
    public ModelAndView postAddFamily(@Valid @ModelAttribute("addFamily")AddFamilyBindingModel addFamilyBindingModel,
                                      BindingResult bindingResult,
                                      ModelAndView modelAndView) {

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("addFamily", addFamilyBindingModel);
            return super.view("add-family", modelAndView);
        }

        familyService.addFamily(addFamilyBindingModel);

        return super.view("add-family");
    }

    @GetMapping("/all")
    public ModelAndView getAllSpecies() {
        List<Species> allSpecies = speciesService.getAllApproved();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("species", allSpecies);

        return super.view("species", modelAndView);
    }

    @GetMapping("/species/{id}")
    public ModelAndView getSpecies(@PathVariable UUID id) {
        Species speciesById = speciesService.getSpeciesById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("species", speciesById);

        return super.view("species-details", modelAndView);
    }

    @GetMapping("/species/add")
    public ModelAndView getAddSpecies() {
        List<String> familyList = familyService.getAllFamiliesList();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("familyList", familyList);

        return super.view("add-species", modelAndView);
    }

    @PostMapping("/species/add")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") - better to use
    public ModelAndView postAddSpecies(@Valid @ModelAttribute("addSpecies")AddSpeciesBindingModel addSpeciesBindingModel,
                                       BindingResult bindingResult,
                                       ModelAndView modelAndView,
                                       @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        if (bindingResult.hasErrors()) {
            List<String> familyList = familyService.getAllFamiliesList();
            modelAndView.addObject("addSpecies", addSpeciesBindingModel);
            modelAndView.addObject("familyList", familyList);
            return super.view("add-species", modelAndView);
        }

        speciesService.addSpecies(addSpeciesBindingModel, authenticationMetadata.getUserId());

        return super.view("thank-you");
    }

    @ModelAttribute("addSpecies")
    public AddSpeciesBindingModel getAddSpeciesBindingModel() {
        return new AddSpeciesBindingModel();
    }

    @ModelAttribute("addFamily")
    public AddFamilyBindingModel getAddFamilyBindingModel() {
        return new AddFamilyBindingModel();
    }
}
