package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.binding.AddFamilyBindingModel;
import com.CactiEncyclopedia.domain.entities.Family;
import com.CactiEncyclopedia.repositories.FamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyService {
    private final FamilyRepository familyRepository;

    public List<Family> getAllFamiliesWithSpecies() {
        return familyRepository.findAllBySpeciesCountMoreThan1();
    }

    public List<String> getAllFamiliesList() {
        return familyRepository.findAll().stream().map(Family::getName).toList();
    }

    public Family getFamilyByName(String family) {
        return this.familyRepository.findByName(family).orElseThrow();
    }

    public void addFamily(AddFamilyBindingModel addFamilyBindingModel) {
        Family family = new Family();

        family.setName(addFamilyBindingModel.getName());
        family.setImageURL(addFamilyBindingModel.getImageURL());

        familyRepository.save(family);
    }
}
