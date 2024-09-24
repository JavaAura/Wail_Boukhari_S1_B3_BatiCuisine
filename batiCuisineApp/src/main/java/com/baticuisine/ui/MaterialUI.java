package com.baticuisine.ui;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.model.Material;
import com.baticuisine.service.MaterialService;
import com.baticuisine.utils.InputValidator;

public class MaterialUI {
    private static final Logger LOGGER = Logger.getLogger(MaterialUI.class.getName());
    private final Scanner scanner;
    private final MaterialService materialService;
    private final InputValidator inputValidator;

    public MaterialUI(MaterialService materialService, InputValidator inputValidator) {
        this.scanner = new Scanner(System.in);
        this.materialService = materialService;
        this.inputValidator = inputValidator;
    }

    public void manageMaterials() {
        while (true) {
            displayMenu();
            int choice = inputValidator.getValidIntInput(scanner, "Choisissez une option : ");
            if (!handleMenuChoice(choice)) {
                break;
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n=== Gestion des matériaux ===");
        System.out.println("1. Ajouter un matériau");
        System.out.println("2. Afficher tous les matériaux");
        System.out.println("3. Mettre à jour un matériau");
        System.out.println("4. Supprimer un matériau");
        System.out.println("5. Retour au menu principal");
    }

    private boolean handleMenuChoice(int choice) {
        try {
            switch (choice) {
                case 1: addMaterial(); return true;
                case 2: displayAllMaterials(); return true;
                case 3: updateMaterial(); return true;
                case 4: deleteMaterial(); return true;
                case 5: return false;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
                    return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while managing materials", e);
            System.out.println("Une erreur est survenue. Veuillez réessayer.");
            return true;
        }
    }

    private void addMaterial() {
        LOGGER.info("Starting new material creation process");
        System.out.println("=== Ajout d'un nouveau matériau ===");
        Material newMaterial = createMaterialFromInput();
        try {
            materialService.createMaterial(newMaterial);
            System.out.println("Matériau ajouté avec succès : " + newMaterial);
        } catch (RuntimeException e) {
            System.out.println("Échec de l'ajout du matériau : " + e.getMessage());
        }
    }

    private Material createMaterialFromInput() {
        String name = inputValidator.getValidStringInput(scanner, "Nom du matériau (ex: Bois, Métal) : ");
        double vatRate = inputValidator.getValidDoubleInput(scanner, "Taux de TVA (ex: 20.0) : ");
        double unitCost = inputValidator.getValidDoubleInput(scanner, "Coût unitaire (ex: 15.50) : ");
        double quantity = inputValidator.getValidDoubleInput(scanner, "Quantité (ex: 100) : ");
        double transportCost = inputValidator.getValidDoubleInput(scanner, "Coût de transport (ex: 50.0) : ");
        double qualityCoefficient = inputValidator.getValidDoubleInput(scanner, "Coefficient de qualité (ex: 1.2) : ");
        
        return new Material(name, vatRate, unitCost, quantity, transportCost, qualityCoefficient);
    }
    private void displayAllMaterials() {
        LOGGER.info("Displaying all materials");
        System.out.println("=== Liste de tous les matériaux ===");
        List<Material> materials = materialService.getAllMaterials();
        if (materials.isEmpty()) {
            System.out.println("Aucun matériau trouvé.");
        } else {
            materials.forEach(this::displayMaterial);
        }
    }

    private void displayMaterial(Material material) {
        System.out.println("--------------------");
        System.out.println("Nom: " + material.getName());
        System.out.println("Coût unitaire: " + material.getUnitCost() + " €");
        System.out.println("Quantité: " + material.getQuantity());
        System.out.println("Taux TVA: " + material.getVatRate() + "%");
        System.out.println("Coût transport: " + material.getTransportCost() + " €");
        System.out.println("Coefficient qualité: " + material.getQualityCoefficient());
        System.out.println("Coût total: " + String.format("%.2f", material.calculateCost()) + " €");
        System.out.println("--------------------");
    }

    private void updateMaterial() {
        LOGGER.info("Starting material update process");
        System.out.println("=== Mise à jour d'un matériau ===");
        String name = inputValidator.getValidStringInput(scanner, "Nom du matériau à mettre à jour : ");
        
        Optional<Material> materialOpt = materialService.getMaterialByName(name);
        
        if (materialOpt.isPresent()) {
            Material material = materialOpt.get();
            updateMaterialFields(material);
            try {
                materialService.updateMaterial(material);
                System.out.println("Matériau mis à jour avec succès : " + material);
            } catch (RuntimeException e) {
                System.out.println("Échec de la mise à jour du matériau : " + e.getMessage());
            }
        } else {
            System.out.println("Matériau non trouvé.");
        }
    }

    private void updateMaterialFields(Material material) {
        System.out.println("Appuyez sur Entrée pour conserver la valeur actuelle.");
        String newName = inputValidator.getValidStringInput(scanner, "Nouveau nom (" + material.getName() + ") : ");
        double newVatRate = inputValidator.getValidDoubleInput(scanner, "Nouveau taux de TVA (" + material.getVatRate() + ") : ");
        double newUnitCost = inputValidator.getValidDoubleInput(scanner, "Nouveau coût unitaire (" + material.getUnitCost() + ") : ");
        double newQuantity = inputValidator.getValidDoubleInput(scanner, "Nouvelle quantité (" + material.getQuantity() + ") : ");
        double newTransportCost = inputValidator.getValidDoubleInput(scanner, "Nouveau coût de transport (" + material.getTransportCost() + ") : ");
        double newQualityCoefficient = inputValidator.getValidDoubleInput(scanner, "Nouveau coefficient de qualité (" + material.getQualityCoefficient() + ") : ");
    
        if (!newName.isEmpty()) material.setName(newName);
        if (newVatRate >= 0) material.setVatRate(newVatRate);
        if (newUnitCost >= 0) material.setUnitCost(newUnitCost);
        if (newQuantity >= 0) material.setQuantity(newQuantity);
        if (newTransportCost >= 0) material.setTransportCost(newTransportCost);
        if (newQualityCoefficient >= 0) material.setQualityCoefficient(newQualityCoefficient);
    }

    private void deleteMaterial() {
        LOGGER.info("Starting material deletion process");
        System.out.println("=== Suppression d'un matériau ===");
        String name = inputValidator.getValidStringInput(scanner, "Nom du matériau à supprimer : ");

        Optional<Material> materialOpt = materialService.getMaterialByName(name);
        if (materialOpt.isPresent()) {
            confirmAndDeleteMaterial(materialOpt.get());
        } else {
            System.out.println("Matériau non trouvé.");
        }
    }

    private void confirmAndDeleteMaterial(Material material) {
        System.out.println("Êtes-vous sûr de vouloir supprimer ce matériau ? " + material);
        boolean confirm = inputValidator.getValidBooleanInput(scanner, "Confirmer la suppression (oui/non) : ");
    
        if (confirm) {
            try {
                materialService.deleteMaterial(material.getId());
                System.out.println("Matériau supprimé avec succès : " + material);
            } catch (RuntimeException e) {
                System.out.println("Échec de la suppression du matériau : " + e.getMessage());
            }
        } else {
            System.out.println("Suppression annulée.");
        }
    }
}
