package com.example.turtleautoreplenishment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomerListAdapter extends ArrayAdapter<Customer> implements Observer
{
	private ArrayList<Customer> list;
	private Context context;
	
	
	public CustomerListAdapter(Context context, int resource, ArrayList<Customer> customerList) 
	{
		super(context, resource, customerList);
		this.list = customerList;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewGroup view = null;
		
		if(convertView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(context);
			view = (ViewGroup) inflater.inflate(R.layout.customer_item, null);
		}
		else
		{
			view = (ViewGroup) convertView;
		}
		
		TextView name = (TextView) view.findViewById(R.id.customer_name);
		TextView address = (TextView) view.findViewById(R.id.customer_address);
		
		name.setText(list.get(position).getName());
		address.setText(list.get(position).getFullAddress());
		
		return view;
	}

	@Override
	public void update(Observable observable, Object data) 
	{
		this.notifyDataSetChanged();
	}
	
}
