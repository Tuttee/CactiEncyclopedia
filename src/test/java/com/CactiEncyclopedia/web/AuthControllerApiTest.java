package com.CactiEncyclopedia.web;

import com.CactiEncyclopedia.domain.binding.UserRegisterDto;
import com.CactiEncyclopedia.exception.EmailAlreadyExistsException;
import com.CactiEncyclopedia.exception.UsernameAlreadyExistsException;
import com.CactiEncyclopedia.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
public class AuthControllerApiTest {
    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToRegisterEndpoint_shouldReturnRegisterView() throws Exception {
        MockHttpServletRequestBuilder request = get("/auth/register");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("userRegister", instanceOf(UserRegisterDto.class)));
    }

    @Test
    void getRequestToLoginEndpoint_shouldReturnLoginView() throws Exception {
        MockHttpServletRequestBuilder request = get("/auth/login");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void getRequestToLoginEndpointWithErrorParameter_shouldReturnLoginViewWithError() throws Exception {
        MockHttpServletRequestBuilder request = get("/auth/login")
                .param("error", "");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void postRequestToRegisterEndpoint_shouldReturnLoginViewOnSuccess() throws Exception {
        MockHttpServletRequestBuilder request = post("/auth/register")
                .formField("username", "testtest")
                .formField("email", "test@test.com")
                .formField("firstName", "TestFN")
                .formField("lastName", "TestLN")
                .formField("password", "12345678")
                .formField("confirmPassword", "12345678")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));

        verify(userService, times(1)).register(any());
    }

    @Test
    void postRequestToRegisterEndpointWithInvalidData_shouldReturnRegisterView() throws Exception {
        MockHttpServletRequestBuilder request = post("/auth/register")
                .formField("username", "")
                .formField("email", "test@test.com")
                .formField("firstName", "TestFN")
                .formField("lastName", "TestLN")
                .formField("password", "12345678")
                .formField("confirmPassword", "12345678")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("userRegister"));

        verify(userService, never()).register(any());
    }


    @Test
    void postRequestToRegisterEndpoint_WhenUsernameAlreadyExists_ThenRedirectToRegisterWithFlashParameter() throws Exception {
        when(userService.register(any())).thenThrow(new UsernameAlreadyExistsException("Username already exists!"));

        MockHttpServletRequestBuilder request = post("/auth/register")
                .formField("username", "testtest")
                .formField("email", "test@test.com")
                .formField("firstName", "TestFN")
                .formField("lastName", "TestLN")
                .formField("password", "12345678")
                .formField("confirmPassword", "12345678")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"))
                .andExpect(flash().attributeExists("userRegister"))
                .andExpect(flash().attributeExists("usernameAlreadyExistsMessage"));

        verify(userService, times(1)).register(any());
    }

    @Test
    void postRequestToRegisterEndpoint_WhenEmailAlreadyExists_ThenRedirectToRegisterWithFlashParameter() throws Exception {
        when(userService.register(any())).thenThrow(new EmailAlreadyExistsException("Email already exists!"));

        MockHttpServletRequestBuilder request = post("/auth/register")
                .formField("username", "testtest")
                .formField("email", "test@test.com")
                .formField("firstName", "TestFN")
                .formField("lastName", "TestLN")
                .formField("password", "12345678")
                .formField("confirmPassword", "12345678")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"))
                .andExpect(flash().attributeExists("userRegister"))
                .andExpect(flash().attributeExists("emailAlreadyExistsMessage"));

        verify(userService, times(1)).register(any());
    }

}
