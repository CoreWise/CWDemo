package com.cw.demo.UHF.hxuhf;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cw.demo.R;
import com.cw.phychipsuhfsdk.UHFHXAPI;
import com.cw.serialportsdk.utils.DataUtils;

import java.util.HashMap;

public class MySettingDialog extends Dialog implements View.OnClickListener {
    private String TAG = MySettingDialog.class.getSimpleName();

    private Spinner mSpArea;
    private Spinner mSpRegion;
    private Spinner mSpChannel;
    private Button mButtonGetRegion;
    private Button mButtonSetRegion;
    private Button mButtonGetChannel;
    private Button mButtonSetChannel;
    private Button btn_ok;
    private Button btn_cancel;
    private EditText mEtTimeout, mEtPwd, mEtOffSet, mEtLength;
    private OnMySettingCallback callback;
    private Context context;

    private String[] region;
    private Integer[] channel;
    private HashMap<Integer, String> channelMap;
    private ArrayAdapter<Integer> channelAdapter;
    private boolean isInit = false;

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

        getRegion();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isInit)
                {
                    isInit = true;
                    getChannel();
                }
            }
        },100);

        setCancelable(false);
    }

    private void init() {
        mSpArea = findViewById(R.id.sp_area);
        String[] areas = new String[]{"EPC", "TID", "USER"};
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(context,
                R.layout.hxuhf_simple_list_item, areas);
        areaAdapter.setDropDownViewResource(R.layout.hxuhf_simple_list_item);
        mSpArea.setAdapter(areaAdapter);

        mSpChannel = findViewById(R.id.sp_channel);
        channelMap = new HashMap<>();

        mSpRegion = findViewById(R.id.sp_region);
        region = context.getResources().getStringArray(R.array.hxuhf_region);
        ArrayAdapter<String> regionAdapter = new ArrayAdapter<String>(context,
                R.layout.hxuhf_simple_list_item, region);
        regionAdapter.setDropDownViewResource(R.layout.hxuhf_simple_list_item);
        mSpRegion.setAdapter(regionAdapter);

        mSpRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setChannelSpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mEtTimeout = findViewById(R.id.et_outtime);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtOffSet = findViewById(R.id.et_offset);
        mEtLength = findViewById(R.id.et_strlength);

        btn_ok = findViewById(R.id.ok);
        btn_ok.setOnClickListener(this);

        btn_cancel = findViewById(R.id.cancel);
        mButtonGetRegion = findViewById(R.id.get_region_button);
        mButtonSetRegion = findViewById(R.id.set_region_button);
        mButtonGetChannel = findViewById(R.id.get_channel_button);
        mButtonSetChannel = findViewById(R.id.set_channel_button);

        btn_cancel.setOnClickListener(this);
        mButtonGetRegion.setOnClickListener(this);
        mButtonSetRegion.setOnClickListener(this);
        mButtonGetChannel.setOnClickListener(this);
        mButtonSetChannel.setOnClickListener(this);

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
                            (byte) (mSpArea.getSelectedItemPosition()),
                            mEtPwd.getText().toString(),
                            Short.parseShort(mEtOffSet.getText().toString()),
                            Short.parseShort(mEtLength.getText().toString()));
                    dismiss();
                }
                break;
            case R.id.cancel:
                dismiss();
                break;
            case R.id.get_region_button:
                //获取区域
                getRegion();
                break;
            case R.id.set_region_button:
                //设置区域
                int position = mSpRegion.getSelectedItemPosition();
                setRegion(position);

                break;
            case R.id.get_channel_button:
                //获取信道
                getChannel();
                break;
            case R.id.set_channel_button:
                //设置信道
                int position2 = mSpChannel.getSelectedItemPosition();
                setChannel(position2);
                break;
            default:
                break;
        }
    }

    private void getRegion() {
        //获取区域
        UHFHXAPI.Response getRegionResponse = ((HXUHFActivity) context).api.getRegion();
        Log.e(TAG, "getRegionResponse.result = " + getRegionResponse.result);

        //判断返回值
        if (getRegionResponse.result == UHFHXAPI.Response.RESPONSE_PACKET) {
            //获取返回区域
            String getRegion = DataUtils.toHexString(getRegionResponse.data);
            Log.e(TAG, "getRegionResponse.data = " + getRegion);

            //查看区域对应spinner位置
            int position = getRegionPosition(getRegion);
            if (position > -1) {
                //设置对应位置
                mSpRegion.setSelection(position, true);
                //设置区域对应信道
                setChannelSpinner(position);
                DataUtils.showToast(context, context.getString(R.string.hxuhf_get_region_success));
            } else {
                DataUtils.showToast(context, context.getString(R.string.hxuhf_get_region_error2));
            }
        } else {
            DataUtils.showToast(context, context.getString(R.string.hxuhf_get_region_error1));
        }
    }

    private void setRegion(int position) {
        int argument = getRegionArgument(position);

        UHFHXAPI.Response setRegionResponse = ((HXUHFActivity) context).api.setRegion(argument);

        Log.e(TAG, "setRegionResponse.result = " + setRegionResponse.result);
        if (setRegionResponse.result == UHFHXAPI.Response.RESPONSE_PACKET) {
            String getRegion = DataUtils.toHexString(setRegionResponse.data);
            Log.e(TAG, "setRegionResponse.data = " + getRegion);
            DataUtils.showToast(context, context.getString(R.string.hxuhf_set_region_success));
        } else {
            DataUtils.showToast(context, context.getString(R.string.hxuhf_set_region_error1));
        }
    }

    private void getChannel() {
        //获取信道
        UHFHXAPI.Response getCurrentRFChannel = ((HXUHFActivity) context).api.getCurrentRFChannel();
        Log.e(TAG, "getCurrentRFChannel.result = " + getCurrentRFChannel.result);

        //判断返回值
        if (getCurrentRFChannel.result == UHFHXAPI.Response.RESPONSE_PACKET) {
            //获取返回信道 截取前2位
            String substring = DataUtils.bytesToHexString(getCurrentRFChannel.data).substring(0, 2);
            Log.e(TAG, "currentRFChannel.data = " + DataUtils.bytesToHexString(getCurrentRFChannel.data));
            //16进制转10进制
            Integer integer = Integer.valueOf(substring, 16);
            Log.e(TAG, "integer = " + integer);

            //查看信道对应spinner位置
            int channel = getChannelPosition(integer);
            if (channel > -1) {
                //设置对应位置
                mSpChannel.setSelection(channel, true);
                DataUtils.showToast(context, context.getString(R.string.hxuhf_get_channel_success));
            } else {
                DataUtils.showToast(context, context.getString(R.string.hxuhf_get_channel_error2));
            }
        } else {
            DataUtils.showToast(context, context.getString(R.string.hxuhf_get_channel_error1));
        }
    }

    private void setChannel(int position) {
        byte[] argument2 = DataUtils.int2Byte(channel[position]);

        UHFHXAPI.Response setCurrentRFChannel = ((HXUHFActivity) context).api.setCurrentRFChannel(argument2);

        Log.e(TAG, "setCurrentRFChannel.result = " + setCurrentRFChannel.result);
        if (setCurrentRFChannel.result == UHFHXAPI.Response.RESPONSE_PACKET) {
            String getChannel = DataUtils.toHexString(setCurrentRFChannel.data);
            Log.e(TAG, "setCurrentRFChannel.data = " + getChannel);
            DataUtils.showToast(context, context.getString(R.string.hxuhf_set_channel_success));
        } else {
            DataUtils.showToast(context, context.getString(R.string.hxuhf_set_channel_error1));
        }
    }

    /**
     * 获取区域
     *
     * @param position 选择的position
     * @return 16进制
     */
    private int getRegionArgument(int position) {
        int argument;
        switch (position) {
            case 0:
                //韩国
                argument = 0x11;
                break;
            case 1:
                //北美
                argument = 0x21;
                break;
            case 2:
                //us
                argument = 0x22;
                break;
            case 3:
                //欧洲
                argument = 0x31;
                break;
            case 4:
                //日本
                argument = 0x41;
                break;
            case 5:
                //中国1
                argument = 0x51;
                break;
            case 6:
                //中国2
                argument = 0x52;
                break;
            default:
                argument = 0x11;
                break;
        }
        return argument;
    }

    private int getRegionPosition(String regionName) {
        int position = -1;
        switch (regionName) {
            case "11":
                //韩国
                position = 0;
                break;
            case "21":
                //北美
                position = 1;
                break;
            case "22":
                //us
                position = 2;
                break;
            case "31":
                //欧洲
                position = 3;
                break;
            case "41":
                //日本
                position = 4;
                break;
            case "51":
                //中国1
                position = 5;
                break;
            case "52":
                //中国2
                position = 6;
                break;
        }

//        for (int i = 0; i < region.length; i++) {
//            if (TextUtils.equals(region[i], regionName)) {
//                return i;
//            }
//        }
        return position;
    }

    private void setChannelSpinner(int position) {
        channelMap.clear();
        double band;
        switch (position) {
            case 0:
                //韩国
                channel = new Integer[32];
                band = 920.90;
                for (int i = 0; i < 32; i++) {
                    channel[i] = i + 1;
                    if (i + 1 >= 20) {
                        band = band + (i + 1 - 20) * 0.2;
                        channelMap.put(i + 1, band + " MHz");
                    }
                }
                break;
            case 1:
                //北美
                channel = new Integer[50];
                band = 917.10;
                for (int i = 0; i < 50; i++) {
                    channel[i] = i + 1;
                    band = band + i * 0.2;
                    channelMap.put(i + 1, band + " MHz");
                }
                break;
            case 2:
                //us
                channel = new Integer[50];
                band = 902.75;
                for (int i = 0; i < 50; i++) {
                    channel[i] = i + 1;
                    band = band + i * 0.5;
                    channelMap.put(i + 1, band + " MHz");
                }
                break;
            case 3:
                //欧洲
                channel = new Integer[4];
                channel[0] = 4;
                channel[1] = 7;
                channel[2] = 10;
                channel[3] = 13;
                channelMap.put(4, "865.70 MHz");
                channelMap.put(7, "866.30 MHz");
                channelMap.put(10, "866.90 MHz");
                channelMap.put(13, "867.50 MHz");
                break;
            case 4:
                //日本
                channel = new Integer[15];
                band = 920.6;
                for (int i = 0; i < 15; i++) {
                    channel[i] = i + 24;
                    band = band + i * 0.2;
                    channelMap.put(i + 24, band + " MHz");
                }
                break;
            case 5:
                //中国1
                channel = new Integer[0];
                break;
            case 6:
                //中国2
                channel = new Integer[20];
                band = 920.125;
                for (int i = 0; i < 20; i++) {
                    channel[i] = i + 1;
                    band = band + i * 0.25;
                    channelMap.put(i + 1, band + " MHz");
                }
                break;
        }

        channelAdapter = new ArrayAdapter<Integer>(context, R.layout.hxuhf_simple_list_item2, channel);
        channelAdapter.setDropDownViewResource(R.layout.hxuhf_simple_list_item2);
        mSpChannel.setAdapter(channelAdapter);
    }

    private int getChannelPosition(int integer) {
        for (int i = 0; i < channel.length; i++) {
            if (channel[i] == integer) {
                return i;
            }
        }

        return -1;
    }

    public interface OnMySettingCallback {
        void onSetting(int times, byte code, String pwd, short sa, short dl);
    }
}