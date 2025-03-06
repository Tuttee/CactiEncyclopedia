package com.CactiEncyclopedia.domain.binding;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddQuestionBindingModel {
    private String content;
    private String speciesId;
}
