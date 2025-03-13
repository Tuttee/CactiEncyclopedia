package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.binding.UserRegisterDto;
import com.CactiEncyclopedia.domain.entities.Species;
import com.CactiEncyclopedia.domain.entities.User;
import com.CactiEncyclopedia.domain.view.UserDetailsViewModel;
import com.CactiEncyclopedia.exception.EmailAlreadyExistsException;
import com.CactiEncyclopedia.exception.UsernameAlreadyExistsException;
import com.CactiEncyclopedia.repositories.UserRepository;
import com.CactiEncyclopedia.security.AuthenticationMetadata;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public boolean existsByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public void register(UserRegisterDto userRegisterDto) {
        if (existsByUsername(userRegisterDto.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }

        if (existsByEmail(userRegisterDto.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();
        user.setUsername(userRegisterDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        user.setEmail(userRegisterDto.getEmail());
        user.setFirstName(userRegisterDto.getFirstName());
        user.setLastName(userRegisterDto.getLastName());
        user.setRole(isDbInit() ? roleService.getUserRole() : roleService.getAdminRole());

        this.userRepository.saveAndFlush(user);
    }

    private boolean isDbInit() {
        return this.userRepository.count() > 0;
    }

    @Transactional
    public UserDetailsViewModel getLoggedUserDetails(UUID userId) {
        User loggedUser = findUserById(userId);

        return mapToUserDetailsViewModel(loggedUser);
    }

    public User findUserById(UUID userId) {
        return this.userRepository.findById(userId).orElseThrow();
    }


    public List<UserDetailsViewModel> getAllUsersExceptLogged(UUID id) {
        return this.userRepository.findAllByIdNot(id)
                .stream().map(UserService::mapToUserDetailsViewModel)
                .toList();
    }

    private static UserDetailsViewModel mapToUserDetailsViewModel(User user) {
        UserDetailsViewModel userDetailsViewModel = new UserDetailsViewModel();

        userDetailsViewModel.setUsername(user.getUsername());
        userDetailsViewModel.setFirstName(user.getFirstName());
        userDetailsViewModel.setLastName(user.getLastName());
        userDetailsViewModel.setEmail(user.getEmail());
        userDetailsViewModel.setRole(user.getRole().getRoleName());
        userDetailsViewModel.setAddedSpecies(user.getAddedSpecies());
        return userDetailsViewModel;
    }

    public void updateUserRole(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();

        switch (user.getRole().getRoleName()) {
            case USER -> user.setRole(roleService.getAdminRole());
            case ADMIN -> user.setRole(roleService.getUserRole());
        }

        this.userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

        return new AuthenticationMetadata(user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole(),
                true);
    }

    @Transactional
    public void saveSpeciesToUser(User user, Species species) {
        user.getAddedSpecies().add(species);
        this.userRepository.save(user);
    }
}
