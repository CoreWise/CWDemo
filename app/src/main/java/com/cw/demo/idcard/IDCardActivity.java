package com.cw.demo.idcard;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.cw.demo.MyApplication;
import com.cw.demo.utils.BaseUtils;
import com.cw.demo.R;
import com.cw.idcardsdk.AsyncParseSFZ;
import com.cw.idcardsdk.ParseSFZAPI;
import com.cw.serialportsdk.cw;
import com.cw.serialportsdk.utils.DataUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;


/**
 * 作者：
 * 时间：2018/7/13
 * 描述：身份证Demo
 *
 * @author Administrator
 */
public class IDCardActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "CwIDCardActivity";

    private static final int UPDATEINFOS = 1222;
    long StartMillis;
    boolean isBack = false;
    Handler mHandler = new Handler();
    private TextView sfz_name;
    private TextView sfz_sex;
    private TextView sfz_nation;
    private TextView sfz_year;
    private TextView sfz_mouth;
    private TextView sfz_day;
    private TextView sfz_address;
    private TextView sfz_id;
    private TextView sfz_modle;
    private ImageView sfz_photo;
    private Button read_button;
    private Button clear_button;
    private Button sequential_read;
    private Button uid_button;
    private Button stop;
    private TextView resultInfo;
    private AsyncParseSFZ asyncParseSFZ;
    private int readTime = 0;
    private int readFailTime = 0;
    private int readTimeout = 0;
    private int readFailFor8084 = 0;
    private int readFailFor4145 = 0;
    private int readFailForOther = 0;
    private int readSuccessTime = 0;
    private long nowTime, oneTime;
    /**
     * 是否是连续读取
     */
    private boolean isSequentialRead = false;
    private MediaPlayer mediaPlayer = null;
    //设置日期格式
    private SimpleDateFormat df;
    private Date StartTime, EndTime;
    private String result = "";
    @SuppressLint("HandlerLeak")
    Handler mUpDateUIHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case UPDATEINFOS:

                    result = "总共:" + readTime + "   成功:" + readSuccessTime + "   成功率:" + BaseUtils.getPerCent(readSuccessTime, readTime) + "   失败(总共):"
                            + readFailTime + "\n超时次数:" + readTimeout + "    读卡失败(80):" + readFailFor8084 + "    读卡失败(41):" + readFailFor4145 + "\n其他失败:" + readFailForOther + "    单次读卡时间:   " + oneTime + " ms" +
                            "\n开始连读时间:   " + df.format(StartTime);

                    Log.i("whw", "result=" + result);

                    resultInfo.setText(result);

                    break;
            }
        }
    };
    private Vibrator mVibrator;
    private SoundPool soundPool;
    private int load;
    //默认五寸屏，无指纹模块
    private boolean Hasfinger = false;
    private int clickCount = 0;
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            nowTime = System.currentTimeMillis();
            readTime++;
            clear();
            asyncParseSFZ.readSFZ(ParseSFZAPI.THIRD_GENERATION_CARD);
        }
    };

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_idcard);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        load = soundPool.load(getApplicationContext(), R.raw.ok, 1);

        initView();
        initData();


        View sfz = findViewById(R.id.sfz);

        sfz.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount++;
                if (clickCount == 5) {
                    //连续点5下显示
                    clickCount = 0;
                    resultInfo.setVisibility(View.VISIBLE);
                }
            }
        });

        //长按隐藏
        sfz.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                resultInfo.setVisibility(View.GONE);
                return false;
            }
        });

    }

    private void initView() {

        sfz_name = findViewById(R.id.sfz_name);
        sfz_nation = findViewById(R.id.sfz_nation);
        sfz_sex = findViewById(R.id.sfz_sex);
        sfz_year = findViewById(R.id.sfz_year);
        sfz_mouth = findViewById(R.id.sfz_mouth);
        sfz_day = findViewById(R.id.sfz_day);
        sfz_address = findViewById(R.id.sfz_address);
        sfz_id = findViewById(R.id.sfz_id);
        sfz_modle = findViewById(R.id.tv_sfz_modle);
        sfz_photo = findViewById(R.id.sfz_photo);

        read_button = findViewById(R.id.read_sfz);
        clear_button = findViewById(R.id.clear_sfz);
        sequential_read = findViewById(R.id.sequential_read);
        stop = findViewById(R.id.stop);
        uid_button = findViewById(R.id.uid_sfz);
        resultInfo = findViewById(R.id.resultInfo);

        read_button.setOnClickListener(this);
        clear_button.setOnClickListener(this);
        sequential_read.setOnClickListener(this);
        stop.setOnClickListener(this);
        uid_button.setOnClickListener(this);

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    }

    private void initData() {

        mediaPlayer = MediaPlayer.create(this, R.raw.ok);
        asyncParseSFZ = new AsyncParseSFZ(getMainLooper(), this);

        asyncParseSFZ.setTargetSdkVersion(this.getApplicationInfo().targetSdkVersion);

        asyncParseSFZ.setOnReadSFZListener(new AsyncParseSFZ.OnReadSFZListener() {

            @Override
            public void onReadSuccess(Object people) {
                updateInfo((ParseSFZAPI.People) people);
                readSuccessTime++;
                refresh(isSequentialRead);
                oneTime = System.currentTimeMillis() - nowTime;
                mVibrator.vibrate(60);
                soundPool.play(load, 1, 1, 0, 0, 1);

                if (!isSequentialRead) {
                    read_button.setEnabled(true);
                    sequential_read.setEnabled(true);
                    clear_button.setEnabled(true);

                } else {
                    mUpDateUIHandler.sendEmptyMessage(UPDATEINFOS);

                }
            }

            @Override
            public void onReadFail(int confirmationCode,String errorCode) {

                if (!isSequentialRead) {
                    read_button.setEnabled(true);
                    sequential_read.setEnabled(true);
                    clear_button.setEnabled(true);

                }

                if (isBack) {
                    return;
                }

                if (confirmationCode == ParseSFZAPI.Result.FIND_FAIL) {
                    if (!isSequentialRead) {
                        Toast.makeText(getApplicationContext(), "未寻到卡,有返回数据", Toast.LENGTH_SHORT).show();
                    }
                } else if (confirmationCode == ParseSFZAPI.Result.TIME_OUT) {//3
                    if (!isSequentialRead) {
                        Toast.makeText(getApplicationContext(), "未寻到卡,无返回数据，超时！！(串口无数据)", Toast.LENGTH_SHORT).show();
                    }
                    readTimeout++;
                } else if (confirmationCode == ParseSFZAPI.Result.OTHER_EXCEPTION) {
                    if (!isSequentialRead) {
                        Toast.makeText(getApplicationContext(), "可能是串口打开失败或其他异常", Toast.LENGTH_SHORT).show();
                    }
                } else if (confirmationCode == ParseSFZAPI.Result.NO_THREECARD) {
                    if (!isSequentialRead) {
                        Toast.makeText(getApplicationContext(), "此二代证没有指纹数据", Toast.LENGTH_SHORT).show();
                    }
                } else if (confirmationCode == ParseSFZAPI.Result.FIND_FAIL_8084) {//6
                    if (!isSequentialRead) {
                        Toast.makeText(getApplicationContext(), "未寻到卡,有返回数据(80)", Toast.LENGTH_SHORT).show();
                    }
                    readFailFor8084++;
                } else if (confirmationCode == ParseSFZAPI.Result.FIND_FAIL_4145) {
                    if (!isSequentialRead) {
                        Toast.makeText(getApplicationContext(), "未寻到卡,有返回数据(41)", Toast.LENGTH_SHORT).show();
                    }
                    readFailFor4145++;
                } else if (confirmationCode == ParseSFZAPI.Result.FIND_FAIL_other) {//12
                    if (!isSequentialRead) {
                        Toast.makeText(getApplicationContext(), "未寻到卡,有返回数据(其他错误)", Toast.LENGTH_SHORT).show();
                    }
                    readFailForOther++;
                } else if (confirmationCode == ParseSFZAPI.Result.FIND_FAIL_Length) {
                    if (!isSequentialRead) {
                        Toast.makeText(getApplicationContext(), "未寻到卡,有返回数据(数据接收不完整)", Toast.LENGTH_SHORT).show();
                    }
                    readFailForOther++;
                }
                readFailTime++;
                clear();
                refresh(isSequentialRead);
                if (isSequentialRead) {
                    oneTime = System.currentTimeMillis() - nowTime;
                    mUpDateUIHandler.sendEmptyMessage(UPDATEINFOS);
                }
            }
        });

        asyncParseSFZ.setOnReadCardIDListener(new AsyncParseSFZ.OnReadCardIDListener() {

            @Override
            public void onReadSuccess(String id) {
                sfz_modle.setText(id);
                if (!isSequentialRead) {
                    read_button.setEnabled(true);
                    sequential_read.setEnabled(true);
                    clear_button.setEnabled(true);

                }
            }

            @Override
            public void onReadFail() {
                Toast.makeText(getApplicationContext(), "读取卡号失败", Toast.LENGTH_SHORT).show();
                if (!isSequentialRead) {
                    read_button.setEnabled(true);
                    sequential_read.setEnabled(true);
                    clear_button.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.read_sfz:

                clear();
                resultInfo.setText("");
                isSequentialRead = false;
                //showProgressDialog("正在读取数据...");
                asyncParseSFZ.readSFZ(ParseSFZAPI.THIRD_GENERATION_CARD);
                Log.i("whw", "read_sfz");

                read_button.setEnabled(false);
                sequential_read.setEnabled(false);
                clear_button.setEnabled(false);
                stop.setEnabled(false);


                break;


            case R.id.clear_sfz:
                clear();
                resultInfo.setText("");
                readTime = 0;
                readFailTime = 0;
                readTimeout = 0;
                readSuccessTime = 0;
                break;


            case R.id.sequential_read:

                clear();
                stop.setEnabled(true);
                isSequentialRead = true;
                readTime = 0;
                readFailTime = 0;
                readTimeout = 0;
                readSuccessTime = 0;
                readFailFor8084 = 0;
                readFailFor4145 = 0;
                readFailForOther = 0;
                mHandler.removeCallbacksAndMessages(null);
                mHandler.post(task);
                sequential_read.setEnabled(false);
                read_button.setEnabled(false);
                clear_button.setEnabled(false);

                nowTime = System.currentTimeMillis();

                StartTime = Calendar.getInstance().getTime();
                StartMillis = System.currentTimeMillis();
                Log.i(TAG, df.format(StartTime));

                break;

            case R.id.stop:
                mHandler.removeCallbacks(task);

                isSequentialRead = false;
                sequential_read.setEnabled(true);
                read_button.setEnabled(true);
                clear_button.setEnabled(true);

                EndTime = Calendar.getInstance().getTime();
                Log.i(TAG, df.format(EndTime));

                if (resultInfo.getLineCount() == 4) {
                    result = result + "\n停止连读时间:   " + df.format(EndTime) + "\n单次平均读取时间: " + (System.currentTimeMillis() - StartMillis) / (readTime) + "ms";
                }
                resultInfo.setText(result);

                break;
            case R.id.uid_sfz:
                asyncParseSFZ.readCardID();
                break;
            default:

                break;
        }
    }

    private void refresh(boolean isSequentialRead) {

        if (!isSequentialRead) {
            return;
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(task, 200);

    }

    private void updateInfo(ParseSFZAPI.People people) {

        if (mediaPlayer!=null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
            mediaPlayer = MediaPlayer.create(this, R.raw.ok);
            mediaPlayer.start();
        }

        if (TextUtils.equals(people.getType(),"J"))
        {
            Toast.makeText(this,"港澳台居住证",Toast.LENGTH_SHORT).show();
        }

        sfz_address.setText(people.getPeopleAddress());
        sfz_id.setText(people.getPeopleIDCode());
        sfz_name.setText(people.getPeopleName());
        sfz_nation.setText(people.getPeopleNation());
        sfz_sex.setText(people.getPeopleSex());

        if (people.getPeopleBirthday() != null && people.getPeopleBirthday().length() > 0)
        {
            sfz_year.setText(people.getPeopleBirthday().substring(0, 4));
            sfz_mouth.setText(people.getPeopleBirthday().substring(4, 6));
            sfz_day.setText(people.getPeopleBirthday().substring(6));
        }

        if (people.getPhoto() != null) {
            Bitmap photo = BitmapFactory.decodeByteArray(people.getPhoto(), 0,
                    people.getPhoto().length);
            sfz_photo.setBackgroundDrawable(new BitmapDrawable(photo));
        }

        if (people.getModel() != null) {
            sfz_modle.setText("第一组指纹数据为:" + people.getWhichFinger()[0] + "\n第二组指纹数据为:" + people.getWhichFinger()[1] + "\n" + DataUtils.toHexString(people.getModel()));
            Log.i(TAG, "--" + people.getWhichFinger()[0] + "--" + people.getWhichFinger()[1] + "----指纹数据长度-----" + people.getModel().length);


        } else {
            sfz_modle.setText("没有指纹信息");
        }

//        sfz_modle.setText(people.toString());

//        String s = asyncParseSFZ.readUID();
//        sfz_modle.setText(s);
    }

    private void clear() {
        sfz_address.setText("");
        sfz_day.setText("");
        sfz_id.setText("");
        sfz_mouth.setText("");
        sfz_name.setText("");
        sfz_nation.setText("");
        sfz_sex.setText("");
        sfz_year.setText("");
        sfz_modle.setText("");
        sfz_photo.setBackgroundColor(0);
        //moduleView.setText("");
    }


    @Override
    protected void onStart() {
        Log.e(TAG, "--------onStart---------");
        super.onStart();
        isSequentialRead = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "-------" + getLocalClassName());

        asyncParseSFZ.openIDCardSerialPort(cw.getDeviceModel());
        //SwitchUtil.getInstance().openUSB();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        Log.e(TAG, "-------onPause------");
        //SerialPortManager.getInstance().closeSerialPort();
        asyncParseSFZ.closeIDCardSerialPort(cw.getDeviceModel());
        MyApplication.getApp().maintainScannerService();
        //关闭职位模块，省电
        //asyncParseSFZ.closeFingerDevice(IDCardActivity.this, mScanner);
        mHandler.removeCallbacksAndMessages(null);
        //mHandler.removeCallbacks(task);
        //isSequentialRead = false;
        //将USB切回正常状态，省电
        //SwitchUtil.getInstance().closeUSB();
        sequential_read.setEnabled(true);
        read_button.setEnabled(true);
        clear_button.setEnabled(true);
        isSequentialRead = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (soundPool != null) {
            soundPool.release();
        }
        Log.i(TAG, "IDCardActivity onDestroy");
        mHandler.removeCallbacks(task);

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG, "--onBackPressed--");
        isSequentialRead = false;
        isBack = true;
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