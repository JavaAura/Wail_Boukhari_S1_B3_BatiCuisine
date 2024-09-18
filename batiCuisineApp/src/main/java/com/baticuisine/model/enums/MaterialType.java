package com.baticuisine.model.enums;

public enum MaterialType {
    APPLIANCE("Électroménager"),
    CABINET("Meuble"),
    COUNTERTOP("Plan de travail"),
    PLUMBING("Plomberie"),
    ELECTRICAL("Électricité"),
    FLOORING("Revêtement de sol"),
    PAINT("Peinture"),
    HARDWARE("Quincaillerie"),
    OTHER("Autre");

    private final String displayName;

    MaterialType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
