package com.CactiEncyclopedia.integration;

import com.CactiEncyclopedia.domain.binding.UserRegisterDto;
import com.CactiEncyclopedia.domain.entities.User;
import com.CactiEncyclopedia.repositories.UserRepository;
import com.CactiEncyclopedia.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.CactiEncyclopedia.TestBuilder.getAdminRegisterDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserITest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void addGenera_happyPath() {
        UserRegisterDto userRegisterDto = getAdminRegisterDto();
        userService.register(userRegisterDto);

        Optional<User> byUsername = userRepository.findByUsername(userRegisterDto.getUsername());

        assertTrue(byUsername.isPresent());
        assertEquals(userRegisterDto.getUsername(), byUsername.get().getUsername());
        assertEquals(userRegisterDto.getEmail(), byUsername.get().getEmail());
        assertEquals(userRegisterDto.getFirstName(), byUsername.get().getFirstName());
        assertEquals(userRegisterDto.getLastName(), byUsername.get().getLastName());
    }
}
