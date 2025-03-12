package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.client.FactClient;
import com.CactiEncyclopedia.domain.binding.AddFactDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FactService {
    private final FactClient factClient;

    public void addFact(AddFactDto addFactDto, UUID userId) {
        addFactDto.setAddedBy(userId);
        ResponseEntity<Void> voidResponseEntity = factClient.addFact(addFactDto);

        if (!voidResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Fact cannot be created");
        }
    }
}
