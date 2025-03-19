package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.binding.AddSpeciesDto;
import com.CactiEncyclopedia.domain.binding.UserRegisterDto;
import com.CactiEncyclopedia.domain.entities.*;
import com.CactiEncyclopedia.domain.enums.RoleName;
import com.CactiEncyclopedia.exception.SpeciesAlreadyExistsException;
import com.CactiEncyclopedia.repositories.SpeciesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

        speciesService.addSpecies(addSpeciesDto, user.getId());

        verify(userService, times(1)).findUserById(user.getId());
        verify(speciesRepository, times(1)).findByName(addSpeciesDto.getName());
        verify(modelMapper, times(1)).map(addSpeciesDto, Species.class);
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

        speciesService.addSpecies(addSpeciesDto, user.getId());

        verify(userService, times(1)).findUserById(user.getId());
        verify(speciesRepository, times(1)).findByName(addSpeciesDto.getName());
        verify(modelMapper, times(1)).map(addSpeciesDto, Species.class);
        verify(speciesRepository, times(1)).save(unapprovedSpecies);
        assertFalse(unapprovedSpecies.isApproved());

    }


//    @Test
//    void givenCorrectData_whenFirstUserRegister_successAdmin() {
//        UserRegisterDto userRegisterDto = getAdminRegisterDto();
//
//        when(userRepository.existsByUsername(any())).thenReturn(false);
//        when(userRepository.existsByEmail(any())).thenReturn(false);
//
//        User adminUser = getAdmin();
//        when(userRepository.saveAndFlush(any())).thenReturn(adminUser);
//        when(userRepository.count()).thenReturn(0L);
//
//        userService.register(userRegisterDto);
//
//        verify(roleService, times(1)).getAdminRole();
//        verify(roleService, never()).getUserRole();
//        verify(userRepository, times(1)).saveAndFlush(any());
//    }

    private AddSpeciesDto getAddSpeciesDto() {
        return new AddSpeciesDto("Species",
                "Mammillaria",
                "http://",
                "Test Description",
                "Test Cultivation",
                "Test cold hardiness",
                false);
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
        return new Genera("Mammillaria", "http://", new ArrayList<>());
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
}
