package com.CactiEncyclopedia.web;

import com.CactiEncyclopedia.domain.binding.AddQuestionDto;
import com.CactiEncyclopedia.security.AuthenticationMetadata;
import com.CactiEncyclopedia.services.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@RequestMapping("/question")
@Controller
@RequiredArgsConstructor
public class QuestionController extends BaseController {
    private final QuestionService questionService;

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ModelAndView postQuestion(AddQuestionDto addQuestionDto,
                                     @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                     @PathVariable UUID id) {
        questionService.addQuestion(addQuestionDto, id, authenticationMetadata);
        return super.redirect("/catalog/species/" + id);
    }
}
