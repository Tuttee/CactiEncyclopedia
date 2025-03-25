package com.CactiEncyclopedia.integration;

import com.CactiEncyclopedia.domain.binding.AddGeneraDto;
import com.CactiEncyclopedia.domain.binding.AddSpeciesDto;
import com.CactiEncyclopedia.domain.binding.UserRegisterDto;
import com.CactiEncyclopedia.domain.entities.Genera;
import com.CactiEncyclopedia.domain.entities.Species;
import com.CactiEncyclopedia.domain.entities.User;
import com.CactiEncyclopedia.repositories.GeneraRepository;
import com.CactiEncyclopedia.repositories.SpeciesRepository;
import com.CactiEncyclopedia.repositories.UserRepository;
import com.CactiEncyclopedia.services.GeneraService;
import com.CactiEncyclopedia.services.SpeciesService;
import com.CactiEncyclopedia.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.CactiEncyclopedia.TestBuilder.*;
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
