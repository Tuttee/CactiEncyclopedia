package com.app.web;

import com.app.client.FactClient;
import com.app.domain.binding.FactDto;
import com.app.domain.entities.Genera;
import com.app.domain.entities.Species;
import com.app.domain.entities.UserRole;
import com.app.domain.enums.RoleName;
import com.app.exception.GeneraAlreadyExistsException;
import com.app.exception.SpeciesAlreadyExistsException;
import com.app.security.AuthenticationMetadata;
import com.app.services.GeneraService;
import com.app.services.SpeciesService;
import feign.RetryableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.app.TestBuilder.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatalogController.class)
@ActiveProfiles("test")
public class CatalogControllerApiTest {
    @MockitoBean
    private GeneraService generaService;
    @MockitoBean
    private SpeciesService speciesService;
    @MockitoBean
    private FactClient factClient;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToCatalogEndpoint_whenFactIsAvailable_shouldReturnCatalogView() throws Exception {
        MockHttpServletRequestBuilder request = get("/catalog");

        Genera genera = getGenera();
        List<Genera> generaList = List.of(genera, genera, genera);
        Page<Genera> allGenera = new PageImpl<>(generaList);
        FactDto factDto = new FactDto("RandomFact");
        ResponseEntity<FactDto> responseEntity = new ResponseEntity<>(factDto, HttpStatus.OK);
        List<Species> speciesList = List.of(getApprovedSpecies(), getApprovedSpecies());

        when(generaService.getAllGeneraWithSpecies(any())).thenReturn(allGenera);
        when(factClient.randomFact()).thenReturn(responseEntity);
        when(speciesService.get10RecentlyAdded()).thenReturn(speciesList);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attributeExists("allGenera"))
                .andExpect(model().attributeExists("fact"))
                .andExpect(model().attributeExists("recentlyAdded"));

