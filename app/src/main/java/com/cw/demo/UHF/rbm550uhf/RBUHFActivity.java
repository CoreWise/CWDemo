package com.cw.demo.UHF.rbm550uhf;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cowise.rbm550uhfsdk.RBUFHAPI;
import com.cw.demo.MyApplication;
import com.cw.demo.R;
import com.cw.serialportsdk.cw;
import com.cw.serialportsdk.utils.DataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.cw.serialportsdk.cw.Device_U5;

/**
 * Created by 金宇凡 on 2020/6/23.
 */
public class RBUHFActivity extends AppCompatActivity {

    private String TAG = RBUHFActivity.class.getSimpleName();

    private ToggleButton buttonConnect;
    private ToggleButton buttonInv;
    private Button singleSearch;
    private TextView txtCount;
    private TextView txtTimes;
    public RBUFHAPI api;
    private ProgressDialog prgDlg;

    private SoundPool soundPool;
    private MediaPlayer mediaPlayer;

    private boolean isStop;
    private RBTagListFragment objFragment;

    private TextView txtReadEpc;
    private TextView txtReadResult;

    private List<String> tagInfoList = new ArrayList<>();
    protected static HashMap<String, Integer> number = new HashMap<String, Integer>();
    protected static HashMap<String, Long> readTime = new HashMap<String, Long>();
    private int tagCount = 0;
    private int tagTimes = 0;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            api.inventory();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_rbufh);

        api = new RBUFHAPI();
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer = MediaPlayer.create(this, R.raw.ok);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ok);
                        mediaPlayer.setOnErrorListener(this);
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        if (buttonInv.isChecked()) {
            buttonInv.setChecked(false);
            buttonInv.setClickable(false);
        }
        if (buttonConnect.isChecked()) {
            buttonConnect.setChecked(false);
        }

        api.closeRBUHFSerialPort(Device_U5);

        MyApplication.getApp().maintainScannerService();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mediaPlayer = null;
        super.onDestroy();
    }

    private void initData() {
        soundPool = new SoundPool(50, AudioManager.STREAM_SYSTEM, 5);
    }

    private void initListener() {
        singleSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int temperature = api.getTemperature();
                Log.i(TAG, "temperature = " + temperature);

//                RBUFHAPI.CmdResponse frequencyRegion = api.getFrequencyRegion();
//                Log.i(TAG, "frequencyRegion = " + frequencyRegion.respondData);

//                int errorCode = api.setPower(RBUFHConfig.Max_Power);
//                Log.i(TAG, "errorCode = " + errorCode);

//                RBUFHAPI.CmdResponse power = api.getPower();
//                Log.i(TAG, "power = " + power.respondData);

//                int BaudRate = api.setBaudRate(RBUFHConfig.BaudRate_115200);
//                Log.i(TAG, "BaudRate = " + BaudRate);
//                Log.i(TAG, "BaudRate = " + (BaudRate == RBUFHConfig.command_success));


//                String version = api.getVersion();
//                Log.i(TAG,"version = "+version);

//                int reset = api.reset();
//                Log.i(TAG,"reset = "+(reset == RBUFHConfig.command_success));
            }
        });

        //盘点
        api.setOnInventoryRespondListener(new RBUFHAPI.onInventoryRespondListener() {
            @Override
            public void onRespondSuccess(String address, String cmd, String freqAnt, String PC, String EPC, String RSSI) {
                Log.i(TAG, "Inventory onRespondSuccess");
                long l = System.currentTimeMillis();
                readTime.put(EPC, l);
                ShowEPC(EPC);
                isStopInventory();
            }

            @Override
            public void onRespondFailure(int errorCode, String respond) {
                Log.i(TAG, "Inventory onRespondFailure");
                isStopInventory();
            }

            @Override
            public void onReaderSuccess(String address, String cmd, String antID, String readRate, String totalRead) {
                Log.i(TAG, "Inventory onReaderSuccess");
                isStopInventory();
            }

            @Override
            public void onReaderFailure(String address, String cmd, String errorCode) {
                Log.i(TAG, "Inventory onReaderFailure");
                isStopInventory();
            }

            @Override
            public void TimeOut() {
                Log.i(TAG, "Inventory TimeOut");
                isStopInventory();
            }
        });

        //读写
        api.setOnReadWriteRespondListener(new RBUFHAPI.onReadWriteRespondListener() {
            @Override
            public void onRespondSuccess(String address, String cmd, String tagCount, String data, String epc, String readData) {
                Log.i(TAG, "Read onRespondSuccess");
                txtReadEpc.setText(epc);
                txtReadResult.setText(readData);
            }

            @Override
            public void onRespondFailure(int errorCode, String respond) {
                Log.i(TAG, "Read onRespondFailure");
                DataUtils.showToast(RBUHFActivity.this, "Read onRespondFailure ,errorCode =" + errorCode + "\nrespond =" + respond);
            }

            @Override
            public void onReaderFailure(String address, String cmd, String errorCode) {
                Log.i(TAG, "Read onReaderFailure");
                DataUtils.showToast(RBUHFActivity.this, "Read onReaderFailure ,errorCode =" + errorCode);
            }

            @Override
            public void TimeOut() {
                Log.i(TAG, "Read TimeOut");
                DataUtils.showToast(RBUHFActivity.this, "Read TimeOut");
            }
        });

        buttonConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    buttonInv.setClickable(true);
                    if (prgDlg != null) {
                        prgDlg.show();
                    } else {
                        prgDlg = ProgressDialog.show(RBUHFActivity.this, getResources().getString(R.string.hxuhf_connect_to_device),
                                getResources().getString(R.string.hxuhf_connecting_to_device), true, false);
                    }

                    boolean openRBUHFSerialPort = api.openRBUHFSerialPort(Device_U5);
                    prgDlg.dismiss();
                    if (openRBUHFSerialPort) {
                        Toast.makeText(RBUHFActivity.this, getText(R.string.hxuhf_info_connect_success), Toast.LENGTH_SHORT).show();
                        buttonInv.setClickable(true);
                    } else {
                        Toast.makeText(RBUHFActivity.this, getText(R.string.hxuhf_info_connect_fail), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //顺便清空下条目

                    objFragment.clearItem();

                    buttonInv.setClickable(false);
                    api.closeRBUHFSerialPort(Device_U5);
                }
            }
        });

        buttonInv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //llReadWrite.setEnabled(false);
