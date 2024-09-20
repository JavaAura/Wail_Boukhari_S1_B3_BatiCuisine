package com.baticuisine.model;

import java.util.UUID;

import com.baticuisine.model.enums.MaterialType;

public class Material {
    private UUID id;
    private String name;
    private double unitPrice;
    private double quantity;
    private String unit;
    private MaterialType type;
    private double vatRate;
    private double transportCost;
    private double qualityCoefficient;

    public Material(UUID id, String name, double unitPrice, String unit, MaterialType type, double vatRate, double transportCost, double qualityCoefficient) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.type = type;
        this.vatRate = vatRate;
        this.transportCost = transportCost;
        this.qualityCoefficient = qualityCoefficient;
    }

    public Material(String name, double unitPrice, String unit, MaterialType type, double vatRate, double transportCost, double qualityCoefficient) {
        this(UUID.randomUUID(), name, unitPrice, unit, type, vatRate, transportCost, qualityCoefficient);
    }

    // Getters and setters
    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public MaterialType getType() { return type; }
    public void setType(MaterialType type) { this.type = type; }

    public double getVatRate() { return vatRate; }
    public void setVatRate(double vatRate) { this.vatRate = vatRate; }

    public double getTransportCost() { return transportCost; }
    public void setTransportCost(double transportCost) { this.transportCost = transportCost; }

    public double getQualityCoefficient() { return qualityCoefficient; }
    public void setQualityCoefficient(double qualityCoefficient) { this.qualityCoefficient = qualityCoefficient; }

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                ", unit='" + unit + '\'' +
                ", quantity=" + quantity +
                ", type=" + type +
                ", vatRate=" + vatRate +
                ", transportCost=" + transportCost +
                ", qualityCoefficient=" + qualityCoefficient +
                '}';
    }
}
