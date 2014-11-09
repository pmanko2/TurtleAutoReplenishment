package com.example.turtleautoreplenishment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.turtleautoreplenishment.webservices.HttpClient;
import com.example.turtleautoreplenishment.webservices.HttpDataDelegate;

public class ChooseCustomerActivity extends FragmentActivity implements
	ActionBar.OnNavigationListener, HttpDataDelegate 
{

	private boolean paused;
	private ArrayList<Customer> list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		
		paused = false;
		
		// test arraylist of customers -- needs to be taken from db
		list = new ArrayList<Customer>();
		
		Intent intent = getIntent();
		Bundle passed = intent.getBundleExtra("loginInfo");
		
		loadCustomerData();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scanning_menu, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) 
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onPause() 
	{
		super.onPause();
		paused = true;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
	}

	@Override
	public void handleAsyncDataReturn(Object ret) 
	{ 
		// we want to go through returned jsonarray, extract all user info and create customer in 
		// arraylist
		if(ret instanceof JSONObject)
		{
			JSONObject returnJson = (JSONObject) ret;
			
			try 
			{
				if(returnJson.getBoolean("successful"))
				{
					setContentView(R.layout.activity_main);
					
					JSONArray customerArray = returnJson.getJSONArray("customers");
					
					for(int i = 0; i < customerArray.length(); i++)
					{
						JSONObject customer = customerArray.getJSONObject(i);
						
						int id = customer.getInt("id");
						String name = customer.getString("name");
						String firstAddress = customer.getString("first_address");
						String secondAddress = customer.getString("second_address");
						String city = customer.getString("city");
						String state = customer.getString("state");
						String zipCode = customer.getString("zip");
						
						list.add(new Customer(id, name, firstAddress, secondAddress, city, state, zipCode));
					}
					
					setupUI();
				}
				else
				{
					Toast.makeText(this, "Could not authenticate", Toast.LENGTH_SHORT);
					
					Intent intent = new Intent(ChooseCustomerActivity.this, MainActivity.class);
					startActivity(intent);
					
				}
				
			} catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		
		if(ret instanceof String)
		{
			Toast.makeText(this, (String) ret, Toast.LENGTH_LONG).show();
		}
		
	}
	
	private void loadCustomerData()
	{
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "customer_list"));
		
		HttpClient.getInstance().getJsonInBackground("POST", this, params);
	}
	
	private void setupUI()
	{
		// associate customerlist adapter with listview
		ListView initCustomerList = (ListView) findViewById(R.id.main_customer_list);
		final CustomerListAdapter adapter = new CustomerListAdapter(this, R.layout.customer_item, list);
		
		initCustomerList.setAdapter(adapter);
		initCustomerList.setOnItemClickListener(new OnItemClickListener(){


			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) 
			{
				Intent intent = new Intent(ChooseCustomerActivity.this, ScanningActivity.class);
				intent.putExtra("companyName", list.get(position).getName());
                intent.putExtra("companyNumber", list.get(position).getId());
				startActivity(intent);
				
			}
			
		});
	}

}
