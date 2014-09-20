package com.example.turtleautoreplenishment;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.turtleautoreplenishment.barcodescanner.IntentIntegrator;
import com.example.turtleautoreplenishment.barcodescanner.IntentResult;
import com.example.turtleautoreplenishment.webservices.HttpClient;
import com.example.turtleautoreplenishment.webservices.HttpDataDelegate;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.NumberPicker;

public class ScanningActivity extends FragmentActivity implements HttpDataDelegate
{
    ReplenishmentPagerAdapter pagerAdapter;
    ViewPager viewPager;
    Activity currentActivity;
    ArrayList<ScannedItem> itemList;
    boolean foundProduct;
    boolean isAuto;
    String barCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();
        isAuto = true;

		setContentView(R.layout.activity_scanning);

        pagerAdapter = new ReplenishmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.replenishment_pager);

        viewPager.setAdapter(pagerAdapter);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener tabListener = new ActionBar.TabListener(){

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        };

        actionBar.addTab(actionBar.newTab().setText("Auto").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Manual").setTabListener(tabListener));

        viewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener()
                {
                    @Override
                    public void onPageSelected(int position)
                    {
                        getActionBar().setSelectedNavigationItem(position);
                    }
                }
        );

        itemList = new ArrayList<ScannedItem>();
        currentActivity = this;
        Intent intent = getIntent();
        String companyName = intent.getStringExtra("companyName");
        setTitle("Scanning for " + companyName);

        Button scanButton = (Button) findViewById(R.id.button_scan);
        Button viewScanned = (Button) findViewById(R.id.button_view_scanned);

        viewScanned.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v)
            {
                saveCurrentItem();

                Intent scannedItems = new Intent(currentActivity, ScannedItemsActivity.class);
                scannedItems.putParcelableArrayListExtra("scannedArray", itemList);
                startActivity(scannedItems);

            }

        });

		barCode = "";
		foundProduct = false;

		handleScan(scanButton);
	}
	
	protected void saveCurrentItem()
	{
        boolean isAuto = false;

        if(viewPager.getCurrentItem() == 0)
            isAuto = true;

		if(foundProduct)
		{
			String replenishType = (isAuto) ? "Auto" : "Manual";
            ReplenishmentFragment currentFragment = (isAuto) ? pagerAdapter.getRegisteredFragment(0) : pagerAdapter.getRegisteredFragment(1);

			ScannedItem item = new ScannedItem(currentFragment.getProductNumber(), replenishType,
					currentFragment.getItemQuantity());
			itemList.add(item);
		}
		
	}


	// when scanning app returns information, gather the product number and display/do more later
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

		barCode = result.getContents();
		
		if(barCode != null)
		{
			String finalBarCode = convertBarCodeToCorrectFormat(barCode);
			
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "barcode_info"));
			params.add(new BasicNameValuePair("bar_code", finalBarCode));
			
			HttpClient.getInstance().getJsonInBackground("POST", this, params);
		}
		else
		{
			ReplenishmentFragment autoFragment = pagerAdapter.getRegisteredFragment(0);
            ReplenishmentFragment manualFragment = pagerAdapter.getRegisteredFragment(1);

            autoFragment.setProductNotFound();
            manualFragment.setProductNotFound();

			foundProduct = false;
		}
		
	}
	
	private String convertBarCodeToCorrectFormat(String barCode) 
	{
		int codeLength = barCode.length();
		String toReturn = "";
		
		switch(codeLength)
		{
			case 12:
				toReturn = barCode.substring(1);
				break;
			case 13:
				toReturn = barCode.substring(0, codeLength - 2);
				break;
			case 14:
				String temp = barCode.substring(1);
				toReturn = temp.substring(0, temp.length() - 2);
				break;
			default:
				toReturn = barCode;
				break;
		}
		
		return toReturn;
	}

	// initiate scanning app on button click
	private void handleScan(Button scan)
	{
		scan.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) 
			{	
				
				if(foundProduct)
				{
					if(itemQuantity.is == 0)
					{
						if(autoRadioButton.isChecked())
							createAlertDialog(true);
						else
							createAlertDialog(false);
					}
					else
					{
                    String replenishType = null;

                    if(autoRadioButton.isChecked())
                    {
                        replenishType = "Auto";
                    }
                    else
                    {
                        replenishType = "Manual";
                    }

                    ScannedItem item = new ScannedItem(productNumber.getText().toString(), replenishType,
                            itemQuantity.getValue());
                    itemList.add(item);

                    group.setVisibility(View.INVISIBLE);
                    group.clearCheck();
                    itemQuantity.setVisibility(View.INVISIBLE);
                    quantityPrompt.setVisibility(View.INVISIBLE);
                    itemQuantity.setValue(0);
                    IntentIntegrator scanIntent = new IntentIntegrator(currentActivity);
                    scanIntent.initiateScan();
                    foundProduct = false;
					//}
				}
				else
				{
					IntentIntegrator scanIntent = new IntentIntegrator(currentActivity);
					scanIntent.initiateScan();
					foundProduct = false;
				}
				
			}
			
		});
	}
	
	private void createAlertDialog(boolean isAuto)
	{
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle("Quantity Empty");
		
		if(isAuto)
		{
			alertBuilder.setMessage("Please Enter On Shelf Item Quantity");
		}
		else
		{
			alertBuilder.setMessage("Please Enter Item Quantity You Would Like To Order");
		}
		
		alertBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
				
			}
		});
		
		AlertDialog dialog = alertBuilder.create();
		dialog.show();
	}
	
	private void setRadioGroupListener()
	{
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) 
			{
				promptLayout.setVisibility(View.VISIBLE);
				
				if(checkedId == R.id.manually_replenish_radio_button)
				{
					quantityPrompt.setText("Enter quantity to order: ");
				}
				else
				{
					quantityPrompt.setText("Enter quantity left on shelf: ");
				}				
			}

			
		});
	}

	@Override
	public void handleAsyncDataReturn(Object ret) 
	{
		if(ret instanceof JSONObject)
		{
			JSONObject returnJson = (JSONObject) ret;
			
			int success;
			
			descriptionLayout.setVisibility(View.VISIBLE);
			
			try 
			{
				success = returnJson.getInt("success");
				
				if(success == 0)
				{
					productNumber.setText("Could not find Product");
					productNumber.setVisibility(View.VISIBLE);
					
					foundProduct = false;
				}
				else
				{
					//String customerId = returnJson.getString("customer_id");
					String id = returnJson.getString("id");
					String customerProductId = returnJson.getString("cust_product_id");
					String description = returnJson.getString("description");
					
					productNumber.setText(barCode);
					productDescription.setText(description);
					productTurtleId.setText(id);
					productCustomerId.setText(customerProductId);
					
					promptLayout.setVisibility(View.VISIBLE);
					productNumber.setVisibility(View.VISIBLE);
					group.setVisibility(View.VISIBLE);
					
					
					foundProduct = true;
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
	}
	
}
