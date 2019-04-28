package com.cw.demo.R2000UHF.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.cw.demo.R;
import com.cw.demo.R2000UHF.UHF2000Activity;
import com.cw.r2000uhfsdk.IOnCommonReceiver;
import com.cw.r2000uhfsdk.base.CMD;


public class PageReaderTemperature extends Fragment {


    private TextView mGet;

    private EditText mTemperatureEditText;


    private LocalBroadcastManager lbm;


    UHF2000Activity activity;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (UHF2000Activity) getActivity();

        rootView = inflater.inflate(R.layout.r2000uhf_page_reader_temperature, container, false);

        init();

        return rootView;
    }

    private void init() {
        mGet = (TextView) rootView.findViewById(R.id.get);
        mTemperatureEditText = (EditText) rootView.findViewById(R.id.temperature_text);

        mGet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                activity.r2000UHFAPI.getReaderTemperature();
            }
        });

        activity.r2000UHFAPI.setOnCommonReceiver(new IOnCommonReceiver() {

            @Override
            public void onReceiver(byte cmd, Object result) {
                switch (cmd) {
                    case CMD.GET_READER_TEMPERATURE:
                        mTemperatureEditText.setText((String) result);

                        break;
                }
            }

            @Override
            public void onLog(String strLog, int type) {
                activity.mLogList.writeLog(strLog, type);
            }
        });
    }


}

