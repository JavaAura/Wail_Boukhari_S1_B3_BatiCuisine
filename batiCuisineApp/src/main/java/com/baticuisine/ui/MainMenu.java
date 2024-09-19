package com.baticuisine.ui;

import java.util.Scanner;
import java.util.logging.Logger;

import com.baticuisine.utils.InputValidator;

public class MainMenu {
    private static final Logger LOGGER = Logger.getLogger(MainMenu.class.getName());
    private Scanner scanner;
    private InputValidator inputValidator;

    public MainMenu() {
        this.scanner = new Scanner(System.in);
        this.inputValidator = new InputValidator();
    }

    public int display() {
        System.out.println("\n=== Menu Principal de Bati-Cuisine ===");
        System.out.println("1. Créer un nouveau projet");
        System.out.println("2. Afficher les projets existants");
        System.out.println("3. Calculer le coût d'un projet");
        System.out.println("4. Gérer les clients");
        System.out.println("5. Gérer les matériaux");
        System.out.println("6. Quitter");

        return inputValidator.getValidIntInput(scanner, "Choisissez une option : ");
    }
}
