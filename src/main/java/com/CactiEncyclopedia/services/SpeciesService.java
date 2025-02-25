package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.binding.AddSpeciesBindingModel;
import com.CactiEncyclopedia.domain.entities.Species;
import com.CactiEncyclopedia.domain.entities.User;
import com.CactiEncyclopedia.repositories.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpeciesService {
    private final SpeciesRepository speciesRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final FamilyService familyService;

    public List<Species> getApprovedSpeciesByFamily(String family) {
        return this.speciesRepository.findAllByFamily_NameAndApprovedIsTrue(family);
    }

    public Species getSpeciesById(String id) {
        return this.speciesRepository.findById(id).orElseThrow();
    }

    public List<Species> getAllApproved() {
        return this.speciesRepository.findAllByApprovedIsTrue();
    }

    public List<Species> getAllUnapproved() {
        return this.speciesRepository.findAllByApprovedIsFalse();
    }

    public void addSpecies(AddSpeciesBindingModel addSpeciesBindingModel, String userId) {
        User user = modelMapper.map(userService.getLoggedUserDetails(userId), User.class);

        Species species = modelMapper.map(addSpeciesBindingModel, Species.class);

        species.setCreatedBy(user);
        species.setFamily(familyService.getFamilyByName(addSpeciesBindingModel.getFamily()));

        this.speciesRepository.save(species);
    }

    public void approve(String id) {
        Species species = this.speciesRepository.findById(id).orElseThrow();
        species.setApproved(true);
        this.speciesRepository.save(species);
    }

    public void delete(String id) {
        this.speciesRepository.deleteById(id);
    }
}
