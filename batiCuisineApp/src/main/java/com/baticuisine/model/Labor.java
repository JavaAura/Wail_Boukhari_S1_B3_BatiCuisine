package com.baticuisine.model;

import java.util.UUID;

public class Labor extends Component {
    private double tauxHoraire;
    private double heuresTravail;
    private double productiviteOuvrier;

    public Labor(String name, double tauxTVA, double tauxHoraire, double heuresTravail, double productiviteOuvrier) {
        super(name, "Main-d'œuvre", tauxTVA);
        this.tauxHoraire = tauxHoraire;
        this.heuresTravail = heuresTravail;
        this.productiviteOuvrier = productiviteOuvrier;
    }

    // Getters and setters
    public double getTauxHoraire() { return tauxHoraire; }
    public void setTauxHoraire(double tauxHoraire) { this.tauxHoraire = tauxHoraire; }

    public double getHeuresTravail() { return heuresTravail; }
    public void setHeuresTravail(double heuresTravail) { this.heuresTravail = heuresTravail; }

    public double getProductiviteOuvrier() { return productiviteOuvrier; }
    public void setProductiviteOuvrier(double productiviteOuvrier) { this.productiviteOuvrier = productiviteOuvrier; }

    @Override
    public double calculateCost() {
        return tauxHoraire * heuresTravail * productiviteOuvrier * (1 + tauxTVA / 100);
    }
}
