package com.CactiEncyclopedia.domain.binding;

import com.CactiEncyclopedia.domain.entities.BaseEntity;
import com.CactiEncyclopedia.domain.entities.Species;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.List;

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
