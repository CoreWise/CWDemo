package com.cw.demo.fingerprint.jra;

import android.hardware.Sensor;
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
import com.cw.fpjrcsdk.JrcApiBase;
import com.cw.fpjrcsdk.JrcApiZiDevice;
import com.cw.serialportsdk.usbFingerManager.USBFingerManager;
import com.fm.bio.FPM;

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
    public JrcApiBase mJrcApi;
    private String mFingerDevice = "";
    public UsbManager mUsbManager;
    private boolean isInit = false;

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
                mFingerDevice = s;
                if (!isInit)
                {
                    initFragment();
                }
                mUsbManager = usbManager;
                if (mFingerDevice.equals(USBFingerManager.JRA_DEVICE)) {
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
                } else if (mFingerDevice.equals(JrcApiBase.ZiDevice)) {
                    MyApplication.getApp().cancleProgressDialog();
                    mJrcApi = new JrcApiZiDevice(JRAActivity.this);

                    int ret = mJrcApi.openJRC();
                    Log.i(TAG, "ret = " + ret);
                    updateMsg("ret = " + ret);

                    if (ret == FPM.SUCCESS) {
                        //成功
                        updateMsg("open device success");
                    } else if (ret == FPM.E_DEVICE) {
                        //没有设备
                        updateMsg("can't find this device!");
                    } else if (ret == FPM.E_UNKOWN) {
                        //未知错误
                        updateMsg("unknown mistake");
                    } else {
                        //其他错误
                        updateMsg("Other errors");
                    }
                } else {
                    MyApplication.getApp().cancleProgressDialog();
                    updateMsg("未知的指纹模块");
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
        if (mFingerDevice.equals(USBFingerManager.JRA_DEVICE)) {
            mJraFragment.closeThread();
            jraApi.closeJRA();
        } else if (mFingerDevice.equals(JrcApiBase.ZiDevice)) {
            mJrcFragment.stopThread();
            mJrcApi.closeJRC();
        }
        USBFingerManager.getInstance(this).closeUSB();
    }


    private void initView() {
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        tvResult = findViewById(R.id.tv_infos);
        tvResult.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private JraFragment mJraFragment;
    private JrcFragment mJrcFragment;
    private void initFragment() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JraCR30AFragment jraCR30AFragment = new JraCR30AFragment();
                mJraFragment = new JraFragment();
                mJrcFragment = new JrcFragment();

                if (mFingerDevice.equals(USBFingerManager.JRA_DEVICE)) {
                    fragmnts.add(mJraFragment);
                } else if (mFingerDevice.equals(JrcApiBase.ZiDevice)) {
                    fragmnts.add(mJrcFragment);
                } else {
                    fragmnts.add(mJraFragment);
                }

                fragmnts.add(jraCR30AFragment);

                String[] titles = getResources().getStringArray(R.array.fp_jra_title);

                mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragmnts, titles));
                mTabLayout.setupWithViewPager(mViewPager);

                isInit = true;
            }
        });

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
                    tvResult.append( msg+"\n" );
                }

//                //当前文本高度
//                int offset = lineCount * tvResult.getLineHeight();
//                if (offset > tvResult.getHeight()) {
//                    tvResult.scrollTo(0, offset - tvResult.getLineHeight());
//                }
                int scrollAmount = tvResult.getLayout().getLineTop(tvResult.getLineCount()) - tvResult.getHeight();
                if (scrollAmount > 0) {
                    tvResult.scrollTo(0, scrollAmount);
                } else {
                    tvResult.scrollTo(0, 0);
                }
            }
        });
    }

}
