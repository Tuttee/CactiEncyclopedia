package com.app.domain.binding;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFactDto {
    @Size(min = 5, message = "Fact content must be at least 5 characters!")
    private String content;

    private UUID addedBy;
}
