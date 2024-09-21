package com.baticuisine.model;

public class Material extends Component {
    private static final double DEFAULT_TVA = 20.0;
    private double coutUnitaire;
    private double quantite;
    private double coutTransport;
    private double coefficientQualite;

    public Material(String name, double coutUnitaire, double quantite, double coutTransport, double coefficientQualite) {
        super(name, "Matériel", DEFAULT_TVA);
        this.coutUnitaire = coutUnitaire;
        this.quantite = quantite;
        this.coutTransport = coutTransport;
        this.coefficientQualite = coefficientQualite;
    }

    // Getters and setters
    public double getCoutUnitaire() { return coutUnitaire; }
    public void setCoutUnitaire(double coutUnitaire) { this.coutUnitaire = coutUnitaire; }

    public double getQuantite() { return quantite; }
    public void setQuantite(double quantite) { this.quantite = quantite; }

    public double getCoutTransport() { return coutTransport; }
    public void setCoutTransport(double coutTransport) { this.coutTransport = coutTransport; }

    public double getCoefficientQualite() { return coefficientQualite; }
    public void setCoefficientQualite(double coefficientQualite) { this.coefficientQualite = coefficientQualite; }

    @Override
    public double calculateCost() {
        return (coutUnitaire * quantite + coutTransport) * coefficientQualite * (1 + DEFAULT_TVA / 100);
    }
}