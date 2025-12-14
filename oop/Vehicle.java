// Abstract class demonstrating ABSTRACTION and ENCAPSULATION
public abstract class Vehicle {
    // ENCAPSULATION: Private fields with protected access
    private String plateNumber;
    private String ownerName;
    private String vehicleType;
    
    // Constructor
    public Vehicle(String plateNumber, String ownerName, String vehicleType) {
        this.plateNumber = plateNumber;
        this.ownerName = ownerName;
        this.vehicleType = vehicleType;
    }
    
    // ABSTRACTION: Abstract methods - must be implemented by subclasses
    public abstract double calculateBaseFine();
    public abstract double calculateDailyStorageFee();
    
    // Concrete method - shared by all vehicles
    public String getVehicleInfo() {
        return String.format("Plate: %s | Owner: %s | Type: %s", 
                           plateNumber, ownerName, vehicleType);
    }
    
    // Calculate total bill for given days
    public double calculateTotalBill(int daysHeld) {
        return calculateBaseFine() + (calculateDailyStorageFee() * daysHeld);
    }
    
    // ENCAPSULATION: Getters and Setters
    public String getPlateNumber() {
        return plateNumber;
    }
    
    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public String getVehicleType() {
        return vehicleType;
    }
    
    protected void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    // toString for easy display
    @Override
    public String toString() {
        return String.format("%s - Plate: %s, Owner: %s", 
                           vehicleType, plateNumber, ownerName);
    }
}