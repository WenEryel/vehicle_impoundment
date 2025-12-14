// ABSTRACTION: Interface defining contract for releasable items
public interface Releasable {
    
    // Abstract methods that must be implemented
    boolean canBeReleased();
    
    double calculateTotalBill(int daysHeld);
    
    String generateReceipt(int daysHeld);
    
    // Default method (Java 8+)
    default String getReleaseStatus() {
        return canBeReleased() ? "Ready for Release" : "Cannot be Released";
    }
}