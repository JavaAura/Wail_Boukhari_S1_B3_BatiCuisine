package com.baticuisine;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.database.DatabaseConnection;
import com.baticuisine.repository.*;
import com.baticuisine.service.*;
import com.baticuisine.ui.*;
import com.baticuisine.utils.*;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Starting Bati-Cuisine application");

        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            
            // Initialize repositories
            ProjectRepository projectRepository = new ProjectRepositoryImpl(connection);
            ClientRepository clientRepository = new ClientRepositoryImpl(connection);
            QuoteRepository quoteRepository = new QuoteRepositoryImpl(connection);
            ComponentRepository componentRepository = new ComponentRepositoryImpl();

            // Initialize services
            DateUtils dateUtils = new DateUtils();
            ProjectService projectService = new ProjectService(projectRepository, dateUtils, componentRepository);
            ClientService clientService = new ClientService(clientRepository);
            MaterialService materialService = new MaterialService(componentRepository);
            CostCalculator costCalculator = new CostCalculator(materialService);
            QuoteGenerator quoteGenerator = new QuoteGenerator(costCalculator, quoteRepository);

            // Initialize UI components
            InputValidator inputValidator = new InputValidator();
            ProjectUI projectUI = new ProjectUI(projectService, costCalculator, inputValidator, quoteGenerator, materialService, clientService);
            ClientUI clientUI = new ClientUI(clientService, inputValidator);
            MaterialUI materialUI = new MaterialUI(materialService, inputValidator);

            MainMenu mainMenu = new MainMenu();
            
            runApplication(mainMenu, projectUI, clientUI, materialUI);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while running the application", e);
        } finally {
            DatabaseConnection.getInstance().closeConnection();
        }
    }

    private static void runApplication(MainMenu mainMenu, ProjectUI projectUI, ClientUI clientUI, MaterialUI materialUI) {
        while (true) {
            MainMenu.MenuOption choice = mainMenu.display();
            switch (choice) {
                case MANAGE_PROJECTS:
                    projectUI.manageProjects();
                    break;
                case MANAGE_CLIENTS:
                    clientUI.manageClients();
                    break;
                case EXIT:
                    LOGGER.info("Application shutting down");
                    System.out.println("Au revoir !");
                    return;
            }
        }
    }
}