package com.CactiEncyclopedia.repositories;

import com.CactiEncyclopedia.domain.entities.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyRepository extends JpaRepository<Family, UUID> {
    Optional<Family> findByName(String family);

    @Query("select f from Family f cross join Species s where size(f.speciesList) > 0 and s.approved=true")
    List<Family> findAllBySpeciesCountMoreThan1();
}
