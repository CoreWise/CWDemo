package com.cw.demo.R2000UHF.fragment;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;


import android.support.annotation.RequiresApi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.cw.demo.R;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import q.rorbin.verticaltablayout.VerticalTabLayout;


public class mainfragment extends Fragment {

    private static final String TAG = "UHF2000Activity";

    @BindView(R.id.tab)
    VerticalTabLayout tab;

    @BindView(R.id.vPager)
    ViewPager vPager;


    private String[] mTabTitles = new String[2];
    private List<android.support.v4.app.Fragment> listViews; //Tab页面列表


    View rootView;


    private Unbinder unBinder;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_r2000uhf_main, container,false);
        unBinder = ButterKnife.bind(this,rootView);
        initView();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unBinder.unbind();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initView() {
        mTabTitles = new String[]{getString(R.string.uhf_tag_test), getString(R.string.uhf_setting)};
        //tab.setTabMode(TabLayout.MODE_FIXED);
        listViews = new ArrayList<>();
        listViews.add(new TagTestFragment());
        listViews.add(new SettingFragment());
        vPager.setAdapter(new MyFrageStatePagerAdapter(getChildFragmentManager()));
        //将ViewPager和TabLayout绑定
        tab.setupWithViewPager(vPager);
    }


    class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter {

        public MyFrageStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return listViews.get(position);
        }

        @Override
        public int getCount() {
            return listViews.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }
    }


}
