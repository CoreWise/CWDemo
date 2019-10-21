package com.cw.demo.barcode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;


import com.cw.demo.MyApplication;
import com.cw.demo.R;
import com.cw.demo.barcode.BarCodeActivity;
import com.cw.demo.utils.BaseUtils;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 作者：李阳
 * 时间：2018/9/15
 * 描述：
 */
public class ScannerSettingFragment extends Fragment {


    private static final String TAG = "ScannerSettingFragment";

    @BindView(R.id.et_interval)
    EditText etInterval;
    @BindView(R.id.sp_outputmode)
    Spinner spOutputmode;
    @BindView(R.id.sp_endchar)
    Spinner spEndchar;
    @BindView(R.id.et_pre)
    EditText etPre;
    @BindView(R.id.et_end)
    EditText etEnd;
    @BindView(R.id.sp_effect)
    Spinner spEffect;
    @BindView(R.id.tb_toast)
    ToggleButton tbToast;
    Unbinder unbinder;
    @BindView(R.id.btn_set)
    Button btnSet;
    BarCodeActivity barCodeActivity;
    @BindView(R.id.ll_setting)
    LinearLayout llSetting;

    public static Fragment newInstance() {
        ScannerSettingFragment fragment = new ScannerSettingFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.barcode_fragment_scannersetting, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        barCodeActivity = (BarCodeActivity) getActivity();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseUtils.disableSubControls(llSetting, false);
        btnSet.setText(R.string.barcode_setting_enable);

        etPre.setText(Settings.System.getString(getActivity().getContentResolver(), "SCANNER_PREFIX"));
        etEnd.setText(Settings.System.getString(getActivity().getContentResolver(), "SCANNER_SUFFIX"));
        spEndchar.setSelection(Settings.System.getInt(getActivity().getContentResolver(), "SCANNER_TERMINAL_CHAR", 0));

        etInterval.setText(""+(Integer) MyApplication.getApp().getParam(getActivity(), "intervaltime", 800));


        boolean soundOn = Settings.System.getInt(getActivity().getContentResolver(), "SCANNER_SOUND_ON", 1) == 1;
        boolean vibrationOn = Settings.System.getInt(getActivity().getContentResolver(), "SCANNER_VIBRATION_ON", 0) == 1;

        if (!soundOn && !vibrationOn) {
            spEffect.setSelection(0);
        } else if (soundOn && vibrationOn) {
            spEffect.setSelection(3);
        } else if (soundOn && !vibrationOn) {
            spEffect.setSelection(2);
        } else if (!soundOn && vibrationOn) {
            spEffect.setSelection(1);
        }

        int outputMode = Settings.System.getInt(getActivity().getContentResolver(), "SCANNER_OUTPUT_MODE", 0);

        spOutputmode.setSelection(outputMode);

        boolean isShowToast = Settings.System.getInt(getActivity().getContentResolver(), "SCANNER_ISSHOWTOAST", 1) == 1; //是否打印吐司，默认打印

        tbToast.setChecked(isShowToast);

        initSetting();

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.i(TAG, "setUserVisibleHint");
        //BaseUtils.disableSubControls(llSetting, false);
        if (isVisibleToUser) {

        } else {
            // btnSet.setText("取消禁用");
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
        unbinder.unbind();
    }

    @OnClick(R.id.btn_set)
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_set:
                View view = getActivity().getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                String s1 = btnSet.getText().toString();
                if (s1.equals(getString(R.string.barcode_setting_disable))) {//||s1.equals("Disable")
                    BaseUtils.disableSubControls(llSetting, false);
                    etInterval.setEnabled(false);
                    etPre.setEnabled(false);
                    etEnd.setEnabled(false);

                    etInterval.setFocusable(false);
                    etPre.setFocusable(false);
                    etEnd.setFocusable(false);

                    etInterval.setFocusableInTouchMode(false);
                    etPre.setFocusableInTouchMode(false);
                    etEnd.setFocusableInTouchMode(false);

                    etInterval.setCursorVisible(false);
                    etPre.setCursorVisible(false);
                    etEnd.setCursorVisible(false);

                    btnSet.setText(R.string.barcode_setting_enable);
                    Settings.System.putString(getActivity().getContentResolver(), "SCANNER_PREFIX", String.valueOf(etPre.getText()));
                    Settings.System.putString(getActivity().getContentResolver(), "SCANNER_SUFFIX", String.valueOf(etEnd.getText()));

                    String s = etInterval.getText().toString();

                    int i = Integer.parseInt(s);

                    MyApplication.getApp().setParam(getActivity(), "intervaltime", i);


                    Settings.System.putInt(getActivity().getContentResolver(), "scan_timeout", i);


                } else if (s1.equals(getString(R.string.barcode_setting_enable))) {//||s1.equals("Enable")
                    etInterval.setEnabled(true);
                    etPre.setEnabled(true);
                    etEnd.setEnabled(true);

                    etInterval.setFocusable(true);
                    etPre.setFocusable(true);
                    etEnd.setFocusable(true);

                    etInterval.setFocusableInTouchMode(true);
                    etPre.setFocusableInTouchMode(true);
                    etEnd.setFocusableInTouchMode(true);

                    etInterval.setCursorVisible(true);
                    etPre.setCursorVisible(true);
                    etEnd.setCursorVisible(true);

                    BaseUtils.disableSubControls(llSetting, true);
                    btnSet.setText(R.string.barcode_setting_disable);
                }
                /*Toast.makeText(barCodeActivity, "设置成功!", Toast.LENGTH_SHORT).show();*/
                break;
        }
    }


    private void initSetting() {

        spEndchar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Settings.System.putInt(getActivity().getContentResolver(), "SCANNER_TERMINAL_CHAR", position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spEffect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Settings.System.putInt(getActivity().getContentResolver(), "SCANNER_SOUND_ON", 0);
                        Settings.System.putInt(getActivity().getContentResolver(), "SCANNER_VIBRATION_ON", 0);
                        break;
                    case 1:
                        Settings.System.putInt(getActivity().getContentResolver(), "SCANNER_SOUND_ON", 0);
                        Settings.System.putInt(getActivity().getContentResolver(), "SCANNER_VIBRATION_ON", 1);
                        break;
                    case 2:
                        Settings.System.putInt(getActivity().getContentResolver(), "SCANNER_SOUND_ON", 1);
                        Settings.System.putInt(getActivity().getContentResolver(), "SCANNER_VIBRATION_ON", 0);
                        break;
                    case 3:
                        Settings.System.putInt(getActivity().getContentResolver(), "SCANNER_SOUND_ON", 1);
                        Settings.System.putInt(getActivity().getContentResolver(), "SCANNER_VIBRATION_ON", 1);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        //result.addTextChangedListener(ResultTextwatcher);

        spOutputmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Settings.System.putInt(getActivity().getContentResolver(), "SCANNER_OUTPUT_MODE", position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tbToast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.System.putInt(getActivity().getContentResolver(), "SCANNER_ISSHOWTOAST", isChecked ? 1 : 0);
                Intent intent = new Intent();
                intent.setAction("SCANNER_ISSHOWTOAST");
                getActivity().sendBroadcast(intent);
            }
        });
    }

}
