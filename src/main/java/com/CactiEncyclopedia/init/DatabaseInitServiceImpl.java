package com.CactiEncyclopedia.init;

import com.CactiEncyclopedia.services.RoleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatabaseInitServiceImpl {
    private final RoleService roleService;

    @PostConstruct
    public void postConstruct() {
        if (!isDbInit()) {
            initDb();
        }
    }

    public boolean isDbInit() {
        return this.roleService.isDbInit();
    }

    public void initDb() {
        this.roleService.initDb();
    }
}
