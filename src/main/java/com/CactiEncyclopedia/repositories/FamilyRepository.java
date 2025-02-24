package com.CactiEncyclopedia.repositories;

import com.CactiEncyclopedia.domain.entities.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyRepository extends JpaRepository<Family, String> {
}
