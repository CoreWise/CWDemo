package com.cw.demo.R2000UHF.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.cw.demo.R;
import com.cw.demo.R2000UHF.UHF2000Activity;
import com.cw.demo.R2000UHF.ui.AbstractSpinerAdapter;
import com.cw.demo.R2000UHF.ui.HexEditTextBox;
import com.cw.demo.R2000UHF.ui.LogList;
import com.cw.demo.R2000UHF.ui.SpinerPopWindow;
import com.cw.demo.R2000UHF.ui.TagAccessList;
import com.cw.r2000uhfsdk.IOnTagOperation;
import com.cw.r2000uhfsdk.helper.InventoryBuffer;
import com.cw.r2000uhfsdk.helper.OperateTagBuffer;

import java.util.ArrayList;
import java.util.List;


public class PageTagAccess extends Fragment {


    private TextView mGet, mRead, mSelect, mWrite, mLock, mKill;

    private TextView mRefreshButton;

    private TextView mTagAccessListText;
    private TableRow mDropDownRow;

    private List<String> mAccessList;

    private SpinerPopWindow mSpinerPopWindow;

    private HexEditTextBox mPasswordEditText;
    private EditText mStartAddrEditText;
    private EditText mDataLenEditText;
    private HexEditTextBox mDataEditText;
    private HexEditTextBox mLockPasswordEditText;
    private HexEditTextBox mKillPasswordEditText;

    private RadioGroup mGroupAccessAreaType;
    private RadioGroup mGroupLockAreaType;
    private RadioGroup mGroupLockType;

    private TagAccessList mTagAccessList;


    private int mPos = -1;

    String btWordAdd;
    String btWordCnt;

    String btAryPassWord;
    byte btMemBank = 0x00;
    View rootView;

    UHF2000Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity = (UHF2000Activity) getActivity();

        rootView = inflater.inflate(R.layout.r2000uhf_page_tag_access, container, false);

        init();

