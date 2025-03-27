package com.CactiEncyclopedia.web;

import com.CactiEncyclopedia.domain.entities.UserRole;
import com.CactiEncyclopedia.domain.enums.RoleName;
import com.CactiEncyclopedia.security.AuthenticationMetadata;
import com.CactiEncyclopedia.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static com.CactiEncyclopedia.TestBuilder.aRandomUserDetailsViewModel;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerApiTest {
    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUnauthenticatedRequestToMyProfileEndpoint_shouldRRedirectToLogin() throws Exception {
        MockHttpServletRequestBuilder request = get("/user/my-profile");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/auth/login"));

        verify(userService, times(0)).getLoggedUserDetails(any());
    }

    @Test
    void getAuthenticatedRequestToMyProfileEndpoint_shouldReturnMyProfileView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.USER), true);
        MockHttpServletRequestBuilder request = get("/user/my-profile")
                .with(user(principal));

        when(userService.getLoggedUserDetails(userId)).thenReturn(aRandomUserDetailsViewModel());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("my-profile"))
                .andExpect(model().attributeExists("userProfile"));

        verify(userService, times(1)).getLoggedUserDetails(userId);
    }
}