        verify(generaService, times(1)).getAllGeneraWithSpecies(any());
        verify(factClient, times(1)).randomFact();
        verify(speciesService, times(1)).get10RecentlyAdded();
    }

    @Test
    void getRequestToCatalogEndpoint_whenFactIsNull_shouldReturnCatalogView() throws Exception {
        MockHttpServletRequestBuilder request = get("/catalog");

        Genera genera = getGenera();
        List<Genera> generaList = List.of(genera, genera, genera);
        Page<Genera> allGenera = new PageImpl<>(generaList);
        List<Species> speciesList = List.of(getApprovedSpecies(), getApprovedSpecies());
        FactDto factDto = null;
        ResponseEntity<FactDto> responseEntity = new ResponseEntity<>(factDto, HttpStatus.OK);

        when(generaService.getAllGeneraWithSpecies(any())).thenReturn(allGenera);
        when(factClient.randomFact()).thenReturn(responseEntity);
        when(speciesService.get10RecentlyAdded()).thenReturn(speciesList);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attributeExists("allGenera"))
                .andExpect(model().attributeDoesNotExist("fact"))
                .andExpect(model().attributeExists("recentlyAdded"));

        verify(generaService, times(1)).getAllGeneraWithSpecies(any());
        verify(factClient, times(1)).randomFact();
        verify(speciesService, times(1)).get10RecentlyAdded();
    }

    @Test
    void getRequestToCatalogEndpoint_whenFactIsNotAvailable_shouldReturnCatalogView() throws Exception {
        MockHttpServletRequestBuilder request = get("/catalog");

        Genera genera = getGenera();
        List<Genera> generaList = List.of(genera, genera, genera);
        Page<Genera> allGenera = new PageImpl<>(generaList);
        List<Species> speciesList = List.of(getApprovedSpecies(), getApprovedSpecies());

        when(generaService.getAllGeneraWithSpecies(any())).thenReturn(allGenera);
        when(factClient.randomFact()).thenThrow(RetryableException.class);
        when(speciesService.get10RecentlyAdded()).thenReturn(speciesList);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("catalog"))
                .andExpect(model().attributeExists("allGenera"))
                .andExpect(model().attributeDoesNotExist("fact"))
                .andExpect(model().attributeExists("recentlyAdded"));

        verify(generaService, times(1)).getAllGeneraWithSpecies(any());
        verify(factClient, times(1)).randomFact();
        verify(speciesService, times(1)).get10RecentlyAdded();
    }

    @Test
    void getRequestToGeneraEndpoint_whenGeneraExists_shouldReturnSpeciesViewForTheGenera() throws Exception {
        Genera genera = getGenera();
        MockHttpServletRequestBuilder request = get("/catalog/" + genera.getName());

        List<Species> speciesList = List.of(getApprovedSpecies(), getApprovedSpecies());
        Page<Species> allSpecies = new PageImpl<>(speciesList);

        when(speciesService.getApprovedSpeciesByGenera(any(), any())).thenReturn(allSpecies);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("species"))
                .andExpect(model().attributeExists("species"))
                .andExpect(model().attributeExists("generaName"))
                .andExpect(model().attributeExists("all"));

        verify(speciesService, times(1)).getApprovedSpeciesByGenera(any(), any());
    }

    @Test
    void getRequestToGeneraEndpoint_whenGeneraDoesNotExist_shouldReturn404AndNotFoundView() throws Exception {
        Genera genera = getGenera();
        MockHttpServletRequestBuilder request = get("/catalog/" + genera.getName());

        when(speciesService.getApprovedSpeciesByGenera(any(), any())).thenThrow(new NoSuchElementException());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));

        verify(speciesService, times(1)).getApprovedSpeciesByGenera(any(), any());
    }

    @Test
    void getUnauthorizedRequestToAddGeneraEndpoint_shouldReturn404AndNotFoundView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.USER), true);

        MockHttpServletRequestBuilder request = get("/catalog/add-genera")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }

    @Test
    void getAuthorizedRequestToAddGeneraEndpoint_shouldReturnAddFactView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        MockHttpServletRequestBuilder request = get("/catalog/add-genera")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("add-genera"));
    }

    @Test
    void postUnauthorizedRequestToAddGeneraEndpoint_shouldReturn403AndNotFoundView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.USER), true);

        MockHttpServletRequestBuilder request = post("/catalog/add-genera")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }

    @Test
    void postAuthorizedRequestToAddGeneraEndpoint_whenValidData_shouldReturnAddFactView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        MockHttpServletRequestBuilder request = post("/catalog/add-genera")
                .with(user(principal))
                .formField("name", "Mammillaria")
                .formField("imageURL", "http://test.com")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalog/add-genera"));
        verify(generaService, times(1)).addGenera(any());
    }

    @Test
    void postAuthorizedRequestToAddGeneraEndpoint_whenInvalidData_shouldReturnAddFactView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        MockHttpServletRequestBuilder request = post("/catalog/add-genera")
                .with(user(principal))
                .formField("name", "M")
                .formField("imageURL", "ht")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("add-genera"))
                .andExpect(model().attributeExists("addGenera"))
                .andExpect(model().hasErrors());
    }

    @Test
    void postAuthorizedRequestToAddGeneraEndpoint_whenGeneraAlreadyExists_ThenRedirectToAddGeneraWithFlashParameter() throws Exception {
        when(generaService.addGenera(any())).thenThrow(new GeneraAlreadyExistsException("Genera already exists!"));

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        MockHttpServletRequestBuilder request = post("/catalog/add-genera")
                .with(user(principal))
                .formField("name", "Mammillaria")
                .formField("imageURL", "http://test.com")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalog/add-genera"))
                .andExpect(flash().attributeExists("addGenera"))
                .andExpect(flash().attributeExists("generaAlreadyExistsMessage"));

        verify(generaService, times(1)).addGenera(any());
    }

    @Test
    void getRequestToAllSpeciesEndpoint_whenFactIsAvailable_shouldReturnCatalogView() throws Exception {
        MockHttpServletRequestBuilder request = get("/catalog/all");

        List<Species> speciesList = List.of(getApprovedSpecies(), getApprovedSpecies());
        Page<Species> species = new PageImpl<>(speciesList);

        when(speciesService.getAllApproved(any())).thenReturn(species);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("species"))
                .andExpect(model().attributeExists("species"))
                .andExpect(model().attributeExists("all"));

        verify(speciesService, times(1)).getAllApproved(any());
    }

    @Test
    void getRequestToSpeciesDetailsEndpoint_whenSpeciesExists_shouldReturnSpeciesDetailsView() throws Exception {
        Species species = getSpecies();
        MockHttpServletRequestBuilder request = get("/catalog/species/" + species.getId());

        when(speciesService.getSpeciesById(species.getId())).thenReturn(species);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("species-details"))
                .andExpect(model().attributeExists("species"));

        verify(speciesService, times(1)).getSpeciesById(species.getId());
    }

    @Test
    void getRequestToSpeciesDetailsEndpoint_whenGeneraDoesNotExist_shouldReturn404AndNotFoundView() throws Exception {
        Genera genera = getGenera();
        MockHttpServletRequestBuilder request = get("/catalog/" + genera.getName());

        when(speciesService.getApprovedSpeciesByGenera(any(), any())).thenThrow(new NoSuchElementException());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));

        verify(speciesService, times(1)).getApprovedSpeciesByGenera(any(), any());
    }

    @Test
    void getUnauthenticatedRequestToAddSpeciesEndpoint_shouldReturn404() throws Exception {
        MockHttpServletRequestBuilder request = get("/catalog/species/add");

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
    }

    @Test
    void getAuthenticatedRequestToGeneraFactEndpoint_shouldReturnAddSpeciesView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.USER), true);

        MockHttpServletRequestBuilder request = get("/catalog/species/add")
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("add-species"))
                .andExpect(model().attributeExists("generaList"));
    }

    @Test
    void postUnauthenticatedRequestToAddSpeciesEndpoint_shouldReturn403AndNotFoundView() throws Exception {
        MockHttpServletRequestBuilder request = post("/catalog/species/add");

        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    void postAuthenticatedRequestFromUserToAddSpeciesEndpoint_whenValidData_shouldReturnAddFactView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.USER), true);

        MockHttpServletRequestBuilder request = post("/catalog/species/add")
                .with(user(principal))
                .formField("name", "Mammillaria")
                .formField("genera", "Mammillaria")
                .formField("description", "MammillariaMammillariaMammillariaMammillariaMammillariaMammillaria")
                .formField("imageURL", "http://test.com")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("thank-you"));
        verify(speciesService, times(1)).addSpecies(any() ,any());
    }

    @Test
    void postAuthenticatedRequestFromAdminToGeneraFactEndpoint_whenValidData_shouldReturnAddFactView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        MockHttpServletRequestBuilder request = post("/catalog/species/add")
                .with(user(principal))
                .formField("name", "Mammillaria")
                .formField("genera", "Mammillaria")
                .formField("description", "MammillariaMammillariaMammillariaMammillariaMammillariaMammillaria")
                .formField("imageURL", "http://test.com")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalog/species/add"))
                .andExpect(view().name("redirect:/catalog/species/add"));
        verify(speciesService, times(1)).addSpecies(any() ,any());
    }

    @Test
    void postAuthenticatedRequestToAddSpeciesEndpoint_whenInvalidData_shouldReturnAddFactView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        MockHttpServletRequestBuilder request = post("/catalog/species/add")
                .with(user(principal))
                .formField("name", "M")
                .formField("genera", "M")
                .formField("description", "M")
                .formField("imageURL", "test")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("add-species"))
                .andExpect(model().attributeExists("addSpecies"))
                .andExpect(model().attributeExists("generaList"))
                .andExpect(model().hasErrors());

        verify(speciesService, times(0)).addSpecies(any() ,any());
    }

    @Test
    void postAuthenticatedRequestToAddSpeciesEndpoint_whenSpeciesAlreadyExists_ThenRedirectToAddGSpeciesWithFlashParameter() throws Exception {
        when(speciesService.addSpecies(any(), any())).thenThrow(new SpeciesAlreadyExistsException("Species already exists!"));

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "user123", "12312312", new UserRole(RoleName.ADMIN), true);

        MockHttpServletRequestBuilder request = post("/catalog/species/add")
                .with(user(principal))
                .formField("name", "Mammillaria")
                .formField("genera", "Mammillaria")
                .formField("description", "MammillariaMammillariaMammillariaMammillariaMammillariaMammillaria")
                .formField("imageURL", "http://test.com")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalog/species/add"))
                .andExpect(flash().attributeExists("addSpecies"))
                .andExpect(flash().attributeExists("speciesAlreadyExistsMessage"));

        verify(speciesService, times(1)).addSpecies(any() ,any());
    }


}
