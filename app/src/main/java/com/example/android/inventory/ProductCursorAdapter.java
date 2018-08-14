package com.example.android.inventory;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract.ProductEntry;

/**
 * Created by Hazem_Khaled on 2017-11-20.
 */

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, Context context, final Cursor cursor) {

        TextView productName_tv = (TextView) view.findViewById(R.id.item_name_tv);
        TextView productQuantity_tv = (TextView) view.findViewById(R.id.in_stock_tv);
        TextView productPrice_tv = (TextView) view.findViewById(R.id.price_tv);

        final int id=cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME));
        final Integer quantityValue = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        String quantity = quantityValue.toString();
        Integer priceValue = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE));
        String price = priceValue.toString();

        productName_tv.setText(name);
        productPrice_tv.setText(price + "$");
        productQuantity_tv.setText(quantity + " Piece ");

        final String mName = name;

        final Uri mUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

        Button buyBtn = (Button) view.findViewById(R.id.buy_button);
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tmpQuantity = quantityValue;
                ContentValues values = new ContentValues();

                if (tmpQuantity > 0) {
                    tmpQuantity--;
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, tmpQuantity);
                    view.getContext().getContentResolver().update(mUri, values, null, null);
                    view.getContext().getContentResolver().notifyChange(mUri, null);
                }
                else{
                    Toast.makeText(view.getContext(),R.string.out_of_stock_message,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}