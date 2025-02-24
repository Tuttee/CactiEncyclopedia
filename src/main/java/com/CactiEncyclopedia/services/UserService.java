package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.binding.UserLoginBindingModel;
import com.CactiEncyclopedia.domain.binding.UserRegisterBindingModel;
import com.CactiEncyclopedia.domain.entities.User;
import com.CactiEncyclopedia.domain.view.UserDetailsViewModel;
import com.CactiEncyclopedia.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
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

    public boolean validateLogin(UserLoginBindingModel userLoginBindingModel) {
        Optional<User> byUsername = this.userRepository.findByUsername(userLoginBindingModel.getUsername());

        if (byUsername.isPresent() && passwordEncoder.matches(userLoginBindingModel.getPassword(), byUsername.get().getPassword())) {
            return true;
        }
        return false;
    }

    public void register(UserRegisterBindingModel userRegisterBindingModel) {
        User user = new User();
        user.setUsername(userRegisterBindingModel.getUsername());
        user.setPassword(passwordEncoder.encode(userRegisterBindingModel.getPassword()));
        user.setEmail(userRegisterBindingModel.getEmail());
        user.setFirstName(userRegisterBindingModel.getFirstName());
        user.setLastName(userRegisterBindingModel.getLastName());
        user.setRole(isDbInit() ? roleService.getUserRole() : roleService.getAdminRole());

        this.userRepository.saveAndFlush(user);
    }

    public User login(UserLoginBindingModel userLoginBindingModel) {
        if (validateLogin(userLoginBindingModel)) {
            return this.userRepository.findByUsername(userLoginBindingModel.getUsername()).get();
        }
        throw new RuntimeException("Invalid username or password");
    }

    private boolean isDbInit() {
        return this.userRepository.count() > 0;
    }

    @Transactional
    public UserDetailsViewModel getLoggedUserDetails(String userId) {
        User loggedUser = this.userRepository.findById(userId).orElseThrow();

        UserDetailsViewModel userDetailsViewModel = new UserDetailsViewModel();

        userDetailsViewModel.setUsername(loggedUser.getUsername());
        userDetailsViewModel.setFirstName(loggedUser.getFirstName());
        userDetailsViewModel.setLastName(loggedUser.getLastName());
        userDetailsViewModel.setEmail(loggedUser.getEmail());
        userDetailsViewModel.setRole(loggedUser.getRole().getRoleName());
        userDetailsViewModel.setAddedSpecies(loggedUser.getAddedSpecies());

        return userDetailsViewModel;
    }
}
