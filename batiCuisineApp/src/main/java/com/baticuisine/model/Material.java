package com.baticuisine.model;

public class Material extends Component {
    private double coutUnitaire;
    private double quantite;
    private double coutTransport;
    private double coefficientQualite;

    public Material(String name, double coutUnitaire, double quantite, double tauxTVA, double coutTransport, double coefficientQualite) {
        super(name, "Matériel", tauxTVA);
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
        return (coutUnitaire * quantite + coutTransport) * coefficientQualite * (1 + tauxTVA / 100);
    }
}
