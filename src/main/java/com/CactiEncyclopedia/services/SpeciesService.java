package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.binding.AddSpeciesDto;
import com.CactiEncyclopedia.domain.entities.Species;
import com.CactiEncyclopedia.domain.entities.User;
import com.CactiEncyclopedia.domain.enums.RoleName;
import com.CactiEncyclopedia.exception.SpeciesAlreadyExistsException;
import com.CactiEncyclopedia.repositories.SpeciesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Species> getApprovedSpeciesByGenera(String genera, Pageable pageable) {
        return this.speciesRepository.findAllByGenera_NameAndApprovedIsTrue(genera, pageable);
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
    public Page<Species> getAllApproved(Pageable pageable) {
        return this.speciesRepository.findAllByApprovedIsTrue(pageable);
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

        if (speciesRepository.findByName(addSpeciesDto.getName()).isPresent()) {
            throw new SpeciesAlreadyExistsException(addSpeciesDto.getName());
        }

        Species species = modelMapper.map(addSpeciesDto, Species.class);

        species.setCreatedBy(user);
        species.setGenera(generaService.getGeneraByName(addSpeciesDto.getGenera()));
        species.setAddedOn(LocalDate.now());

        if (user.getRole().getRoleName().equals(RoleName.ADMIN)) {
            species.setApproved(true);
        }

        this.speciesRepository.save(species);
    }

    @CacheEvict(value = {"all-species", "species-by-genera", "genera"}, allEntries = true)
    public void approve(UUID id) {
        Species species = this.speciesRepository.findById(id).orElseThrow();
        species.setApproved(true);
        this.speciesRepository.save(species);
    }

    @CacheEvict(value = {"all-species", "species-by-genera", "genera"}, allEntries = true)
    public void delete(UUID id) {
        this.speciesRepository.deleteById(id);
    }

    public List<Species> get10RecentlyAdded() {
        return this.speciesRepository.find10RecentlyAddedAndApproved();
    }
}
