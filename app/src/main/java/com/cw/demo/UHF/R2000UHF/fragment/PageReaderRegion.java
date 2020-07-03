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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TableRow;
import android.widget.TextView;


import com.cw.demo.R;
import com.cw.demo.UHF.R2000UHF.UHF2000Activity;
import com.cw.demo.UHF.R2000UHF.ui.AbstractSpinerAdapter;
import com.cw.demo.UHF.R2000UHF.ui.SpinerPopWindow;
import com.cw.r2000uhfsdk.IOnRegionReceiver;

import java.util.ArrayList;
import java.util.List;


public class PageReaderRegion extends Fragment {


    private LinearLayout mRegionDefaultLayout, mRegionCustomLayout;

    private TextView mGet, mSet;

    private RadioGroup mGroupRegion;
    private RadioGroup mGroupRegionDefaultType;
    private TextView mFreqStartText, mFreqEndText;
    private TableRow mDropDownRow1, mDropDownRow2;
    private List<String> mFreqStartList = new ArrayList<String>();
    private List<String> mFreqEndList = new ArrayList<String>();

    private EditText mFreqStartEditText, mFreqIntervalEditText, mFreqNumsEditText;


    private SpinerPopWindow mSpinerPopWindow1;
    private SpinerPopWindow mSpinerPopWindow2;

    private int mPos1 = -1, mPos2 = -1;


    private LocalBroadcastManager lbm;

    private View rootView;

