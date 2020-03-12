package com.wattathlon.wattathlon2;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class Choose extends AppCompatActivity {

    private static final String TAG  = "Choose";
    public class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {

            switch (index) {
                case 0:
                    // Top Rated fragment activity
                    return new TabOne();
                case 1:
                    // Games fragment activity
                    return new TabTwo();
            }

            return null;
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 2;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager()));

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        tabs.getTabAt(0).setIcon(R.drawable.ic_rowing_black_24dp);
        tabs.getTabAt(1).setIcon(R.drawable.ic_person_black_24dp);
        Log.d(TAG, "row ftp:" + ((Account) getApplication()).getRowFtp());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}