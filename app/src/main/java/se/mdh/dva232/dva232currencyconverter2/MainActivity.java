package se.mdh.dva232.dva232currencyconverter2;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LIFECYCLE", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager, true);

    }

    @Override
    protected void onStart() {
        Log.d("LIFECYCLE", "onStart");
        super.onStart();
   }

    @Override
    protected void onResume() {
        Log.d("LIFECYCLE", "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("LIFECYCLE", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("LIFECYCLE", "onStop");
        super.onStop();
     }

    @Override
    protected void onDestroy() {
        Log.d("LIFECYCLE", "onDestroy");
        super.onDestroy();
   }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0: return CurrencyConverterFragment.newInstance();
                case 1: return ConversionRatesFragment.newInstance();
                default: return CurrencyConverterFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}