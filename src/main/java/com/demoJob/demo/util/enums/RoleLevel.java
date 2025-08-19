package com.demoJob.demo.util.enums;

import lombok.Getter;

@Getter
public enum RoleLevel {
    ADMIN(4),
    MANAGER(3),
    HR(2),
    USER(1);

    private final int level;

    RoleLevel(int level) {
        this.level = level;
    }

    public static RoleLevel fromRoleName(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Vai trò không được để trống");
        }
        String cleanName = roleName.toUpperCase().replace("ROLE_", "").trim();
        try {
            return RoleLevel.valueOf(cleanName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Vai trò không hợp lệ: " + roleName);
        }
    }
}
