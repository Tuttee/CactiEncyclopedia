package com.CactiEncyclopedia.repositories;

import com.CactiEncyclopedia.domain.entities.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyRepository extends JpaRepository<Family, String> {
    Optional<Family> findByName(String family);

    @Query("select f from Family f where size(f.speciesList) > 0")
    List<Family> findAllBySpeciesCountMoreThan1();
}
