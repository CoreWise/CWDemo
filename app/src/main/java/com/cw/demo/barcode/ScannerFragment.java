package com.cw.demo.barcode;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.cw.barcodesdk.SoftDecodingAPI;
import com.cw.demo.MyApplication;
import com.cw.demo.R;
import com.cw.serialportsdk.CoreWise;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 作者：李阳
 * 时间：2018/9/15
 * 描述：
 */
public class ScannerFragment extends Fragment implements SoftDecodingAPI.IBarCodeData {

    private static final String TAG = "CoreWise"+"ScannerFragment";

    @BindView(R.id.scan)
    Button scan;
    @BindView(R.id.scanning)
    Button scanning;
    @BindView(R.id.end)
    Button end;
    @BindView(R.id.clear)
    Button clear;
    @BindView(R.id.et_barcode)
    EditText etBarcode;
    @BindView(R.id.tv_all)
    TextView tvAll;
    @BindView(R.id.tv_success)
    TextView tvSuccess;

    Unbinder unbinder;

    SoftDecodingAPI api;
    @BindView(R.id.cb)
    CheckBox cb;

    volatile boolean isAutoClear = true;
    volatile boolean isKey = true;

    volatile boolean isScanning = false;

    @BindView(R.id.tb)
    ToggleButton tb;
    @BindView(R.id.ll3)
    LinearLayout ll3;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    break;
            }
        }
    };
    private int success = 0;
    public int all = 0;

    BarCodeActivity barCodeActivity;

    private BarCodeReceiver receiver;

    private int clickCount = 0;

    public static Fragment newInstance() {
        ScannerFragment fragment = new ScannerFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.barcode_fragment_scanner, container, false);
        api = new SoftDecodingAPI(getActivity(), this);




        unbinder = ButterKnife.bind(this, rootView);
        barCodeActivity = (BarCodeActivity) getActivity();


        tvSuccess.setText(getActivity().getString(R.string.barcode_scan_success, ""));
        tvAll.setText(getActivity().getString(R.string.barcode_scan_all, ""));
        cb.setChecked(isAutoClear);

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAutoClear = isChecked;
            }
        });


        //开发者模式和销售模式
        etBarcode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ll3.setVisibility(View.GONE);
                return false;
            }
        });

        etBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount++;
                if (clickCount == 5) {
                    clickCount = 0;
                    ll3.setVisibility(View.VISIBLE);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new BarCodeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        filter.addAction(Intent.ACTION_REBOOT);
        getActivity().registerReceiver(receiver, filter);

    }


    @Override
    public void sendScan() {

        mhandler.post(new Runnable() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void run() {
                all++;

                if (!isKey) {
                    tvAll.setText(getActivity().getString(R.string.barcode_scan_all, all));
                }
                //成功率计算

            }
        });
    }

    @Override
    public void onBarCodeData(final String data) {
        mhandler.post(new Runnable() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void run() {
                //按键扫描
                if (!isKey) {
                    success++;
                    tvSuccess.setText(getActivity().getString(R.string.barcode_scan_success, success));
                }


                etBarcode.append(data + "\n");
                //etBarcode.setText(data);
                int offset = etBarcode.getLineCount() * etBarcode.getLineHeight();
                if (offset > etBarcode.getHeight()) {
                    etBarcode.scrollTo(0, offset - etBarcode.getHeight());
                }

                //自清
                if (isAutoClear) {
                    //Log.i(TAG, "----------" + etBarcode.getLineCount());
                    if (etBarcode.getLineCount() > 100) {
                        etBarcode.setText("");
                    }
                }
                //LocalLog.e(TAG, "--all:--" + all + "--success:--" + success + "--data:--" + data);
            }
        });
    }

    @Override
    public void getSettings(int PowerOnOff, int OutputMode, int TerminalChar, String Prefix, String Suffix, int Volume, int PlayoneMode) {

    }

    @Override
    public void setSettingsSuccess() {

    }


    @Override
    public void onResume() {
        super.onResume();

        api.openBarCodeReceiver();
        tb.setChecked(api.isScannerServiceRunning(getActivity()));

        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "--------" + isChecked);

                api.setGlobalSwicth(isChecked);
                scan.setEnabled(isChecked);
                scanning.setEnabled(isChecked);
                end.setEnabled(isChecked);

            }
        });

        switch (CoreWise.getAndroidVersion()) {
            case CoreWise.deviceSysVersion.O:
                scan.setEnabled(true);
                scanning.setEnabled(true);
                end.setEnabled(true);
                break;
            case CoreWise.deviceSysVersion.U:
                if (api.isScannerServiceRunning(getActivity())) {

                } else {
                    scan.setEnabled(false);
                    scanning.setEnabled(false);
                    end.setEnabled(false);
                    api.CloseScanning();
                }
                break;

        }


        boolean isScan = (boolean) MyApplication.getApp().getParam(getActivity(), "isScanning", false);

        if (isScan) {
            api.setTime((Integer) MyApplication.getApp().getParam(getActivity(), "intervaltime", 800));
            clear.setEnabled(false);
            end.setEnabled(true);
            scan.setEnabled(false);
            scanning.setEnabled(false);
            isKey = false;
            api.ContinuousScanning();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "--onPause--");
        api.CloseScanning();
        api.closeBarCodeReceiver();
        MyApplication.getApp().setParam(getActivity(), "isScanning", isScanning);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "--onDestroyView--");
        unbinder.unbind();
        getActivity().unregisterReceiver(receiver);
    }


    @OnClick({R.id.scan, R.id.scanning, R.id.end, R.id.clear, R.id.tb})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.scan:
                api.scan();
                //isKey = false;

                break;
            case R.id.scanning:
                isScanning = true;
                api.setTime((Integer) MyApplication.getApp().getParam(getActivity(), "intervaltime", 800));
                clear.setEnabled(false);
                end.setEnabled(true);
                scan.setEnabled(false);
                scanning.setEnabled(false);
                isKey = false;
                api.ContinuousScanning();

                break;
            case R.id.end:
                isScanning = false;
                clear.setEnabled(true);
                end.setEnabled(false);
                scan.setEnabled(true);
                scanning.setEnabled(true);
                isKey = true;
                api.CloseScanning();

                break;
            case R.id.clear:
                etBarcode.setText("");
                success = 0;
                all = 0;
                tvSuccess.setText(getActivity().getString(R.string.barcode_scan_success, ""));
                tvAll.setText(getActivity().getString(R.string.barcode_scan_all, ""));
                break;

            case R.id.tb:


                break;
        }
    }


    //用来监听上层应用的广播,动态广播
    public class BarCodeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }

            switch (action) {
                case Intent.ACTION_SCREEN_ON:
                    Log.i(TAG, "亮屏了");

                    break;
                case Intent.ACTION_SCREEN_OFF:
                    Log.i(TAG, "息屏了");
                    //api.close();
                    isScanning = false;
                    MyApplication.getApp().setParam(getActivity(), "isScanning", isScanning);
                    clear.setEnabled(true);
                    end.setEnabled(false);
                    scan.setEnabled(true);
                    scanning.setEnabled(true);
                    isKey = true;
                    api.CloseScanning();
                    //api.closeBarCodeReceiver();
                    break;


                case Intent.ACTION_SHUTDOWN:
                    Log.i(TAG, TAG + "---关机了");
                    isScanning = false;
                    MyApplication.getApp().setParam(getActivity(), "isScanning", isScanning);

                    api.CloseScanning();
                    clear.setEnabled(true);
                    end.setEnabled(false);
                    scan.setEnabled(true);
                    scanning.setEnabled(true);
                    isKey = true;

                    break;
                case Intent.ACTION_REBOOT:
                    Log.i(TAG, TAG + "---亮屏了");
                    break;


            }
        }
    }


}
