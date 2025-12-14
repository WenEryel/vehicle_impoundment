/**
 * OOP DEMONSTRATION CLASS
 * This class demonstrates all OOP concepts in one place
 * Run this to see OOP principles in action!
 */
public class OOPDemonstration {
    
    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║   VEHICLE IMPOUND SYSTEM - OOP DEMO       ║");
        System.out.println("╚════════════════════════════════════════════╝\n");
        
        // 1. INHERITANCE DEMONSTRATION
        demonstrateInheritance();
        
        // 2. POLYMORPHISM DEMONSTRATION
        demonstratePolymorphism();
        
        // 3. ABSTRACTION DEMONSTRATION
        demonstrateAbstraction();
        
        // 4. ENCAPSULATION DEMONSTRATION
        demonstrateEncapsulation();
    }
    
    // ===== 1. INHERITANCE =====
    public static void demonstrateInheritance() {
        System.out.println("\n█ 1. INHERITANCE DEMONSTRATION");
        System.out.println("═".repeat(50));
        System.out.println("Inheritance: Child classes inherit from parent class\n");
        
        // Create different vehicle types
        Motorcycle bike = new Motorcycle("ABC-123", "Juan");
        Sedan car = new Sedan("XYZ-789", "Maria");
        Truck truck = new Truck("TRK-456", "Pedro");
        Bus bus = new Bus("BUS-999", "Rosa");
        
        System.out.println("All these classes INHERIT from Vehicle:");
        System.out.println("  - Motorcycle extends Vehicle");
        System.out.println("  - Sedan extends Vehicle");
        System.out.println("  - Truck extends Vehicle");
        System.out.println("  - Bus extends Vehicle\n");
        
        System.out.println("They all inherit the getVehicleInfo() method:");
        System.out.println("  " + bike.getVehicleInfo());
        System.out.println("  " + car.getVehicleInfo());
        System.out.println("  " + truck.getVehicleInfo());
        System.out.println("  " + bus.getVehicleInfo());
    }
    
    // ===== 2. POLYMORPHISM =====
    public static void demonstratePolymorphism() {
        System.out.println("\n\n█ 2. POLYMORPHISM DEMONSTRATION");
        System.out.println("═".repeat(50));
        System.out.println("Polymorphism: Same method, different behavior\n");
        
        // Store different objects in array of parent type
        Vehicle[] vehicles = {
            new Motorcycle("M-001", "Owner1"),
            new Sedan("S-002", "Owner2"),
            new Truck("T-003", "Owner3"),
            new Bus("B-004", "Owner4")
        };
        
        System.out.println("Same method call: calculateBaseFine()");
        System.out.println("Different results based on vehicle type:\n");
        
        // POLYMORPHISM: Same method call, different results
        for (Vehicle v : vehicles) {
            System.out.printf("  %-15s -> ₱%.2f\n", 
                v.getVehicleType(), v.calculateBaseFine());
        }
        
        System.out.println("\nSame method call: calculateDailyStorageFee()");
        for (Vehicle v : vehicles) {
            System.out.printf("  %-15s -> ₱%.2f/day\n", 
                v.getVehicleType(), v.calculateDailyStorageFee());
        }
        
        System.out.println("\nPolymorphic bill calculation (7 days):");
        for (Vehicle v : vehicles) {
            double total = v.calculateTotalBill(7);
            System.out.printf("  %-15s -> ₱%.2f\n", 
                v.getVehicleType(), total);
        }
    }
    
    // ===== 3. ABSTRACTION =====
    public static void demonstrateAbstraction() {
        System.out.println("\n\n█ 3. ABSTRACTION DEMONSTRATION");
        System.out.println("═".repeat(50));
        System.out.println("Abstraction: Hiding implementation details\n");
        
        System.out.println("Vehicle is an ABSTRACT CLASS:");
        System.out.println("  - Cannot instantiate: new Vehicle() ❌");
        System.out.println("  - Has abstract methods that MUST be implemented");
        System.out.println("  - abstract double calculateBaseFine();");
        System.out.println("  - abstract double calculateDailyStorageFee();\n");
        
        System.out.println("Each subclass provides its own implementation:");
        
        Vehicle bike = new Motorcycle("M-100", "Test");
        Vehicle car = new Sedan("S-200", "Test");
        
        System.out.println("\n  Motorcycle implementation:");
        System.out.println("    calculateBaseFine() -> ₱1000.00");
        System.out.println("    calculateDailyStorageFee() -> ₱150.00");
        
        System.out.println("\n  Sedan implementation:");
        System.out.println("    calculateBaseFine() -> ₱1500.00");
        System.out.println("    calculateDailyStorageFee() -> ₱200.00");
        
        System.out.println("\nThe user doesn't need to know HOW it's calculated,");
        System.out.println("just that they CAN calculate it! That's ABSTRACTION.");
    }
    
    // ===== 4. ENCAPSULATION =====
    public static void demonstrateEncapsulation() {
        System.out.println("\n\n█ 4. ENCAPSULATION DEMONSTRATION");
        System.out.println("═".repeat(50));
        System.out.println("Encapsulation: Data hiding with getters/setters\n");
        
        Motorcycle bike = new Motorcycle("M-500", "Original Owner");
        
        System.out.println("Vehicle class has PRIVATE fields:");
        System.out.println("  - private String plateNumber;");
        System.out.println("  - private String ownerName;");
        System.out.println("  - private String vehicleType;\n");
        
        System.out.println("Cannot access directly: bike.plateNumber ❌");
        System.out.println("Must use getters/setters:\n");
        
        System.out.println("Initial state:");
        System.out.println("  Plate: " + bike.getPlateNumber());
        System.out.println("  Owner: " + bike.getOwnerName());
        
        // Modify using setters
        bike.setPlateNumber("M-999");
        bike.setOwnerName("New Owner");
        
        System.out.println("\nAfter modification using setters:");
        System.out.println("  Plate: " + bike.getPlateNumber());
        System.out.println("  Owner: " + bike.getOwnerName());
        
        System.out.println("\nBenefits of Encapsulation:");
        System.out.println("  ✓ Data is protected from direct access");
        System.out.println("  ✓ Can add validation in setters");
        System.out.println("  ✓ Can change internal implementation without affecting users");
    }
    
    // ===== BONUS: PRACTICAL EXAMPLE =====
    public static void practicalExample() {
        System.out.println("\n\n█ PRACTICAL EXAMPLE: Using OOP in Real System");
        System.out.println("═".repeat(50));
        
        // Use factory to create vehicles
        Vehicle v1 = VehicleFactory.createVehicle("Motorcycle", "ABC-123", "Juan");
        Vehicle v2 = VehicleFactory.createVehicle("Sedan", "XYZ-789", "Maria");
        
        System.out.println("\nVehicle 1: " + v1.getVehicleInfo());
        System.out.println("  Base Fine: ₱" + v1.calculateBaseFine());
        System.out.println("  Daily Fee: ₱" + v1.calculateDailyStorageFee());
        System.out.println("  Total (5 days): ₱" + v1.calculateTotalBill(5));
        
        System.out.println("\nVehicle 2: " + v2.getVehicleInfo());
        System.out.println("  Base Fine: ₱" + v2.calculateBaseFine());
        System.out.println("  Daily Fee: ₱" + v2.calculateDailyStorageFee());
        System.out.println("  Total (5 days): ₱" + v2.calculateTotalBill(5));
    }
}