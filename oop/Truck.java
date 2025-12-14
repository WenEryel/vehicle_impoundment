// INHERITANCE: Truck extends Vehicle
// POLYMORPHISM: Heavy vehicle implementation
public class Truck extends Vehicle {
    
    public Truck(String plateNumber, String ownerName) {
        super(plateNumber, ownerName, "Truck");
    }
    
    // POLYMORPHISM: Higher fees for trucks
    @Override
    public double calculateBaseFine() {
        return 2500.0; // Higher fine for trucks
    }
    
    @Override
    public double calculateDailyStorageFee() {
        return 350.0; // Higher storage fee
    }
    
    // Additional truck-specific method
    public String getVehicleCategory() {
        return "Heavy Commercial Vehicle";
    }
}