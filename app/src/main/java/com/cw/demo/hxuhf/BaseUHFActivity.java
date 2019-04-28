package com.cw.demo.hxuhf;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.cw.demo.R;
import com.cw.serialportsdk.CoreWise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android_serialport_api.SerialPortManager;

public abstract class BaseUHFActivity extends AppCompatActivity {

    private String TAG = "CoreWise"+BaseUHFActivity.class.getSimpleName();

    protected static final int MSG_SHOW_EPC_INFO = 1;
    protected static final int MSG_DISMISS_CONNECT_WAIT_SHOW = 2;
    protected static final int INVENTORY_OVER = 3;
    protected static final int INFO_INV_SUCCESS = 4;
    protected static final int INFO_INV_FAIL = 5;
    protected static final int INFO_STOPINV_SUCCESS = 6;
    protected static final int INFO_STOPINV_FAIL = 7;
    protected static final int INFO_DISCONNECT_SUCCESS = 8;
    protected static final int INFO_DISCONNECT_FAIL = 9;
    protected ToggleButton buttonConnect = null;
    protected ToggleButton buttonInv = null;
    protected ProgressDialog prgDlg = null;
    protected static TaglistFragment objFragment = null;
    protected static TextView txtCount = null;
    Button setting;
    public static TextView txtTimes = null;
    protected static int tagCount = 0;
    protected static int tagTimes = 0;
    protected static List<String> tagInfoList = new ArrayList<String>();
    protected int exitcount = 1;
    protected static HashMap<String, Integer> number = new HashMap<String, Integer>();
    protected static HashMap<String, Long> readTime = new HashMap<String, Long>();


    protected static MediaPlayer mediaPlayer = null;
    protected ExecutorService pool;

    @Override
    protected void onResume() {
        Log.e(TAG,"onResume");
        pool = Executors.newSingleThreadExecutor();
        mediaPlayer = MediaPlayer.create(this, R.raw.ok);
        super.onResume();
        if (!SerialPortManager.getInstance().openSerialPort(CoreWise.type.uhf)) {
            Toast.makeText(this, R.string.general_open_serial_fail, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.general_open_serial_success, Toast.LENGTH_SHORT).show();
        }

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED)
                {
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
        Log.e(TAG,"onPause");
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        pool.shutdown();
        pool = null;
        SerialPortManager.getInstance().closeSerialPort();
        super.onPause();

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
