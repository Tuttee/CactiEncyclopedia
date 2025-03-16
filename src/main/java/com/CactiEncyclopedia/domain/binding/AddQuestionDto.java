package com.CactiEncyclopedia.domain.binding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddQuestionDto {
    private String content;
    private String speciesId;
}
