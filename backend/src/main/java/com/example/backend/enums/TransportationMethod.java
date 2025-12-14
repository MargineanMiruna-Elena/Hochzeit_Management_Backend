package com.example.backend.enums;

public enum TransportationMethod {
    CAR("Car"),
    PUBLIC_TRANSPORT("Public Transport"),
    WALKING("Walking"),
    BICYCLE("Bicycle"),
    OTHER("Other");

    private final String displayName;

    TransportationMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}