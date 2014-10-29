package com.example.turtleautoreplenishment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turtleautoreplenishment.databaseservices.ScannedItemDataSource;
import com.example.turtleautoreplenishment.webservices.HttpClient;
import com.example.turtleautoreplenishment.webservices.HttpDataDelegate;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class ScannedItemListAdapter extends ArrayAdapter<ScannedItem> implements HttpDataDelegate
{
	//private ArrayList<ScannedItem> list;
	private int resourceID;
	private Context context;
    private ArrayList<ScannedItem> toDelete;
    private HashMap<Integer, Boolean> selected = new HashMap<Integer, Boolean>();
    private boolean isAuto;
    private ScannedItemDataSource dataSource;
	
	public ScannedItemListAdapter(Context context, int resource,
                                    ScannedItemDataSource source)
	{
		super(context, resource, source.getAllItems());
		//this.list = source.getAllItems();
		this.resourceID = resource;
		this.context = context;
        toDelete = new ArrayList<ScannedItem>();
        isAuto = false;
        this.dataSource = source;
    }
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        ViewGroup view = null;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = (ViewGroup) inflater.inflate(R.layout.scanned_item_item, null);
        } else {
            view = (ViewGroup) convertView;
        }

        ArrayList<ScannedItem> list = dataSource.getAllItems();

        // set scanned item textview information
        TextView turtleID = (TextView) view.findViewById(R.id.order_turtle_id);
        TextView customerID = (TextView) view.findViewById(R.id.order_customer_id);
        TextView quantity = (TextView) view.findViewById(R.id.order_item_quantity);
        TextView type = (TextView) view.findViewById(R.id.replenishment_type);
        TextView max = (TextView) view.findViewById(R.id.list_max);
        TextView min = (TextView) view.findViewById(R.id.list_min);

        turtleID.setText(list.get(position).getTurtleProduct());
        customerID.setText(list.get(position).getCustomerProduct());
        quantity.setText(String.valueOf(list.get(position).getQuantity()));
        type.setText(list.get(position).getReplenishmentType());
        max.setText("Max: " + list.get(position).getMax());
        min.setText("Min: " + list.get(position).getMin());

        Integer key = list.get(position).getSqLiteID();

        if (selected.containsKey(key) && selected.get(key))
        {
            Log.i("Color change: ", "Highlighted");
            view.setBackgroundColor(Color.parseColor("#ff33b5e5"));
        }
        else
        {
            Log.i("Color change: ", "Original");
            view.setBackgroundColor(Color.parseColor("#ff000000"));
        }

		return view;
	}

    public void indicateChecked(int id, boolean checked)
    {
        Log.i("Indicating Checked: ", "Position: " + id + " " + checked);
        selected.put(id, checked);
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
        for(Integer toDelete : selected.keySet())
        {
            ScannedItem delete = dataSource.getScannedItemByID(toDelete);
            dataSource.deleteScannedItem(delete);
        }

        notifyDataSetChanged();
    }

    public void editSelected()
    {
        for(Integer keyToEdit : selected.keySet())
        {
            showEditItemDialog(dataSource.getScannedItemByID(keyToEdit));
        }
    }

    //handle json return for edit item request
    @Override
    public void handleAsyncDataReturn(Object ret)
    {
        if(ret instanceof JSONObject)
        {
            JSONObject returnJson = (JSONObject) ret;

            int success;

            try
            {
                success = returnJson.getInt("success");

                // if server was not able to confirm change, indicate with Toast
                if(success == 0)
                {
                    Toast.makeText(context, "Edit Item Was Unsuccessful", Toast.LENGTH_LONG).show();
                }
                else
                {
                    // if server indicates that item was successfully edited, indicate to user
                    Toast.makeText(context, "Successfully Edited Item", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    private void showEditItemDialog(final ScannedItem item)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Item");

        // set view of alert dialog to custom edit item alert view
        LayoutInflater inflater = LayoutInflater.from(context);
        final View editDialog = inflater.inflate(R.layout.edit_item_alert, null);
        builder.setView(editDialog);

        // set edittexts to values that are assigned to current item being edited
        final EditText maxEdit = (EditText) editDialog.findViewById(R.id.edit_max);
        final EditText minEdit = (EditText) editDialog.findViewById(R.id.edit_min);
        final EditText qtyEdit = (EditText) editDialog.findViewById(R.id.edit_quantity);
        final Button auto = (Button) editDialog.findViewById(R.id.auto_button);
        final Button manual = (Button) editDialog.findViewById(R.id.manual_button);

        maxEdit.setText(item.getMax());
        minEdit.setText(item.getMin());
        qtyEdit.setText("" + item.getQuantity());

        // on = manual, off = auto
        setupButtons(editDialog, item, auto, manual);

        // if user goes ahead with edit, send request for edit to server. wait for confirmation
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "edit_item"));
                params.add(new BasicNameValuePair("cust_no", "1")); //TODO change once everything connected
                params.add(new BasicNameValuePair("cust_prod", item.getCustomerProduct()));
                params.add(new BasicNameValuePair("new_max", maxEdit.getText().toString()));
                params.add(new BasicNameValuePair("new_min", minEdit.getText().toString()));

                //HttpClient.getInstance().getJsonInBackground("POST", ScannedItemListAdapter.this, params);


                item.setQuantity(Integer.parseInt(qtyEdit.getText().toString()));
                item.setMax(maxEdit.getText().toString());
                item.setMin(minEdit.getText().toString());

                String replenishmentType = (isAuto) ? "Auto" : "Manual";
                item.setReplenishmentType(replenishmentType);

                dataSource.updateItem(item);

                notifyDataSetChanged();

                Toast.makeText(context, "Product " + item.getCustomerProduct() +
                        " successfully edited", Toast.LENGTH_LONG).show();
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
