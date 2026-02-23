package azuelorhoderick;

import azuelorhoderick.Screens.Dashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class POSController {

    private final Dashboard dashboard;
    private final int userId;

    // UI sizes based on your panel
    private static final int W = 1350;
    private static final int H = 500;

    // Theme
    private final Color BLUE = new Color(38, 60, 255);
    private final Color BLUE_DARK = new Color(25, 40, 190);
    private final Color BG = new Color(245, 247, 252);
    private final Color CARD = Color.WHITE;
    private final Color BORDER = new Color(220, 225, 235);
    private final Color TEXT = new Color(25, 25, 25);
    private final Color MUTED = new Color(90, 98, 110);

    // Components
    private JPanel root;

    // Search
    private JTextField searchTxt;
    private JButton searchBtn;

    // Products
    private JTable productsTbl;
    private DefaultTableModel productsModel;

    // Cart
    private JTable cartTbl;
    private DefaultTableModel cartModel;

    // Bottom controls
    private JTextField qtyTxt;
    private JButton plusQtyBtn;
    private JButton minusQtyBtn;
    private JButton addToCartBtn;
    private JButton removeBtn;
    private JButton clearBtn;

    // Labels
    private JLabel cartItemsLbl;
    private JLabel cartTotalTopRightLbl;

    // Totals labels
    private JLabel subtotalLbl;
    private JLabel discountLbl;
    private JLabel taxLbl;

    // NEW: Discount UI
    private JComboBox<String> discountTypeCmb; // None, %, Amount
    private JTextField discountValueTxt;
    private JButton discountClearBtn;

    // NEW: Tax UI
    private JCheckBox taxEnabledChk;
    private JLabel taxInfoLbl; // "VAT 12%" etc.
    private BigDecimal activeTaxRate = BigDecimal.ZERO; // 0.12 etc.

    // Payment method (required)
    private JComboBox<String> paymentMethodCmb;
    private JTextField amountPaidTxt;
    private JLabel changeLbl;

    private JButton checkoutBtn;
    private JButton printReceiptBtn;

    // NEW: Return + Purchase
    private JButton returnBtn;
    private JButton purchaseBtn;

    private JTextArea receiptArea;

    // Totals values
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal tax = BigDecimal.ZERO;
    private BigDecimal grandTotal = BigDecimal.ZERO;

    private final DecimalFormat moneyFmt = new DecimalFormat("#,##0.00");

    public POSController(Dashboard dashboard, int userId) {
        this.dashboard = dashboard;
        this.userId = userId;
    }

    // =========================
    // PUBLIC INIT
    // =========================
    public void init() {
        SwingUtilities.invokeLater(() -> {
            JPanel container = dashboard.getPOSContainerPanel();
            container.removeAll();
            container.setLayout(new BorderLayout());
            container.setPreferredSize(new Dimension(W, H));
            container.setBackground(BG);

            root = new JPanel(new BorderLayout());
            root.setBackground(BG);
            root.setBorder(new EmptyBorder(12, 12, 12, 12));

            root.add(buildBodyDesign1(), BorderLayout.CENTER);

            container.add(root, BorderLayout.CENTER);
            container.revalidate();
            container.repaint();

            // Load defaults
            loadProducts("");
            loadActiveTaxFromDB();
            recalcTotals();
            buildReceiptPreview(null, null, null, null);
        });
    }

    // =========================
    // UI BUILDERS
    // =========================
    private JComponent buildBodyDesign1() {
    // Use split pane so the right side can always show all features
    JComponent left = buildProductsCardDesign1();
    JComponent right = buildCartPaymentCardDesign1();

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
    split.setResizeWeight(0.58);          // 58% products, 42% cart/payment (change if you want)
    split.setDividerLocation((int) (W * 0.58));
    split.setDividerSize(8);
    split.setContinuousLayout(true);
    split.setBorder(null);
    split.setOpaque(false);

    // Make split pane background blend with your theme
    split.setBackground(BG);
    if (split.getLeftComponent() instanceof JComponent) {
        ((JComponent) split.getLeftComponent()).setOpaque(false);
    }
    if (split.getRightComponent() instanceof JComponent) {
        ((JComponent) split.getRightComponent()).setOpaque(false);
    }

    return split;
}

    private JComponent buildProductsCardDesign1() {
        RoundedPanel card = new RoundedPanel(18, CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Header: "Products" + Search
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Products");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JLabel scanLbl = new JLabel("Scan barcode");
        scanLbl.setForeground(new Color(120, 130, 145));
        scanLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchTxt = new JTextField(16);
        stylizeField(searchTxt);
        searchTxt.setToolTipText("Search barcode or product name (Enter)");

        searchBtn = new PrimaryButton("Search", BLUE, BLUE_DARK);

        right.add(scanLbl);
        right.add(searchTxt);
        right.add(searchBtn);

        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        // Products table with status badge
        productsModel = new DefaultTableModel(
                new Object[]{"ID", "Barcode", "Product", "Price", "Stock", "Status"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        productsTbl = new JTable(productsModel);
        stylizeTable(productsTbl);
        productsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Hide ID
        productsTbl.getColumnModel().getColumn(0).setMinWidth(0);
        productsTbl.getColumnModel().getColumn(0).setMaxWidth(0);
        productsTbl.getColumnModel().getColumn(0).setWidth(0);

        // Column widths
        productsTbl.getColumnModel().getColumn(1).setPreferredWidth(95);
        productsTbl.getColumnModel().getColumn(2).setPreferredWidth(240);
        productsTbl.getColumnModel().getColumn(3).setPreferredWidth(70);
        productsTbl.getColumnModel().getColumn(4).setPreferredWidth(55);
        productsTbl.getColumnModel().getColumn(5).setPreferredWidth(110);

        // Status badge renderer
        productsTbl.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane sp = new JScrollPane(productsTbl);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        sp.getViewport().setBackground(Color.WHITE);

        // Bottom controls
        JPanel bottom = new JPanel(new GridBagLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(10, 0, 0, 0));

        GridBagConstraints b = new GridBagConstraints();
        b.insets = new Insets(0, 6, 0, 6);
        b.fill = GridBagConstraints.HORIZONTAL;
        b.gridy = 0;

        JLabel qtyLbl = new JLabel("Qty:");
        qtyLbl.setForeground(MUTED);
        qtyLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        qtyTxt = new JTextField("1", 6);
        stylizeField(qtyTxt);

        plusQtyBtn = new PrimaryButton("+ Qty", BLUE, BLUE_DARK);
        minusQtyBtn = new OutlineButton("- Qty");
        addToCartBtn = new PrimaryButton("Add to Cart", BLUE, BLUE_DARK);
        removeBtn = new OutlineButton("Remove");
        clearBtn = new OutlineButton("Clear");

        plusQtyBtn.setPreferredSize(new Dimension(90, 38));
        minusQtyBtn.setPreferredSize(new Dimension(90, 38));
        addToCartBtn.setPreferredSize(new Dimension(160, 38));
        removeBtn.setPreferredSize(new Dimension(90, 38));
        clearBtn.setPreferredSize(new Dimension(80, 38));

        b.gridx = 0; b.weightx = 0;
        bottom.add(qtyLbl, b);

        b.gridx = 1; b.weightx = 0.16;
        bottom.add(qtyTxt, b);

        b.gridx = 2; b.weightx = 0.0;
        bottom.add(plusQtyBtn, b);

        b.gridx = 3; b.weightx = 0.0;
        bottom.add(minusQtyBtn, b);

        b.gridx = 4; b.weightx = 0.55;
        bottom.add(addToCartBtn, b);

        b.gridx = 5; b.weightx = 0.0;
        bottom.add(removeBtn, b);

        b.gridx = 6; b.weightx = 0.0;
        bottom.add(clearBtn, b);

        // Events
        searchBtn.addActionListener(e -> loadProducts(searchTxt.getText().trim()));
        searchTxt.addActionListener(e -> loadProducts(searchTxt.getText().trim()));

        plusQtyBtn.addActionListener(e -> bumpQty(+1));
        minusQtyBtn.addActionListener(e -> bumpQty(-1));

        addToCartBtn.addActionListener(e -> addSelectedProductToCart());
        removeBtn.addActionListener(e -> removeSelectedCartItemSafe());
        clearBtn.addActionListener(e -> clearCart());

        productsTbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) addSelectedProductToCart();
            }
        });

        card.add(header, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private JComponent buildCartPaymentCardDesign1() {
    RoundedPanel card = new RoundedPanel(18, CARD);
    card.setLayout(new BorderLayout());
    card.setBorder(new EmptyBorder(12, 12, 12, 12));

    // =========================
    // Header
    // =========================
    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);

    JLabel title = new JLabel("Cart & Payment");
    title.setFont(new Font("Segoe UI", Font.BOLD, 16));
    title.setForeground(TEXT);

    cartTotalTopRightLbl = new JLabel("0.00 PHP");
    cartTotalTopRightLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
    cartTotalTopRightLbl.setForeground(TEXT);

    header.add(title, BorderLayout.WEST);
    header.add(cartTotalTopRightLbl, BorderLayout.EAST);

    cartItemsLbl = new JLabel("Items: 0");
    cartItemsLbl.setForeground(MUTED);
    cartItemsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    cartItemsLbl.setBorder(new EmptyBorder(6, 0, 8, 0));

    // =========================
    // Cart table
    // =========================
    cartModel = new DefaultTableModel(
            new Object[]{"ID", "Barcode", "Product", "Price", "Qty", "Subtotal"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };

    cartTbl = new JTable(cartModel);
    stylizeTable(cartTbl);
    cartTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // hide ID
    cartTbl.getColumnModel().getColumn(0).setMinWidth(0);
    cartTbl.getColumnModel().getColumn(0).setMaxWidth(0);
    cartTbl.getColumnModel().getColumn(0).setWidth(0);

    cartTbl.getColumnModel().getColumn(1).setPreferredWidth(90);
    cartTbl.getColumnModel().getColumn(2).setPreferredWidth(200);
    cartTbl.getColumnModel().getColumn(3).setPreferredWidth(60);
    cartTbl.getColumnModel().getColumn(4).setPreferredWidth(40);
    cartTbl.getColumnModel().getColumn(5).setPreferredWidth(80);

    JScrollPane cartSp = new JScrollPane(cartTbl);
    cartSp.setBorder(BorderFactory.createLineBorder(BORDER));
    cartSp.getViewport().setBackground(Color.WHITE);

    // Keep cart visible but not too tall (so payment/receipt can be seen)
    cartSp.setPreferredSize(new Dimension(0, 170));

    // =========================
    // Totals summary box
    // =========================
    JPanel summaryBox = new JPanel(new GridBagLayout());
    summaryBox.setOpaque(false);
    summaryBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(235, 238, 245)),
            new EmptyBorder(10, 10, 10, 10)
    ));

    GridBagConstraints t = new GridBagConstraints();
    t.insets = new Insets(4, 0, 4, 0);
    t.fill = GridBagConstraints.HORIZONTAL;
    t.gridy = 0;

    subtotalLbl = new JLabel("0.00 PHP");
    discountLbl = new JLabel("0.00 PHP");
    taxLbl = new JLabel("0.00 PHP");

    stylizeMoneyLabel(subtotalLbl, true);
    stylizeMoneyLabel(discountLbl, false);
    stylizeMoneyLabel(taxLbl, false);

    // Subtotal row
    addTotalsRow(summaryBox, t, "Subtotal:", subtotalLbl);

    // Discount row (controls right aligned)
    discountTypeCmb = new JComboBox<>(new String[]{"None", "%", "Amount"});
    discountTypeCmb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    discountTypeCmb.setPreferredSize(new Dimension(85, 28));

    discountValueTxt = new JTextField("0");
    stylizeField(discountValueTxt);
    discountValueTxt.setPreferredSize(new Dimension(55, 28));

    discountClearBtn = new OutlineButton("Clear");
    discountClearBtn.setPreferredSize(new Dimension(65, 28));

    JPanel discControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
    discControls.setOpaque(false);
    discControls.add(discountTypeCmb);
    discControls.add(discountValueTxt);
    discControls.add(discountClearBtn);

    JPanel discRight = new JPanel();
    discRight.setOpaque(false);
    discRight.setLayout(new BoxLayout(discRight, BoxLayout.Y_AXIS));
    discountLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
    discControls.setAlignmentX(Component.RIGHT_ALIGNMENT);
    discRight.add(discountLbl);
    discRight.add(Box.createVerticalStrut(2));
    discRight.add(discControls);

    JLabel discL = new JLabel("Discount:");
    discL.setForeground(MUTED);
    discL.setFont(new Font("Segoe UI", Font.PLAIN, 13));

    t.gridx = 0; t.weightx = 0.55;
    summaryBox.add(discL, t);
    t.gridx = 1; t.weightx = 0.45;
    summaryBox.add(discRight, t);
    t.gridy++;

    // Tax row
    taxEnabledChk = new JCheckBox("Apply tax");
    taxEnabledChk.setOpaque(false);
    taxEnabledChk.setSelected(true);
    taxEnabledChk.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    taxEnabledChk.setForeground(MUTED);

    taxInfoLbl = new JLabel("No active tax");
    taxInfoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    taxInfoLbl.setForeground(new Color(120, 130, 145));
    taxInfoLbl.setHorizontalAlignment(SwingConstants.RIGHT);

    JPanel taxMeta = new JPanel(new BorderLayout(8, 0));
    taxMeta.setOpaque(false);
    taxMeta.add(taxEnabledChk, BorderLayout.WEST);
    taxMeta.add(taxInfoLbl, BorderLayout.EAST);

    JPanel taxRight = new JPanel();
    taxRight.setOpaque(false);
    taxRight.setLayout(new BoxLayout(taxRight, BoxLayout.Y_AXIS));
    taxLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
    taxMeta.setAlignmentX(Component.RIGHT_ALIGNMENT);
    taxRight.add(taxLbl);
    taxRight.add(Box.createVerticalStrut(2));
    taxRight.add(taxMeta);

    JLabel taxL = new JLabel("Tax:");
    taxL.setForeground(MUTED);
    taxL.setFont(new Font("Segoe UI", Font.PLAIN, 13));

    t.gridx = 0; t.weightx = 0.55;
    summaryBox.add(taxL, t);
    t.gridx = 1; t.weightx = 0.45;
    summaryBox.add(taxRight, t);
    t.gridy++;

    // =========================
    // Payment panel
    // =========================
    JPanel paymentPanel = new JPanel(new GridBagLayout());
    paymentPanel.setOpaque(false);
    paymentPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

    GridBagConstraints p = new GridBagConstraints();
    p.insets = new Insets(6, 0, 6, 0);
    p.fill = GridBagConstraints.HORIZONTAL;
    p.gridy = 0;

    paymentMethodCmb = new JComboBox<>(new String[]{"Cash", "Card", "GCash", "Maya"});
    paymentMethodCmb.setFont(new Font("Segoe UI", Font.PLAIN, 13));

    amountPaidTxt = new JTextField();
    stylizeField(amountPaidTxt);

    changeLbl = new JLabel("0.00 PHP");
    stylizeMoneyLabel(changeLbl, true);

    p.gridx = 0; p.weightx = 0.40;
    paymentPanel.add(labelMuted("Payment Method:"), p);
    p.gridx = 1; p.weightx = 0.60;
    paymentPanel.add(paymentMethodCmb, p);

    p.gridy++;
    p.gridx = 0; p.weightx = 0.40;
    paymentPanel.add(labelMuted("Amount Paid:"), p);
    p.gridx = 1; p.weightx = 0.60;
    paymentPanel.add(amountPaidTxt, p);

    p.gridy++;
    p.gridx = 0; p.weightx = 0.40;
    paymentPanel.add(labelMuted("Change:"), p);
    p.gridx = 1; p.weightx = 0.60;
    paymentPanel.add(changeLbl, p);

    // =========================
    // Action buttons
    // =========================
    JPanel actionGrid = new JPanel(new GridLayout(2, 2, 10, 10));
    actionGrid.setOpaque(false);
    actionGrid.setBorder(new EmptyBorder(8, 0, 0, 0));

    checkoutBtn = new PrimaryButton("CHECKOUT", BLUE, BLUE_DARK);
    printReceiptBtn = new OutlineButton("Print Receipt");
    printReceiptBtn.setEnabled(false);

    returnBtn = new OutlineButton("Return");
    purchaseBtn = new OutlineButton("New Purchase");

    checkoutBtn.setPreferredSize(new Dimension(0, 36));
    printReceiptBtn.setPreferredSize(new Dimension(0, 36));
    returnBtn.setPreferredSize(new Dimension(0, 36));
    purchaseBtn.setPreferredSize(new Dimension(0, 36));

    actionGrid.add(checkoutBtn);
    actionGrid.add(printReceiptBtn);
    actionGrid.add(returnBtn);
    actionGrid.add(purchaseBtn);

    // =========================
    // Receipt preview
    // =========================
    JLabel receiptLbl = new JLabel("Receipt Preview");
    receiptLbl.setForeground(MUTED);
    receiptLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
    receiptLbl.setBorder(new EmptyBorder(10, 0, 6, 0));

    receiptArea = new JTextArea(7, 20);
    receiptArea.setEditable(false);
    receiptArea.setFont(new Font("Consolas", Font.PLAIN, 12));
    receiptArea.setBorder(BorderFactory.createLineBorder(BORDER));
    receiptArea.setBackground(new Color(252, 252, 252));

    JScrollPane receiptSp = new JScrollPane(receiptArea);
    receiptSp.setBorder(BorderFactory.createEmptyBorder());
    receiptSp.setPreferredSize(new Dimension(0, 150));

    // =========================
    // Scrollable center stack
    // =========================
    JPanel stack = new JPanel();
    stack.setOpaque(false);
    stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));

    // Force children to fill width in BoxLayout
    Dimension fillW = new Dimension(Integer.MAX_VALUE, 1);

    JPanel cartBlock = new JPanel(new BorderLayout());
    cartBlock.setOpaque(false);
    cartBlock.add(cartItemsLbl, BorderLayout.NORTH);
    cartBlock.add(cartSp, BorderLayout.CENTER);
    cartBlock.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

    summaryBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
    paymentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
    actionGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
    receiptSp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

    // Add to stack
    stack.add(cartBlock);
    stack.add(Box.createVerticalStrut(8));
    stack.add(summaryBox);
    stack.add(Box.createVerticalStrut(8));
    stack.add(paymentPanel);
    stack.add(actionGrid);
    stack.add(receiptLbl);
    stack.add(receiptSp);

    // Make the whole stack scrollable (THIS fixes “cut off” content)
    JScrollPane rightScroll = new JScrollPane(stack);
    rightScroll.setBorder(null);
    rightScroll.getViewport().setOpaque(false);
    rightScroll.setOpaque(false);
    rightScroll.getVerticalScrollBar().setUnitIncrement(18);

    // Final card
    card.add(header, BorderLayout.NORTH);
    card.add(rightScroll, BorderLayout.CENTER);

    // =========================
    // EVENTS
    // =========================
    amountPaidTxt.addKeyListener(new KeyAdapter() {
        @Override public void keyReleased(KeyEvent e) { updateChangePreview(); }
    });
    paymentMethodCmb.addActionListener(e -> updateChangePreview());

    checkoutBtn.addActionListener(e -> checkout());
    printReceiptBtn.addActionListener(e -> printReceipt());

    cartTbl.addMouseListener(new MouseAdapter() {
        @Override public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) adjustSelectedCartQty(-1);
        }
    });

    discountTypeCmb.addActionListener(e -> { recalcTotals(); buildReceiptPreview(null, null, null, null); });
    discountValueTxt.addKeyListener(new KeyAdapter() {
        @Override public void keyReleased(KeyEvent e) { recalcTotals(); buildReceiptPreview(null, null, null, null); }
    });
    discountClearBtn.addActionListener(e -> {
        discountTypeCmb.setSelectedItem("None");
        discountValueTxt.setText("0");
        recalcTotals();
        buildReceiptPreview(null, null, null, null);
    });

    taxEnabledChk.addActionListener(e -> { recalcTotals(); buildReceiptPreview(null, null, null, null); });

    returnBtn.addActionListener(e -> openReturnDialog());
    purchaseBtn.addActionListener(e -> openPurchaseDialog());

    return card;
}

    // =========================
    // LOADERS
    // =========================
    private void loadProducts(String keywordOrBarcode) {
        productsModel.setRowCount(0);

        String kw = (keywordOrBarcode == null) ? "" : keywordOrBarcode.trim();

        String sql =
                "SELECT p.product_id, p.barcode, p.product_name, p.unit_price, p.reorder_level, " +
                "       COALESCE(i.current_stock, p.stock_quantity) AS stock " +
                "FROM products p " +
                "LEFT JOIN inventory i ON i.product_id = p.product_id " +
                "WHERE p.status = 'Available' " +
                "  AND ( ? = '' OR p.barcode = ? OR p.product_name LIKE ? ) " +
                "ORDER BY p.product_name ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, "%" + kw + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int stock = rs.getInt("stock");
                    int reorder = rs.getInt("reorder_level");

                    String status;
                    if (stock <= 0) status = "OUT OF STOCK";
                    else if (stock <= reorder) status = "LOW STOCK";
                    else status = "IN STOCK";

                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("product_id"));
                    row.add(rs.getString("barcode"));
                    row.add(rs.getString("product_name"));
                    row.add(rs.getBigDecimal("unit_price"));
                    row.add(stock);
                    row.add(status);
                    productsModel.addRow(row);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dashboard, "Failed to load products.\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // NEW: load active tax from taxes table
    private void loadActiveTaxFromDB() {
        activeTaxRate = BigDecimal.ZERO;
        String label = "No active tax";

        String sql = "SELECT tax_name, tax_rate FROM taxes WHERE status='Active' ORDER BY tax_id ASC LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String name = rs.getString("tax_name");
                BigDecimal rate = rs.getBigDecimal("tax_rate");
                if (rate == null) rate = BigDecimal.ZERO;

                // If user stored 12.000 => treat as 12%
                // If user stored 0.120 => treat as 12% too
                BigDecimal normalized = rate;
                if (normalized.compareTo(BigDecimal.ONE) > 0) {
                    normalized = normalized.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
                }
                activeTaxRate = normalized.max(BigDecimal.ZERO);

                BigDecimal pct = activeTaxRate.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
                label = (name == null || name.trim().isEmpty())
                        ? ("Tax " + pct + "%")
                        : (name + " " + pct + "%");
            }

        } catch (Exception ex) {
            // keep tax 0, just show info
            label = "Tax load failed";
        }

        if (taxInfoLbl != null) taxInfoLbl.setText(label);
    }

    // =========================
    // QTY
    // =========================
    private void bumpQty(int delta) {
        int qty;
        try {
            qty = Integer.parseInt(qtyTxt.getText().trim());
        } catch (Exception e) {
            qty = 1;
        }
        qty += delta;
        if (qty < 1) qty = 1;
        qtyTxt.setText(String.valueOf(qty));
        qtyTxt.requestFocus();
    }

    // =========================
    // CART
    // =========================
    private void addSelectedProductToCart() {
        int row = productsTbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(dashboard, "Select a product first.");
            return;
        }

        int productId = toInt(productsModel.getValueAt(row, 0));
        String barcode = String.valueOf(productsModel.getValueAt(row, 1));
        String name = String.valueOf(productsModel.getValueAt(row, 2));
        BigDecimal price = toBig(productsModel.getValueAt(row, 3));
        int stock = toInt(productsModel.getValueAt(row, 4));
        String status = String.valueOf(productsModel.getValueAt(row, 5));

        if ("OUT OF STOCK".equalsIgnoreCase(status) || stock <= 0) {
            JOptionPane.showMessageDialog(dashboard, "This product is OUT OF STOCK.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyTxt.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dashboard, "Invalid quantity.");
            return;
        }

        int existingRow = findCartRowByProductId(productId);
        int newQty = qty;

        if (existingRow >= 0) {
            int currentQty = toInt(cartModel.getValueAt(existingRow, 4));
            newQty = currentQty + qty;
        }

        if (newQty > stock) {
            JOptionPane.showMessageDialog(dashboard,
                    "Not enough stock.\nAvailable: " + stock + "\nRequested: " + newQty,
                    "Stock Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal lineSubtotal = price.multiply(BigDecimal.valueOf(newQty));

        if (existingRow >= 0) {
            cartModel.setValueAt(newQty, existingRow, 4);
            cartModel.setValueAt(lineSubtotal, existingRow, 5);
        } else {
            cartModel.addRow(new Object[]{ productId, barcode, name, price, newQty, lineSubtotal });
        }

        recalcTotals();
        buildReceiptPreview(null, null, null, null);

        qtyTxt.setText("1");
        qtyTxt.requestFocus();
    }

    private void removeSelectedCartItemSafe() {
        int row = cartTbl.getSelectedRow();
        if (row < 0 && cartModel.getRowCount() > 0) {
            row = cartModel.getRowCount() - 1;
            cartTbl.setRowSelectionInterval(row, row);
        }
        if (row < 0) {
            JOptionPane.showMessageDialog(dashboard, "No item selected in cart.");
            return;
        }
        cartModel.removeRow(row);
        recalcTotals();
        buildReceiptPreview(null, null, null, null);
    }

    private void clearCart() {
        if (cartModel.getRowCount() == 0) return;
        int ok = JOptionPane.showConfirmDialog(dashboard, "Clear all cart items?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        cartModel.setRowCount(0);
        recalcTotals();
        buildReceiptPreview(null, null, null, null);
        printReceiptBtn.setEnabled(false);
    }

    private void adjustSelectedCartQty(int delta) {
        int row = cartTbl.getSelectedRow();
        if (row < 0 && cartModel.getRowCount() > 0) {
            row = cartModel.getRowCount() - 1;
            cartTbl.setRowSelectionInterval(row, row);
        }
        if (row < 0) return;

        int productId = toInt(cartModel.getValueAt(row, 0));
        BigDecimal price = toBig(cartModel.getValueAt(row, 3));
        int qty = toInt(cartModel.getValueAt(row, 4));
        int newQty = qty + delta;

        if (newQty <= 0) {
            cartModel.removeRow(row);
            recalcTotals();
            buildReceiptPreview(null, null, null, null);
            return;
        }

        int maxStock = getStockFromProductsList(productId);
        if (maxStock >= 0 && newQty > maxStock) {
            JOptionPane.showMessageDialog(dashboard,
                    "Not enough stock.\nAvailable: " + maxStock + "\nRequested: " + newQty,
                    "Stock Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal lineSubtotal = price.multiply(BigDecimal.valueOf(newQty));
        cartModel.setValueAt(newQty, row, 4);
        cartModel.setValueAt(lineSubtotal, row, 5);

        recalcTotals();
        buildReceiptPreview(null, null, null, null);
    }

    private int getStockFromProductsList(int productId) {
        if (productsModel == null) return -1;
        for (int i = 0; i < productsModel.getRowCount(); i++) {
            int id = toInt(productsModel.getValueAt(i, 0));
            if (id == productId) return toInt(productsModel.getValueAt(i, 4));
        }
        return -1;
    }

    private int findCartRowByProductId(int productId) {
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            int id = toInt(cartModel.getValueAt(i, 0));
            if (id == productId) return i;
        }
        return -1;
    }

    // =========================
    // TOTALS (UPDATED for Discount + Tax)
    // =========================
    private void recalcTotals() {
        subtotal = BigDecimal.ZERO;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            subtotal = subtotal.add(toBig(cartModel.getValueAt(i, 5)));
        }

        // Discount
        discount = computeDiscountFromUI(subtotal);

        BigDecimal taxableBase = subtotal.subtract(discount);
        if (taxableBase.compareTo(BigDecimal.ZERO) < 0) taxableBase = BigDecimal.ZERO;

        // Tax
        boolean taxEnabled = (taxEnabledChk != null && taxEnabledChk.isSelected());
        tax = taxEnabled ? taxableBase.multiply(activeTaxRate) : BigDecimal.ZERO;
        tax = tax.setScale(2, RoundingMode.HALF_UP);

        grandTotal = taxableBase.add(tax);
        if (grandTotal.compareTo(BigDecimal.ZERO) < 0) grandTotal = BigDecimal.ZERO;

        subtotalLbl.setText(moneyFmt.format(subtotal) + " PHP");
        discountLbl.setText(moneyFmt.format(discount) + " PHP");
        taxLbl.setText(moneyFmt.format(tax) + " PHP");

        cartTotalTopRightLbl.setText(moneyFmt.format(grandTotal) + " PHP");
        cartItemsLbl.setText("Items: " + cartModel.getRowCount());

        updateChangePreview();
    }

    private BigDecimal computeDiscountFromUI(BigDecimal base) {
        if (discountTypeCmb == null || discountValueTxt == null) return BigDecimal.ZERO;

        String type = String.valueOf(discountTypeCmb.getSelectedItem());
        BigDecimal val = parseMoney(discountValueTxt.getText());

        if (base == null) base = BigDecimal.ZERO;
        if (val.compareTo(BigDecimal.ZERO) < 0) val = BigDecimal.ZERO;

        BigDecimal d = BigDecimal.ZERO;

        if ("None".equalsIgnoreCase(type)) {
            d = BigDecimal.ZERO;
        } else if ("%".equals(type)) {
            // percent 0-100
            if (val.compareTo(BigDecimal.valueOf(100)) > 0) val = BigDecimal.valueOf(100);
            d = base.multiply(val).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if ("Amount".equalsIgnoreCase(type)) {
            d = val.setScale(2, RoundingMode.HALF_UP);
        }

        if (d.compareTo(base) > 0) d = base;
        if (d.compareTo(BigDecimal.ZERO) < 0) d = BigDecimal.ZERO;
        return d;
    }

    private void updateChangePreview() {
        BigDecimal paid = parseMoney(amountPaidTxt.getText().trim());
        String method = String.valueOf(paymentMethodCmb.getSelectedItem());

        BigDecimal change = BigDecimal.ZERO;

        if ("Cash".equalsIgnoreCase(method)) {
            if (paid.compareTo(grandTotal) >= 0) change = paid.subtract(grandTotal);
        } else {
            change = BigDecimal.ZERO;
        }

        changeLbl.setText(moneyFmt.format(change) + " PHP");
    }

    // =========================
    // CHECKOUT (kept your flow but now saves discount/tax)
    // =========================
    private void checkout() {
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(dashboard, "Cart is empty.");
            return;
        }

        String method = String.valueOf(paymentMethodCmb.getSelectedItem());
        BigDecimal paid = parseMoney(amountPaidTxt.getText().trim());

        if ("Cash".equalsIgnoreCase(method)) {
            if (paid.compareTo(grandTotal) < 0) {
                JOptionPane.showMessageDialog(dashboard,
                        "Insufficient cash.\nGrand Total: " + moneyFmt.format(grandTotal),
                        "Payment Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            if (paid.compareTo(grandTotal) != 0) {
                JOptionPane.showMessageDialog(dashboard,
                        "For " + method + ", Amount Paid must equal Grand Total.\nGrand Total: " + moneyFmt.format(grandTotal),
                        "Payment Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        BigDecimal change = "Cash".equalsIgnoreCase(method) ? paid.subtract(grandTotal) : BigDecimal.ZERO;

        String trx = "TRX-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // Re-check stock in DB
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                int productId = toInt(cartModel.getValueAt(i, 0));
                int qty = toInt(cartModel.getValueAt(i, 4));

                int dbStock = getCurrentStock(con, productId);
                if (qty > dbStock) {
                    con.rollback();
                    JOptionPane.showMessageDialog(dashboard,
                            "Stock changed in DB.\nProduct ID: " + productId +
                                    "\nAvailable: " + dbStock + "\nRequested: " + qty,
                            "Stock Error", JOptionPane.ERROR_MESSAGE);
                    loadProducts(searchTxt.getText().trim());
                    return;
                }
            }

            int saleId = insertSale(con, trx, method);

            for (int i = 0; i < cartModel.getRowCount(); i++) {
                int productId = toInt(cartModel.getValueAt(i, 0));
                int qty = toInt(cartModel.getValueAt(i, 4));
                BigDecimal unitPrice = toBig(cartModel.getValueAt(i, 3));
                BigDecimal lineSubtotal = toBig(cartModel.getValueAt(i, 5));

                // keep per-item discount 0 for now (whole-sale discount handled in sales table)
                insertSaleDetail(con, saleId, productId, qty, unitPrice, BigDecimal.ZERO, lineSubtotal);

                updateStockAfterSale(con, productId, qty);
                insertInventoryMovement(con, productId, "OUT", qty, "POS Sale " + trx);
            }

            insertPayment(con, saleId, method, paid, change);

            String receiptNo = "RCP-" + trx;
            insertReceipt(con, saleId, receiptNo);

            insertAuditLog(con, userId, "POS CHECKOUT",
                    "Completed sale " + trx + " Grand Total " + moneyFmt.format(grandTotal));

            con.commit();

            buildReceiptPreview(trx, receiptNo, method, paid);
            printReceiptBtn.setEnabled(true);

            JOptionPane.showMessageDialog(dashboard,
                    "Checkout Successful!\nTransaction: " + trx + "\nGrand Total: " + moneyFmt.format(grandTotal),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            cartModel.setRowCount(0);
            recalcTotals();
            amountPaidTxt.setText("");
            loadProducts(searchTxt.getText().trim());

        } catch (Exception ex) {
            try { if (con != null) con.rollback(); } catch (Exception ignore) {}
            JOptionPane.showMessageDialog(dashboard,
                    "Checkout failed.\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (Exception ignore) {}
            try { if (con != null) con.close(); } catch (Exception ignore) {}
        }
    }

    private int getCurrentStock(Connection con, int productId) throws SQLException {
        String sql =
                "SELECT COALESCE(i.current_stock, p.stock_quantity) AS stock " +
                "FROM products p LEFT JOIN inventory i ON i.product_id = p.product_id " +
                "WHERE p.product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("stock");
            }
        }
        return 0;
    }

    private int insertSale(Connection con, String trx, String method) throws SQLException {
        String sql =
                "INSERT INTO sales (transaction_number, user_id, customer_id, total_amount, discount_amount, tax_amount, grand_total, payment_method, payment_status) " +
                "VALUES (?, ?, NULL, ?, ?, ?, ?, ?, 'Paid')";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, trx);
            ps.setInt(2, userId);
            ps.setBigDecimal(3, subtotal);
            ps.setBigDecimal(4, discount);
            ps.setBigDecimal(5, tax);
            ps.setBigDecimal(6, grandTotal);
            ps.setString(7, method);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Failed to create sale record.");
    }

    private void insertSaleDetail(Connection con, int saleId, int productId, int qty,
                                  BigDecimal unitPrice, BigDecimal disc, BigDecimal lineSubtotal) throws SQLException {
        String sql =
                "INSERT INTO sale_details (sale_id, product_id, quantity, unit_price, discount, subtotal) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            ps.setBigDecimal(4, unitPrice);
            ps.setBigDecimal(5, disc);
            ps.setBigDecimal(6, lineSubtotal);
            ps.executeUpdate();
        }
    }

    private void updateStockAfterSale(Connection con, int productId, int qty) throws SQLException {
        ensureInventoryRow(con, productId);

        String invSql =
                "UPDATE inventory " +
                "SET quantity_out = quantity_out + ?, current_stock = current_stock - ? " +
                "WHERE product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(invSql)) {
            ps.setInt(1, qty);
            ps.setInt(2, qty);
            ps.setInt(3, productId);
            ps.executeUpdate();
        }

        String prodSql =
                "UPDATE products " +
                "SET stock_quantity = stock_quantity - ? " +
                "WHERE product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(prodSql)) {
            ps.setInt(1, qty);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }

        int newStock = getCurrentStock(con, productId);
        String status = (newStock <= 0) ? "Out of Stock" : "Available";

        String statusSql =
                "UPDATE products SET status = ? WHERE product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(statusSql)) {
            ps.setString(1, status);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    private void ensureInventoryRow(Connection con, int productId) throws SQLException {
        String check = "SELECT inventory_id FROM inventory WHERE product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(check)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return;
            }
        }

        int prodStock = 0;
        try (PreparedStatement ps = con.prepareStatement("SELECT stock_quantity FROM products WHERE product_id = ?")) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) prodStock = rs.getInt(1);
            }
        }

        String ins =
                "INSERT INTO inventory (product_id, quantity_in, quantity_out, current_stock) VALUES (?, 0, 0, ?)";
        try (PreparedStatement ps = con.prepareStatement(ins)) {
            ps.setInt(1, productId);
            ps.setInt(2, prodStock);
            ps.executeUpdate();
        }
    }

    private void insertInventoryMovement(Connection con, int productId, String type, int qty, String notes) throws SQLException {
        String sql =
                "INSERT INTO inventory_movements (product_id, movement_type, quantity, notes) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setString(2, type);
            ps.setInt(3, qty);
            ps.setString(4, notes);
            ps.executeUpdate();
        }
    }

    private void insertPayment(Connection con, int saleId, String method, BigDecimal paid, BigDecimal change) throws SQLException {
        String sql =
                "INSERT INTO payments (sale_id, payment_method, amount_paid, `change`) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            ps.setString(2, method);
            ps.setBigDecimal(3, paid);
            ps.setBigDecimal(4, change);
            ps.executeUpdate();
        }
    }

    private void insertReceipt(Connection con, int saleId, String receiptNo) throws SQLException {
        String sql =
                "INSERT INTO receipts (sale_id, receipt_number) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            ps.setString(2, receiptNo);
            ps.executeUpdate();
        }
    }

    private void insertAuditLog(Connection con, int userId, String action, String desc) throws SQLException {
        String sql =
                "INSERT INTO audit_logs (user_id, action, description) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, desc);
            ps.executeUpdate();
        }
    }

    // =========================
    // RETURNS (NEW)
    // =========================
    private void openReturnDialog() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(root), "Return Item", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(720, 520);
        dlg.setLocationRelativeTo(dashboard);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel trxLbl = new JLabel("Transaction No:");
        trxLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTextField trxTxt = new JTextField(18);
        stylizeField(trxTxt);
        JButton loadBtn = new PrimaryButton("Load", BLUE, BLUE_DARK);
        top.add(trxLbl);
        top.add(trxTxt);
        top.add(loadBtn);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"SaleID", "ProductID", "Product", "Qty Sold", "Already Returned", "Unit Price", "Line Subtotal"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(model);
        stylizeTable(tbl);

        // Hide IDs
        tbl.getColumnModel().getColumn(0).setMinWidth(0);
        tbl.getColumnModel().getColumn(0).setMaxWidth(0);
        tbl.getColumnModel().getColumn(0).setWidth(0);
        tbl.getColumnModel().getColumn(1).setMinWidth(0);
        tbl.getColumnModel().getColumn(1).setMaxWidth(0);
        tbl.getColumnModel().getColumn(1).setWidth(0);

        JScrollPane sp = new JScrollPane(tbl);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));

        JPanel bottom = new JPanel(new GridBagLayout());
        bottom.setBorder(new EmptyBorder(10, 0, 0, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;

        JTextField qtyTxt = new JTextField("1", 8);
        stylizeField(qtyTxt);

        JTextField reasonTxt = new JTextField("", 20);
        stylizeField(reasonTxt);

        JLabel refundLbl = new JLabel("Refund: 0.00 PHP");
        refundLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refundLbl.setForeground(TEXT);

        JButton processBtn = new PrimaryButton("Process Return", BLUE, BLUE_DARK);
        JButton closeBtn = new OutlineButton("Close");

        c.gridx = 0; c.weightx = 0.2;
        bottom.add(labelMuted("Return Qty:"), c);
        c.gridx = 1; c.weightx = 0.3;
        bottom.add(qtyTxt, c);

        c.gridx = 2; c.weightx = 0.2;
        bottom.add(labelMuted("Reason:"), c);
        c.gridx = 3; c.weightx = 0.8;
        bottom.add(reasonTxt, c);

        c.gridy++;
        c.gridx = 0; c.weightx = 0.6;
        bottom.add(refundLbl, c);

        c.gridx = 2; c.weightx = 0.2;
        bottom.add(processBtn, c);
        c.gridx = 3; c.weightx = 0.2;
        bottom.add(closeBtn, c);

        panel.add(top, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        dlg.setContentPane(panel);

        Runnable recomputeRefund = () -> {
            int r = tbl.getSelectedRow();
            if (r < 0) { refundLbl.setText("Refund: 0.00 PHP"); return; }
            int qtySold = toInt(model.getValueAt(r, 3));
            int alreadyRet = toInt(model.getValueAt(r, 4));
            int max = Math.max(0, qtySold - alreadyRet);

            int rq;
            try { rq = Integer.parseInt(qtyTxt.getText().trim()); } catch (Exception ex) { rq = 0; }
            if (rq < 0) rq = 0;
            if (rq > max) rq = max;

            BigDecimal lineSubtotal = toBig(model.getValueAt(r, 6));
            BigDecimal perUnit = (qtySold > 0)
                    ? lineSubtotal.divide(BigDecimal.valueOf(qtySold), 4, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            BigDecimal refund = perUnit.multiply(BigDecimal.valueOf(rq)).setScale(2, RoundingMode.HALF_UP);
            refundLbl.setText("Refund: " + moneyFmt.format(refund) + " PHP");
        };

        loadBtn.addActionListener(e -> {
            model.setRowCount(0);
            String trx = trxTxt.getText().trim();
            if (trx.isEmpty()) {
                JOptionPane.showMessageDialog(dashboard, "Enter Transaction No.");
                return;
            }
            try (Connection con = DBConnection.getConnection()) {
                int saleId = getSaleIdByTrx(con, trx);
                if (saleId <= 0) {
                    JOptionPane.showMessageDialog(dashboard, "Transaction not found.");
                    return;
                }

                String sql =
                        "SELECT sd.sale_id, sd.product_id, p.product_name, sd.quantity, sd.unit_price, sd.subtotal " +
                        "FROM sale_details sd " +
                        "JOIN products p ON p.product_id = sd.product_id " +
                        "WHERE sd.sale_id = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, saleId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int pid = rs.getInt("product_id");
                            int sold = rs.getInt("quantity");
                            int already = getReturnedQty(con, saleId, pid);

                            model.addRow(new Object[]{
                                    rs.getInt("sale_id"),
                                    pid,
                                    rs.getString("product_name"),
                                    sold,
                                    already,
                                    rs.getBigDecimal("unit_price"),
                                    rs.getBigDecimal("subtotal")
                            });
                        }
                    }
                }

                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(dashboard, "No sale details found for this transaction.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dashboard, "Load failed.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        tbl.getSelectionModel().addListSelectionListener(e -> recomputeRefund.run());
        qtyTxt.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { recomputeRefund.run(); }
        });

        processBtn.addActionListener(e -> {
            int r = tbl.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(dashboard, "Select an item to return."); return; }

            int saleId = toInt(model.getValueAt(r, 0));
            int productId = toInt(model.getValueAt(r, 1));
            String productName = String.valueOf(model.getValueAt(r, 2));
            int sold = toInt(model.getValueAt(r, 3));
            int already = toInt(model.getValueAt(r, 4));
            int max = Math.max(0, sold - already);

            int rq;
            try { rq = Integer.parseInt(qtyTxt.getText().trim()); } catch (Exception ex) { rq = 0; }
            if (rq <= 0) { JOptionPane.showMessageDialog(dashboard, "Invalid return quantity."); return; }
            if (rq > max) {
                JOptionPane.showMessageDialog(dashboard, "Return qty exceeds available.\nMax: " + max);
                return;
            }

            String reason = reasonTxt.getText().trim();
            if (reason.isEmpty()) reason = "Return";

            BigDecimal lineSubtotal = toBig(model.getValueAt(r, 6));
            BigDecimal perUnit = (sold > 0)
                    ? lineSubtotal.divide(BigDecimal.valueOf(sold), 4, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            BigDecimal refund = perUnit.multiply(BigDecimal.valueOf(rq)).setScale(2, RoundingMode.HALF_UP);

            int ok = JOptionPane.showConfirmDialog(dashboard,
                    "Process return?\nProduct: " + productName + "\nQty: " + rq + "\nRefund: " + moneyFmt.format(refund),
                    "Confirm Return", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;

            Connection con = null;
            try {
                con = DBConnection.getConnection();
                con.setAutoCommit(false);

                // insert returns
                String ins =
                        "INSERT INTO returns (sale_id, product_id, quantity, return_reason, refund_amount) " +
                        "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(ins)) {
                    ps.setInt(1, saleId);
                    ps.setInt(2, productId);
                    ps.setInt(3, rq);
                    ps.setString(4, reason);
                    ps.setBigDecimal(5, refund);
                    ps.executeUpdate();
                }

                // restock inventory/products
                ensureInventoryRow(con, productId);

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE inventory SET quantity_in = quantity_in + ?, current_stock = current_stock + ? WHERE product_id = ?")) {
                    ps.setInt(1, rq);
                    ps.setInt(2, rq);
                    ps.setInt(3, productId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE products SET stock_quantity = stock_quantity + ?, status='Available' WHERE product_id = ?")) {
                    ps.setInt(1, rq);
                    ps.setInt(2, productId);
                    ps.executeUpdate();
                }

                insertInventoryMovement(con, productId, "IN", rq, "RETURN SaleID " + saleId);
                insertAuditLog(con, userId, "RETURN", "Returned product_id=" + productId + " qty=" + rq + " refund=" + moneyFmt.format(refund));

                con.commit();

                JOptionPane.showMessageDialog(dashboard, "Return processed.\nRefund: " + moneyFmt.format(refund));
                loadProducts(searchTxt.getText().trim());
                // refresh table
                loadBtn.doClick();

            } catch (Exception ex) {
                try { if (con != null) con.rollback(); } catch (Exception ignore) {}
                JOptionPane.showMessageDialog(dashboard, "Return failed.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { if (con != null) con.setAutoCommit(true); } catch (Exception ignore) {}
                try { if (con != null) con.close(); } catch (Exception ignore) {}
            }
        });

        closeBtn.addActionListener(e -> dlg.dispose());

        dlg.setVisible(true);
    }

    private int getSaleIdByTrx(Connection con, String trx) throws SQLException {
        String sql = "SELECT sale_id FROM sales WHERE transaction_number = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trx);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    private int getReturnedQty(Connection con, int saleId, int productId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(quantity),0) FROM returns WHERE sale_id = ? AND product_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    // =========================
    // PURCHASES (NEW)
    // =========================
    private void openPurchaseDialog() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(root), "New Purchase (Stock-In)", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(820, 560);
        dlg.setLocationRelativeTo(dashboard);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // top: supplier + payment status
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints tc = new GridBagConstraints();
        tc.insets = new Insets(6, 6, 6, 6);
        tc.fill = GridBagConstraints.HORIZONTAL;
        tc.gridy = 0;

        JComboBox<SupplierItem> supplierCmb = new JComboBox<>();
        supplierCmb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loadSuppliersToCombo(supplierCmb);

        JComboBox<String> payStatusCmb = new JComboBox<>(new String[]{"Unpaid", "Partially Paid", "Paid"});
        payStatusCmb.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel hint = new JLabel("Tip: Select a product on the LEFT Products table, then click 'Add Selected Product'.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(new Color(120, 130, 145));

        tc.gridx = 0; tc.weightx = 0.2;
        top.add(labelMuted("Supplier:"), tc);
        tc.gridx = 1; tc.weightx = 0.5;
        top.add(supplierCmb, tc);

        tc.gridx = 2; tc.weightx = 0.2;
        top.add(labelMuted("Payment Status:"), tc);
        tc.gridx = 3; tc.weightx = 0.3;
        top.add(payStatusCmb, tc);

        tc.gridy++;
        tc.gridx = 0; tc.gridwidth = 4; tc.weightx = 1;
        top.add(hint, tc);

        // purchase items table
        DefaultTableModel pm = new DefaultTableModel(
                new Object[]{"ProductID", "Barcode", "Product", "Qty", "Cost Price", "Subtotal"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        JTable pt = new JTable(pm);
        stylizeTable(pt);

        // hide ProductID
        pt.getColumnModel().getColumn(0).setMinWidth(0);
        pt.getColumnModel().getColumn(0).setMaxWidth(0);
        pt.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane sp = new JScrollPane(pt);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));

        // bottom controls
        JPanel bottom = new JPanel(new GridBagLayout());
        bottom.setBorder(new EmptyBorder(10, 0, 0, 0));
        GridBagConstraints bc = new GridBagConstraints();
        bc.insets = new Insets(6, 6, 6, 6);
        bc.fill = GridBagConstraints.HORIZONTAL;
        bc.gridy = 0;

        JTextField qty = new JTextField("1", 6);
        stylizeField(qty);
        JTextField cost = new JTextField("", 8);
        stylizeField(cost);

        JButton addSelectedBtn = new PrimaryButton("Add Selected Product", BLUE, BLUE_DARK);
        JButton removeLineBtn = new OutlineButton("Remove Line");
        JButton clearBtn = new OutlineButton("Clear Items");

        JLabel totalLbl = new JLabel("Total: 0.00 PHP");
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLbl.setForeground(TEXT);

        JButton savePurchaseBtn = new PrimaryButton("Save Purchase", BLUE, BLUE_DARK);
        JButton closeBtn = new OutlineButton("Close");

        bc.gridx = 0; bc.weightx = 0.2;
        bottom.add(labelMuted("Qty:"), bc);
        bc.gridx = 1; bc.weightx = 0.2;
        bottom.add(qty, bc);

        bc.gridx = 2; bc.weightx = 0.2;
        bottom.add(labelMuted("Cost Price:"), bc);
        bc.gridx = 3; bc.weightx = 0.2;
        bottom.add(cost, bc);

        bc.gridx = 4; bc.weightx = 0.4;
        bottom.add(addSelectedBtn, bc);

        bc.gridy++;
        bc.gridx = 0; bc.weightx = 0.25;
        bottom.add(removeLineBtn, bc);
        bc.gridx = 1; bc.weightx = 0.25;
        bottom.add(clearBtn, bc);
        bc.gridx = 2; bc.gridwidth = 2; bc.weightx = 0.5;
        bottom.add(totalLbl, bc);
        bc.gridwidth = 1;
        bc.gridx = 4; bc.weightx = 0.3;
        bottom.add(savePurchaseBtn, bc);

        bc.gridy++;
        bc.gridx = 4; bc.weightx = 0.2;
        bottom.add(closeBtn, bc);

        panel.add(top, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        dlg.setContentPane(panel);

        Runnable updateTotal = () -> {
            BigDecimal total = BigDecimal.ZERO;
            for (int i = 0; i < pm.getRowCount(); i++) {
                total = total.add(toBig(pm.getValueAt(i, 5)));
            }
            totalLbl.setText("Total: " + moneyFmt.format(total) + " PHP");
        };

        addSelectedBtn.addActionListener(e -> {
            int pr = (productsTbl == null) ? -1 : productsTbl.getSelectedRow();
            if (pr < 0) {
                JOptionPane.showMessageDialog(dashboard, "Select a product from the LEFT Products table first.");
                return;
            }

            int productId = toInt(productsModel.getValueAt(pr, 0));
            String barcode = String.valueOf(productsModel.getValueAt(pr, 1));
            String name = String.valueOf(productsModel.getValueAt(pr, 2));

            int q;
            try { q = Integer.parseInt(qty.getText().trim()); } catch (Exception ex) { q = 0; }
            if (q <= 0) { JOptionPane.showMessageDialog(dashboard, "Invalid qty."); return; }

            BigDecimal cp = parseMoney(cost.getText().trim());
            if (cp.compareTo(BigDecimal.ZERO) <= 0) {
                // if empty, try product.cost_price from DB
                cp = getProductCostPrice(productId);
                if (cp.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(dashboard, "Enter valid cost price.");
                    return;
                }
            }

            // merge if exists
            int existing = -1;
            for (int i = 0; i < pm.getRowCount(); i++) {
                if (toInt(pm.getValueAt(i, 0)) == productId) { existing = i; break; }
            }

            if (existing >= 0) {
                int oldQ = toInt(pm.getValueAt(existing, 3));
                int newQ = oldQ + q;
                BigDecimal sub = cp.multiply(BigDecimal.valueOf(newQ)).setScale(2, RoundingMode.HALF_UP);

                pm.setValueAt(newQ, existing, 3);
                pm.setValueAt(cp, existing, 4);
                pm.setValueAt(sub, existing, 5);
            } else {
                BigDecimal sub = cp.multiply(BigDecimal.valueOf(q)).setScale(2, RoundingMode.HALF_UP);
                pm.addRow(new Object[]{productId, barcode, name, q, cp, sub});
            }

            updateTotal.run();
        });

        removeLineBtn.addActionListener(e -> {
            int r = pt.getSelectedRow();
            if (r < 0) return;
            pm.removeRow(r);
            updateTotal.run();
        });

        clearBtn.addActionListener(e -> {
            pm.setRowCount(0);
            updateTotal.run();
        });

        savePurchaseBtn.addActionListener(e -> {
            if (pm.getRowCount() == 0) {
                JOptionPane.showMessageDialog(dashboard, "No purchase items.");
                return;
            }
            SupplierItem sup = (SupplierItem) supplierCmb.getSelectedItem();
            if (sup == null || sup.id <= 0) {
                JOptionPane.showMessageDialog(dashboard, "Select supplier.");
                return;
            }

            BigDecimal total = BigDecimal.ZERO;
            for (int i = 0; i < pm.getRowCount(); i++) total = total.add(toBig(pm.getValueAt(i, 5)));
            total = total.setScale(2, RoundingMode.HALF_UP);

            String payStatus = String.valueOf(payStatusCmb.getSelectedItem());

            int ok = JOptionPane.showConfirmDialog(dashboard,
                    "Save purchase?\nSupplier: " + sup.name + "\nTotal Cost: " + moneyFmt.format(total),
                    "Confirm Purchase", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;

            Connection con = null;
            try {
                con = DBConnection.getConnection();
                con.setAutoCommit(false);

                int purchaseId = insertPurchase(con, sup.id, userId, total, payStatus);

                for (int i = 0; i < pm.getRowCount(); i++) {
                    int productId = toInt(pm.getValueAt(i, 0));
                    int q = toInt(pm.getValueAt(i, 3));
                    BigDecimal cp = toBig(pm.getValueAt(i, 4));
                    BigDecimal sub = toBig(pm.getValueAt(i, 5));

                    insertPurchaseDetail(con, purchaseId, productId, q, cp, sub);

                    // stock-in
                    ensureInventoryRow(con, productId);

                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE inventory SET quantity_in = quantity_in + ?, current_stock = current_stock + ? WHERE product_id = ?")) {
                        ps.setInt(1, q);
                        ps.setInt(2, q);
                        ps.setInt(3, productId);
                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE products SET stock_quantity = stock_quantity + ?, cost_price = ?, status='Available' WHERE product_id = ?")) {
                        ps.setInt(1, q);
                        ps.setBigDecimal(2, cp);
                        ps.setInt(3, productId);
                        ps.executeUpdate();
                    }

                    insertInventoryMovement(con, productId, "IN", q, "PURCHASE #" + purchaseId);
                }

                insertAuditLog(con, userId, "PURCHASE", "Created purchase_id=" + purchaseId + " total=" + moneyFmt.format(total));

                con.commit();

                JOptionPane.showMessageDialog(dashboard, "Purchase saved.\nPurchase ID: " + purchaseId);
                loadProducts(searchTxt.getText().trim());
                dlg.dispose();

            } catch (Exception ex) {
                try { if (con != null) con.rollback(); } catch (Exception ignore) {}
                JOptionPane.showMessageDialog(dashboard, "Save failed.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { if (con != null) con.setAutoCommit(true); } catch (Exception ignore) {}
                try { if (con != null) con.close(); } catch (Exception ignore) {}
            }
        });

        closeBtn.addActionListener(e -> dlg.dispose());

        dlg.setVisible(true);
    }

    private void loadSuppliersToCombo(JComboBox<SupplierItem> cmb) {
        cmb.removeAllItems();
        cmb.addItem(new SupplierItem(0, "-- Select Supplier --"));
        String sql = "SELECT supplier_id, supplier_name FROM suppliers WHERE status='Active' ORDER BY supplier_name ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cmb.addItem(new SupplierItem(rs.getInt(1), rs.getString(2)));
            }
        } catch (Exception ex) {
            // ignore - just keep default
        }
    }

    private BigDecimal getProductCostPrice(int productId) {
        String sql = "SELECT cost_price FROM products WHERE product_id = ? LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1) == null ? BigDecimal.ZERO : rs.getBigDecimal(1);
            }
        } catch (Exception ignore) {}
        return BigDecimal.ZERO;
    }

    private int insertPurchase(Connection con, int supplierId, int userId, BigDecimal totalCost, String paymentStatus) throws SQLException {
        String sql =
                "INSERT INTO purchases (supplier_id, user_id, total_cost, payment_status) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, supplierId);
            ps.setInt(2, userId);
            ps.setBigDecimal(3, totalCost);
            ps.setString(4, paymentStatus);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Failed to insert purchase.");
    }

    private void insertPurchaseDetail(Connection con, int purchaseId, int productId, int qty,
                                      BigDecimal costPrice, BigDecimal subtotal) throws SQLException {
        String sql =
                "INSERT INTO purchase_details (purchase_id, product_id, quantity, cost_price, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, purchaseId);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            ps.setBigDecimal(4, costPrice);
            ps.setBigDecimal(5, subtotal);
            ps.executeUpdate();
        }
    }

    private static class SupplierItem {
        final int id;
        final String name;
        SupplierItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }

    // =========================
    // RECEIPT
    // =========================
    private void buildReceiptPreview(String trx, String receiptNo, String method, BigDecimal paid) {
        StringBuilder sb = new StringBuilder();

        sb.append("        POS SYSTEM RECEIPT\n");
        sb.append("================================\n");
        sb.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
        if (trx != null) sb.append("Transaction: ").append(trx).append("\n");
        if (receiptNo != null) sb.append("Receipt No: ").append(receiptNo).append("\n");
        sb.append("--------------------------------\n");

        for (int i = 0; i < cartModel.getRowCount(); i++) {
            String name = String.valueOf(cartModel.getValueAt(i, 2));
            int qty = toInt(cartModel.getValueAt(i, 4));
            BigDecimal line = toBig(cartModel.getValueAt(i, 5));

            sb.append(trimTo(name, 18)).append(" x").append(qty)
              .append("  ").append(moneyFmt.format(line)).append("\n");
        }

        sb.append("--------------------------------\n");
        sb.append("Subtotal:     ").append(moneyFmt.format(subtotal)).append("\n");
        sb.append("Discount:     ").append(moneyFmt.format(discount)).append("\n");
        sb.append("Tax:          ").append(moneyFmt.format(tax)).append("\n");
        sb.append("GRAND TOTAL:  ").append(moneyFmt.format(grandTotal)).append("\n");

        if (method != null) {
            sb.append("--------------------------------\n");
            sb.append("Payment: ").append(method).append("\n");
            if (paid != null) sb.append("Paid:    ").append(moneyFmt.format(paid)).append("\n");
            sb.append("Change:  ").append(changeLbl.getText()).append("\n");
        }

        sb.append("================================\n");
        sb.append("     Thank you! Come again.\n");

        receiptArea.setText(sb.toString());
    }

    private void printReceipt() {
        try {
            boolean ok = receiptArea.print();
            if (!ok) JOptionPane.showMessageDialog(dashboard, "Print cancelled.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dashboard, "Print failed.\n" + ex.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String trimTo(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max - 1) + "…";
    }

    // =========================
    // UI HELPERS
    // =========================
    private JLabel labelMuted(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(MUTED);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private void stylizeField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    private void stylizeTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(246, 248, 255));
        table.getTableHeader().setForeground(TEXT);
        table.setGridColor(new Color(235, 238, 245));
        table.setSelectionBackground(new Color(220, 232, 255));
        table.setSelectionForeground(TEXT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setReorderingAllowed(false);
    }

    private void stylizeMoneyLabel(JLabel lbl, boolean big) {
        lbl.setForeground(TEXT);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setFont(new Font("Segoe UI", big ? Font.BOLD : Font.PLAIN, big ? 14 : 13));
    }

    private void addTotalsRow(JPanel panel, GridBagConstraints t, String left, JLabel right) {
        JLabel l = new JLabel(left);
        l.setForeground(MUTED);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        t.gridx = 0; t.weightx = 0.55;
        panel.add(l, t);

        t.gridx = 1; t.weightx = 0.45;
        panel.add(right, t);

        t.gridy++;
    }

    // =========================
    // CONVERTERS
    // =========================
    private int toInt(Object v) {
        if (v == null) return 0;
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(String.valueOf(v)); } catch (Exception e) { return 0; }
    }

    private BigDecimal toBig(Object v) {
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof BigDecimal) return (BigDecimal) v;
        if (v instanceof Number) return BigDecimal.valueOf(((Number) v).doubleValue());
        try { return new BigDecimal(String.valueOf(v)); } catch (Exception e) { return BigDecimal.ZERO; }
    }

    private BigDecimal parseMoney(String s) {
        if (s == null || s.trim().isEmpty()) return BigDecimal.ZERO;
        try {
            s = s.replace(",", "");
            return new BigDecimal(s);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    // =========================
    // STATUS BADGE RENDERER
    // =========================
    private class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            String text = (value == null) ? "" : value.toString();
            BadgeLabel badge = new BadgeLabel(text);

            if (isSelected) {
                badge.setOpaque(true);
                badge.setBackground(new Color(220, 232, 255));
            } else {
                badge.setOpaque(false);
            }

            return badge;
        }
    }

    private class BadgeLabel extends JLabel {
        private final Color pillColor;

        public BadgeLabel(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setHorizontalAlignment(SwingConstants.CENTER);
            setForeground(Color.WHITE);

            if ("IN STOCK".equalsIgnoreCase(text)) pillColor = new Color(34, 197, 94);
            else if ("LOW STOCK".equalsIgnoreCase(text)) pillColor = new Color(245, 158, 11);
            else if ("OUT OF STOCK".equalsIgnoreCase(text)) pillColor = new Color(239, 68, 68);
            else pillColor = new Color(107, 114, 128);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(pillColor);
            g2.fillRoundRect(6, 6, w - 12, h - 12, 18, 18);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Insets getInsets() {
            return new Insets(6, 10, 6, 10);
        }
    }

    // =========================
    // CUSTOM COMPONENTS
    // =========================
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, radius, radius);

            g2.setColor(new Color(225, 230, 240));
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class PrimaryButton extends JButton {
        private final Color base;
        private final Color hover;
        private boolean isHover = false;

        public PrimaryButton(String text, Color base, Color hover) {
            super(text);
            this.base = base;
            this.hover = hover;
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHover = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(isEnabled() ? (isHover ? hover : base) : new Color(170, 175, 185));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class OutlineButton extends JButton {
        private boolean isHover = false;

        public OutlineButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(210, 215, 225)),
                    new EmptyBorder(9, 12, 9, 12)
            ));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setForeground(new Color(30, 30, 30));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHover = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isHover) {
                g2.setColor(new Color(245, 248, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}