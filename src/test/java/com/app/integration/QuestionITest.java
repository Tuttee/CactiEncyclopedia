package com.app.integration;

import com.app.domain.binding.AddGeneraDto;
import com.app.domain.binding.AddQuestionDto;
import com.app.domain.binding.AddSpeciesDto;
import com.app.domain.binding.UserRegisterDto;
import com.app.domain.entities.Question;
import com.app.domain.entities.Species;
import com.app.domain.entities.User;
import com.app.repositories.QuestionRepository;
import com.app.repositories.SpeciesRepository;
import com.app.repositories.UserRepository;
import com.app.security.AuthenticationMetadata;
import com.app.services.GeneraService;
import com.app.services.QuestionService;
import com.app.services.SpeciesService;
import com.app.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.app.TestBuilder.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class QuestionITest {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private GeneraService generaService;

    @Autowired
    private SpeciesService speciesService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SpeciesRepository speciesRepository;

    @Test
    @Transactional
    void addQuestion_HappyPath() {

        UserRegisterDto userRegisterDto = getAdminRegisterDto();
        userService.register(userRegisterDto);
        User register = userRepository.findByUsername(userRegisterDto.getUsername()).get();
        AuthenticationMetadata principal = new AuthenticationMetadata(register.getId(),
                register.getUsername(),
                register.getPassword(),
                register.getRole(),
                true);


        AddGeneraDto addGeneraDto = getAddGeneraDto();
        generaService.addGenera(addGeneraDto);

        AddSpeciesDto addSpeciesDto = getAddSpeciesDto();
        speciesService.addSpecies(addSpeciesDto, register.getId());
        Species speciesByName = speciesRepository.findByName(addSpeciesDto.getName()).get();

        AddQuestionDto testContent = new AddQuestionDto("TestContent", speciesByName.getId().toString());
        this.questionService.addQuestion(testContent, speciesByName.getId(), principal);

        Optional<Question> questionByContent = this.questionRepository.findQuestionByContent(testContent.getContent());

        assertTrue(questionByContent.isPresent());
        assertEquals(testContent.getContent(), questionByContent.get().getContent());

    }
}
