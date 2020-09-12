package com.waslabank.wasslabank.ui;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.waslabank.wasslabank.R;
import com.waslabank.wasslabank.adapters.FragmentAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyRidesActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tab)
    TabLayout mTabLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    FragmentAdapter fragmentPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mToolbar.setNavigationOnClickListener(v -> finish());

        fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(fragmentPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    protected void onStart() {
        super.onStart();
        fragmentPagerAdapter.notifyDataSetChanged();
    }
}
