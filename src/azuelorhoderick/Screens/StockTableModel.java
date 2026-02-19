package azuelorhoderick.Screens;

import javax.swing.table.DefaultTableModel;

public class StockTableModel {

    public static DefaultTableModel model = new DefaultTableModel(
        new String[]{
            "Barcode",
            "Product ID",
            "Product Name",
            "Category",
            "Supplier",
            "Batch No",
            "MFG Date",
            "EXP Date",
            "Quantity",
            "Unit Cost",
            "Selling Price",
            "Storage",
            "Stock Status",
            "Stock Date",
            "Stocked By",
            "Remarks"
        },
        0
    );
}
