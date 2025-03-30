package com.app.domain.binding;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddQuestionDto {
    @NotBlank(message = "You cannot publish an empty question!")
    private String content;
    private String speciesId;
}
