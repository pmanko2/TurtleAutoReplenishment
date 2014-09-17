package com.example.turtleautoreplenishment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Pawel on 9/16/2014.
 * Class for implementing manual and auto replenishment/ordering
 */
public class ReplenishmentPagerAdapter extends FragmentPagerAdapter
{
    public ReplenishmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        Fragment fragment = new ReplenishmentFragment();
        Bundle args = new Bundle();
        args.putInt("fragment_number", i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
