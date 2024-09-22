package com.baticuisine.model;

public class Material extends Component {
    private double unitCost;
    private double quantity;
    private double transportCost;
    private double qualityCoefficient;

    public Material(String name, double vatRate, double unitCost, double quantity, 
                    double transportCost, double qualityCoefficient) {
        super(name, "Material", vatRate);
        this.unitCost = unitCost;
        this.quantity = quantity;
        this.transportCost = transportCost;
        this.qualityCoefficient = qualityCoefficient;
    }

    // Getters and setters
    public double getUnitCost() { return unitCost; }
    public void setUnitCost(double unitCost) { this.unitCost = unitCost; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public double getTransportCost() { return transportCost; }
    public void setTransportCost(double transportCost) { this.transportCost = transportCost; }
    public double getQualityCoefficient() { return qualityCoefficient; }
    public void setQualityCoefficient(double qualityCoefficient) { this.qualityCoefficient = qualityCoefficient; }

    @Override
    public double calculateCost() {
        return (unitCost * quantity * qualityCoefficient) + transportCost;
    }

    @Override
    public String toString() {
        return String.format("%s: %.2f € (quantity: %.2f, unit cost: %.2f €, quality: %.2f, transport: %.2f €)",
                getName(), calculateCost(), quantity, unitCost, qualityCoefficient, transportCost);
    }
}