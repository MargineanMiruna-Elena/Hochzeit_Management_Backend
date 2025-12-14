package com.example.backend.enums;

public enum FoodPreference {
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    GLUTEN_FREE("Gluten Free"),
    NO_PREFERENCE("No Preference");

    private final String displayName;

    FoodPreference(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}