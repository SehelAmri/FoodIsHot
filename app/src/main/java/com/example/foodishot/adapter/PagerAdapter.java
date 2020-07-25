package com.example.foodishot.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.foodishot.PendingOrders;
import com.example.foodishot.PreviousOrders;

public class PagerAdapter extends FragmentStatePagerAdapter {
int noOfTabs;
    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.noOfTabs = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                PreviousOrders previousOrders = new PreviousOrders();
                return previousOrders;
            case 1:
                PendingOrders pendingOrders = new PendingOrders();
               return  pendingOrders;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
