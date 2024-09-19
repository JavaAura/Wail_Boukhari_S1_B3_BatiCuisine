package com.baticuisine;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.database.DatabaseConnection;
import com.baticuisine.repository.ClientRepository;
import com.baticuisine.repository.MaterialRepository;
import com.baticuisine.repository.ProjectRepository;
import com.baticuisine.service.ClientService;
import com.baticuisine.service.CostCalculator;
import com.baticuisine.service.MaterialService;
import com.baticuisine.service.ProjectService;
import com.baticuisine.service.QuoteGenerator;
import com.baticuisine.ui.ClientUI;
import com.baticuisine.ui.MainMenu;
import com.baticuisine.ui.MaterialUI;
import com.baticuisine.ui.ProjectUI;
import com.baticuisine.utils.DateUtils;
import com.baticuisine.utils.InputValidator;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Starting Bati-Cuisine application");

        try {
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            Connection connection = dbConnection.getConnection();

            Map<String, Object> repositories = new HashMap<>();
            repositories.put("project", new ProjectRepository(connection));
            repositories.put("client", new ClientRepository(connection));
            repositories.put("material", new MaterialRepository(connection));

            InputValidator inputValidator = new InputValidator();
            DateUtils dateUtils = new DateUtils();

            Map<String, Object> services = new HashMap<>();
            services.put("project", new ProjectService((ProjectRepository) repositories.get("project"), dateUtils));
            services.put("client", new ClientService((ClientRepository) repositories.get("client")));
            services.put("material", new MaterialService((MaterialRepository) repositories.get("material")));

            CostCalculator costCalculator = new CostCalculator((MaterialService) services.get("material"));
            QuoteGenerator quoteGenerator = new QuoteGenerator(costCalculator);

            MainMenu mainMenu = new MainMenu();
            ProjectUI projectUI = new ProjectUI((ProjectService) services.get("project"), costCalculator, inputValidator, quoteGenerator);
            ClientUI clientUI = new ClientUI((ClientService) services.get("client"), inputValidator);
            MaterialUI materialUI = new MaterialUI((MaterialService) services.get("material"), inputValidator);

            runApplication(mainMenu, projectUI, clientUI, materialUI);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while running the application", e);
        } finally {
            DatabaseConnection.getInstance().closeConnection();
        }
    }

    private static void runApplication(MainMenu mainMenu, ProjectUI projectUI, ClientUI clientUI, MaterialUI materialUI) {
        boolean running = true;
        while (running) {
            int choice = mainMenu.display();
            switch (choice) {
                case 1:
                    projectUI.createNewProject();
                    break;
                case 2:
                    projectUI.displayExistingProjects();
                    break;
                case 3:
                    projectUI.calculateProjectCost();
                    break;
                case 4:
                    clientUI.manageClients();
                    break;
                case 5:
                    materialUI.manageMaterials();
                    break;
                case 6:
                    running = false;
                    LOGGER.info("Application shutting down");
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }
}
