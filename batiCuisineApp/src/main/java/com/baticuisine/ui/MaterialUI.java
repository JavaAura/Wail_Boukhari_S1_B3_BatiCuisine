package com.baticuisine.ui;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.model.Material;
import com.baticuisine.model.enums.MaterialType;
import com.baticuisine.service.MaterialService;
import com.baticuisine.utils.InputValidator;

public class MaterialUI {
    private static final Logger LOGGER = Logger.getLogger(MaterialUI.class.getName());
    private Scanner scanner;
    private MaterialService materialService;
    private InputValidator inputValidator;

    public MaterialUI(MaterialService materialService, InputValidator inputValidator) {
        this.scanner = new Scanner(System.in);
        this.materialService = materialService;
        this.inputValidator = inputValidator;
    }

    public void manageMaterials() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Gestion des matériaux ===");
            System.out.println("1. Ajouter un matériau");
            System.out.println("2. Afficher tous les matériaux");
            System.out.println("3. Mettre à jour un matériau");
            System.out.println("4. Supprimer un matériau");
            System.out.println("5. Retour au menu principal");

            int choice = inputValidator.getValidIntInput(scanner, "Choisissez une option : ");

            try {
                switch (choice) {
                    case 1:
                        addMaterial();
                        break;
                    case 2:
                        displayAllMaterials();
                        break;
                    case 3:
                        updateMaterial();
                        break;
                    case 4:
                        deleteMaterial();
                        break;
                    case 5:
                        running = false;
                        break;
                    default:
                        System.out.println("Option invalide. Veuillez réessayer.");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An error occurred while managing materials", e);
                System.out.println("Une erreur est survenue. Veuillez réessayer.");
            }
        }
    }
    private void addMaterial() {
        LOGGER.info("Starting new material creation process");
        System.out.println("=== Ajout d'un nouveau matériau ===");
        String name = inputValidator.getValidStringInput(scanner, "Nom du matériau : ");
        double coutUnitaire = inputValidator.getValidDoubleInput(scanner, "Coût unitaire : ");
        double quantite = inputValidator.getValidDoubleInput(scanner, "Quantité : ");
        double tauxTVA = inputValidator.getValidDoubleInput(scanner, "Taux de TVA (%) : ");
        double coutTransport = inputValidator.getValidDoubleInput(scanner, "Coût de transport : ");
        double coefficientQualite = inputValidator.getValidDoubleInput(scanner, "Coefficient de qualité : ");
    
        Material newMaterial = new Material(name, coutUnitaire, quantite, tauxTVA, coutTransport, coefficientQualite);
        materialService.createMaterial(newMaterial);
        LOGGER.info("New material created: " + name);
        System.out.println("Matériau ajouté avec succès !");
    }

    private void displayAllMaterials() {
        LOGGER.info("Displaying all materials");
        System.out.println("=== Liste de tous les matériaux ===");
        List<Material> materials = materialService.getAllMaterials();
        if (materials.isEmpty()) {
            System.out.println("Aucun matériau trouvé.");
        } else {
            for (Material material : materials) {
                System.out.println("--------------------");
                System.out.println("Nom: " + material.getName());
                System.out.println("Coût unitaire: " + material.getCoutUnitaire() + " €");
                System.out.println("Quantité: " + material.getQuantite());
                System.out.println("Taux TVA: " + material.getTauxTVA() + "%");
                System.out.println("Coût transport: " + material.getCoutTransport() + " €");
                System.out.println("Coefficient qualité: " + material.getCoefficientQualite());
                System.out.println("Coût total: " + String.format("%.2f", material.calculateCost()) + " €");
                System.out.println("--------------------");
            }
        }
    }

    private void updateMaterial() {
        LOGGER.info("Starting material update process");
        System.out.println("=== Mise à jour d'un matériau ===");
        String name = inputValidator.getValidStringInput(scanner, "Nom du matériau à mettre à jour : ");
    
        Optional<Material> materialOpt = materialService.getMaterialByName(name);
    
        if (materialOpt.isPresent()) {
            Material material = materialOpt.get();
            double coutUnitaire = inputValidator.getValidDoubleInput(scanner, "Nouveau coût unitaire : ");
            double quantite = inputValidator.getValidDoubleInput(scanner, "Nouvelle quantité : ");
            double tauxTVA = inputValidator.getValidDoubleInput(scanner, "Nouveau taux de TVA (%) : ");
            double coutTransport = inputValidator.getValidDoubleInput(scanner, "Nouveau coût de transport : ");
            double coefficientQualite = inputValidator.getValidDoubleInput(scanner, "Nouveau coefficient de qualité : ");
    
            material.setCoutUnitaire(coutUnitaire);
            material.setQuantite(quantite);
            material.setTauxTVA(tauxTVA);
            material.setCoutTransport(coutTransport);
            material.setCoefficientQualite(coefficientQualite);
    
            materialService.updateMaterial(material);
            LOGGER.info("Material updated: " + name);
            System.out.println("Matériau mis à jour avec succès !");
        } else {
            System.out.println("Matériau non trouvé.");
        }
    }

    private void deleteMaterial() {
        LOGGER.info("Starting material deletion process");
        System.out.println("=== Suppression d'un matériau ===");
        String name = inputValidator.getValidStringInput(scanner, "Nom du matériau à supprimer : ");

        Optional<Material> materialOpt = materialService.getMaterialByName(name);
        if (materialOpt.isPresent()) {
            boolean confirm = inputValidator.getValidBooleanInput(scanner, "Êtes-vous sûr de vouloir supprimer ce matériau ? (oui/non) : ");
            if (confirm) {
                materialService.deleteMaterial(name);
                LOGGER.info("Material deleted: " + name);
                System.out.println("Matériau supprimé avec succès !");
            } else {
                System.out.println("Suppression annulée.");
            }
        } else {
            System.out.println("Matériau non trouvé.");
        }
    }

    private MaterialType getValidMaterialType() {
        while (true) {
            System.out.println("Types de matériau disponibles :");
            for (MaterialType type : MaterialType.values()) {
                System.out.println("- " + type.name());
            }
            String typeInput = inputValidator.getValidStringInput(scanner, "Type de matériau : ");
            try {
                return MaterialType.valueOf(typeInput.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Type de matériau invalide. Veuillez réessayer.");
            }
        }
    }
}
