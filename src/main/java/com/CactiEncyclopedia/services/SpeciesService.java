package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.binding.AddSpeciesDto;
import com.CactiEncyclopedia.domain.entities.Species;
import com.CactiEncyclopedia.domain.entities.User;
import com.CactiEncyclopedia.domain.enums.RoleName;
import com.CactiEncyclopedia.repositories.SpeciesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
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

    //to test with Cacheable
    @Cacheable("species-by-genera")
    public List<Species> getApprovedSpeciesByGenera(String genera) {
        return this.speciesRepository.findAllByGenera_NameAndApprovedIsTrue(genera);
    }

    public Species getSpeciesById(UUID id) {
        Species species = this.speciesRepository.findById(id).orElseThrow();

        return sortSpeciesQuestionsByDate(species);
    }

    private Species sortSpeciesQuestionsByDate(Species species) {
        species.getQuestions().sort((q1, q2) -> q2.getAskedOn().compareTo(q1.getAskedOn()));
        return species;
    }

    @Cacheable("all-species")
    public List<Species> getAllApproved() {
        return this.speciesRepository.findAllByApprovedIsTrue();
    }


    public List<Species> getAllUnapproved() {
        return this.speciesRepository.findAllByApprovedIsFalse();
    }

    @Transactional
    public void addSpecies(AddSpeciesDto addSpeciesDto, UUID userId) {
        User user = userService.findUserById(userId);

        Species species = modelMapper.map(addSpeciesDto, Species.class);

        species.setCreatedBy(user);
        species.setGenera(generaService.getGeneraByName(addSpeciesDto.getGenera()));
        species.setAddedOn(LocalDate.now());

        if (user.getRole().getRoleName().equals(RoleName.ADMIN)) {
            species.setApproved(true);
        }

//        userService.saveSpeciesToUser(userId, species);
        this.speciesRepository.save(species);
    }

    public void approve(UUID id) {
        Species species = this.speciesRepository.findById(id).orElseThrow();
        species.setApproved(true);
        this.speciesRepository.save(species);
    }

    public void delete(UUID id) {
        this.speciesRepository.deleteById(id);
    }

    public List<Species> get10RecentlyAdded() {
        return this.speciesRepository.find10RecentlyAddedAndApproved();
    }
}
