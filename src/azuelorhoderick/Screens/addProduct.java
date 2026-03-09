/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package azuelorhoderick.Screens;

import azuelorhoderick.DBConnection;
import static com.mysql.cj.conf.PropertyKey.logger;
import javax.swing.JOptionPane;
import javax.swing.*;
import java.sql.*;
import java.text.DecimalFormat;


import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;

import java.io.ByteArrayInputStream;





public class addProduct extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(addProduct.class.getName());


    private final Dashboard dashboard;   // ✅ to refresh JTable
    private boolean editMode = false;
    private Integer editingProductId = null;
    
    private String selectedImageBase64 = null;
    private File selectedImageFile = null;
    private final Random random = new Random();
  
   
   

    
    public addProduct() {
    this(null);
}

    public addProduct(Dashboard dashboard) {
        initComponents();
        this.dashboard = dashboard;

        update_btn.setEnabled(false); // ✅ disabled by default
        loadCategoriesFromDB(); // ✅ combo from DB
        loadSuppliersFromDB();
        setupAutoBarcode();

    }

    private void loadCategoriesFromDB() {
    category_cmb.removeAllItems();
    category_cmb.addItem("Select Category");

    String sql = "SELECT category_name FROM categories ORDER BY category_name ASC";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            category_cmb.addItem(rs.getString("category_name"));
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    private void loadSuppliersFromDB() {
    supplier_cmb.removeAllItems();
    supplier_cmb.addItem("Select Supplier");

    String sql = "SELECT supplier_name FROM suppliers ORDER BY supplier_name ASC";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            supplier_cmb.addItem(rs.getString("supplier_name"));
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading suppliers: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    private Integer getSupplierIdByName(String supplierName) {
    String sql = "SELECT supplier_id FROM suppliers WHERE supplier_name=? LIMIT 1";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, supplierName);

        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) return rs.getInt("supplier_id");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Supplier lookup error: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
}


    
    private Integer getCategoryIdByName(String categoryName) {
    String sql = "SELECT category_id FROM categories WHERE category_name=? LIMIT 1";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, categoryName);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) return rs.getInt("category_id");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Category lookup error: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
}
    
    public void setEditMode(int productId, String name, String categoryName, String supplierName,
                        String barcode, String unitPrice, String costPrice, String qty,
                        String reorder, String unit, String status) {

    this.editMode = true;
    this.editingProductId = productId;

    add_btn.setEnabled(false);
    update_btn.setEnabled(true);

    productName_txt.setText(name);
    category_cmb.setSelectedItem(categoryName);
    supplier_cmb.setSelectedItem(supplierName);
    barcode_txt.setText(barcode);
    unitPrice_txt.setText(unitPrice);
    costPrice_txt.setText(costPrice);
    quantity_txt.setText(qty);
    reorderLevel_txt.setText(reorder);
    unitMeasure_cmb.setSelectedItem(unit);
    status_cmb.setSelectedItem(status);
    loadProductImageFromDB(productId);
}
    
    
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

private Integer parseIntField(String label, JTextField tf) {
    try { return Integer.parseInt(tf.getText().trim()); }
    catch (Exception e) {
        JOptionPane.showMessageDialog(this, label + " must be a valid number.");
        tf.requestFocus();
        return null;
    }
}

