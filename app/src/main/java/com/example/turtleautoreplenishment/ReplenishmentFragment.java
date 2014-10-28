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
 * Fragment represents either an auto or manual replenishment screen in application
 */
public class ReplenishmentFragment extends Fragment
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
    private TextView productDescription;
    private TextView productTurtleId;
    private TextView productSecondDescription;
    private String barCode;
    private TextView minText;
    private TextView maxText;
    private TextView binText;
    private TextView productThirdDescription;

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
        itemQuantity = (NumberPicker) fragmentView.findViewById(R.id.number_to_order);
        quantityPrompt = (TextView) fragmentView.findViewById(R.id.quantity_prompt);
        productNumber = (TextView) fragmentView.findViewById(R.id.product_number);
        productDescription = (TextView) fragmentView.findViewById(R.id.product_description);
        productSecondDescription = (TextView) fragmentView.findViewById(R.id.product_second_description);
        productThirdDescription = (TextView) fragmentView.findViewById(R.id.product_third_description);
        productTurtleId = (TextView) fragmentView.findViewById(R.id.product_turtle_id);
        minText = (TextView) fragmentView.findViewById(R.id.minimum);
        maxText = (TextView) fragmentView.findViewById(R.id.maximum);
        binText = (TextView) fragmentView.findViewById(R.id.bin);

        itemQuantity.setMinValue(0);
        itemQuantity.setMaxValue(10000);
        itemQuantity.setVisibility(View.INVISIBLE);
        itemQuantity.setWrapSelectorWheel(true);
        itemQuantity.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        itemQuantity.setOnLongPressUpdateInterval(10);

        productNumber.setText("");
        productDescription.setText("");
        productTurtleId.setText("");
        quantityPrompt.setText("");
        productSecondDescription.setText("");
        productThirdDescription.setText("");
        minText.setText("");
        maxText.setText("");
        binText.setText("");


        return fragmentView;
    }

    public void setProductFound(String pNumber, String pDescription, String turtleID, String min,
                                    String max, String bin, String secondDescription, String thirdDescription)
    {

        itemQuantity.setVisibility(View.VISIBLE);
        productNumber.setText(pNumber);
        productDescription.setText(pDescription);
        productTurtleId.setText(turtleID);
        productSecondDescription.setText(secondDescription);
        productThirdDescription.setText(thirdDescription);

        minText.setText("Min: " + min);
        maxText.setText("Max: " + max);
        binText.setText("Bin: " + bin);


        if(fragmentNumber == 0)
            quantityPrompt.setText("Enter quantity on shelf:");
        else
            quantityPrompt.setText("Enter quantity to order:");
    }

    public void setProductNotFound()
    {
        itemQuantity.setVisibility(View.INVISIBLE);
        productNumber.setText("Product Not Found");
        productDescription.setText("");
        productTurtleId.setText("");
        quantityPrompt.setText("");
        productSecondDescription.setText("");
        minText.setText("");
        maxText.setText("");
        binText.setText("");
        productThirdDescription.setText("");
    }

    public void clearProductInfo()
    {
        itemQuantity.setVisibility(View.INVISIBLE);
        productNumber.setText("");
        productDescription.setText("");
        productTurtleId.setText("");
        quantityPrompt.setText("");
        productSecondDescription.setText("");
        minText.setText("");
        maxText.setText("");
        binText.setText("");
        productThirdDescription.setText("");
    }

    public String getTurtleProductNumber()
    {
        return productTurtleId.getText().toString();
    }
    public String getCustomerProductNumber() {return productNumber.getText().toString();}
    public String getDescOne()
    {
        return productDescription.getText().toString();
    }
    public String getDescTwo() {return productSecondDescription.getText().toString();}
    public int getItemQuantity()
    {
        return itemQuantity.getValue();
    }
    public String getItemMax(){return maxText.getText().toString();}
    public String getItemMin(){return minText.getText().toString();}
    public String getBin(){return binText.getText().toString();}
}

