package com.example.backend.model;

public enum MenuPreference {
    STANDARD("Standard Menu"),
    VEGAN("Vegan Menu"),
    KIDS("Kids Menu");

    private final String displayName;

    MenuPreference(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}