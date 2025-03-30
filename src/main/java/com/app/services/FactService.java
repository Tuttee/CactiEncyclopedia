package com.app.services;

import com.app.client.FactClient;
import com.app.domain.binding.AddFactDto;
import com.app.event.FactAddedEventProducer;
import com.app.event.payload.FactAddedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FactService {
    private final FactClient factClient;
    private final FactAddedEventProducer factAddedEventProducer;

    public boolean addFact(AddFactDto addFactDto, UUID userId) {
        addFactDto.setAddedBy(userId);

        //Using FeignClient:
//        ResponseEntity<Void> stringResponseEntity = factClient.addFact(addFactDto);
//        return stringResponseEntity.getStatusCode().isSameCodeAs(HttpStatus.CREATED);

        //Using Kafka
        FactAddedEvent factAddedEvent = FactAddedEvent.builder().addedBy(addFactDto.getAddedBy())
                .content(addFactDto.getContent())
                .createdOn(LocalDateTime.now())
                .build();
        factAddedEventProducer.sendEvent(factAddedEvent);
        return true;
    }
}
