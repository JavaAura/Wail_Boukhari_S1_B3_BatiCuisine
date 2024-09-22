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
import com.baticuisine.repository.QuoteRepository;
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

            MaterialRepository materialRepository = new MaterialRepository(connection);
            MaterialService materialService = new MaterialService(materialRepository);

            Map<String, Object> repositories = new HashMap<>();
            repositories.put("project", new ProjectRepository(connection));
            repositories.put("client", new ClientRepository(connection));
            repositories.put("material", materialRepository);
            repositories.put("quote", new QuoteRepository(connection));

            InputValidator inputValidator = new InputValidator();
            DateUtils dateUtils = new DateUtils();

            Map<String, Object> services = new HashMap<>();
            services.put("project", new ProjectService((ProjectRepository) repositories.get("project"), dateUtils, materialRepository));
            services.put("client", new ClientService((ClientRepository) repositories.get("client")));
            services.put("material", materialService);

            CostCalculator costCalculator = new CostCalculator((MaterialService) services.get("material"));
            QuoteGenerator quoteGenerator = new QuoteGenerator(costCalculator, (QuoteRepository) repositories.get("quote"));

            MainMenu mainMenu = new MainMenu();
            ProjectUI projectUI = new ProjectUI((ProjectService) services.get("project"), costCalculator, inputValidator, quoteGenerator, (MaterialService) services.get("material"), (ClientService) services.get("client"));
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
