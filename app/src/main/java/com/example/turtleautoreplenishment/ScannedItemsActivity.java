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
    private int companyID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent(); 
		scannedItems = intent.getParcelableArrayListExtra("scannedArray");
        companyID = intent.getIntExtra("companyID", -1);
		
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
				
				arrayItem.put("turtle_id", item.getTurtleProduct());
				arrayItem.put("replenishment_type", item.getReplenishmentType());
				arrayItem.put("quantity", item.getQuantity());
                //TODO add information from application
                arrayItem.put("cust_part_no", item.getCustomerProduct());
                arrayItem.put("desc_one", item.getDescOne());
                arrayItem.put("desc_two", item.getDescTwo());
				
				scannedItemsArray.put(arrayItem);
			}

            request.put("customer_number",companyID);
            request.put("ship_to","1234"); //TODO
            request.put("user", AuthenticatedUser.getUser().getUserName());
			request.put("item_list", scannedItemsArray);

		}catch(JSONException e)
		{
			Log.i("JsonException: ", e.getMessage());
		}
		
		params.add(new BasicNameValuePair("tag", "create_scanned_order"));
		params.add(new BasicNameValuePair("report_json", request.toString()));

        Log.i("Report Request: ", request.toString());

		HttpClient.getInstance().getJsonInBackground("POST", this, params);
		
	}

	@Override
	public void handleAsyncDataReturn(Object ret) {
		Log.i("Send report result: ", ret.toString());
		
	}
}
