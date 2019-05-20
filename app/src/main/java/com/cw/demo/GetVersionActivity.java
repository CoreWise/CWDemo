package com.cw.demo;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cw.serialportsdk.cw;

import java.io.UnsupportedEncodingException;

import android_serialport_api.SerialPortManager;

public class GetVersionActivity extends Activity implements OnClickListener {
	private Button mBtApk, mBtStm;
	private TextView mTvApk, mTvStm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_version);
		init();
	}

	private void init() {
		mBtApk = (Button) findViewById(R.id.bt_getApk);
		mBtApk.setOnClickListener(this);

		mBtStm = (Button) findViewById(R.id.bt_getStmVersion);
		mBtStm.setOnClickListener(this);


		mTvApk = (TextView) findViewById(R.id.tv_apkVersion);
		mTvStm = (TextView) findViewById(R.id.tv_stmVersion);
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_getApk:
			mTvApk.setText(cw.getApkVersion(getApplicationContext()));
			break;
		case R.id.bt_getStmVersion:
			mTvStm.setText(cw.getStm32Version());
			break;

		default:
			break;
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