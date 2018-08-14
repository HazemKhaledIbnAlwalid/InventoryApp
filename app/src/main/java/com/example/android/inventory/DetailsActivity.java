package com.example.android.inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract.ProductEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private static final int PICK_IMAGE_REQUEST = 0;

    private Uri mCurrentUri;

    //Input fields
    private EditText mNameEditText;

    private EditText mPriceEditText;

    private EditText mQuantityEditText;

    private EditText mSupplierEditText;

    private ImageView mPhotoImageView;

    private Spinner mSaleSpinner;

    private Spinner mShipmentSpinner;

    private String picturePath="";

    private int mSale ;

    private int mShipment;

    private EditText QuantityChange;

    private boolean mProductHasChange = false;

    private Integer quantityVal;

    private Cursor mCursor;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChange = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        Button orderBtn = (Button) findViewById(R.id.order_button);
        Button incBtn = (Button) findViewById(R.id.increase_quantity_button);
        Button decBtn = (Button) findViewById(R.id.decrease_quantity_button);
        QuantityChange = (EditText) findViewById(R.id.changing_value_etv);

        mSale = ProductEntry.SALESUNKOWN;
        mShipment = ProductEntry.SHIPMENTUNKOWN;

        if (mCurrentUri == null) {
            setTitle(R.string.title_new_product);
            QuantityChange.setVisibility(View.INVISIBLE);
            incBtn.setVisibility(View.INVISIBLE);
            decBtn.setVisibility(View.INVISIBLE);
            orderBtn.setVisibility(View.INVISIBLE);

        }
        else {
            setTitle(R.string.title_edit_product);
            QuantityChange.setVisibility(View.VISIBLE);
            incBtn.setVisibility(View.VISIBLE);
            decBtn.setVisibility(View.VISIBLE);
            orderBtn.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mSupplierEditText = (EditText) findViewById(R.id.edit_product_supplier);
        mPhotoImageView = (ImageView) findViewById(R.id.Photo_imageView);
        mSaleSpinner = (Spinner) findViewById(R.id.spinner_sale);
        mShipmentSpinner = (Spinner) findViewById(R.id.spinner_shipement);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mPhotoImageView.setOnTouchListener(mTouchListener);
        mSaleSpinner.setOnTouchListener(mTouchListener);
        mShipmentSpinner.setOnTouchListener(mTouchListener);

        setupSaleSpinner();
        setupShipmentSpinner();
    }

    private void setupSaleSpinner() {
        ArrayAdapter saleSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_sale_options, android.R.layout.simple_spinner_item);

        saleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mSaleSpinner.setAdapter(saleSpinnerAdapter);

        mSaleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.sale))) {
                        mSale = ProductEntry.SALE;
                    } else if (selection.equals(getString(R.string.no_sale))) {
                        mSale = ProductEntry.NOSALE;
                    } else {
                        mSale = ProductEntry.SALESUNKOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSale = ProductEntry.SALESUNKOWN;
            }
        });
    }

    private void setupShipmentSpinner() {
        ArrayAdapter shipSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_shipment_options, android.R.layout.simple_spinner_item);

        shipSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mShipmentSpinner.setAdapter(shipSpinnerAdapter);

        mShipmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.shipment))) {
                        mShipment = ProductEntry.SHIPMENTAVALIABLE;
                    } else if (selection.equals(getString(R.string.no_shipment))) {
                        mShipment = ProductEntry.SHIPMENTNOTAVALIABLE;
                    } else {
                        mShipment = ProductEntry.SHIPMENTUNKOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mShipment = ProductEntry.SHIPMENTUNKOWN;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() {

        String name = mNameEditText.getText().toString().trim();
        String price = mPriceEditText.getText().toString().trim();
        String quantity = mQuantityEditText.getText().toString().trim();
        String supplier = mSupplierEditText.getText().toString().trim();

        if (mCurrentUri == null &&
                TextUtils.isEmpty(name) && TextUtils.isEmpty(quantity) &&
                TextUtils.isEmpty(price) && TextUtils.isEmpty(supplier)
                && mSale == ProductEntry.SALESUNKOWN &&
                mShipment == ProductEntry.SHIPMENTUNKOWN) {
            return;
        }

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantity) &&
                TextUtils.isEmpty(price) || TextUtils.isEmpty(supplier) ||
                mSale == ProductEntry.SALESUNKOWN ||   mShipment == ProductEntry.SHIPMENTUNKOWN || TextUtils.isEmpty(picturePath)) {
            Toast.makeText(getApplicationContext(),R.string.message_for_null_values,Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        values.put(ProductEntry.COLUMN_PRODUCT_PICTURE, picturePath);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplier);
        values.put(ProductEntry.COLUMN_PRODUCT_SHIPMENT, mShipment);
        values.put(ProductEntry.COLUMN_PRODUCT_SALE, mSale);

        int pricenum = 0;
        if (!TextUtils.isEmpty(price)) {
            pricenum = Integer.parseInt(price);
        }
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, pricenum);

        int quantnum = 0;
        if (!TextUtils.isEmpty(quantity)) {
            quantnum = Integer.parseInt(quantity);
        }
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        if (mCurrentUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.details_insert_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.details_insert_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.details_update_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.details_update_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void deleteProduct() {
        if (mCurrentUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);

            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.details_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.details_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    public void onClickOrder(View view) {
        // get supplier name from database
        int supplierColumnIndex = mCursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
        String supplierName=mCursor.getString(supplierColumnIndex);

        //replacaAll("\\s","") remove all white spaces in the string
        String address =supplierName.replaceAll("\\s", "") + "@gmail.com";
        Intent intent = new Intent(Intent.ACTION_SENDTO);

        intent.setData(Uri.parse("mailto:" + address));
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_SUBJECT, R.string.Messege_Content + mNameEditText.getText().toString());
        intent.putExtra(Intent.EXTRA_TEXT, R.string.Messege_Content);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void onClickIncrease(View view) {
        if(mQuantityEditText.getText().toString().equals(""))
            return;

        if (!TextUtils.isEmpty(mQuantityEditText.getText().toString())) {
            quantityVal = Integer.parseInt(mQuantityEditText.getText().toString());
            if(!TextUtils.isEmpty(QuantityChange.getText().toString())){
                Integer incVal = Integer.parseInt(QuantityChange.getText().toString());
                Integer val = quantityVal + incVal;
                mQuantityEditText.setText(val.toString());
            }
        }
    }

    public void onClickDecrease(View view) {
        if(mQuantityEditText.getText().toString().equals(""))
            return;

        if (!TextUtils.isEmpty(mQuantityEditText.getText().toString())) {
            quantityVal = Integer.parseInt(mQuantityEditText.getText().toString());
            if(!TextUtils.isEmpty(QuantityChange.getText().toString())){
                Integer decVal = Integer.parseInt(QuantityChange.getText().toString());
                Integer val = quantityVal - decVal;
                if (decVal <= quantityVal)
                    mQuantityEditText.setText(val.toString());
                else
                    Toast.makeText(getApplicationContext(), R.string.invalid_decrease_message, Toast.LENGTH_LONG).show();
            }
        }
    }

    // image handling
    public void onClickBrowse(View view){
        openImageSelector();
    }

    public void openImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                Uri mUri = resultData.getData();
                picturePath=mUri.toString();
                Log.i(LOG_TAG, "Uri: " + mUri.toString());

                mPhotoImageView.setImageBitmap(getBitmapFromUri(mUri));
            }
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = mPhotoImageView.getWidth();
        int targetH = mPhotoImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }
    //---------------------------------------

    // confirm Message
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setMessage(R.string.unsaved_changes_dialog_msg);
        mBuilder.setPositiveButton(R.string.discard, discardButtonClickListener);
        mBuilder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (dialogInterface != null)
                    dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChange) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    //-------------------------------------------------------

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PICTURE,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_SHIPMENT,
                ProductEntry.COLUMN_PRODUCT_SALE,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY};

        return new CursorLoader(this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            mCursor=cursor;
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int shipmentColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SHIPMENT);
            int saleColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SALE);
            int photoColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PICTURE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String photo = cursor.getString(photoColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int shipment = cursor.getInt(shipmentColumnIndex);
            int sale = cursor.getInt(saleColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mQuantityEditText.setText(Integer.toString(quantity));
            mPriceEditText.setText(Integer.toString(price));
            mSupplierEditText.setText(supplier);
            mPhotoImageView.setImageURI(Uri.parse(photo));

            switch (sale) {
                case ProductEntry.SALE:
                    mSaleSpinner.setSelection(1);
                    break;
                case ProductEntry.NOSALE:
                    mSaleSpinner.setSelection(2);
                    break;
                default:
                    mSaleSpinner.setSelection(0);
                    break;
            }
            switch (shipment) {
                case ProductEntry.SHIPMENTAVALIABLE:
                    mShipmentSpinner.setSelection(1);
                    break;
                case ProductEntry.SHIPMENTNOTAVALIABLE:
                    mShipmentSpinner.setSelection(2);
                    break;
                default:
                    mShipmentSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mSupplierEditText.setText("");
        mSaleSpinner.setSelection(0);
        mShipmentSpinner.setSelection(0);
        mPhotoImageView.setImageResource(0);
    }
}