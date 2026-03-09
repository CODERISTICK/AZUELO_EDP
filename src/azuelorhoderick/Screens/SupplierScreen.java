/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package azuelorhoderick.Screens;

import azuelorhoderick.DBConnection;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;


public class SupplierScreen extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SupplierScreen.class.getName());

    
    private int selectedSupplierId = -1;

    private final Color normalFieldColor = Color.WHITE;
    private final Color invalidFieldColor = new Color(255, 204, 204);
    
    public SupplierScreen() {
        initComponents();
        initCustomEvents();
        loadSuppliers();
        clearFieldsWithoutConfirmation();
    }
    
    private void initCustomEvents() {
    supplierSearch_txt.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            searchSuppliers();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            searchSuppliers();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            searchSuppliers();
        }
    });

    contactNumber_txt.addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            String text = contactNumber_txt.getText();

            if (!Character.isDigit(c) || text.length() >= 11) {
                e.consume();
            }
        }
    });

    email_txt.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            validateEmailField();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            validateEmailField();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            validateEmailField();
        }
    });

    supplier_tbl.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            loadSelectedRowToFields();
        }
    });
}
    
    private void validateEmailField() {
    String email = email_txt.getText().trim();

    if (email.isEmpty()) {
        email_txt.setBackground(normalFieldColor);
        return;
    }

    if (isValidEmail(email)) {
        email_txt.setBackground(normalFieldColor);
    } else {
        email_txt.setBackground(invalidFieldColor);
    }
}

private boolean isValidEmail(String email) {
    if (email.contains(" ")) {
        return false;
    }

    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    return Pattern.matches(emailRegex, email);
}

