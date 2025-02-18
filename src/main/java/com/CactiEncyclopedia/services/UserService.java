package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.binding.UserLoginBindingModel;
import com.CactiEncyclopedia.domain.binding.UserRegisterBindingModel;
import com.CactiEncyclopedia.domain.entities.User;
import com.CactiEncyclopedia.repositories.UserRepository;
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

        this.userRepository.saveAndFlush(user);
    }

//    public void login(UserLoginBindingModel userLoginBindingModel) {
//        if (validateLogin(userLoginBindingModel)) {
//            User user = this.userRepository.findByUsername(userLoginBindingModel.getUsername()).get();
//            loggedUser.setId(user.getId());
//            loggedUser.setUsername(user.getUsername());
//        }
//    }
}
