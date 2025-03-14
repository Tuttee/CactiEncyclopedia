package com.CactiEncyclopedia.web;

import com.CactiEncyclopedia.client.FactClient;
import com.CactiEncyclopedia.domain.binding.AddGeneraDto;
import com.CactiEncyclopedia.domain.binding.AddQuestionDto;
import com.CactiEncyclopedia.domain.binding.AddSpeciesDto;
import com.CactiEncyclopedia.domain.binding.FactDto;
import com.CactiEncyclopedia.domain.entities.Genera;
import com.CactiEncyclopedia.domain.entities.Species;
import com.CactiEncyclopedia.domain.enums.RoleName;
import com.CactiEncyclopedia.security.AuthenticationMetadata;
import com.CactiEncyclopedia.services.GeneraService;
import com.CactiEncyclopedia.services.SpeciesService;
import feign.RetryableException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController extends BaseController {
    private final GeneraService generaService;
    private final SpeciesService speciesService;
    private final FactClient factClient;

    @GetMapping
    public ModelAndView catalog() {
        List<Genera> allGenera = generaService.getAllGeneraWithSpecies();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("allGenera", allGenera);

        try {
            FactDto body = factClient.randomFact().getBody();

            if (body != null) {
                modelAndView.addObject("fact", body.getContent());
            }
        } catch (RetryableException e) {
            log.error(e.getMessage());
        }

        List<Species> recentlyAdded = speciesService.get10RecentlyAdded();
        modelAndView.addObject("recentlyAdded", recentlyAdded);

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
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ModelAndView postAddGenera(@Valid @ModelAttribute("addGenera") AddGeneraDto addGeneraDto,
                                      BindingResult bindingResult,
                                      ModelAndView modelAndView) {

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("addGenera", addGeneraDto);
            return super.view("add-genera", modelAndView);
        }

        generaService.addGenera(addGeneraDto);

        return super.redirect("/catalog/add-genera");
    }

    @GetMapping("/all")
    public ModelAndView getAllSpecies() {
        List<Species> allSpecies = speciesService.getAllApproved();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("species", allSpecies);
        modelAndView.addObject("all", true);

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
    public ModelAndView postAddSpecies(@Valid @ModelAttribute("addSpecies") AddSpeciesDto addSpeciesDto,
                                       BindingResult bindingResult,
                                       ModelAndView modelAndView,
                                       @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        if (bindingResult.hasErrors()) {
            List<String> generaList = generaService.getAllGeneraNamesList();
            modelAndView.addObject("addSpecies", addSpeciesDto);
            modelAndView.addObject("generaList", generaList);
            return super.view("add-species", modelAndView);
        }

        speciesService.addSpecies(addSpeciesDto, authenticationMetadata.getUserId());

        if (authenticationMetadata.getRole().getRoleName().equals(RoleName.ADMIN)) {
            return super.redirect("/catalog/species/add");
        }
        return super.view("thank-you");
    }

    @ModelAttribute("addSpecies")
    public AddSpeciesDto getAddSpeciesBindingModel() {
        return new AddSpeciesDto();
    }

    @ModelAttribute("addGenera")
    public AddGeneraDto getAddGeneraBindingModel() {
        return new AddGeneraDto();
    }

    @ModelAttribute("addQuestion")
    public AddQuestionDto getAddQuestionBindingModel() {
        return new AddQuestionDto();
    }
}
