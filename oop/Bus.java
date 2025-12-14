// INHERITANCE: Bus extends Vehicle
// POLYMORPHISM: Largest vehicle implementation
public class Bus extends Vehicle {
    
    public Bus(String plateNumber, String ownerName) {
        super(plateNumber, ownerName, "Bus");
    }
    
    // POLYMORPHISM: Highest fees for buses
    @Override
    public double calculateBaseFine() {
        return 3000.0; // Highest fine for buses
    }
    
    @Override
    public double calculateDailyStorageFee() {
        return 400.0; // Highest storage fee
    }
    
    // Additional bus-specific method
    public String getVehicleCategory() {
        return "Public Transport Vehicle";
    }
}