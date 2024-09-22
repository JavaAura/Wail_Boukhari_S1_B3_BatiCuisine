package com.baticuisine.model;
public class Labor extends Component {
    private double hourlyRate;
    private double hoursWorked;
    private double workerProductivity;

    public Labor(String name, double vatRate, double hourlyRate, double hoursWorked, 
                 double workerProductivity) {
        super(name, "Labor", vatRate);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
        this.workerProductivity = workerProductivity;
    }

    // Getters and setters
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(double hoursWorked) { this.hoursWorked = hoursWorked; }

    public double getWorkerProductivity() { return workerProductivity; }
    public void setWorkerProductivity(double workerProductivity) { this.workerProductivity = workerProductivity; }

    @Override
    public double calculateCost() {
        return hourlyRate * hoursWorked * workerProductivity;
    }

    @Override
    public String toString() {
        return String.format("%s: %.2f € (hourly rate: %.2f €/h, hours worked: %.2f h, productivity: %.2f)",
                getName(), calculateCost(), hourlyRate, hoursWorked, workerProductivity);
    }
}
