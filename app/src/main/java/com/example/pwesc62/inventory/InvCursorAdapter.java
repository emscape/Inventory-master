package com.example.pwesc62.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pwesc62.inventory.data.InvContract;
import com.example.pwesc62.inventory.data.InvContract.InvEntry;

/**
 * {@link InvCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class InvCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link InvCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InvCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        //Find views
        TextView tvName =  view.findViewById(R.id.name);
        TextView tvSummary =  view.findViewById(R.id.summary);
        final TextView tvQuantity = view.findViewById(R.id.quantity);
        final TextView soldButton = view.findViewById(R.id.sale_button);
        final TextView saleTextView =  view.findViewById(R.id.sale_view);

        //Extract properties from cursor
        int idColumnIndex = cursor.getColumnIndex(InvEntry. _ID);
        final String name = cursor.getString(cursor.getColumnIndexOrThrow(InvEntry.COLUMN_PRODUCT_NAME));
        final String summary = cursor.getString(cursor.getColumnIndexOrThrow(InvEntry.COLUMN_PRODUCT_CREATOR));
        final int quantity = cursor.getInt(cursor.getColumnIndex(InvEntry.COLUMN_QUANTITY));
        String quantityString =  "Quantity Remaining: " + Integer.toString(quantity);
        final double price = cursor.getDouble(cursor.getColumnIndexOrThrow(InvEntry.COLUMN_PRICE));
        final String supplierString = cursor.getString(cursor.getColumnIndexOrThrow(InvEntry.COLUMN_SUPPLIER_NAME));
        final String supplierPhoneString = cursor.getString(cursor.getColumnIndexOrThrow(InvEntry.COLUMN_SUPPLIER_PHONE_NUMBER));

        String priceString = Double.toString(price);
        Log.i(priceString, "Greater than 0");

        //Set text from cursor to views
        tvName.setText(name);
        tvSummary.setText(summary);
        tvQuantity.setText(quantityString);
        saleTextView.setText(cursor.getString(idColumnIndex));

        //set up Click Listener on the soldButton
        soldButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //get current quantity from TextView
                int updateQuantity = quantity;
                if (updateQuantity > 0) {
                    updateQuantity = updateQuantity - 1;
                    tvQuantity.setText(Integer.toString(updateQuantity));
                    //get id from view
                    long id_number = Integer.parseInt(saleTextView.getText().toString());
                    Uri itemSelected = ContentUris.withAppendedId(InvEntry.CONTENT_URI, id_number);
                    ContentValues values = new ContentValues();
                    values.put(InvEntry.COLUMN_QUANTITY, updateQuantity);
                    values.put(InvEntry.COLUMN_PRODUCT_NAME, name);
                    values.put(InvEntry.COLUMN_PRODUCT_CREATOR, summary);
                    values.put(InvEntry.COLUMN_PRICE, price);
                    values.put(InvEntry.COLUMN_SUPPLIER_NAME, supplierString);
                    values.put(InvEntry.COLUMN_SUPPLIER_NAME, supplierPhoneString);
                    //update database
                    int rowsAffected = context.getContentResolver().update(itemSelected, values, null, null);
                    //confirm whether quantity was updated or not
                    if (rowsAffected == 0) {
                        Toast.makeText(context, R.string.quantity_update_error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, R.string.quantity_updated, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, R.string.sale_not_possible, Toast.LENGTH_SHORT).show();
                }
            }

        });

    }
}