        return rootView;
    }

    private void init() {

        mAccessList = new ArrayList<String>();

        mGet = (TextView) rootView.findViewById(R.id.get);
        mRead = (TextView) rootView.findViewById(R.id.read);
        mSelect = (TextView) rootView.findViewById(R.id.select);
        mWrite = (TextView) rootView.findViewById(R.id.write);
        mLock = (TextView) rootView.findViewById(R.id.lock);
        mKill = (TextView) rootView.findViewById(R.id.kill);
        mGet.setOnClickListener(setAccessOnClickListener);
        mRead.setOnClickListener(setAccessOnClickListener);
        mSelect.setOnClickListener(setAccessOnClickListener);
        mWrite.setOnClickListener(setAccessOnClickListener);
        mLock.setOnClickListener(setAccessOnClickListener);
        mKill.setOnClickListener(setAccessOnClickListener);

        mPasswordEditText = (HexEditTextBox) rootView.findViewById(R.id.password_text);
        mStartAddrEditText = (EditText) rootView.findViewById(R.id.start_addr_text);
        mDataLenEditText = (EditText) rootView.findViewById(R.id.data_length_text);
        mDataEditText = (HexEditTextBox) rootView.findViewById(R.id.data_write_text);
        mLockPasswordEditText = (HexEditTextBox) rootView.findViewById(R.id.lock_password_text);
        mKillPasswordEditText = (HexEditTextBox) rootView.findViewById(R.id.kill_password_text);

        mGroupAccessAreaType = (RadioGroup) rootView.findViewById(R.id.group_access_area_type);
        mGroupLockAreaType = (RadioGroup) rootView.findViewById(R.id.group_lock_area_type);
        mGroupLockType = (RadioGroup) rootView.findViewById(R.id.group_lock_type);

        mTagAccessListText = (TextView) rootView.findViewById(R.id.tag_access_list_text);
        mDropDownRow = (TableRow) rootView.findViewById(R.id.table_row_tag_access_list);

        mTagAccessList = (TagAccessList) rootView.findViewById(R.id.tag_access_list);


        mDropDownRow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpinWindow();
            }
        });

        mAccessList.clear();
        mAccessList.add("Cancel");
        InventoryBuffer curInventoryBuffer = activity.r2000UHFAPI.getCurInventoryBuffer();
        for (int i = 0; i < curInventoryBuffer.lsTagList.size(); i++) {
            mAccessList.add(curInventoryBuffer.lsTagList.get(i).strEPC);
        }

        mSpinerPopWindow = new SpinerPopWindow(activity);
        mSpinerPopWindow.refreshData(mAccessList, 0);
        mSpinerPopWindow.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                setAccessSelectText(pos);
            }
        });

        updateView();


        activity.r2000UHFAPI.setOnTagOperation(new IOnTagOperation() {
            @Override
            public void getAccessEpcMatch(OperateTagBuffer m_curOperateTagBuffer) {
                mTagAccessListText.setText(m_curOperateTagBuffer.strAccessEpcMatch);

            }

            @Override
            public void readTagResult(OperateTagBuffer m_curOperateTagBuffer) {
                mTagAccessList.refreshList(m_curOperateTagBuffer);
            }

            @Override
            public void writeTagResult(String mDataLen) {
                mDataLenEditText.setText(mDataLen);

            }

            @Override
            public void lockTagResult() {

            }

            @Override
            public void killTagResult() {

            }

            @Override
            public void onLog(String strLog, int type) {
                activity.mLogList.writeLog(strLog, type);
            }
        });


    }


    private void setAccessSelectText(int pos) {
        if (pos >= 0 && pos < mAccessList.size()) {
            String value = mAccessList.get(pos);
            mTagAccessListText.setText(value);
            mPos = pos;
        }
    }

    private void showSpinWindow() {
        mSpinerPopWindow.setWidth(mDropDownRow.getWidth());
        mSpinerPopWindow.showAsDropDown(mDropDownRow);
    }

    private void updateView() {
        if (mPos < 0) {
            mPos = 0;
        }
        setAccessSelectText(mPos);
    }


    private OnClickListener setAccessOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            switch (arg0.getId()) {
                case R.id.get:
                    activity.r2000UHFAPI.getAccessEpcMatch();
                    break;

                case R.id.select:

                    activity.r2000UHFAPI.setAccessEpcMatch(mPos, mAccessList);

                    break;

                case R.id.read:

                    btWordAdd = mStartAddrEditText.getText().toString();
                    btWordCnt = mDataLenEditText.getText().toString();
                    btAryPassWord = mPasswordEditText.getText().toString().toUpperCase();


                    if (btAryPassWord.equals("")) {
                        Toast.makeText(activity, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    } else if (btWordAdd.equals("")) {
                        Toast.makeText(activity, "起始地址不能为空！", Toast.LENGTH_SHORT).show();
                    } else if (btWordCnt.equals("")) {
                        Toast.makeText(activity, "数据长度不能为空！", Toast.LENGTH_SHORT).show();
                    }

                    if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_password) {
                        btMemBank = 0x00;
                    } else if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_epc) {
                        btMemBank = 0x01;
                    } else if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_tid) {
                        btMemBank = 0x02;
                    } else if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_user) {
                        btMemBank = 0x03;
                    }


                    activity.r2000UHFAPI.readTag(btMemBank, btWordAdd, btWordCnt, btAryPassWord);

                    break;
                case R.id.write:

                    btWordAdd = mStartAddrEditText.getText().toString();
                    btWordCnt = mDataLenEditText.getText().toString();
                    btAryPassWord = mPasswordEditText.getText().toString().toUpperCase();

                    String data = mDataEditText.getText().toString();

                    if (btAryPassWord.equals("")) {
                        Toast.makeText(activity, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    } else if (btWordAdd.equals("")) {
                        Toast.makeText(activity, "起始地址不能为空！", Toast.LENGTH_SHORT).show();
                    } else if (data.equals("")) {
                        Toast.makeText(activity, "写入数据不能为空！", Toast.LENGTH_SHORT).show();
                    }

                    if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_password) {
                        btMemBank = 0x00;
                    } else if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_epc) {
                        btMemBank = 0x01;
                    } else if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_tid) {
                        btMemBank = 0x02;
                    } else if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_user) {
                        btMemBank = 0x03;
                    }


                    activity.r2000UHFAPI.writeTag(btMemBank, btWordAdd, btWordCnt, btAryPassWord, data);
                    break;


                case R.id.lock:

                    byte btMemBank = 0x00;
                    byte btLockType = 0x00;
                    if (mGroupLockAreaType.getCheckedRadioButtonId() == R.id.set_lock_area_access_password) {
                        btMemBank = 0x04;
                    } else if (mGroupLockAreaType.getCheckedRadioButtonId() == R.id.set_lock_area_kill_password) {
                        btMemBank = 0x05;
                    } else if (mGroupLockAreaType.getCheckedRadioButtonId() == R.id.set_lock_area_epc) {
                        btMemBank = 0x03;
                    } else if (mGroupLockAreaType.getCheckedRadioButtonId() == R.id.set_lock_area_tid) {
                        btMemBank = 0x02;
                    } else if (mGroupLockAreaType.getCheckedRadioButtonId() == R.id.set_lock_area_user) {
                        btMemBank = 0x01;
                    }

                    if (mGroupLockType.getCheckedRadioButtonId() == R.id.set_lock_free) {
                        btLockType = 0x00;
                    } else if (mGroupLockType.getCheckedRadioButtonId() == R.id.set_lock_free_ever) {
                        btLockType = 0x02;
                    } else if (mGroupLockType.getCheckedRadioButtonId() == R.id.set_lock_lock) {
                        btLockType = 0x01;
                    } else if (mGroupLockType.getCheckedRadioButtonId() == R.id.set_lock_lock_ever) {
                        btLockType = 0x03;
                    }


                    String btAryPassWord = mLockPasswordEditText.getText().toString().toUpperCase();

                    if (btAryPassWord.equals("")) {
                        Toast.makeText(activity, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    }

                    activity.r2000UHFAPI.lockTag(btAryPassWord, btMemBank, btLockType);
                    break;

                case R.id.kill:

                    String killAryPassWord = mKillPasswordEditText.getText().toString().toUpperCase();

                    if (killAryPassWord.equals("")) {
                        Toast.makeText(activity, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    }
                    activity.r2000UHFAPI.killTag(killAryPassWord);

                    break;

            }
        }
    };


}

