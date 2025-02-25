package com.CactiEncyclopedia.repositories;

import com.CactiEncyclopedia.domain.entities.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, String> {
    List<Species> findAllByFamily_NameAndApprovedIsTrue(String family);

    List<Species> findAllByApprovedIsTrue();

    List<Species> findAllByApprovedIsFalse();
}
