package com.baticuisine.model.enums;

public enum ProjectStatus {
    EN_COURS("En cours"),
    TERMINE("Terminé"),
    ANNULE("Annulé"),
    EN_ATTENTE("En attente");

    private final String displayName;

    ProjectStatus(String displayName) {
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
