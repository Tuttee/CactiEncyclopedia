package com.app.domain.binding;

import jakarta.persistence.Column;
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
public class AddSpeciesDto {
    @Size(min = 3, max = 200, message = "Name length must be between 3 and 200 characters!")
    private String name;

    @NotBlank(message = "Selecting a family is mandatory!")
    private String genera;

    @NotBlank(message = "Please enter a valid URL!")
    @URL(message = "Please enter a valid URL!")
    private String imageURL;

    @Size(min = 50, max = 2000, message = "Description must be between 50 and 2000 characters.")
    private String description;

    @Size(max = 2000, message = "Cultivation must be maximum of 2000 characters.")
    private String cultivation;

    @Size(max = 1000, message = "Cold hardiness must be maximum of 1000 characters.")
    private String coldHardiness;

    @Column
    private boolean approved = false;

}
