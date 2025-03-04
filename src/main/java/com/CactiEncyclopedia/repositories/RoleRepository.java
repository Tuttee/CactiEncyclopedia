package com.CactiEncyclopedia.repositories;

import com.CactiEncyclopedia.domain.entities.UserRole;
import com.CactiEncyclopedia.domain.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<UserRole, UUID> {

    UserRole findByRoleName(RoleName roleName);
}
