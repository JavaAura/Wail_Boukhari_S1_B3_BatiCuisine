# BatiCuisineApp

BatiCuisineApp is a project management application designed to help manage construction and renovation projects. It provides functionalities to create, view, manage, and delete projects, as well as handle clients, materials, and labor associated with each project.

## Features

- **Project Management**: Create, view, update, and delete projects.
- **Client Management**: Add new clients or select existing clients for projects.
- **Material and Labor Management**: Add materials and labor to projects, calculate costs, and save them to the database.
- **Quote Generation**: Generate and review quotes for projects.
- **Cost Calculation**: Calculate the total cost of projects including materials and labor.

## Project Structure

### Main Components

- **ProjectUI**: The main user interface class for managing projects.
- **ClientService**: Service class for managing client-related operations.
- **CostCalculator**: Service class for calculating project costs.
- **MaterialService**: Service class for managing materials.
- **ProjectService**: Service class for managing projects.
- **QuoteGenerator**: Service class for generating and managing quotes.
- **InputValidator**: Utility class for validating user inputs.

### Models

- **Client**: Represents a client with attributes like name, email, address, phone, etc.
- **Labor**: Represents labor with attributes like name, hours worked, hourly rate, etc.
- **Material**: Represents material with attributes like name, unit cost, quantity, etc.
- **Project**: Represents a project with attributes like name, surface, start date, status, client, etc.
- **Quote**: Represents a quote with attributes like content, acceptance status, etc.
- **ProjectStatus**: Enum representing the status of a project (EN_COURS, TERMINE, ANNULE, EN_ATTENTE).

## Database

The application uses a SQL database to store information about projects, clients, materials, and labor. The database schema is defined in the create_tables.sql file.


## Usage

1. **Run the Application**: Start the application by running the main class.
2. **Manage Projects**: Use the ProjectUI class to manage projects through the console interface.
3. **Add Components**: Add materials and labor to projects and save them to the database.
4. **Generate Quotes**: Generate and review quotes for projects.
5. **Calculate Costs**: Calculate the total cost of projects including materials and labor.
## Contributing

Contributions are welcome! Please fork the repository and submit pull requests for any enhancements or bug fixes.

## License

This project is licensed under the MIT License. 