package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.binding.AddSpeciesBindingModel;
import com.CactiEncyclopedia.domain.entities.Species;
import com.CactiEncyclopedia.domain.entities.User;
import com.CactiEncyclopedia.repositories.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpeciesService {
    private final SpeciesRepository speciesRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final GeneraService generaService;

    public List<Species> getApprovedSpeciesByGenera(String genera) {
        return this.speciesRepository.findAllByGenera_NameAndApprovedIsTrue(genera);
    }

    public Species getSpeciesById(UUID id) {
        return this.speciesRepository.findById(id).orElseThrow();
    }

    public List<Species> getAllApproved() {
        return this.speciesRepository.findAllByApprovedIsTrue();
    }

    public List<Species> getAllUnapproved() {
        return this.speciesRepository.findAllByApprovedIsFalse();
    }

    public void addSpecies(AddSpeciesBindingModel addSpeciesBindingModel, UUID userId) {
        User user = modelMapper.map(userService.getLoggedUserDetails(userId), User.class);

        Species species = modelMapper.map(addSpeciesBindingModel, Species.class);

        species.setCreatedBy(user);
        species.setGenera(generaService.getGeneraByName(addSpeciesBindingModel.getGenera()));
        species.setAddedOn(LocalDate.now());

        userService.saveSpeciesToUser(user, species);
        this.speciesRepository.saveAndFlush(species);
    }

    public void approve(UUID id) {
        Species species = this.speciesRepository.findById(id).orElseThrow();
        species.setApproved(true);
        this.speciesRepository.save(species);
    }

    public void delete(UUID id) {
        this.speciesRepository.deleteById(id);
    }
}
