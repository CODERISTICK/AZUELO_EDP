/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

package azuelorhoderick.Screens;


import azuelorhoderick.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class CategoriesScreen extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CategoriesScreen.class.getName());

    private int selectedCategoryId = -1;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public CategoriesScreen() {
        initComponents();
        sdf.setLenient(false);
        initCustomEvents();
        loadCategories();
        clearFieldsWithoutConfirmation();
    }

    private void initCustomEvents() {
        categoriesSearch_txt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchCategories();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchCategories();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchCategories();
            }
        });

        Category_tbl.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRowToFields();
            }
        });
    }

    private String getFormattedDateFromChooser() {
        Date date = DateCreated_txt.getDate();
        if (date == null) {
            return null;
        }
        return sdf.format(date);
    }

    private boolean isValidDateInput() {
        return DateCreated_txt.getDate() != null;
    }

    private void loadSelectedRowToFields() {
        int row = Category_tbl.getSelectedRow();
        if (row == -1) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) Category_tbl.getModel();

        selectedCategoryId = Integer.parseInt(model.getValueAt(row, 0).toString());
        categoryName_txt.setText(model.getValueAt(row, 1).toString());
        description_txt.setText(model.getValueAt(row, 2).toString());

        String dateValue = model.getValueAt(row, 3).toString();
        try {
            Date date = sdf.parse(dateValue);
            DateCreated_txt.setDate(date);
        } catch (ParseException e) {
            DateCreated_txt.setDate(null);
        }
    }

    private void loadCategories() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Category Name", "Description", "Date Created"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String sql = "SELECT category_id, category_name, description, date_created FROM categories ORDER BY category_id ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getString("description"),
                    rs.getString("date_created")
                });
            }

            Category_tbl.setModel(model);

            Category_tbl.getColumnModel().getColumn(0).setMinWidth(0);
            Category_tbl.getColumnModel().getColumn(0).setMaxWidth(0);
            Category_tbl.getColumnModel().getColumn(0).setPreferredWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories:\n" + e.getMessage());
        }
    }

    private void searchCategories() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Category Name", "Description", "Date Created"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String keyword = categoriesSearch_txt.getText().trim().toLowerCase();

        String sql = "SELECT category_id, category_name, description, date_created " +
                     "FROM categories " +
                     "WHERE LOWER(category_name) LIKE ? " +
                     "OR LOWER(date_created) LIKE ? " +
                     "ORDER BY category_id ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            String searchValue = "%" + keyword + "%";
            pst.setString(1, searchValue);
            pst.setString(2, searchValue);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("description"),
                        rs.getString("date_created")
                    });
                }
            }

            Category_tbl.setModel(model);

            Category_tbl.getColumnModel().getColumn(0).setMinWidth(0);
            Category_tbl.getColumnModel().getColumn(0).setMaxWidth(0);
            Category_tbl.getColumnModel().getColumn(0).setPreferredWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching categories:\n" + e.getMessage());
        }
    }

    private boolean validateInputs() {
        String categoryName = categoryName_txt.getText().trim();
        String description = description_txt.getText().trim();

        if (categoryName.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return false;
        }

        if (!isValidDateInput()) {
            JOptionPane.showMessageDialog(this, "Please select a valid date.");
            return false;
        }

        return true;
    }

    private boolean isDuplicateCategory(String categoryName, int excludeCategoryId) {
        String sql = "SELECT category_id FROM categories WHERE LOWER(category_name) = LOWER(?) AND category_id <> ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, categoryName);
            pst.setInt(2, excludeCategoryId);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking duplicate category:\n" + e.getMessage());
            return true;
        }
    }

    private void addCategory() {
        if (!validateInputs()) {
            return;
        }

        String categoryName = categoryName_txt.getText().trim();
        String description = description_txt.getText().trim();
        String dateCreated = getFormattedDateFromChooser();

        if (isDuplicateCategory(categoryName, -1)) {
            JOptionPane.showMessageDialog(this, "Duplicate category name already exists.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to add this category?",
                "Confirm Add",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "INSERT INTO categories (category_name, description, date_created) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, categoryName);
            pst.setString(2, description);
            pst.setString(3, dateCreated);

            int result = pst.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Category added successfully.");
                loadCategories();
                clearFieldsWithoutConfirmation();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add category.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding category:\n" + e.getMessage());
        }
    }

    private void updateCategory() {
        if (selectedCategoryId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category from the table first.");
            return;
        }

        if (!validateInputs()) {
            return;
        }

        String categoryName = categoryName_txt.getText().trim();
        String description = description_txt.getText().trim();
        String dateCreated = getFormattedDateFromChooser();

        if (isDuplicateCategory(categoryName, selectedCategoryId)) {
            JOptionPane.showMessageDialog(this, "Duplicate category name already exists.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to update this category?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "UPDATE categories SET category_name = ?, description = ?, date_created = ? WHERE category_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, categoryName);
            pst.setString(2, description);
            pst.setString(3, dateCreated);
            pst.setInt(4, selectedCategoryId);

            int result = pst.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Category updated successfully.");
                loadCategories();
                clearFieldsWithoutConfirmation();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update category.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating category:\n" + e.getMessage());
        }
    }

    private void deleteCategory() {
        if (selectedCategoryId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category from the table first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this category?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Connection con = null;
        PreparedStatement pstProducts = null;
        PreparedStatement pstCategory = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            String deleteProductsSql = "DELETE FROM products WHERE category_id = ?";
            pstProducts = con.prepareStatement(deleteProductsSql);
            pstProducts.setInt(1, selectedCategoryId);
            pstProducts.executeUpdate();

            String deleteCategorySql = "DELETE FROM categories WHERE category_id = ?";
            pstCategory = con.prepareStatement(deleteCategorySql);
            pstCategory.setInt(1, selectedCategoryId);

            int result = pstCategory.executeUpdate();

            if (result > 0) {
                con.commit();
                JOptionPane.showMessageDialog(this, "Category deleted successfully.");
                loadCategories();
                clearFieldsWithoutConfirmation();
            } else {
                con.rollback();
                JOptionPane.showMessageDialog(this, "Failed to delete category.");
            }

        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Rollback failed:\n" + ex.getMessage());
            }

            JOptionPane.showMessageDialog(this, "Error deleting category:\n" + e.getMessage());

        } finally {
            try {
                if (pstProducts != null) pstProducts.close();
                if (pstCategory != null) pstCategory.close();
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
        categoryName_txt.setText("");
        description_txt.setText("");
        DateCreated_txt.setDate(null);
        Category_tbl.clearSelection();
        selectedCategoryId = -1;
        categoryName_txt.requestFocus();
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        categoriesSearch_txt = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        categoryName_txt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        description_txt = new javax.swing.JTextArea();
        DateCreated_txt = new com.toedter.calendar.JDateChooser();
        jPanel5 = new javax.swing.JPanel();
        delete_btn = new javax.swing.JButton();
        clear_btn = new javax.swing.JButton();
        update_btn = new javax.swing.JButton();
        add_btn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Category_tbl = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(817, 497));
        setMinimumSize(new java.awt.Dimension(817, 497));
        setResizable(false);

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
        jLabel4.setText("CATEGORIES");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, -1, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 50));

        jPanel3.setBackground(new java.awt.Color(245, 247, 251));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/search (1).png"))); // NOI18N
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, -1, 30));

        categoriesSearch_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.add(categoriesSearch_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 470, 30));

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Search:");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 50, 50));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 550, 50));

        jPanel4.setBackground(new java.awt.Color(245, 247, 251));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        categoryName_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.add(categoryName_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 220, 30));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Category Details");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 40));

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Category Name:");
        jPanel4.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, -1));

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Description:");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 90, -1));

        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Date Created:");
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 110, -1));

        description_txt.setBackground(new java.awt.Color(255, 255, 255));
        description_txt.setColumns(20);
        description_txt.setRows(5);
        jScrollPane2.setViewportView(description_txt);

        jPanel4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 220, -1));

        DateCreated_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.add(DateCreated_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 220, 30));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        delete_btn.setBackground(new java.awt.Color(204, 0, 0));
        delete_btn.setForeground(new java.awt.Color(255, 255, 255));
        delete_btn.setText("DELETE");
        delete_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_btnActionPerformed(evt);
            }
        });
        jPanel5.add(delete_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 90, 30));

        clear_btn.setBackground(new java.awt.Color(204, 204, 204));
        clear_btn.setForeground(new java.awt.Color(0, 0, 0));
        clear_btn.setText("CLEAR");
        clear_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear_btnActionPerformed(evt);
            }
        });
        jPanel5.add(clear_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 50, 90, 30));

        update_btn.setBackground(new java.awt.Color(0, 51, 153));
        update_btn.setForeground(new java.awt.Color(255, 255, 255));
        update_btn.setText("UPDATE");
        update_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                update_btnActionPerformed(evt);
            }
        });
        jPanel5.add(update_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 90, 30));

        add_btn.setBackground(new java.awt.Color(0, 204, 51));
        add_btn.setForeground(new java.awt.Color(255, 255, 255));
        add_btn.setText("ADD");
        add_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_btnActionPerformed(evt);
            }
        });
        jPanel5.add(add_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 30));

        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 220, 100));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 60, 240, 410));

        Category_tbl.setBackground(new java.awt.Color(245, 247, 251));
        Category_tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Category Name", "Description", "Date Created"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(Category_tbl);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 550, 350));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 817, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 497, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void clear_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear_btnActionPerformed
        // TODO add your handling code here:
        clearFields();
    }//GEN-LAST:event_clear_btnActionPerformed

    private void add_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_btnActionPerformed
        // TODO add your handling code here:
        addCategory();
    }//GEN-LAST:event_add_btnActionPerformed

    private void update_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_update_btnActionPerformed
        // TODO add your handling code here:
        updateCategory();
    }//GEN-LAST:event_update_btnActionPerformed

    private void delete_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_btnActionPerformed
        // TODO add your handling code here:
        deleteCategory();
    }//GEN-LAST:event_delete_btnActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new CategoriesScreen().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Category_tbl;
    private com.toedter.calendar.JDateChooser DateCreated_txt;
    private javax.swing.JButton add_btn;
    private javax.swing.JTextField categoriesSearch_txt;
    private javax.swing.JTextField categoryName_txt;
    private javax.swing.JButton clear_btn;
    private javax.swing.JButton delete_btn;
    private javax.swing.JTextArea description_txt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton update_btn;
    // End of variables declaration//GEN-END:variables
}
