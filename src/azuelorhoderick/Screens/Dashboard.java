/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */



package azuelorhoderick.Screens;

import javax.swing.JOptionPane;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.Timer;

import azuelorhoderick.DBConnection;
import azuelorhoderick.LowStock;
import java.awt.CardLayout;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;






public class Dashboard extends javax.swing.JFrame {
    
    private java.awt.CardLayout cardLayout;

// Card names //edit
   private static final String CARD_PRODUCTS = "PRODUCTS";
   private static final String CARD_INVENTORY = "INVENTORY";
   private static final String CARD_STOCK = "STOCK";
   private static final String CARD_POS = "POS";
   private static final String CARD_REPORTS = "REPORTS";
   private static final String CARD_USERS = "USERS";
   
   
   //edit
   private final java.awt.Color NAV_ACTIVE = new java.awt.Color(37, 99, 235);
   private final java.awt.Color NAV_DEFAULT = java.awt.Color.WHITE;
   private final java.awt.Color TEXT_ACTIVE = java.awt.Color.WHITE;
   private final java.awt.Color TEXT_DEFAULT = new java.awt.Color(40, 40, 40);




   private static final String CARD_INV_STATUS = "INVENTORY_STATUS";
   private static final String CARD_LOW_STOCK = "LOW_STOCK";
   private static final String CARD_MOVEMENTS = "STOCK_MOVEMENTS";

   private CardLayout reportsCardLayout;
   
   
   //
   private azuelorhoderick.LowStock lowStockCtrl;
   private azuelorhoderick.StockMovement stockMoveCtrl;

   public void initReportsControllers() {
       lowStockCtrl = new azuelorhoderick.LowStock(this);
       lowStockCtrl.init();

       stockMoveCtrl = new azuelorhoderick.StockMovement(this);
       stockMoveCtrl.init();
}

   public void refreshReports() {
       if (lowStockCtrl != null) lowStockCtrl.refresh();          // you will add this
       if (stockMoveCtrl != null) stockMoveCtrl.refresh();        // you will add this
}




//for Inventory status
// ===== Inventory Status Report getters =====
   public javax.swing.JComboBox<String> getInventoryCategoryCmb() { return inventoryCategory_cmb; }
   public javax.swing.JComboBox<String> getInventoryAllProductCmb() { return InventoryAllProduct_cmb; }
   public javax.swing.JTextField getInventorySearchTxt() { return InventoryStatSearch_txt; }

   public javax.swing.JButton getInventoryRefreshBtn() { return inventoryRefresh_btn; }
   public javax.swing.JButton getInventoryExportCsvBtn() { return inventoryExportCSV_btn; }
   public javax.swing.JButton getInventoryPdfBtn() { return inventoryPdf_btn; }

   public javax.swing.JTable getInventoryStatusTbl() { return inventoryStatus_tbl; }



  // for low stock
    public JTextField getLowStockSearchTxt(){ return lowStockSearch_txt; }
    public JButton getLowStockRefreshBtn(){ return lowStockrefresh_btn; }
    public JButton getLowStockExportCsvBtn(){ return lowStockExportCSV_btn; }
    public JButton getLowStockPdfBtn(){ return lowStockPdf_btn; }
    public JComboBox<String> getCategoryLowStockCmb(){ return categoryLowStock_cmb; }
    public JTable getLowStockTbl(){ return lowStock_tbl; }

  
     //stockmovementGetter
   
    public JButton getRefresh_btn5(){ return refresh_btn5; }
    public JButton getMovementExportCsv_btn(){ return MovementExportCsv_btn; }
    public JButton getPdfMovement_btn(){ return pdfMovement_btn; }
    public JComboBox<String> getMovementType_txt(){ return movementType_txt; }
    public JTextField getSearchMovement_btn(){ return searchMovement_btn; }
    public JComboBox<String> getCategoryMovement_cmb(){ return categoryMovement_cmb; }
    public JTable getStockMovement_tbl(){ return stockMovement_tbl; }
    
    
    
    //test for pos
   public javax.swing.JPanel getPOSContainerPanel() {
    return posControllerPanel;
}

   
    public Dashboard() {
        initComponents();
        startDateTimePH();
        setupCards();
        loadUsersToTable();
        setupUserSearch();
        new azuelorhoderick.InventoryStatus(this).init();
        new azuelorhoderick.LowStock(this).init();
        new azuelorhoderick.StockMovement(this).init();
        

        loadProductsToTable();          // ✅ add
        setupProductEditRule();
        loadInventoryToTable();
        
        // VERY IMPORTANT LINE
        reportsCardLayout = new CardLayout();
        reportsRightPanel.setLayout(reportsCardLayout);

        setupReportsPanels(); // create panels
    
        initReportsControllers();
    
   
    }
    
    private void startDateTimePH() {

    ZoneId phZone = ZoneId.of("Asia/Manila");

    DateTimeFormatter dateFormat =
            DateTimeFormatter.ofPattern("MMM dd, yyyy");

    DateTimeFormatter timeFormat =
            DateTimeFormatter.ofPattern("hh:mm:ss a");

    Timer timer = new Timer(1000, e -> {
        LocalDateTime now = LocalDateTime.now(phZone);

        Date_lbl.setText(now.format(dateFormat));
        time_lbl.setText(now.format(timeFormat));
    });

    timer.start();
}
    
    public Dashboard(String fullName, String role) {
    initComponents();
    startDateTimePH();
    setupCards(); // ✅ IMPORTANT (this initializes cardLayout)
    loadUsersToTable();
     setupUserSearch();
     new azuelorhoderick.InventoryStatus(this).init();
     
     loadProductsToTable();          // ✅ add
     setupProductEditRule();
     loadInventoryToTable();
     initReportsControllers();
     new azuelorhoderick.InventoryStatus(this).init();
     new azuelorhoderick.LowStock(this).init();
     new azuelorhoderick.StockMovement(this).init();
     
     
       // VERY IMPORTANT LINE
    reportsCardLayout = new CardLayout();
    reportsRightPanel.setLayout(reportsCardLayout);

    setupReportsPanels(); // create panels

    String firstName = fullName.split(" ")[0];
    role_lbl.setText("Welcome, " + firstName + " (" + role + ")");
}

    
    
    //edit
    private void setupCards() {
    cardLayout = new java.awt.CardLayout();
    jPanel1.setLayout(cardLayout);

    // ✅ register YOUR real panels as cards
    jPanel1.add(productPanel, CARD_PRODUCTS);
    jPanel1.add(inventoryPanel, CARD_INVENTORY);
    jPanel1.add(stockPanel, CARD_STOCK);
    jPanel1.add(posControllerPanel, CARD_POS);
    jPanel1.add(reportPanel, CARD_REPORTS);
    jPanel1.add(userPanel, CARD_USERS);

    showCard(CARD_PRODUCTS); // default
}

    
    private javax.swing.JPanel createPlaceholder(String title) {
    javax.swing.JPanel p = new javax.swing.JPanel();
    p.setBackground(new java.awt.Color(245, 247, 250));
    p.setLayout(new java.awt.GridBagLayout());

    javax.swing.JLabel lbl = new javax.swing.JLabel(title);
    lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
    lbl.setForeground(new java.awt.Color(60, 60, 60));

    p.add(lbl);
    return p;
}
    
