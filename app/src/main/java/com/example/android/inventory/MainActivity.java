package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.widget.AdapterView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract.ProductEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int PET_LOADER=0;

    ProductCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView productListView=(ListView)findViewById(R.id.product_ListView);

        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        adapter=new ProductCursorAdapter(this,null);
        productListView.setAdapter(adapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,DetailsActivity.class);

                Uri mCurrentUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI,id);

                intent.setData(mCurrentUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PET_LOADER,null,this);
    }

    private void insertProduct() {

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "HeadPhoones");
        values.put(ProductEntry.COLUMN_PRODUCT_PICTURE, "C:");
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, "Beats");
        values.put(ProductEntry.COLUMN_PRODUCT_SHIPMENT, 1);
        values.put(ProductEntry.COLUMN_PRODUCT_SALE, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 400);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 50);

        Uri mUri=getContentResolver().insert(ProductEntry.CONTENT_URI,values);
    }


    private void deleteAllProducts() {

        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI,null,null);
        if(rowsDeleted != -1)
            Toast.makeText(getApplicationContext(),R.string.Delete_All_Data_Message,Toast.LENGTH_LONG).show();
    }

    public void onClickAddFloatingButton(View view){
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;

            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY};

        return new CursorLoader(this,ProductEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
