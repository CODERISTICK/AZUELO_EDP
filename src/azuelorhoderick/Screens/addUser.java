package azuelorhoderick.Screens;

import javax.swing.JOptionPane;
import azuelorhoderick.DBConnection;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.mindrot.jbcrypt.BCrypt;

public class addUser extends javax.swing.JFrame {

    private Dashboard dashboard;

    private enum Mode { ADD, EDIT, CHANGE_PASSWORD }
    private Mode mode = Mode.ADD;

    private int userId = -1;

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(addUser.class.getName());

    public addUser(Dashboard dashboard) {
        initComponents();
        this.dashboard = dashboard;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(dashboard);
        setupContactNumberValidation();
        setModeAdd();
    }

    public addUser() {
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setupContactNumberValidation();
        setModeAdd();
    }

    public void setModeAdd() {
        mode = Mode.ADD;
        userId = -1;

        role_cmb.setEnabled(true);
        status_cmb.setEnabled(true);

        firstname_txt.setEditable(true);
        middlename_txt.setEditable(true);
        lastname_txt.setEditable(true);
        email_txt.setEditable(true);
        contactNum_txt.setEditable(true);

        username_txt.setEditable(true);
        password_txt.setEditable(true);
        password_txt.setEnabled(true);

        clearFields();
        setDateCreatedNow();

        Register_btn.setLabel("REGISTER");
        setTitle("Add User");
    }

    public void setModeEdit(int id, String role, String status,
                            String firstName, String middleName, String lastName,
                            String email, String contact, String dateCreated, String username) {

        mode = Mode.EDIT;
        userId = id;

        role_cmb.setSelectedItem(role);
        status_cmb.setSelectedItem(status);

        firstname_txt.setText(firstName);
        middlename_txt.setText(middleName);
        lastname_txt.setText(lastName);
        email_txt.setText(email);
        contactNum_txt.setText(contact);
        dateCreated_txt.setText(dateCreated);

        username_txt.setText(username);
        password_txt.setText("");

        role_cmb.setEnabled(true);
        status_cmb.setEnabled(true);

        firstname_txt.setEditable(true);
        middlename_txt.setEditable(true);
        lastname_txt.setEditable(true);
        email_txt.setEditable(true);
        contactNum_txt.setEditable(true);

        username_txt.setEditable(false);
        username_txt.setEnabled(false);

        password_txt.setEditable(false);
        password_txt.setEnabled(false);

        dateCreated_txt.setEditable(false);
        dateCreated_txt.setEnabled(false);

        Register_btn.setLabel("UPDATE");
        setTitle("Update User");
    }

    public void setModeChangePassword(int id, String role, String status,
                                      String firstName, String middleName, String lastName,
                                      String email, String contact, String dateCreated, String username) {

        mode = Mode.CHANGE_PASSWORD;
        userId = id;

        role_cmb.setSelectedItem(role);
        status_cmb.setSelectedItem(status);

        firstname_txt.setText(firstName);
        middlename_txt.setText(middleName);
        lastname_txt.setText(lastName);
        email_txt.setText(email);
        contactNum_txt.setText(contact);
        dateCreated_txt.setText(dateCreated);

        username_txt.setText(username);
        password_txt.setText("");

        role_cmb.setEnabled(false);
        status_cmb.setEnabled(false);

        firstname_txt.setEditable(false);
        middlename_txt.setEditable(false);
        lastname_txt.setEditable(false);
        email_txt.setEditable(false);
        contactNum_txt.setEditable(false);

        dateCreated_txt.setEditable(false);
        dateCreated_txt.setEnabled(false);

        username_txt.setEnabled(true);
        username_txt.setEditable(true);

        password_txt.setEnabled(true);
        password_txt.setEditable(true);

        Register_btn.setLabel("CHANGE PASSWORD");
        setTitle("Change Password");
    }

    private void clearFields() {
        if (role_cmb.getItemCount() > 0) role_cmb.setSelectedIndex(0);
        if (status_cmb.getItemCount() > 0) status_cmb.setSelectedIndex(0);

        firstname_txt.setText("");
        middlename_txt.setText("");
        lastname_txt.setText("");
        email_txt.setText("");
        contactNum_txt.setText("");
        username_txt.setText("");
        password_txt.setText("");
        dateCreated_txt.setText("");

        username_txt.setEnabled(true);
        username_txt.setEditable(true);

        password_txt.setEnabled(true);
        password_txt.setEditable(true);

        dateCreated_txt.setEnabled(true);
        dateCreated_txt.setEditable(false);
    }

