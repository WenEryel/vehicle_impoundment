import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

public class ImpoundSystem extends JFrame {
    private DatabaseHandler db = new DatabaseHandler();
    private JTable lotTable, historyTable;
    private DefaultTableModel lotModel, historyModel;
    private JComboBox<String> cmbType;
    JTextField txtPlate, txtOwner, txtPhone, txtEmail, txtAddress, txtLicense, txtDesc, txtBaseFine, txtDailyFee, txtSearchActive, txtSearchHistory;

    // ===== CORAL PINK THEME =====
    final Color PRIMARY_CORAL = new Color(255, 107, 107);
    final Color PRIMARY_LIGHT = new Color(255, 135, 135);
    final Color BACKGROUND_MAIN = new Color(255, 245, 245);
    final Color CARD_BG = Color.WHITE;
    final Color BORDER_COLOR = new Color(255, 227, 227);
    final Color TEXT_PRIMARY = new Color(238, 90, 82);
    final Color TEXT_SECONDARY = new Color(99, 110, 114);
    final Color SUCCESS_GREEN = new Color(81, 207, 102);
    final Color HOVER_PINK = new Color(251, 196, 196);
    final Color TAB_ACTIVE = new Color(255, 227, 227);
    final Color TAB_INACTIVE = Color.WHITE;
    final Color HEADER_DARK = new Color(180, 60, 60);  // Dark color for headers
    
    final Font MAIN_FONT = new Font("Inter", Font.PLAIN, 14);
    final Font HEADER_FONT = new Font("Inter", Font.BOLD, 16);
    final Font TITLE_FONT = new Font("Inter", Font.BOLD, 18);

    public ImpoundSystem() {
        setTitle("Vehicle Impound Management System");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_MAIN);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(HEADER_FONT);
        tabs.setBackground(BACKGROUND_MAIN);
        tabs.setForeground(TEXT_PRIMARY);

        tabs.addTab("  Impound Vehicle  ", createImpoundPanel());
        tabs.addTab("  Active Lot  ", createActiveLotPanel());
        tabs.addTab("  History Logs  ", createHistoryPanel());

        updateTabColors(tabs);
        tabs.addChangeListener(e -> {
            refreshTables();
            updateTabColors(tabs);
        });

