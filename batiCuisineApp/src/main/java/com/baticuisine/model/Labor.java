package com.baticuisine.model;

import java.util.UUID;

public class Labor {
    private UUID id;
    private String description;
    private double hourlyRate;
    private double hours;

    public Labor(String description, double hourlyRate, double hours) {
        this.id = UUID.randomUUID();
        this.description = description;
        this.hourlyRate = hourlyRate;
        this.hours = hours;
    }

    // Getters and setters
    public UUID getId() { return id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public double getHours() { return hours; }
    public void setHours(double hours) { this.hours = hours; }

    public double getCost() {
        return hourlyRate * hours;
    }

    @Override
    public String toString() {
        return "Labor{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", hourlyRate=" + hourlyRate +
                ", hours=" + hours +
                '}';
    }
}
