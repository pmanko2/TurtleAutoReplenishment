package com.example.turtleautoreplenishment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by Pawel on 9/16/2014.
 * Class for implementing manual and auto replenishment/ordering
 */
public class ReplenishmentPagerAdapter extends FragmentPagerAdapter
{
    SparseArray<ReplenishmentFragment> registeredFragments;

    public ReplenishmentPagerAdapter(FragmentManager fm)
    {
        super(fm);
        registeredFragments  = new SparseArray<ReplenishmentFragment>();
    }

    @Override
    public Fragment getItem(int i) {

        Log.i("Changing fragment info", "Changing fragment to fragment " + i);

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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ReplenishmentFragment fragment = (ReplenishmentFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public ReplenishmentFragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }


}