    private UHF2000Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.r2000uhf_page_reader_region, container, false);
        activity= (UHF2000Activity) getActivity();
        init();
        return rootView;
    }

    private void init() {
        mGroupRegion = (RadioGroup) rootView.findViewById(R.id.group_region);
        mGroupRegionDefaultType = (RadioGroup) rootView.findViewById(R.id.group_region_default_type);
        mFreqStartText = (TextView) rootView.findViewById(R.id.freq_start_text);
        mFreqEndText = (TextView) rootView.findViewById(R.id.freq_end_text);

        mFreqStartEditText = (EditText) rootView.findViewById(R.id.set_freq_start_text);
        mFreqIntervalEditText = (EditText) rootView.findViewById(R.id.set_freq_interval_text);
        mFreqNumsEditText = (EditText) rootView.findViewById(R.id.set_freq_nums_text);

        mDropDownRow1 = (TableRow) rootView.findViewById(R.id.table_row_spiner_freq_start);
        mDropDownRow2 = (TableRow) rootView.findViewById(R.id.table_row_spiner_freq_end);

        mRegionDefaultLayout = (LinearLayout) rootView.findViewById(R.id.layout_region_default);
        mRegionCustomLayout = (LinearLayout) rootView.findViewById(R.id.layout_region_custom);

        mGet = (TextView) rootView.findViewById(R.id.get);
        mSet = (TextView) rootView.findViewById(R.id.set);

        mGet.setOnClickListener(setRegionOnClickListener);
        mSet.setOnClickListener(setRegionOnClickListener);


        if (mGroupRegion.getCheckedRadioButtonId() == R.id.set_region_default) {
            mRegionDefaultLayout.setVisibility(View.VISIBLE);
            mRegionCustomLayout.setVisibility(View.GONE);
        } else if (mGroupRegion.getCheckedRadioButtonId() == R.id.set_region_custom) {
            mRegionDefaultLayout.setVisibility(View.GONE);
            mRegionCustomLayout.setVisibility(View.VISIBLE);
        }

        mGroupRegion.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mGroupRegion.getCheckedRadioButtonId() == R.id.set_region_default) {
                    mRegionDefaultLayout.setVisibility(View.VISIBLE);
                    mRegionCustomLayout.setVisibility(View.GONE);
                } else if (mGroupRegion.getCheckedRadioButtonId() == R.id.set_region_custom) {
                    mRegionDefaultLayout.setVisibility(View.GONE);
                    mRegionCustomLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        mGroupRegionDefaultType.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mFreqStartText.setText("");
                mFreqEndText.setText("");
                changeData();
            }

        });


        mDropDownRow1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpinWindow1();
            }
        });

        mDropDownRow2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpinWindow2();
            }
        });


        mSpinerPopWindow1 = new SpinerPopWindow(getContext());

        mSpinerPopWindow1.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                setFreqStartText(pos);
            }
        });

        mSpinerPopWindow2 = new SpinerPopWindow(getContext());

        mSpinerPopWindow2.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                setFreqEndText(pos);
            }
        });

        changeData();

        activity.r2000UHFAPI.setOnRegionReceiver(new IOnRegionReceiver() {
            @Override
            public void onRegionReceiver(int Region, int FreqStart, int FreqEnd) {

                switch (Region) {
                    case 0x01:
                        mGroupRegion.check(R.id.set_region_default);
                        mFreqStartEditText.setText("");
                        mFreqIntervalEditText.setText("");
                        mFreqNumsEditText.setText("");
                        mGroupRegionDefaultType.check(R.id.set_region_fcc);
                        changeData();
                        mPos1 = FreqStart - 7;
                        mPos2 = FreqEnd - 7;
                        if (mPos1 >= 0 && mPos1 < mFreqStartList.size()) {
                            mFreqStartText.setText(mFreqStartList.get(mPos1));
                        }

                        if (mPos2 >= 0 && mPos2 < mFreqEndList.size()) {
                            mFreqEndText.setText(mFreqEndList.get(mPos2));
                        }
                        break;
                    case 0x02:
                        mGroupRegion.check(R.id.set_region_default);
                        mFreqStartEditText.setText("");
                        mFreqIntervalEditText.setText("");
                        mFreqNumsEditText.setText("");
                        mGroupRegionDefaultType.check(R.id.set_region_etsi);
                        changeData();
                        mPos1 = FreqStart;
                        mPos2 = FreqEnd;
                        if (mPos1 >= 0 && mPos1 < mFreqStartList.size()) {
                            mFreqStartText.setText(mFreqStartList.get(mPos1));
                        }

                        if (mPos2 >= 0 && mPos2 < mFreqEndList.size()) {
                            mFreqEndText.setText(mFreqEndList.get(mPos2));
                        }
                        break;
                    case 0x03:
                        mGroupRegion.check(R.id.set_region_default);
                        mFreqStartEditText.setText("");
                        mFreqIntervalEditText.setText("");
                        mFreqNumsEditText.setText("");
                        mGroupRegionDefaultType.check(R.id.set_region_chn);
                        changeData();
                        mPos1 = FreqStart - 43;
                        mPos2 = FreqEnd - 43;
                        if (mPos1 >= 0 && mPos1 < mFreqStartList.size()) {
                            mFreqStartText.setText(mFreqStartList.get(mPos1));
                        }

                        if (mPos2 >= 0 && mPos2 < mFreqEndList.size()) {
                            mFreqEndText.setText(mFreqEndList.get(mPos2));
                        }
                        break;

                    default:
                        break;
                }
            }

            /*@Override
            public void onRegionReceiver(ReaderSetting m_curReaderSetting) {

                mGroupRegion.check(R.id.set_region_custom);
                mFreqStartEditText.setText(String.valueOf(m_curReaderSetting.nUserDefineStartFrequency));
                mFreqIntervalEditText.setText(String.valueOf((m_curReaderSetting.btUserDefineFrequencyInterval & 0xFF) * 10));
                mFreqNumsEditText.setText(String.valueOf((m_curReaderSetting.btUserDefineChannelQuantity & 0xFF)));

            }*/

            @Override
            public void onLog(String strLog, int type) {
                activity.mLogList.writeLog(strLog, type);
            }
        });
    }


    private void showSpinWindow1() {
        mSpinerPopWindow1.setWidth(mDropDownRow1.getWidth());
        mSpinerPopWindow1.showAsDropDown(mDropDownRow1);
    }

    private void showSpinWindow2() {
        mSpinerPopWindow2.setWidth(mDropDownRow2.getWidth());
        mSpinerPopWindow2.showAsDropDown(mDropDownRow2);
    }

    private void changeData() {
        float nStart = 0x0;
        int nloop = 0;

        mFreqStartList.clear();
        mFreqEndList.clear();
        if (mGroupRegionDefaultType.getCheckedRadioButtonId() == R.id.set_region_fcc) {
            nStart = 902.00f;
            for (nloop = 0; nloop < 53; nloop++) {
                String strTemp = String.format("%.2f", nStart);
                mFreqStartList.add(strTemp);
                mFreqEndList.add(strTemp);
                nStart += 0.5f;
            }
        } else if (mGroupRegionDefaultType.getCheckedRadioButtonId() == R.id.set_region_etsi) {
            nStart = 865.00f;
            for (nloop = 0; nloop < 7; nloop++) {
                String strTemp = String.format("%.2f", nStart);
                mFreqStartList.add(strTemp);
                mFreqEndList.add(strTemp);
                nStart += 0.5f;
            }
        } else if (mGroupRegionDefaultType.getCheckedRadioButtonId() == R.id.set_region_chn) {
            nStart = 920.00f;
            for (nloop = 0; nloop < 11; nloop++) {
                String strTemp = String.format("%.2f", nStart);
                mFreqStartList.add(strTemp);
                mFreqEndList.add(strTemp);
                nStart += 0.5f;
            }
        }

        mSpinerPopWindow1.refreshData(mFreqStartList, 0);
        mSpinerPopWindow2.refreshData(mFreqEndList, 0);

    }

    private void setFreqStartText(int pos) {
        if (pos >= 0 && pos < mFreqStartList.size()) {
            String value = mFreqStartList.get(pos);
            mFreqStartText.setText(value);
            mPos1 = pos;
        }
    }

    private void setFreqEndText(int pos) {
        if (pos >= 0 && pos < mFreqStartList.size()) {
            String value = mFreqStartList.get(pos);
            mFreqEndText.setText(value);
            mPos2 = pos;
        }
    }


    private OnClickListener setRegionOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            switch (arg0.getId()) {
                case R.id.get:

                    activity.r2000UHFAPI.getFrequencyRegion();

                    break;
                case R.id.set:

                    byte btRegion = 0x00, btStartFreq = 0x00, btEndFreq = 0x00;

                    if (mGroupRegion.getCheckedRadioButtonId() == R.id.set_region_default) {
                        if (mGroupRegionDefaultType.getCheckedRadioButtonId() == R.id.set_region_fcc) {
                            btRegion = 0x01;
                            btStartFreq = (byte) (mPos1 + 7);
                            btEndFreq = (byte) (mPos2 + 7);
                        } else if (mGroupRegionDefaultType.getCheckedRadioButtonId() == R.id.set_region_etsi) {
                            btRegion = 0x02;
                            btStartFreq = (byte) (mPos1);
                            btEndFreq = (byte) (mPos2);
                        } else if (mGroupRegionDefaultType.getCheckedRadioButtonId() == R.id.set_region_chn) {
                            btRegion = 0x03;
                            btStartFreq = (byte) (mPos1 + 43);
                            btEndFreq = (byte) (mPos2 + 43);
                        } else {
                            return;
                        }
                        activity.r2000UHFAPI.setFrequencyRegion(btRegion, btStartFreq, btEndFreq);
                    } else if (mGroupRegion.getCheckedRadioButtonId() == R.id.set_region_custom) {

                        int nStartFrequency = 0;
                        int nFrequencyInterval = 0;
                        byte btChannelQuantity = 0;
                        try {
                            nStartFrequency = Integer.parseInt(mFreqStartEditText.getText().toString());
                            nFrequencyInterval = Integer.parseInt(mFreqIntervalEditText.getText().toString());
                            nFrequencyInterval = nFrequencyInterval / 10;
                            btChannelQuantity = (byte) Integer.parseInt(mFreqNumsEditText.getText().toString());
                        } catch (Exception e) {
                            return;
                        }
                        activity.r2000UHFAPI.setUserDefineFrequencyRegion(nStartFrequency, nFrequencyInterval, btChannelQuantity);
                    }

                    break;
            }
        }
    };
}

