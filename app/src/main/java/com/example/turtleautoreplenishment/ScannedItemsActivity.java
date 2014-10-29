package com.example.turtleautoreplenishment;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.turtleautoreplenishment.databaseservices.ScannedItemDataSource;
import com.example.turtleautoreplenishment.webservices.HttpClient;
import com.example.turtleautoreplenishment.webservices.HttpDataDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
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

        ScannedItemDataSource dataSource = new ScannedItemDataSource(this);
        dataSource.openDB();

		Intent intent = getIntent(); 
		scannedItems = dataSource.getAllItems();
        companyID = intent.getIntExtra("companyID", -1);
		
		setContentView(R.layout.activity_scanned_items);
		
		final ListView scannedList = (ListView) findViewById(R.id.scanned_items_view);
		final ScannedItemListAdapter adapter = new ScannedItemListAdapter(this, R.layout.scanned_item_item, dataSource);
		
		scannedList.setAdapter(adapter);

        // set up contexual menu
        scannedList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        scannedList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {
            int numChecked = 0;

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked)
            {
                Log.i("Contexual menu: ", "Checked state has been changed" + " position: " + position + " checked " + checked);
                adapter.indicateChecked(position, checked);

                // keep track of num checked items -- only show edit button if one item checked
                if(checked)
                    numChecked++;
                else
                    numChecked--;

                actionMode.invalidate();
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                //inflate menu defined in scanned_items_menu in action bar
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.scanned_items_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
            {
                MenuItem edit = menu.getItem(0);

                if(numChecked != 1) {
                    edit.setVisible(false);
                }
                else
                {
                    edit.setVisible(true);
                }

                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
            {
                // handle action clicks (delete items or edit item)
                switch(menuItem.getItemId())
                {
                    case R.id.discard_scanned_items:
                        deleteItems();
                        actionMode.finish();
                        return true;
                    case R.id.edit_scanned_item:
                        editItem();
                        actionMode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode)
            {
                adapter.resetSelected();
                numChecked = 0;
            }

            private void deleteItems()
            {
                adapter.deleteSelected();
            }

            private void editItem()
            {
                adapter.editSelected();
            }
        });

        scannedList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                Log.i("Long Click Listener: ", "Long click listener called");
                //scannedList.setItemChecked(position, !adapter.isPositionSelected(position));
                return false;
            }
        });
		
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
