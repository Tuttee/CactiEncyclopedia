package com.CactiEncyclopedia.repositories;

import com.CactiEncyclopedia.domain.entities.UserRole;
import com.CactiEncyclopedia.domain.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<UserRole, String> {

    UserRole findByRoleName(RoleName roleName);
}
