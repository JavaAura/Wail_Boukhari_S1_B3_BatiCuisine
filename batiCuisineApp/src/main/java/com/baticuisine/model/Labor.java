package com.baticuisine.model;

import java.util.UUID;

public class Labor {
    private UUID id;
    private String description;
    private double hourlyRate;
    private double hours;
    private double productivityFactor;
    private double vatRate;

    public Labor(String description, double hourlyRate, double hours, double productivityFactor, double vatRate) {
        this.id = UUID.randomUUID();
        this.description = description;
        this.hourlyRate = hourlyRate;
        this.hours = hours;
        this.productivityFactor = productivityFactor;
        this.vatRate = vatRate;
    }

    // Getters and setters
    public UUID getId() { return id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public double getHours() { return hours; }
    public void setHours(double hours) { this.hours = hours; }

    public double getProductivityFactor() { return productivityFactor; }
    public void setProductivityFactor(double productivityFactor) { this.productivityFactor = productivityFactor; }

    public double getVatRate() { return vatRate; }
    public void setVatRate(double vatRate) { this.vatRate = vatRate; }

    public double getCost() {
        return hourlyRate * hours * productivityFactor * (1 + vatRate / 100);
    }

    @Override
    public String toString() {
        return "Labor{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", hourlyRate=" + hourlyRate +
                ", hours=" + hours +
                ", productivityFactor=" + productivityFactor +
                ", vatRate=" + vatRate +
                '}';
    }
}
