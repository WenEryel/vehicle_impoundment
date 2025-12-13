import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class DatabaseHandler {
    private final String URL = "jdbc:mysql://localhost:3306/impound_db";
    private final String USER = "root";
    private final String PASS = ""; 

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // 1. ADD VEHICLE (Updated with new fields)
    public String addVehicle(String plate, String owner, String type, double baseFine, double dailyFee, 
                             String phone, String email, String address, String license, String desc) {
        try (Connection conn = connect()) {
            PreparedStatement checkHist = conn.prepareStatement("SELECT * FROM history_log WHERE plate_number = ?");
            checkHist.setString(1, plate);
            boolean isRepeat = checkHist.executeQuery().next();

            PreparedStatement checkActive = conn.prepareStatement("SELECT * FROM active_lot WHERE plate_number = ?");
            checkActive.setString(1, plate);
            if (checkActive.executeQuery().next()) return "Error: Vehicle is already in the lot!";

            String sql = "INSERT INTO active_lot (plate_number, owner_name, vehicle_type, base_fine, daily_fee, " +
                         "phone_number, email, address, license_number, violation_desc) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                         
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, plate);
            stmt.setString(2, owner);
            stmt.setString(3, type);
            stmt.setDouble(4, baseFine);
            stmt.setDouble(5, dailyFee);
            // New Fields
            stmt.setString(6, phone);
            stmt.setString(7, email);
            stmt.setString(8, address);
            stmt.setString(9, license);
            stmt.setString(10, desc);
            
            stmt.executeUpdate();

            return isRepeat ? "WARNING: Vehicle added, but looks like a Repeat Offender!" : "Success";
        } catch (SQLException e) { return "Error: " + e.getMessage(); }
    }

    // 2. RELEASE VEHICLE (Updated to copy new fields to history)
    public String releaseVehicle(int id) {
        try (Connection conn = connect()) {
            String query = "SELECT *, GREATEST(DATEDIFF(NOW(), date_impounded), 1) as days_calculated FROM active_lot WHERE id = ?";
            PreparedStatement get = conn.prepareStatement(query);
            get.setInt(1, id);
            ResultSet rs = get.executeQuery();
            
            if (rs.next()) {
                // Get Calculations
                String plate = rs.getString("plate_number");
                String owner = rs.getString("owner_name");
                double total = rs.getDouble("base_fine") + (rs.getInt("days_calculated") * rs.getDouble("daily_fee"));
                
                // Copy to History
                String copy = "INSERT INTO history_log (plate_number, owner_name, vehicle_type, days_held, total_paid, " +
                              "phone_number, email, address, license_number, violation_desc) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                              
                PreparedStatement hist = conn.prepareStatement(copy);
                hist.setString(1, plate);
                hist.setString(2, owner);
                hist.setString(3, rs.getString("vehicle_type"));
                hist.setInt(4, rs.getInt("days_calculated"));
                hist.setDouble(5, total);
                // Copy New Fields
                hist.setString(6, rs.getString("phone_number"));
                hist.setString(7, rs.getString("email"));
                hist.setString(8, rs.getString("address"));
                hist.setString(9, rs.getString("license_number"));
                hist.setString(10, rs.getString("violation_desc"));
                
                hist.executeUpdate();

                // Delete from Active (Using Secure Statement)
                PreparedStatement del = conn.prepareStatement("DELETE FROM active_lot WHERE id = ?");
                del.setInt(1, id);
                del.executeUpdate();
                
                return String.format("--- OFFICIAL RECEIPT ---\nPlate: %s\nOwner: %s\nTotal Paid: ₱%.2f", plate, owner, total);
            }
        } catch (SQLException e) { return "Error: " + e.getMessage(); }
        return "Error: ID not found.";
    }

    // 3. LOAD & SEARCH (No changes needed, SELECT * picks up new columns automatically)
    public void loadTable(DefaultTableModel model, String tableName) {
        model.setRowCount(0);
        try (Connection conn = connect()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= colCount; i++) row.add(rs.getObject(i));
                model.addRow(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void searchTable(DefaultTableModel model, String tableName, String keyword) {
        model.setRowCount(0);
        try (Connection conn = connect()) {
            String sql = "SELECT * FROM " + tableName + " WHERE plate_number LIKE ? OR owner_name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= colCount; i++) row.add(rs.getObject(i));
                model.addRow(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}