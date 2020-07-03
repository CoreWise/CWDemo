package com.cw.demo.UHF.R2000UHF.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cw.demo.R;
import com.cw.demo.UHF.R2000UHF.UHF2000Activity;
import com.cw.r2000uhfsdk.IOnCommonReceiver;
import com.cw.r2000uhfsdk.base.CMD;


public class PageReaderProfile extends Fragment {


    private TextView mSet, mGet;

    private RadioGroup mGroupProfile;

    UHF2000Activity activity;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity= (UHF2000Activity) getActivity();
        rootView = inflater.inflate(R.layout.r2000uhf_page_reader_profile, container, false);

        init();

        return rootView;
    }

    private void init() {

        mSet = (TextView) rootView.findViewById(R.id.set);
        mGet = (TextView) rootView.findViewById(R.id.get);
        mGroupProfile = (RadioGroup) rootView.findViewById(R.id.group_profile);

        mSet.setOnClickListener(setProfileOnClickListener);
        mGet.setOnClickListener(setProfileOnClickListener);


        activity.r2000UHFAPI.setOnCommonReceiver(new IOnCommonReceiver() {
            @Override
            public void onReceiver(byte cmd, Object result) {
                if (cmd == CMD.GET_RF_LINK_PROFILE) {

                    if ((int) result == 0xD0) {
                        mGroupProfile.check(R.id.set_profile_option0);
                    } else if ((int) result == 0xD1) {
                        mGroupProfile.check(R.id.set_profile_option1);
                    } else if ((int) result == 0xD2) {
                        mGroupProfile.check(R.id.set_profile_option2);
                    } else if ((int) result == 0xD3) {
                        mGroupProfile.check(R.id.set_profile_option3);
                    }

                }
            }

            @Override
            public void onLog(String strLog, int type) {
                activity.mLogList.writeLog(strLog, type);
            }
        });
    }


    private OnClickListener setProfileOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            switch (arg0.getId()) {
                case R.id.get:
                    activity.r2000UHFAPI.getRfLinkProfile();
                    break;
                case R.id.set:

                    byte btProfile = 0;
                    switch (mGroupProfile.getCheckedRadioButtonId()) {
                        case R.id.set_profile_option0:
                            btProfile = (byte) 0xD0;
                            break;
                        case R.id.set_profile_option1:
                            btProfile = (byte) 0xD1;
                            break;
                        case R.id.set_profile_option2:
                            btProfile = (byte) 0xD2;
                            break;
                        case R.id.set_profile_option3:
                            btProfile = (byte) 0xD3;
                            break;
                        default:
                            return;
                    }
                    activity.r2000UHFAPI.setRfLinkProfile(btProfile);
            }
        }
    };


}

