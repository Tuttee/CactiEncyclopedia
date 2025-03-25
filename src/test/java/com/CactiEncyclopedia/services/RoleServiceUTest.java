package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.entities.UserRole;
import com.CactiEncyclopedia.domain.enums.RoleName;
import com.CactiEncyclopedia.repositories.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.CactiEncyclopedia.TestBuilder.getAdminRole;
import static com.CactiEncyclopedia.TestBuilder.getUserRole;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceUTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void givenDbIsInit_whenIsDbInit_thenReturnTrue() {
        when(roleRepository.count()).thenReturn(1L);

        boolean dbInit = roleService.isDbInit();

        assertTrue(dbInit);
    }

    @Test
    void givenDbIsNotInit_whenIsDbInit_thenReturnFalse() {
        when(roleRepository.count()).thenReturn(0L);

        boolean dbInit = roleService.isDbInit();

        assertFalse(dbInit);
    }

    @Test
    void givenNoDb_whenInitDb_thenSuccess() {
        roleService.initDb();

        verify(roleRepository, times(1)).saveAllAndFlush(any());
    }

    @Test
    void givenHappyPath_whenGetAdminRole() {
        UserRole expected = getAdminRole();

        when(roleRepository.findByRoleName(RoleName.ADMIN)).thenReturn(Optional.of(expected));

        UserRole actual = roleService.getAdminRole();

        verify(roleRepository, times(1)).findByRoleName(RoleName.ADMIN);
        assertEquals(expected, actual);
    }

    @Test
    void givenRoleNotExist_whenGetAdminRole() {
        when(roleRepository.findByRoleName(any())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> roleService.getAdminRole());
    }

    @Test
    void givenHappyPath_whenGetUserRole() {
        UserRole expectedUser = getUserRole();

        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(Optional.of(expectedUser));

        UserRole actualAdmin = roleService.getUserRole();

        verify(roleRepository, times(1)).findByRoleName(RoleName.USER);
        assertEquals(expectedUser, actualAdmin);
    }
}
