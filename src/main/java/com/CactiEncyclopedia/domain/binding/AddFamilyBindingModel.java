package com.CactiEncyclopedia.domain.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddFamilyBindingModel {
    @Size(min = 5, max = 200)
    private String name;

    @NotBlank
    @URL
    private String imageURL;
}
