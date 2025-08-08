package com.demoJob.demo.util;

public enum RoleLevel {
    ADMIN,
    HR,
    EMPLOYER,
    USER;

    public static RoleLevel fromRoleName(String roleName) {
        try {
            return RoleLevel.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Vai trò không hợp lệ: " + roleName);
        }
    }
}
