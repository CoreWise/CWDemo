package com.cw.demo.barcode;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;


import com.cw.demo.MyApplication;
import com.cw.demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BarCodeActivity extends AppCompatActivity {

    private static final String TAG = "BarCodeActivity";

    @BindView(R.id.tab)
    TabLayout tab;


    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private Fragment[] mFragmentArrays = new Fragment[2];

    private String[] mTabTitles = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_activity);
        ButterKnife.bind(this);

        //亮屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        initView();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getApp().setParam(this, "isScanning", false);
    }


    private void initView() {

        mTabTitles[0] = getString(R.string.barcode_scan);
        mTabTitles[1] = getString(R.string.barcode_setting);
        tab.setTabMode(TabLayout.MODE_FIXED);
        mFragmentArrays[0] = ScannerFragment.newInstance();
        mFragmentArrays[1] = ScannerFragment.newInstance();
        PagerAdapter pagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(pagerAdapter);
        //将ViewPager和TabLayout绑定
        tab.setupWithViewPager(viewpager);

    }


    final class MyViewPagerAdapter extends FragmentPagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentArrays[position];
        }


        @Override
        public int getCount() {
            return mFragmentArrays.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }
    }

    int time = 0;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Log.i(TAG, "点击了返回键");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.general_tips);
                builder.setMessage(R.string.general_exit);

                //设置确定按钮
                builder.setNegativeButton(R.string.general_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                //设置取消按钮
                builder.setPositiveButton(R.string.general_no, null);
                //显示提示框
                builder.show();
                break;

            case KeyEvent.KEYCODE_UNKNOWN:
                time++;
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}