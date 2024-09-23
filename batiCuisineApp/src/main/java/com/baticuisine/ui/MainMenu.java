package com.baticuisine.ui;

import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;

import com.baticuisine.utils.InputValidator;

public class MainMenu {
    private static final Logger LOGGER = Logger.getLogger(MainMenu.class.getName());
    private final Scanner scanner;
    private final InputValidator inputValidator;

    public enum MenuOption {
        MANAGE_PROJECTS("Gérer les projets"),
        MANAGE_CLIENTS("Gérer les clients"),
        EXIT("Quitter");

        private final String description;

        MenuOption(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public MainMenu() {
        this.scanner = new Scanner(System.in);
        this.inputValidator = new InputValidator();
    }

    public MenuOption display() {
        System.out.println("\n=== Menu Principal ===");
        Arrays.stream(MenuOption.values())
                .forEach(option -> System.out.println((option.ordinal() + 1) + ". " + option.getDescription()));

        int choice;
        do {
            choice = inputValidator.getValidIntInput(scanner, "Choisissez une option : ");
        } while (choice < 1 || choice > MenuOption.values().length);

        return MenuOption.values()[choice - 1];
    }

    public boolean isExitOption(MenuOption option) {
        return option == MenuOption.EXIT;
    }
}