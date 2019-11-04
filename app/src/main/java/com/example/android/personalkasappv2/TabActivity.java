package com.example.android.personalkasappv2;


import android.support.v4.app.FragmentManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabActivity extends AppCompatActivity {

    private final String[] TITLE_PAGES = new String[]{"INCOME","OUTCOME"};

    private final Fragment[] PAGES = new Fragment[]{new IncomeFragment(),
            new OutcomeFragment()};

    @BindView(R.id.tab_main)
    TabLayout tab_main;

    @BindView(R.id.view_pager_main)
    ViewPager view_pager_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        ButterKnife.bind(this);

        view_pager_main.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tab_main.setupWithViewPager(view_pager_main);


        getSupportActionBar().setTitle("Tambah Baru");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private class PagerAdapter extends FragmentPagerAdapter {


        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return PAGES[position];
        }

        @Override
        public int getCount() {
            return PAGES.length;
        }

        @Override
        public CharSequence getPageTitle(int position){
            return TITLE_PAGES[position];
        }
    }
}
