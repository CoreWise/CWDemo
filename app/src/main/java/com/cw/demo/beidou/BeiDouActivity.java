package com.cw.demo.beidou;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.cw.beidousdk.BeiDouAPI;
import com.cw.beidousdk.intf.DWXXListener;
import com.cw.beidousdk.intf.FKXXListener;
import com.cw.beidousdk.intf.ICJCListener;
import com.cw.beidousdk.intf.TXXXListener;
import com.cw.beidousdk.intf.XTZJListener;
import com.cw.demo.R;
import com.cw.serialportsdk.utils.DataUtils;

import java.io.UnsupportedEncodingException;

/**
 * @author Administrator
 */
public class BeiDouActivity extends AppCompatActivity implements OnClickListener, FKXXListener, TXXXListener {

    private static final String TAG = "BeiDouActivity";

    private Button mBtXTZJ, mBtICJC, mBtDCDW, mBtTXSQ;
    private TextView mTvICState, mTvHardwareState, mTvBatteryState, mTvInboundState, mTvBeam1, mTvBeam2, mTvBeam3,
            mTvBeam4, mTvBeam5, mTvBeam6, mTvBroadcastID, mTvUserCharacteristic, mTvServiceFrequency, mTvEncryptionMark,
            mTvTime, mTvLat, mTvLng, mTvHight, mTvData, mTvOtherAddress, mTvRevData;
    private EditText mEtAddress, mEtMsg;
    private Spinner mSpIsChinese;
    private String[] sp = {"汉字", "代码", "混发"};


    BeiDouAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "-----------onCreate------------");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_beidou);

        init();

        api = BeiDouAPI.getInstance();

        api.setTXXXListener(this);

    }

    @Override
    protected void onResume() {
        Log.i(TAG, "-----------onResume------------");

        api.open(this);
        super.onResume();

    }


    @Override
    protected void onPause() {
        Log.i(TAG, "-----------onPause------------");

        api.close();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "-----------onStart------------");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "-----------onDestroy------------");


    }

    private void init() {
        mBtXTZJ = (Button) findViewById(R.id.bt_xtzj);
        mBtXTZJ.setOnClickListener(this);

        mBtICJC = (Button) findViewById(R.id.bt_ICJC);
        mBtICJC.setOnClickListener(this);

        mBtDCDW = (Button) findViewById(R.id.bt_DCDW);
        mBtDCDW.setOnClickListener(this);

        mBtTXSQ = (Button) findViewById(R.id.bt_send);
        mBtTXSQ.setOnClickListener(this);

        mTvICState = (TextView) findViewById(R.id.tv_ICState);
        mTvHardwareState = (TextView) findViewById(R.id.tv_hardwareState);
        mTvBatteryState = (TextView) findViewById(R.id.tv_batteryState);
        mTvInboundState = (TextView) findViewById(R.id.tv_inboundState);
        mTvBeam1 = (TextView) findViewById(R.id.tv_beam1);
        mTvBeam2 = (TextView) findViewById(R.id.tv_beam2);
        mTvBeam3 = (TextView) findViewById(R.id.tv_beam3);
        mTvBeam4 = (TextView) findViewById(R.id.tv_beam4);
        mTvBeam5 = (TextView) findViewById(R.id.tv_beam5);
        mTvBeam6 = (TextView) findViewById(R.id.tv_beam6);
        mTvBroadcastID = (TextView) findViewById(R.id.tv_broadcastID);
        mTvUserCharacteristic = (TextView) findViewById(R.id.tv_userCharacteristic);
        mTvServiceFrequency = (TextView) findViewById(R.id.tv_serviceFrequency);
        mTvEncryptionMark = (TextView) findViewById(R.id.tv_encryptionMark);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvLat = (TextView) findViewById(R.id.tv_lat);
        mTvLng = (TextView) findViewById(R.id.tv_lng);
        mTvHight = (TextView) findViewById(R.id.tv_hight);
        mTvData = (TextView) findViewById(R.id.tv_data);
        mTvOtherAddress = (TextView) findViewById(R.id.tv_otherAddress);
        mTvRevData = (TextView) findViewById(R.id.tv_revData);

        mSpIsChinese = (Spinner) findViewById(R.id.sp_isChinese);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sp);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpIsChinese.setAdapter(adapter);
        mSpIsChinese.setSelection(0);

        mEtAddress = (EditText) findViewById(R.id.et_address);
        mEtMsg = (EditText) findViewById(R.id.et_msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_xtzj:
                //系统自检,0,单次检测
                api.XTZJ(0, new XTZJListener() {

                    @Override
                    public void ICStates(boolean isICHandlerNormal, boolean isIDNormal, boolean isCheckCodeCorrect,
                                         boolean isSerialNoNormal, boolean ManagementCardOrUserCard, boolean isDataNormal,
                                         boolean isICNormal) {
                        StringBuilder ICState = new StringBuilder();
                        ICState.append(isICHandlerNormal ? "智能卡处理正常、" : "智能卡处理异常");
                        ICState.append(isIDNormal ? "ID号正常、" : "ID号出错、");
                        ICState.append(isCheckCodeCorrect ? "校验码正常、" : "校验码错误、");
                        ICState.append(isSerialNoNormal ? "序列号正常、" : "序列号出错、");
                        ICState.append(ManagementCardOrUserCard ? "管理卡、" : "用户卡、");
                        ICState.append(isDataNormal ? "智能卡数据正常、" : "智能卡数据不完整、");
                        ICState.append(isICNormal ? "智能卡正常" : "智能卡物理缺损");
                        mTvICState.setText(ICState);
                    }

                    @Override
                    public void HardwareStates(boolean isAntennaNormal, boolean isChannelNormal,
                                               boolean isMainBoardNormal) {
                        StringBuilder HardwareState = new StringBuilder();
                        HardwareState.append(isAntennaNormal ? "天线状态正常、" : "天线未连接、");
                        HardwareState.append(isChannelNormal ? "通道状态正常、" : "通道故障、");
                        HardwareState.append(isMainBoardNormal ? "主板正常、" : "主板故障、");
                        mTvHardwareState.setText(HardwareState);
                    }

                    @Override
                    public void BatteryLevel(int percent) {
                        String BatteryLevel = percent + "%";
                        mTvBatteryState.setText(BatteryLevel);
                    }

                    @Override
                    public void InboundStates(boolean isSuppression, boolean isSilence) {
                        StringBuilder InboundState = new StringBuilder();
                        InboundState.append(isSuppression ? "抑制状态：抑制、" : "抑制状态：非抑制、");
                        InboundState.append(isSilence ? "静默状态：静默、" : "抑制状态：非静默、");
                        mTvInboundState.setText(InboundState);
                    }
                });
                break;
            case R.id.bt_ICJC:
                api.ICJC(0, new ICJCListener() {

                    @Override
                    public void ICInfo(int broadcastID, String userCharacteristic, int serviceFrequency,
                                       boolean isEncryptionUser) {
                        mTvBroadcastID.setText(broadcastID + "");
                        String Characteristic = "";
                        switch (userCharacteristic) {
                            case "000":
                                Characteristic = "指挥型用户机";
                                break;
                            case "001":
                                Characteristic = "一类用户机";
                                break;
                            case "010":
                                Characteristic = "二类用户机";
                                break;
                            case "011":
                                Characteristic = "三类用户机";
                                break;
                            case "100":
                                Characteristic = "指挥型用户机（进行身份证验证）";
                                break;
                            case "101":
                                Characteristic = "一类用户机（进行身份证验证）";
                                break;
                            case "110":
                                Characteristic = "二类用户机（进行身份证验证）";
                                break;
                            case "111":
                                Characteristic = "三类用户机（进行身份证验证）";
                                break;
                            default:
                                break;
                        }
                        mTvUserCharacteristic.setText(userCharacteristic + "(" + Characteristic + ")");
                        mTvServiceFrequency.setText(serviceFrequency + "s");
                        mTvEncryptionMark.setText(isEncryptionUser ? "保密用户" : "非密用户");
                    }
                });
                break;
            case R.id.bt_DCDW:
                api.DWSQ(false, 0, new DWXXListener() {

                    @Override
                    public void LocationInfo(String time, float lng, float lat) {
                        mTvTime.setText(time);
                        mTvLat.setText(lat + "");
                        mTvLng.setText(lng + "");
                    }
                });
                break;
            case R.id.bt_send:


                int type = mSpIsChinese.getSelectedItemPosition();
                byte[] msg = null;
                String sendMsg = mEtMsg.getText().toString();
                if (sendMsg.equals("")) {
                    Toast.makeText(this, "发送内容不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    switch (type) {
                        case 0:
                            msg = sendMsg.getBytes("gb2312");
                            break;
                        case 1:
                            msg = DataUtils.hexStringTobyte(sendMsg);
                            break;
                        case 2:
                            byte[] data = sendMsg.getBytes("gb2312");
                            msg = new byte[1 + data.length];
                            msg[0] = (byte) 0xa4;
                            System.arraycopy(data, 0, msg, 1, data.length);
                            break;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String userAdress = mEtAddress.getText().toString();
                if (userAdress.equals("")) {
                    Toast.makeText(this, "用户地址不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }

                int address = Integer.parseInt(userAdress);

                String addressHex = Integer.toHexString(address);

                if (!(addressHex.length() / 2 == 0)) {
                    addressHex = "0" + addressHex;
                }

                api.TXSQ(type, DataUtils.hexStringTobyte(addressHex), msg);

                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //返回键监听
                Log.i(TAG, "点击了返回键");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.general_tips);
                builder.setMessage(R.string.general_exit);

                //设置确定按钮
                builder.setNegativeButton(getResources().getString(R.string.general_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                //设置取消按钮
                builder.setPositiveButton(getResources().getString(R.string.general_no), null);
                //显示提示框
                builder.show();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void FKXX(int mark, String cmd) {
        String msg = "";
        switch (mark) {
            case 0:
                msg = "成功";
                break;
            case 1:
                msg = "失败";
                break;
            case 2:
                msg = "信号未锁定";
                break;
            case 3:
                msg = "发射被抑制";
                break;
            case 4:
                msg = "发射频度未找到";
                break;
            case 5:
                msg = "加解密错误";
                break;
            case 6:
                msg = "CRC错误";
                break;
            case 7:
                msg = "用户机被抑制";
                break;
            case 8:
                msg = "抑制解除";
                break;
            default:

                break;
        }
        Toast.makeText(getApplicationContext(), cmd + ":" + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void testData(byte[] data) {
        String hexData = DataUtils.toHexString(data);
        mTvData.setText(hexData + "\n" + mTvData.getText().toString());
    }

    @Override
    public void TXXX(byte[] address, boolean isChinese, byte[] msg) {
        mTvOtherAddress.setText(DataUtils.toHexString(address));
        String revData = "";
        if (isChinese) {
            try {
                revData = new String(msg, "gb2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            if ((msg[0] & 0xff) == 0xa4) {
                byte[] data = new byte[msg.length - 1];
                System.arraycopy(msg, 1, data, 0, data.length);
                try {
                    revData = new String(data, "gb2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                revData = DataUtils.toHexString(msg);
            }
        }
        mTvRevData.setText(revData);
    }

    @Override
    public void TXFail(byte[] data) {
        mTvData.setText("通信解析失败:" + DataUtils.toHexString(data) + "\n" + mTvData.getText().toString());
    }
}