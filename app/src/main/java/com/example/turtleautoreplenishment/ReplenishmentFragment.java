package com.example.turtleautoreplenishment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.turtleautoreplenishment.webservices.HttpDataDelegate;

import java.util.ArrayList;

/**
 * Created by Pawel on 9/16/2014.
 */
public class ReplenishmentFragment extends Fragment implements HttpDataDelegate
{

    int fragmentNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentNumber = getArguments() != null ? getArguments().getInt("fragment_number") : 3;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.scanned_item_fragment, container, false);

        Bundle args = getArguments();

        ((TextView) fragmentView.findViewById(R.id.product_number)).setText("This is fragment" + fragmentNumber);

        return fragmentView;
    }

    @Override
    public void handleAsyncDataReturn(Object ret) {

    }
}

