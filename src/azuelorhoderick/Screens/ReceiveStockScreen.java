/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package azuelorhoderick.Screens;

import javax.swing.JOptionPane;
import azuelorhoderick.DBConnection;
import java.sql.*;
import java.util.Date;



public class ReceiveStockScreen extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ReceiveStockScreen.class.getName());
    private Integer selectedProductId = null;
    private final Dashboard dashboard;

    public ReceiveStockScreen() {
          this(null);
}

    public ReceiveStockScreen(Dashboard dashboard) {
         initComponents();
         this.dashboard = dashboard;

         loadProductsToCombo();
         setupProductComboListener();
}
    
    

   

  
    

    private void loadProductsToCombo() {
    Product_txt.removeAllItems();
    Product_txt.addItem("-- Select Product --");

    String sql = "SELECT product_id, product_name FROM products ORDER BY product_name ASC";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("product_id");
            String name = rs.getString("product_name");
            // Store both id + name in display text
            Product_txt.addItem(id + " - " + name);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    
    
    private void setupProductComboListener() {
    Product_txt.addActionListener(e -> {
        if (Product_txt.getSelectedIndex() <= 0) {
            selectedProductId = null;
            clearProductDetails();
            return;
        }

        String selected = String.valueOf(Product_txt.getSelectedItem());
        // format is "id - name"
        int dashIndex = selected.indexOf(" - ");
        if (dashIndex <= 0) return;

        selectedProductId = Integer.parseInt(selected.substring(0, dashIndex).trim());
        loadSelectedProductDetails(selectedProductId);
    });
}

    private void clearProductDetails() {
    barcode1_txt.setText("");
    barcode_txt.setText("");
    currentStock_lbl.setText("");
    reorderLevel_lbl.setText("");
    unit_txt.setText("");
    costPrice_txt.setText("");
    unitPrice_lbl.setText("");
}
    
    private void loadSelectedProductDetails(int productId) {
    String sql =
        "SELECT p.barcode, p.unit_of_measure, p.cost_price, p.unit_price, p.reorder_level, " +
        "       COALESCE(i.current_stock,0) AS current_stock " +
        "FROM products p " +
        "LEFT JOIN inventory i ON i.product_id = p.product_id " +
        "WHERE p.product_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, productId);

        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                String barcode = rs.getString("barcode");
                String unit = rs.getString("unit_of_measure");
                int reorder = rs.getInt("reorder_level");
                int currentStock = rs.getInt("current_stock");

                barcode1_txt.setText(barcode);
                barcode_txt.setText(barcode);
                unit_txt.setText(unit);
                reorderLevel_lbl.setText(String.valueOf(reorder));
                currentStock_lbl.setText(String.valueOf(currentStock));
                costPrice_txt.setText(rs.getBigDecimal("cost_price").toString());
                unitPrice_lbl.setText(rs.getBigDecimal("unit_price").toString());
            } else {
                clearProductDetails();
            }
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading product details: " + e.getMessage());
        e.printStackTrace();
    }
}
    
   






    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        Product_txt = new javax.swing.JComboBox<>();
        unit_txt = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        barcode_txt = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        barcode1_txt = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        currentStock_lbl = new javax.swing.JLabel();
        unitPrice_lbl = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        reorderLevel_lbl = new javax.swing.JLabel();
        ReceivingDate_txt = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        notes_txt = new javax.swing.JTextArea();
        cancel_btn = new javax.swing.JButton();
        receive_btn = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        costPrice_txt = new javax.swing.JLabel();
        quantityAdd_txt = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(779, 522));
        setMinimumSize(new java.awt.Dimension(779, 522));
        setPreferredSize(new java.awt.Dimension(779, 522));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(245, 247, 251));
        jPanel1.setMaximumSize(new java.awt.Dimension(779, 522));
        jPanel1.setMinimumSize(new java.awt.Dimension(779, 522));
        jPanel1.setName(""); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(779, 522));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 780, 1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 204, 0));
        jLabel1.setText("Receive Stock");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 8, 190, -1));

        Product_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(Product_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 70, 680, 30));

        unit_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(unit_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 190, 220, 30));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/barcode.png"))); // NOI18N
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 30, 30));

        barcode_txt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        barcode_txt.setForeground(new java.awt.Color(0, 0, 0));
        jPanel3.add(barcode_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, 110, 30));

        jPanel4.setBackground(new java.awt.Color(204, 204, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 750, 1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Barcode:");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 60, 30));

        barcode1_txt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        barcode1_txt.setForeground(new java.awt.Color(0, 0, 0));
        jPanel3.add(barcode1_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 110, 30));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 740, 70));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Notes");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 350, 50, 20));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Product:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 60, 30));

        currentStock_lbl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        currentStock_lbl.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.add(currentStock_lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 190, 110, 30));

        unitPrice_lbl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        unitPrice_lbl.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.add(unitPrice_lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 270, 110, 30));

        jPanel5.setBackground(new java.awt.Color(204, 204, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 190, 1, 100));

        reorderLevel_lbl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        reorderLevel_lbl.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.add(reorderLevel_lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 270, 110, 30));

        ReceivingDate_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(ReceivingDate_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 310, 550, 30));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Receiving Date:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 110, 50));

        notes_txt.setColumns(20);
        notes_txt.setRows(5);
        jScrollPane1.setViewportView(notes_txt);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 350, 450, 100));

        cancel_btn.setBackground(new java.awt.Color(204, 204, 255));
        cancel_btn.setForeground(new java.awt.Color(0, 0, 0));
        cancel_btn.setText("Cancel");
        cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_btnActionPerformed(evt);
            }
        });
        jPanel1.add(cancel_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 400, 130, 40));

        receive_btn.setBackground(new java.awt.Color(0, 204, 0));
        receive_btn.setText("Receive Stock");
        receive_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                receive_btnActionPerformed(evt);
            }
        });
        jPanel1.add(receive_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 350, 130, 40));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Quantity to Add:");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 110, 30));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("Unit:");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 190, 110, 30));

        costPrice_txt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        costPrice_txt.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.add(costPrice_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 230, 110, 30));

        quantityAdd_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(quantityAdd_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 230, 220, 30));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("Current Stock: ");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, 110, 30));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Reorder Level:");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 110, 30));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setText("Cost Price:");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 230, 110, 30));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Unit Price:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 270, 110, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 779, 522));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cancel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_btnActionPerformed
        // TODO add your handling code here:
        int choice = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to go back to Dashboard?",
        "Confirm Exit",
        JOptionPane.YES_NO_OPTION
    );

    if (choice == JOptionPane.YES_OPTION) {
        this.dispose();
    }
        
    }//GEN-LAST:event_cancel_btnActionPerformed

    private void receive_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_receive_btnActionPerformed
        // TODO add your handling code here:
         if (selectedProductId == null) {
        JOptionPane.showMessageDialog(this, "Please select a product.");
        return;
    }

    String qtyText = quantityAdd_txt.getText().trim();
    if (qtyText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Quantity to Add is required.");
        quantityAdd_txt.requestFocus();
        return;
    }

    int qtyToAdd;
    try {
        qtyToAdd = Integer.parseInt(qtyText);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Quantity to Add must be a valid number.");
        quantityAdd_txt.requestFocus();
        return;
    }

    if (qtyToAdd <= 0) {
        JOptionPane.showMessageDialog(this, "Quantity to Add must be greater than 0.");
        quantityAdd_txt.requestFocus();
        return;
    }

    Date chosen = ReceivingDate_txt.getDate();
    Timestamp receivingDate = (chosen == null)
            ? new Timestamp(System.currentTimeMillis())
            : new Timestamp(chosen.getTime());

    String notes = notes_txt.getText().trim();

    String updateInv =
        "UPDATE inventory " +
        "SET quantity_in = quantity_in + ?, " +
        "    current_stock = current_stock + ?, " +
        "    last_updated = NOW() " +
        "WHERE product_id = ?";

    String insertInv =
        "INSERT INTO inventory (product_id, quantity_in, quantity_out, current_stock, last_updated) " +
        "VALUES (?, ?, 0, ?, NOW())";

    String movSql =
        "INSERT INTO inventory_movements (product_id, movement_type, quantity, movement_date, notes) " +
        "VALUES (?, 'IN', ?, ?, ?)";

    String statusSql =
        "UPDATE products p " +
        "JOIN inventory i ON i.product_id = p.product_id " +
        "SET p.status = CASE WHEN i.current_stock > 0 THEN 'Available' ELSE 'Out of Stock' END " +
        "WHERE p.product_id = ?";

    try (Connection con = DBConnection.getConnection()) {
        con.setAutoCommit(false);

        try {
            int rows;

            // 1) Update inventory
            try (PreparedStatement pst = con.prepareStatement(updateInv)) {
                pst.setInt(1, qtyToAdd);
                pst.setInt(2, qtyToAdd);
                pst.setInt(3, selectedProductId);
                rows = pst.executeUpdate();
            }

            // 2) If no row existed, insert inventory row
            if (rows == 0) {
                try (PreparedStatement ins = con.prepareStatement(insertInv)) {
                    ins.setInt(1, selectedProductId);
                    ins.setInt(2, qtyToAdd);
                    ins.setInt(3, qtyToAdd);
                    ins.executeUpdate();
                }
            }

            // 3) Insert into inventory_movements
            try (PreparedStatement mv = con.prepareStatement(movSql)) {
                mv.setInt(1, selectedProductId);
                mv.setInt(2, qtyToAdd);
                mv.setTimestamp(3, receivingDate);
                mv.setString(4, notes);
                mv.executeUpdate();
            }

            // 4) Update product availability
            try (PreparedStatement st = con.prepareStatement(statusSql)) {
                st.setInt(1, selectedProductId);
                st.executeUpdate();
            }

            con.commit();

        } catch (Exception ex) {
            con.rollback();
            throw ex;
        }

        JOptionPane.showMessageDialog(this,
            "✅ Stock received!\nAdded: " + qtyToAdd +
            "\nReceiving Date: " + receivingDate +
            (notes.isEmpty() ? "" : "\nNotes: " + notes)
        );

        loadSelectedProductDetails(selectedProductId);

        if (dashboard != null) {
            dashboard.loadInventoryToTable();
            dashboard.loadProductsToTable();
        }

        quantityAdd_txt.setText("");
        notes_txt.setText("");
        ReceivingDate_txt.setDate(null);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
    }
    }//GEN-LAST:event_receive_btnActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new ReceiveStockScreen().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> Product_txt;
    private com.toedter.calendar.JDateChooser ReceivingDate_txt;
    private javax.swing.JLabel barcode1_txt;
    private javax.swing.JLabel barcode_txt;
    private javax.swing.JButton cancel_btn;
    private javax.swing.JLabel costPrice_txt;
    private javax.swing.JLabel currentStock_lbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea notes_txt;
    private javax.swing.JTextField quantityAdd_txt;
    private javax.swing.JButton receive_btn;
    private javax.swing.JLabel reorderLevel_lbl;
    private javax.swing.JLabel unitPrice_lbl;
    private javax.swing.JTextField unit_txt;
    // End of variables declaration//GEN-END:variables
}
