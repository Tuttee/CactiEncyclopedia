package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.binding.AddGeneraDto;
import com.CactiEncyclopedia.domain.entities.Genera;
import com.CactiEncyclopedia.exception.GeneraAlreadyExistsException;
import com.CactiEncyclopedia.repositories.GeneraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GeneraServiceUTest {
    @Mock
    private GeneraRepository generaRepository;

    @InjectMocks
    private GeneraService generaService;

    @Test
    void givenExistingGenera_whenGetAllGeneraWithSpecies_thenPageOfGenera() {
        Pageable pageable = Pageable.unpaged();
        Genera genera = getGenera("Mammilarria");
        Page<Genera> generaPage = new PageImpl<>(Arrays.asList(genera, genera));

        when(generaRepository.findAllByApprovedSpeciesCountMoreThan0(pageable))
                .thenReturn(generaPage);

        Page<Genera> result = generaService.getAllGeneraWithSpecies(pageable);

        verify(generaRepository, times(1))
                .findAllByApprovedSpeciesCountMoreThan0(pageable);
        assertEquals(generaPage.getTotalElements(), result.getTotalElements());
        assertEquals(generaPage.getTotalPages(), result.getTotalPages());
        assertEquals(generaPage.getNumber(), result.getNumber());
        assertEquals(generaPage.getSize(), result.getSize());
        assertEquals(generaPage.getContent(), result.getContent());
        assertEquals(generaPage, result);
    }

    @Test
    void givenExistingGenera_whenGetAllGeneraNamesList_thenListOfGeneraNames() {
        List<Genera> list = Arrays.asList(getGenera("Mammillaria"), getGenera("Ferocactus"));

        when(generaRepository.findAll())
                .thenReturn(list);

        List<String> result = generaService.getAllGeneraNamesList();

        verify(generaRepository, times(1))
                .findAll();
        assertEquals(list.size(), result.size());
        assertEquals(list.get(0).getName(), result.get(0));
        assertEquals(list.get(1).getName(), result.get(1));
    }

    @Test
    void givenInvalidName_whenGetGeneraByName_thenThrowException() {
        when(generaRepository.findByName(any()))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> generaService.getGeneraByName(any()));
    }

    @Test
    void givenHappyPath_whenGetGeneraByName() {
        Genera genera = getGenera("Mammillaria");

        when(generaRepository.findByName(genera.getName()))
                .thenReturn(Optional.of(genera));

        Genera result = generaService.getGeneraByName(genera.getName());

        verify(generaRepository, times(1))
                .findByName(genera.getName());
        assertEquals(genera, result);
    }

    @Test
    void givenNameExists_whenAddGenera_thenThrowException() {
        Genera genera = getGenera("Mammilarria");
        AddGeneraDto addGeneraDto = new AddGeneraDto();
        addGeneraDto.setName(genera.getName());
        addGeneraDto.setImageURL(genera.getImageURL());
        when(generaRepository.findByName(addGeneraDto.getName())).thenReturn(Optional.of(genera));

        assertThrows(GeneraAlreadyExistsException.class, () -> generaService.addGenera(addGeneraDto));
    }

    @Test
    void givenHappyPath_whenAddGenera() {
        Genera genera = getGenera("Mammilarria");
        AddGeneraDto addGeneraDto = new AddGeneraDto();
        addGeneraDto.setName(genera.getName());
        addGeneraDto.setImageURL(genera.getImageURL());

        when(generaRepository.findByName(addGeneraDto.getName())).thenReturn(Optional.empty());

        generaService.addGenera(addGeneraDto);

        verify(generaRepository, times(1)).findByName(addGeneraDto.getName());
        verify(generaRepository, times(1)).save(any(Genera.class));
    }

    private Genera getGenera(String name) {
        return new Genera(name, "http://", new ArrayList<>());
    }


}
