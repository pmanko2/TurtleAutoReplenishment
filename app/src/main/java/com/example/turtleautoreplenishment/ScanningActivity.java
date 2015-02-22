package com.example.turtleautoreplenishment;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.turtleautoreplenishment.barcodescanner.IntentIntegrator;
import com.example.turtleautoreplenishment.barcodescanner.IntentResult;
import com.example.turtleautoreplenishment.databaseservices.ScannedItemDataSource;
import com.example.turtleautoreplenishment.webservices.HttpClient;
import com.example.turtleautoreplenishment.webservices.HttpDataDelegate;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ScanningActivity extends FragmentActivity implements HttpDataDelegate
{
    private ReplenishmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private Activity currentActivity;
    //private ArrayList<ScannedItem> itemList;
    private boolean foundProduct;
    private boolean isAuto;
    private String barCode;
    private int companyID;
    private ScannedItemDataSource itemsDataSource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();

        // needed to open database connection for querying
        itemsDataSource = new ScannedItemDataSource(this);
        itemsDataSource.openDB();

        isAuto = true;

		setContentView(R.layout.activity_scanning);

        pagerAdapter = new ReplenishmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.replenishment_pager);

        viewPager.setAdapter(pagerAdapter);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // listener for the tab view of scanning activity
        ActionBar.TabListener tabListener = new ActionBar.TabListener(){


            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                // set tab as current if user selects it
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        };

        // add manual and auto tabs to aciton bar
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

        //itemList = new ArrayList<ScannedItem>(); // this holds all scanned items--might change to db
                                                 // implementation
        currentActivity = this;
        Intent intent = getIntent();
        String companyName = intent.getStringExtra("companyName");
        companyID = intent.getIntExtra("companyNumber",0);
        setTitle(companyName);

        Button scanButton = (Button) findViewById(R.id.button_scan);
        Button viewScanned = (Button) findViewById(R.id.button_view_scanned);

        // start activity that deals with all teh scanned items
        viewScanned.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v)
            {
                saveCurrentItem();

                Intent scannedItems = new Intent(currentActivity, ScannedItemsActivity.class);
                scannedItems.putExtra("companyID", companyID);
                startActivity(scannedItems);

            }

        });

		barCode = "";
		foundProduct = false;

        //setupSlidingMenu();

		handleScan(scanButton);

        continueOrderPrompt();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.scanning_menu, menu);
        return true;
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

            ScannedItem item = itemsDataSource.createScannedItem(currentFragment.getTurtleProductNumber(),
                    currentFragment.getCustomerProductNumber(), replenishType,
                    currentFragment.getDescOne(), currentFragment.getDescTwo(),
					currentFragment.getItemQuantity(), currentFragment.getItemMax(),
                    currentFragment.getItemMin(), currentFragment.getBin());

            Log.i("DB Activity: ", "Created new item in DB. Item no: " + item.getSqLiteID());
		}
		
	}


	// when scanning app returns information, gather the product number and display/do more later
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

		barCode = result.getContents();

        // scanning returns barcode - query db if barcode found otherwise indicate not found
		if(barCode != null)
		{
			barCode = convertBarCodeToCorrectFormat(barCode);
			
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tag", "barcode_info"));
			params.add(new BasicNameValuePair("bar_code", barCode));
			
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

    // method converts scanned barcode to correct format
    // correct format has 11 characters
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
				// if product was previously found, save that product
				if(foundProduct)
				{
                    boolean isAuto = viewPager.getCurrentItem() == 0;
                    String replenishType = (isAuto) ? "Auto" : "Manual";

                    // get fragments from adapter
                    ReplenishmentFragment autoFragment = pagerAdapter.getRegisteredFragment(0);
                    ReplenishmentFragment manualFragment = pagerAdapter.getRegisteredFragment(1);
                    ReplenishmentFragment currentFragment = (isAuto) ? autoFragment : manualFragment;

                    ScannedItem item = itemsDataSource.createScannedItem(currentFragment.getTurtleProductNumber(),
                            currentFragment.getCustomerProductNumber(), replenishType,
                            currentFragment.getDescOne(), currentFragment.getDescTwo(),
                            currentFragment.getItemQuantity(), currentFragment.getItemMax(),
                            currentFragment.getItemMin(), currentFragment.getBin());

                    autoFragment.clearProductInfo();
                    manualFragment.clearProductInfo();

                    IntentIntegrator scanIntent = new IntentIntegrator(currentActivity);
                    scanIntent.initiateScan();
                    foundProduct = false;
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

	@Override
	public void handleAsyncDataReturn(Object ret) 
	{
        // need fragments to change display of fragments when we get product data back
        ReplenishmentFragment autoFragment = pagerAdapter.getRegisteredFragment(0);
        ReplenishmentFragment manualFragment = pagerAdapter.getRegisteredFragment(1);

		if(ret instanceof JSONObject)
		{
			JSONObject returnJson = (JSONObject) ret;
			
			int success;
			
			try 
			{
				success = returnJson.getInt("success");

                // if product not found, indicate and prompt if user wants to order anyway
				if(success == 0)
				{
                    promptAddProduct(returnJson);
				}
				else
				{
                    // if product is found, show information on screen
					//String customerId = returnJson.getString("customer_id");
					String turtleID = returnJson.getString("id");
					String customerProductId = returnJson.getString("cust_product_id");
					String description = returnJson.getString("description");
                    String secondDescription = returnJson.getString("description_2");
                    String min = returnJson.getString("min");
                    String max = returnJson.getString("max");
                    String bin = returnJson.getString("bin");
                    String thirdDescription = returnJson.getString("description_3");

                    autoFragment.setProductFound(customerProductId, description, turtleID, min, max,
                                                bin, secondDescription, thirdDescription);
                    manualFragment.setProductFound(customerProductId, description, turtleID, min, max,
                                                bin, secondDescription, thirdDescription);

					foundProduct = true;
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
	}

    // Alert Dialog created to prompt if we want to add unknown product to order
    private boolean promptAddProduct(final JSONObject returnJson)
    {
        boolean add = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Product Not Found");
        builder.setMessage("Would you like to order this product anyway?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleProduct(returnJson, true);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleProduct(returnJson, false);
            }
        });

        builder.create().show();

        return add;
    }

    private void handleProduct(JSONObject returnJson, boolean add)
    {
        ReplenishmentFragment autoFragment = pagerAdapter.getRegisteredFragment(0);
        ReplenishmentFragment manualFragment = pagerAdapter.getRegisteredFragment(1);

        if(add)
        {
            //if user wants to order product anyway, display just barcode (prodID)
            autoFragment.setProductFound(barCode, "","","","","","","");
            manualFragment.setProductFound(barCode, "","","","","","","");
            foundProduct = true;
        }
        else
        {
            autoFragment.setProductNotFound();
            manualFragment.setProductNotFound();
            foundProduct = false;
        }
    }

    private void continueOrderPrompt()
    {
        boolean orderExists = itemsDataSource.getCount() != 0;

        Log.i("DATABASE INFORMATION: ", "Does the order exist?: " + orderExists);

        if(orderExists)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Previous Order Found");
            builder.setMessage("Would you like to continue with your " +
                    "previous order or start a new one?");

            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {

                }
            });

            builder.setNegativeButton("Start New Order", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    itemsDataSource.clearTable();
                }
            });

            builder.create().show();
        }
    }

    @Override
    protected void onResume() {
        itemsDataSource.openDB();
        super.onResume();
    }

    @Override
    protected void onPause() {
        itemsDataSource.closeDB();
        super.onPause();
    }
}
