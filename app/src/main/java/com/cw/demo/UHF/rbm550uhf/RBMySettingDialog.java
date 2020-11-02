package com.cw.demo.UHF.rbm550uhf;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.cowise.rbm550uhfsdk.RBUFHAPI;
import com.cowise.rbm550uhfsdk.RBUFHConfig;
import com.cw.demo.R;
import com.cw.serialportsdk.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

public class RBMySettingDialog extends Dialog implements View.OnClickListener {
    private String TAG = RBMySettingDialog.class.getSimpleName();

    private Button mButtonGetRegion;
    private Button mButtonSetRegion;
    private Button mButtonGetVersion;//获取版本信息
    private Button mButtonReset;//复位
    private Button mButtonSetPower;//设置功率
    private Button mButtonGetPower;//获取功率
    private Button btn_ok;
    private Context context;
    private TextView mVersionText;//显示版本信息
    private EditText mOutPowerText;//功率显示

    private RadioGroup mRegionRadioGroup;//选择设置频率布局
    private int mRegionView = 0;//记录当前显示频率设置布局  0 自定义  1 系统默认

    private LinearLayout mCustomizeView;//自定义频率布局
    private EditText mCustomizeStartEdit;//频谱 频率起始点
    private EditText mCustomizeEndEdit;//频率结束点
    private EditText mCustomizeSpaceEdit;//频点间隔
    private EditText mCustomizeQuantityEdit;//频点数量

    private LinearLayout mSystemView;//系统默认频点布局
    //    private EditText mSystemStartEdit;//频率起始点
//    private EditText mSystemEndEdit;//频率结束点
    private TextView mFreqStartText;//频率开始
    private TextView mFreqEndText;//频率结束
    private TableRow mDropDownRow1;//开始下拉选择按钮
    private TableRow mDropDownRow2;//结束下拉选择按钮
    private RBSpinerPopWindow mSpinerPopWindow1;//频率开始选项
    private RBSpinerPopWindow mSpinerPopWindow2;//频率结束选项

    private int mPos1 = -1;
    private int mPos2 = -1;
    private byte btStartFreq = 0x00;
    private byte btEndFreq = 0x00;

    private Spinner mSystemSpRegion;//射频规范
    private String[] mSystemRegion;

    private List<String> mFreqStartList = new ArrayList<String>();
    private List<String> mFreqEndList = new ArrayList<String>();

    public RBMySettingDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rbuhf_setting_dialog_hx);
        setTitle(R.string.hxuhf_txt_singleScanSetting);

        initView();
        initData();
        setListener();
