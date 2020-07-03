package com.cw.demo.UHF.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;

import com.cw.demo.R;


public class NumberPickerDialog extends Dialog implements OnClickListener {

	private NumberPicker mNumberPicker;
	private Button btn_ok;
	private Button btn_cancel;
	private short initNumber;
	private int mode;

	public interface OnMyNumberSetListener {
		/**
		 * 数字被设定之后执行此方法
		 * 
		 * @param number
		 *            当前文字框中字符串
		 * @param mode
		 *            可用以标识调用者
		 */
		void onNumberSet(short number, int mode);
	}

	private OnMyNumberSetListener mListener;

	public NumberPickerDialog(Context context, OnMyNumberSetListener listener,
                              short number, int mode) {
		super(context);
		this.mListener = listener;
		this.initNumber = number;
		this.mode = mode;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hxuhf_inputnumber_dialog_hk);
		setTitle("数字选择");

		mNumberPicker = findViewById(R.id.nump_input);
		mNumberPicker.setMaxValue(128);
		mNumberPicker.setMinValue(0);
		mNumberPicker.setValue(initNumber);

		btn_ok = findViewById(R.id.ok);
		btn_cancel = findViewById(R.id.cancel);
		btn_ok.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		setCancelable(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok:
			mListener.onNumberSet((short) mNumberPicker.getValue(), mode);
			dismiss();
			break;
		case R.id.cancel:
			dismiss();
			break;
		}
	}
}
