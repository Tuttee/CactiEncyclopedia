package com.app.domain.binding;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddQuestionDto {
    @NotBlank(message = "You cannot publish an empty question!")
    @Length(max = 1000, message = "Max length must be 1000 characters!")
    private String content;
    private String speciesId;
}
