import javax.swing.*;
import javax.swing.border.*; // Fixes all border errors
import javax.swing.table.*;
import java.awt.*;

public class ImpoundSystem extends JFrame {
    private DatabaseHandler db = new DatabaseHandler();
    private JTable lotTable, historyTable;
    private DefaultTableModel lotModel, historyModel;
    private JComboBox<String> cmbType;

    // --- COMPRESSED VARIABLES ---
    // All TextFields in one line
    JTextField txtPlate, txtOwner, txtPhone, txtEmail, txtAddress, txtLicense, txtDesc, txtBaseFine, txtDailyFee, txtSearchActive, txtSearchHistory;

    // All Colors in fewer lines
    final Color BG_DARK = new Color(45, 45, 48), BG_LIGHT = new Color(60, 60, 60), TEXT_COLOR = new Color(230, 230, 230), BORDER_COLOR = new Color(80, 80, 80);
    final Color ACCENT_GREEN = new Color(40, 167, 69), ACCENT_RED = new Color(200, 50, 50), ACCENT_BLUE = new Color(0, 122, 204);
    final Color TAB_INACTIVE_BG = Color.WHITE, TAB_INACTIVE_FG = Color.BLACK, TAB_ACTIVE_BG = new Color(180, 180, 180), TAB_ACTIVE_FG = Color.BLACK;

