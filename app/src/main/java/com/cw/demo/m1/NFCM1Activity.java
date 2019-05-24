package com.cw.demo.m1;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import com.cw.demo.R;
import com.cw.serialportsdk.utils.DataUtils;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 *
 * NFC读M1卡是调用Android标准接口
 */

public class NFCM1Activity extends AppCompatActivity {


    private static final String TAG = "NFCM1Activity";
    @BindView(R.id.tv_m1_type)
    AppCompatTextView tvM1Type;
    @BindView(R.id.tv_m1_passwordA)
    AppCompatEditText tvM1PasswordA;
    @BindView(R.id.tv_m1_passwordB)
    AppCompatEditText tvM1PasswordB;
    @BindView(R.id.sp_m1_blocknum)
    AppCompatSpinner spM1Blocknum;
    @BindView(R.id.tv_m1_blockResult)
    AppCompatTextView tvM1BlockResult;
    @BindView(R.id.tv_sectornum)
    AppCompatTextView tvSectornum;
    @BindView(R.id.tv_data)
    AppCompatEditText tvData;
    @BindView(R.id.btn_write)
    AppCompatButton btnWrite;


    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] mWriteTagFilters;        // 允许扫描的标签类型

    private String[][] mTechLists;// 允许扫描的标签类型


    private ArrayList<Integer> blockDatas = new ArrayList<Integer>();

    private MifareClassic mfc;
    private Tag tag;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nfc_m1);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ButterKnife.bind(this);
        setTitle("NFC读M1卡演示Demo");

        initNFC();
        initEvent();

    }

    private void initEvent() {

        spM1Blocknum.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                //Toast.makeText(NFCM1Activity.this, "Add:"+spM1Blocknum.getSelectedItemPosition(), Toast.LENGTH_SHORT).show();
                tvSectornum.setText("扇区: " + spM1Blocknum.getSelectedItemPosition() / 4);
                try {
                    if (mfc.isConnected()) {
                        byte[] bytes = mfc.readBlock(spM1Blocknum.getSelectedItemPosition());

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                //Toast.makeText(NFCM1Activity.this, "Remove:"+spM1Blocknum.getSelectedItemPosition(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //开启前台调度系统

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, mWriteTagFilters, mTechLists);

    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //当该Activity接收到NFC标签时，运行该方法
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {

            tvM1PasswordA.setError(null);
            tvM1PasswordB.setError(null);
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            String[] techList = tag.getTechList();
            boolean haveMifareUltralight = false;

            for (String tech : techList) {
                if (tech.indexOf("MifareClassic") >= 0) {
                    haveMifareUltralight = true;
                    break;
                }
            }
            if (!haveMifareUltralight) {
                Toast.makeText(this, "不支持MifareClassic", Toast.LENGTH_LONG).show();
                return;
            }

            //有卡过来先读卡号,M1卡基本信息
            mfc = MifareClassic.get(tag);
            try {
                mfc.connect();

                int sectorCount = mfc.getSectorCount();
                if (sectorCount == 16) {
                    tvM1Type.setText("S50");
                } else {
                    tvM1Type.setText("S70");
                }

                int blockCount = mfc.getBlockCount();

                int selectedItemPosition = spM1Blocknum.getSelectedItemPosition();
                blockDatas.clear();
                for (int i = 0; i < blockCount; i++) {
                    blockDatas.add(i);
                }
                ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, blockDatas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spM1Blocknum.setAdapter(adapter);

                if (selectedItemPosition == -1) {
                    spM1Blocknum.setSelection(0);
                    selectedItemPosition = 0;
                }
                if (selectedItemPosition <= blockCount) {
                    spM1Blocknum.setSelection(selectedItemPosition);
                }

                mfc.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            readBlock(tag, spM1Blocknum.getSelectedItemPosition());

        }
    }


    public String readBlock(Tag tag, int block) {
        mfc = MifareClassic.get(tag);
        for (String tech : tag.getTechList()) {
            Log.e(TAG, tech);
        }
        boolean auth = false;
        //读取TAG

        try {
            String metaInfo = "";
            mfc.connect();
            //获取TAG的类型
            auth = mfc.authenticateSectorWithKeyA(block / 4, DataUtils.hexStringTobyte(tvM1PasswordA.getText().toString()));
            //auth = mfc.authenticateSectorWithKeyA(block / 4, MifareClassic.KEY_DEFAULT);

            if (auth) {
                // 读取扇区中的块
                byte[] data = mfc.readBlock(block);
                tvM1PasswordA.setError(null);
                tvM1BlockResult.setText(DataUtils.toHexString(data));
            } else {
                tvM1PasswordA.setError("密码A验证失败!");
                tvM1BlockResult.setText("");
            }
            return metaInfo;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (mfc != null) {
                try {
                    mfc.close();
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        return null;

    }


    public boolean writeBlock(Tag tag, int block, String data) {
        mfc = MifareClassic.get(tag);
        for (String tech : tag.getTechList()) {
            Log.e(TAG, tech);
        }
        boolean auth = false;
        try {
            mfc.connect();
            auth = mfc.authenticateSectorWithKeyA(block / 4, DataUtils.hexStringTobyte(tvM1PasswordA.getText().toString()));
            if (auth) {

                if (data.length() < 32) {

                }

                mfc.writeBlock(block, DataUtils.hexStringTobyte(data));
                mfc.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }


    private void initNFC() {
        // 获取nfc适配器，判断设备是否支持NFC功能
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "当前设备不支持NFC功能", Toast.LENGTH_SHORT).show();
        } else if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC功能未打开，请先开启后重试！", Toast.LENGTH_SHORT).show();
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

        ndef.addCategory("*/*");

        mWriteTagFilters = new IntentFilter[]{ndef};

        mTechLists = new String[][]{
                new String[]{MifareClassic.class.getName()},
                new String[]{NfcA.class.getName()}};
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.btn_write)
    public void onViewClicked() {
        tvData.setError(null);

        if (mfc==null) {
            Toast.makeText(NFCM1Activity.this, "mfc is null!", Toast.LENGTH_SHORT).show();
            return;
        }

        String data = tvData.getText().toString();
        if (data.length() < 32) {
            Toast.makeText(this, "写入数据小于32", Toast.LENGTH_SHORT).show();
        } else {
            if (data.length() == 32) {
                boolean b = writeBlock(tag, spM1Blocknum.getSelectedItemPosition(), data);
                if (b) {
                    //Drawable dd=n;
                    Drawable dr = NFCM1Activity.this.getDrawable(R.drawable.ic_pass);
                                dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());

                    tvData.setError("写入数据成功",dr);
                } else {

                    tvData.setError("写入数据失败");
                }
            }
        }

    }

        @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.general_tips);
            builder.setMessage(R.string.general_exit);

            //设置确定按钮
            builder.setNegativeButton(R.string.general_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            //设置取消按钮
            builder.setPositiveButton(R.string.general_no, null);
            //显示提示框
            builder.show();
        }
        return super.onKeyDown(keyCode, event);
    }

}

