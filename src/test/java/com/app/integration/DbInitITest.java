package com.app.integration;

import com.app.domain.entities.UserRole;
import com.app.domain.enums.RoleName;
import com.app.repositories.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DbInitITest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    void rolesAreCreatedAfterStart() {
        List<UserRole> all = roleRepository.findAll();

        assertTrue(all.stream().anyMatch(r -> r.getRoleName().equals(RoleName.ADMIN)));
        assertTrue(all.stream().anyMatch(r -> r.getRoleName().equals(RoleName.USER)));
    }
}
