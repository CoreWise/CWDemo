package com.cw.demo.fingerprint.jra;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.WindowManager;

import com.cw.demo.BaseActivity;
import com.cw.demo.MyApplication;
import com.cw.demo.R;
import com.cw.fpjrasdk.JRA_API;
import com.cw.serialportsdk.usbFingerManager.USBFingerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：李阳
 * 时间：2019/6/25
 * 描述：
 */
public class JRAActivity extends BaseActivity {

    private static final String TAG = "JRAActivity";
    public JRA_API jraApi;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private AppCompatTextView tvResult;
    private List<Fragment> fragmnts = new ArrayList<Fragment>();
    private JraFragment mJraFragment;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fp_jra_new);

        initView();
    }


    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.getApp().showProgressDialog(this, getString(R.string.fp_usb_init));

        Log.i(TAG, "------------onStart--------------");
        USBFingerManager.getInstance(this).openUSB(new USBFingerManager.OnUSBFingerListener() {
            @Override
            public void onOpenUSBFingerSuccess(String s, UsbManager usbManager, UsbDevice usbDevice) {
                if (s.equals(USBFingerManager.JRA_DEVICE)) {
                    MyApplication.getApp().cancleProgressDialog();

                    Log.i(TAG, "切换USB成功");
                    updateMsg(getString(R.string.fp_usb_open_success));
                    jraApi = new JRA_API(usbManager, usbDevice);
                    int ret = jraApi.openJRA();
                    if (ret == JRA_API.DEVICE_SUCCESS) {
                        updateMsg("open device success");
                    } else if (ret == JRA_API.PS_DEVICE_NOT_FOUND) {
                        updateMsg("can't find this device!");
                    } else if (ret == JRA_API.PS_EXCEPTION) {
                        updateMsg("open device fail");
                    }
                }
            }

            @Override
            public void onOpenUSBFingerFailure(String s) {
                Log.i(TAG, "切换USB失败");
                updateMsg(getString(R.string.fp_usb_open_failure));
                MyApplication.getApp().cancleProgressDialog();


            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "------------onStop--------------");
        updateMsg("设备已关闭");
        jraApi.closeJRA();
        USBFingerManager.getInstance(this).closeUSB();
    }


    private void initView() {
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        tvResult = findViewById(R.id.tv_infos);
        tvResult.setMovementMethod(ScrollingMovementMethod.getInstance());

        mJraFragment = new JraFragment();


        JraCR30AFragment jraCR30AFragment = new JraCR30AFragment();

        fragmnts.add(mJraFragment);

        fragmnts.add(jraCR30AFragment);


        String[] titles = getResources().getStringArray(R.array.fp_jra_title);


        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragmnts, titles));

        mTabLayout.setupWithViewPager(mViewPager);
    }


    public void updateMsg(final String msg) {
        if (msg == null) {
            tvResult.setText("");
            tvResult.scrollTo(0, 0);

            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //获取当前行数
                int lineCount = tvResult.getLineCount();
                if (lineCount > 2000) {
                    //大于100行自动清零
                    tvResult.setText("");
                    tvResult.setText(msg);
                } else {
                    //小于100行追加
                    tvResult.append("\n" + msg);
                }

                //当前文本高度
                int offset = lineCount * tvResult.getLineHeight();
                if (offset > tvResult.getHeight()) {
                    tvResult.scrollTo(0, offset - tvResult.getLineHeight());
                }

            }
        });
    }

}
