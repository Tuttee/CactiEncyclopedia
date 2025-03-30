package com.app.event.payload;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class FactAddedEvent {
//    @Size(min = 5, message = "Fact content must be at least 5 characters!")
    private String content;

    private UUID addedBy;
    private LocalDateTime createdOn;
}