private boolean isValidPHMobile(String number) {
    return number.matches("^09\\d{9}$");
}

       private void loadSelectedRowToFields() {
    int row = supplier_tbl.getSelectedRow();
    if (row == -1) {
        return;
    }

    DefaultTableModel model = (DefaultTableModel) supplier_tbl.getModel();

    selectedSupplierId = Integer.parseInt(model.getValueAt(row, 0).toString());
    supplierName_txt.setText(model.getValueAt(row, 1).toString());
    contactPerson_txt.setText(model.getValueAt(row, 2).toString());
    contactNumber_txt.setText(model.getValueAt(row, 3).toString());
    email_txt.setText(model.getValueAt(row, 4).toString());
    address_txt.setText(model.getValueAt(row, 5).toString());
    status_cmb.setSelectedItem(model.getValueAt(row, 6).toString());

    validateEmailField();
}
       
       private void loadSuppliers() {
    DefaultTableModel model = new DefaultTableModel(
        new Object[]{"ID", "Supplier Name", "Contact Person", "Contact Number", "Email", "Address", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    String sql = "SELECT supplier_id, supplier_name, contact_person, contact_number, email, address, status " +
                 "FROM suppliers ORDER BY supplier_id ASC";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("supplier_id"),
                rs.getString("supplier_name"),
                rs.getString("contact_person"),
                rs.getString("contact_number"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getString("status")
            });
        }

        supplier_tbl.setModel(model);

        supplier_tbl.getColumnModel().getColumn(0).setMinWidth(0);
        supplier_tbl.getColumnModel().getColumn(0).setMaxWidth(0);
        supplier_tbl.getColumnModel().getColumn(0).setPreferredWidth(0);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading suppliers:\n" + e.getMessage());
    }
}
       
       private void searchSuppliers() {
    DefaultTableModel model = new DefaultTableModel(
        new Object[]{"ID", "Supplier Name", "Contact Person", "Contact Number", "Email", "Address", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    String keyword = supplierSearch_txt.getText().trim().toLowerCase();

    String sql = "SELECT supplier_id, supplier_name, contact_person, contact_number, email, address, status " +
                 "FROM suppliers " +
                 "WHERE LOWER(supplier_name) LIKE ? " +
                 "OR LOWER(contact_person) LIKE ? " +
                 "OR LOWER(email) LIKE ? " +
                 "OR LOWER(status) LIKE ? " +
                 "ORDER BY supplier_id ASC";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        String searchValue = "%" + keyword + "%";

        pst.setString(1, searchValue);
        pst.setString(2, searchValue);
        pst.setString(3, searchValue);
        pst.setString(4, searchValue);

        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("supplier_id"),
                    rs.getString("supplier_name"),
                    rs.getString("contact_person"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("status")
                });
            }
        }

        supplier_tbl.setModel(model);

        supplier_tbl.getColumnModel().getColumn(0).setMinWidth(0);
        supplier_tbl.getColumnModel().getColumn(0).setMaxWidth(0);
        supplier_tbl.getColumnModel().getColumn(0).setPreferredWidth(0);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error searching suppliers:\n" + e.getMessage());
    }
}
       
       
       private boolean validateInputs() {
    String supplierName = supplierName_txt.getText().trim();
    String contactPerson = contactPerson_txt.getText().trim();
    String contactNumber = contactNumber_txt.getText().trim();
    String email = email_txt.getText().trim();
    String address = address_txt.getText().trim();

    if (supplierName.isEmpty() || contactPerson.isEmpty() || contactNumber.isEmpty()
            || email.isEmpty() || address.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields are required.");
        return false;
    }

    if (!contactNumber.matches("\\d+")) {
        JOptionPane.showMessageDialog(this, "Contact number must contain digits only.");
        return false;
    }

    if (contactNumber.length() != 11 || !isValidPHMobile(contactNumber)) {
        JOptionPane.showMessageDialog(this, "Invalid contact number. Use a valid Philippine mobile number.");
        return false;
    }

    if (!isValidEmail(email)) {
        JOptionPane.showMessageDialog(this, "Invalid email format. No spaces allowed.");
        email_txt.setBackground(invalidFieldColor);
        return false;
    }

    email_txt.setBackground(normalFieldColor);
    return true;
}
       
       
      private boolean isDuplicateSupplier(String supplierName, String email, int excludeSupplierId) {
    String sql = "SELECT supplier_id FROM suppliers " +
                 "WHERE (LOWER(supplier_name) = LOWER(?) OR LOWER(email) = LOWER(?)) " +
                 "AND supplier_id <> ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, supplierName);
        pst.setString(2, email);
        pst.setInt(3, excludeSupplierId);

        try (ResultSet rs = pst.executeQuery()) {
            return rs.next();
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error checking duplicate supplier:\n" + e.getMessage());
        return true;
    }
}
      
      private void addSupplier() {
    if (!validateInputs()) {
        return;
    }

    String supplierName = supplierName_txt.getText().trim();
    String contactPerson = contactPerson_txt.getText().trim();
    String contactNumber = contactNumber_txt.getText().trim();
    String email = email_txt.getText().trim();
    String address = address_txt.getText().trim();
    String status = status_cmb.getSelectedItem().toString();

    if (isDuplicateSupplier(supplierName, email, -1)) {
        JOptionPane.showMessageDialog(this, "Duplicate supplier name or email already exists.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to add this supplier?",
            "Confirm Add",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    String sql = "INSERT INTO suppliers (supplier_name, contact_person, contact_number, email, address, status) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, supplierName);
        pst.setString(2, contactPerson);
        pst.setString(3, contactNumber);
        pst.setString(4, email);
        pst.setString(5, address);
        pst.setString(6, status);

        int result = pst.executeUpdate();

        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Supplier added successfully.");
            loadSuppliers();
            clearFieldsWithoutConfirmation();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add supplier.");
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error adding supplier:\n" + e.getMessage());
    }
}
      
      private void updateSupplier() {
    if (selectedSupplierId == -1) {
        JOptionPane.showMessageDialog(this, "Please select a supplier from the table first.");
        return;
    }

    if (!validateInputs()) {
        return;
    }

    String supplierName = supplierName_txt.getText().trim();
    String contactPerson = contactPerson_txt.getText().trim();
    String contactNumber = contactNumber_txt.getText().trim();
    String email = email_txt.getText().trim();
    String address = address_txt.getText().trim();
    String status = status_cmb.getSelectedItem().toString();

    if (isDuplicateSupplier(supplierName, email, selectedSupplierId)) {
        JOptionPane.showMessageDialog(this, "Duplicate supplier name or email already exists.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to update this supplier?",
            "Confirm Update",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    String sql = "UPDATE suppliers SET supplier_name=?, contact_person=?, contact_number=?, email=?, address=?, status=? " +
                 "WHERE supplier_id=?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, supplierName);
        pst.setString(2, contactPerson);
        pst.setString(3, contactNumber);
        pst.setString(4, email);
        pst.setString(5, address);
        pst.setString(6, status);
        pst.setInt(7, selectedSupplierId);

        int result = pst.executeUpdate();

        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Supplier updated successfully.");
            loadSuppliers();
            clearFieldsWithoutConfirmation();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update supplier.");
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error updating supplier:\n" + e.getMessage());
    }
}
      
      private void deleteSupplier() {
    if (selectedSupplierId == -1) {
        JOptionPane.showMessageDialog(this, "Please select a supplier from the table first.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this supplier?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    Connection con = null;
    PreparedStatement pstProducts = null;
    PreparedStatement pstPurchases = null;
    PreparedStatement pstSupplier = null;

    try {
        con = DBConnection.getConnection();
        con.setAutoCommit(false);

        String updateProductsSql = "UPDATE products SET supplier_id = NULL WHERE supplier_id = ?";
        pstProducts = con.prepareStatement(updateProductsSql);
        pstProducts.setInt(1, selectedSupplierId);
        pstProducts.executeUpdate();

        String deletePurchasesSql = "DELETE FROM purchases WHERE supplier_id = ?";
        pstPurchases = con.prepareStatement(deletePurchasesSql);
        pstPurchases.setInt(1, selectedSupplierId);
        pstPurchases.executeUpdate();

        String deleteSupplierSql = "DELETE FROM suppliers WHERE supplier_id = ?";
        pstSupplier = con.prepareStatement(deleteSupplierSql);
        pstSupplier.setInt(1, selectedSupplierId);

        int result = pstSupplier.executeUpdate();

        if (result > 0) {
            con.commit();
            JOptionPane.showMessageDialog(this, "Supplier deleted successfully.");
            loadSuppliers();
            clearFieldsWithoutConfirmation();
        } else {
            con.rollback();
            JOptionPane.showMessageDialog(this, "Failed to delete supplier.");
        }

    } catch (SQLException e) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Rollback failed:\n" + ex.getMessage());
        }

        JOptionPane.showMessageDialog(this, "Error deleting supplier:\n" + e.getMessage());

    } finally {
        try {
            if (pstProducts != null) pstProducts.close();
            if (pstPurchases != null) pstPurchases.close();
            if (pstSupplier != null) pstSupplier.close();
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error closing resources:\n" + e.getMessage());
        }
    }
}
      
      private void clearFields() {
    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to clear the fields?",
            "Confirm Clear",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        clearFieldsWithoutConfirmation();
    }
}

private void clearFieldsWithoutConfirmation() {
    supplierName_txt.setText("");
    contactPerson_txt.setText("");
    contactNumber_txt.setText("");
    email_txt.setText("");
    address_txt.setText("");
    status_cmb.setSelectedIndex(0);
    supplier_tbl.clearSelection();
    selectedSupplierId = -1;
    email_txt.setBackground(normalFieldColor);
    supplierName_txt.requestFocus();
}

        


    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        supplierSearch_txt = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        supplierName_txt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        contactPerson_txt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        contactNumber_txt = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        email_txt = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        address_txt = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        status_cmb = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        supplier_tbl = new javax.swing.JTable();
        clear_btn = new javax.swing.JButton();
        add_btn = new javax.swing.JButton();
        update_btn = new javax.swing.JButton();
        delete_btn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(817, 497));
        setMinimumSize(new java.awt.Dimension(817, 497));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(817, 497));
        jPanel1.setMinimumSize(new java.awt.Dimension(817, 497));
        jPanel1.setName(""); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(817, 497));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("SUPPLIERS");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, -1, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 50));

        jPanel3.setBackground(new java.awt.Color(245, 247, 251));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/search (1).png"))); // NOI18N
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, -1, 30));

        supplierSearch_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.add(supplierSearch_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 470, 30));

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Search:");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 50, 50));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 550, 50));

        jPanel4.setBackground(new java.awt.Color(245, 247, 251));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        supplierName_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.add(supplierName_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 220, 30));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Supplier Details");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 40));

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Supplier Name:");
        jPanel4.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, -1));

        contactPerson_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.add(contactPerson_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 220, 30));

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Contact Person:");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 90, -1));

        contactNumber_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.add(contactNumber_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 220, 30));

        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Contact Number:");
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 110, -1));

        email_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.add(email_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 220, 30));

        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Email:");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 110, -1));

        address_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.add(address_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 220, 40));

        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Address:");
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 110, -1));

        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Active:");
        jPanel4.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 70, -1));

        status_cmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Inactive" }));
        jPanel4.add(status_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 390, 220, 30));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 60, 240, 430));

        supplier_tbl.setBackground(new java.awt.Color(245, 247, 251));
        supplier_tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Supplier Name", "Contact Person", "Contact Number", "Email", "Address", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(supplier_tbl);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 550, 310));

        clear_btn.setText("CLEAR");
        clear_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear_btnActionPerformed(evt);
            }
        });
        jPanel1.add(clear_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 450, 90, 30));

        add_btn.setText("ADD");
        add_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_btnActionPerformed(evt);
            }
        });
        jPanel1.add(add_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 450, 90, 30));

        update_btn.setText("UPDATE");
        update_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                update_btnActionPerformed(evt);
            }
        });
        jPanel1.add(update_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 450, 90, 30));

        delete_btn.setText("DELETE");
        delete_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_btnActionPerformed(evt);
            }
        });
        jPanel1.add(delete_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 450, 90, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 817, 497));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void update_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_update_btnActionPerformed
        // TODO add your handling code here:
        updateSupplier();
    }//GEN-LAST:event_update_btnActionPerformed

    private void add_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_btnActionPerformed
        // TODO add your handling code here:
        addSupplier();
    }//GEN-LAST:event_add_btnActionPerformed

    private void delete_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_btnActionPerformed
        // TODO add your handling code here:
        deleteSupplier();
    }//GEN-LAST:event_delete_btnActionPerformed

    private void clear_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear_btnActionPerformed
        // TODO add your handling code here:
        clearFields();
    }//GEN-LAST:event_clear_btnActionPerformed

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new SupplierScreen().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add_btn;
    private javax.swing.JTextField address_txt;
    private javax.swing.JButton clear_btn;
    private javax.swing.JTextField contactNumber_txt;
    private javax.swing.JTextField contactPerson_txt;
    private javax.swing.JButton delete_btn;
    private javax.swing.JTextField email_txt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> status_cmb;
    private javax.swing.JTextField supplierName_txt;
    private javax.swing.JTextField supplierSearch_txt;
    private javax.swing.JTable supplier_tbl;
    private javax.swing.JButton update_btn;
    // End of variables declaration//GEN-END:variables
}