    private void setDateCreatedNow() {
        ZoneId ph = ZoneId.of("Asia/Manila");
        DateTimeFormatter displayFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        String nowDisplay = LocalDateTime.now(ph).format(displayFmt);
        dateCreated_txt.setText(nowDisplay);
        dateCreated_txt.setEditable(false);
    }

    private void setupContactNumberValidation() {
        contactNum_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                String text = contactNum_txt.getText();

                if (!Character.isDigit(c) || text.length() >= 11) {
                    evt.consume();
                }
            }
        });
    }

    private String normalizeRoleForDB(String roleUI) {
        if (roleUI == null) return "Staff";

        String r = roleUI.trim();

        if (r.equalsIgnoreCase("Inventory Staff")) return "Staff";
        if (r.equalsIgnoreCase("Staff")) return "Staff";
        if (r.equalsIgnoreCase("Admin")) return "Admin";
        if (r.equalsIgnoreCase("Cashier")) return "Cashier";

        return "Staff";
    }

    private String normalizeStatusForDB(String statusUI) {
        if (statusUI == null) return "Active";

        String s = statusUI.trim();

        if (s.equalsIgnoreCase("Not Active")) return "Inactive";
        if (s.equalsIgnoreCase("Inactive")) return "Inactive";
        if (s.equalsIgnoreCase("Active")) return "Active";

        return "Active";
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        role_cmb = new javax.swing.JComboBox<>();
        firstname_txt = new javax.swing.JTextField();
        password_txt = new javax.swing.JTextField();
        username_txt = new javax.swing.JTextField();
        Register_btn = new java.awt.Button();
        cancel_btn = new java.awt.Button();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        status_cmb = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        email_txt = new javax.swing.JTextField();
        lbl = new javax.swing.JLabel();
        lastname_txt = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        contactNum_txt = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        dateCreated_txt = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        middlename_txt = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(607, 360));
        setMinimumSize(new java.awt.Dimension(607, 360));
        setPreferredSize(new java.awt.Dimension(607, 360));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(607, 333));
        jPanel1.setMinimumSize(new java.awt.Dimension(607, 333));
        jPanel1.setName(""); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(607, 333));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Password:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 210, 70, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Email:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 40, 30));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Last Name:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 90, 70, 20));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Register User");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 120, 20));

        role_cmb.setBackground(new java.awt.Color(255, 255, 255));
        role_cmb.setForeground(new java.awt.Color(0, 0, 0));
        role_cmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Cashier", "Inventory Staff" }));
        jPanel1.add(role_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, 180, 30));

        firstname_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(firstname_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 180, 30));

        password_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(password_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 230, 170, 30));

        username_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(username_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 180, 30));

        Register_btn.setBackground(new java.awt.Color(0, 0, 255));
        Register_btn.setLabel("REGISTER");
        Register_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Register_btnActionPerformed(evt);
            }
        });
        jPanel1.add(Register_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 280, 140, 30));

        cancel_btn.setBackground(new java.awt.Color(255, 255, 255));
        cancel_btn.setForeground(new java.awt.Color(0, 0, 0));
        cancel_btn.setLabel("CANCEL");
        cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_btnActionPerformed(evt);
            }
        });
        jPanel1.add(cancel_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 280, 140, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Status:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 50, 50, 30));

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 510, 1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Role:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 40, 30));

        status_cmb.setBackground(new java.awt.Color(255, 255, 255));
        status_cmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Inactive" }));
        jPanel1.add(status_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 50, 170, 30));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Date Created:");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 210, -1, 20));

        email_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(email_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 280, 30));

        lbl.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbl.setForeground(new java.awt.Color(0, 0, 0));
        lbl.setText("Middle Name:");
        jPanel1.add(lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 90, 90, 20));

        lastname_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lastname_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 110, 170, 30));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Username:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 90, 20));

        contactNum_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(contactNum_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 170, 280, 30));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Contact #:");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 150, 90, 20));

        dateCreated_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(dateCreated_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 230, 180, 30));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("First Name:");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 90, 20));

        middlename_txt.setBackground(new java.awt.Color(255, 255, 255));
        middlename_txt.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.add(middlename_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 110, 200, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 607, 360));

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

    private void Register_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Register_btnActionPerformed
        // TODO add your handling code here:
        String roleUI = String.valueOf(role_cmb.getSelectedItem()).trim();
        String statusUI = String.valueOf(status_cmb.getSelectedItem()).trim();

        String role = normalizeRoleForDB(roleUI);
        String status = normalizeStatusForDB(statusUI);

        String firstName = firstname_txt.getText().trim();
        String middleName = middlename_txt.getText().trim();
        String lastName  = lastname_txt.getText().trim();
        String email     = email_txt.getText().trim();
        String contact   = contactNum_txt.getText().trim();
        String username  = username_txt.getText().trim();
        String password  = password_txt.getText().trim();

        ZoneId ph = ZoneId.of("Asia/Manila");
        DateTimeFormatter dbFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowDb = LocalDateTime.now(ph).format(dbFmt);

        try (Connection con = DBConnection.getConnection()) {

            if (mode == Mode.ADD) {

                if (firstName.isEmpty() || middleName.isEmpty() || lastName.isEmpty()
                        || username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please fill in required fields (First, Middle, Last, Username, Password).");
                    return;
                }

                if (!contact.isEmpty() && !contact.matches("^09\\d{9}$")) {
                    JOptionPane.showMessageDialog(this,
                            "Contact number must be a valid 11-digit Philippine mobile number.");
                    return;
                }

                String checkSql = "SELECT 1 FROM users WHERE username=? LIMIT 1";
                try (PreparedStatement pst = con.prepareStatement(checkSql)) {
                    pst.setString(1, username);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(this, "Username already exists.");
                            return;
                        }
                    }
                }

                String insertSql =
                    "INSERT INTO users(first_name,middle_name,last_name,username,password,role,email,contact_number,status,date_created) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?)";

                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                try (PreparedStatement pst = con.prepareStatement(insertSql)) {
                    pst.setString(1, firstName);
                    pst.setString(2, middleName);
                    pst.setString(3, lastName);
                    pst.setString(4, username);
                    pst.setString(5, hashedPassword);
                    pst.setString(6, role);
                    pst.setString(7, email.isEmpty() ? null : email);
                    pst.setString(8, contact.isEmpty() ? null : contact);
                    pst.setString(9, status);
                    pst.setString(10, nowDb);
                    pst.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "User Registered Successfully!");
            }

            else if (mode == Mode.EDIT) {

                if (userId <= 0) return;

                if (firstName.isEmpty() || middleName.isEmpty() || lastName.isEmpty() || username.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in First, Middle, Last, Username.");
                    return;
                }

                if (!contact.isEmpty() && !contact.matches("^09\\d{9}$")) {
                    JOptionPane.showMessageDialog(this,
                            "Contact number must be a valid 11-digit Philippine mobile number.");
                    return;
                }

                String checkSql = "SELECT 1 FROM users WHERE username=? AND user_id<>? LIMIT 1";
                try (PreparedStatement pst = con.prepareStatement(checkSql)) {
                    pst.setString(1, username);
                    pst.setInt(2, userId);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(this, "Username already exists.");
                            return;
                        }
                    }
                }

                String updateSql =
                    "UPDATE users SET first_name=?, middle_name=?, last_name=?, username=?, role=?, email=?, contact_number=?, status=? " +
                    "WHERE user_id=?";

                try (PreparedStatement pst = con.prepareStatement(updateSql)) {
                    pst.setString(1, firstName);
                    pst.setString(2, middleName);
                    pst.setString(3, lastName);
                    pst.setString(4, username);
                    pst.setString(5, role);
                    pst.setString(6, email.isEmpty() ? null : email);
                    pst.setString(7, contact.isEmpty() ? null : contact);
                    pst.setString(8, status);
                    pst.setInt(9, userId);
                    pst.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "User Updated Successfully!");
            }

            else if (mode == Mode.CHANGE_PASSWORD) {

                if (userId <= 0) return;

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter username and new password.");
                    return;
                }

                String updateSql = "UPDATE users SET username=?, password=? WHERE user_id=?";

                String newHashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                try (PreparedStatement pst = con.prepareStatement(updateSql)) {
                    pst.setString(1, username);
                    pst.setString(2, newHashedPassword);
                    pst.setInt(3, userId);
                    pst.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Password Updated Successfully!");
            }

            if (dashboard != null) dashboard.loadUsersToTable();
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
            e.printStackTrace();
        }

    }//GEN-LAST:event_Register_btnActionPerformed

    
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
        java.awt.EventQueue.invokeLater(() -> new addUser().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button Register_btn;
    private java.awt.Button cancel_btn;
    private javax.swing.JTextField contactNum_txt;
    private javax.swing.JTextField dateCreated_txt;
    private javax.swing.JTextField email_txt;
    private javax.swing.JTextField firstname_txt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField lastname_txt;
    private javax.swing.JLabel lbl;
    private javax.swing.JTextField middlename_txt;
    private javax.swing.JTextField password_txt;
    private javax.swing.JComboBox<String> role_cmb;
    private javax.swing.JComboBox<String> status_cmb;
    private javax.swing.JTextField username_txt;
    // End of variables declaration//GEN-END:variables
}