        add(tabs);
        refreshTables();
        setVisible(true);
    }

    private JPanel createImpoundPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_MAIN);
        
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(CARD_BG);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 2, true),
            new EmptyBorder(40, 50, 40, 50)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        JLabel titleLabel = new JLabel("Impound Vehicle");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        formCard.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        txtPlate = createModernFieldWithPlaceholder("e.g., ABC-1234");
        txtOwner = createModernFieldWithPlaceholder("e.g., Juan Dela Cruz");
        txtPhone = createModernFieldWithPlaceholder("e.g., 09171234567");
        txtEmail = createModernFieldWithPlaceholder("e.g., owner@email.com");
        txtAddress = createModernFieldWithPlaceholder("e.g., 123 Main St., City");
        txtLicense = createModernFieldWithPlaceholder("e.g., N01-12-345678");
        txtDesc = createModernFieldWithPlaceholder("e.g., Illegal parking, No helmet");
        
        String[] types = {"Motorcycle", "Tricycle", "Sedan", "SUV", "AUV/Van", "Jeepney", "Truck", "Bus"};
        cmbType = new JComboBox<>(types);
        styleComboBox(cmbType);
        
        txtBaseFine = createModernFieldWithPlaceholder("Default: 1500");
        txtBaseFine.setText("1500");
        txtDailyFee = createModernFieldWithPlaceholder("Default: 200");
        txtDailyFee.setText("200");

        int row = 1;
        addModernFormRow(formCard, "Plate Number", txtPlate, gbc, row++);
        addModernFormRow(formCard, "Owner Name", txtOwner, gbc, row++);
        addModernFormRow(formCard, "Phone Number", txtPhone, gbc, row++);
        addModernFormRow(formCard, "Email Address", txtEmail, gbc, row++);
        addModernFormRow(formCard, "Home Address", txtAddress, gbc, row++);
        addModernFormRow(formCard, "License Number", txtLicense, gbc, row++);
        addModernFormRow(formCard, "Vehicle Type", cmbType, gbc, row++);
        addModernFormRow(formCard, "Violation Description", txtDesc, gbc, row++);
        addModernFormRow(formCard, "Base Fine (₱)", txtBaseFine, gbc, row++);
        addModernFormRow(formCard, "Daily Storage Fee (₱)", txtDailyFee, gbc, row++);

        JButton btnImpound = createCoralButton("IMPOUND VEHICLE");
        btnImpound.addActionListener(e -> impoundVehicle());
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        formCard.add(btnImpound, gbc);

        panel.add(formCard);
        return panel;
    }

    private JPanel createActiveLotPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_MAIN);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_MAIN);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_MAIN);

        JButton btnRelease = createSuccessButton("COMPUTE & RELEASE");
        btnRelease.setPreferredSize(new Dimension(220, 45));
        btnRelease.addActionListener(e -> processRelease());

        JButton btnUpdate = createCoralButton("UPDATE INFO");
        btnUpdate.setPreferredSize(new Dimension(180, 45));
        btnUpdate.addActionListener(e -> updateVehicleInfo());

        buttonPanel.add(btnRelease);
        buttonPanel.add(btnUpdate);

        JPanel searchPanel = createSearchPanel(
            e -> {
                if(!txtSearchActive.getText().isEmpty())
                    db.searchTable(lotModel, "active_lot", txtSearchActive.getText());
            },
            e -> {
                txtSearchActive.setText("");
                db.loadTable(lotModel, "active_lot");
            }
        );
        txtSearchActive = (JTextField) searchPanel.getComponent(1);

        topPanel.add(buttonPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        String[] cols = {"ID", "Plate", "Owner", "Type", "Fine", "Fee", "Phone", "Email", "Address", "License", "Violation", "Date In"};
        lotModel = new DefaultTableModel(cols, 0);
        lotTable = createModernTable(lotModel);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(createModernScrollPane(lotTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_MAIN);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel searchPanel = createSearchPanel(
            e -> {
                if(!txtSearchHistory.getText().isEmpty())
                    db.searchTable(historyModel, "history_log", txtSearchHistory.getText());
            },
            e -> {
                txtSearchHistory.setText("");
                db.loadTable(historyModel, "history_log");
            }
        );
        txtSearchHistory = (JTextField) searchPanel.getComponent(1);

        String[] cols = {"ID", "Plate", "Owner", "Type", "Days", "Paid", "Phone", "Email", "Address", "License", "Violation", "Date Out"};
        historyModel = new DefaultTableModel(cols, 0);
        historyTable = createModernTable(historyModel);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(createModernScrollPane(historyTable), BorderLayout.CENTER);
        return panel;
    }

    // ===== UI COMPONENTS =====
    
    private JTextField createModernField() {
        JTextField field = new JTextField();
        field.setFont(MAIN_FONT);
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 2, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        field.setPreferredSize(new Dimension(350, 42));
        return field;
    }

    private JTextField createModernFieldWithPlaceholder(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(150, 150, 150, 100));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    FontMetrics fm = g2.getFontMetrics();
                    int x = getInsets().left;
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(placeholder, x, y);
                    g2.dispose();
                }
            }
        };
        
        field.setFont(MAIN_FONT);
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 2, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        field.setPreferredSize(new Dimension(350, 42));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { field.repaint(); }
            public void focusLost(java.awt.event.FocusEvent e) { field.repaint(); }
        });
        return field;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(MAIN_FONT);
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(new LineBorder(BORDER_COLOR, 2, true));
        combo.setPreferredSize(new Dimension(350, 42));
    }

    private void addModernFormRow(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Inter", Font.BOLD, 13));
        label.setForeground(TEXT_SECONDARY);
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(field, gbc);
    }

    private JButton createCoralButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_CORAL, 0, getHeight(), PRIMARY_LIGHT);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Inter", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createSuccessButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SUCCESS_GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Inter", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTable createModernTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? BACKGROUND_MAIN : Color.WHITE);
                    c.setForeground(TEXT_PRIMARY);
                } else {
                    c.setBackground(new Color(255, 227, 227));
                    c.setForeground(TEXT_PRIMARY);
                }
                return c;
            }
        };
        
        table.setFont(MAIN_FONT);
        table.setRowHeight(35);
        table.setGridColor(BORDER_COLOR);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // FIXED HEADER STYLING - Dark background with white text
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 13));
        header.setOpaque(true);
        
        // Custom header renderer to ensure colors apply properly
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setBackground(HEADER_DARK);  // Dark background
                label.setForeground(Color.WHITE);   // White text
                label.setFont(new Font("Inter", Font.BOLD, 13));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, HEADER_DARK));
                label.setOpaque(true);
                return label;
            }
        });
        
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            String colName = table.getColumnName(i);
            if (colName.equals("ID")) column.setPreferredWidth(50);
            else if (colName.equals("Plate")) column.setPreferredWidth(100);
            else if (colName.equals("Owner")) column.setPreferredWidth(140);
            else if (colName.equals("Type")) column.setPreferredWidth(100);
            else if (colName.contains("Fine") || colName.contains("Fee") || colName.equals("Days") || colName.equals("Paid")) {
                column.setPreferredWidth(90);
                column.setCellRenderer(centerRenderer);
            }
            else if (colName.equals("Phone")) column.setPreferredWidth(130);
            else if (colName.equals("Email")) column.setPreferredWidth(200);
            else if (colName.equals("Address")) column.setPreferredWidth(180);
            else if (colName.equals("License")) column.setPreferredWidth(130);
            else if (colName.contains("Violation")) column.setPreferredWidth(150);
            else column.setPreferredWidth(150);
        }
        return table;
    }

    private JScrollPane createModernScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BACKGROUND_MAIN);
        sp.setBorder(new LineBorder(BORDER_COLOR, 2, true));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    private JPanel createSearchPanel(java.awt.event.ActionListener searchAction, java.awt.event.ActionListener resetAction) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(BACKGROUND_MAIN);
        JLabel lbl = new JLabel("Search:");
        lbl.setFont(MAIN_FONT);
        lbl.setForeground(TEXT_SECONDARY);
        JTextField txt = createModernField();
        txt.setPreferredSize(new Dimension(300, 38));
        JButton btnSearch = createSmallButton("Search", PRIMARY_CORAL);
        btnSearch.addActionListener(searchAction);
        JButton btnReset = createSmallButton("Refresh", TEXT_SECONDARY);
        btnReset.addActionListener(resetAction);
        panel.add(lbl);
        panel.add(txt);
        panel.add(btnSearch);
        panel.add(btnReset);
        return panel;
    }

    private JButton createSmallButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Inter", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 38));
        return btn;
    }

    private void updateTabColors(JTabbedPane tabs) {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            tabs.setBackgroundAt(i, i == tabs.getSelectedIndex() ? TAB_ACTIVE : TAB_INACTIVE);
            tabs.setForegroundAt(i, TEXT_PRIMARY);
        }
    }

    // ===== LOGIC =====
    
    private void impoundVehicle() {
        String plate = txtPlate.getText().trim();
        if(plate.isEmpty()) {
            showModernDialog("Error", "Plate Number is required!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double base = Double.parseDouble(txtBaseFine.getText());
            double daily = Double.parseDouble(txtDailyFee.getText());
            if (base < 0 || daily < 0) {
                showModernDialog("Error", "Fees cannot be negative!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String result = db.addVehicle(plate, txtOwner.getText(), cmbType.getSelectedItem().toString(),
                base, daily, txtPhone.getText(), txtEmail.getText(), txtAddress.getText(),
                txtLicense.getText(), txtDesc.getText());
            if(result.startsWith("Error")) {
                showModernDialog("Error", result, JOptionPane.ERROR_MESSAGE);
            } else {
                showModernDialog("Success", 
                    result.startsWith("WARNING") ? result : "Vehicle impounded successfully!",
                    result.startsWith("WARNING") ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshTables();
            }
        } catch (NumberFormatException ex) {
            showModernDialog("Error", "Invalid fees. Please enter valid numbers.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processRelease() {
        int row = lotTable.getSelectedRow();
        if (row == -1) {
            showModernDialog("Error", "Please select a vehicle first!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(lotTable.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to release this vehicle?",
            "Confirm Release", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String receipt = db.releaseVehicle(id);
            showModernDialog("Release Receipt", receipt, JOptionPane.INFORMATION_MESSAGE);
            refreshTables();
        }
    }

    private void updateVehicleInfo() {
        int row = lotTable.getSelectedRow();
        if (row == -1) {
            showModernDialog("Error", "Please select a vehicle first!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(lotTable.getValueAt(row, 0).toString());
        String currentPhone = lotTable.getValueAt(row, 6).toString();
        String currentEmail = lotTable.getValueAt(row, 7).toString();
        String currentAddress = lotTable.getValueAt(row, 8).toString();
        
        JTextField phoneField = new JTextField(currentPhone);
        JTextField emailField = new JTextField(currentEmail);
        JTextField addressField = new JTextField(currentAddress);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Update Vehicle Information", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String updateResult = db.updateVehicle(id, phoneField.getText(), 
                                                  emailField.getText(), 
                                                  addressField.getText());
            if (updateResult.startsWith("Success")) {
                showModernDialog("Success", updateResult, JOptionPane.INFORMATION_MESSAGE);
                refreshTables();
            } else {
                showModernDialog("Error", updateResult, JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtPlate.setText("");
        txtOwner.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        txtLicense.setText("");
        txtDesc.setText("");
    }

    private void refreshTables() {
        db.loadTable(lotModel, "active_lot");
        db.loadTable(historyModel, "history_log");
    }

    private void showModernDialog(String title, String message, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new ImpoundSystem());
    }
}