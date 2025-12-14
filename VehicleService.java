// BUSINESS LAYER - Handles business logic and validation
// This completes the 3-layer architecture:
// 1. Presentation Layer (ImpoundSystem.java)
// 2. Business Layer (VehicleService.java) <- THIS FILE
// 3. Data Access Layer (DatabaseHandler.java)

public class VehicleService {
    private DatabaseHandler db = new DatabaseHandler();
    
    /**
     * Business logic for impounding a vehicle
     * Validates input and delegates to data layer
     */
    public ValidationResult validateAndImpound(String plate, String owner, String type, 
                                               double baseFine, double dailyFee,
                                               String phone, String email, String address,
                                               String license, String desc) {
        // BUSINESS VALIDATION
        if (plate == null || plate.trim().isEmpty()) {
            return new ValidationResult(false, "Plate Number is required!");
        }
        
        // Validate plate format (example: ABC-1234)
        if (!isValidPlateFormat(plate)) {
            return new ValidationResult(false, "Invalid plate format! Use format: ABC-1234");
        }
        
        if (baseFine < 0 || dailyFee < 0) {
            return new ValidationResult(false, "Fees cannot be negative!");
        }
        
        // Business rule: Minimum base fine
        if (baseFine < 500) {
            return new ValidationResult(false, "Base fine must be at least ₱500!");
        }
        
        // Business rule: Validate email format
        if (email != null && !email.isEmpty() && !isValidEmail(email)) {
            return new ValidationResult(false, "Invalid email format!");
        }
        
        // Business rule: Validate phone format
        if (phone != null && !phone.isEmpty() && !isValidPhone(phone)) {
            return new ValidationResult(false, "Invalid phone format! Use: 09XXXXXXXXX");
        }
        
        // Use OOP: Create vehicle object using factory
        Vehicle vehicle = VehicleFactory.createVehicle(type, plate, owner);
        
        // Optional: Use suggested fees from vehicle type
        double suggestedBase = vehicle.calculateBaseFine();
        double suggestedDaily = vehicle.calculateDailyStorageFee();
        
        // Delegate to data layer
        String result = db.addVehicle(plate, owner, type, baseFine, dailyFee, 
                                      phone, email, address, license, desc);
        
        return new ValidationResult(
            !result.startsWith("Error"), 
            result
        );
    }
    
    /**
     * Business logic for calculating release bill
     */
    public ReleaseCalculation calculateReleaseBill(int vehicleId, String vehicleType, 
                                                   double baseFine, double dailyFee, 
                                                   int daysHeld) {
        // Business calculation
        double totalBill = baseFine + (dailyFee * daysHeld);
        
        // Apply business rule: Minimum 1 day charge
        if (daysHeld < 1) {
            daysHeld = 1;
        }
        
        // Generate receipt
        String receipt = String.format(
            "=== OFFICIAL RECEIPT ===\n" +
            "Vehicle ID: %d\n" +
            "Type: %s\n" +
            "Days Held: %d\n" +
            "Base Fine: ₱%.2f\n" +
            "Daily Fee: ₱%.2f × %d days\n" +
            "=======================\n" +
            "TOTAL: ₱%.2f\n" +
            "=======================",
            vehicleId, vehicleType, daysHeld, baseFine, dailyFee, daysHeld, totalBill
        );
        
        return new ReleaseCalculation(vehicleId, daysHeld, baseFine, totalBill, receipt);
    }
    
    /**
     * Update vehicle information with validation
     */
    public ValidationResult updateVehicleInfo(int id, String phone, String email, String address) {
        // Validation
        if (phone == null || phone.trim().isEmpty()) {
            return new ValidationResult(false, "Phone number is required!");
        }
        
        if (!isValidPhone(phone)) {
            return new ValidationResult(false, "Invalid phone format! Use: 09XXXXXXXXX");
        }
        
        if (email != null && !email.isEmpty() && !isValidEmail(email)) {
            return new ValidationResult(false, "Invalid email format!");
        }
        
        String result = db.updateVehicle(id, phone, email, address);
        return new ValidationResult(!result.startsWith("Error"), result);
    }
    
    // ===== VALIDATION HELPER METHODS =====
    
    private boolean isValidPlateFormat(String plate) {
        // Simple validation: XXX-XXXX or similar
        return plate.matches("[A-Z0-9]{2,3}-[A-Z0-9]{3,4}") || plate.length() >= 5;
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    private boolean isValidPhone(String phone) {
        // Philippine mobile format: 09XXXXXXXXX (11 digits)
        return phone.matches("09\\d{9}");
    }
    
    // ===== INNER CLASSES FOR RETURN VALUES =====
    
    /**
     * Validation Result - encapsulates validation outcome
     */
    public static class ValidationResult {
        public final boolean success;
        public final String message;
        
        public ValidationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
    
    /**
     * Release Calculation - encapsulates release details
     */
    public static class ReleaseCalculation {
        public final int vehicleId;
        public final int daysHeld;
        public final double baseFine;
        public final double totalBill;
        public final String receipt;
        
        public ReleaseCalculation(int vehicleId, int daysHeld, double baseFine, 
                                 double totalBill, String receipt) {
            this.vehicleId = vehicleId;
            this.daysHeld = daysHeld;
            this.baseFine = baseFine;
            this.totalBill = totalBill;
            this.receipt = receipt;
        }
    }
}