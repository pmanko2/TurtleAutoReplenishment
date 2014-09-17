package com.example.turtleautoreplenishment;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.turtleautoreplenishment.webservices.HttpClient;
import com.example.turtleautoreplenishment.webservices.HttpDataDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ScannedItemsActivity extends FragmentActivity implements HttpDataDelegate
{
	private ArrayList<ScannedItem> scannedItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent(); 
		scannedItems = intent.getParcelableArrayListExtra("scannedArray");
		
		setContentView(R.layout.activity_scanned_items);
		
		ListView scannedList = (ListView) findViewById(R.id.scanned_items_view);
		ScannedItemListAdapter adapter = new ScannedItemListAdapter(this, R.layout.scanned_item_item, scannedItems);
		
		scannedList.setAdapter(adapter);
		
		Button sendReport = (Button) findViewById(R.id.send_scanned_items_button);
		
		sendReport.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				if(scannedItems.isEmpty())
				{
					Toast.makeText(ScannedItemsActivity.this, "You have not scanned any items", Toast.LENGTH_LONG).show();
				}
				else
				{
					sendScannedItemsJson();
				}
				
			}
			
		});
	}
	
	private void sendScannedItemsJson()
	{
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		
		JSONObject request = new JSONObject();
		
		try
		{
			JSONArray scannedItemsArray = new JSONArray();
			
			for(ScannedItem item : scannedItems)
			{
				JSONObject arrayItem = new JSONObject();
				
				arrayItem.put("barcode", item.getBarcodeNumber());
				arrayItem.put("replenishment_type", item.getReplenishmentType());
				arrayItem.put("quantity", item.getQuantity());
				
				scannedItemsArray.put(arrayItem);
			}
			
			request.put("item_list", scannedItemsArray);
		}catch(JSONException e)
		{
			Log.i("JsonException: ", e.getMessage());
		}
		
		params.add(new BasicNameValuePair("tag", "sending_report"));
		params.add(new BasicNameValuePair("report_json", request.toString()));
		
		//HttpClient.getInstance().getJsonInBackground("POST", this, params);
		
	}

	@Override
	public void handleAsyncDataReturn(Object ret) {
		// TODO Auto-generated method stub
		
	}
}