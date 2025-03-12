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

    @Query("select g from Genera g cross join Species s where size(g.speciesList) > 0 and s.approved=true order by g.name asc")
    List<Genera> findAllBySpeciesCountMoreThan0();
}
