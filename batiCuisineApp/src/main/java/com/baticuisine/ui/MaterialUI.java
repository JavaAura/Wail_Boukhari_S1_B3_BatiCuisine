package com.baticuisine.ui;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
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
        }
    }

    private void addMaterial() {
        LOGGER.info("Starting new material creation process");
        System.out.println("=== Ajout d'un nouveau matériau ===");
        String name = inputValidator.getValidStringInput(scanner, "Nom du matériau : ");
        double unitPrice = inputValidator.getValidDoubleInput(scanner, "Prix unitaire : ");
        String unit = inputValidator.getValidStringInput(scanner, "Unité de mesure : ");
        MaterialType type = inputValidator.getValidEnumInput(scanner, "Type de matériau (APPLIANCE, CABINET, COUNTERTOP, PLUMBING, ELECTRICAL, FLOORING, PAINT, HARDWARE, OTHER) : ", MaterialType.class);

        Material newMaterial = new Material(name, unitPrice, unit, type);
        materialService.createMaterial(newMaterial);
        LOGGER.info("New material created: " + name);
        System.out.println("Matériau ajouté avec succès !");
    }

    private void displayAllMaterials() {
        LOGGER.info("Displaying all materials");
        System.out.println("=== Liste de tous les matériaux ===");
        List<Material> materials = materialService.getAllMaterials();
        materials.forEach(System.out::println);
    }

    private void updateMaterial() {
        LOGGER.info("Starting material update process");
        System.out.println("=== Mise à jour d'un matériau ===");
        String name = inputValidator.getValidStringInput(scanner, "Nom du matériau à mettre à jour : ");

        Optional<Material> materialOpt = materialService.getMaterialByName(name);

        if (materialOpt.isPresent()) {
            Material material = materialOpt.get();
            double unitPrice = inputValidator.getValidDoubleInput(scanner, "Nouveau prix unitaire : ");
            String unit = inputValidator.getValidStringInput(scanner, "Nouvelle unité de mesure : ");
            MaterialType type = inputValidator.getValidEnumInput(scanner, "Nouveau type de matériau (APPLIANCE, CABINET, COUNTERTOP, PLUMBING, ELECTRICAL, FLOORING, PAINT, HARDWARE, OTHER) : ", MaterialType.class);

            material.setUnitPrice(unitPrice);
            material.setUnit(unit);
            material.setType(type);

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

        materialService.deleteMaterial(name);
        LOGGER.info("Material deleted: " + name);
        System.out.println("Matériau supprimé avec succès !");
    }
}
