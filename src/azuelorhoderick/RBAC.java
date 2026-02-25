package azuelorhoderick;

import java.util.Locale;

public final class RBAC {

    private RBAC() {}

    public enum Feature {
        PRODUCTS,
        INVENTORY,
        POS,
        REPORTS,
        USERS,
        ABOUT,
        PRODUCTS_WRITE 
    }

    // Normalize role from DB
    public static String normalizeRole(String roleFromDb) {
        if (roleFromDb == null) return "";
        String r = roleFromDb.trim().toLowerCase(Locale.ROOT);

        if (r.contains("admin")) return "ADMIN";
        if (r.contains("cashier")) return "CASHIER";

        // inventory staff / staff
        if (r.contains("inventory")) return "INVENTORY_STAFF";
        if (r.equals("staff")) return "INVENTORY_STAFF";

        return roleFromDb.trim().toUpperCase(Locale.ROOT);
    }

    // Permission checker
    public static boolean canAccess(String roleFromDb, Feature feature) {
    String role = normalizeRole(roleFromDb);

    switch (role) {

        case "ADMIN":
            return true;

        case "INVENTORY_STAFF":
            return feature == Feature.PRODUCTS
                    || feature == Feature.PRODUCTS_WRITE   // ✅ can add/edit
                    || feature == Feature.INVENTORY
                    || feature == Feature.REPORTS
                    || feature == Feature.ABOUT;

        case "CASHIER":
            // ✅ cashier can view products, but NOT write
            return feature == Feature.PRODUCTS
                    || feature == Feature.POS
                    || feature == Feature.ABOUT;

        default:
            return false;
    }
}
}