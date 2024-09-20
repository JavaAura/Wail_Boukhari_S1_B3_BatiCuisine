package com.baticuisine.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import com.baticuisine.utils.InputValidator;

public class MainMenu {
    private static final Logger LOGGER = Logger.getLogger(MainMenu.class.getName());
    private Scanner scanner;
    private InputValidator inputValidator;
    private Map<MenuOption, String> menuOptions;

    public enum MenuOption {
        MANAGE_PROJECTS,
        MANAGE_CLIENTS,
        MANAGE_MATERIALS,
        EXIT
    }

    public MainMenu() {
        this.scanner = new Scanner(System.in);
        this.inputValidator = new InputValidator();
        this.menuOptions = new LinkedHashMap<>();
        initializeMenuOptions();
    }

    private void initializeMenuOptions() {
        addMenuOption(MenuOption.MANAGE_PROJECTS, "Gérer les projets");
        addMenuOption(MenuOption.MANAGE_CLIENTS, "Gérer les clients");
        addMenuOption(MenuOption.MANAGE_MATERIALS, "Gérer les matériaux");
        addMenuOption(MenuOption.EXIT, "Quitter");
    }

    public void addMenuOption(MenuOption option, String description) {
        menuOptions.put(option, description);
    }

    public MenuOption display() {
        System.out.println("\n=== Menu Principal ===");
        System.out.println("1. Gérer les projets");
        System.out.println("2. Gérer les clients");
        System.out.println("3. Gérer les matériaux");
        System.out.println("4. Quitter");

        int choice;
        do {
            System.out.print("Choisissez une option : ");
            choice = scanner.nextInt();
        } while (choice < 1 || choice > 4);

        return MenuOption.values()[choice - 1];
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public boolean isExitOption(MenuOption option) {
        return option == MenuOption.EXIT;
    }
}