//        setCancelable(false);
    }

    private void initView() {
        btn_ok = findViewById(R.id.ok);
        mButtonGetRegion = findViewById(R.id.get_region_button);
        mButtonSetRegion = findViewById(R.id.set_region_button);
        mButtonGetVersion = findViewById(R.id.rb_version_button);
        mButtonReset = findViewById(R.id.rb_reset_button);
        mButtonSetPower = findViewById(R.id.rb_set_power_button);
        mButtonGetPower = findViewById(R.id.rb_get_power_button);
        mVersionText = findViewById(R.id.rb_version_text);
        mOutPowerText = findViewById(R.id.rb_power_text);
        mCustomizeView = findViewById(R.id.rb_customize_view);
        mCustomizeStartEdit = findViewById(R.id.rb_region_start);
        mCustomizeEndEdit = findViewById(R.id.rb_region_end);
        mCustomizeSpaceEdit = findViewById(R.id.rb_region_space);
        mCustomizeQuantityEdit = findViewById(R.id.rb_region_quantity);
        mSystemView = findViewById(R.id.rb_system_view);
//        mSystemStartEdit = findViewById(R.id.rb_system_region_start);
//        mSystemEndEdit = findViewById(R.id.rb_system_region_end);
        mDropDownRow1 = (TableRow) findViewById(R.id.table_row_spiner_freq_start);
        mDropDownRow2 = (TableRow) findViewById(R.id.table_row_spiner_freq_end);
        mFreqStartText = (TextView) findViewById(R.id.freq_start_text);
        mFreqEndText = (TextView) findViewById(R.id.freq_end_text);
        mSystemSpRegion = findViewById(R.id.sp_region);
        mRegionRadioGroup = findViewById(R.id.rb_region_select_radio_group);
    }

    private void initData() {
        mSystemRegion = context.getResources().getStringArray(R.array.rbhxuhf_region);
        ArrayAdapter<String> regionAdapter = new ArrayAdapter<String>(context,
                R.layout.hxuhf_simple_list_item, mSystemRegion);
        regionAdapter.setDropDownViewResource(R.layout.hxuhf_simple_list_item);
        mSystemSpRegion.setAdapter(regionAdapter);

        mSpinerPopWindow1 = new RBSpinerPopWindow(getContext());
        mSpinerPopWindow2 = new RBSpinerPopWindow(getContext());

        changeData(getRegionNorm(mSystemSpRegion.getSelectedItemPosition()));
    }

    private void setListener() {
        btn_ok.setOnClickListener(this);
        mButtonGetRegion.setOnClickListener(this);
        mButtonSetRegion.setOnClickListener(this);
        mButtonGetVersion.setOnClickListener(this);
        mButtonReset.setOnClickListener(this);
        mButtonSetPower.setOnClickListener(this);
        mButtonGetPower.setOnClickListener(this);

        mDropDownRow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpinWindow1();
            }
        });

        mDropDownRow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpinWindow2();
            }
        });

        mSpinerPopWindow1.setItemListener(new RBAbstractSpinerAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                setFreqStartText(pos);
            }
        });


        mSpinerPopWindow2.setItemListener(new RBAbstractSpinerAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                setFreqEndText(pos);
            }
        });

        mRegionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                freqViewSelect(checkedId);
            }
        });

        mSystemSpRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFreqStartText.setText("");
                mFreqEndText.setText("");
                btStartFreq = 0;
                btEndFreq = 0;
                changeData(getRegionNorm(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showSpinWindow1() {
        mSpinerPopWindow1.setWidth(mDropDownRow1.getWidth());
        mSpinerPopWindow1.showAsDropDown(mDropDownRow1);
    }

    private void showSpinWindow2() {
        mSpinerPopWindow2.setWidth(mDropDownRow2.getWidth());
        mSpinerPopWindow2.showAsDropDown(mDropDownRow2);
    }

    private void setFreqStartText(int pos) {
        if (pos >= 0 && pos < mFreqStartList.size()) {
            String value = mFreqStartList.get(pos);
            mFreqStartText.setText(value);
            mPos1 = pos;
        }
    }

    private void setFreqEndText(int pos) {
        if (pos >= 0 && pos < mFreqStartList.size()) {
            String value = mFreqStartList.get(pos);
            mFreqEndText.setText(value);
            mPos2 = pos;
        }
    }

    private void changeData(byte regionNorm) {
        float nStart = 0x0;
        int nloop = 0;

        mFreqStartList.clear();
        mFreqEndList.clear();
        if (regionNorm == 0x01) {
            nStart = 902.00f;
            for (nloop = 0; nloop < 53; nloop++) {
                String strTemp = String.format("%.2f", nStart);
                mFreqStartList.add(strTemp);
                mFreqEndList.add(strTemp);
                nStart += 0.5f;
            }
        } else if (regionNorm == 0x02) {
            nStart = 865.00f;
            for (nloop = 0; nloop < 7; nloop++) {
                String strTemp = String.format("%.2f", nStart);
                mFreqStartList.add(strTemp);
                mFreqEndList.add(strTemp);
                nStart += 0.5f;
            }
        } else if (regionNorm == 0x03) {
            nStart = 920.00f;
            for (nloop = 0; nloop < 11; nloop++) {
                String strTemp = String.format("%.2f", nStart);
                mFreqStartList.add(strTemp);
                mFreqEndList.add(strTemp);
                nStart += 0.5f;
            }
        }

        mSpinerPopWindow1.refreshData(mFreqStartList, 0);
        mSpinerPopWindow2.refreshData(mFreqEndList, 0);

    }

    /**
     * 频谱界面选择
     */
    private void freqViewSelect(int checkedId) {
        switch (checkedId) {
            case R.id.rb_custom_spectrum_radio_button:
                Log.i(TAG, "显示自定义频谱");
                mRegionView = 0;
                mCustomizeView.setVisibility(View.VISIBLE);
                mSystemView.setVisibility(View.GONE);
                mCustomizeStartEdit.setText("");
                mCustomizeEndEdit.setText("");
                mCustomizeSpaceEdit.setText("1");
                mCustomizeQuantityEdit.setText("1");
                break;
            case R.id.rb_system_frequency_radio_button:
                Log.i(TAG, "显示系统频点");
                mRegionView = 1;
                mCustomizeView.setVisibility(View.GONE);
                mSystemView.setVisibility(View.VISIBLE);
                mFreqStartText.setText("");
                mFreqEndText.setText("");
                btStartFreq = 0;
                btEndFreq = 0;
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                dismiss();
                break;
            case R.id.get_region_button:
                //获取区域
                getRegion();
                break;
            case R.id.set_region_button:
                //设置区域
                setRegion(mRegionView);
                break;
            case R.id.rb_version_button:
                //获取版本信息
                String version = ((RBUHFActivity) context).api.getVersion();
                mVersionText.setText(version);
                break;
            case R.id.rb_reset_button:
                //复位
                int resetCode = ((RBUHFActivity) context).api.reset();
                String resetMsg = resetCode == RBUFHConfig.command_success ?
                        context.getResources().getString(R.string.rbuhf_reset_success) :
                        context.getResources().getString(R.string.rbuhf_reset_failure) + resetCode;
                mVersionText.setText(resetMsg);
                break;
            case R.id.rb_get_power_button:
                //获取功率
                mOutPowerText.setText("");
                RBUFHAPI.CmdResponse getPower = ((RBUHFActivity) context).api.getPower();
                String getPowerMsg;
                Log.i(TAG, "getPower.respondData = " + getPower.respondData);
                if (TextUtils.equals(getPower.responseHead, DataUtils.toHexString1(RBUFHConfig.Head)) &&
                        TextUtils.equals(getPower.responseCmd, DataUtils.toHexString1(RBUFHConfig.cmd_get_output_power))) {
                    getPowerMsg = context.getResources().getString(R.string.rbuhf_get_power_success);
                    mOutPowerText.setText(getPower.responseErrorCode + "");
                } else {
                    getPowerMsg = context.getResources().getString(R.string.rbuhf_get_power_failure) + getPower.respondData;
                }
                DataUtils.showToast(context, getPowerMsg);
                break;
            case R.id.rb_set_power_button:
                //设置功率
                int setPower = Integer.parseInt(mOutPowerText.getText().toString());
                String setPowerMsg;
                if (setPower < 18 || setPower > 26) {
                    setPowerMsg = context.getResources().getString(R.string.rbuhf_set_power_outnumber);
                    DataUtils.showToast(context, setPowerMsg);
                    return;
                }
                Log.i(TAG, "setPower = " + setPower + ",(byte) setPower = " + (byte) setPower);
                int setPowerCode = ((RBUHFActivity) context).api.setPower((byte) setPower);
                setPowerMsg = setPowerCode == RBUFHConfig.command_success ?
                        context.getResources().getString(R.string.rbuhf_set_power_success) :
                        context.getResources().getString(R.string.rbuhf_set_power_failure) + setPowerCode;
                DataUtils.showToast(context, setPowerMsg);
                break;
            default:
                break;
        }
    }

    private void getRegion() {
        //获取区域
        RBUFHAPI.CmdResponse getRegion = ((RBUHFActivity) context).api.getFrequencyRegion();
        Log.e(TAG, "getRegion.respondData = " + getRegion.respondData);
        String msg;

        //判断返回值
        if (TextUtils.equals(getRegion.responseHead, DataUtils.toHexString1(RBUFHConfig.Head)) &&
                TextUtils.equals(getRegion.responseCmd, DataUtils.toHexString1(RBUFHConfig.cmd_get_frequency_region))) {

            //获取返回区域
            if (TextUtils.equals(getRegion.responseLength, DataUtils.toHexString1((byte) 0x06))) {
                //系统默认频点
                mRegionRadioGroup.check(R.id.rb_system_frequency_radio_button);
                msg = context.getString(R.string.hxuhf_get_region_success);

                //射频规范
                String region = DataUtils.toHexString1(getRegion.temp[4]);
                btStartFreq = getRegion.temp[5];
                btEndFreq = getRegion.temp[6];
                int position = getPosition(getRegion.temp[4]);

                Log.i(TAG, "系统默认频点  region = " + region + " ,btStartFreq = " + btStartFreq + " ,btEndFreq = " + btEndFreq);

                mSystemSpRegion.setSelection(position);

                if (mPos1 >= 0 && mPos1 < mFreqStartList.size()) {
                    mFreqStartText.setText(mFreqStartList.get(mPos1));
                }
                if (mPos2 >= 0 && mPos2 < mFreqEndList.size()) {
                    mFreqEndText.setText(mFreqEndList.get(mPos2));
                }

            } else if (TextUtils.equals(getRegion.responseLength, DataUtils.toHexString1((byte) 0x09))) {
                //自定义频点
                mRegionRadioGroup.check(R.id.rb_custom_spectrum_radio_button);
                msg = context.getString(R.string.hxuhf_get_region_success);

                //射频规范 固定0x04
                String region = DataUtils.toHexString1(getRegion.temp[4]);
                //频点间隔 = FreqSpace x 10KHz
                int freqSpace = Integer.parseInt(DataUtils.toHexString1(getRegion.temp[5]), 16);
                //频点数量 1为以起始频率定频发射。此参数必须大于0
                int freqQuantity = Integer.parseInt(DataUtils.toHexString1(getRegion.temp[6]), 16);
                //起始频率
                byte[] data = new byte[3];
                System.arraycopy(getRegion.temp, 7, data, 0, data.length);
                String start = DataUtils.toHexString(data);//单位为KHz。16进制数高位在前。例如915000KHz则返回 0D F6 38

                int startFreq = Integer.valueOf(start, 16);
                int endFreq = startFreq + freqSpace * 10 * freqQuantity;

                Log.i(TAG, "自定义频点 region = " + region + " ,freqSpace = " + freqSpace + " ,freqQuantity = " + freqQuantity + " ,startFreq = " + startFreq + " ,endFreq = " + endFreq);

                mCustomizeSpaceEdit.setText(freqSpace + "");
                mCustomizeQuantityEdit.setText(freqQuantity + "");
                mCustomizeStartEdit.setText(startFreq + "");
                mCustomizeEndEdit.setText(endFreq + "");
            } else {
                msg = context.getString(R.string.hxuhf_get_region_error1);
            }
        } else {
            msg = context.getString(R.string.hxuhf_get_region_error1);
        }
        DataUtils.showToast(context, msg);
    }

    private void setRegion(int regionMode) {
        byte[] concat;

        if (regionMode == 0) {
            //自定义频谱
            //获取起始频率、频点间隔、频点数量 数值
            String CustomizeStart = mCustomizeStartEdit.getText().toString();
            String CustomizeSpace = mCustomizeSpaceEdit.getText().toString();
            String CustomizeQuantity = mCustomizeQuantityEdit.getText().toString();

            //判空
            if (TextUtils.isEmpty(CustomizeStart)) {
                DataUtils.showToast(context, context.getString(R.string.rbuhf_setting_empty_error1));
                return;
            }
            if (TextUtils.isEmpty(CustomizeSpace)) {
                DataUtils.showToast(context, context.getString(R.string.rbuhf_setting_empty_error2));
                return;
            }
            if (TextUtils.isEmpty(CustomizeQuantity)) {
                DataUtils.showToast(context, context.getString(R.string.rbuhf_setting_empty_error3));
                return;
            }

            //转换有效值
            int startEdit = Integer.parseInt(CustomizeStart);
            short spaceEdit = Short.parseShort(CustomizeSpace);
            short quantityEdit = Short.parseShort(CustomizeQuantity);

            //判断有效值
            if (spaceEdit < 1) {
                DataUtils.showToast(context, context.getString(R.string.rbuhf_setting_error2));
                return;
            }
            if (quantityEdit < 1) {
                DataUtils.showToast(context, context.getString(R.string.rbuhf_setting_error3));
                return;
            }

            //起始频率 补零
            String startString = Integer.toHexString(startEdit);
            if (startString.length() % 2 == 1) {
                startString = "0" + startString;
            }

            //合成指令  Region  FreqSpace  RreqQuantity  StartFreq
            concat = ((RBUHFActivity) context).api.concat(new byte[]{(byte) 0x04},
                    DataUtils.int1Byte1(spaceEdit),
                    DataUtils.int1Byte1(quantityEdit),
                    DataUtils.hexStringTobyte(startString));

        } else {//if (regionMode == 1)
            //系统频谱
            int position = mSystemSpRegion.getSelectedItemPosition();

            byte region = getRegionNorm(position);
            Log.i(TAG, "position = " + position);
            Log.i(TAG, "region = " + region);

            if (TextUtils.isEmpty(mFreqStartText.getText().toString())) {
                DataUtils.showToast(context, context.getString(R.string.rbuhf_setting_empty_error1));
                return;
            }
            if (TextUtils.isEmpty(mFreqEndText.getText().toString())) {
                DataUtils.showToast(context, context.getString(R.string.rbuhf_setting_empty_error4));
                return;
            }
            if (btStartFreq > btEndFreq) {
                DataUtils.showToast(context, context.getString(R.string.rbuhf_setting_error4));
                return;
            }

            //合成指令  Region  FreqSpace  RreqQuantity  StartFreq
            concat = ((RBUHFActivity) context).api.concat(new byte[]{region, btStartFreq, btEndFreq});
        }

        Log.i(TAG, "concat = " + DataUtils.toHexString(concat));

        int code = ((RBUHFActivity) context).api.setFrequencyRegion(concat);
        Log.i(TAG, "setFrequencyRegion code = " + code);
        DataUtils.showToast(context, code == RBUFHConfig.command_success ?
                context.getString(R.string.rbuhf_set_power_success) :
                context.getString(R.string.rbuhf_set_power_failure) + code);
    }

    private byte getRegionNorm(int position) {
        byte regionNorm;
        switch (position) {
            case 0:
                regionNorm = 0x01;
                btStartFreq = (byte) (mPos1 + 7);
                btEndFreq = (byte) (mPos2 + 7);
                break;
            case 1:
                regionNorm = 0x02;
                btStartFreq = (byte) (mPos1);
                btEndFreq = (byte) (mPos2);
                break;
            case 2:
                regionNorm = 0x03;
                btStartFreq = (byte) (mPos1 + 43);
                btEndFreq = (byte) (mPos2 + 43);
                break;
            default:
                regionNorm = 0x01;
                break;
        }
        return regionNorm;
    }

    private int getPosition(byte regionNorm) {
        int position = 0;
        switch (regionNorm) {
            case 0x01:
                position = 0;
                mPos1 = btStartFreq - 7;
                mPos2 = btEndFreq - 7;
                break;
            case 0x02:
                position = 1;
                mPos1 = btStartFreq;
                mPos2 = btEndFreq;
                break;
            case 0x03:
                position = 2;
                mPos1 = btStartFreq - 43;
                mPos2 = btEndFreq - 43;
                break;
        }
        return position;
    }

    public interface OnMySettingCallback {
        void onSetting(int times, byte code, String pwd, short sa, short dl);
    }
}