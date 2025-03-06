package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.binding.AddQuestionBindingModel;
import com.CactiEncyclopedia.domain.entities.Question;
import com.CactiEncyclopedia.repositories.QuestionRepository;
import com.CactiEncyclopedia.security.AuthenticationMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final SpeciesService speciesService;
    private final UserService userService;

    public void addQuestion(AddQuestionBindingModel questionAdd, UUID speciesId, AuthenticationMetadata authenticationMetadata) {
        Question question = new Question();

        question.setContent(questionAdd.getContent());
        question.setSpecies(speciesService.getSpeciesById(speciesId));
        question.setAskedBy(userService.findUserById(authenticationMetadata.getUserId()));
        question.setApproved(true);
        question.setAskedOn(LocalDateTime.now());

        questionRepository.save(question);
    }
}
