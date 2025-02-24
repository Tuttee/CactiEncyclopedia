package com.CactiEncyclopedia.web;

import com.CactiEncyclopedia.domain.entities.Family;
import com.CactiEncyclopedia.domain.entities.Species;
import com.CactiEncyclopedia.services.FamilyService;
import com.CactiEncyclopedia.services.SpeciesService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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
        List<Family> allFamilies = familyService.getAllFamilies();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("families", allFamilies);

        return super.view("catalog", modelAndView);
    }

    //TODO: Following method and template has to be reworked because it reuses Family
    @GetMapping("/{familyName}")
    public ModelAndView family(@PathVariable String familyName) {
        List<Species> speciesByFamily = speciesService.getSpeciesByFamily(familyName);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("families", speciesByFamily);

        return super.view("catalog", modelAndView);
    }
}