private Double parseDoubleField(String label, JTextField tf) {
    try { return Double.parseDouble(tf.getText().trim()); }
    catch (Exception e) {
        JOptionPane.showMessageDialog(this, label + " must be a valid number.");
        tf.requestFocus();
        return null;
    }
}

    

    private boolean validateForm() {
    if (isBlank(productName_txt.getText())) {
        JOptionPane.showMessageDialog(this, "Product Name is required.");
        productName_txt.requestFocus();
        return false;
    }

    if (category_cmb.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(this, "Please select a Category.");
        category_cmb.requestFocus();
        return false;
    }

    if (supplier_cmb.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(this, "Please select a Supplier.");
        supplier_cmb.requestFocus();
        return false;
    }

    if (isBlank(barcode_txt.getText())) {
        JOptionPane.showMessageDialog(this, "Barcode is required.");
        barcode_txt.requestFocus();
        return false;
    }

    if (isBarcodeDuplicate(barcode_txt.getText().trim(), editMode ? editingProductId : null)) {
        JOptionPane.showMessageDialog(this, "Barcode already exists. Please use a unique barcode.");
        barcode_txt.requestFocus();
        return false;
    }

    if (parseDoubleField("Selling Price", unitPrice_txt) == null) return false;
    if (parseDoubleField("Cost Price", costPrice_txt) == null) return false;

    Integer qty = parseIntField("Stock Quantity", quantity_txt);
    if (qty == null) return false;
    if (qty < 0) {
        JOptionPane.showMessageDialog(this, "Quantity cannot be negative.");
        quantity_txt.requestFocus();
        return false;
    }

    Integer reorder = parseIntField("Reorder Level", reorderLevel_txt);
    if (reorder == null) return false;
    if (reorder < 0) {
        JOptionPane.showMessageDialog(this, "Reorder Level cannot be negative.");
        reorderLevel_txt.requestFocus();
        return false;
    }

    if (unitMeasure_cmb.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(this, "Please select Unit.");
        return false;
    }

    if (status_cmb.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(this, "Please select Status.");
        return false;
    }

    return true;
}


     private void setImagePreview(File file) {
    try {
        Image img = ImageIO.read(file);
        if (img != null) {
            Image scaled = img.getScaledInstance(
                image_lbl.getWidth(),
                image_lbl.getHeight(),
                Image.SCALE_SMOOTH
            );
            image_lbl.setIcon(new ImageIcon(scaled));
            image_lbl.setText("");
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error previewing image: " + e.getMessage());
    }
}

private String encodeImageToBase64(File file) throws IOException {
    byte[] fileContent = Files.readAllBytes(file.toPath());
    return Base64.getEncoder().encodeToString(fileContent);
}
   

    private void clearImage() {
    selectedImageFile = null;
    selectedImageBase64 = null;
    image_lbl.setIcon(null);
    image_lbl.setText("");
}
    
    
    private boolean isBarcodeDuplicate(String barcode, Integer excludeProductId) {
    String sql;

    if (excludeProductId == null) {
        sql = "SELECT product_id FROM products WHERE barcode = ? LIMIT 1";
    } else {
        sql = "SELECT product_id FROM products WHERE barcode = ? AND product_id <> ? LIMIT 1";
    }

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, barcode);

        if (excludeProductId != null) {
            pst.setInt(2, excludeProductId);
        }

        try (ResultSet rs = pst.executeQuery()) {
            return rs.next();
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Barcode check error: " + e.getMessage());
        e.printStackTrace();
    }

    return true;
}
    
    
    private String generateUniqueBarcode() {
    String barcode;
    do {
        barcode = String.valueOf(10000 + random.nextInt(900000));
    } while (isBarcodeDuplicate(barcode, editMode ? editingProductId : null));
    return barcode;
}
    
    private void setupAutoBarcode() {
    autoGenerateBarcode_rbt.addActionListener(evt -> {
        if (autoGenerateBarcode_rbt.isSelected()) {
            String generated = generateUniqueBarcode();
            barcode_txt.setText(generated);
            barcode_txt.setEditable(false);
        } else {
            barcode_txt.setText("");
            barcode_txt.setEditable(true);
            barcode_txt.requestFocus();
        }
    });
}



    private void loadProductImageFromDB(int productId) {
    String sql = "SELECT product_image FROM products WHERE product_id = ? LIMIT 1";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, productId);

        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                String base64 = rs.getString("product_image");

                if (base64 != null && !base64.trim().isEmpty()) {
                    byte[] imageBytes = Base64.getDecoder().decode(base64);
                    ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                    Image img = ImageIO.read(bis);

                    if (img != null) {
                        Image scaled = img.getScaledInstance(
                            image_lbl.getWidth(),
                            image_lbl.getHeight(),
                            Image.SCALE_SMOOTH
                        );
                        image_lbl.setIcon(new ImageIcon(scaled));
                        image_lbl.setText("");
                        selectedImageBase64 = base64;
                    } else {
                        image_lbl.setIcon(null);
                        image_lbl.setText("");
                    }
                } else {
                    image_lbl.setIcon(null);
                    image_lbl.setText("");
                    selectedImageBase64 = null;
                }
            }
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading product image: " + e.getMessage());
        e.printStackTrace();
    }
}
    
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        exit_btn = new javax.swing.JButton();
        add_btn = new javax.swing.JButton();
        update_btn = new javax.swing.JButton();
        clear_btn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        productName_txt = new javax.swing.JTextField();
        category_cmb = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        costPrice_txt = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        unitPrice_txt = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        description_txt = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        barcode_txt = new javax.swing.JTextField();
        quantity_txt = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        status_cmb = new javax.swing.JComboBox<>();
        unitMeasure_cmb = new javax.swing.JComboBox<>();
        reorderLevel_txt = new javax.swing.JTextField();
        dateAdded_txt = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
        supplier_cmb = new javax.swing.JComboBox<>();
        autoGenerateBarcode_rbt = new javax.swing.JRadioButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        image_lbl = new javax.swing.JLabel();
        RemoveImage_btn = new javax.swing.JButton();
        chooseImage_btn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Product Manager");
        setMaximumSize(new java.awt.Dimension(750, 498));
        setMinimumSize(new java.awt.Dimension(750, 498));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(245, 247, 251));
        jPanel1.setMaximumSize(new java.awt.Dimension(750, 498));
        jPanel1.setMinimumSize(new java.awt.Dimension(750, 498));
        jPanel1.setPreferredSize(new java.awt.Dimension(750, 498));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 20, 1, 410));

        jPanel3.setBackground(new java.awt.Color(204, 204, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 430, 730, 1));

        exit_btn.setBackground(new java.awt.Color(153, 0, 0));
        exit_btn.setText("EXIT");
        exit_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exit_btnActionPerformed(evt);
            }
        });
        jPanel1.add(exit_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 450, 90, 30));

        add_btn.setBackground(new java.awt.Color(51, 255, 0));
        add_btn.setText("ADD");
        add_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_btnActionPerformed(evt);
            }
        });
        jPanel1.add(add_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 450, 90, 30));

        update_btn.setBackground(new java.awt.Color(0, 0, 255));
        update_btn.setText("UPDATE");
        update_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                update_btnActionPerformed(evt);
            }
        });
        jPanel1.add(update_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 450, 90, 30));

        clear_btn.setBackground(new java.awt.Color(204, 255, 204));
        clear_btn.setText("CLEAR");
        clear_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear_btnActionPerformed(evt);
            }
        });
        jPanel1.add(clear_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 450, 90, 30));

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Category:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 70, 30));

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Product Name:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 90, 30));

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Cost Price:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, 70, 30));

        productName_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(productName_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, 240, 30));

        category_cmb.setBackground(new java.awt.Color(255, 255, 255));
        category_cmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Chocolate & Candy", "Coffee", "Chips", "Biscuits & Cookies", "Instant Noodles", "Shampoo", "Soap", "Toothpaste", "Lotion", "Deodorant" }));
        jPanel1.add(category_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 130, 240, 30));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("PRODUCT IMAGE");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 220, 230, 20));

        jPanel4.setBackground(new java.awt.Color(204, 204, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, -140, 250, 1));

        jPanel5.setBackground(new java.awt.Color(204, 204, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 260, 1));

        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Description:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 90, 30));

        costPrice_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(costPrice_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 270, 240, 30));

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Supplier:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 90, 100, 30));

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Unit Price:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 70, 30));

        unitPrice_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(unitPrice_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 310, 240, 30));

        description_txt.setBackground(new java.awt.Color(255, 255, 255));
        description_txt.setColumns(20);
        description_txt.setRows(5);
        jScrollPane1.setViewportView(description_txt);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, 240, -1));

        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Barcode:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 70, 30));

        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Stock Quantity:");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 90, 30));

        barcode_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(barcode_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, 120, 30));

        quantity_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(quantity_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 350, 240, 30));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("PRODUCT INFORMATION");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 230, 20));

        jPanel6.setBackground(new java.awt.Color(204, 204, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 210, 280, 1));

        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Reorder Level:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 390, 90, 30));

        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Unit of Measure:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 50, 100, 30));

        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Status:");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 130, 100, 40));

        status_cmb.setBackground(new java.awt.Color(255, 255, 255));
        status_cmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Available", "Out of Stock" }));
        jPanel1.add(status_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 130, 240, 30));

        unitMeasure_cmb.setBackground(new java.awt.Color(255, 255, 255));
        unitMeasure_cmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Piece", "Pack", "Box", "Bottle", "Can", "Sachet", "Tube", "Cup", "Pouch" }));
        jPanel1.add(unitMeasure_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 50, 240, 30));

        reorderLevel_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(reorderLevel_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 390, 240, 30));

        dateAdded_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(dateAdded_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 170, 240, 30));

        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("Image Preview:");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 250, 100, 20));

        supplier_cmb.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(supplier_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 90, 240, 30));

        autoGenerateBarcode_rbt.setForeground(new java.awt.Color(0, 0, 0));
        autoGenerateBarcode_rbt.setText("Auto- Generate");
        jPanel1.add(autoGenerateBarcode_rbt, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 91, -1, 30));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Additional Information");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 10, 230, 20));

        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setText("Date Added:");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 160, 100, 50));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel7.add(image_lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 250, 120));

        RemoveImage_btn.setText("Remove");
        RemoveImage_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveImage_btnActionPerformed(evt);
            }
        });
        jPanel7.add(RemoveImage_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 110, 30));

        chooseImage_btn.setText("Choose Image");
        chooseImage_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseImage_btnActionPerformed(evt);
            }
        });
        jPanel7.add(chooseImage_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 110, 30));

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, 250, 170));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 750, 498));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void add_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_btnActionPerformed
        // TODO add your handling code here:
        if (!validateForm()) return;

    String name = productName_txt.getText().trim();
    String desc = description_txt.getText().trim();

    String categoryName = String.valueOf(category_cmb.getSelectedItem());
    Integer categoryId = getCategoryIdByName(categoryName);
    if (categoryId == null) return;

    // ⭐ GET SUPPLIER FIRST (MUST BE BEFORE SQL)
    String supplierName = String.valueOf(supplier_cmb.getSelectedItem());
    Integer supplierId = getSupplierIdByName(supplierName);
    if (supplierId == null) {
        JOptionPane.showMessageDialog(this, "Please select a valid Supplier.");
        return;
    }

    String barcode = barcode_txt.getText().trim();
    double unitPrice = Double.parseDouble(unitPrice_txt.getText().trim());
    double costPrice = Double.parseDouble(costPrice_txt.getText().trim());
    int qty = Integer.parseInt(quantity_txt.getText().trim());
    int reorder = Integer.parseInt(reorderLevel_txt.getText().trim());

    String unit = String.valueOf(unitMeasure_cmb.getSelectedItem());
    String status = String.valueOf(status_cmb.getSelectedItem());

    java.util.Date chosen = dateAdded_txt.getDate();
    Timestamp dateAdded = (chosen == null)
            ? new Timestamp(System.currentTimeMillis())
            : new Timestamp(chosen.getTime());

    // ⭐ SQL MUST BE STRING
    String sql =
    "INSERT INTO products (" +
    "product_name, description, category_id, supplier_id, " +
    "barcode, unit_price, cost_price, " +
    "stock_quantity, reorder_level, product_image, " +
    "unit_of_measure, status, date_added) " +
    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection con = DBConnection.getConnection()) {

        con.setAutoCommit(false);

        int newId = -1;

        try (PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // ⭐ PARAMETER ORDER MUST MATCH SQL
            pst.setString(1, name);
            pst.setString(2, desc);
            pst.setInt(3, categoryId);
            pst.setInt(4, supplierId);
            pst.setString(5, barcode);
            pst.setDouble(6, unitPrice);
            pst.setDouble(7, costPrice);
            pst.setInt(8, qty);
            pst.setInt(9, reorder);
            pst.setString(10, selectedImageBase64); // image
            pst.setString(11, unit);
            pst.setString(12, status);
            pst.setTimestamp(13, dateAdded);

            int rows = pst.executeUpdate();

            if (rows <= 0) {
                con.rollback();
                JOptionPane.showMessageDialog(this, "Insert failed.");
                return;
            }

            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) newId = keys.getInt(1);
            }
        }

        // ⭐ INVENTORY INSERT (UNCHANGED)
        String invSql =
            "INSERT INTO inventory (product_id, quantity_in, quantity_out, current_stock, last_updated) " +
            "VALUES (?, ?, 0, ?, NOW()) " +
            "ON DUPLICATE KEY UPDATE " +
            "quantity_in=VALUES(quantity_in), quantity_out=0, current_stock=VALUES(current_stock), last_updated=NOW()";

        try (PreparedStatement invPst = con.prepareStatement(invSql)) {
            invPst.setInt(1, newId);
            invPst.setInt(2, qty);
            invPst.setInt(3, qty);
            invPst.executeUpdate();
        }

        con.commit();

        DecimalFormat df = new DecimalFormat("#,##0.00");

        String receipt =
            "✅ PRODUCT ADDED\n\n" +
            "Product ID: " + newId + "\n" +
            "Name: " + name + "\n" +
            "Category: " + categoryName + "\n" +
            "Supplier: " + supplierName + "\n" +   // ⭐ NOW INCLUDED
            "Barcode: " + barcode + "\n" +
            "Unit: " + unit + "\n" +
            "Unit Price: ₱" + df.format(unitPrice) + "\n" +
            "Cost Price: ₱" + df.format(costPrice) + "\n" +
            "Quantity: " + qty + "\n" +
            "Reorder Level: " + reorder + "\n" +
            "Status: " + status + "\n";

        JOptionPane.showMessageDialog(this, receipt, "Receipt", JOptionPane.INFORMATION_MESSAGE);

        if (dashboard != null) {
            dashboard.loadProductsToTable();
            dashboard.loadInventoryToTable();
        }

        this.dispose();

    } catch (SQLIntegrityConstraintViolationException dup) {
        JOptionPane.showMessageDialog(this, "Barcode already exists.");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        e.printStackTrace();
    }
    }//GEN-LAST:event_add_btnActionPerformed

    private void update_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_update_btnActionPerformed
        // TODO add your handling code here:
         // EDIT MODE CHECK
    if (!editMode || editingProductId == null) {
        JOptionPane.showMessageDialog(this, "Update is only available in EDIT mode.");
        return;
    }
    if (!validateForm()) return;

    // ===== GET VALUES =====
    String name = productName_txt.getText().trim();
    String desc = description_txt.getText().trim();

    String categoryName = String.valueOf(category_cmb.getSelectedItem());
    Integer categoryId = getCategoryIdByName(categoryName);
    if (categoryId == null) return;

    // ⭐ GET SUPPLIER
    String supplierName = String.valueOf(supplier_cmb.getSelectedItem());
    Integer supplierId = getSupplierIdByName(supplierName);
    if (supplierId == null) {
        JOptionPane.showMessageDialog(this, "Please select a valid Supplier.");
        return;
    }

    String barcode = barcode_txt.getText().trim();
    double unitPrice = Double.parseDouble(unitPrice_txt.getText().trim());
    double costPrice = Double.parseDouble(costPrice_txt.getText().trim());
    int qty = Integer.parseInt(quantity_txt.getText().trim());
    int reorder = Integer.parseInt(reorderLevel_txt.getText().trim());
    String unit = String.valueOf(unitMeasure_cmb.getSelectedItem());
    String status = String.valueOf(status_cmb.getSelectedItem());

    // ===== SQL =====
    String prodSql =
    "UPDATE products SET " +
    "product_name=?, description=?, category_id=?, supplier_id=?, barcode=?, unit_price=?, cost_price=?, " +
    "stock_quantity=?, reorder_level=?, product_image=?, unit_of_measure=?, status=? " +
    "WHERE product_id=?";

    String invUpdate =
        "UPDATE inventory SET quantity_in=?, quantity_out=0, current_stock=?, last_updated=NOW() " +
        "WHERE product_id=?";

    String invInsert =
        "INSERT INTO inventory (product_id, quantity_in, quantity_out, current_stock, last_updated) " +
        "VALUES (?, ?, 0, ?, NOW())";

    try (Connection con = DBConnection.getConnection()) {

        con.setAutoCommit(false);

        // ===== UPDATE PRODUCT =====
        int prodRows;
        try (PreparedStatement pst = con.prepareStatement(prodSql)) {

            pst.setString(1, name);
            pst.setString(2, desc);
            pst.setInt(3, categoryId);
            pst.setInt(4, supplierId);
            pst.setString(5, barcode);
            pst.setDouble(6, unitPrice);
            pst.setDouble(7, costPrice);
            pst.setInt(8, qty);
            pst.setInt(9, reorder);
            pst.setString(10, selectedImageBase64);
            pst.setString(11, unit);
            pst.setString(12, status);
            pst.setInt(13, editingProductId);

            prodRows = pst.executeUpdate();
        }

        if (prodRows <= 0) {
            con.rollback();
            JOptionPane.showMessageDialog(this, "Update failed (product not found).");
            return;
        }

        // ===== UPDATE INVENTORY =====
        int invRows;
        try (PreparedStatement inv = con.prepareStatement(invUpdate)) {
            inv.setInt(1, qty);
            inv.setInt(2, qty);
            inv.setInt(3, editingProductId);
            invRows = inv.executeUpdate();
        }

        // if inventory row missing → create one
        if (invRows == 0) {
            try (PreparedStatement ins = con.prepareStatement(invInsert)) {
                ins.setInt(1, editingProductId);
                ins.setInt(2, qty);
                ins.setInt(3, qty);
                ins.executeUpdate();
            }
        }

        con.commit();

        JOptionPane.showMessageDialog(this, "✅ Product Updated Successfully!");

        if (dashboard != null) {
            dashboard.loadProductsToTable();
            dashboard.loadInventoryToTable();
        }

        this.dispose();

    } catch (SQLIntegrityConstraintViolationException dup) {
        JOptionPane.showMessageDialog(this, "Barcode already exists. Please use a unique barcode.");
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
    }
    }//GEN-LAST:event_update_btnActionPerformed

    private void clear_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear_btnActionPerformed
        // TODO add your handling code here:
         productName_txt.setText("");
    description_txt.setText("");
    barcode_txt.setText("");
    unitPrice_txt.setText("");
    costPrice_txt.setText("");
    quantity_txt.setText("");
    reorderLevel_txt.setText("");

    category_cmb.setSelectedIndex(0);
    supplier_cmb.setSelectedIndex(0);
    unitMeasure_cmb.setSelectedIndex(0);
    status_cmb.setSelectedIndex(0);
    dateAdded_txt.setDate(null);

    clearImage();
    autoGenerateBarcode_rbt.setSelected(false);
    barcode_txt.setEditable(true);
    }//GEN-LAST:event_clear_btnActionPerformed

    private void exit_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exit_btnActionPerformed
        // TODO add your handling code here:
        int choice = JOptionPane.showConfirmDialog(
        this,
        "Close Product Manager?",
        "Confirm Exit",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );
    if (choice == JOptionPane.YES_OPTION) dispose();
    }//GEN-LAST:event_exit_btnActionPerformed

    private void RemoveImage_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveImage_btnActionPerformed
        // TODO add your handling code here:
        clearImage();
    }//GEN-LAST:event_RemoveImage_btnActionPerformed

    private void chooseImage_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseImage_btnActionPerformed
        // TODO add your handling code here:
         JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Choose Product Image");

    int result = chooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();

        try {
            selectedImageBase64 = encodeImageToBase64(file);
            selectedImageFile = file;
            setImagePreview(file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage());
        }
    }
    }//GEN-LAST:event_chooseImage_btnActionPerformed

    
   public static void main(String args[]) {
    try {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
        logger.log(java.util.logging.Level.SEVERE, null, ex);
    }

    java.awt.EventQueue.invokeLater(() -> new addProduct().setVisible(true));
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton RemoveImage_btn;
    private javax.swing.JButton add_btn;
    private javax.swing.JRadioButton autoGenerateBarcode_rbt;
    private javax.swing.JTextField barcode_txt;
    private javax.swing.JComboBox<String> category_cmb;
    private javax.swing.JButton chooseImage_btn;
    private javax.swing.JButton clear_btn;
    private javax.swing.JTextField costPrice_txt;
    private com.toedter.calendar.JDateChooser dateAdded_txt;
    private javax.swing.JTextArea description_txt;
    private javax.swing.JButton exit_btn;
    private javax.swing.JLabel image_lbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField productName_txt;
    private javax.swing.JTextField quantity_txt;
    private javax.swing.JTextField reorderLevel_txt;
    private javax.swing.JComboBox<String> status_cmb;
    private javax.swing.JComboBox<String> supplier_cmb;
    private javax.swing.JComboBox<String> unitMeasure_cmb;
    private javax.swing.JTextField unitPrice_txt;
    private javax.swing.JButton update_btn;
    // End of variables declaration//GEN-END:variables
}
