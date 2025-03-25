package com.CactiEncyclopedia;

import com.CactiEncyclopedia.domain.binding.AddGeneraDto;
import com.CactiEncyclopedia.domain.binding.AddSpeciesDto;
import com.CactiEncyclopedia.domain.binding.UserRegisterDto;
import com.CactiEncyclopedia.domain.entities.*;
import com.CactiEncyclopedia.domain.enums.RoleName;
import com.CactiEncyclopedia.domain.view.UserDetailsViewModel;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class TestBuilder {

    public static UserRole getAdminRole() {
        return new UserRole(RoleName.ADMIN);
    }

    public static UserRole getUserRole() {
        return new UserRole(RoleName.USER);
    }

    public static User getAdmin() {
        User user = new User("admin",
                "adminadmin",
                "admin@user.com",
                "Admin",
                "Adminov",
                getAdminRole(),
                new ArrayList<>(),
                new ArrayList<>());
        user.setId(UUID.randomUUID());
        return user;
    }

    public static User getUser() {
        User user = new User("Testuser",
                "testpass",
                "test@user.com",
                "TestFname",
                "TestLname",
                getUserRole(),
                new ArrayList<>(),
                new ArrayList<>());
        user.setId(UUID.randomUUID());
        return user;
    }

    public static UserRegisterDto getAdminRegisterDto() {

        return new UserRegisterDto(getAdmin().getUsername(),
                getAdmin().getPassword(),
                getAdmin().getPassword(),
                getAdmin().getEmail(),
                getAdmin().getFirstName(),
                getAdmin().getLastName());
    }

    public static UserRegisterDto getUserRegisterDto() {

        return new UserRegisterDto(getUser().getUsername(),
                getUser().getPassword(),
                getUser().getPassword(),
                getUser().getEmail(),
                getUser().getFirstName(),
                getUser().getLastName());
    }

    public static Species getSpecies() {
        Species species = new Species();
        species.setId(UUID.randomUUID());
        species.setName("Species");
        species.setDescription("Species Description");
        species.setCreatedBy(new User());
        species.setAddedOn(LocalDate.now());
        species.setGenera(getGenera());
        species.setColdHardiness("Species Cold Hardiness");
        species.setCultivation("Species Cultivation");

        Question q1 = getQuestion(species);
        Question q2 = getQuestion(species);
        List<Question> questions = Arrays.asList(q1, q2);
        species.setQuestions(questions);

        return species;
    }

    public static Question getQuestion(Species species) {
        return Question.builder()
                .species(species)
                .content("TestContent")
                .approved(true)
                .askedBy(getUser())
                .askedOn(LocalDateTime.now()).build();
    }

    public static Genera getGenera() {
        return new Genera("Mammillaria", "http://", new ArrayList<>());
    }

    public static AddGeneraDto getAddGeneraDto() {
        return new AddGeneraDto(getGenera().getName(), getGenera().getImageURL());
    }

    public static Species getApprovedSpecies() {
        Species species = getSpecies();
        species.setApproved(true);
        return species;
    }

    public static Species getUnapprovedSpecies() {
        Species species = getSpecies();
        species.setApproved(false);
        return species;
    }

    public static AddSpeciesDto getAddSpeciesDto() {
        return new AddSpeciesDto("Species",
                "Mammillaria",
                "http://",
                "Test Description",
                "Test Cultivation",
                "Test cold hardiness",
                false);
    }

    public static UserDetailsViewModel aRandomUserDetailsViewModel() {
        UserDetailsViewModel viewModel = new UserDetailsViewModel();
        viewModel.setUsername("username");
        viewModel.setFirstName("firstname");
        viewModel.setLastName("lastname");
        viewModel.setEmail("email@email.com");
        viewModel.setRole(RoleName.USER);
        return viewModel;
    }
}
