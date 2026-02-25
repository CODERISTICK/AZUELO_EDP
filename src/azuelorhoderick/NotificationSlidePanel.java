/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package azuelorhoderick;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.SwingUtilities;

public class NotificationSlidePanel extends JPanel {

    // Theme
    private final Color BLUE = new Color(38, 60, 255);
    private final Color BLUE_DARK = new Color(25, 40, 190);
    private final Color BG = new Color(245, 247, 252);
    private final Color CARD = Color.WHITE;
    private final Color BORDER = new Color(220, 225, 235);
    private final Color TEXT = new Color(25, 25, 25);
    private final Color MUTED = new Color(110, 110, 110);

    private JLabel unreadCountLbl;
    private JButton closeBtn;

    private JButton tabSystemBtn;
    private JButton tabMessagesBtn;
    private JButton tabRequestsBtn;

    private JPanel listContainer;
    private JScrollPane scroll;

    private JButton composeBtn;

    private String activeTab = "MESSAGES";

    // Context
    private final int currentUserId;
    private final String currentRole;
    private final boolean isAdmin;
    private final NotificationDAO dao;

    public NotificationSlidePanel(int userId, String role, NotificationDAO dao) {
        this.currentUserId = userId;
        this.currentRole = (role == null ? "STAFF" : role);
        this.isAdmin = this.currentRole.toUpperCase().contains("ADMIN");
        this.dao = dao;

        setOpaque(true);
        setBackground(BG);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        showTab("MESSAGES");
        refreshFromDB();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE_DARK);
        header.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel title = new JLabel("Notifications");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        unreadCountLbl = pillLabel("0", new Color(255, 77, 77), Color.WHITE);
        unreadCountLbl.setToolTipText("Unread notifications");

