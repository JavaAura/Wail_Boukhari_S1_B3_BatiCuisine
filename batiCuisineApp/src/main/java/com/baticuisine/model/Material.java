package com.baticuisine.model;

import java.util.UUID;

import com.baticuisine.model.enums.MaterialType;

public class Material {
    private UUID id;
    private String name;
    private double unitPrice;
    private String unit;
    private int quantity;
    private MaterialType type;

    public Material(UUID id, String name, double unitPrice, String unit, MaterialType type) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.quantity = 0;
        this.type = type;
    }

    public Material(String name, double unitPrice, String unit, MaterialType type) {
        this(UUID.randomUUID(), name, unitPrice, unit, type);
    }

    // Getters and setters
    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public MaterialType getType() { return type; }
    public void setType(MaterialType type) { this.type = type; }

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                ", unit='" + unit + '\'' +
                ", quantity=" + quantity +
                ", type=" + type +
                '}';
    }
}
