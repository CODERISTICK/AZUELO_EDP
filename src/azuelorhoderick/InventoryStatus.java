/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package azuelorhoderick;

import azuelorhoderick.DBConnection;
import azuelorhoderick.Screens.Dashboard;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.Desktop;
import java.text.SimpleDateFormat;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;






public class InventoryStatus {

    private final Dashboard dashboard;

    // Dashboard components
    private JComboBox<String> inventoryCategory_cmb;
    private JComboBox<String> InventoryAllProduct_cmb;
    private JTextField InventoryStatSearch_txt;

    private JButton inventoryRefresh_btn;
    private JButton inventoryExportCSV_btn;
    private JButton inventoryPdf_btn;

    private JTable inventoryStatus_tbl;

    public InventoryStatus(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public void init() {
        // get UI refs via getters
        inventoryCategory_cmb = dashboard.getInventoryCategoryCmb();
        InventoryAllProduct_cmb = dashboard.getInventoryAllProductCmb();
        InventoryStatSearch_txt = dashboard.getInventorySearchTxt();

        inventoryRefresh_btn = dashboard.getInventoryRefreshBtn();
        inventoryExportCSV_btn = dashboard.getInventoryExportCsvBtn();
        inventoryPdf_btn = dashboard.getInventoryPdfBtn();

        inventoryStatus_tbl = dashboard.getInventoryStatusTbl();

        // setup
        setupTableModelIfNeeded();
        loadCategoryCombo();
        loadProductCombo();
        loadInventoryStatusTable(); // initial load

        // listeners
        inventoryCategory_cmb.addActionListener(e -> loadInventoryStatusTable());
        InventoryAllProduct_cmb.addActionListener(e -> loadInventoryStatusTable());

        InventoryStatSearch_txt.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadInventoryStatusTable(); }
            public void removeUpdate(DocumentEvent e) { loadInventoryStatusTable(); }
            public void changedUpdate(DocumentEvent e) { loadInventoryStatusTable(); }
        });

        inventoryRefresh_btn.addActionListener(e -> {
            // best refresh logic: reload combos + table (so if new products/categories added it updates)
            loadCategoryCombo();
            loadProductCombo();
            InventoryStatSearch_txt.setText("");
            loadInventoryStatusTable();
        });

        inventoryExportCSV_btn.addActionListener(e -> exportTableToCSV(inventoryStatus_tbl));

        inventoryPdf_btn.addActionListener(e -> exportTableToPDF(inventoryStatus_tbl));


    }

    private void setupTableModelIfNeeded() {
        // Make sure your JTable has the correct columns
        if (inventoryStatus_tbl.getModel() == null || inventoryStatus_tbl.getColumnCount() == 0) {
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"Product", "Category", "Current Stock", "Reorder Level", "Status", "Last Updated"}, 0
            ) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            inventoryStatus_tbl.setModel(model);
        }
    }

    private void loadCategoryCombo() {
        inventoryCategory_cmb.removeAllItems();
        inventoryCategory_cmb.addItem("All Categories");

        String sql = "SELECT category_name FROM categories ORDER BY category_name ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                inventoryCategory_cmb.addItem(rs.getString("category_name"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(dashboard, "Error loading categories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadProductCombo() {
    InventoryAllProduct_cmb.removeAllItems();

    InventoryAllProduct_cmb.addItem("All Products");
    InventoryAllProduct_cmb.addItem("IN STOCK");
    InventoryAllProduct_cmb.addItem("LOW STOCK");
    InventoryAllProduct_cmb.addItem("OUT OF STOCK");
}

    public void loadInventoryStatusTable() {
        DefaultTableModel model = (DefaultTableModel) inventoryStatus_tbl.getModel();
        model.setRowCount(0);

        String selectedCategory = String.valueOf(inventoryCategory_cmb.getSelectedItem());
        String selectedProduct = String.valueOf(InventoryAllProduct_cmb.getSelectedItem());
        String keyword = InventoryStatSearch_txt.getText().trim();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("  p.product_name AS product, ")
           .append("  c.category_name AS category, ")
           .append("  COALESCE(i.current_stock,0) AS current_stock, ")
           .append("  p.reorder_level AS reorder_level, ")
           .append("  CASE ")
           .append("    WHEN COALESCE(i.current_stock,0) = 0 THEN 'OUT OF STOCK' ")
           .append("    WHEN COALESCE(i.current_stock,0) <= p.reorder_level THEN 'LOW STOCK' ")
           .append("    ELSE 'IN STOCK' ")
           .append("  END AS status, ")
           .append("  i.last_updated AS last_updated ")
           .append("FROM products p ")
           .append("JOIN categories c ON p.category_id = c.category_id ")
           .append("LEFT JOIN inventory i ON i.product_id = p.product_id ")
           .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // Filter by category
        if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
            sql.append(" AND c.category_name = ? ");
            params.add(selectedCategory);
        }

        // Filter by status (using the same CASE logic)
         if (selectedProduct != null && !selectedProduct.equals("All Products")) {

         if (selectedProduct.equals("OUT OF STOCK")) {
             sql.append(" AND COALESCE(i.current_stock,0) = 0 ");
    } else if (selectedProduct.equals("LOW STOCK")) {
             sql.append(" AND COALESCE(i.current_stock,0) > 0 ");
             sql.append(" AND COALESCE(i.current_stock,0) <= p.reorder_level ");
    } else if (selectedProduct.equals("IN STOCK")) {
             sql.append(" AND COALESCE(i.current_stock,0) > p.reorder_level ");
    }
}

        // Live search
        if (!keyword.isEmpty()) {
            sql.append(" AND (p.product_name LIKE ? OR p.barcode LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        sql.append(" ORDER BY p.product_name ASC ");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("product"),
                            rs.getString("category"),
                            rs.getInt("current_stock"),
                            rs.getInt("reorder_level"),
                            rs.getString("status"),
                            rs.getTimestamp("last_updated")
                    });
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(dashboard, "Error loading Inventory Status Report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== Export CSV (works without extra libraries) =====
    private void exportTableToCSV(JTable table) {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(dashboard, "No data to export.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save CSV");
        chooser.setSelectedFile(new File("inventory_status_report.csv"));

        int result = chooser.showSaveDialog(dashboard);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(writer)) {

            // header
            for (int c = 0; c < table.getColumnCount(); c++) {
                bw.write(csvEscape(table.getColumnName(c)));
                if (c < table.getColumnCount() - 1) bw.write(",");
            }
            bw.newLine();

            // rows
            for (int r = 0; r < table.getRowCount(); r++) {
                for (int c = 0; c < table.getColumnCount(); c++) {
                    Object val = table.getValueAt(r, c);
                    bw.write(csvEscape(val == null ? "" : String.valueOf(val)));
                    if (c < table.getColumnCount() - 1) bw.write(",");
                }
                bw.newLine();
            }

            JOptionPane.showMessageDialog(dashboard, "✅ CSV exported:\n" + file.getAbsolutePath());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(dashboard, "CSV Export Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String csvEscape(String s) {
        // Escape CSV safely: wrap in quotes if contains comma/quote/newline
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }
    
   private void exportTableToPDF(JTable table) {
    if (table.getRowCount() == 0) {
        JOptionPane.showMessageDialog(dashboard, "No data to export.");
        return;
    }

    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Save PDF");
    chooser.setSelectedFile(new File("inventory_status_report.pdf"));

    int result = chooser.showSaveDialog(dashboard);
    if (result != JFileChooser.APPROVE_OPTION) return;

    File file = chooser.getSelectedFile();
    if (!file.getName().toLowerCase().endsWith(".pdf")) {
        file = new File(file.getAbsolutePath() + ".pdf");
    }

    String generatedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

    try (PDDocument doc = new PDDocument()) {

        PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);


        float fontSize = 10f;
        float titleSize = 16f;

        PDPage page = new PDPage(PDRectangle.LETTER);
        doc.addPage(page);

        PDRectangle mediaBox = page.getMediaBox();
        float margin = 40f;
        float yStart = mediaBox.getHeight() - margin;

        float rowHeight = 18f;
        float tableWidth = mediaBox.getWidth() - (2 * margin);
        int cols = table.getColumnCount();

        // simple equal column width
        float colWidth = tableWidth / cols;

        PDPageContentStream cs = new PDPageContentStream(doc, page);

        // ===== Title =====
        cs.beginText();
        cs.setFont(fontBold, titleSize);
        cs.newLineAtOffset(margin, yStart);
        cs.showText("Inventory Status Report");
        cs.endText();

        yStart -= 22;

        // ===== Subtitle / filters =====
        String cat = String.valueOf(inventoryCategory_cmb.getSelectedItem());
        String prod = String.valueOf(InventoryAllProduct_cmb.getSelectedItem());
        String search = InventoryStatSearch_txt.getText().trim();

        String filterLine = "Category: " + cat + "   |   Status: " + prod + "   |   Search: " + (search.isEmpty() ? "-" : search);

        cs.beginText();
        cs.setFont(font, 10);
        cs.newLineAtOffset(margin, yStart);
        cs.showText(filterLine.length() > 120 ? filterLine.substring(0, 120) + "..." : filterLine);
        cs.endText();

        yStart -= 14;

        cs.beginText();
        cs.setFont(font, 9);
        cs.newLineAtOffset(margin, yStart);
        cs.showText("Generated: " + generatedAt);
        cs.endText();

        yStart -= 22;

        float y = yStart;

        // ===== Header Row =====
        y = drawRow(doc, cs, table, true, margin, y, colWidth, rowHeight, fontBold, fontSize);

        // ===== Data Rows =====
        for (int r = 0; r < table.getRowCount(); r++) {

            // new page if needed
            if (y - rowHeight < margin) {
                cs.close();

                page = new PDPage(PDRectangle.LETTER);
                doc.addPage(page);
                cs = new PDPageContentStream(doc, page);

                y = page.getMediaBox().getHeight() - margin;

                // repeat title on next page small
                cs.beginText();
                cs.setFont(fontBold, 12);
                cs.newLineAtOffset(margin, y);
                cs.showText("Inventory Status Report (continued)");
                cs.endText();

                y -= 18;

                // repeat header
                y = drawRow(doc, cs, table, true, margin, y, colWidth, rowHeight, fontBold, fontSize);
            }

            y = drawDataRow(cs, table, r, margin, y, colWidth, rowHeight, font, fontSize);
        }

        cs.close();
        doc.save(file);

        JOptionPane.showMessageDialog(dashboard, "✅ PDF exported:\n" + file.getAbsolutePath());

        // optional: auto open
        try {
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(file);
        } catch (Exception ignore) {}

    } catch (Exception e) {
        JOptionPane.showMessageDialog(dashboard, "PDF Export Error: " + e.getMessage());
        e.printStackTrace();
    }
}

private float drawRow(PDDocument doc, PDPageContentStream cs, JTable table, boolean header,
                      float x, float y, float colWidth, float rowHeight,
                      PDFont font, float fontSize) throws IOException {

    // draw header texts only (simple)
    for (int c = 0; c < table.getColumnCount(); c++) {
        String text = table.getColumnName(c);
        float textX = x + (c * colWidth) + 2;
        float textY = y - 12;

        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(textX, textY);
        cs.showText(safeText(text, 25));
        cs.endText();
    }
    return y - rowHeight;
}

private float drawDataRow(PDPageContentStream cs, JTable table, int row,
                          float x, float y, float colWidth, float rowHeight,
                          PDFont font, float fontSize) throws IOException {

    for (int c = 0; c < table.getColumnCount(); c++) {
        Object val = table.getValueAt(row, c);
        String text = (val == null) ? "" : String.valueOf(val);

        float textX = x + (c * colWidth) + 2;
        float textY = y - 12;

        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(textX, textY);
        cs.showText(safeText(text, 25));
        cs.endText();
    }
    return y - rowHeight;
}

private String safeText(String s, int maxLen) {
    if (s == null) return "";
    // PDFBox can't show some unicode with default font; keep safe
    s = s.replace("\n", " ").replace("\r", " ");
    if (s.length() > maxLen) return s.substring(0, maxLen - 3) + "...";
    return s;
}

}