//                BaseUtils.disableSubControls(llReadWrite, !isChecked);
                soundPool.release();

                if (isChecked) {

                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ok);
                    }

                    buttonConnect.setClickable(false);
                    isStop = false;
                    Inv();


                } else {
                    buttonConnect.setClickable(true);
                    isStop = true;
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
            }
        });
    }

    private void initView() {
        buttonConnect = findViewById(R.id.togBtn_open);
        buttonInv = findViewById(R.id.togBtn_inv);
        singleSearch = findViewById(R.id.bt_singleSearch);
        txtCount = findViewById(R.id.txtCount);
        txtTimes = findViewById(R.id.txtTimes);
        FragmentManager fragmentManager = getFragmentManager();
        objFragment = (RBTagListFragment) fragmentManager.findFragmentById(R.id.fragment_taglist);
        txtReadEpc = findViewById(R.id.txtReadEpc);
        txtReadResult = findViewById(R.id.txtReadResult);
    }

    /**
     * 显示搜索得到的标签信息
     *
     * @param flagID
     */
    public void ShowEPC(String flagID) {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(100);
        } else {
            mediaPlayer.start();
        }
        if (!tagInfoList.contains(flagID)) {
            number.put(flagID, 1);
            tagCount++;
            tagInfoList.add(flagID);
            objFragment.addItem(flagID);

            try {
                txtCount.setText(String.format("%d", tagCount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            int num = number.get(flagID);
            number.put(flagID, ++num);
            Log.i(TAG, "flagID=" + flagID + "   num=" + num);
        }
        objFragment.myadapter.notifyDataSetChanged();
        tagTimes++;
        try {
            txtTimes.setText(String.format("%d", tagTimes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void isStopInventory() {
        if (isStop) {
            stopHandler();
        } else {
            startHandler();
        }
    }

    public void startHandler() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(mRunnable, 100);
    }

    public void stopHandler() {
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 开启盘点操作
     */
    public void Inv() {
        startHandler();
        tagInfoList.clear();
        tagCount = 0;
        tagTimes = 0;
        objFragment.clearItem();

        try {
            txtCount.setText(String.format("%d", tagCount));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            txtTimes.setText(String.format("%d", tagTimes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        //拦截系统的旋转
        return;
    }
}
