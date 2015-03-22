package com.movil.contabilidad.personalaccountancy;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by hass-pc on 17/03/2015.
 */
public class ViewPageAdapter extends FragmentPagerAdapter {

    public static final String tabTitles[] = {"CYCLES", "RUBLES"};
    public static final int TAB_COUNT = 2;

    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0: return new FragmentCycles();
            case 1: return new FragmentRubles();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
