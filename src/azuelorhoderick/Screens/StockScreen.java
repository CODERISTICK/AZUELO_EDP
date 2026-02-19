/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

package azuelorhoderick.Screens;

import javax.swing.table.DefaultTableModel;


import javax.swing.JOptionPane;


public class StockScreen extends javax.swing.JFrame {

    
    public StockScreen() {
        initComponents();
       
        stock_date.setDate(new java.util.Date());
    }
    
    

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        javax.swing.JTabbedPane jTabbedPane1 = new javax.swing.JTabbedPane();
        product = new javax.swing.JPanel();
        label1 = new java.awt.Label();
        label2 = new java.awt.Label();
        label3 = new java.awt.Label();
        label4 = new java.awt.Label();
        label5 = new java.awt.Label();
        barcode_txt = new javax.swing.JTextField();
        productName_txt = new javax.swing.JTextField();
        category_cmb = new javax.swing.JComboBox<>();
        ProductID_txt = new javax.swing.JTextField();
        supplierName_txt = new javax.swing.JTextField();
        batch = new javax.swing.JPanel();
        label6 = new java.awt.Label();
        label7 = new java.awt.Label();
        label8 = new java.awt.Label();
        label9 = new java.awt.Label();
        label10 = new java.awt.Label();
        label11 = new java.awt.Label();
        label = new java.awt.Label();
        label13 = new java.awt.Label();
        exp_date = new com.toedter.calendar.JDateChooser();
        mfg_date = new com.toedter.calendar.JDateChooser();
        quantity_txt = new javax.swing.JTextField();
        stockStat_cmb = new javax.swing.JComboBox<>();
        BatchNum_txt = new javax.swing.JTextField();
        unit_txt = new javax.swing.JTextField();
        selling_txt = new javax.swing.JTextField();
        storagae_txt = new javax.swing.JTextField();
        audit = new javax.swing.JPanel();
        label12 = new java.awt.Label();
        label14 = new java.awt.Label();
        label15 = new java.awt.Label();
        stockedBy_txt = new javax.swing.JTextField();
        textArea_txt = new javax.swing.JScrollPane();
        txtArea_txt = new javax.swing.JTextArea();
        save_btn = new javax.swing.JButton();
        Clear_btn = new javax.swing.JButton();
        back_btn = new javax.swing.JButton();
        view_btn = new javax.swing.JButton();
        stock_date = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(565, 385));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(153, 255, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(565, 385));
        jPanel1.setMinimumSize(new java.awt.Dimension(565, 385));
        jPanel1.setPreferredSize(new java.awt.Dimension(565, 385));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane1.setBackground(new java.awt.Color(0, 204, 255));

        product.setBackground(new java.awt.Color(51, 255, 255));
        product.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        label1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label1.setText("Supplier Name:");
        product.add(label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 130, 110, 30));

        label2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label2.setText("Barcode:");
        product.add(label2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 70, 30));

        label3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label3.setText("Product ID:");
        product.add(label3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, 30));

        label4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label4.setText("Product Name:");
        product.add(label4, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 50, 110, 30));

        label5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label5.setText("Category:");
        product.add(label5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 191, 90, 30));

        barcode_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barcode_txtActionPerformed(evt);
            }
        });
        product.add(barcode_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, 140, 30));
        product.add(productName_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 50, 160, 30));

        category_cmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        product.add(category_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 190, 140, 30));
        product.add(ProductID_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 130, 140, 30));
        product.add(supplierName_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 130, 160, 30));

        jTabbedPane1.addTab("Product Information", product);

        batch.setBackground(new java.awt.Color(51, 255, 255));
        batch.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        label6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label6.setText("Stock Status:");
        batch.add(label6, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 210, 100, 30));

        label7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label7.setText("Storage Loc:");
        batch.add(label7, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 160, 90, 30));

        label8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label8.setText("Batch Num:");
        batch.add(label8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 90, 30));

        label9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label9.setText("Mfg Date:");
        batch.add(label9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 90, 30));

        label10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label10.setText("Exp Date:");
        batch.add(label10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 90, 30));

        label11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label11.setText("Quantity Added:");
        batch.add(label11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 120, 30));

        label.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label.setText("Unit Cost:");
        batch.add(label, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 60, 90, 30));

        label13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label13.setText("Selling Price:");
        batch.add(label13, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 100, 90, 50));
        batch.add(exp_date, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 160, 150, 30));
        batch.add(mfg_date, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 110, 150, 30));
        batch.add(quantity_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 210, 150, 30));

        stockStat_cmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Available", "Hold", "Expired", " " }));
        batch.add(stockStat_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 210, 150, 30));
        batch.add(BatchNum_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60, 150, 30));
        batch.add(unit_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 60, 150, 30));
        batch.add(selling_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 110, 150, 30));
        batch.add(storagae_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 160, 150, 30));

        jTabbedPane1.addTab("Batch & Stock Detail", batch);

        audit.setBackground(new java.awt.Color(51, 255, 255));
        audit.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        label12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label12.setText("Remarks:");
        audit.add(label12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 100, 30));

        label14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label14.setText("Date Stocked:");
        audit.add(label14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 100, 30));

        label15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label15.setText("Stocked By:");
        audit.add(label15, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 100, 30));
        audit.add(stockedBy_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 140, 30));

        txtArea_txt.setColumns(20);
        txtArea_txt.setRows(5);
        textArea_txt.setViewportView(txtArea_txt);

        audit.add(textArea_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 160, 270, 120));

        save_btn.setBackground(new java.awt.Color(0, 204, 204));
        save_btn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        save_btn.setText("SAVE");
        save_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_btnActionPerformed(evt);
            }
        });
        audit.add(save_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 160, 100, 30));

        Clear_btn.setBackground(new java.awt.Color(0, 204, 204));
        Clear_btn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Clear_btn.setText("CLEAR");
        Clear_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Clear_btnActionPerformed(evt);
            }
        });
        audit.add(Clear_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 200, 100, 30));

        back_btn.setBackground(new java.awt.Color(0, 204, 204));
        back_btn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        back_btn.setText("EXIT");
        back_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                back_btnActionPerformed(evt);
            }
        });
        audit.add(back_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 240, 100, 30));

        view_btn.setBackground(new java.awt.Color(0, 204, 204));
        view_btn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        view_btn.setText("VIEW LIST");
        view_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view_btnActionPerformed(evt);
            }
        });
        audit.add(view_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 280, 100, 30));
        audit.add(stock_date, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 140, 30));

        jTabbedPane1.addTab("System & Audit Information", audit);

        jPanel1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 580, 390));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 0, 580, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void barcode_txtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barcode_txtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_barcode_txtActionPerformed

    private void save_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_btnActionPerformed
        // TODO add your handling code here:
        // Read values
    String barcode = barcode_txt.getText();
    String productID = ProductID_txt.getText();
    String category = category_cmb.getSelectedItem().toString();
    String productName = productName_txt.getText();
    String supplier = supplierName_txt.getText();

    String batchNum = BatchNum_txt.getText();
    String mfgDate = (mfg_date.getDate() != null) ? mfg_date.getDate().toString() : "N/A";
    String expDate = (exp_date.getDate() != null) ? exp_date.getDate().toString() : "N/A";
    String quantity = quantity_txt.getText();
    String unitCost = unit_txt.getText();
    String sellingPrice = selling_txt.getText();
    String storage = storagae_txt.getText();
    String stockStatus = stockStat_cmb.getSelectedItem().toString();

    String stockDate = (stock_date.getDate() != null) ? stock_date.getDate().toString() : "N/A";
    String stockedBy = stockedBy_txt.getText();
    String remarks = txtArea_txt.getText();

    // Format summary
    String summary =
        "PRODUCT INFORMATION\n" +
        "---------------------------\n" +
        "Barcode: " + barcode + "\n" +
        "Product ID: " + productID + "\n" +
        "Product Name: " + productName + "\n" +
        "Category: " + category + "\n" +
        "Supplier: " + supplier + "\n\n" +

        "BATCH & STOCK DETAILS\n" +
        "---------------------------\n" +
        "Batch No: " + batchNum + "\n" +
        "MFG Date: " + mfgDate + "\n" +
        "EXP Date: " + expDate + "\n" +
        "Quantity: " + quantity + "\n" +
        "Unit Cost: " + unitCost + "\n" +
        "Selling Price: " + sellingPrice + "\n" +
        "Storage: " + storage + "\n" +
        "Stock Status: " + stockStatus + "\n\n" +

        "AUDIT INFO\n" +
        "---------------------------\n" +
        "Date Stocked: " + stockDate + "\n" +
        "Stocked By: " + stockedBy + "\n" +
        "Remarks: " + remarks;

    JOptionPane.showMessageDialog(
        this,
        summary,
        "Stock Saved Successfully",
        JOptionPane.INFORMATION_MESSAGE
    );
    
    
    DefaultTableModel model = StockTableModel.model;

