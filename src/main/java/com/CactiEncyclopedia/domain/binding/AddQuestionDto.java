package com.CactiEncyclopedia.domain.binding;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddQuestionDto {
    private String content;
    private String speciesId;
}
