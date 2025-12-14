// INHERITANCE: Motorcycle extends Vehicle
// POLYMORPHISM: Implements abstract methods with specific behavior
public class Motorcycle extends Vehicle {
    
    public Motorcycle(String plateNumber, String ownerName) {
        super(plateNumber, ownerName, "Motorcycle");
    }
    
    // POLYMORPHISM: Overriding abstract method with specific implementation
    @Override
    public double calculateBaseFine() {
        return 1000.0; // Lower fine for motorcycles
    }
    
    @Override
    public double calculateDailyStorageFee() {
        return 150.0; // Lower storage fee
    }
    
    // Additional motorcycle-specific method
    public String getVehicleCategory() {
        return "Two-Wheeler";
    }
}