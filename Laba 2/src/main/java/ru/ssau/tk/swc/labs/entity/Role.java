package ru.ssau.tk.swc.labs.entity;

public enum Role {
    ADMIN,
    USER;

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER; // По умолчанию USER
        }
    }
}
