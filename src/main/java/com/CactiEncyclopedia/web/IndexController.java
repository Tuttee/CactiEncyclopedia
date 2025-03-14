package com.CactiEncyclopedia.web;

import com.CactiEncyclopedia.client.FactClient;
import com.CactiEncyclopedia.domain.binding.AddFactDto;
import com.CactiEncyclopedia.security.AuthenticationMetadata;
import com.CactiEncyclopedia.services.FactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class IndexController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);
    private final FactClient factClient;
    private final FactService factService;

    @GetMapping
    public ModelAndView getIndex() {
        ModelAndView modelAndView = new ModelAndView();

        return super.view("index", modelAndView);
    }

    @GetMapping("/about")
    public ModelAndView about() {
        return super.view("about");
    }

    @GetMapping("/add-fact")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ModelAndView getAddFact() {
        return super.view("add-fact");
    }

    @PostMapping("/add-fact")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ModelAndView postAddFact(@Valid @ModelAttribute("addFact") AddFactDto addFactDto,
                                    BindingResult bindingResult,
                                    ModelAndView modelAndView,
                                    @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("addFact", addFactDto);
            return super.view("/add-fact", modelAndView);
        }

        factService.addFact(addFactDto, authenticationMetadata.getUserId());

        modelAndView.addObject("success", true);
        return super.redirect("/add-fact", modelAndView);
    }

    @GetMapping("/thank")
    public ModelAndView thank() {
        return super.view("thank-you");
    }

    @ModelAttribute("addFact")
    public AddFactDto addFact() {
        return new AddFactDto();
    }
}
