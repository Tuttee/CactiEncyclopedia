package com.CactiEncyclopedia.domain.binding;

import com.CactiEncyclopedia.validation.userAndPasswordMatch.UserAndPasswordMatch;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@UserAndPasswordMatch
public class UserLoginBindingModel {
    @Size(min = 3, max = 20, message = "Username length must be between 3 and 20 characters!")
    private String username;

    @Size(min = 3, max = 20, message = "Password length must be between 3 and 20 characters!")
    private String password;
}
