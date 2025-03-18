package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.binding.AddGeneraDto;
import com.CactiEncyclopedia.domain.entities.Genera;
import com.CactiEncyclopedia.repositories.GeneraRepository;
import lombok.RequiredArgsConstructor;
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
        return generaRepository.findAll().stream().map(Genera::getName).toList();
    }

    @Cacheable("genera-list")
    public Genera getGeneraByName(String genera) {
        return this.generaRepository.findByName(genera).orElseThrow();
    }

    public void addGenera(AddGeneraDto addGeneraDto) {
        Genera genera = new Genera();

        genera.setName(addGeneraDto.getName());
        genera.setImageURL(addGeneraDto.getImageURL());

        generaRepository.save(genera);
    }
}
