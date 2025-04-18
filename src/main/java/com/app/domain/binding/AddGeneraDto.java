package com.app.domain.binding;

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
public class AddGeneraDto {
    @Size(min = 5, max = 200, message = "Genera name must be between 5 and 200 characters!")
    private String name;

    @NotBlank
    @URL(message = "Please enter a valid URL!")
    private String imageURL;
}
