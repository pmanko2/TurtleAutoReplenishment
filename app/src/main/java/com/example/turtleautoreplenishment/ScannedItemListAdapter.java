package com.example.turtleautoreplenishment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turtleautoreplenishment.webservices.HttpClient;
import com.example.turtleautoreplenishment.webservices.HttpDataDelegate;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class ScannedItemListAdapter extends ArrayAdapter<ScannedItem> implements HttpDataDelegate
{
	private ArrayList<ScannedItem> list;
	private int resourceID;
	private Context context;
    private ArrayList<ScannedItem> toDelete;
    private HashMap<Integer, Boolean> selected = new HashMap<Integer, Boolean>();
	
	public ScannedItemListAdapter(Context context, int resource, ArrayList<ScannedItem> itemList) 
	{
		super(context, resource, itemList);
		this.list = itemList;
		this.resourceID = resource;
		this.context = context;
        toDelete = new ArrayList<ScannedItem>();
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

        // set scanned item textview information
        TextView turtleID = (TextView) view.findViewById(R.id.order_turtle_id);
        TextView customerID = (TextView) view.findViewById(R.id.order_customer_id);
        TextView quantity = (TextView) view.findViewById(R.id.order_item_quantity);
        TextView type = (TextView) view.findViewById(R.id.replenishment_type);

        turtleID.setText(list.get(position).getTurtleProduct());
        customerID.setText(list.get(position).getCustomerProduct());
        quantity.setText(String.valueOf(list.get(position).getQuantity()));
        type.setText(list.get(position).getReplenishmentType());

        Integer curPosition = new Integer(position);

        if (selected.containsKey(curPosition) && selected.get(curPosition) == true)
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

    public void indicateChecked(int position, boolean checked)
    {
        Log.i("Indicating Checked: ", "Position: " + position + " " + checked);
        selected.put(new Integer(position), checked);
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
            list.remove(toDelete.intValue());
        }

        notifyDataSetChanged();
    }

    public void editSelected()
    {
        for(Integer toEdit : selected.keySet())
        {
            showEditItemDialog(toEdit.intValue());
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

    private void showEditItemDialog(final int itemToEdit)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Item");

        LayoutInflater inflater = LayoutInflater.from(context);

        builder.setView(inflater.inflate(R.layout.edit_item_alert, null));

        // if user goes ahead with edit, send request for edit to server. wait for confirmation
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "edit_item"));
                params.add(new BasicNameValuePair("cust_no", "1"));
                params.add(new BasicNameValuePair("cust_prod", "2"));   //TODO change once everything connected
                params.add(new BasicNameValuePair("new_max", "200"));
                params.add(new BasicNameValuePair("new_min", "10"));

                //HttpClient.getInstance().getJsonInBackground("POST", ScannedItemListAdapter.this, params);

                //TODO quantity has to be handled differently
                ScannedItem toEdit = list.get(itemToEdit);
                toEdit.setQuantity(17);
                toEdit.setReplenishmentType("Manual");
            }
        });

        // close alertdialog if user cancels edit request
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });

        builder.create().show();


    }
}
