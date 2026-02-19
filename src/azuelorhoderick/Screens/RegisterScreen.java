/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package azuelorhoderick.Screens;

import azuelorhoderick.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

public class RegisterScreen extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RegisterScreen.class.getName());

  
    public RegisterScreen() {
        
       
        initComponents();
        
   


    }
    
    

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        role_cmb = new javax.swing.JComboBox<>();
        fullname_txt = new javax.swing.JTextField();
        username_txt = new javax.swing.JTextField();
        password_txt = new javax.swing.JPasswordField();
        register_btn = new javax.swing.JButton();
        cancel_btn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Register New User");
        setBackground(new java.awt.Color(0, 0, 255));
        setMaximumSize(new java.awt.Dimension(498, 318));
        setMinimumSize(new java.awt.Dimension(498, 318));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(498, 318));
        jPanel1.setMinimumSize(new java.awt.Dimension(498, 318));
        jPanel1.setPreferredSize(new java.awt.Dimension(498, 318));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Password:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 197, 90, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Role:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 90, 30));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Full name:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 90, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Username:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 147, 90, 20));

        role_cmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Cashier", "Staff" }));
        jPanel1.add(role_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 320, 30));

        fullname_txt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jPanel1.add(fullname_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 320, 30));

        username_txt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jPanel1.add(username_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 140, 320, 30));
        jPanel1.add(password_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 190, 320, 30));

        register_btn.setBackground(new java.awt.Color(51, 0, 255));
        register_btn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        register_btn.setText("Register");
        register_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                register_btnActionPerformed(evt);
            }
        });
        jPanel1.add(register_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 250, 120, 30));

        cancel_btn.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cancel_btn.setText("Cancel");
        cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_btnActionPerformed(evt);
            }
        });
        jPanel1.add(cancel_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 250, 120, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 300));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void register_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_register_btnActionPerformed
        // TODO add your handling code here:
     String role = String.valueOf(role_cmb.getSelectedItem()).trim();
    String fullname = fullname_txt.getText().trim();
    String username = username_txt.getText().trim();
    String password = new String(password_txt.getPassword()).trim();

    // ✅ Validation
    if (role.isEmpty() || role.equals("") || role.equals(" ") ||
        fullname.isEmpty() || username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill in all fields.");
        return;
    }

    // ✅ Check if username already exists
    String checkSql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
    String insertSql = "INSERT INTO users (fullname, role, username, password) VALUES (?, ?, ?, ?)";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement checkPst = con.prepareStatement(checkSql)) {

        checkPst.setString(1, username);

        try (ResultSet rs = checkPst.executeQuery()) {
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose another.");
                username_txt.requestFocus();
                return;
            }
        }

        
        try (PreparedStatement insertPst = con.prepareStatement(insertSql)) {
            insertPst.setString(1, fullname);
            insertPst.setString(2, role);
            insertPst.setString(3, username);
            insertPst.setString(4, password);

            int rows = insertPst.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registered Successfully!");

                // Clear fields after success
                    LoginScreen login = new LoginScreen();
                    login.setVisible(true);
                    this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Please try again.");
            }
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        e.printStackTrace();
    }
    }//GEN-LAST:event_register_btnActionPerformed

    private void cancel_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_btnActionPerformed
        // TODO add your handling code here:
        LoginScreen db = new LoginScreen();
        db.setVisible(true);
        this.dispose(); // closes Register window
    }//GEN-LAST:event_cancel_btnActionPerformed

    
    public static void main(String args[]) {
        
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

       
        java.awt.EventQueue.invokeLater(() -> new RegisterScreen().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel_btn;
    private javax.swing.JTextField fullname_txt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField password_txt;
    private javax.swing.JButton register_btn;
    private javax.swing.JComboBox<String> role_cmb;
    private javax.swing.JTextField username_txt;
    // End of variables declaration//GEN-END:variables
}