model.addRow(new Object[]{
    barcode_txt.getText(),
    ProductID_txt.getText(),
    productName_txt.getText(),
    category_cmb.getSelectedItem(),
    supplierName_txt.getText(),

    BatchNum_txt.getText(),
    mfg_date.getDate(),
    exp_date.getDate(),
    quantity_txt.getText(),
    unit_txt.getText(),
    selling_txt.getText(),
    storagae_txt.getText(),
    stockStat_cmb.getSelectedItem(),

    stock_date.getDate(),
    stockedBy_txt.getText(),
    txtArea_txt.getText()
});

    new StockList().setVisible(true);

    }//GEN-LAST:event_save_btnActionPerformed

    private void Clear_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Clear_btnActionPerformed
        // TODO add your handling code here:
        int choice = JOptionPane.showConfirmDialog(
        this,
        "Do you want to clear all fields?",
        "Confirm Clear",
        JOptionPane.YES_NO_OPTION
    );

    if (choice == JOptionPane.YES_OPTION) {
        clearFields();
    }
    }//GEN-LAST:event_Clear_btnActionPerformed

    private void back_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_back_btnActionPerformed
        // TODO add your handling code here:
        int choice = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to go back to Dashboard?",
        "Confirm Exit",
        JOptionPane.YES_NO_OPTION
    );

    if (choice == JOptionPane.YES_OPTION) {
        new Dashboard().setVisible(true);
        this.dispose();
    }
    }//GEN-LAST:event_back_btnActionPerformed

    private void view_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_view_btnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_view_btnActionPerformed

    private void clearFields() {

    // Product tab
    barcode_txt.setText("");
    ProductID_txt.setText("");
    productName_txt.setText("");
    supplierName_txt.setText("");
    category_cmb.setSelectedIndex(0);

    // Batch tab
    BatchNum_txt.setText("");
    quantity_txt.setText("");
    unit_txt.setText("");
    selling_txt.setText("");
    storagae_txt.setText("");
    stockStat_cmb.setSelectedIndex(0);
    mfg_date.setDate(null);
    exp_date.setDate(null);

    // Audit tab
    stockedBy_txt.setText("");
    txtArea_txt.setText("");
    stock_date.setDate(new java.util.Date()); // reset to today
}

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
            java.util.logging.Logger.getLogger(StockScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StockScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StockScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StockScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StockScreen().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField BatchNum_txt;
    private javax.swing.JButton Clear_btn;
    private javax.swing.JTextField ProductID_txt;
    private javax.swing.JPanel audit;
    private javax.swing.JButton back_btn;
    private javax.swing.JTextField barcode_txt;
    private javax.swing.JPanel batch;
    private javax.swing.JComboBox<String> category_cmb;
    private com.toedter.calendar.JDateChooser exp_date;
    private javax.swing.JPanel jPanel1;
    private java.awt.Label label;
    private java.awt.Label label1;
    private java.awt.Label label10;
    private java.awt.Label label11;
    private java.awt.Label label12;
    private java.awt.Label label13;
    private java.awt.Label label14;
    private java.awt.Label label15;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private java.awt.Label label4;
    private java.awt.Label label5;
    private java.awt.Label label6;
    private java.awt.Label label7;
    private java.awt.Label label8;
    private java.awt.Label label9;
    private com.toedter.calendar.JDateChooser mfg_date;
    private javax.swing.JPanel product;
    private javax.swing.JTextField productName_txt;
    private javax.swing.JTextField quantity_txt;
    private javax.swing.JButton save_btn;
    private javax.swing.JTextField selling_txt;
    private javax.swing.JComboBox<String> stockStat_cmb;
    private com.toedter.calendar.JDateChooser stock_date;
    private javax.swing.JTextField stockedBy_txt;
    private javax.swing.JTextField storagae_txt;
    private javax.swing.JTextField supplierName_txt;
    private javax.swing.JScrollPane textArea_txt;
    private javax.swing.JTextArea txtArea_txt;
    private javax.swing.JTextField unit_txt;
    private javax.swing.JButton view_btn;
    // End of variables declaration//GEN-END:variables
}
