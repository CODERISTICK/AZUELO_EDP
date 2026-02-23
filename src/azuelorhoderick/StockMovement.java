package azuelorhoderick;

import azuelorhoderick.Screens.Dashboard;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

import java.awt.Desktop;
import java.io.*;
import java.io.IOException;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.PDPageContentStream;



public class StockMovement {

    private final Dashboard dashboard;

    // Dashboard components (variable names from your UI)
    private JButton refresh_btn5;
    private JButton MovementExportCsv_btn;
    private JButton pdfMovement_btn;
    private JComboBox<String> movementType_txt;
    private JTextField searchMovement_btn;
    private JComboBox<String> categoryMovement_cmb;
    private JTable stockMovement_tbl;

    private DefaultTableModel movementModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private boolean loadingCategories = false;

    public StockMovement(Dashboard dashboard) {
        this.dashboard = dashboard;
    }
    
    public void refresh() {
    loadCategoryCombo();
    loadMovementTable(false);
}

    public void init() {
        // ===== bind from Dashboard getters =====
        refresh_btn5          = dashboard.getRefresh_btn5();
        MovementExportCsv_btn = dashboard.getMovementExportCsv_btn();
        pdfMovement_btn       = dashboard.getPdfMovement_btn();
        movementType_txt      = dashboard.getMovementType_txt();
        searchMovement_btn    = dashboard.getSearchMovement_btn();
        categoryMovement_cmb  = dashboard.getCategoryMovement_cmb();
        stockMovement_tbl     = dashboard.getStockMovement_tbl();

        setupTable();
        setupSearchFilter();
        setupActions();

        loadCategoryCombo();
        loadMovementTable(true);
    }

    // =========================
    // TABLE SETUP
    // =========================
    private void setupTable() {
        movementModel = new DefaultTableModel(
                new Object[]{"Date", "Product", "Type", "Quantity", "Notes"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        stockMovement_tbl.setModel(movementModel);
        stockMovement_tbl.setRowHeight(28);

        sorter = new TableRowSorter<>(movementModel);
        stockMovement_tbl.setRowSorter(sorter);
    }

    // =========================
    // LIVE SEARCH (filters table as you type)
    // =========================
    private void setupSearchFilter() {
        searchMovement_btn.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });
    }

