// POLYMORPHISM demonstration through Factory Pattern
public class VehicleFactory {
    
    // Factory method - creates appropriate vehicle object
    // Demonstrates POLYMORPHISM: returns Vehicle type but actual object varies
    public static Vehicle createVehicle(String type, String plate, String owner) {
        switch (type) {
            case "Motorcycle":
            case "Tricycle":
                return new Motorcycle(plate, owner);
            
            case "Sedan":
            case "SUV":
            case "AUV/Van":
                return new Sedan(plate, owner);
            
            case "Truck":
                return new Truck(plate, owner);
            
            case "Bus":
            case "Jeepney":
                return new Bus(plate, owner);
            
            default:
                return new Sedan(plate, owner); // Default to sedan
        }
    }
    
    // POLYMORPHISM: Same method call, different behavior based on object type
    public static double calculateFees(Vehicle vehicle, int daysHeld) {
        // This works for ANY Vehicle subclass!
        double baseFine = vehicle.calculateBaseFine();
        double dailyFee = vehicle.calculateDailyStorageFee();
        return baseFine + (dailyFee * daysHeld);
    }
    
    // Display vehicle info using polymorphism
    public static void displayVehicleInfo(Vehicle vehicle) {
        System.out.println("=================================");
        System.out.println(vehicle.getVehicleInfo());
        System.out.println("Base Fine: ₱" + vehicle.calculateBaseFine());
        System.out.println("Daily Fee: ₱" + vehicle.calculateDailyStorageFee());
        System.out.println("=================================");
    }
    
    // POLYMORPHISM DEMONSTRATION - Main method for testing
    public static void demonstratePolymorphism() {
        System.out.println("\n===== POLYMORPHISM DEMONSTRATION =====\n");
        
        // Array of different vehicle types stored as Vehicle references
        // This is POLYMORPHISM - one reference type, many object types
        Vehicle[] vehicles = {
            new Motorcycle("ABC123", "Juan Dela Cruz"),
            new Sedan("XYZ789", "Maria Santos"),
            new Truck("TRK456", "Pedro Reyes"),
            new Bus("BUS999", "Rosa Garcia")
        };
        
        // POLYMORPHISM: Same loop, different behavior for each vehicle
        System.out.println("Calculating fees for 7 days impound:\n");
        for (Vehicle v : vehicles) {
            displayVehicleInfo(v);
            System.out.println("Total Bill (7 days): ₱" + calculateFees(v, 7));
            System.out.println();
        }
        
        // Demonstrate that same method calls produce different results
        System.out.println("\n===== SAME METHOD, DIFFERENT RESULTS =====\n");
        for (Vehicle v : vehicles) {
            // Same method call: calculateBaseFine()
            // But different result based on actual object type
            System.out.printf("%-15s -> Base Fine: ₱%.2f\n", 
                v.getVehicleType(), v.calculateBaseFine());
        }
    }
    
    // Test method - can be run independently
    public static void main(String[] args) {
        demonstratePolymorphism();
    }
}