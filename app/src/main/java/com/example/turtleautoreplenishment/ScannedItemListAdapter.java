package com.example.turtleautoreplenishment;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ScannedItemListAdapter extends ArrayAdapter<ScannedItem>
{
	private ArrayList<ScannedItem> list;
	private int resourceID;
	private Context context;
	
	
	public ScannedItemListAdapter(Context context, int resource, ArrayList<ScannedItem> itemList) 
	{
		super(context, resource, itemList);
		this.list = itemList;
		this.resourceID = resource;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
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
		
		turtleID.setText(list.get(position).getTurtleProduct());
        customerID.setText(list.get(position).getCustomerProduct());
		quantity.setText(String.valueOf(list.get(position).getQuantity()));
		type.setText(list.get(position).getReplenishmentType());
		
		return view;
	}
}
