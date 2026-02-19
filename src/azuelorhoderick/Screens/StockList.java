/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package azuelorhoderick.Screens;


public class StockList extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(StockList.class.getName());

   
    public StockList() {
        initComponents();
        
        listStocking_tbl.setModel(StockTableModel.model);
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listStocking_tbl = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(962, 457));
        setMinimumSize(new java.awt.Dimension(962, 457));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 255, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(962, 457));
        jPanel1.setMinimumSize(new java.awt.Dimension(962, 457));
        jPanel1.setPreferredSize(new java.awt.Dimension(763, 457));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listStocking_tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Barcode", "Product ID", "Product Name", "Category", "Supplier", "Batch No", "MFG Date", "EXP Date", "Quantity", "Unit Cost", "Selling Price", "Storage Location", "Stock Status", "Date Stocked", "Stocked By", "Remarks"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(listStocking_tbl);
        if (listStocking_tbl.getColumnModel().getColumnCount() > 0) {
            listStocking_tbl.getColumnModel().getColumn(0).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(1).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(2).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(3).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(4).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(5).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(6).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(7).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(8).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(9).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(10).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(11).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(12).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(13).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(14).setResizable(false);
            listStocking_tbl.getColumnModel().getColumn(15).setResizable(false);
        }

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 960, 350));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 962, 457));
        jPanel1.getAccessibleContext().setAccessibleName("");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    
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
        java.awt.EventQueue.invokeLater(() -> new StockList().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable listStocking_tbl;
    // End of variables declaration//GEN-END:variables
}
