/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package azuelorhoderick;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    private final Connection conn;

    public NotificationDAO(Connection conn) {
        this.conn = conn;
    }

    // For bell badge count
    public int countUnreadForUser(int userId, String userRole) throws SQLException {
        String role = normalizeRole(userRole);

        String sql =
    "SELECT COUNT(*) AS c " +
    "FROM notifications " +
    "WHERE `status` = 'UNREAD' " +
    "AND ( `receiver_user_id` = ? " +
    "   OR (`receiver_user_id` IS NULL AND `receiver_role` = ?) )";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, role);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("c") : 0;
            }
        }
    }

    // Load list by tab
    public List<Notification> loadForUserByTab(int userId, String userRole, String tab) throws SQLException {
        String role = normalizeRole(userRole);

        // Tab filters:
        // SYSTEM: type IN ('SYSTEM','ALERT')
        // MESSAGES: type IN ('INFO','ALERT') but not REQUEST
        // REQUESTS: type='REQUEST'
        String typeWhere;
          switch (tab) {
    case "SYSTEM" -> typeWhere = "`type` IN ('SYSTEM','ALERT')";
    case "REQUESTS" -> typeWhere = "`type` = 'REQUEST'";
    default -> typeWhere = "`type` IN ('INFO','ALERT')";
}

       String sql =
    "SELECT `notification_id`, `sender_user_id`, `receiver_user_id`, `receiver_role`, " +
    "       `title`, `message`, `type`, `priority`, `status`, `created_at` " +
    "FROM notifications " +
    "WHERE ( `receiver_user_id` = ? " +
    "   OR (`receiver_user_id` IS NULL AND `receiver_role` = ?) ) " +
    "  AND " + typeWhere + " " +
    "ORDER BY `created_at` DESC " +
    "LIMIT 50";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, role);

            List<Notification> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification();
                    n.id = rs.getInt("notification_id");
                    int s = rs.getInt("sender_user_id");
                    n.senderUserId = rs.wasNull() ? null : s;

                    int ru = rs.getInt("receiver_user_id");
                    n.receiverUserId = rs.wasNull() ? null : ru;

                    n.receiverRole = rs.getString("receiver_role");
                    n.title = rs.getString("title");
                    n.message = rs.getString("message");
                    n.type = rs.getString("type");
                    n.priority = rs.getString("priority");
                    n.status = rs.getString("status");
                    n.createdAt = rs.getTimestamp("created_at");
                    list.add(n);
                }
            }
            return list;
        }
    }

    public void markRead(int notificationId) throws SQLException {
        String sql = "UPDATE notifications SET status='READ', read_at=NOW() WHERE notification_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        }
    }

    // Admin send message to a role
    public void sendToRole(int senderId, String receiverRole, String title, String message, String type, String priority) throws SQLException {
        String role = normalizeRole(receiverRole);
        String sql = """
            INSERT INTO notifications(sender_user_id, receiver_role, title, message, type, priority)
            VALUES(?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setString(2, role);
            ps.setString(3, title);
            ps.setString(4, message);
            ps.setString(5, safeType(type));
            ps.setString(6, safePriority(priority));
            ps.executeUpdate();
        }
    }

    // Admin send message to one user
    public void sendToUser(int senderId, int receiverUserId, String title, String message, String type, String priority) throws SQLException {
        String sql = """
            INSERT INTO notifications(sender_user_id, receiver_user_id, title, message, type, priority)
            VALUES(?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverUserId);
            ps.setString(3, title);
            ps.setString(4, message);
            ps.setString(5, safeType(type));
            ps.setString(6, safePriority(priority));
            ps.executeUpdate();
        }
    }

    // Staff/Cashier create request to admin
    public void requestToAdmin(int senderId, String title, String message, String priority) throws SQLException {
        String sql = """
            INSERT INTO notifications(sender_user_id, receiver_role, title, message, type, priority, status)
            VALUES(?, 'ADMIN', ?, ?, 'REQUEST', ?, 'UNREAD')
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setString(2, title);
            ps.setString(3, message);
            ps.setString(4, safePriority(priority));
            ps.executeUpdate();
        }
    }

    // ---- helpers ----
    private String safeType(String t) {
        if (t == null) return "INFO";
        t = t.toUpperCase().trim();
        return switch (t) {
            case "INFO", "ALERT", "REQUEST", "SYSTEM" -> t;
            default -> "INFO";
        };
    }

    private String safePriority(String p) {
        if (p == null) return "NORMAL";
        p = p.toUpperCase().trim();
        return p.equals("IMPORTANT") ? "IMPORTANT" : "NORMAL";
    }

    // Adjust this if your DB role strings are like "Inventory staff"
    public String normalizeRole(String role) {
        if (role == null) return "STAFF";
        role = role.trim().toUpperCase();

        if (role.contains("ADMIN")) return "ADMIN";
        if (role.contains("CASHIER")) return "CASHIER";
        if (role.contains("STAFF")) return "STAFF";

        return role; // fallback
    }
}
