package com.cw.demo.ISO_15693;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.cw.demo.R;

import java.util.HashMap;


/**
 * Created by 金宇凡 on 2019/3/11.
 * ISO 15693,基于NFC扫描
 */
public class NFCISO15693Activity extends AppCompatActivity {
    private String TAG = NFCISO15693Activity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private ISOAdapter mAdapter;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] mWriteTagFilters; // 允许扫描的标签类型
    private String[][] mTechLists;// 允许扫描的标签类型

    private ISOShowView showView;
    private Tag mTag;

    private Handler mHandler = new Handler();
    private Runnable mReadRunnable = new Runnable() {
        @Override
        public void run() {
            //读卡
            NfcV mNfcV = NfcV.get(mTag);
            HashMap<Integer,String> hashMap = new HashMap<>();
            try {
                mNfcV.connect();
                ISONfcVUtil mNfcVutil = new ISONfcVUtil(mNfcV);

                showView.showView(mNfcVutil.getUID(),mNfcVutil.getBlockNumber(),mNfcVutil.getOneBlockSize());

                int maxNumber = mNfcVutil.getBlockNumber();
                for (int i = 0; i < maxNumber; i++) {
                    String s = mNfcVutil.readOneBlock(i);
                    if (hashMap.containsKey(i))
                    {
                        hashMap.remove(i);
                    }
                    hashMap.put(i,s);
                }
                mNfcV.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mAdapter.setData(hashMap);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.iso_demo_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();
        initNFC();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.iso_show_recycler);
        showView = (ISOShowView) findViewById(R.id.iso_show);
        mAdapter = new ISOAdapter();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //设置布局管理器
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置增加或删除条目的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置Adapter
        mRecyclerView.setAdapter(mAdapter);

        showView.setOnWriterListener(new ISOShowView.OnWriterListener() {
            @Override
            public void onWriter(final int blockNum, final String writeData) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        boolean b = writeBlock(mTag, blockNum, writeData);
                        if (b)
                        {

                        }
                        else
                        {
                            Toast.makeText(NFCISO15693Activity.this,getString(R.string.general_write_fail),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * 初始化扫描设备
     */
    private void initNFC() {
        // 获取nfc适配器，判断设备是否支持NFC功能
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, getString(R.string.nfc_15693_error), Toast.LENGTH_SHORT).show();
        } else if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, getString(R.string.nfc_15693_error_not_open), Toast.LENGTH_SHORT).show();
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

        ndef.addCategory("*/*");

        mWriteTagFilters = new IntentFilter[]{ndef};

        mTechLists = new String[][]{
                new String[]{NfcV.class.getName()}};
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //当该Activity接收到NFC标签时，运行该方法
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            mHandler.removeCallbacksAndMessages(null);
            mHandler.post(mReadRunnable);
        }
    }

    private boolean writeBlock(Tag tag, int blockNum, String writeData)
    {
        if (null == tag)
        {
            // TODO: 2019/3/7 没有NFC卡
            return false;
        }

        //读卡
        NfcV mNfcV = NfcV.get(tag);
        try {
            if (!mNfcV.isConnected())
            {
                mNfcV.connect();
            }
            ISONfcVUtil mNfcVutil = new ISONfcVUtil(mNfcV);
            int maxNumber = mNfcVutil.getBlockNumber();
            int blockSize = mNfcVutil.getOneBlockSize();

            if (blockNum >= maxNumber)
            {
                Toast.makeText(this,getString(R.string.nfc_15693_error_no_block), Toast.LENGTH_SHORT).show();
                mNfcV.close();
                return false;
            }

            if (writeData.length() > blockSize)
            {
                Toast.makeText(this,getString(R.string.nfc_15693_error_max)+blockSize, Toast.LENGTH_SHORT).show();
                mNfcV.close();
                return false;
            }

            boolean b = mNfcVutil.writeBlock(blockNum, strToByteArray(writeData));
            if (b)
            {
                Toast.makeText(this,getString(R.string.general_write_success), Toast.LENGTH_SHORT).show();
            }
            mNfcV.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * string类型转换成byte
     */
    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray = str.getBytes();
        return byteArray;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开启前台调度系统
        if (null != nfcAdapter) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, mWriteTagFilters, mTechLists);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != nfcAdapter) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
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
