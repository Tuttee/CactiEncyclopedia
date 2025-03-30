package com.app.services;

import com.app.domain.entities.UserRole;
import com.app.domain.enums.RoleName;
import com.app.repositories.RoleRepository;
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

    public UserRole getUserRole() {
        return this.roleRepository.findByRoleName(RoleName.USER).orElseThrow();
    }

    public UserRole getAdminRole() {
        return this.roleRepository.findByRoleName(RoleName.ADMIN).orElseThrow();
    }
}
