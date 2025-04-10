package com.app.web;

import com.app.domain.binding.AddFactDto;
import com.app.domain.entities.UserRole;
import com.app.domain.enums.RoleName;
import com.app.security.AuthenticationMetadata;
import com.app.services.FactService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
@ActiveProfiles("test")
public class IndexControllerApiTest {
    @MockitoBean
    private FactService factService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToIndexEndpoint_shouldReturnIndexView() throws Exception {
        MockHttpServletRequestBuilder request = get("/");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getRequestToAboutEndpoint_shouldReturnAboutView() throws Exception {
        MockHttpServletRequestBuilder request = get("/about");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("about"));
    }

    @Test
    void getUnauthenticatedRequestToThankEndpoint_shouldRedirectToLogin() throws Exception {
        MockHttpServletRequestBuilder request = get("/thank");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/auth/login"));
    }

    @Test
    void getAuthenticatedRequestToThankEndpoint_shouldReturnThankView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.USER), true);

        MockHttpServletRequestBuilder request = get("/thank")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("thank-you"));
    }

    @Test
    void getUnauthorizedRequestToAddFactEndpoint_shouldReturn404AndNotFoundView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.USER), true);

        MockHttpServletRequestBuilder request = get("/add-fact")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }

    @Test
    void getAuthorizedRequestToAddFactEndpoint_shouldReturnAddFactView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        MockHttpServletRequestBuilder request = get("/add-fact")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("add-fact"));
    }

    @Test
    void postUnauthorizedRequestToAddFactEndpoint_shouldReturn403AndNotFoundView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.USER), true);

        MockHttpServletRequestBuilder request = post("/add-fact")
                .with(user(principal))
                .param("content", "test")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));

        verify(factService, never()).addFact(any(), any());
    }

    @Test
    void postAuthorizedRequestToAddFactEndpoint_shouldReturnAndFactViewWithSuccessAttribute() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        AddFactDto addFactDto = new AddFactDto("TestQ", userId);

        MockHttpServletRequestBuilder request = post("/add-fact")
                .with(user(principal))
                .param("content", addFactDto.getContent())
                .with(csrf());

        when(factService.addFact(any(), any())).thenReturn(true);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("add-fact"))
                .andExpect(model().attributeExists("success"));

        verify(factService, times(1)).addFact(any(), any());
    }

    @Test
    void postAuthorizedRequestToAddFactEndpoint_whenInvalidData_shouldReturnAndFactViewWithError() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        AddFactDto addFactDto = new AddFactDto("Test", userId);

        MockHttpServletRequestBuilder request = post("/add-fact")
                .with(user(principal))
                .param("content", addFactDto.getContent())
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("add-fact"))
                .andExpect(model().attributeExists("addFact"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeDoesNotExist("success"));

        verify(factService, times(0)).addFact(any(), any());
    }

    @Test
    void postAuthorizedRequestToAddFactEndpoint_whenSavingErrorOccurs_shouldReturnAndFactViewWithError() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        AddFactDto addFactDto = new AddFactDto("Test123456", userId);

        MockHttpServletRequestBuilder request = post("/add-fact")
                .with(user(principal))
                .param("content", addFactDto.getContent())
                .with(csrf());

        when(factService.addFact(any(), any())).thenThrow(FeignException.class);

        mockMvc.perform(request)
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/add-fact"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(model().attributeDoesNotExist("success"));

        verify(factService, times(1)).addFact(any(), any());
    }

    @Test
    void postAuthorizedRequestToAddFactEndpoint_whenRuntimeException_shouldReturn500AndInternalServerErrorView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        AddFactDto addFactDto = new AddFactDto("Test123456", userId);

        MockHttpServletRequestBuilder request = post("/add-fact")
                .with(user(principal))
                .param("content", addFactDto.getContent())
                .with(csrf());

        when(factService.addFact(any(), any())).thenThrow(new RuntimeException("error"));

        mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("internal-server-error"))
                .andExpect(model().attributeExists("error"));

        verify(factService, times(1)).addFact(any(), any());
    }
}
