package com.example.turtleautoreplenishment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turtleautoreplenishment.databaseservices.ScannedItemDataSource;
import com.example.turtleautoreplenishment.webservices.HttpClient;
import com.example.turtleautoreplenishment.webservices.HttpDataDelegate;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pawel on 11/6/2014.
 * Replaces arraylist adapter -- better integration with sqlite db
 */
public class ScannedItemCursorAdapter extends CursorAdapter
{
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<ScannedItem> toDelete;
    private HashMap<Integer, Boolean> selected = new HashMap<Integer, Boolean>();
    private boolean isAuto;
    private Cursor dbCursor;
    private ScannedItemDataSource dataSource;

    public ScannedItemCursorAdapter(Context context, Cursor c, int flags, ScannedItemDataSource source) {
        super(context, c, flags);
        inflater = LayoutInflater.from(context);
        this.context = context;
        toDelete = new ArrayList<ScannedItem>();
        isAuto = false;
        dbCursor = c;
        this.dataSource = source;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return inflater.inflate(R.layout.scanned_item_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView turtleID = (TextView) view.findViewById(R.id.order_turtle_id);
        TextView customerID = (TextView) view.findViewById(R.id.order_customer_id);
        TextView quantity = (TextView) view.findViewById(R.id.order_item_quantity);
        TextView type = (TextView) view.findViewById(R.id.replenishment_type);
        TextView max = (TextView) view.findViewById(R.id.list_max);
        TextView min = (TextView) view.findViewById(R.id.list_min);

        turtleID.setText(cursor.getString(2));
        customerID.setText(cursor.getString(1));
        quantity.setText(cursor.getString(6));
        type.setText(cursor.getString(3));
        max.setText(cursor.getString(7));
        min.setText(cursor.getString(8));

        int index = cursor.getColumnIndex("_id");
        Integer key = (int) cursor.getInt(index);

        // if this listview element is in selected hashmap, and hashmap indicates selected as true
        // background is light blue

        boolean keyInSelected = selected.containsKey(key);

        if(keyInSelected) {
            boolean selectedIsTrue = selected.get(key);
        }

        if (selected.containsKey(key) && selected.get(key))
        {
            Log.i("Color change: ", "Highlighted");
            view.setBackgroundColor(Color.parseColor("#ff33b5e5"));
        }
        else //otherwise background is original
        {
            Log.i("Color change: ", "Original");
            view.setBackgroundColor(Color.parseColor("#ff000000"));
        }
    }

    public void indicateChecked(long sqlID, boolean checked)
    {
        Log.i("Indicating Checked: ", "SQL ID: " + sqlID + " " + checked);
        selected.put((int)sqlID, checked);
        notifyDataSetChanged();
    }

    public void resetSelected()
    {
        Log.i("Contexual Menu: ", "Selected items reset");
        selected.clear();
        notifyDataSetChanged();
    }

    public void deleteSelected()
    {
        int numDeleted = 0;

        for(Integer toDelete : selected.keySet())
        {
            dataSource.deleteScannedItem(toDelete);
            numDeleted++;
        }

        dbCursor.requery(); // I KNOW THIS IS DEPRECATED OK?
        notifyDataSetChanged();

        Toast.makeText(context, numDeleted + " items successfully deleted", Toast.LENGTH_LONG).show();
    }

    public void editSelected()
    {
        for(Integer keyToEdit : selected.keySet())
        {
            showEditItemDialog(dataSource.getScannedItemByID(keyToEdit), keyToEdit);
        }
    }

    private void showEditItemDialog(final ScannedItem item, final int sqlID)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Item");

        // set view of alert dialog to custom edit item alert view
        LayoutInflater inflater = LayoutInflater.from(context);
        final View editDialog = inflater.inflate(R.layout.edit_item_alert, null);
        builder.setView(editDialog);

        // set edittexts to values that are assigned to current item being edited
        final EditText qtyEdit = (EditText) editDialog.findViewById(R.id.edit_quantity);
        final Button auto = (Button) editDialog.findViewById(R.id.auto_button);
        final Button manual = (Button) editDialog.findViewById(R.id.manual_button);

        qtyEdit.setText("" + item.getQuantity());

        // on = manual, off = auto
        setupButtons(editDialog, item, auto, manual);

        // if user goes ahead with edit, send request for edit to server. wait for confirmation
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                int newQuantity = Integer.valueOf(qtyEdit.getText().toString());
                String newRepType = (isAuto) ? "Auto" : "Manual";

                if(newQuantity == item.getQuantity() && newRepType.equals(item.getReplenishmentType())) {
                    Toast.makeText(context, "Nothing to edit", Toast.LENGTH_LONG).show();
                }
                else
                {
                    item.setQuantity(newQuantity);
                    item.setReplenishmentType(newRepType);
                    dataSource.updateItem(item, item.getSqLiteID());

                    dbCursor.requery(); // YES ITS DEPRECATED IM WORKING ON IT
                    notifyDataSetChanged();

                    Toast.makeText(context, "Product " + item.getTurtleProduct() +
                            " successfully edited", Toast.LENGTH_LONG).show();
                }

            }
        });

        // close alertdialog if user cancels edit request
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builder.create().show();


    }

    // use this method to setup buttons to act as a switch for edit type functionality
    private void setupButtons(View editDialog, ScannedItem item, final Button auto, final Button manual)
    {
        boolean isManual = (item.getReplenishmentType().equals("Manual"));

        if(isManual) {
            manual.setBackgroundColor(Color.GRAY);
            isAuto = false;
        }
        else {
            auto.setBackgroundColor(Color.GRAY);
            isAuto = true;
        }

        //when user clicks auto we want to switch from manual button to auto button
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!isAuto)
                {
                    auto.setBackgroundColor(Color.GRAY);
                    manual.setBackgroundColor(Color.parseColor("#ff000000"));
                    isAuto = true;
                }
            }
        });

        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(isAuto)
                {
                    manual.setBackgroundColor(Color.GRAY);
                    auto.setBackgroundColor(Color.parseColor("#ff000000"));
                    isAuto = false;
                }
            }
        });
    }
}
