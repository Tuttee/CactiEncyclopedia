package com.app.services;

import com.app.domain.binding.AddQuestionDto;
import com.app.domain.entities.Question;
import com.app.domain.entities.Species;
import com.app.domain.entities.User;
import com.app.repositories.QuestionRepository;
import com.app.security.AuthenticationMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceUTest {
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private SpeciesService speciesService;
    @Mock
    private UserService userService;

    @InjectMocks
    private QuestionService questionService;

    @Test
    void givenHappyPath_whenAddSpecies() {
        AddQuestionDto addQuestionDto = new AddQuestionDto();
        addQuestionDto.setSpeciesId(UUID.randomUUID().toString());
        addQuestionDto.setContent("Test");

        UUID speciesId = UUID.randomUUID();
        AuthenticationMetadata authenticationMetadata = new AuthenticationMetadata();
        authenticationMetadata.setUserId(UUID.randomUUID());

        when(speciesService.getSpeciesById(speciesId)).thenReturn(new Species());
        when(userService.findUserById(authenticationMetadata.getUserId())).thenReturn(new User());

        questionService.addQuestion(addQuestionDto, speciesId, authenticationMetadata);

        verify(speciesService, times(1)).getSpeciesById(speciesId);
        verify(userService, times(1)).findUserById(authenticationMetadata.getUserId());
        verify(questionRepository, times(1)).save(any(Question.class));

    }

}
