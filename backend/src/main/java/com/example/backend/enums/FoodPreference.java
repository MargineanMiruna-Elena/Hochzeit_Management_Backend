package com.example.backend.enums;

public enum FoodPreference {
    STANDARD("Standard"),
    VEGAN("Vegan"),
    KIDS("Kids");

    private final String displayName;

    FoodPreference(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}