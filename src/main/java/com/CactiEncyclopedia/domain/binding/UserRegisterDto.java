package com.CactiEncyclopedia.domain.binding;

import com.CactiEncyclopedia.validation.emailIsUnique.EmailIsUnique;
import com.CactiEncyclopedia.validation.passwordMatch.PasswordMatch;
import com.CactiEncyclopedia.validation.usernameIsUnique.UsernameIsUnique;
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
public class UserRegisterDto {
    @Size(min = 5, max = 20, message = "Username length must be between 5 and 20 characters!")
//    @UsernameIsUnique
    private String username;

    @Size(min = 8, max = 40, message = "Password length must be between 8 and 40 characters!")
    private String password;

//    @Size(min = 8, max = 40, message = "")
    private String confirmPassword;

    @NotBlank(message = "Email cannot be empty!")
    @Email
//    @EmailIsUnique
    private String email;

    @Size(min = 2, max = 50, message = "First name length must be between 2 and 50 characters!")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name length must be between 2 and 50 characters!")
    private String lastName;
}
