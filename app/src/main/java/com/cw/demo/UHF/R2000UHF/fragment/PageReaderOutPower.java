package com.cw.demo.UHF.R2000UHF.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.cw.demo.R;
import com.cw.demo.UHF.R2000UHF.UHF2000Activity;
import com.cw.r2000uhfsdk.IOnCommonReceiver;
import com.cw.r2000uhfsdk.base.CMD;


public class PageReaderOutPower extends Fragment {


    private TextView mSet;
    private TextView mGet;

    private EditText mOutPowerText;


    private LocalBroadcastManager lbm;

        View rootView;

        UHF2000Activity activity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        activity= (UHF2000Activity) getActivity();
        rootView = inflater.inflate(R.layout.r2000uhf_page_reader_out_power,container,false);

        init();

        return rootView;
    }

    private void init() {
         mSet = (TextView) rootView.findViewById(R.id.set);
        mGet = (TextView) rootView.findViewById(R.id.get);
        mOutPowerText = (EditText) rootView.findViewById(R.id.out_power_text);

        mSet.setOnClickListener(setOutPowerOnClickListener);

        mGet.setOnClickListener(setOutPowerOnClickListener);


        activity.r2000UHFAPI.setOnCommonReceiver(new IOnCommonReceiver() {
            @Override
            public void onReceiver(byte cmd, Object result) {
                switch (cmd) {

                    case CMD.GET_OUTPUT_POWER:

                        mOutPowerText.setText((String) result);

                        break;


                }
            }

            @Override
            public void onLog(String strLog, int type) {

                activity.mLogList.writeLog(strLog, type);
            }
        });
    }






    private OnClickListener setOutPowerOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            switch (arg0.getId()) {
                case R.id.get:

                    activity.r2000UHFAPI.getOutputPower();
                    break;
                case R.id.set:
                    String s = mOutPowerText.getText().toString();
                    if (s.equals("")) {
                        Toast.makeText(activity, "请输入功率！", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int outpower = Integer.parseInt(s);
                    if (outpower < 0 || outpower > 33) {
                        Toast.makeText(activity, "请输入功率（0 ~ 33dBm）！", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    activity.r2000UHFAPI.setOutputPower(outpower);
                    break;
            }
        }
    };




}

