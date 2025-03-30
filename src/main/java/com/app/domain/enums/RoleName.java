package com.app.domain.enums;

public enum RoleName {
    USER("User"),
    ADMIN("Admin");

    public final String label;

    RoleName(String label) {
        this.label = label;
    }
}
