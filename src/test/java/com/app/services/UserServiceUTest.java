package com.app.services;

import com.app.domain.binding.UserRegisterDto;
import com.app.domain.entities.User;
import com.app.domain.enums.RoleName;
import com.app.domain.view.UserDetailsViewModel;
import com.app.exception.EmailAlreadyExistsException;
import com.app.exception.UsernameAlreadyExistsException;
import com.app.repositories.UserRepository;
import com.app.security.AuthenticationMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.internal.util.Assert;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static com.app.TestBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void givenMissingUserFromDb_whenFindById_thenExceptionIsThrown() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.findUserById(userId));
    }

    @Test
    void givenExistingUserFromDb_whenFindById_thenUserIsFound() {
        User admin = getAdmin();
        when(userRepository.findById(admin.getId()))
                .thenReturn(Optional.of(admin));

        User userById = userService.findUserById(admin.getId());
        assertEquals(admin.getId(), userById.getId());
        assertEquals(admin.getUsername(), userById.getUsername());
    }

    @Test
    void givenUsernameExists() {
        when(userRepository.existsByUsername(getAdmin().getUsername())).thenReturn(true);

        Assert.isTrue(this.userService.existsByUsername(getAdmin().getUsername()));
    }

    @Test
    void givenUsernameNotExists() {
        when(userRepository.existsByUsername(getAdmin().getUsername())).thenReturn(false);

        assertFalse(this.userService.existsByUsername(getAdmin().getUsername()));
    }

    @Test
    void givenEmailExists() {
        when(userRepository.existsByEmail(getAdmin().getEmail())).thenReturn(true);

        assertTrue(this.userService.existsByEmail(getAdmin().getEmail()));
    }

    @Test
    void givenEmailNotExists() {
        when(userRepository.existsByEmail(getAdmin().getEmail())).thenReturn(false);

        assertFalse(this.userService.existsByEmail(getAdmin().getEmail()));
    }

    @Test
    void givenExistingUsername_whenRegister_throws() {
        when(userRepository.existsByUsername(any())).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.register(getAdminRegisterDto()));
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void givenExistingEmail_whenRegister_throws() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(getAdminRegisterDto()));
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void givenCorrectData_whenFirstUserRegister_successAdmin() {
        UserRegisterDto userRegisterDto = getAdminRegisterDto();

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);

        User adminUser = getAdmin();
        when(userRepository.saveAndFlush(any())).thenReturn(adminUser);
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(any())).thenReturn("password");

        userService.register(userRegisterDto);

        verify(roleService, times(1)).getAdminRole();
        verify(roleService, never()).getUserRole();
        verify(userRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void givenCorrectData_whenSecondUserRegister_successUser() {
        UserRegisterDto userRegisterDto = getUserRegisterDto();

        User user = getUser();
        when(userRepository.saveAndFlush(any())).thenReturn(user);
        when(userRepository.count()).thenReturn(1L);
        when(passwordEncoder.encode(any())).thenReturn("password");

        userService.register(userRegisterDto);

        verify(roleService, times(1)).getUserRole();
        verify(roleService, never()).getAdminRole();
        verify(userRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void givenMissingUserFromDatabase_whenLoadUserByUsername_thenExceptionIsThrown() {

        String username = "test123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
    }

    @Test
    void givenExistingUserFromDatabase_whenLoadUserByUsername_thenAuthenticationMetadataIsReturned() {

        User adminUser = getAdmin();
        adminUser.setId(UUID.randomUUID());

        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.of(adminUser));

        UserDetails authenticationMetadata = userService.loadUserByUsername(adminUser.getUsername());

        assertInstanceOf(AuthenticationMetadata.class, authenticationMetadata);
        AuthenticationMetadata result = (AuthenticationMetadata) authenticationMetadata;

        assertEquals(adminUser.getUsername(), result.getUsername());
        assertEquals(adminUser.getPassword(), result.getPassword());
        assertEquals(adminUser.getRole(), result.getRole());
        assertEquals(adminUser.getId(), result.getUserId());
        assertEquals(result.getAuthorities().size(), 1);
        assertEquals("ROLE_" + RoleName.ADMIN, result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void givenIncorrectId_WhenGetLoggedUserDetails_throwsException() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.getLoggedUserDetails(userId));
    }

    @Test
    void givenCorrectId_WhenGetLoggedUserDetails_returns() {
        User admin = getAdmin();

        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));

        UserDetailsViewModel loggedUserDetails = userService.getLoggedUserDetails(admin.getId());

        assertEquals(admin.getUsername(), loggedUserDetails.getUsername());
        assertEquals(admin.getEmail(), loggedUserDetails.getEmail());
        assertEquals(admin.getFirstName(), loggedUserDetails.getFirstName());
        assertEquals(admin.getLastName(), loggedUserDetails.getLastName());
        assertEquals(admin.getRole().getRoleName(), loggedUserDetails.getRole());
        assertEquals(admin.getAddedSpecies(), loggedUserDetails.getAddedSpecies());
    }

    @Test
    void givenInvalidUsername_whenUpdateUserRole_throwsException() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.updateUserRole(getAdmin().getUsername()));
    }

    @Test
    void giveUserWithRoleAdmin_whenUpdateUserRole_thenUserReceivesUserRole() {
        User admin = getAdmin();

        when(userRepository.findByUsername(admin.getUsername()))
                .thenReturn(Optional.of(admin));
        when(roleService.getUserRole()).thenReturn(getUserRole());

        userService.updateUserRole(admin.getUsername());

        verify(roleService, times(1)).getUserRole();
        assertThat(admin.getRole().getRoleName()).isEqualTo(RoleName.USER);
    }

    @Test
    void giveUserWithRoleUser_whenUpdateUserRole_thenUserReceivesAdminRole() {
        User user = getUser();

        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));
        when(roleService.getAdminRole()).thenReturn(getAdminRole());

        userService.updateUserRole(user.getUsername());

        verify(roleService, times(1)).getAdminRole();
        assertThat(user.getRole().getRoleName()).isEqualTo(RoleName.ADMIN);
    }

    @Test
    void givenExistingUsersInDatabase_whenGetAllUsersExceptLogged_thenReturnAllExceptLogged() {
        User admin = getAdmin();
        List<User> userList = List.of(getUser(), getUser());

        when (userRepository.findAllByIdNot(admin.getId())).thenReturn(userList);

        List<UserDetailsViewModel> allUsersExceptLogged = userService.getAllUsersExceptLogged(admin.getId());

        assertThat(allUsersExceptLogged).hasSize(2);
        assertThat(allUsersExceptLogged.stream().filter(u -> u.getUsername().equals(admin.getUsername()))).hasSize(0);
    }


}
