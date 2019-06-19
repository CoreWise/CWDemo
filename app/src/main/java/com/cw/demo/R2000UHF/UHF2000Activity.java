package com.cw.demo.R2000UHF;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;


import com.cw.demo.R;
import com.cw.demo.R2000UHF.fragment.SettingFragment;
import com.cw.demo.R2000UHF.fragment.TagTestFragment;

import com.cw.demo.R2000UHF.fragment.mainfragment;
import com.cw.demo.R2000UHF.ui.LogList;
import com.cw.r2000uhfsdk.R2000UHFAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UHF2000Activity extends AppCompatActivity {

    private static final String TAG = "UHF2000Activity";
    public R2000UHFAPI r2000UHFAPI;
    public LogList mLogList;
    @BindView(R.id.log_list)
    LogList logList;
    FragmentTransaction transaction;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "uhf2000---onCreate");

        r2000UHFAPI = R2000UHFAPI.getInstance();



        /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_r2000uhf);

        ButterKnife.bind(this);

        initView();

        fragmentManager = getSupportFragmentManager();

        toFragment(new mainfragment());

        commitFragment();

    }


    @Override
    protected void onResume() {
        super.onResume();
                r2000UHFAPI.open(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
                r2000UHFAPI.close();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "uhf2000---onDestroy");
    }

    private void initView() {

        mLogList = findViewById(R.id.log_list);


    }

    public void toFragmentBackStack(Fragment fragment) {
        transaction = fragmentManager.beginTransaction();

        transaction.addToBackStack(null);

        transaction.replace(R.id.framelayout_main, fragment);
    }

    public void toFragment(Fragment fragment) {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.framelayout_main, fragment);
        // transaction.addToBackStack(null);
    }

    public void commitFragment() {
        transaction.commit();
    }





    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    //fragmentManager.popBackStack();
                } else {


                    //返回键监听
                    Log.i(TAG, "点击了返回键");
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.general_tips);
                    builder.setMessage(R.string.general_exit);

                    //设置确定按钮
                    builder.setNegativeButton(getResources().getString(R.string.general_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    //设置取消按钮
                    builder.setPositiveButton(getResources().getString(R.string.general_no), null);
                    //显示提示框
                    builder.show();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
