package com.letsplay.letsplay;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CreateMatchFragment.OnMatchCreatedListener {

    private static final String[] MAIN_MENU = new String[]{"MATCHES", "CREATE MATCH", "HISTORY", "PROFILE"};
    private List<String> mDataList = Arrays.asList(MAIN_MENU);

    private CustomAdapter customAdapter;

    private ViewPager mViewPager;
    private MagicIndicator tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (MagicIndicator) findViewById(R.id.tabLayout);

        customAdapter = new CustomAdapter(getFragmentManager(), mDataList);
        initMagicIndicator();
        mViewPager.setAdapter(customAdapter);

    }

    private void initMagicIndicator() {

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {

                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(Color.parseColor("#BEBEBE"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#000000"));
                simplePagerTitleView.setTypeface(null, Typeface.BOLD);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });

                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(Color.parseColor("#29B97C"));
                indicator.setLineHeight(UIUtil.dip2px(MainActivity.this, 3.0f));
                return indicator;

            }
        });

        tabLayout.setNavigator(commonNavigator);
        ViewPagerHelper.bind(tabLayout, mViewPager);
    }

    @Override
    public void onMatchCreated() {
        mViewPager.setCurrentItem(0);
    }
}

