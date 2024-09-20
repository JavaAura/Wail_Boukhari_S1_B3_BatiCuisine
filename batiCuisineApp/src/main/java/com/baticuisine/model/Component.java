package com.baticuisine.model;

import java.util.UUID;

public abstract class Component {
    protected UUID id;
    protected String name;
    protected String typeComposant;
    protected double tauxTVA;

    public Component(String name, String typeComposant, double tauxTVA) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.typeComposant = typeComposant;
        this.tauxTVA = tauxTVA;
    }

    // Getters and setters
    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTypeComposant() { return typeComposant; }
    public void setTypeComposant(String typeComposant) { this.typeComposant = typeComposant; }

    public double getTauxTVA() { return tauxTVA; }
    public void setTauxTVA(double tauxTVA) { this.tauxTVA = tauxTVA; }

    public abstract double calculateCost();
}