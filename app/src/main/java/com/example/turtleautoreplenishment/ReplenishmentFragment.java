package com.example.turtleautoreplenishment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.turtleautoreplenishment.webservices.HttpDataDelegate;

import java.util.ArrayList;

/**
 * Created by Pawel on 9/16/2014.
 */
public class ReplenishmentFragment extends Fragment implements HttpDataDelegate
{

    private int fragmentNumber;
    private Activity currentActivity;
    private ArrayList<ScannedItem> itemList;
    private boolean foundProduct;
    private RadioGroup group;
    private RadioButton autoRadioButton;
    private TextView quantityPrompt;
    private TextView productNumber;
    private NumberPicker itemQuantity;
    private RelativeLayout promptLayout;
    private LinearLayout descriptionLayout;
    private TextView productDescription;
    private TextView productTurtleId;
    private TextView productCustomerId;
    private String barCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentNumber = getArguments() != null ? getArguments().getInt("fragment_number") : 3;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.i("Fragment info", "Creating view of fragment" + fragmentNumber);

        View fragmentView = inflater.inflate(R.layout.scanned_item_fragment, container, false);

        Bundle args = getArguments();


        // initialize view items
        /*itemQuantity = (NumberPicker) fragmentView.findViewById(R.id.number_to_order);
        quantityPrompt = (TextView) fragmentView.findViewById(R.id.quantity_prompt);
        productNumber = (TextView) fragmentView.findViewById(R.id.product_number);
        productDescription = (TextView) fragmentView.findViewById(R.id.product_description);
        productTurtleId = (TextView) fragmentView.findViewById(R.id.product_turtle_id);
        productCustomerId = (TextView) fragmentView.findViewById(R.id.product_customer_id);
        descriptionLayout = (LinearLayout) fragmentView.findViewById(R.id.product_info_layout);


        productNumber.setVisibility(View.INVISIBLE);

        *//*Intent intent = getIntent();
        currentActivity = this;*//*

       *//* String companyName = intent.getStringExtra("companyName");
        setTitle("Scanning for " + companyName);*//*

        *//*Button scanButton = (Button) findViewById(R.id.button_scan);
        Button viewScanned = (Button) findViewById(R.id.button_view_scanned);*//*

        itemQuantity.setMinValue(0);
        itemQuantity.setMaxValue(10000);*/


        if(fragmentNumber == 0)
        {
            ((TextView) fragmentView.findViewById(R.id.quantity_prompt)).setText("Enter quantity on shelf:");
        }
        else
        {
            ((TextView) fragmentView.findViewById(R.id.quantity_prompt)).setText("Enter quantity to order:");
        }

        return fragmentView;
    }

    @Override
    public void handleAsyncDataReturn(Object ret) {

    }

    public void setProductFound(String pNumber, String pDescription, String turtleID, String custID)
    {
        productNumber.setText(pNumber);
        productDescription.setText(pDescription);
        productTurtleId.setText(turtleID);
        productCustomerId.setText(custID);
    }

    public void setProductNotFound()
    {
        productNumber.setText("Product Not Found");
        productDescription.setText("");
        productTurtleId.setText("");
        productCustomerId.setText("");
    }
}

