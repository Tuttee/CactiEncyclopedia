package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.entities.Species;
import com.CactiEncyclopedia.repositories.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpeciesService {
    private final SpeciesRepository speciesRepository;

    public List<Species> getSpeciesByFamily(String family) {
        return this.speciesRepository.findAllByFamily_Name(family);
    }
}