    private void resetNav() {
    navProducts.setBackground(NAV_DEFAULT);
    navInventory.setBackground(NAV_DEFAULT);
    navStock.setBackground(NAV_DEFAULT);
    navPosController.setBackground(NAV_DEFAULT);
    navReports.setBackground(NAV_DEFAULT);
    navUsers.setBackground(NAV_DEFAULT);

    lblProducts.setForeground(TEXT_DEFAULT);
    lblInventory.setForeground(TEXT_DEFAULT);
    lblStock.setForeground(TEXT_DEFAULT);
    lblPosController.setForeground(TEXT_DEFAULT);
    lblReports.setForeground(TEXT_DEFAULT);
    lblUsers.setForeground(TEXT_DEFAULT);
}

    private void showCard(String card) {
    cardLayout.show(jPanel1, card);
    resetNav();

    switch (card) {
        case CARD_PRODUCTS:
            navProducts.setBackground(NAV_ACTIVE);
            lblProducts.setForeground(TEXT_ACTIVE);
            break;
        case CARD_INVENTORY:
            navInventory.setBackground(NAV_ACTIVE);
            lblInventory.setForeground(TEXT_ACTIVE);
            break;
        case CARD_STOCK:
            navStock.setBackground(NAV_ACTIVE);
            lblStock.setForeground(TEXT_ACTIVE);
            break;
        case CARD_POS:
            navPosController.setBackground(NAV_ACTIVE);
            lblPosController.setForeground(TEXT_ACTIVE);
            break;
        case CARD_REPORTS:
            navReports.setBackground(NAV_ACTIVE);
            lblReports.setForeground(TEXT_ACTIVE);
            break;
        case CARD_USERS:
            navUsers.setBackground(NAV_ACTIVE);
            lblUsers.setForeground(TEXT_ACTIVE);
            break;
    }
}
    
    public void loadUsersToTable() {
        DefaultTableModel model = (DefaultTableModel) user_tbl.getModel();
    model.setRowCount(0);

    String sql = "SELECT user_id, first_name, last_name, role, email, contact_number, " +
                 "status, date_created, last_login " +
                 "FROM users ORDER BY user_id ASC";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("user_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("role"),
                rs.getString("email"),
                rs.getString("contact_number"),
                rs.getString("status"),
                rs.getTimestamp("date_created"), // ok to show Timestamp
                rs.getTimestamp("last_login")    // can be null
            });
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        e.printStackTrace();
    }
}
    
private Integer getSelectedUserId() {
    int row = user_tbl.getSelectedRow();
    if (row == -1) return null;
    Object val = user_tbl.getValueAt(row, 0); // user_id column
    if (val == null) return null;
    return Integer.valueOf(val.toString());    
}

private String getSelectedFullname() {
    int row = user_tbl.getSelectedRow();
    if (row == -1) return null;

    String first = String.valueOf(user_tbl.getValueAt(row, 1)); // first_name
    String last  = String.valueOf(user_tbl.getValueAt(row, 2)); // last_name
    return (first + " " + last).trim();
}

private String getSelectedRole() {
    int row = user_tbl.getSelectedRow();
    if (row == -1) return null;
    Object val = user_tbl.getValueAt(row, 3); // role column
    return val == null ? null : val.toString();
}

private String getSelectedEmail() {
    int row = user_tbl.getSelectedRow();
    if (row == -1) return null;
    Object val = user_tbl.getValueAt(row, 4); // email
    return val == null ? "" : val.toString();
}

private String getSelectedContact() {
    int row = user_tbl.getSelectedRow();
    if (row == -1) return null;
    Object val = user_tbl.getValueAt(row, 5); // contact_number
    return val == null ? "" : val.toString();
}

private String getSelectedStatus() {
    int row = user_tbl.getSelectedRow();
    if (row == -1) return null;
    Object val = user_tbl.getValueAt(row, 6); // status
    return val == null ? "Active" : val.toString();
}

private String getSelectedDateCreated() {
    int row = user_tbl.getSelectedRow();
    if (row == -1) return null;
    Object val = user_tbl.getValueAt(row, 7); // date_created
    return val == null ? "" : val.toString();
}

private String fetchUsernameById(int userId) {
     String sql = "SELECT username FROM users WHERE user_id=? LIMIT 1";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, userId);

        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) return rs.getString("username");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error fetching username: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
}


    private void loadUsersToTable(String keyword) {
          DefaultTableModel model = (DefaultTableModel) user_tbl.getModel();
    model.setRowCount(0);

    String sql = "SELECT user_id, first_name, last_name, role, email, contact_number, " +
                 "status, date_created, last_login " +
                 "FROM users " +
                 "WHERE first_name LIKE ? OR last_name LIKE ? " +
                 "ORDER BY user_id ASC";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        String k = "%" + keyword + "%";
        pst.setString(1, k);
        pst.setString(2, k);

        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("role"),
                    rs.getString("email"),
                    rs.getString("contact_number"),
                    rs.getString("status"),
                    rs.getTimestamp("date_created"),
                    rs.getTimestamp("last_login")
                });
            }
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Search Error: " + e.getMessage());
        e.printStackTrace();
    }
}

      
      private void setupUserSearch() {
    searchName_txt.getDocument().addDocumentListener(new DocumentListener() {

        private void runSearch() {
            String keyword = searchName_txt.getText().trim();

            if (keyword.isEmpty()) {
                loadUsersToTable(); // show all
            } else {
                loadUsersToTable(keyword); // filtered
            }
        }

        @Override public void insertUpdate(DocumentEvent e) { runSearch(); }
        @Override public void removeUpdate(DocumentEvent e) { runSearch(); }
        @Override public void changedUpdate(DocumentEvent e) { runSearch(); }
    });
}
      
      
      
   // addProduct
