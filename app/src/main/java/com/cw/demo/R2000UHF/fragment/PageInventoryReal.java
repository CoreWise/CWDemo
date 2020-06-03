package com.cw.demo.R2000UHF.fragment;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.cw.demo.R;
import com.cw.demo.R2000UHF.UHF2000Activity;
import com.cw.demo.R2000UHF.ui.AbstractSpinerAdapter;
import com.cw.demo.R2000UHF.ui.LogList;
import com.cw.demo.R2000UHF.ui.SpinerPopWindow;
import com.cw.demo.R2000UHF.ui.TagRealList;
import com.cw.r2000uhfsdk.IOnCommonReceiver;
import com.cw.r2000uhfsdk.IOnInventoryRealReceiver;
import com.cw.r2000uhfsdk.base.CMD;
import com.cw.r2000uhfsdk.helper.InventoryBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PageInventoryReal extends Fragment {



    private TextView mStart;
    private TextView mStop;

    private TextView mRefreshTemp;

    private LinearLayout mLayoutRealSession;

    private TextView mSessionIdTextView, mInventoriedFlagTextView;
    private TableRow mDropDownRow1, mDropDownRow2;



    private CheckBox mCbRealSession;

    private List<String> mSessionIdList;
    private List<String> mInventoriedFlagList;

    private SpinerPopWindow mSpinerPopWindow1, mSpinerPopWindow2;

    private EditText mRealRoundEditText;

    private TagRealList mTagRealList;


    private int mPos1 = 0, mPos2 = 0;

    private Handler handler;

    UHF2000Activity activity;
        View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity= (UHF2000Activity) getActivity();

        rootView = inflater.inflate(R.layout.r2000uhf_page_inventory_real,container,false);
        init();
        return rootView;
    }

    private void init() {

          mSessionIdList = new ArrayList<String>();
        mInventoriedFlagList = new ArrayList<String>();

        mRefreshTemp = rootView.findViewById(R.id.refresh);

        mRefreshTemp.setOnClickListener(setInventoryRealOnClickListener);


        mStart = (TextView)  rootView.findViewById(R.id.start);
        mStop = (TextView)  rootView.findViewById(R.id.stop);

        mCbRealSession = (CheckBox)  rootView.findViewById(R.id.check_real_session);
        mLayoutRealSession = (LinearLayout)  rootView.findViewById(R.id.layout_real_session);


        mSessionIdTextView = (TextView)  rootView.findViewById(R.id.session_id_text);
        mInventoriedFlagTextView = (TextView)  rootView.findViewById(R.id.inventoried_flag_text);
        mDropDownRow1 = (TableRow)  rootView.findViewById(R.id.table_row_session_id);
        mDropDownRow2 = (TableRow)  rootView.findViewById(R.id.table_row_inventoried_flag);

        mTagRealList = (TagRealList)  rootView.findViewById(R.id.tag_real_list);

        mRealRoundEditText = (EditText)  rootView.findViewById(R.id.real_round_text);


        mStart.setOnClickListener(setInventoryRealOnClickListener);

        mStop.setOnClickListener(setInventoryRealOnClickListener);


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

        mSessionIdList.clear();

        mInventoriedFlagList.clear();

        String[] lists = getResources().getStringArray(R.array.session_id_list);

        mSessionIdList.addAll(Arrays.asList(lists));

        lists = getResources().getStringArray(R.array.inventoried_flag_list);

        mInventoriedFlagList.addAll(Arrays.asList(lists));

        mSpinerPopWindow1 = new SpinerPopWindow(activity);

        mSpinerPopWindow1.refreshData(mSessionIdList, 0);

        mSpinerPopWindow1.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                setSessionIdText(pos);
            }
        });

        mSpinerPopWindow2 = new SpinerPopWindow(activity);

        mSpinerPopWindow2.refreshData(mInventoriedFlagList, 0);

        mSpinerPopWindow2.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                setInventoriedFlagText(pos);
            }
        });


        mCbRealSession.setChecked(false);
        mLayoutRealSession.setVisibility(View.GONE);


        mStop.setEnabled(activity.r2000UHFAPI.getReaderHelper().getInventoryFlag());
        mStart.setEnabled(!activity.r2000UHFAPI.getReaderHelper().getInventoryFlag());

        mCbRealSession.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (mCbRealSession.isChecked()) {
                    mLayoutRealSession.setVisibility(View.VISIBLE);
                    mSpinerPopWindow1.refreshData(mSessionIdList, 0);

                } else {
                    mLayoutRealSession.setVisibility(View.GONE);
                }
            }
        });
        //默认一条
        mRealRoundEditText.setText(String.valueOf("1"));


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.r2000UHFAPI.getReaderTemperature();
                handler.postDelayed(this, 1000);
            }
        }, 1000);

        activity.r2000UHFAPI.setOnCommonReceiver(new IOnCommonReceiver() {
            @Override
            public void onReceiver(byte cmd, Object result) {
                switch (cmd) {
                    case CMD.GET_READER_TEMPERATURE:
                        mRefreshTemp.setText((String) result);
                        //Log.i("TTTTTT", "Temp" + (String) result);

                        //  mDataEntries.add(new Entry(position++, (Float) result));


                        break;
                }
            }

            @Override
            public void onLog(String strLog, int type) {
                //mLogList.writeLog(strLog, type);
            }
        });

        activity.r2000UHFAPI.setOnInventoryRealReceiver(new IOnInventoryRealReceiver() {
            @Override
            public void realTimeInventory() {

            }

            @Override
            public void customized_session_target_inventory(InventoryBuffer inventoryBuffer) {

            }

            @Override
            public void inventoryErr() {

            }

            @Override
            public void inventoryErrEnd() {

            }

            @Override
            public void inventoryEnd(InventoryBuffer inventoryBuffer) {
                Log.i("TAGGGG", inventoryBuffer.toString());
                mTagRealList.refreshText(inventoryBuffer);
            }

            @Override
            public void inventoryRefresh(InventoryBuffer inventoryBuffer) {
                mTagRealList.refreshList(inventoryBuffer);
                InventoryBuffer.InventoryTagMap map = inventoryBuffer.newTagMap;
                Log.i("inventoryRefresh","inventoryRefresh");
                Log.i("inventoryRefresh","newMap = "+ map.toString());
            }

            @Override
            public void onLog(String strLog, int type) {
                activity.mLogList.writeLog(strLog, type);
            }
        });

        setSessionIdText(mPos1);
        setInventoriedFlagText(mPos2);

    }



    @SuppressLint("ResourceAsColor")
    @Override
    public void onPause() {
        super.onPause();


        activity.r2000UHFAPI.stopInventoryReal();
    }

    @Override
    public void onResume() {
        super.onResume();

        mStop.setEnabled(false);
        mStart.setEnabled(true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

    }




    private void setSessionIdText(int pos) {
        if (pos >= 0 && pos < mSessionIdList.size()) {
            String value = mSessionIdList.get(pos);
            mSessionIdTextView.setText(value);
            mPos1 = pos;
        }
    }

    private void setInventoriedFlagText(int pos) {
        if (pos >= 0 && pos < mInventoriedFlagList.size()) {
            String value = mInventoriedFlagList.get(pos);
            mInventoriedFlagTextView.setText(value);
            mPos2 = pos;
        }
    }

    private void showSpinWindow1() {
        mSpinerPopWindow1.setWidth(mDropDownRow1.getWidth());
        mSpinerPopWindow1.showAsDropDown(mDropDownRow1);
    }

    private void showSpinWindow2() {
        mSpinerPopWindow2.setWidth(mDropDownRow2.getWidth());
        mSpinerPopWindow2.showAsDropDown(mDropDownRow2);
    }


    private OnClickListener setInventoryRealOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.refresh:
                    Toast.makeText(activity, "查看历史温度", Toast.LENGTH_SHORT).show();


                    break;

                case R.id.start:
                    mStop.setEnabled(true);
                    mStart.setEnabled(false);
                    if (mCbRealSession.isChecked()) {
                        //Toast.makeText(PageInventoryReal.this, "自定义Session参数", Toast.LENGTH_SHORT).show();
                        activity.r2000UHFAPI.startInventoryReal(mRealRoundEditText.getText().toString(), mPos1, mPos2);

                    } else {
                        //Toast.makeText(PageInventoryReal.this, "未自定义Session参数", Toast.LENGTH_SHORT).show();
                        //cw.R2000UHFAPI().startInventoryReal(0x00, mRealRoundEditText.getText().toString());
                        activity.r2000UHFAPI.startInventoryReal(mRealRoundEditText.getText().toString());
                    }
                    break;

                case R.id.stop:

                    mStop.setEnabled(false);
                    mStart.setEnabled(true);
                    activity.r2000UHFAPI.stopInventoryReal();

                    break;
            }
        }
    };




}

