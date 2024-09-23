package com.baticuisine.model.enums;

public enum ProjectStatus {
    EN_COURS("En cours", "EN_COURS"),
    TERMINE("Terminé", "TERMINE"),
    ANNULE("Annulé", "ANNULE"),
    EN_ATTENTE("En attente", "EN_ATTENTE");
    
    private final String displayName;
    private final String dbValue;

    ProjectStatus(String displayName, String dbValue) {
        this.displayName = displayName;
        this.dbValue = dbValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static ProjectStatus fromDbValue(String dbValue) {
        for (ProjectStatus status : values()) {
            if (status.dbValue.equals(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with dbValue " + dbValue);
    }

    @Override
    public String toString() {
        return displayName;
    }
}