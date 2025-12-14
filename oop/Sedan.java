// INHERITANCE: Sedan extends Vehicle
// POLYMORPHISM: Different implementation of same methods
public class Sedan extends Vehicle {
    
    public Sedan(String plateNumber, String ownerName) {
        super(plateNumber, ownerName, "Sedan");
    }
    
    // POLYMORPHISM: Same method, different behavior
    @Override
    public double calculateBaseFine() {
        return 1500.0; // Standard fine for sedans
    }
    
    @Override
    public double calculateDailyStorageFee() {
        return 200.0; // Standard storage fee
    }
    
    // Additional sedan-specific method
    public String getVehicleCategory() {
        return "Passenger Car";
    }
}