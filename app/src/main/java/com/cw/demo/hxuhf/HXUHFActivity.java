package com.cw.demo.hxuhf;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.cw.demo.R;
import com.cw.demo.utils.BaseUtils;
import com.cw.phychipsuhfsdk.UHFHXAPI;
import com.cw.serialportsdk.cw;
import com.cw.serialportsdk.utils.DataUtils;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HXUHFActivity extends BaseUHFActivity {

    public UHFHXAPI api;
    @BindView(R.id.ll_read_write)
    LinearLayout llReadWrite;
    private String TAG = "CW" + BaseUHFActivity.class.getSimpleName();
    private Button singleSearch;
    private Button mBtSetting;
    private SoundPool soundPool;
    private Handler hMsg = new StartHander(this);
    private Handler mhandler;
    private int times = 5000;// 默认超时5秒
    private byte code = 1;// 默认读取epc区域 0:读取EPC,1:读取TID
    private short sa = 0;// 默认偏移从0开始
    private short dl = 5;// 默认数据长度5
    private String pwd = "00000000";// 默认访问密码00000000
    private boolean isStop;
    private long startTime;
    private Runnable task = new Runnable() {

        @Override
        public void run() {

            api.readEPC(new UHFHXAPI.AutoRead() {
                @Override
                public void timeout() {
                    Log.i(TAG, "timeout");
                    if (!isStop) {
                        pool.execute(task);
                    } else {
                        hMsg.sendEmptyMessage(INVENTORY_OVER);
                    }
                }

                @Override
                public void start() {
                    //load = soundPool.load(getApplicationContext(), R.raw.ok, 1);
                    Log.i(TAG, "start");
                    startTime = System.currentTimeMillis();
                }


                @Override
                public void processing(byte[] data) {
                    String epc = DataUtils.toHexString(data).substring(4);
                    long l = System.currentTimeMillis() - startTime;
                    readTime.put(epc, l);
                    hMsg.obtainMessage(MSG_SHOW_EPC_INFO, epc).sendToTarget();
                    Log.i(TAG, "data=" + epc + "    time=" + l);
                }

                @Override
                public void end() {
                    Log.i(TAG, "end");
                    Log.i(TAG, "isStop=" + isStop);
                    Log.e(TAG, "===================================================================================");
                    if (!isStop) {
                        pool.execute(task);
                    } else {
                        hMsg.sendEmptyMessage(INVENTORY_OVER);
                    }
                }

            });


            //api.readEPC();

            /*api.startAutoRead2A(new UHFHXAPI.AutoRead() {
                @Override
                public void timeout() {
                    Log.i(TAG, "timeout");
                }

                @Override
                public void start() {
                    //load = soundPool.load(getApplicationContext(), R.raw.ok, 1);
                    Log.i(TAG, "start");
                    startTime = System.currentTimeMillis();
                }


                @Override
                public void processing(byte[] data) {
                    String epc = DataUtils.toHexString(data).substring(4);
                    long l = System.currentTimeMillis() - startTime;
                    readTime.put(epc, l);
                    hMsg.obtainMessage(MSG_SHOW_EPC_INFO, epc).sendToTarget();
                    Log.i(TAG, "data=" + epc + "    time=" + l);
                }

                @Override
                public void end() {
                    Log.i(TAG, "end");
                    Log.i(TAG, "isStop=" + isStop);
                    Log.e(TAG, "===================================================================================");
                    if (!isStop) {
                        pool.execute(task);
                    } else {
                        hMsg.sendEmptyMessage(INVENTORY_OVER);
                    }
                }

            });*/
        }
    };
    private boolean isOnPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.hxuhf_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//亮屏

        ButterKnife.bind(this);

        soundPool = new SoundPool(50, AudioManager.STREAM_SYSTEM, 5);

        mhandler = new Handler();

        singleSearch = findViewById(R.id.bt_singleSearch);

        singleSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                api.startAutoRead2C(times, code, pwd, sa, dl, new UHFHXAPI.SearchAndRead() {
                    @Override
                    public void timeout() {
                        mhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.hxuhf_read_timeout), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void returnData(final byte[] data) {
                        mhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "data:" + DataUtils.toHexString(data), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void readFail() {
                        mhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.hxuhf_read_fail), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        mBtSetting = findViewById(R.id.bt_setting);

        mBtSetting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MySettingDialog dialog = new MySettingDialog(HXUHFActivity.this);
                dialog.show();
                dialog.setOnMySettingCallback(new MySettingDialog.OnMySettingCallback() {

                    @Override
                    public void onSetting(int times, byte code, String pwd, short sa, short dl) {
                        HXUHFActivity.this.times = times;
                        HXUHFActivity.this.code = code;
                        HXUHFActivity.this.pwd = pwd;
                        HXUHFActivity.this.sa = sa;
                        HXUHFActivity.this.dl = dl;
                    }
                });
            }
        });

        api = new UHFHXAPI();

        txtCount = findViewById(R.id.txtCount);
        txtTimes = findViewById(R.id.txtTimes);
        setting = findViewById(R.id.setting_params);

        setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UHFDialogFragment dialog = new UHFDialogFragment();
                dialog.show(getFragmentManager(), "CoreWise");
            }
        });
        buttonConnect = findViewById(R.id.togBtn_open);
        buttonInv = findViewById(R.id.togBtn_inv);
        final FragmentManager fragmentManager = getFragmentManager();
        objFragment = (TaglistFragment) fragmentManager.findFragmentById(R.id.fragment_taglist);

        final Fragment operationFragment = fragmentManager.findFragmentById(R.id.tag_operation_fragment);

        buttonConnect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    buttonInv.setClickable(true);
                    if (prgDlg != null) {
                        prgDlg.show();
                    } else {
                        prgDlg = ProgressDialog.show(HXUHFActivity.this, getResources().getString(R.string.hxuhf_connect_to_device),
                                getResources().getString(R.string.hxuhf_connecting_to_device), true, false);
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            Message closemsg = new Message();
                            //closemsg.obj = api.open();
                            closemsg.obj = api.openHXUHFSerialPort(cw.getDeviceModel());
                            closemsg.what = MSG_DISMISS_CONNECT_WAIT_SHOW;
                            hMsg.sendMessage(closemsg);
                        }
                    }.start();

                } else {
                    //顺便清空下条目

                    objFragment.clearItem();

                    buttonInv.setClickable(false);
                    if (!isOnPause) {
                        //api.close();
                        api.closeHXUHFSerialPort(cw.getDeviceModel());
                        setting.setEnabled(false);
                    }
                }
            }
        });

        buttonInv.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //llReadWrite.setEnabled(false);
                BaseUtils.disableSubControls(llReadWrite, !isChecked);
                soundPool.release();

                if (isChecked) {

                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ok);
                    }

                    buttonConnect.setClickable(false);
                    isStop = false;
                    Inv();
                    setting.setEnabled(false);


                } else {
                    buttonConnect.setClickable(true);
                    isStop = true;
                    setting.setEnabled(true);
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
            }
        });


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

    /**
     * 开启盘点操作
     */
    public void Inv() {
        pool.execute(task);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        isOnPause = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        soundPool.release();
        isOnPause = true;
        isStop = true;
        if (buttonInv.isChecked()) {
            buttonInv.setChecked(false);
            buttonInv.setClickable(false);
        }
        if (buttonConnect.isChecked()) {
            buttonConnect.setChecked(false);
        }
        api.closeHXUHFSerialPort(cw.getDeviceModel());
        super.onPause();
    }

    /**
     * 用于集中处理显示等事件信息的静态类
     *
     * @author chenshanjing
     */
    class StartHander extends Handler {

        WeakReference<Activity> mActivityRef;

        StartHander(Activity activity) {
            mActivityRef = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivityRef.get();
            if (activity == null) {
                return;
            }

            switch (msg.what) {
                case MSG_SHOW_EPC_INFO:
                    ShowEPC((String) msg.obj);
                    break;

                case MSG_DISMISS_CONNECT_WAIT_SHOW:
                    prgDlg.dismiss();
                    if ((Boolean) msg.obj) {
                        Toast.makeText(activity, activity.getText(R.string.hxuhf_info_connect_success), Toast.LENGTH_SHORT)
                                .show();
                        // byte[] data = api.setRegion(0x52).data;
                        // byte[] data = api.getRegion().data;
                        // Log.d("jokey", "data-->"+DataUtils.toHexString(data));
                        setting.setEnabled(true);
                        buttonInv.setClickable(true);
                    } else {
                        Toast.makeText(activity, activity.getText(R.string.hxuhf_info_connect_fail), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case INVENTORY_OVER:
                    Toast.makeText(HXUHFActivity.this, R.string.hxuhf_inventory_over, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


}