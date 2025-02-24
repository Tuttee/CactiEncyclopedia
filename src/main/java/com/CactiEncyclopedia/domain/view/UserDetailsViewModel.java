package com.CactiEncyclopedia.domain.view;

import com.CactiEncyclopedia.domain.entities.Species;
import com.CactiEncyclopedia.domain.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDetailsViewModel {
    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private RoleName role;

    private List<Species> addedSpecies;
}
