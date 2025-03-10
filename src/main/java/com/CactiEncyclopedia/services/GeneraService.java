package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.binding.AddGeneraBindingModel;
import com.CactiEncyclopedia.domain.entities.Genera;
import com.CactiEncyclopedia.repositories.GeneraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneraService {
    private final GeneraRepository generaRepository;

    public List<Genera> getAllGeneraWithSpecies() {
        return generaRepository.findAllBySpeciesCountMoreThan0();
    }

    public List<String> getAllGeneraNamesList() {
        return generaRepository.findAll().stream().map(Genera::getName).toList();
    }

    public Genera getGeneraByName(String genera) {
        return this.generaRepository.findByName(genera).orElseThrow();
    }

    public void addGenera(AddGeneraBindingModel addGeneraBindingModel) {
        Genera genera = new Genera();

        genera.setName(addGeneraBindingModel.getName());
        genera.setImageURL(addGeneraBindingModel.getImageURL());

        generaRepository.save(genera);
    }
}
