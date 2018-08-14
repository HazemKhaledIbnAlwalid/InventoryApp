package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventory.data.ProductContract.ProductEntry;

/**
 * Created by Hazem_Khaled on 2017-11-19.
 */

public class ProductProvider extends ContentProvider {

    public final static String LOG_TAG = ProductProvider.class.getSimpleName();

    private ProductDbHelper mDbHelper;

    private final static int PRODUCTS = 100;

    private final static int PRODUCTS_ID = 101;

    private final static UriMatcher mURI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //Query on whole table
        mURI_MATCHER.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_INVENTORY, PRODUCTS);

        // Query on column
        mURI_MATCHER.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_INVENTORY + "/#", PRODUCTS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    // Select statement
    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = mURI_MATCHER.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCTS_ID:

                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot Query Unkown Uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = mURI_MATCHER.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCTS_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final int match = mURI_MATCHER.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for this " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a valid name");
        }

        String supplier = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException("Product requires a valid supplier");
        }

        String image = values.getAsString(ProductEntry.COLUMN_PRODUCT_PICTURE);
        if (image == null) {
            throw new IllegalArgumentException("Product requires a valid image");
        }

        Integer price = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price == null || (price != null && price < 0)) {
            throw new IllegalArgumentException("Product requires a valid price");
        }

        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null || (quantity != null && quantity < 0)) {
            throw new IllegalArgumentException("Product requires a valid price");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = mURI_MATCHER.match(uri);
        int rowsDeleted;
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case PRODUCTS_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for this " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final int match = mURI_MATCHER.match(uri);

        switch (match) {
            case PRODUCTS:
                return updatePet(uri, values, selection, selectionArgs);

            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for this" + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a valid name");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PICTURE)) {
            String image = values.getAsString(ProductEntry.COLUMN_PRODUCT_PICTURE);
            if (image == null) {
                throw new IllegalArgumentException("Product requires a valid image");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_SUPPLIER)) {
            String supplier = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Product requires a valid supplier");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || (price != null && price < 0)) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement
        return rowsUpdated;

    }
}
