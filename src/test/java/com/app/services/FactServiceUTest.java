package com.app.services;

import com.app.client.FactClient;
import com.app.domain.binding.AddFactDto;
import com.app.event.FactAddedEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FactServiceUTest {
    @Mock
    private FactClient factClient;

    @Mock
    private FactAddedEventProducer factAddedEventProducer;

    @InjectMocks
    private FactService factService;

    @Test
    void givenCorrectData_whenAddFact_thenReturnsResponseCreated() {
        AddFactDto addFactDto = new AddFactDto();

        // Lines are for when FeignClient is being used
//        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
//        when(factClient.addFact(addFactDto)).thenReturn(responseEntity);

        boolean isFactAdded = factService.addFact(addFactDto, UUID.randomUUID());

        // Line is for when FeignClient is being used
//        verify(factClient, times(1)).addFact(addFactDto);
        assertTrue(isFactAdded);
    }

    //Test for the case when using FeignClient
//    @Test
//    void givenAPINotWorking_whenAddFact_thenThrowsException() {
//        AddFactDto addFactDto = new AddFactDto();
//
//        when(factClient.addFact(addFactDto)).thenReturn(null);
//
//        assertThrows(RuntimeException.class, () -> factService.addFact(addFactDto, UUID.randomUUID()));
//    }

}
