package com.CactiEncyclopedia.web;

import com.CactiEncyclopedia.domain.binding.AddGeneraBindingModel;
import com.CactiEncyclopedia.domain.binding.AddQuestionBindingModel;
import com.CactiEncyclopedia.domain.binding.AddSpeciesBindingModel;
import com.CactiEncyclopedia.domain.entities.Genera;
import com.CactiEncyclopedia.domain.entities.Species;
import com.CactiEncyclopedia.security.AuthenticationMetadata;
import com.CactiEncyclopedia.services.GeneraService;
import com.CactiEncyclopedia.services.SpeciesService;
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
    private final GeneraService generaService;
    private final SpeciesService speciesService;

    public CatalogController(GeneraService generaService, SpeciesService speciesService) {
        super();
        this.generaService = generaService;
        this.speciesService = speciesService;
    }

    @GetMapping
    public ModelAndView catalog() {
        List<Genera> allGenera = generaService.getAllGeneraWithSpecies();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("allGenera", allGenera);

        return super.view("catalog", modelAndView);
    }

    @GetMapping("/{generaName}")
    public ModelAndView getGenera(@PathVariable String generaName) {
        List<Species> speciesByGenera = speciesService.getApprovedSpeciesByGenera(generaName);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("species", speciesByGenera);

        return super.view("species", modelAndView);
    }

    @GetMapping("/add-genera")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ModelAndView getAddGenera() {

        return super.view("add-genera");
    }

    @PostMapping("/add-genera")
    @PreAuthorize("hasAnyRole(ADMIN')")
    public ModelAndView postAddGenera(@Valid @ModelAttribute("addGenera") AddGeneraBindingModel addGeneraBindingModel,
                                      BindingResult bindingResult,
                                      ModelAndView modelAndView) {

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("addGenera", addGeneraBindingModel);
            return super.view("add-genera", modelAndView);
        }

        generaService.addGenera(addGeneraBindingModel);

        return super.view("add-genera");
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
        List<String> GeneraList = generaService.getAllGeneraNamesList();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("generaList", GeneraList);

        return super.view("add-species", modelAndView);
    }

    @PostMapping("/species/add")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ModelAndView postAddSpecies(@Valid @ModelAttribute("addSpecies")AddSpeciesBindingModel addSpeciesBindingModel,
                                       BindingResult bindingResult,
                                       ModelAndView modelAndView,
                                       @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        if (bindingResult.hasErrors()) {
            List<String> generaList = generaService.getAllGeneraNamesList();
            modelAndView.addObject("addSpecies", addSpeciesBindingModel);
            modelAndView.addObject("generaList", generaList);
            return super.view("add-species", modelAndView);
        }

        speciesService.addSpecies(addSpeciesBindingModel, authenticationMetadata.getUserId());

        return super.view("thank-you");
    }

    @ModelAttribute("addSpecies")
    public AddSpeciesBindingModel getAddSpeciesBindingModel() {
        return new AddSpeciesBindingModel();
    }

    @ModelAttribute("addGenera")
    public AddGeneraBindingModel getAddGeneraBindingModel() {
        return new AddGeneraBindingModel();
    }

    @ModelAttribute("addQuestion")
    public AddQuestionBindingModel getAddQuestionBindingModel() {
        return new AddQuestionBindingModel();
    }
}