public void loadProductsToTable() {
    DefaultTableModel model = (DefaultTableModel) product_tbl.getModel();
    model.setRowCount(0);

    String sql =
        "SELECT p.product_id, p.barcode, p.product_name, " +
        "       c.category_name AS category, " +
        "       s.supplier_name AS supplier, " +
        "       p.unit_of_measure, p.unit_price, p.cost_price, " +
        "       COALESCE(i.current_stock, 0) AS stock_quantity, " +  // ✅ USE INVENTORY STOCK
        "       p.reorder_level, p.status, p.date_added " +
        "FROM products p " +
        "JOIN categories c ON p.category_id = c.category_id " +
        "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
        "LEFT JOIN inventory i ON i.product_id = p.product_id " +     // ✅ JOIN INVENTORY
        "ORDER BY p.product_id ASC";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            model.addRow(new Object[] {
                rs.getInt("product_id"),
                rs.getString("barcode"),
                rs.getString("product_name"),
                rs.getString("category"),
                rs.getString("supplier"),
                rs.getString("unit_of_measure"),
                rs.getBigDecimal("unit_price"),
                rs.getBigDecimal("cost_price"),
                rs.getInt("stock_quantity"),   // ✅ NOW REAL STOCK
                rs.getInt("reorder_level"),
                rs.getString("status"),
                rs.getTimestamp("date_added")
            });
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        e.printStackTrace();
    }
}

      
      
      private void setupProductEditRule() {
    edit_btn.setEnabled(false);

    product_tbl.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            edit_btn.setEnabled(product_tbl.getSelectedRow() != -1);
        }
    });
}
      
      public void loadInventoryToTable() {
    DefaultTableModel model = (DefaultTableModel) inventory_tbl.getModel();
    model.setRowCount(0);

    String sql =
        "SELECT p.product_id, p.barcode, p.product_name, " +
        "       s.supplier_name AS supplier, " +   // ✅ ADDED
        "       c.category_name AS category, " +
        "       i.current_stock, p.reorder_level, p.unit_of_measure, " +
        "       p.cost_price, p.unit_price, " +
        "       CASE " +
        "           WHEN COALESCE(i.current_stock,0) = 0 THEN 'OUT OF STOCK' " +
        "           WHEN COALESCE(i.current_stock,0) <= p.reorder_level THEN 'LOW STOCK' " +
        "           ELSE 'IN STOCK' " +
        "       END AS stock_status, " +
        "       i.last_updated " +
        "FROM products p " +
        "JOIN categories c ON p.category_id = c.category_id " +
        "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " + // ✅ JOIN SUPPLIER
        "LEFT JOIN inventory i ON i.product_id = p.product_id " +
        "ORDER BY p.product_id ASC";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            model.addRow(new Object[] {
                rs.getInt("product_id"),
                rs.getString("barcode"),
                rs.getString("product_name"),
                rs.getString("supplier"),  // ✅ ADD THIS COLUMN IN JTable
                rs.getString("category"),
                rs.getInt("current_stock"),
                rs.getInt("reorder_level"),
                rs.getString("unit_of_measure"),
                rs.getBigDecimal("cost_price"),
                rs.getBigDecimal("unit_price"),
                rs.getString("stock_status"),
                rs.getTimestamp("last_updated")
            });
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading inventory: " + e.getMessage());
        e.printStackTrace();
    }
}

      
      //reports nav
      

     private void setupReportsPanels() {

    reportsRightPanel.removeAll(); // IMPORTANT

    reportsRightPanel.add(pnlInventoryStatus, CARD_INV_STATUS);
    reportsRightPanel.add(pnlLowStock, CARD_LOW_STOCK);
    reportsRightPanel.add(pnlStockMovements, CARD_MOVEMENTS);

    reportsCardLayout.show(reportsRightPanel, CARD_INV_STATUS);
}


      



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dashboardNav = new javax.swing.JPanel();
        navProducts = new javax.swing.JPanel();
        lblProducts = new javax.swing.JLabel();
        lblProductIcon = new javax.swing.JLabel();
        navInventory = new javax.swing.JPanel();
        lblInventoryIcon = new javax.swing.JLabel();
        lblInventory = new javax.swing.JLabel();
        navStock = new javax.swing.JPanel();
        lblStock = new javax.swing.JLabel();
        lblStocksIcon = new javax.swing.JLabel();
        navReports = new javax.swing.JPanel();
        lblReports = new javax.swing.JLabel();
        lblReportsIcon = new javax.swing.JLabel();
        navUsers = new javax.swing.JPanel();
        lblUsers = new javax.swing.JLabel();
        lblUsersIcon = new javax.swing.JLabel();
        navPosController = new javax.swing.JPanel();
        lblPosController = new javax.swing.JLabel();
        lblPosControllerIcon = new javax.swing.JLabel();
        footer = new javax.swing.JPanel();
        Footer = new javax.swing.JLabel();
        Date_lbl = new javax.swing.JLabel();
        Footer1 = new javax.swing.JLabel();
        time_lbl = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        logout = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        role_lbl = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        reportPanel = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        inventoryStatus_btn = new javax.swing.JButton();
        lowStock_btn = new javax.swing.JButton();
        stockMovements_btn = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        reportsRightPanel = new javax.swing.JPanel();
        pnlInventoryStatus = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        inventoryCategory_cmb = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        InventoryStatSearch_txt = new javax.swing.JTextField();
        inventoryPdf_btn = new javax.swing.JButton();
        inventoryRefresh_btn = new javax.swing.JButton();
        inventoryExportCSV_btn = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        inventoryStatus_tbl = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        InventoryAllProduct_cmb = new javax.swing.JComboBox<>();
        pnlLowStock = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        lowStockSearch_txt = new javax.swing.JTextField();
        lowStockPdf_btn = new javax.swing.JButton();
        lowStockrefresh_btn = new javax.swing.JButton();
        lowStockExportCSV_btn = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        categoryLowStock_cmb = new javax.swing.JComboBox<>();
        jScrollPane5 = new javax.swing.JScrollPane();
        lowStock_tbl = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        pnlStockMovements = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        pdfMovement_btn = new javax.swing.JButton();
        refresh_btn5 = new javax.swing.JButton();
        MovementExportCsv_btn = new javax.swing.JButton();
        movementType_txt = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        categoryMovement_cmb = new javax.swing.JComboBox<>();
        jScrollPane6 = new javax.swing.JScrollPane();
        stockMovement_tbl = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        searchMovement_btn = new javax.swing.JTextField();
        inventoryPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        searchInventory_txt = new javax.swing.JTextField();
        adjustStock_btn = new javax.swing.JButton();
        receiveStock_btn = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        inventory_tbl = new javax.swing.JTable();
        productPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        product_tbl = new javax.swing.JTable();
        addProduct_btn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        search_txt = new javax.swing.JTextField();
        edit_btn = new javax.swing.JButton();
        userPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        user_tbl = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        searchName_txt = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        addUser_btn = new javax.swing.JButton();
        update_btn = new javax.swing.JButton();
        delete_btn = new javax.swing.JButton();
        changePass_btn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        stockPanel = new javax.swing.JPanel();
        posControllerPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dashboard");
        setBackground(new java.awt.Color(153, 153, 153));
        setForeground(new java.awt.Color(153, 153, 153));
        setMaximumSize(new java.awt.Dimension(1366, 760));
        setMinimumSize(new java.awt.Dimension(1366, 760));
        setPreferredSize(new java.awt.Dimension(1366, 760));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashboardNav.setBackground(new java.awt.Color(255, 255, 255));
        dashboardNav.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        navProducts.setBackground(new java.awt.Color(255, 255, 255));
        navProducts.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        navProducts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navProductsMouseClicked(evt);
            }
        });
        navProducts.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblProducts.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblProducts.setText("Products");
        navProducts.add(lblProducts, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        lblProductIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/products (1).png"))); // NOI18N
        lblProductIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblProductIconMouseClicked(evt);
            }
        });
        lblProductIcon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lblProductIconKeyPressed(evt);
            }
        });
        navProducts.add(lblProductIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        dashboardNav.add(navProducts, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 70));

        navInventory.setBackground(new java.awt.Color(255, 255, 255));
        navInventory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        navInventory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navInventoryMouseClicked(evt);
            }
        });
        navInventory.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblInventoryIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/inventory.png"))); // NOI18N
        navInventory.add(lblInventoryIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        lblInventory.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblInventory.setText("Inventory");
        navInventory.add(lblInventory, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        dashboardNav.add(navInventory, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 80, 70));

        navStock.setBackground(new java.awt.Color(255, 255, 255));
        navStock.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        navStock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navStockMouseClicked(evt);
            }
        });
        navStock.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblStock.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblStock.setText("Stock");
        lblStock.setToolTipText("");
        navStock.add(lblStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        lblStocksIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/packages.png"))); // NOI18N
        lblStocksIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblStocksIconMouseClicked(evt);
            }
        });
        navStock.add(lblStocksIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, -1, -1));

        dashboardNav.add(navStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 20, 80, 70));

        navReports.setBackground(new java.awt.Color(255, 255, 255));
        navReports.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        navReports.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navReportsMouseClicked(evt);
            }
        });
        navReports.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblReports.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblReports.setText("Reports");
        navReports.add(lblReports, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        lblReportsIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/report.png"))); // NOI18N
        navReports.add(lblReportsIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        dashboardNav.add(navReports, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 80, 70));

        navUsers.setBackground(new java.awt.Color(255, 255, 255));
        navUsers.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        navUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navUsersMouseClicked(evt);
            }
        });
        navUsers.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblUsers.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblUsers.setText("Users");
        navUsers.add(lblUsers, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        lblUsersIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/avatar (2).png"))); // NOI18N
        lblUsersIcon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        navUsers.add(lblUsersIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        dashboardNav.add(navUsers, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, 80, 70));

        navPosController.setBackground(new java.awt.Color(255, 255, 255));
        navPosController.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        navPosController.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navPosControllerMouseClicked(evt);
            }
        });
        navPosController.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPosController.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblPosController.setText("POS ");
        navPosController.add(lblPosController, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 40, -1));

        lblPosControllerIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/cashier.png"))); // NOI18N
        navPosController.add(lblPosControllerIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        dashboardNav.add(navPosController, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 80, 70));

        getContentPane().add(dashboardNav, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 1360, 100));

        footer.setBackground(new java.awt.Color(51, 0, 255));
        footer.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Footer.setForeground(new java.awt.Color(255, 255, 255));
        Footer.setText("| Time.");
        footer.add(Footer, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 10, 50, 30));

        Date_lbl.setForeground(new java.awt.Color(255, 255, 255));
        footer.add(Date_lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 10, 90, 30));

        Footer1.setForeground(new java.awt.Color(255, 255, 255));
        Footer1.setText("POS System | Date:");
        footer.add(Footer1, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, 110, 30));

        time_lbl.setForeground(new java.awt.Color(255, 255, 255));
        footer.add(time_lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 10, 90, 30));

        getContentPane().add(footer, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 670, 1360, 50));

        jPanel3.setBackground(new java.awt.Color(51, 0, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logout.setBackground(new java.awt.Color(255, 0, 0));
        logout.setForeground(new java.awt.Color(255, 255, 255));
        logout.setText("LOGOUT");
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutActionPerformed(evt);
            }
        });
        jPanel3.add(logout, new org.netbeans.lib.awtextra.AbsoluteConstraints(1200, 20, 80, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("POS SYSTEM DASHBOARD");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 250, 30));

        role_lbl.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        role_lbl.setForeground(new java.awt.Color(255, 255, 255));
        role_lbl.setText("Welcome, ");
        jPanel3.add(role_lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 10, 230, 50));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/avatar (2).png"))); // NOI18N
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 10, 30, 50));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/po.png"))); // NOI18N
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 50, 50));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 70));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        reportPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(153, 153, 153));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel9.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 180, 1));

        inventoryStatus_btn.setBackground(new java.awt.Color(255, 255, 255));
        inventoryStatus_btn.setForeground(new java.awt.Color(0, 0, 0));
        inventoryStatus_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/seo-report (1).png"))); // NOI18N
        inventoryStatus_btn.setText("Inventory Status");
        inventoryStatus_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inventoryStatus_btnActionPerformed(evt);
            }
        });
        jPanel9.add(inventoryStatus_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 160, 40));

        lowStock_btn.setBackground(new java.awt.Color(255, 255, 255));
        lowStock_btn.setForeground(new java.awt.Color(0, 0, 0));
        lowStock_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/arrow (1).png"))); // NOI18N
        lowStock_btn.setText("Low Stock           ");
        lowStock_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lowStock_btnActionPerformed(evt);
            }
        });
        jPanel9.add(lowStock_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 160, 40));

        stockMovements_btn.setBackground(new java.awt.Color(255, 255, 255));
        stockMovements_btn.setForeground(new java.awt.Color(0, 0, 0));
        stockMovements_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/warehouse.png"))); // NOI18N
        stockMovements_btn.setText("Stock Movements");
        stockMovements_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stockMovements_btnActionPerformed(evt);
            }
        });
        jPanel9.add(stockMovements_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 160, 40));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/chart.png"))); // NOI18N
        jLabel9.setText("  Reports");
        jPanel9.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 150, 40));

        reportPanel.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 180, 500));

        reportsRightPanel.setBackground(new java.awt.Color(245, 247, 251));
        reportsRightPanel.setForeground(new java.awt.Color(0, 0, 0));
        reportsRightPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlInventoryStatus.setBackground(new java.awt.Color(245, 247, 251));
        pnlInventoryStatus.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel10.setBackground(new java.awt.Color(204, 204, 204));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Search:");
        jPanel10.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 20, 60, 20));

        inventoryCategory_cmb.setBackground(new java.awt.Color(255, 255, 255));
        inventoryCategory_cmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Chocolate & Candy", "Coffee", "Chips", "Biscuits & Cookies", "Instant Noodles", "Shampoo", "Soap", "Toothpaste", "Lotion", "Deodorant" }));
        jPanel10.add(inventoryCategory_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 250, 40));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("Category:");
        jPanel10.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 60, 20));

        InventoryStatSearch_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.add(InventoryStatSearch_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, 380, 40));

        inventoryPdf_btn.setBackground(new java.awt.Color(255, 255, 255));
        inventoryPdf_btn.setForeground(new java.awt.Color(0, 0, 0));
        inventoryPdf_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/file (1).png"))); // NOI18N
        jPanel10.add(inventoryPdf_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 10, 30, 40));

        inventoryRefresh_btn.setBackground(new java.awt.Color(255, 255, 255));
        inventoryRefresh_btn.setForeground(new java.awt.Color(0, 0, 0));
        inventoryRefresh_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/refresh.png"))); // NOI18N
        inventoryRefresh_btn.setText("Refresh");
        jPanel10.add(inventoryRefresh_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 10, 120, 40));

        inventoryExportCSV_btn.setBackground(new java.awt.Color(255, 255, 255));
        inventoryExportCSV_btn.setForeground(new java.awt.Color(0, 0, 0));
        inventoryExportCSV_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/export.png"))); // NOI18N
        inventoryExportCSV_btn.setText("Export CSV");
        jPanel10.add(inventoryExportCSV_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 10, 120, 40));

        pnlInventoryStatus.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1180, 60));

        inventoryStatus_tbl.setBackground(new java.awt.Color(255, 255, 255));
        inventoryStatus_tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product", "Category", "Current Stock", "Reorder Level", "Status", "Last Updated"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(inventoryStatus_tbl);
        if (inventoryStatus_tbl.getColumnModel().getColumnCount() > 0) {
            inventoryStatus_tbl.getColumnModel().getColumn(0).setResizable(false);
            inventoryStatus_tbl.getColumnModel().getColumn(1).setResizable(false);
            inventoryStatus_tbl.getColumnModel().getColumn(2).setResizable(false);
            inventoryStatus_tbl.getColumnModel().getColumn(3).setResizable(false);
            inventoryStatus_tbl.getColumnModel().getColumn(4).setResizable(false);
            inventoryStatus_tbl.getColumnModel().getColumn(5).setResizable(false);
        }

        pnlInventoryStatus.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 1150, 320));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 255));
        jLabel10.setText("Inventory Status Report");
        pnlInventoryStatus.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 15, -1, 30));

        InventoryAllProduct_cmb.setBackground(new java.awt.Color(255, 255, 255));
        pnlInventoryStatus.add(InventoryAllProduct_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 130, 30));

        reportsRightPanel.add(pnlInventoryStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1180, 500));

        pnlLowStock.setBackground(new java.awt.Color(245, 247, 251));
        pnlLowStock.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(51, 51, 255));
        jLabel13.setText("Low Stock Report");
        pnlLowStock.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 15, -1, 30));

        jPanel12.setBackground(new java.awt.Color(204, 204, 204));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lowStockSearch_txt.setBackground(new java.awt.Color(255, 255, 255));
        lowStockSearch_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lowStockSearch_txtActionPerformed(evt);
            }
        });
        jPanel12.add(lowStockSearch_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 450, 30));

        lowStockPdf_btn.setBackground(new java.awt.Color(255, 255, 255));
        lowStockPdf_btn.setForeground(new java.awt.Color(0, 0, 0));
        lowStockPdf_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/file (1).png"))); // NOI18N
        lowStockPdf_btn.setIconTextGap(2);
        jPanel12.add(lowStockPdf_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 10, 20, 30));

        lowStockrefresh_btn.setBackground(new java.awt.Color(255, 255, 255));
        lowStockrefresh_btn.setForeground(new java.awt.Color(0, 0, 0));
        lowStockrefresh_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/refresh.png"))); // NOI18N
        lowStockrefresh_btn.setText("Refresh");
        lowStockrefresh_btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lowStockrefresh_btn.setIconTextGap(2);
        jPanel12.add(lowStockrefresh_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 10, 110, 30));

        lowStockExportCSV_btn.setBackground(new java.awt.Color(255, 255, 255));
        lowStockExportCSV_btn.setForeground(new java.awt.Color(0, 0, 0));
        lowStockExportCSV_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/export.png"))); // NOI18N
        lowStockExportCSV_btn.setText("Export CSV");
        lowStockExportCSV_btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lowStockExportCSV_btn.setIconTextGap(2);
        jPanel12.add(lowStockExportCSV_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 10, 120, 30));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 0, 0));
        jLabel18.setText("Search:");
        jPanel12.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 60, 30));

        pnlLowStock.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1180, 50));

        categoryLowStock_cmb.setBackground(new java.awt.Color(255, 255, 255));
        pnlLowStock.add(categoryLowStock_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, 450, 30));

        lowStock_tbl.setBackground(new java.awt.Color(255, 255, 255));
        lowStock_tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product", "Category", "Current Stock", "Reorder Level", "Suggested Order Qty", "Supplier"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(lowStock_tbl);
        if (lowStock_tbl.getColumnModel().getColumnCount() > 0) {
            lowStock_tbl.getColumnModel().getColumn(0).setResizable(false);
            lowStock_tbl.getColumnModel().getColumn(1).setResizable(false);
            lowStock_tbl.getColumnModel().getColumn(2).setResizable(false);
            lowStock_tbl.getColumnModel().getColumn(3).setResizable(false);
            lowStock_tbl.getColumnModel().getColumn(4).setResizable(false);
            lowStock_tbl.getColumnModel().getColumn(5).setResizable(false);
            lowStock_tbl.getColumnModel().getColumn(5).setHeaderValue("Supplier");
        }

        pnlLowStock.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 1150, 320));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("Category:");
        pnlLowStock.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 70, 30));

        reportsRightPanel.add(pnlLowStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1180, 500));

        pnlStockMovements.setBackground(new java.awt.Color(245, 247, 251));
        pnlStockMovements.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 51, 255));
        jLabel15.setText("Stock Movements Report");
        pnlStockMovements.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 15, 210, 30));

        jPanel13.setBackground(new java.awt.Color(204, 204, 204));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pdfMovement_btn.setBackground(new java.awt.Color(255, 255, 255));
        pdfMovement_btn.setForeground(new java.awt.Color(0, 0, 0));
        pdfMovement_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/file (1).png"))); // NOI18N
        pdfMovement_btn.setIconTextGap(2);
        jPanel13.add(pdfMovement_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 10, 20, 30));

        refresh_btn5.setBackground(new java.awt.Color(255, 255, 255));
        refresh_btn5.setForeground(new java.awt.Color(0, 0, 0));
        refresh_btn5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/refresh.png"))); // NOI18N
        refresh_btn5.setText("Refresh");
        refresh_btn5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        refresh_btn5.setIconTextGap(2);
        jPanel13.add(refresh_btn5, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 10, 110, 30));

        MovementExportCsv_btn.setBackground(new java.awt.Color(255, 255, 255));
        MovementExportCsv_btn.setForeground(new java.awt.Color(0, 0, 0));
        MovementExportCsv_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/export.png"))); // NOI18N
        MovementExportCsv_btn.setText("Export CSV");
        MovementExportCsv_btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MovementExportCsv_btn.setIconTextGap(2);
        jPanel13.add(MovementExportCsv_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 10, 120, 30));

        movementType_txt.setBackground(new java.awt.Color(255, 255, 255));
        movementType_txt.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "IN", "OUT" }));
        movementType_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                movementType_txtActionPerformed(evt);
            }
        });
        jPanel13.add(movementType_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 10, 180, 30));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 0, 0));
        jLabel21.setText("Movement Type:");
        jPanel13.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, 110, 30));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 0, 0));
        jLabel20.setText("Category:");
        jPanel13.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 70, 30));

        categoryMovement_cmb.setBackground(new java.awt.Color(255, 255, 255));
        categoryMovement_cmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All CATEGORIES" }));
        categoryMovement_cmb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryMovement_cmbActionPerformed(evt);
            }
        });
        jPanel13.add(categoryMovement_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 200, 30));

        pnlStockMovements.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1180, 60));

        stockMovement_tbl.setBackground(new java.awt.Color(255, 255, 255));
        stockMovement_tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Product", "Type", "Quantity", "Notes"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(stockMovement_tbl);
        if (stockMovement_tbl.getColumnModel().getColumnCount() > 0) {
            stockMovement_tbl.getColumnModel().getColumn(0).setResizable(false);
            stockMovement_tbl.getColumnModel().getColumn(1).setResizable(false);
            stockMovement_tbl.getColumnModel().getColumn(2).setResizable(false);
            stockMovement_tbl.getColumnModel().getColumn(3).setResizable(false);
            stockMovement_tbl.getColumnModel().getColumn(4).setResizable(false);
        }

        pnlStockMovements.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 1150, 310));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setText("Search:");
        pnlStockMovements.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 50, 50));

        searchMovement_btn.setBackground(new java.awt.Color(255, 255, 255));
        pnlStockMovements.add(searchMovement_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 130, 530, 30));

        reportsRightPanel.add(pnlStockMovements, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1180, 500));

        reportPanel.add(reportsRightPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 0, 1180, 500));

        jPanel1.add(reportPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 500));

        inventoryPanel.setBackground(new java.awt.Color(245, 247, 251));
        inventoryPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Search:");
        jPanel8.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 50, 40));

        searchInventory_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.add(searchInventory_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 290, 40));

        adjustStock_btn.setBackground(new java.awt.Color(255, 102, 0));
        adjustStock_btn.setForeground(new java.awt.Color(0, 0, 0));
        adjustStock_btn.setText("Adjust Stock");
        adjustStock_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adjustStock_btnActionPerformed(evt);
            }
        });
        jPanel8.add(adjustStock_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 20, 140, 40));

        receiveStock_btn.setBackground(new java.awt.Color(102, 255, 51));
        receiveStock_btn.setForeground(new java.awt.Color(0, 0, 0));
        receiveStock_btn.setText("Receive Stock");
        receiveStock_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                receiveStock_btnActionPerformed(evt);
            }
        });
        jPanel8.add(receiveStock_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 20, 140, 40));

        inventory_tbl.setBackground(new java.awt.Color(255, 255, 255));
        inventory_tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Barcode", "Product Name", "Supplier", "Category", "Current Stock", "Reorder Level", "Unit", "Cost Price", "Unit Price", "Stock Status", "Last Updated"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(inventory_tbl);
        if (inventory_tbl.getColumnModel().getColumnCount() > 0) {
            inventory_tbl.getColumnModel().getColumn(0).setMinWidth(80);
            inventory_tbl.getColumnModel().getColumn(0).setMaxWidth(80);
            inventory_tbl.getColumnModel().getColumn(1).setResizable(false);
            inventory_tbl.getColumnModel().getColumn(2).setResizable(false);
            inventory_tbl.getColumnModel().getColumn(3).setResizable(false);
            inventory_tbl.getColumnModel().getColumn(4).setResizable(false);
            inventory_tbl.getColumnModel().getColumn(5).setResizable(false);
            inventory_tbl.getColumnModel().getColumn(6).setResizable(false);
            inventory_tbl.getColumnModel().getColumn(7).setResizable(false);
            inventory_tbl.getColumnModel().getColumn(8).setResizable(false);
            inventory_tbl.getColumnModel().getColumn(9).setResizable(false);
            inventory_tbl.getColumnModel().getColumn(10).setResizable(false);
            inventory_tbl.getColumnModel().getColumn(11).setResizable(false);
        }

        jPanel8.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 1310, 390));

        inventoryPanel.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1340, 470));

        jPanel1.add(inventoryPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 500));

        productPanel.setBackground(new java.awt.Color(245, 247, 251));
        productPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        product_tbl.setBackground(new java.awt.Color(238, 242, 255));
        product_tbl.setForeground(new java.awt.Color(17, 24, 39));
        product_tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Barcode", "Product Name", "Category", "Supplier", "Unit ", "Unit Price", "Cost Price", "Quantity", "Reorder Level", "Status", "Date Added"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(product_tbl);
        if (product_tbl.getColumnModel().getColumnCount() > 0) {
            product_tbl.getColumnModel().getColumn(0).setResizable(false);
            product_tbl.getColumnModel().getColumn(1).setResizable(false);
            product_tbl.getColumnModel().getColumn(2).setResizable(false);
            product_tbl.getColumnModel().getColumn(3).setResizable(false);
            product_tbl.getColumnModel().getColumn(4).setResizable(false);
            product_tbl.getColumnModel().getColumn(5).setResizable(false);
            product_tbl.getColumnModel().getColumn(6).setResizable(false);
            product_tbl.getColumnModel().getColumn(7).setResizable(false);
            product_tbl.getColumnModel().getColumn(8).setResizable(false);
            product_tbl.getColumnModel().getColumn(9).setResizable(false);
            product_tbl.getColumnModel().getColumn(10).setResizable(false);
            product_tbl.getColumnModel().getColumn(11).setResizable(false);
        }

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 1320, 410));

        addProduct_btn.setBackground(new java.awt.Color(16, 185, 129));
        addProduct_btn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addProduct_btn.setForeground(new java.awt.Color(255, 255, 255));
        addProduct_btn.setText("Add Product");
        addProduct_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProduct_btnActionPerformed(evt);
            }
        });
        jPanel2.add(addProduct_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 10, 130, 30));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Search:");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 60, 50));

        search_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.add(search_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 250, 30));

        edit_btn.setBackground(new java.awt.Color(0, 0, 255));
        edit_btn.setText("EDIT PRODUCT");
        edit_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_btnActionPerformed(evt);
            }
        });
        jPanel2.add(edit_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1200, 10, 130, 30));

        productPanel.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1340, 470));

        jPanel1.add(productPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 500));

        userPanel.setBackground(new java.awt.Color(245, 247, 251));
        userPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        user_tbl.setBackground(new java.awt.Color(255, 255, 255));
        user_tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "user_id", "first_name", "last_name", "role", "email", "contact_number", "status", "date_created"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(user_tbl);
        if (user_tbl.getColumnModel().getColumnCount() > 0) {
            user_tbl.getColumnModel().getColumn(0).setResizable(false);
            user_tbl.getColumnModel().getColumn(1).setResizable(false);
            user_tbl.getColumnModel().getColumn(2).setResizable(false);
            user_tbl.getColumnModel().getColumn(3).setResizable(false);
            user_tbl.getColumnModel().getColumn(4).setResizable(false);
            user_tbl.getColumnModel().getColumn(5).setResizable(false);
            user_tbl.getColumnModel().getColumn(6).setResizable(false);
            user_tbl.getColumnModel().getColumn(7).setResizable(false);
        }

        jPanel4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 1070, 310));

        jLabel2.setText("Search Name:");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 100, 30));

        searchName_txt.setBackground(new java.awt.Color(255, 255, 255));
        searchName_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchName_txtActionPerformed(evt);
            }
        });
        jPanel4.add(searchName_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, 280, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel3.setText("USERS");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel7.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 230, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 230, 2));

        userPanel.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 1090, 440));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addUser_btn.setBackground(new java.awt.Color(51, 255, 51));
        addUser_btn.setText("Add User");
        addUser_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addUser_btnActionPerformed(evt);
            }
        });
        jPanel5.add(addUser_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 170, 40));

        update_btn.setBackground(new java.awt.Color(0, 0, 255));
        update_btn.setText("UPDATE");
        update_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                update_btnActionPerformed(evt);
            }
        });
        jPanel5.add(update_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 170, 40));

        delete_btn.setBackground(new java.awt.Color(204, 0, 0));
        delete_btn.setText("DELETE");
        delete_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_btnActionPerformed(evt);
            }
        });
        jPanel5.add(delete_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 170, 40));

        changePass_btn.setBackground(new java.awt.Color(255, 102, 0));
        changePass_btn.setText("CHANGE PASSWORD");
        changePass_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePass_btnActionPerformed(evt);
            }
        });
        jPanel5.add(changePass_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, 170, 40));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel4.setText("USERS");
        jPanel5.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel6.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 230, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel5.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 230, 2));

        userPanel.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 30, 230, 440));

        jPanel1.add(userPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 500));

        stockPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(stockPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 500));

        posControllerPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(posControllerPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 500));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 170, 1360, 500));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
        // TODO add your handling code here:
     
        int choice = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to logout?",
        "Logout Confirmation",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
);

