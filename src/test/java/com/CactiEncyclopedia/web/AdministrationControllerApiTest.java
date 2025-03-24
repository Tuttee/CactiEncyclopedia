package com.CactiEncyclopedia.web;

import com.CactiEncyclopedia.domain.entities.*;
import com.CactiEncyclopedia.domain.enums.RoleName;
import com.CactiEncyclopedia.domain.view.UserDetailsViewModel;
import com.CactiEncyclopedia.security.AuthenticationMetadata;
import com.CactiEncyclopedia.services.SpeciesService;
import com.CactiEncyclopedia.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdministrationController.class)
public class AdministrationControllerApiTest {
    @MockitoBean
    private SpeciesService speciesService;
    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getUnauthorizedRequestToSpeciesAdministrationEndpoint_shouldReturn404AndNotFoundView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.USER), true);

        MockHttpServletRequestBuilder request = get("/administration/species")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }

    @Test
    void getAuthorizedRequestToSpeciesAdministrationEndpoint_shouldReturnSpeciesAdministrationView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        MockHttpServletRequestBuilder request = get("/administration/species")
                .with(user(principal))
                .with(csrf());

        when(speciesService.getAllApproved()).thenReturn(List.of(getApprovedSpecies()));
        when(speciesService.getAllUnapproved()).thenReturn(List.of(getUnapprovedSpecies()));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("species-administration"))
                .andExpect(model().attributeExists("approvedSpecies"))
                .andExpect(model().attributeExists("unapprovedSpecies"));
        verify(speciesService, times(1)).getAllApproved();
        verify(speciesService, times(1)).getAllUnapproved();
    }

    @Test
    void patchUnauthorizedRequestToSpeciesAdministrationEndpoint_shouldReturn404AndNotFoundView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.USER), true);
        Species approvedSpecies = getApprovedSpecies();

        MockHttpServletRequestBuilder request = patch("/administration/species/" + approvedSpecies.getId())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }

    @Test
    void patchAuthorizedRequestToSpeciesAdministrationEndpoint_shouldReturnSpeciesAdministrationView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);
        Species approvedSpecies = getApprovedSpecies();

        MockHttpServletRequestBuilder request = patch("/administration/species/" + approvedSpecies.getId())
                .with(user(principal))
                .with(csrf());



        mockMvc.perform(request)
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/administration/species"));
        verify(speciesService, times(1)).approve(approvedSpecies.getId());
    }

    @Test
    void deleteUnauthorizedRequestToSpeciesAdministrationEndpoint_shouldReturn404AndNotFoundView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.USER), true);
        Species approvedSpecies = getApprovedSpecies();

        MockHttpServletRequestBuilder request = delete("/administration/species/" + approvedSpecies.getId())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }

    @Test
    void deleteAuthorizedRequestToSpeciesAdministrationEndpoint_shouldReturnSpeciesAdministrationView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);
        Species approvedSpecies = getApprovedSpecies();

        MockHttpServletRequestBuilder request = delete("/administration/species/" + approvedSpecies.getId())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/administration/species"));
        verify(speciesService, times(1)).delete(approvedSpecies.getId());
    }

    @Test
    void getUnauthorizedRequestToUsersAdministrationEndpoint_shouldReturn404AndNotFoundView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.USER), true);

        MockHttpServletRequestBuilder request = get("/administration/users")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }

    @Test
    void getAuthorizedRequestToUsersAdministrationEndpoint_shouldReturnSpeciesAdministrationView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        MockHttpServletRequestBuilder request = get("/administration/users")
                .with(user(principal))
                .with(csrf());

        UserDetailsViewModel userDetailsViewModel = new UserDetailsViewModel(getUser().getUsername(),
                getUser().getEmail(),
                getUser().getFirstName(),
                getUser().getLastName(),
                getUser().getRole().getRoleName(),
                getUser().getAddedSpecies());

        when(userService.getAllUsersExceptLogged(userId)).thenReturn(List.of(userDetailsViewModel));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user-administration"))
                .andExpect(model().attributeExists("users"));
        verify(userService, times(1)).getAllUsersExceptLogged(userId);
    }


    @Test
    void patchUnauthorizedRequestToUsersAdministrationEndpoint_shouldReturn404AndNotFoundView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.USER), true);

        MockHttpServletRequestBuilder request = patch("/administration/users/" + principal.getUsername())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }

    @Test
    void patchAuthorizedRequestToUsersAdministrationEndpoint_shouldReturnUsersAdministrationView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        MockHttpServletRequestBuilder request = patch("/administration/users/" + principal.getUsername())
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/administration/users"));
        verify(userService, times(1)).updateUserRole(principal.getUsername());
    }

    private Species getSpecies() {
        Species species = new Species();
        species.setId(UUID.randomUUID());
        species.setName("Species");
        species.setDescription("Species Description");
        species.setCreatedBy(new User());
        species.setAddedOn(LocalDate.now());
        species.setGenera(getGenera());
        species.setColdHardiness("Species Cold Hardiness");
        species.setCultivation("Species Cultivation");

        Question q1 = Question.builder().askedOn(LocalDateTime.now()).build();
        Question q2 = Question.builder().askedOn(LocalDateTime.now()).build();
        List<Question> questions = Arrays.asList(q1, q2);
        species.setQuestions(questions);

        return species;
    }

    private Genera getGenera() {
        return new Genera("Mammillaria", "https://", new ArrayList<>());
    }

    private Species getApprovedSpecies() {
        Species species = getSpecies();
        species.setApproved(true);
        return species;
    }
    private Species getUnapprovedSpecies() {
        Species species = getSpecies();
        species.setApproved(false);
        return species;
    }

    private User getUser() {
        User user = new User("Testuser",
                "testpass",
                "test@user.com",
                "TestFname",
                "TestLname",
                createUserRole(),
                new ArrayList<>(),
                new ArrayList<>());
        user.setId(UUID.randomUUID());
        return user;
    }

    private UserRole createUserRole() {
        return new UserRole(RoleName.USER);
    }
}
