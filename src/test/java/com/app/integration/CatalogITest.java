package com.app.integration;

import com.app.domain.binding.AddGeneraDto;
import com.app.domain.binding.AddSpeciesDto;
import com.app.domain.binding.UserRegisterDto;
import com.app.domain.entities.Genera;
import com.app.domain.entities.Species;
import com.app.domain.entities.User;
import com.app.repositories.GeneraRepository;
import com.app.repositories.SpeciesRepository;
import com.app.repositories.UserRepository;
import com.app.services.GeneraService;
import com.app.services.SpeciesService;
import com.app.services.UserService;
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
public class CatalogITest {
    @Autowired
    private UserService userService;

    @Autowired
    private SpeciesService speciesService;

    @Autowired
    private SpeciesRepository speciesRepository;

    @Autowired
    private GeneraService generaService;

    @Autowired
    private GeneraRepository generaRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void addGenera_happyPath() {
        UserRegisterDto userRegisterDto = getAdminRegisterDto();
        userService.register(userRegisterDto);
        AddGeneraDto addGeneraDto = getAddGeneraDto();
        generaService.addGenera(addGeneraDto);

        Optional<Genera> generaByName = generaRepository.findByName(addGeneraDto.getName());

        assertTrue(generaByName.isPresent());
        assertEquals(addGeneraDto.getName(), generaByName.get().getName());
        assertEquals(addGeneraDto.getImageURL(), generaByName.get().getImageURL());

    }

    @Test
    void addSpecies_happyPath() {
        UserRegisterDto userRegisterDto = getAdminRegisterDto();
        AddSpeciesDto addSpeciesDto = getAddSpeciesDto();

        userService.register(userRegisterDto);
        User register = userRepository.findByUsername(userRegisterDto.getUsername()).get();
        AddGeneraDto addGeneraDto = getAddGeneraDto();
        generaService.addGenera(addGeneraDto);
        speciesService.addSpecies(addSpeciesDto, register.getId());

        Optional<Species> byName = speciesRepository.findByName(addSpeciesDto.getName());

        assertTrue(byName.isPresent());
        assertEquals(addSpeciesDto.getName(), byName.get().getName());
        assertEquals(addSpeciesDto.getGenera(), byName.get().getGenera().getName());
        assertEquals(addSpeciesDto.getImageURL(), byName.get().getImageURL());
        assertEquals(addSpeciesDto.getColdHardiness(), byName.get().getColdHardiness());
        assertEquals(addSpeciesDto.getDescription(), byName.get().getDescription());
        assertEquals(addSpeciesDto.getCultivation(), byName.get().getCultivation());
    }


}