if (choice == JOptionPane.YES_OPTION) {
    LoginScreen db = new LoginScreen();
    db.setVisible(true);   
    this.dispose();
}

        
    }//GEN-LAST:event_logoutActionPerformed

    private void lblProductIconKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblProductIconKeyPressed
        // TODO add your handling code here:
    
    }//GEN-LAST:event_lblProductIconKeyPressed

    private void lblProductIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblProductIconMouseClicked
        // TODO add your handling code here:
            showCard(CARD_PRODUCTS);
 
    }//GEN-LAST:event_lblProductIconMouseClicked

    private void lblStocksIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblStocksIconMouseClicked
        // TODO add your handling code here:
        showCard(CARD_STOCK);
    }//GEN-LAST:event_lblStocksIconMouseClicked

    private void navProductsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_navProductsMouseClicked
        // TODO add your handling code here:
        showCard(CARD_PRODUCTS);
    }//GEN-LAST:event_navProductsMouseClicked

    private void navInventoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_navInventoryMouseClicked
        // TODO add your handling code here:
        showCard(CARD_INVENTORY);
    }//GEN-LAST:event_navInventoryMouseClicked

    private void navStockMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_navStockMouseClicked
        // TODO add your handling code here:
        showCard(CARD_STOCK);
        
    }//GEN-LAST:event_navStockMouseClicked

    private void navReportsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_navReportsMouseClicked
        // TODO add your handling code here:
        showCard(CARD_REPORTS);
    }//GEN-LAST:event_navReportsMouseClicked

    private void navPosControllerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_navPosControllerMouseClicked
        // TODO add your handling code here:
         showCard(CARD_POS);
         new azuelorhoderick.POSController(this, 1).init();

         
    }//GEN-LAST:event_navPosControllerMouseClicked

    private void navUsersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_navUsersMouseClicked
        // TODO add your handling code here:
        showCard(CARD_USERS);
    }//GEN-LAST:event_navUsersMouseClicked

    private void addProduct_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProduct_btnActionPerformed
        // TODO add your handling code here:
         addProduct ps = new addProduct(this); // ✅ pass Dashboard
         ps.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
         ps.setLocationRelativeTo(this);
         ps.setVisible(true);
    }//GEN-LAST:event_addProduct_btnActionPerformed

    private void addUser_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUser_btnActionPerformed
        // TODO add your handling code here:
        addUser au = new addUser(this); // ✅ pass dashboard reference
         au.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    au.setLocationRelativeTo(this);
    au.setVisible(true);
       
    }//GEN-LAST:event_addUser_btnActionPerformed

    private void update_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_update_btnActionPerformed
          Integer id = getSelectedUserId();
    if (id == null) {
        JOptionPane.showMessageDialog(this, "Please select a user first.");
        return;
    }

    String fullname = getSelectedFullname();
    String role = getSelectedRole();
    String status = getSelectedStatus();
    String email = getSelectedEmail();
    String contact = getSelectedContact();
    String dateCreated = getSelectedDateCreated();
    String username = fetchUsernameById(id); // still fetch username

    if (username == null) return;

    addUser au = new addUser(this);
    au.setModeEdit(id, role, status, fullname, email, contact, dateCreated, username);
    au.setVisible(true);
    }//GEN-LAST:event_update_btnActionPerformed

    private void delete_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_btnActionPerformed
        // TODO add your handling code here:
        Integer id = getSelectedUserId();
    if (id == null) {
        JOptionPane.showMessageDialog(this, "Please select a user first.");
        return;
    }

    int choice = JOptionPane.showConfirmDialog(
            this,
            "Delete this user permanently?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );

    if (choice != JOptionPane.YES_OPTION) return;

    String sql = "DELETE FROM users WHERE user_id=?";


    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, id);
        int rows = pst.executeUpdate();

        if (rows > 0) {
            JOptionPane.showMessageDialog(this, "User Deleted Successfully!");
            loadUsersToTable(); // refresh
        } else {
            JOptionPane.showMessageDialog(this, "Delete failed (user not found).");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        e.printStackTrace();
    }
    }//GEN-LAST:event_delete_btnActionPerformed

    private void changePass_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePass_btnActionPerformed
        // TODO add your handling code here:
         Integer id = getSelectedUserId();
    if (id == null) {
        JOptionPane.showMessageDialog(this, "Please select a user first.");
        return;
    }

    String fullname = getSelectedFullname();
    String role = getSelectedRole();
    String status = getSelectedStatus();
    String email = getSelectedEmail();
    String contact = getSelectedContact();
    String dateCreated = getSelectedDateCreated();
    String username = fetchUsernameById(id);

    if (username == null) return;

    addUser au = new addUser(this);
    au.setModeChangePassword(id, role, status, fullname, email, contact, dateCreated, username);
    au.setVisible(true);
    }//GEN-LAST:event_changePass_btnActionPerformed

    private void searchName_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchName_txtActionPerformed
        // TODO add your handling code here:
        // search name
        String keyword = searchName_txt.getText().trim();
    if (keyword.isEmpty()) loadUsersToTable();
    else loadUsersToTable(keyword);
    }//GEN-LAST:event_searchName_txtActionPerformed

    private void edit_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_btnActionPerformed
        // TODO add your handling code here:
        int row = product_tbl.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a product first.");
        return;
    }

    int productId = Integer.parseInt(product_tbl.getValueAt(row, 0).toString());

    String barcode   = String.valueOf(product_tbl.getValueAt(row, 1));
    String name      = String.valueOf(product_tbl.getValueAt(row, 2));
    String category  = String.valueOf(product_tbl.getValueAt(row, 3)); // category_name
    String unit      = String.valueOf(product_tbl.getValueAt(row, 4));
    String unitPrice = String.valueOf(product_tbl.getValueAt(row, 5));
    String costPrice = String.valueOf(product_tbl.getValueAt(row, 6));
    String qty       = String.valueOf(product_tbl.getValueAt(row, 7));
    String reorder   = String.valueOf(product_tbl.getValueAt(row, 8));
    String status    = String.valueOf(product_tbl.getValueAt(row, 9));

    addProduct frm = new addProduct(this);
    frm.setEditMode(productId, name, category, barcode, unitPrice, costPrice, qty, reorder, unit, status);
    frm.setLocationRelativeTo(this);
    frm.setVisible(true);
    }//GEN-LAST:event_edit_btnActionPerformed

    private void receiveStock_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_receiveStock_btnActionPerformed

       ReceiveStockScreen rs = new ReceiveStockScreen();
       rs.setLocationRelativeTo(this);
       rs.setVisible(true);

    }//GEN-LAST:event_receiveStock_btnActionPerformed

    private void adjustStock_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adjustStock_btnActionPerformed
        // TODO add your handling code here:
        AdjustStockScreen as = new AdjustStockScreen();
        as.setLocationRelativeTo(this);
        as.setVisible(true);
    }//GEN-LAST:event_adjustStock_btnActionPerformed

    private void lowStock_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lowStock_btnActionPerformed
        // TODO add your handling code here:
        reportsCardLayout.show(reportsRightPanel, CARD_LOW_STOCK);
        
        reportsRightPanel.revalidate();
        reportsRightPanel.repaint();
    }//GEN-LAST:event_lowStock_btnActionPerformed

    private void inventoryStatus_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inventoryStatus_btnActionPerformed
        // TODO add your handling code here:
        reportsCardLayout.show(reportsRightPanel, CARD_INV_STATUS);
        
        reportsRightPanel.revalidate();
        reportsRightPanel.repaint();
    }//GEN-LAST:event_inventoryStatus_btnActionPerformed

    private void stockMovements_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stockMovements_btnActionPerformed
        // TODO add your handling code here:
         reportsCardLayout.show(reportsRightPanel, CARD_MOVEMENTS);
         reportsRightPanel.revalidate();
         reportsRightPanel.repaint();
         
    }//GEN-LAST:event_stockMovements_btnActionPerformed

    private void movementType_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_movementType_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_movementType_txtActionPerformed

    private void categoryMovement_cmbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryMovement_cmbActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categoryMovement_cmbActionPerformed

    private void lowStockSearch_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lowStockSearch_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lowStockSearch_txtActionPerformed

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Dashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Date_lbl;
    private javax.swing.JLabel Footer;
    private javax.swing.JLabel Footer1;
    private javax.swing.JComboBox<String> InventoryAllProduct_cmb;
    private javax.swing.JTextField InventoryStatSearch_txt;
    private javax.swing.JButton MovementExportCsv_btn;
    private javax.swing.JButton addProduct_btn;
    private javax.swing.JButton addUser_btn;
    private javax.swing.JButton adjustStock_btn;
    private javax.swing.JComboBox<String> categoryLowStock_cmb;
    private javax.swing.JComboBox<String> categoryMovement_cmb;
    private javax.swing.JButton changePass_btn;
    private javax.swing.JPanel dashboardNav;
    private javax.swing.JButton delete_btn;
    private javax.swing.JButton edit_btn;
    private javax.swing.JPanel footer;
    private javax.swing.JComboBox<String> inventoryCategory_cmb;
    private javax.swing.JButton inventoryExportCSV_btn;
    private javax.swing.JPanel inventoryPanel;
    private javax.swing.JButton inventoryPdf_btn;
    private javax.swing.JButton inventoryRefresh_btn;
    private javax.swing.JButton inventoryStatus_btn;
    private javax.swing.JTable inventoryStatus_tbl;
    private javax.swing.JTable inventory_tbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel lblInventory;
    private javax.swing.JLabel lblInventoryIcon;
    private javax.swing.JLabel lblPosController;
    private javax.swing.JLabel lblPosControllerIcon;
    private javax.swing.JLabel lblProductIcon;
    private javax.swing.JLabel lblProducts;
    private javax.swing.JLabel lblReports;
    private javax.swing.JLabel lblReportsIcon;
    private javax.swing.JLabel lblStock;
    private javax.swing.JLabel lblStocksIcon;
    private javax.swing.JLabel lblUsers;
    private javax.swing.JLabel lblUsersIcon;
    private javax.swing.JButton logout;
    private javax.swing.JButton lowStockExportCSV_btn;
    private javax.swing.JButton lowStockPdf_btn;
    private javax.swing.JTextField lowStockSearch_txt;
    private javax.swing.JButton lowStock_btn;
    private javax.swing.JTable lowStock_tbl;
    private javax.swing.JButton lowStockrefresh_btn;
    private javax.swing.JComboBox<String> movementType_txt;
    private javax.swing.JPanel navInventory;
    private javax.swing.JPanel navPosController;
    private javax.swing.JPanel navProducts;
    private javax.swing.JPanel navReports;
    private javax.swing.JPanel navStock;
    private javax.swing.JPanel navUsers;
    private javax.swing.JButton pdfMovement_btn;
    private javax.swing.JPanel pnlInventoryStatus;
    private javax.swing.JPanel pnlLowStock;
    private javax.swing.JPanel pnlStockMovements;
    private javax.swing.JPanel posControllerPanel;
    private javax.swing.JPanel productPanel;
    private javax.swing.JTable product_tbl;
    private javax.swing.JButton receiveStock_btn;
    private javax.swing.JButton refresh_btn5;
    private javax.swing.JPanel reportPanel;
    private javax.swing.JPanel reportsRightPanel;
    private javax.swing.JLabel role_lbl;
    private javax.swing.JTextField searchInventory_txt;
    private javax.swing.JTextField searchMovement_btn;
    private javax.swing.JTextField searchName_txt;
    private javax.swing.JTextField search_txt;
    private javax.swing.JTable stockMovement_tbl;
    private javax.swing.JButton stockMovements_btn;
    private javax.swing.JPanel stockPanel;
    private javax.swing.JLabel time_lbl;
    private javax.swing.JButton update_btn;
    private javax.swing.JPanel userPanel;
    private javax.swing.JTable user_tbl;
    // End of variables declaration//GEN-END:variables
}
