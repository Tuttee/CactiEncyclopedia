package com.app.repositories;

import com.app.domain.entities.Genera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeneraRepository extends JpaRepository<Genera, UUID> {
    Optional<Genera> findByName(String family);

    @Query("select distinct g from Genera g join g.speciesList s where s.approved=true order by g.name asc")
    Page<Genera> findAllByApprovedSpeciesCountMoreThan0(Pageable pageable);
}
