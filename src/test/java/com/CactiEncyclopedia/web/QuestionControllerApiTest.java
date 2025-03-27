package com.CactiEncyclopedia.web;

import com.CactiEncyclopedia.domain.entities.UserRole;
import com.CactiEncyclopedia.domain.enums.RoleName;
import com.CactiEncyclopedia.security.AuthenticationMetadata;
import com.CactiEncyclopedia.services.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
@ActiveProfiles("test")
public class QuestionControllerApiTest {
    @MockitoBean
    private QuestionService questionService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postUnauthenticatedRequestToPostQuestionEndpoint_shouldRedirectToLogin() throws Exception {
        MockHttpServletRequestBuilder request = post("/question/" + UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().isForbidden());

        verify(questionService, times(0)).addQuestion(any(), any(), any());
    }

    @Test
    void postAdminRequestToPostQuestionEndpoint_shouldPostCommentAndRedirectToSpeciesPage() throws Exception {
        UUID speciesId = UUID.randomUUID();

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        MockHttpServletRequestBuilder request = post("/question/" + speciesId)
                .with(user(principal))
                .param("content", "TestQuestion")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalog/species/" + speciesId));

        verify(questionService, times(1)).addQuestion(any(), any(), any());
    }

    @Test
    void postUserRequestToPostQuestionEndpoint_shouldPostCommentAndRedirectToSpeciesPage() throws Exception {
        UUID speciesId = UUID.randomUUID();

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.USER), true);

        MockHttpServletRequestBuilder request = post("/question/" + speciesId)
                .with(user(principal))
                .param("content", "TestQuestion")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalog/species/" + speciesId));

        verify(questionService, times(1)).addQuestion(any(), any(), any());
    }

}
