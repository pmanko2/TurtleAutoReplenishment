package com.example.turtleautoreplenishment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
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
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		ViewGroup view = null;
		
		if(convertView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(context);
			view = (ViewGroup) inflater.inflate(R.layout.scanned_item_item, null);
		}
		else
		{
			view = (ViewGroup) convertView;
		}
		
		// set scanned item textview information
		TextView turtleID = (TextView) view.findViewById(R.id.order_turtle_id);
        TextView customerID = (TextView) view.findViewById(R.id.order_customer_id);
		TextView quantity = (TextView) view.findViewById(R.id.order_item_quantity);
		TextView type = (TextView) view.findViewById(R.id.replenishment_type);
        CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkBox);
		
		turtleID.setText(list.get(position).getTurtleProduct());
        customerID.setText(list.get(position).getCustomerProduct());
		quantity.setText(String.valueOf(list.get(position).getQuantity()));
		type.setText(list.get(position).getReplenishmentType());

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                {
                    toDelete.add(list.get(position));
                }
                else
                {
                   int indexToDelete = toDelete.indexOf(list.get(position));

                   if(indexToDelete != -1)
                       toDelete.remove(toDelete.indexOf(list.get(position)));
                }

            }
        });

        if(selected.containsKey(position))
            view.setBackgroundColor(Color.RED);
		
		return view;
	}

    public ArrayList<ScannedItem> getToDeleteList()
    {
        return this.toDelete;
    }

    public boolean isPositionSelected(int position)
    {
        return selected.containsKey(position);
    }

    public Set<Integer> getCurrentCheckedPosition()
    {
        return selected.keySet();
    }

    public void clearSelection()
    {
        selected.clear();
        notifyDataSetChanged();
    }

    public void removeSelection(int position)
    {
        selected.remove(position);
        notifyDataSetChanged();
    }

    public void setNewSelection(int position, boolean checked)
    {
        selected.put(position, checked);
        notifyDataSetChanged();
    }
}
