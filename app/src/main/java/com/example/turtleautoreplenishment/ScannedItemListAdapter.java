package com.example.turtleautoreplenishment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
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

public class ScannedItemListAdapter extends ArrayAdapter<ScannedItem>
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
        Drawable originalBackground = view.getBackground();

        turtleID.setText(list.get(position).getTurtleProduct());
        customerID.setText(list.get(position).getCustomerProduct());
        quantity.setText(String.valueOf(list.get(position).getQuantity()));
        type.setText(list.get(position).getReplenishmentType());

        Integer curPosition = new Integer(position);

        if (selected.containsKey(curPosition) && selected.get(curPosition) == true)
        {
            Log.i("Color change: ", "Red");
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

}