        closeBtn = new JButton("✕");
        closeBtn.setFocusable(false);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        closeBtn.setContentAreaFilled(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        right.add(iconText("🔔", Color.WHITE));
        right.add(unreadCountLbl);
        right.add(closeBtn);

        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel tabs = new JPanel(new GridLayout(1, 3, 8, 0));
        tabs.setOpaque(false);

        tabSystemBtn = tabButton("System Alerts");
        tabMessagesBtn = tabButton("Admin Messages");
        tabRequestsBtn = tabButton("Requests");

        tabs.add(tabSystemBtn);
        tabs.add(tabMessagesBtn);
        tabs.add(tabRequestsBtn);

        tabSystemBtn.addActionListener(e -> showTab("SYSTEM"));
        tabMessagesBtn.addActionListener(e -> showTab("MESSAGES"));
        tabRequestsBtn.addActionListener(e -> showTab("REQUESTS"));

        listContainer = new JPanel();
        listContainer.setOpaque(false);
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));

        scroll = new JScrollPane(listContainer);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        body.add(tabs, BorderLayout.NORTH);

        JPanel listWrap = new JPanel(new BorderLayout());
        listWrap.setOpaque(false);
        listWrap.setBorder(new EmptyBorder(10, 0, 0, 0));
        listWrap.add(scroll, BorderLayout.CENTER);

        body.add(listWrap, BorderLayout.CENTER);
        return body;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG);
        footer.setBorder(new EmptyBorder(10, 12, 12, 12));

        JLabel tip = new JLabel(isAdmin
                ? "Admin: You can send messages and handle requests."
                : "You can send a request to Admin (ex: password reset).");
        tip.setForeground(MUTED);
        tip.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        composeBtn = primaryButton(isAdmin ? "Send Notification" : "New Request");
        composeBtn.addActionListener(e -> {
            if (isAdmin) showComposeAdmin();
            else showComposeRequest();
        });

        footer.add(tip, BorderLayout.WEST);
        footer.add(composeBtn, BorderLayout.EAST);

        return footer;
    }

    private JButton tabButton(String text) {
    JButton b = new JButton(text);
    b.setFocusable(false);
    b.setFont(new Font("Segoe UI", Font.BOLD, 12));
    b.setBackground(Color.WHITE);
    b.setForeground(TEXT);
    b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
    ));
    b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return b;
}

    private void highlightTabs() {
        JButton[] all = { tabSystemBtn, tabMessagesBtn, tabRequestsBtn };
        for (JButton b : all) {
            b.setBackground(Color.WHITE);
            b.setForeground(TEXT);
        }

        JButton active =
                activeTab.equals("SYSTEM") ? tabSystemBtn :
                activeTab.equals("REQUESTS") ? tabRequestsBtn : tabMessagesBtn;

        active.setBackground(BLUE);
        active.setForeground(Color.WHITE);
    }

    public void showTab(String tab) {
        this.activeTab = tab;
        highlightTabs();

        // Footer button behavior:
        if (isAdmin) {
            composeBtn.setText("Send Notification");
            composeBtn.setVisible(tab.equals("MESSAGES") || tab.equals("SYSTEM"));
        } else {
            composeBtn.setText("New Request");
            composeBtn.setVisible(tab.equals("REQUESTS"));
        }

        refreshFromDB();
    }

    public void refreshFromDB() {
        listContainer.removeAll();

        try {
            int unread = dao.countUnreadForUser(currentUserId, currentRole);
            setUnreadCount(unread);

            List<Notification> list = dao.loadForUserByTab(currentUserId, currentRole, activeTab);

            if (list.isEmpty()) {
                listContainer.add(emptyState("No notifications here."));
            } else {
                for (Notification n : list) {
                    listContainer.add(buildCard(n));
                    listContainer.add(Box.createVerticalStrut(10));
                }
            }
        } catch (SQLException ex) {
            listContainer.add(emptyState("DB Error: " + ex.getMessage()));
        }

        listContainer.add(Box.createVerticalGlue());
        revalidate();
        repaint();
    }

    public void setUnreadCount(int count) {
        unreadCountLbl.setText(String.valueOf(Math.max(0, count)));
        unreadCountLbl.setVisible(count > 0);
    }

    public JButton getCloseButton() {
        return closeBtn;
    }

    // ---------------- Cards ----------------
    private JComponent emptyState(String text) {
    JPanel p = new JPanel(new GridBagLayout());
    p.setOpaque(false);

    JLabel l = new JLabel(text);
    l.setForeground(MUTED);
    l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    p.add(l);

    p.setBorder(new EmptyBorder(40, 10, 40, 10));
    return p;
}

    private JPanel buildCard(Notification n) {
        boolean unread = n.isUnread();
        String time = formatTime(n.createdAt);

        String heading;
        if ("REQUEST".equalsIgnoreCase(n.type)) heading = "Request";
        else if ("SYSTEM".equalsIgnoreCase(n.type) || "ALERT".equalsIgnoreCase(n.type)) heading = "System Alert";
        else heading = "From Admin";

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));

        // Top
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel t = new JLabel(heading + " • " + (n.title == null ? "" : n.title));
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(TEXT);

        JLabel timeLbl = new JLabel(time);
        timeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLbl.setForeground(MUTED);

        top.add(t, BorderLayout.WEST);
        top.add(timeLbl, BorderLayout.EAST);

        // Message
        JTextArea msg = new JTextArea(n.message == null ? "" : n.message);
        msg.setWrapStyleWord(true);
        msg.setLineWrap(true);
        msg.setEditable(false);
        msg.setOpaque(false);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msg.setForeground(TEXT);

        // Bottom
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        Color prBg = "IMPORTANT".equalsIgnoreCase(n.priority) ? new Color(255, 191, 0) : new Color(230, 230, 230);
        Color prFg = "IMPORTANT".equalsIgnoreCase(n.priority) ? new Color(40, 40, 40) : TEXT;

        JLabel pr = pillLabel(n.priority == null ? "NORMAL" : n.priority, prBg, prFg);

        JLabel unreadDot = new JLabel("●");
        unreadDot.setForeground(unread ? BLUE : new Color(200, 200, 200));
        unreadDot.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton action = secondaryButton(unread ? "Mark as Read" : "Close");
        action.addActionListener(e -> {
    if (unread) {
        try {
            dao.markRead(n.id);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to mark as read: " + ex.getMessage());
        }
    }

    refreshFromDB();

    // refresh bell badge on Dashboard (Java 8 safe)
    java.awt.Window win = SwingUtilities.getWindowAncestor(this);
    if (win instanceof azuelorhoderick.Screens.Dashboard) {
        azuelorhoderick.Screens.Dashboard d = (azuelorhoderick.Screens.Dashboard) win;
        d.refreshNotifBadge();
    }
});

        bottom.add(unreadDot, BorderLayout.WEST);
        bottom.add(pr, BorderLayout.CENTER);

        JPanel br = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        br.setOpaque(false);
        br.add(action);
        bottom.add(br, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);

        JPanel mid = new JPanel(new BorderLayout());
        mid.setOpaque(false);
        mid.setBorder(new EmptyBorder(8, 0, 8, 0));
        mid.add(msg, BorderLayout.CENTER);
        card.add(mid, BorderLayout.CENTER);

        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    // ---------------- Compose dialogs ----------------
    private void showComposeAdmin() {
        // Admin can send to ROLE (recommended) or to specific user id
        String[] sendModes = {"Send to Role", "Send to User ID"};
        JComboBox<String> mode = new JComboBox<>(sendModes);

        String[] roles = {"STAFF", "CASHIER", "ADMIN"};
        JComboBox<String> roleCmb = new JComboBox<>(roles);

        JTextField userIdTxt = new JTextField();
        userIdTxt.setEnabled(false);

        mode.addActionListener(e -> {
            boolean toUser = mode.getSelectedIndex() == 1;
            userIdTxt.setEnabled(toUser);
            roleCmb.setEnabled(!toUser);
        });

        JTextField title = new JTextField("Low Stock Alert");
        JTextArea area = new JTextArea(6, 30);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setText("The stock for Chocolate Bar is low, please restock today.");

        String[] types = {"INFO", "ALERT", "SYSTEM"};
        JComboBox<String> typeCmb = new JComboBox<>(types);

        String[] pr = {"NORMAL", "IMPORTANT"};
        JComboBox<String> prCmb = new JComboBox<>(pr);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx=0; c.gridy=0; p.add(new JLabel("Send Mode:"), c);
        c.gridx=1; p.add(mode, c);

        c.gridx=0; c.gridy=1; p.add(new JLabel("Role:"), c);
        c.gridx=1; p.add(roleCmb, c);

        c.gridx=0; c.gridy=2; p.add(new JLabel("User ID:"), c);
        c.gridx=1; p.add(userIdTxt, c);

        c.gridx=0; c.gridy=3; p.add(new JLabel("Type:"), c);
        c.gridx=1; p.add(typeCmb, c);

        c.gridx=0; c.gridy=4; p.add(new JLabel("Priority:"), c);
        c.gridx=1; p.add(prCmb, c);

        c.gridx=0; c.gridy=5; p.add(new JLabel("Title:"), c);
        c.gridx=1; p.add(title, c);

        c.gridx=0; c.gridy=6; c.anchor=GridBagConstraints.NORTH;
        p.add(new JLabel("Message:"), c);
        c.gridx=1; c.fill = GridBagConstraints.BOTH; c.weightx=1; c.weighty=1;
        p.add(new JScrollPane(area), c);

        int ok = JOptionPane.showConfirmDialog(this, p, "Send Notification", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            String t = title.getText().trim();
            String m = area.getText().trim();
            String type = (String) typeCmb.getSelectedItem();
            String pri = (String) prCmb.getSelectedItem();

            if (t.isEmpty() || m.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and Message are required.");
                return;
            }

            if (mode.getSelectedIndex() == 0) {
                dao.sendToRole(currentUserId, (String) roleCmb.getSelectedItem(), t, m, type, pri);
            } else {
                int rid;
                try { rid = Integer.parseInt(userIdTxt.getText().trim()); }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid User ID.");
                    return;
                }
                dao.sendToUser(currentUserId, rid, t, m, type, pri);
            }

            JOptionPane.showMessageDialog(this, "Notification sent!");
            refreshFromDB();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Send failed: " + ex.getMessage());
        }
    }

    private void showComposeRequest() {
        String[] categories = {"Password Reset", "Void Transaction", "Access Request", "Change Price Approval"};
        JComboBox<String> cat = new JComboBox<>(categories);

        JTextArea area = new JTextArea(6, 30);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        String[] pr = {"NORMAL", "IMPORTANT"};
        JComboBox<String> prCmb = new JComboBox<>(pr);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        JPanel top = new JPanel(new GridLayout(1, 2, 8, 0));
        top.add(cat);
        top.add(prCmb);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);

        int ok = JOptionPane.showConfirmDialog(this, p, "New Request to Admin", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        String title = "Request: " + cat.getSelectedItem();
        String msg = area.getText().trim();
        if (msg.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type your request message.");
            return;
        }

        try {
            dao.requestToAdmin(currentUserId, title, msg, (String) prCmb.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Request sent to Admin!");
            refreshFromDB();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Request failed: " + ex.getMessage());
        }
    }

    // ---------------- UI helpers ----------------
    private JLabel pillLabel(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setOpaque(true);
        l.setBackground(bg);
        l.setForeground(fg);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setBorder(new EmptyBorder(4, 10, 4, 10));
        return l;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFocusable(false);
        b.setBackground(BLUE);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFocusable(false);
        b.setBackground(new Color(235, 238, 245));
        b.setForeground(TEXT);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel iconText(String s, Color color) {
        JLabel l = new JLabel(s);
        l.setForeground(color);
        l.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        return l;
    }

    private String formatTime(java.sql.Timestamp ts) {
        if (ts == null) return "—";
        long diff = System.currentTimeMillis() - ts.getTime();
        if (diff < 60_000) return "Just now";
        if (diff < 3_600_000) return (diff / 60_000) + " mins ago";
        return new SimpleDateFormat("MMM d, h:mm a").format(new Date(ts.getTime()));
    }
}
