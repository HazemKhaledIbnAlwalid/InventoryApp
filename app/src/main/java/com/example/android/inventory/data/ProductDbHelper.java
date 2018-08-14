package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory.data.ProductContract.ProductEntry;

/**
 * Created by Hazem_Khaled on 2017-11-19.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_Name="inventory.db";

    private final static Integer DATABASE_VERSION=1;

    private String SQL_CREATE_TABLE;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_Name, null, DATABASE_VERSION);
        SQL_CREATE_TABLE="CREATE TABLE " + ProductEntry.TABLE_NAME + "("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_PICTURE+ " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_SUPPLIER+ " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_SHIPMENT+ " TEXT, "
                + ProductEntry.COLUMN_PRODUCT_SALE + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0);";
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DROP_TABLE="DROP TABLE " + ProductEntry.TABLE_NAME;
        db.execSQL(SQL_DROP_TABLE);
        db.execSQL(SQL_CREATE_TABLE);
    }
}
