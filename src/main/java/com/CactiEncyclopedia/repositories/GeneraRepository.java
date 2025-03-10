package com.CactiEncyclopedia.repositories;

import com.CactiEncyclopedia.domain.entities.Genera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeneraRepository extends JpaRepository<Genera, UUID> {
    Optional<Genera> findByName(String family);

    @Query("select f from Genera f cross join Species s where size(f.speciesList) > 0 and s.approved=true")
    List<Genera> findAllBySpeciesCountMoreThan0();
}
