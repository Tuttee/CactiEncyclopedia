package com.app.services;

import com.app.domain.binding.AddGeneraDto;
import com.app.domain.entities.Genera;
import com.app.exception.GeneraAlreadyExistsException;
import com.app.repositories.GeneraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneraService {
    private final GeneraRepository generaRepository;

    @Cacheable("genera")
    public Page<Genera> getAllGeneraWithSpecies(Pageable pageable) {
        return generaRepository.findAllByApprovedSpeciesCountMoreThan0(pageable);
    }

    public List<String> getAllGeneraNamesList() {
        return generaRepository.findAll().stream().map(Genera::getName).sorted().toList();
    }

    @Cacheable("genera-list")
    public Genera getGeneraByName(String genera) {
        return this.generaRepository.findByName(genera).orElseThrow();
    }

    @CacheEvict(value = {"genera", "genera-list"})
    public boolean addGenera(AddGeneraDto addGeneraDto) {
        Genera genera = new Genera();

        if (generaRepository.findByName(addGeneraDto.getName()).isPresent()) {
            throw new GeneraAlreadyExistsException("Genera with name " + addGeneraDto.getName() + " already exists!");
        }

        genera.setName(addGeneraDto.getName());
        genera.setImageURL(addGeneraDto.getImageURL());

        generaRepository.save(genera);
        return true;
    }
}
