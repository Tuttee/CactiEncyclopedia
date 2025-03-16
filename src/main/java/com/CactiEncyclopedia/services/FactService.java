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

    public boolean addFact(AddFactDto addFactDto, UUID userId) {
        addFactDto.setAddedBy(userId);
        ResponseEntity<Void> stringResponseEntity = factClient.addFact(addFactDto);
        return stringResponseEntity.getStatusCode().is2xxSuccessful();
    }
}
