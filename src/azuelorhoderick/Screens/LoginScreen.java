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

public class LoginScreen extends javax.swing.JFrame {

    
    public LoginScreen() {
        initComponents();
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        Password = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        Username = new javax.swing.JTextField();
        radioRememberMe = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        btn_Login = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtForgotPass = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        txt_register = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LOGIN PAGE");
        setBackground(new java.awt.Color(102, 255, 255));
        setPreferredSize(new java.awt.Dimension(354, 390));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Welcome To NDMU");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 180, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/profile.png"))); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 90, 30, 50));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/azuelorhoderick/azueloIcons/padlock.png"))); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 180, -1, -1));
        getContentPane().add(Password, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 170, 260, 32));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Don't have an account?");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 290, 160, -1));

        Username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UsernameActionPerformed(evt);
            }
        });
        getContentPane().add(Username, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 260, 32));

        radioRememberMe.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        radioRememberMe.setText("Remember me");
        getContentPane().add(radioRememberMe, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Password");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, 89, -1));

        btn_Login.setBackground(new java.awt.Color(0, 0, 255));
        btn_Login.setText("LOGIN");
        btn_Login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LoginActionPerformed(evt);
            }
        });
        getContentPane().add(btn_Login, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 240, 260, 40));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Username");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 89, -1));

        txtForgotPass.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtForgotPass.setForeground(new java.awt.Color(0, 0, 255));
        txtForgotPass.setText("Forgot password?");
        getContentPane().add(txtForgotPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 210, 110, -1));

        jPanel1.setBackground(new java.awt.Color(51, 153, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(354, 390));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_register.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txt_register.setForeground(new java.awt.Color(0, 0, 255));
        txt_register.setText("Register!!");
        txt_register.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txt_registerMouseClicked(evt);
            }
        });
        jPanel1.add(txt_register, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 290, 60, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 350, 350));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_LoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_LoginActionPerformed
         // TODO add your handling code here:
          String username = Username.getText().trim();
    String password = Password.getText().trim();

    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill in all fields.");
        return;
    }

    String sql = "SELECT CONCAT(first_name, ' ', last_name) AS fullname, role " +
                 "FROM users WHERE username = ? AND password = ? AND status='Active' LIMIT 1";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, username);
        pst.setString(2, password);

        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                String fullName = rs.getString("fullname");
                String role = rs.getString("role");

                JOptionPane.showMessageDialog(this, "Login Successful!");

                Dashboard db = new Dashboard(fullName, role);
                db.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password!");
                Password.setText("");
                Password.requestFocus();
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Database Connection Error: " + e.getMessage());
        e.printStackTrace();
    }
    }//GEN-LAST:event_btn_LoginActionPerformed

    private void UsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_UsernameActionPerformed

    private void txt_registerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_registerMouseClicked
        // TODO add your handling code here:
        RegisterScreen db = new RegisterScreen();
        db.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_txt_registerMouseClicked

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
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginScreen().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Password;
    private javax.swing.JTextField Username;
    private javax.swing.JButton btn_Login;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton radioRememberMe;
    private javax.swing.JLabel txtForgotPass;
    private javax.swing.JLabel txt_register;
    // End of variables declaration//GEN-END:variables
}
