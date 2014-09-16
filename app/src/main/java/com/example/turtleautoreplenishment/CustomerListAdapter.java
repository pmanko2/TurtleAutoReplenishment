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
		
		CurrentLocation.getCurrentLocation().addObserver(this);
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
		TextView distance = (TextView) view.findViewById(R.id.distance_to_customer);
		
		name.setText(list.get(position).getName());
		address.setText(list.get(position).getFullAddress());
		//calculateDistanceToCustomer(list.get(position), distance);
		
		return view;
	}
	
	private void calculateDistanceToCustomer(final Customer current, final TextView distanceText)
	{
		final Location currentLocation = CurrentLocation.getCurrentLocation().getLatestLocation();
		
		if(currentLocation == null)
		{
			distanceText.setText("");
			return;
		}
		
		new AsyncTask<Object, Object, Object>()
		{

			@Override
			protected Object doInBackground(Object... params) 
			{
				Geocoder geo = new Geocoder(context);
				
				String custAddressString = current.getFullAddress();
				List<Address> customerAddressList = null;
				try 
				{
					customerAddressList = geo.getFromLocationName(custAddressString, 1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return customerAddressList;
			}
			
			@Override
			protected void onPostExecute(Object result)
			{
				String distanceString = "";
				
				if(result instanceof List<?> && result != null)
				{
					
					@SuppressWarnings("unchecked")
					List<Address> customerAddressList = (List<Address>) result;
					
					Address customerAddress = null;
					
					if(customerAddressList.size() > 0)
						customerAddress = customerAddressList.get(0);
					else
						return;
						
					if(customerAddress.hasLatitude() && customerAddress.hasLongitude())
					{
						// new location object based on lat and log of address object
						Location custLocation = new Location("");
						custLocation.setLatitude(customerAddress.getLatitude());
						custLocation.setLongitude(customerAddress.getLongitude());
						
						float distanceToCurrentCustomer = currentLocation.distanceTo(custLocation);
						double distanceInMiles = distanceToCurrentCustomer * .00062137;
						
						distanceString = String.format("%.2f", distanceInMiles) + " mi";
					}
				}
				
				distanceText.setText(distanceString);
				CustomerListAdapter.this.notifyDataSetChanged();
			}
			
		}.execute();
		
	}

	@Override
	public void update(Observable observable, Object data) 
	{
		this.notifyDataSetChanged();
	}
	
}
