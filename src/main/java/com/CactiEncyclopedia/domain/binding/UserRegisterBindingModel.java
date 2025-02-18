package com.CactiEncyclopedia.domain.binding;

import com.example.spotifyplaylistapp.validation.emailIsUnique.EmailIsUnique;
import com.example.spotifyplaylistapp.validation.passwordMatch.PasswordMatch;
import com.example.spotifyplaylistapp.validation.usernameIsUnique.UsernameIsUnique;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@PasswordMatch(password = "password", confirmPassword = "confirmPassword")
public class UserRegisterBindingModel {
    @Size(min = 3, max = 20, message = "Username length must be between 3 and 20 characters!")
    @UsernameIsUnique
    private String username;

    @Size(min = 3, max = 20, message = "Password length must be between 3 and 20 characters!")
    private String password;

    @Size(min = 3, max = 20, message = "")
    private String confirmPassword;

    @NotBlank(message = "Email cannot be empty!")
    @Email
    @EmailIsUnique
    private String email;

    private String firstName;

    private String lastName;
}
