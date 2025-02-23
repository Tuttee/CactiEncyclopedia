package com.CactiEncyclopedia.services;

import com.CactiEncyclopedia.domain.entities.UserRole;
import com.CactiEncyclopedia.domain.enums.RoleName;
import com.CactiEncyclopedia.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public boolean isDbInit() {
        return roleRepository.count() > 0;
    }

    public void initDb() {
         this.roleRepository.saveAllAndFlush(Arrays.stream(RoleName.values())
                 .map(UserRole::new)
                 .toList());
    }

}
