package com.CactiEncyclopedia.repositories;

import com.CactiEncyclopedia.domain.entities.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, UUID> {
    List<Species> findAllByGenera_NameAndApprovedIsTrue(String family);

    List<Species> findAllByApprovedIsTrue();

    List<Species> findAllByApprovedIsFalse();

    @Query("select s from Species s where s.approved=true order by s.addedOn desc limit 10")
    List<Species> find10RecentlyAddedAndApproved();
}
