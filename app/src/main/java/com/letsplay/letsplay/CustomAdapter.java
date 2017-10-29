package com.letsplay.letsplay;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.List;


/**
 * Created by hackware on 2016/9/10.
 */

public class CustomAdapter extends FragmentPagerAdapter {
    private final List<String> tabNames;

    public CustomAdapter(FragmentManager fragmentManager, List<String> tabNames) {
        super(fragmentManager);
        this.tabNames = tabNames;
    }

    @Override
    public int getCount() {
        return 4;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if (position == 0) {
            fragment = new MatchFragment();
        } else if (position == 1) {
            fragment = new CreateMatchFragment();
        } else if (position == 2) {
            fragment = new HistoryFragment();
        } else if (position == 3) {
            fragment = new ProfileFragment();
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames.get(position);
    }
}