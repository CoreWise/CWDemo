package com.cw.demo.fingerprint.shengyuan;

import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;


import com.cw.demo.MyApplication;

import android_serialport_api.SerialPortManager;

public abstract class BaseActivity extends AppCompatActivity {

	protected HandlerThread handlerThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!SerialPortManager.getInstance().isOpen()) {
			SerialPortManager.getInstance().openSerialPort();
		}
		Log.i("whw", "onResume=" + SerialPortManager.getInstance().isOpen());

	}

	@Override
	protected void onPause() {
		super.onPause();
		SerialPortManager.getInstance().closeSerialPort();
		handlerThread = null;
	}
}