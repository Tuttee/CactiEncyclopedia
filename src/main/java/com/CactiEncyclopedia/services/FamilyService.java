package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.entities.Family;
import com.CactiEncyclopedia.repositories.FamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyService {
    private final FamilyRepository familyRepository;

    public List<Family> getAllFamilies() {
        return familyRepository.findAll();
    }
}
