package com.baticuisine.model;

public abstract class Component {
    private Long id;
    private String name;
    private String componentType;
    private double vatRate;

    public Component(String name, String componentType, double vatRate) {
        this.name = name;
        this.componentType = componentType;
        this.vatRate = vatRate;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getComponentType() { return componentType; }
    public void setComponentType(String componentType) { this.componentType = componentType; }
    public double getVatRate() { return vatRate; }
    public void setVatRate(double vatRate) { this.vatRate = vatRate; }

    // Abstract method to calculate cost
    public abstract double calculateCost();
}