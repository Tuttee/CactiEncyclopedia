package com.app.services;

import com.app.domain.binding.AddSpeciesDto;
import com.app.domain.entities.Genera;
import com.app.domain.entities.Species;
import com.app.domain.entities.User;
import com.app.domain.entities.UserRole;
import com.app.domain.enums.RoleName;
import com.app.exception.SpeciesAlreadyExistsException;
import com.app.repositories.SpeciesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static com.app.TestBuilder.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpeciesServiceUTest {
    @Mock
    private SpeciesRepository speciesRepository;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private GeneraService generaService;

    @InjectMocks
    private SpeciesService speciesService;

    @Test
    void givenMissingSpeciesFromDb_whenGetSpeciesById_thenExceptionIsThrown() {
        UUID speciesId = UUID.randomUUID();

        when(speciesRepository.findById(speciesId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> speciesService.getSpeciesById(speciesId));
    }

    @Test
    void givenExistingSpeciesFromDb_whenGetSpeciesById_thenSpeciesIsFound() {
        Species species = getApprovedSpecies();

        when(speciesRepository.findById(species.getId())).thenReturn(Optional.of(species));

        Species speciesById = speciesService.getSpeciesById(species.getId());

        verify(speciesRepository, times(1)).findById(species.getId());
        assertEquals(species.getId(), speciesById.getId());
        assertEquals(species, speciesById);
    }

    @Test
    void givenExistingSpecies_whenGetApprovedSpeciesByGenera_thenPageOfSpecies() {
        Pageable pageable = Pageable.unpaged();
        Genera genera = getGenera();
        Page<Species> speciesPage = new PageImpl<>(Arrays.asList(getApprovedSpecies(), getApprovedSpecies()));

        when(speciesRepository.findAllByGenera_NameAndApprovedIsTrue(genera.getName(), pageable))
                .thenReturn(speciesPage);

        Page<Species> actualPageSpecies = speciesService.getApprovedSpeciesByGenera(genera.getName(), pageable);

        verify(speciesRepository, times(1))
                .findAllByGenera_NameAndApprovedIsTrue(genera.getName(), pageable);
        assertEquals(speciesPage.getTotalElements(), actualPageSpecies.getTotalElements());
        assertEquals(speciesPage.getTotalPages(), actualPageSpecies.getTotalPages());
        assertEquals(speciesPage.getNumber(), actualPageSpecies.getNumber());
        assertEquals(speciesPage.getSize(), actualPageSpecies.getSize());
        assertEquals(speciesPage.getContent(), actualPageSpecies.getContent());
        assertEquals(speciesPage, actualPageSpecies);
    }

    @Test
    void givenExistingSpecies_whenGetAllApproved_thenPageOfSpecies() {
        Pageable pageable = Pageable.unpaged();
        Page<Species> speciesPage = new PageImpl<>(Arrays.asList(getApprovedSpecies(), getApprovedSpecies()));

        when(speciesRepository.findAllByApprovedIsTrue(pageable)).thenReturn(speciesPage);

        Page<Species> allApproved = speciesService.getAllApproved(pageable);

        verify(speciesRepository, times(1)).findAllByApprovedIsTrue(pageable);
        assertEquals(speciesPage.getTotalElements(), allApproved.getTotalElements());
        assertEquals(speciesPage.getTotalPages(), allApproved.getTotalPages());
        assertEquals(speciesPage.getNumber(), allApproved.getNumber());
        assertEquals(speciesPage.getSize(), allApproved.getSize());
        assertEquals(speciesPage.getContent(), allApproved.getContent());
        assertEquals(speciesPage, allApproved);
    }

    @Test
    void givenExistingSpecies_whenGetAllApproved_thenListOfSpecies() {
        List<Species> speciesPage = Arrays.asList(getApprovedSpecies(), getApprovedSpecies());

        when(speciesRepository.findAllByApprovedIsTrue()).thenReturn(speciesPage);

        List<Species> allApproved = speciesService.getAllApproved();

        verify(speciesRepository, times(1)).findAllByApprovedIsTrue();
        assertEquals(speciesPage, allApproved);
    }

    @Test
    void givenExistingSpecies_whenGetAllUnapproved_thenListOfSpecies() {
        List<Species> speciesPage = Arrays.asList(getUnapprovedSpecies(), getUnapprovedSpecies());

        when(speciesRepository.findAllByApprovedIsFalse()).thenReturn(speciesPage);

        List<Species> allUnapproved = speciesService.getAllUnapproved();

        verify(speciesRepository, times(1)).findAllByApprovedIsFalse();
        assertEquals(speciesPage, allUnapproved);
    }

    @Test
    void givenUserIsInvalid_whenAddSpecies_thenThrowsException() {
        when(userService.findUserById(any())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> speciesService.addSpecies(new AddSpeciesDto(), any()));
    }

    @Test
    void givenSpeciesAlreadyExists_whenAddSpecies_thenThrowsException() {
        AddSpeciesDto addSpeciesDto = getAddSpeciesDto();

        when(speciesRepository.findByName(addSpeciesDto.getName())).thenReturn(Optional.of(new Species()));

        assertThrows(SpeciesAlreadyExistsException.class, () -> speciesService.addSpecies(addSpeciesDto, UUID.randomUUID()));
    }

    @Test
    void givenCorrectDataAndAdminUser_whenAddSpecies_thenApprovedAndSuccess() {
        AddSpeciesDto addSpeciesDto = getAddSpeciesDto();
        Species unapprovedSpecies = getUnapprovedSpecies();
        User user = User.builder().role(new UserRole(RoleName.ADMIN)).build();
        user.setId(UUID.randomUUID());


        when(userService.findUserById(user.getId())).thenReturn(user);
        when(speciesRepository.findByName(addSpeciesDto.getName())).thenReturn(Optional.empty());
        when(modelMapper.map(addSpeciesDto, Species.class)).thenReturn(unapprovedSpecies);
        when(generaService.getGeneraByName(addSpeciesDto.getGenera())).thenReturn(getGenera());

        speciesService.addSpecies(addSpeciesDto, user.getId());

        verify(userService, times(1)).findUserById(user.getId());
        verify(speciesRepository, times(1)).findByName(addSpeciesDto.getName());
        verify(modelMapper, times(1)).map(addSpeciesDto, Species.class);
        verify(generaService, times(1)).getGeneraByName(addSpeciesDto.getGenera());
        verify(speciesRepository, times(1)).save(unapprovedSpecies);
        assertTrue(unapprovedSpecies.isApproved());

    }

    @Test
    void givenCorrectDataAndRegularUser_whenAddSpecies_thenSuccess() {
        AddSpeciesDto addSpeciesDto = getAddSpeciesDto();
        Species unapprovedSpecies = getUnapprovedSpecies();
        User user = User.builder().role(new UserRole(RoleName.USER)).build();
        user.setId(UUID.randomUUID());


        when(userService.findUserById(user.getId())).thenReturn(user);
        when(speciesRepository.findByName(addSpeciesDto.getName())).thenReturn(Optional.empty());
        when(modelMapper.map(addSpeciesDto, Species.class)).thenReturn(unapprovedSpecies);
        when(generaService.getGeneraByName(addSpeciesDto.getGenera())).thenReturn(getGenera());

        speciesService.addSpecies(addSpeciesDto, user.getId());

        verify(userService, times(1)).findUserById(user.getId());
        verify(speciesRepository, times(1)).findByName(addSpeciesDto.getName());
        verify(modelMapper, times(1)).map(addSpeciesDto, Species.class);
        verify(generaService, times(1)).getGeneraByName(addSpeciesDto.getGenera());
        verify(speciesRepository, times(1)).save(unapprovedSpecies);
        assertFalse(unapprovedSpecies.isApproved());

    }

    @Test
    void givenIncorrectId_whenApprove_thenThrowsException() {
        when(speciesRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> speciesService.approve(UUID.randomUUID()));
    }

    @Test
    void givenCorrectId_whenApprove_thenSuccess() {
        Species unapprovedSpecies = getUnapprovedSpecies();

        when(speciesRepository.findById(unapprovedSpecies.getId())).thenReturn(Optional.of(unapprovedSpecies));

        speciesService.approve(unapprovedSpecies.getId());

        verify(speciesRepository, times(1)).findById(unapprovedSpecies.getId());
        verify(speciesRepository, times(1)).save(unapprovedSpecies);
        assertTrue(unapprovedSpecies.isApproved());
    }

    @Test
    void givenId_whenDelete_thenSuccess() {
        UUID id = UUID.randomUUID();

        speciesService.delete(id);

        verify(speciesRepository, times(1)).deleteById(id);
    }

    @Test
    void givenSpeciesExist_whenGet10RecentlyAdded_thenListOfSpecies() {
        List<Species> speciesList = Arrays.asList(getApprovedSpecies(),
                getApprovedSpecies(),
                getApprovedSpecies());

        when(speciesRepository.find10RecentlyAddedAndApproved()).thenReturn(speciesList);

        List<Species> speciesService10RecentlyAdded = speciesService.get10RecentlyAdded();

        verify(speciesRepository, times(1)).find10RecentlyAddedAndApproved();
        assertEquals(speciesList, speciesService10RecentlyAdded);
    }
}
