package com.app.web;

import com.app.domain.binding.AddQuestionDto;
import com.app.security.AuthenticationMetadata;
import com.app.services.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

@RequestMapping("/question")
@Controller
@RequiredArgsConstructor
public class QuestionController extends BaseController {
    private final QuestionService questionService;

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public RedirectView postQuestion(@Valid AddQuestionDto addQuestionDto,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes,
                                     @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                     @PathVariable UUID id) {

        if (bindingResult.hasErrors()) {
//            ModelAndView modelAndView = new ModelAndView();
//            modelAndView.addObject("addQuestion", addQuestionDto);
            redirectAttributes.addFlashAttribute("addQuestion", addQuestionDto);
            redirectAttributes.addFlashAttribute("errorMessage", bindingResult.getFieldError("content").getDefaultMessage());

//            modelAndView.setViewName("redirect:/catalog/species/" + id);
            return new RedirectView("/catalog/species/" + id, true);
        }

        questionService.addQuestion(addQuestionDto, id, authenticationMetadata);
        return new RedirectView("/catalog/species/" + id, false);
    }
}
