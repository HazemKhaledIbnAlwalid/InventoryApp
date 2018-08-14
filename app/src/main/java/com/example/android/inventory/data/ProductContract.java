package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Hazem_Khaled on 2017-11-19.
 */

public final class ProductContract {

    public final static String CONTENT_AUTHORITY = "com.example.android.inventory";

    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INVENTORY = "products";

    private ProductContract() {}

    //class which represents the table of prodcts
    public static final class ProductEntry implements BaseColumns {

        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;


        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PRODUCT_NAME = "Name";

        public final static String COLUMN_PRODUCT_PICTURE = "Picture";

        public final static String COLUMN_PRODUCT_SUPPLIER = "Supplier";

        public final static String COLUMN_PRODUCT_SHIPMENT = "Shipment";

        public final static String COLUMN_PRODUCT_SALE = "Sale";

        public final static String COLUMN_PRODUCT_PRICE = "Price$";

        public final static String COLUMN_PRODUCT_QUANTITY = "Quantity";

        public final static int SALESUNKOWN = -1;
        public final static int SALE = 1;
        public final static int NOSALE = 0;

        public final static int SHIPMENTUNKOWN = -1;
        public final static int SHIPMENTAVALIABLE = 1;
        public final static int SHIPMENTNOTAVALIABLE = 0;

    }
}
