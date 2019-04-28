package com.cw.demo.hxuhf;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cw.demo.R;

public class MySettingDialog extends Dialog implements View.OnClickListener {

	private Spinner mSpArea;
	private Button btn_ok;
	private Button btn_cancel;
	private EditText mEtTimeout, mEtPwd, mEtOffSet, mEtLength;
	private OnMySettingCallback callback;
	private Context context;

	public MySettingDialog(Context context) {
		super(context);
		this.context = context;
	}

	public void setOnMySettingCallback(OnMySettingCallback callback) {
		this.callback = callback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hxuhf_setting_dialog_hx);
		setTitle(R.string.hxuhf_txt_singleScanSetting);

		init();

		setCancelable(false);
	}

	private void init() {
		mSpArea = findViewById(R.id.sp_area);
		String[] areas = new String[] { "EPC", "TID", "USER" };

		ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(context,
				R.layout.hxuhf_simple_list_item, areas);
		areaAdapter.setDropDownViewResource(R.layout.hxuhf_simple_list_item);
		mSpArea.setAdapter(areaAdapter);

		mEtTimeout = findViewById(R.id.et_outtime);
		mEtPwd = findViewById(R.id.et_pwd);
		mEtOffSet = findViewById(R.id.et_offset);
		mEtLength = findViewById(R.id.et_strlength);

		btn_ok = findViewById(R.id.ok);
		btn_ok.setOnClickListener(this);

		btn_cancel = findViewById(R.id.cancel);
		btn_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok:
			if (mEtLength.getText().toString().equals("")
					|| Integer.parseInt(mEtLength.getText().toString()) == 0) {

				Toast.makeText(context, "数据长度必须大于0", Toast.LENGTH_SHORT).show();
			} else {
				callback.onSetting(Integer.parseInt(mEtTimeout.getText().toString()),
						(byte)(mSpArea.getSelectedItemPosition()), 
						mEtPwd.getText().toString(),
						Short.parseShort(mEtOffSet.getText().toString()),
						Short.parseShort(mEtLength.getText().toString()));
				dismiss();
			}
			break;
		case R.id.cancel:
			dismiss();
			break;
		default:
			break;
		}
	}

	public interface OnMySettingCallback {
		void onSetting(int times, byte code, String pwd, short sa, short dl);
	}
}