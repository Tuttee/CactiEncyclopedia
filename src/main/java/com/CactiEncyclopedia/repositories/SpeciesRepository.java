package com.CactiEncyclopedia.repositories;

import com.CactiEncyclopedia.domain.entities.Species;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, UUID> {
    Page<Species> findAllByGenera_NameAndApprovedIsTrue(String genera, Pageable pageable);

    List<Species> findAllByApprovedIsTrue();

    Page<Species> findAllByApprovedIsTrue(Pageable pageable);

    List<Species> findAllByApprovedIsFalse();

    @Query("select s from Species s where s.approved=true order by s.addedOn desc limit 10")
    List<Species> find10RecentlyAddedAndApproved();

    Optional<Species> findByName(String name);
}
