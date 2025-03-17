package com.CactiEncyclopedia.repositories;

import com.CactiEncyclopedia.domain.entities.Genera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeneraRepository extends JpaRepository<Genera, UUID> {
    Optional<Genera> findByName(String family);

    Page<Genera> findAllBySpeciesListNotEmpty(Pageable pageable);
}