    // Fonts
    final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14), HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public ImpoundSystem() {
        setTitle("Impound Management System");
        setSize(1280, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        getContentPane().setBackground(BG_DARK);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(HEADER_FONT);
        tabs.setBackground(BG_DARK);

        // --- TAB 1: IMPOUND FORM ---
        JPanel panelImpound = new JPanel(new GridBagLayout());
        panelImpound.setBackground(BG_DARK);
        
        JPanel formCard = new JPanel(new GridLayout(12, 2, 10, 10)); // 12 Rows for all inputs
        formCard.setBackground(BG_LIGHT);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1), new EmptyBorder(20, 30, 20, 30)
        ));

        // Initialize Fields
        txtPlate = createStyledField(); txtOwner = createStyledField(); txtPhone = createStyledField();
        txtEmail = createStyledField(); txtAddress = createStyledField(); txtLicense = createStyledField();
        txtDesc = createStyledField();
        
        String[] types = {"Motorcycle", "Tricycle", "Sedan", "SUV", "AUV/Van", "Jeepney", "Truck", "Bus"};
        cmbType = new JComboBox<>(types); cmbType.setFont(MAIN_FONT);
        
        txtBaseFine = createStyledField(); txtBaseFine.setText("1500");
        txtDailyFee = createStyledField(); txtDailyFee.setText("200");

        // Add to Form
        addFormRow(formCard, "Plate Number:", txtPlate);
        addFormRow(formCard, "Owner Name:", txtOwner);
        addFormRow(formCard, "Phone Number:", txtPhone);
        addFormRow(formCard, "Email Address:", txtEmail);
        addFormRow(formCard, "Home Address:", txtAddress);
        addFormRow(formCard, "License No:", txtLicense);
        addFormRow(formCard, "Vehicle Type:", cmbType);
        addFormRow(formCard, "Violation Description:", txtDesc);
        addFormRow(formCard, "Base Fine (₱):", txtBaseFine);
        addFormRow(formCard, "Daily Fee (₱):", txtDailyFee);

        JButton btnImpound = createStyledButton("IMPOUND VEHICLE", ACCENT_RED);
        btnImpound.addActionListener(e -> impoundVehicle());
        
        formCard.add(new JLabel("")); formCard.add(btnImpound); // Spacer + Button
        panelImpound.add(formCard);
        tabs.addTab("  Impound Vehicle  ", panelImpound);

        // --- TAB 2: ACTIVE LOT ---
        JPanel panelLot = new JPanel(new BorderLayout(10, 10));
        panelLot.setBackground(BG_DARK);
        panelLot.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topLot = new JPanel(new BorderLayout(10, 10));
        topLot.setBackground(BG_DARK);

        JButton btnRelease = createStyledButton("COMPUTE BILL & RELEASE SELECTED", ACCENT_GREEN);
        btnRelease.setPreferredSize(new Dimension(200, 50));
        btnRelease.addActionListener(e -> processRelease());

        JPanel searchLotPanel = createSearchPanel(e -> {
             if(!txtSearchActive.getText().isEmpty()) db.searchTable(lotModel, "active_lot", txtSearchActive.getText());
        }, e -> {
            txtSearchActive.setText(""); db.loadTable(lotModel, "active_lot");
        });
        txtSearchActive = (JTextField) searchLotPanel.getComponent(1);

        topLot.add(btnRelease, BorderLayout.NORTH);
        topLot.add(searchLotPanel, BorderLayout.SOUTH);

        String[] activeCols = {"ID", "Plate", "Owner", "Type", "Fine", "Fee", "Phone", "Email", "Address", "License", "Violation", "Date In"};
        lotModel = new DefaultTableModel(activeCols, 0);
        lotTable = createStyledTable(lotModel);
        
        panelLot.add(topLot, BorderLayout.NORTH);
        panelLot.add(createStyledScrollPane(lotTable), BorderLayout.CENTER);
        tabs.addTab("  Active Lot  ", panelLot);

        // --- TAB 3: HISTORY ---
        JPanel panelHistory = new JPanel(new BorderLayout(10, 10));
        panelHistory.setBackground(BG_DARK);
        panelHistory.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel searchHistPanel = createSearchPanel(e -> {
            if(!txtSearchHistory.getText().isEmpty()) db.searchTable(historyModel, "history_log", txtSearchHistory.getText());
        }, e -> {
            txtSearchHistory.setText(""); db.loadTable(historyModel, "history_log");
        });
        txtSearchHistory = (JTextField) searchHistPanel.getComponent(1);

        String[] histCols = {"ID", "Plate", "Owner", "Type", "Days", "Paid", "Phone", "Email", "Address", "License", "Violation", "Date Out"};
        historyModel = new DefaultTableModel(histCols, 0);
        historyTable = createStyledTable(historyModel);

        panelHistory.add(searchHistPanel, BorderLayout.NORTH);
        panelHistory.add(createStyledScrollPane(historyTable), BorderLayout.CENTER);
        tabs.addTab("  History Logs  ", panelHistory);

        // --- TABS LOGIC ---
        updateTabColors(tabs);
        tabs.addChangeListener(e -> { refreshTables(); updateTabColors(tabs); });

        add(tabs);
        refreshTables();
        setVisible(true);
    }

    // --- LOGIC METHODS ---
    private void impoundVehicle() {
        String plate = txtPlate.getText();
        if(plate.isEmpty()) { JOptionPane.showMessageDialog(this, "Plate Number is required!"); return; }
        try {
            double base = Double.parseDouble(txtBaseFine.getText());
            double daily = Double.parseDouble(txtDailyFee.getText());
            if (base < 0 || daily < 0) { JOptionPane.showMessageDialog(this, "Fees cannot be negative!", "Input Error", JOptionPane.ERROR_MESSAGE); return; }

            String result = db.addVehicle(plate, txtOwner.getText(), cmbType.getSelectedItem().toString(), 
                base, daily, txtPhone.getText(), txtEmail.getText(), txtAddress.getText(), txtLicense.getText(), txtDesc.getText());
            
            if(result.startsWith("Error")) JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
            else {
                JOptionPane.showMessageDialog(this, result.startsWith("WARNING") ? result : "Vehicle Impounded Successfully!", "Success", result.startsWith("WARNING") ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
                txtPlate.setText(""); txtOwner.setText(""); txtPhone.setText(""); txtEmail.setText("");
                txtAddress.setText(""); txtLicense.setText(""); txtDesc.setText("");
            }
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Invalid Fees. Please enter valid numbers."); }
    }

    private void processRelease() {
        int row = lotTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a vehicle first!"); return; }
        int id = Integer.parseInt(lotTable.getValueAt(row, 0).toString());
        if (JOptionPane.showConfirmDialog(this, "Release this vehicle?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, db.releaseVehicle(id));
            refreshTables(); 
        }
    }

    private void refreshTables() {
        db.loadTable(lotModel, "active_lot");
        db.loadTable(historyModel, "history_log");
    }

    // --- UI HELPERS ---
    private void updateTabColors(JTabbedPane tabs) {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            boolean isSel = (i == tabs.getSelectedIndex());
            tabs.setBackgroundAt(i, isSel ? TAB_ACTIVE_BG : TAB_INACTIVE_BG);
            tabs.setForegroundAt(i, isSel ? TAB_ACTIVE_FG : TAB_INACTIVE_FG);
        }
    }

    private void addFormRow(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(MAIN_FONT); label.setForeground(TEXT_COLOR);
        panel.add(label); panel.add(field);
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setFont(MAIN_FONT); field.setBackground(BORDER_COLOR);
        field.setForeground(Color.WHITE); field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return field;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(MAIN_FONT);
        table.setRowHeight(30);
        table.setBackground(BG_LIGHT);
        table.setForeground(TEXT_COLOR);
        
        // Styling
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(100, 100, 100));
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(Color.WHITE);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(new Color(30, 30, 30));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        // Center Text Renderer
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // --- COLUMN WIDTH ADJUSTMENTS ---
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Keep this to allow scrolling

        for (int i = 0; i < table.getColumnCount(); i++) {
            javax.swing.table.TableColumn column = table.getColumnModel().getColumn(i);
            String colName = table.getColumnName(i);

            // 1. TINY COLUMNS (ID, Days)
            if (colName.equalsIgnoreCase("ID")) {
                column.setPreferredWidth(40); // Very thin
                column.setCellRenderer(centerRenderer); 
            }
            // 2. SMALL COLUMNS (Money, Type, Plate) -> REDUCED
            else if (colName.contains("Fine") || colName.contains("Fee") || colName.startsWith("Days") || colName.equals("Paid")) {
                column.setPreferredWidth(80); // Reduced to 80px (was ~100+)
                column.setCellRenderer(centerRenderer); // Center numbers
            }
            else if (colName.equals("Plate") || colName.equals("Type")) {
                column.setPreferredWidth(90); // Reduced (was ~120+)
                column.setCellRenderer(centerRenderer);
            }
            // 3. WIDE COLUMNS (Email, Address) -> ADJUSTED
            else if (colName.equals("Email")) {
                column.setPreferredWidth(230); // INCREASED significantly (was 150)
            }
            else if (colName.contains("Address")) {
                column.setPreferredWidth(220); // Kept wide
            }
            // 4. VIOLATION -> REDUCED A BIT
            else if (colName.contains("Violation")) {
                column.setPreferredWidth(180); // Reduced (was 250)
            }
            // 5. OTHERS (Phone, Owner, Dates)
            else {
                column.setPreferredWidth(130); // Standard default
            }
        }
        
        return table;
    }
    private JScrollPane createStyledScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);    
        sp.getViewport().setBackground(BG_DARK);
        sp.setBorder(new LineBorder(BORDER_COLOR, 1));
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return sp;
    }

    private JPanel createSearchPanel(java.awt.event.ActionListener searchAction, java.awt.event.ActionListener resetAction) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(BG_DARK);
        JLabel lbl = new JLabel("Search (Plate/Owner): ");
        lbl.setFont(MAIN_FONT); lbl.setForeground(TEXT_COLOR);
        
        JTextField txt = createStyledField();
        txt.setPreferredSize(new Dimension(250, 35));
        
        JButton btnSearch = createStyledButton("Search", ACCENT_BLUE);
        btnSearch.addActionListener(searchAction);
        JButton btnReset = createStyledButton("Refresh", Color.GRAY);
        btnReset.addActionListener(resetAction);
        
        p.add(lbl); p.add(txt); p.add(btnSearch); p.add(btnReset);
        return p;
    }

    public static void main(String[] args) {
        new ImpoundSystem();
    }
}