    private void applyFilter() {
        String text = searchMovement_btn.getText();
        if (text == null) text = "";
        text = text.trim();

        if (text.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + PatternSafe.regex(text)));
    }

    // =========================
    // ACTIONS
    // =========================
   private void setupActions() {

    refresh_btn5.addActionListener(e -> {
        // ✅ reset filters
        searchMovement_btn.setText("");

        // movement type -> All
        if (movementType_txt.getItemCount() > 0) {
            movementType_txt.setSelectedItem("All");
        }


        // category -> All Categories
        if (categoryMovement_cmb.getItemCount() > 0) {
            categoryMovement_cmb.setSelectedItem("All Categories");
        }

        // reload lists + data
        loadCategoryCombo();
        loadMovementTable(false); // no popup on refresh
    });

    movementType_txt.addActionListener(e -> loadMovementTable(false));

    categoryMovement_cmb.addActionListener(e -> {
        if (!loadingCategories) loadMovementTable(false);
    });

    MovementExportCsv_btn.addActionListener(e -> exportTableToCSV(stockMovement_tbl));
    pdfMovement_btn.addActionListener(e -> exportTableToPDF(stockMovement_tbl));
}

    // =========================
    // LOAD CATEGORY COMBO
    // =========================
    private void loadCategoryCombo() {
        loadingCategories = true;
        try {
            categoryMovement_cmb.removeAllItems();
            categoryMovement_cmb.addItem("All Categories");

            String sql = "SELECT category_name FROM categories ORDER BY category_name ASC";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    categoryMovement_cmb.addItem(rs.getString("category_name"));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dashboard, "Error loading categories: " + e.getMessage());
            e.printStackTrace();
        } finally {
            loadingCategories = false;
        }
    }

    // =========================
    // LOAD MOVEMENTS TABLE (from inventory_movements)
    // =========================
    private void loadMovementTable(boolean showDialogIfEmpty) {
        movementModel.setRowCount(0);

        String selectedCategory = String.valueOf(categoryMovement_cmb.getSelectedItem());
        boolean isAllCat = isAllSelection(selectedCategory);

        String movementType = String.valueOf(movementType_txt.getSelectedItem());
        boolean isAllType = (movementType == null
           || movementType.trim().isEmpty()
           || movementType.equalsIgnoreCase("All"));

        

        // Join to products + categories so we can filter and display product name
        String sql =
        "SELECT im.movement_date, p.product_name, im.movement_type, im.quantity, im.notes, c.category_name " +
        "FROM inventory_movements im " +
        "JOIN products p ON p.product_id = im.product_id " +
        "JOIN categories c ON c.category_id = p.category_id " +
        "WHERE 1=1 " +
        (!isAllType ? " AND im.movement_type = ? " : "") +
        (!isAllCat  ? " AND c.category_name = ? " : "") +
        "ORDER BY im.movement_date DESC";

        int paramIndex = 1;
        int rowsAdded = 0;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (!isAllType)     ps.setString(paramIndex++, movementType);
            if (!isAllCat)      ps.setString(paramIndex++, selectedCategory);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp moveDate = rs.getTimestamp("movement_date");
                    String product = rs.getString("product_name");
                    String type = rs.getString("movement_type");
                    int qty = rs.getInt("quantity");
                    String notes = rs.getString("notes");
                    if (notes == null) notes = "";

                    String dateText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(moveDate);

                    movementModel.addRow(new Object[]{
                            dateText,
                            product,
                            type,
                            qty,
                            notes
                    });

                    rowsAdded++;
                }
            }

            applyFilter();

            if (rowsAdded == 0 && showDialogIfEmpty) {
                JOptionPane.showMessageDialog(dashboard, "No stock movements found for the selected filters.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dashboard, "Stock Movement Load Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

   
    // =========================
    // EXPORT CSV (what you see in table)
    // =========================
    private void exportTableToCSV(JTable table) {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(dashboard, "No data to export.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save CSV");
        chooser.setSelectedFile(new File("stock_movements_report.csv"));

        int result = chooser.showSaveDialog(dashboard);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {

            for (int c = 0; c < table.getColumnCount(); c++) {
                pw.print(csvEscape(table.getColumnName(c)));
                if (c < table.getColumnCount() - 1) pw.print(",");
            }
            pw.println();

            for (int r = 0; r < table.getRowCount(); r++) {
                for (int c = 0; c < table.getColumnCount(); c++) {
                    Object val = table.getValueAt(r, c);
                    pw.print(csvEscape(val == null ? "" : String.valueOf(val)));
                    if (c < table.getColumnCount() - 1) pw.print(",");
                }
                pw.println();
            }

            JOptionPane.showMessageDialog(dashboard, "✅ CSV exported:\n" + file.getAbsolutePath());

            try {
                if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(file);
            } catch (Exception ignore) {}

        } catch (Exception e) {
            JOptionPane.showMessageDialog(dashboard, "CSV Export Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String csvEscape(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        s = s.replace("\"", "\"\"");
        return needQuotes ? ("\"" + s + "\"") : s;
    }

    // =========================
    // EXPORT PDF (your style)
    // =========================
    private void exportTableToPDF(JTable table) {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(dashboard, "No data to export.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save PDF");
        chooser.setSelectedFile(new File("stock_movements_report.pdf"));

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
            float colWidth = tableWidth / cols;

            PDPageContentStream cs = new PDPageContentStream(doc, page);

            cs.beginText();
            cs.setFont(fontBold, titleSize);
            cs.newLineAtOffset(margin, yStart);
            cs.showText("Stock Movements Report");
            cs.endText();

            yStart -= 22;

            String cat = String.valueOf(categoryMovement_cmb.getSelectedItem());
            String type = String.valueOf(movementType_txt.getSelectedItem());

            
            String search = searchMovement_btn.getText().trim();

            String filterLine = "Category: " + cat +
                    " | Type: " + type +
                    " | Search: " + (search.isEmpty() ? "-" : search);

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

            y = drawRow(cs, table, margin, y, colWidth, rowHeight, fontBold, fontSize);

            for (int r = 0; r < table.getRowCount(); r++) {

                if (y - rowHeight < margin) {
                    cs.close();

                    page = new PDPage(PDRectangle.LETTER);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);

                    y = page.getMediaBox().getHeight() - margin;

                    cs.beginText();
                    cs.setFont(fontBold, 12);
                    cs.newLineAtOffset(margin, y);
                    cs.showText("Stock Movements Report (continued)");
                    cs.endText();

                    y -= 18;

                    y = drawRow(cs, table, margin, y, colWidth, rowHeight, fontBold, fontSize);
                }

                y = drawDataRow(cs, table, r, margin, y, colWidth, rowHeight, font, fontSize);
            }

            cs.close();
            doc.save(file);

            JOptionPane.showMessageDialog(dashboard, "✅ PDF exported:\n" + file.getAbsolutePath());

            try {
                if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(file);
            } catch (Exception ignore) {}

        } catch (Exception e) {
            JOptionPane.showMessageDialog(dashboard, "PDF Export Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private float drawRow(PDPageContentStream cs, JTable table,
                          float x, float y, float colWidth, float rowHeight,
                          PDFont font, float fontSize) throws IOException {

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
        s = s.replace("\n", " ").replace("\r", " ");
        if (s.length() > maxLen) return s.substring(0, maxLen - 3) + "...";
        return s;
    }
    
    
    private boolean isAllSelection(String s) {
    if (s == null) return true;
    s = s.trim();
    return s.equalsIgnoreCase("All Categories")
        || s.equalsIgnoreCase("ALL PRODUCTS")
        || s.equalsIgnoreCase("All")
        || s.equalsIgnoreCase("ALL");
}

    // =========================
    // Regex helper
    // =========================
    private static class PatternSafe {
        static String regex(String text) {
            return text.replace("\\", "\\\\")
                    .replace(".", "\\.")
                    .replace("^", "\\^")
                    .replace("$", "\\$")
                    .replace("|", "\\|")
                    .replace("?", "\\?")
                    .replace("*", "\\*")
                    .replace("+", "\\+")
                    .replace("(", "\\(")
                    .replace(")", "\\)")
                    .replace("[", "\\[")
                    .replace("]", "\\]")
                    .replace("{", "\\{")
                    .replace("}", "\\}")
                    .replace("-", "\\-");
        }
    }
}