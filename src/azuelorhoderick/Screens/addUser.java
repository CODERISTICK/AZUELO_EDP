package azuelorhoderick.Screens;

import javax.swing.JOptionPane;

import azuelorhoderick.DBConnection;
import javax.swing.*;
import java.sql.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class addUser extends javax.swing.JFrame {

    private Dashboard dashboard; // reference to dashboard

    // modes
    private enum Mode { ADD, EDIT, CHANGE_PASSWORD }
    private Mode mode = Mode.ADD;

    // selected user id for edit/change pass
    private int userId = -1;

    public addUser(Dashboard dashboard) {
        initComponents();
        this.dashboard = dashboard;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(dashboard);
        setModeAdd();
    }

    // ========= PUBLIC SETTERS FOR DASHBOARD =========
    
    public addUser() {
    initComponents();
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    setModeAdd(); // works even without dashboard
}
    
    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(addUser.class.getName());



    public void setModeAdd() {
    mode = Mode.ADD;
    userId = -1;

    role_cmb.setEnabled(true);
    status_cmb.setEnabled(true);

    firstname_txt.setEditable(true);
    lastname_txt.setEditable(true);
    email_txt.setEditable(true);
    contactNum_txt.setEditable(true);

    username_txt.setEditable(true);
    password_txt.setEditable(true);
    password_txt.setEnabled(true);

    clearFields(); //error
    setDateCreatedNow();

    Register_btn.setLabel("REGISTER");
    setTitle("Add User");
}


    public void setModeEdit(int id, String role, String status, String fullname,
                        String email, String contact, String dateCreated, String username) {

    mode = Mode.EDIT;
    userId = id;

    String[] parts = fullname.trim().split("\\s+", 2);
    String first = parts.length > 0 ? parts[0] : "";
    String last  = parts.length > 1 ? parts[1] : "";

    role_cmb.setSelectedItem(role);
    status_cmb.setSelectedItem(status);

    firstname_txt.setText(first);
    lastname_txt.setText(last);
    email_txt.setText(email);
    contactNum_txt.setText(contact);
    dateCreated_txt.setText(dateCreated);

    username_txt.setText(username);
    password_txt.setText("");

    // fields editable in update:
    role_cmb.setEnabled(true);
    status_cmb.setEnabled(true);
    firstname_txt.setEditable(true);
    lastname_txt.setEditable(true);
    email_txt.setEditable(true);
    contactNum_txt.setEditable(true);

    // username & password NOT editable
    username_txt.setEditable(false);
    username_txt.setEnabled(false);

    password_txt.setEditable(false);
    password_txt.setEnabled(false);

    // date created locked
    dateCreated_txt.setEditable(false);
    dateCreated_txt.setEnabled(false);

    Register_btn.setLabel("UPDATE");
    setTitle("Update User");
}
    
    
    private void clearFields() {
    // reset combo boxes
    if (role_cmb.getItemCount() > 0) role_cmb.setSelectedIndex(0);
    if (status_cmb.getItemCount() > 0) status_cmb.setSelectedIndex(0);

    // clear textfields
    firstname_txt.setText("");
    lastname_txt.setText("");
    email_txt.setText("");
    contactNum_txt.setText("");
    username_txt.setText("");
    password_txt.setText("");

    // clear dateCreated (you will fill it using setDateCreatedNow())
    dateCreated_txt.setText("");

    // re-enable fields (important when coming from EDIT/CHANGE_PASSWORD mode)
    username_txt.setEnabled(true);
    username_txt.setEditable(true);

    password_txt.setEnabled(true);
    password_txt.setEditable(true);

    dateCreated_txt.setEnabled(true);
    dateCreated_txt.setEditable(false); // always readonly
}



    public void setModeChangePassword(int id, String role, String status, String fullname,
                                  String email, String contact, String dateCreated, String username) {

    mode = Mode.CHANGE_PASSWORD;
    userId = id;

    String[] parts = fullname.trim().split("\\s+", 2);
    String first = parts.length > 0 ? parts[0] : "";
    String last  = parts.length > 1 ? parts[1] : "";

    role_cmb.setSelectedItem(role);
    status_cmb.setSelectedItem(status);

    firstname_txt.setText(first);
    lastname_txt.setText(last);
    email_txt.setText(email);
    contactNum_txt.setText(contact);
    dateCreated_txt.setText(dateCreated);

    username_txt.setText(username);
    password_txt.setText("");

    // lock everything except username + password
    role_cmb.setEnabled(false);
    status_cmb.setEnabled(false);

    firstname_txt.setEditable(false);
    lastname_txt.setEditable(false);
    email_txt.setEditable(false);
    contactNum_txt.setEditable(false);

    dateCreated_txt.setEditable(false);
    dateCreated_txt.setEnabled(false);

    // ONLY username & password editable
    username_txt.setEnabled(true);
    username_txt.setEditable(true);

    password_txt.setEnabled(true);
    password_txt.setEditable(true);

    Register_btn.setLabel("CHANGE PASSWORD");
    setTitle("Change Password");
}



    
    private void setDateCreatedNow() {
    // PH time
    ZoneId ph = ZoneId.of("Asia/Manila");

    // Display format in the textfield (example: 11/02/2026 03:25 PM)
    DateTimeFormatter displayFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

    String nowDisplay = LocalDateTime.now(ph).format(displayFmt);
    dateCreated_txt.setText(nowDisplay);

    // user should not edit this
    dateCreated_txt.setEditable(false);
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
        jLabel9 = new javax.swing.JLabel();
        lastname_txt = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        contactNum_txt = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        dateCreated_txt = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(530, 333));
        setMinimumSize(new java.awt.Dimension(530, 333));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(530, 333));
        jPanel1.setMinimumSize(new java.awt.Dimension(530, 333));
        jPanel1.setName(""); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(530, 333));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Password:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 180, 70, 30));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Email:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 50, 30));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Last Name:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 100, 90, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Register User");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 120, 20));

        role_cmb.setBackground(new java.awt.Color(255, 255, 255));
        role_cmb.setForeground(new java.awt.Color(0, 0, 0));
        role_cmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Cashier", "Manager" }));
        jPanel1.add(role_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 180, 30));

        firstname_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(firstname_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 100, 180, 30));

        password_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(password_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 180, 170, 30));

        username_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(username_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 180, 180, 30));

        Register_btn.setBackground(new java.awt.Color(0, 0, 255));
        Register_btn.setLabel("REGISTER");
        Register_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Register_btnActionPerformed(evt);
            }
        });
        jPanel1.add(Register_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 270, 140, 30));

        cancel_btn.setBackground(new java.awt.Color(255, 255, 255));
        cancel_btn.setForeground(new java.awt.Color(0, 0, 0));
        cancel_btn.setLabel("CANCEL");
        cancel_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_btnActionPerformed(evt);
            }
        });
        jPanel1.add(cancel_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 270, 140, 30));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Status:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 60, 50, 30));

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 510, 1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Role:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 40, 30));

        status_cmb.setBackground(new java.awt.Color(255, 255, 255));
        status_cmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Inactive" }));
        jPanel1.add(status_cmb, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, 170, 30));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Date Created:");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 220, -1, 30));

        email_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(email_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 140, 430, 30));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("First Name:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 90, 30));

        lastname_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(lastname_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 100, 170, 30));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Username:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 90, 30));

        contactNum_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(contactNum_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 220, 160, 30));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Contact #:");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 90, 30));

        dateCreated_txt.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.add(dateCreated_txt, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 220, 170, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

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
        String role = String.valueOf(role_cmb.getSelectedItem()).trim();
    String status = String.valueOf(status_cmb.getSelectedItem()).trim();

    // Convert "Not Active" to "Inactive" (to match DB ENUM)
    if (status.equalsIgnoreCase("Not Active")) status = "Inactive";

    String firstName = firstname_txt.getText().trim();
    String lastName  = lastname_txt.getText().trim();
    String email     = email_txt.getText().trim();
    String contact   = contactNum_txt.getText().trim();
    String username  = username_txt.getText().trim();
    String password  = password_txt.getText().trim();

    // DB format for DATETIME (MySQL): YYYY-MM-DD HH:MM:SS
    ZoneId ph = ZoneId.of("Asia/Manila");
    DateTimeFormatter dbFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String nowDb = LocalDateTime.now(ph).format(dbFmt);

    try (Connection con = DBConnection.getConnection()) {

        if (mode == Mode.ADD) {

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in required fields (First, Last, Username, Password).");
                return;
            }

            // check duplicate username
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

            // IMPORTANT: role ENUM is Admin/Cashier/Manager (your DB)
            // your combo has Admin/Cashier/Staff -> change Staff to Manager in UI, or map it:
            if (role.equalsIgnoreCase("Staff")) role = "Manager";

            String insertSql =
                "INSERT INTO users(first_name,last_name,username,password,role,email,contact_number,status,date_created) " +
                "VALUES(?,?,?,?,?,?,?,?,?)";

            try (PreparedStatement pst = con.prepareStatement(insertSql)) {
                pst.setString(1, firstName);
                pst.setString(2, lastName);
                pst.setString(3, username);
                pst.setString(4, password); // (later: hash)
                pst.setString(5, role);
                pst.setString(6, email.isEmpty() ? null : email);
                pst.setString(7, contact.isEmpty() ? null : contact);
                pst.setString(8, status);
                pst.setString(9, nowDb);
                pst.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "User Registered Successfully!");
        }

        else if (mode == Mode.EDIT) {

            if (userId <= 0) return;

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in First, Last, Username.");
                return;
            }

            if (role.equalsIgnoreCase("Staff")) role = "Manager";

            // check duplicate username except current user
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
                    "UPDATE users SET first_name=?, last_name=?, role=?, email=?, contact_number=?, status=? " +
                    "WHERE user_id=?";


            try (PreparedStatement pst = con.prepareStatement(updateSql)) {
                  pst.setString(1, firstName);
                  pst.setString(2, lastName);
                  pst.setString(3, role);
                  pst.setString(4, email.isEmpty() ? null : email);
                  pst.setString(5, contact.isEmpty() ? null : contact);
                  pst.setString(6, status);
                  pst.setInt(7, userId);
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
                 try (PreparedStatement pst = con.prepareStatement(updateSql)) {
                 pst.setString(1, username);
                 pst.setString(2, password);
                 pst.setInt(3, userId);
                 pst.executeUpdate();
}


            JOptionPane.showMessageDialog(this, "Password Updated Successfully!");
        }

        // refresh dashboard table
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField lastname_txt;
    private javax.swing.JTextField password_txt;
    private javax.swing.JComboBox<String> role_cmb;
    private javax.swing.JComboBox<String> status_cmb;
    private javax.swing.JTextField username_txt;
    // End of variables declaration//GEN-END:variables
}
