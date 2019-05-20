package com.cw.demo.fingerprint.jra;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.cw.demo.R;
import com.cw.fpjrasdk.USBFingerManager;
import com.cw.serialportsdk.utils.DataUtils;
import com.synochip.sdk.ukey.SyOTG_Key;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 作者：李阳
 * 时间：2018/12/29
 * 描述：
 *
 * @author Administrator
 */
public class FpJRAActivity extends AppCompatActivity {


    private static final String TAG = "SynoOTGKeyFragment";

    private static final String ACTION_USB_PERMISSION = "com.synochip.demo.OTG_DEMO";
    private static final int MAX_LINES = 12;
    private static final int CNT_LINES = 11;
    private static final int PS_NO_FINGER = 0x02;
    private static final int PS_OK = 0x00;
    private static int fingerCnt = 1;
    private static int IMAGE_X = 256;
    private static int IMAGE_Y = 288;
    public int threadCnt;
    //private int opened = 0;
    public int thread_i = 0;
    public int thread_sum = 0;
    public boolean start_clt = false;

    @BindView(R.id.bt_enroll)
    Button btEnroll;
    @BindView(R.id.bt_verify)
    Button btVerify;
    @BindView(R.id.bt_identify)
    Button btIdentify;
    @BindView(R.id.bt_clear)
    Button btClear;
    @BindView(R.id.bt_show)
    Button btShow;
    Unbinder unbinder;
    @BindView(R.id.bar)
    ProgressBar bar;
    @BindView(R.id.fingerimage)
    ImageView fingerView;
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.captureTime)
    EditText captureTime;
    @BindView(R.id.extractTime)
    EditText extractTime;
    @BindView(R.id.verifyTime)
    EditText verifyTime;
    String imagePath = "finger.bmp";
    byte[] fingerBuf = new byte[IMAGE_X * IMAGE_Y];
    boolean ifChecked = false;
    byte mbAppHand[] = new byte[1];
    byte mbConHand[] = new byte[1];
    boolean bIsOpen = false;
    byte[] g_TempData = new byte[512];
    @BindView(R.id.tv_char)
    AppCompatTextView tvChar;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;


    private SyOTG_Key msyUsbKey;
    private boolean mDeviceOpened = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fp_jra);
        unbinder = ButterKnife.bind(this);

        USBFingerManager.getInstance(this).openUSB(new USBFingerManager.OnUSBFingerListener() {
            @Override
            public void onOpenUSBFingerSuccess(String s, UsbManager usbManager, UsbDevice usbDevice) {
                if (s.equals(USBFingerManager.BYD_SMALL_DEVICE)) {

                    msyUsbKey = new SyOTG_Key(usbManager, usbDevice);
                    int ret = msyUsbKey.SyOpen();
                    if (ret == SyOTG_Key.DEVICE_SUCCESS) {
                        Log.e(TAG, "open device success hkey!");
                        btnFingerState(true);
                        mDeviceOpened=true;
                    } else {
                        Log.e(TAG, "open device fail errocde :" + ret);
                    }
                }
            }

            @Override
            public void onOpenUSBFingerFailure(String s) {

            }
        });

        btnFingerState(false);

    }


    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "---------onPause------------");
        btnFingerState(false);
        closeFingerDevice();
        mDeviceOpened = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---------onDestroy------------");
        USBFingerManager.getInstance(this).closeUSB();
    }


    @OnClick({ R.id.bt_enroll, R.id.bt_verify, R.id.bt_identify, R.id.bt_clear, R.id.bt_show})
    public void onViewClicked(View view) {

        tvChar.setText("");

        switch (view.getId()) {

            case R.id.bt_enroll:
                //采集指纹
                enroll();

                btnEnble(false);
                break;
            case R.id.bt_verify:
                //1:1比对

                break;
            case R.id.bt_identify:
                //1:N比对
                SearchAsyncTask asyncTask_search = new SearchAsyncTask();
                asyncTask_search.execute(1);
                btnEnble(false);
                break;
            case R.id.bt_clear:
                //清空指纹库
                clear();

                break;
            case R.id.bt_show:
                //查看设备信息
                tvInfo.setText(DevInfos());

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Log.i(TAG, "点击了返回键");
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
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 关闭指纹设备
     */
    private boolean closeFingerDevice() {

        try {
            //uiState(true);
            //logMsg("Device Closed");
            //openState(true);
            if (msyUsbKey != null) {
                msyUsbKey.SyClose();
            }

            Log.e(TAG, "Device Closed");

            return true;
        } catch (Exception e) {
            //logMsg("Exception: => " + e.toString());
            Log.e(TAG, "Exception: => " + e.toString());
            return false;
        }

    }


    /**
     * 采集指纹
     */
    private void enroll() {

        if (fingerCnt >= 256) {
            Log.i(TAG, "fingerC= 256");
            return;
        }
        bar.setVisibility(View.VISIBLE);
        bar.setProgress(0);
        ImputAsyncTask asyncTask = new ImputAsyncTask();
        asyncTask.execute(1);

    }


    /**
     * 清空指纹库
     */
    private void clear() {
        if (PS_OK != msyUsbKey.SyClear()) {
            Log.e(TAG, "mClear FAIL");
            tvInfo.setText("Clear FAIL");
            return;
        }
        fingerCnt = 0;
        Log.e(TAG, "mClear OK");
        tvInfo.setText("Clear Ok");

    }

    /**
     * 查看设备信息
     */
    private String DevInfos() {
        String Msg = "";

        int[] indexMax = new int[1];
        int[] len = new int[1];
        byte[] index = new byte[256];
        if (0 != getUserContent(indexMax, index, len)) {
            Log.e(TAG, "Get Device info Error");
            Msg += "Get Device info Error";
            return Msg;
        }

        int i;

        Log.e(TAG, "Device have " + len[0] + " fingers");
        Msg += "Device have " + len[0] + " fingers";

        if (len[0] == 0) {
            return Msg;
        }

        for (i = 0; i < len[0]; i++) {
            Log.e(TAG, "id:" + index[i]);
        }

        //Msg += "\nid:" + index[0] + "~" + index[len[0] - 1];

        if (len[0] != 0) {
            Log.e(TAG, "The Max FingerId is :" + indexMax[0]);
            Msg += "\nThe Max FingerId is :" + indexMax[0];

        }

        return Msg;
    }


    /**
     * 打开模式、关闭模式
     *
     * @param state
     */
    private void btnFingerState(boolean state) {

        //btOpenClose.setEnabled(!state);

        btEnroll.setEnabled(state);
        btVerify.setEnabled(state);
        btIdentify.setEnabled(state);
        btClear.setEnabled(state);
        btShow.setEnabled(state);

    }


    private void btnEnble(boolean state) {
        btEnroll.setEnabled(state);
        btIdentify.setEnabled(state);
        btClear.setEnabled(state);
        btShow.setEnabled(state);
    }

    private int getUserContent(int[] max, byte[] fingerid, int[] len) {
        byte[] UserContent = new byte[32];
        byte bt, b;
        int ret = 0;
        int i;
        int iBase;
        int iIndex = 0;
        int iIndexOffset;
        int[] indexFinger = new int[256];
        int j = 0;
        int indexMax = 0;
        ret = msyUsbKey.SyGetInfo(UserContent);
        if (ret != 0) {
            return -1;
        }

        for (i = 0; i < 32; i++) {
            bt = UserContent[i];
            iBase = i * 8;
            if (bt == (byte) 0x00) {
                continue;
            }

            for (b = (byte) 0x01, iIndexOffset = 0; iIndexOffset < 8; b = (byte) (b << 1), iIndexOffset++) {
                if (0 == (bt & b)) {
                    continue;
                }
                iIndex = iBase + iIndexOffset;
                indexFinger[j] = iIndex;
                j++;
                if (iIndex > indexMax) {
                    indexMax = iIndex;
                }
            }
        }
        max[0] = indexMax;
        len[0] = j;

        for (i = 0; i < j; i++) {
            fingerid[i] = (byte) indexFinger[i];
        }

        return 0;
    }


    /**
     * @param imput
     * @return
     */
    private int WriteBmp(byte[] imput) {
        String fileName = "finger.bmp";
        FileOutputStream fout = null;
        try {
            fout = openFileOutput(fileName, MODE_PRIVATE);
        } catch (FileNotFoundException e2) {
            // TODO Auto-generated catch block
            return -100;
        }
        byte[] temp_head = {0x42, 0x4d,//file type
                //0x36,0x6c,0x01,0x00, //file size***
                0x0, 0x0, 0x0, 0x00, //file size***
                0x00, 0x00, //reserved
                0x00, 0x00,//reserved
                0x36, 0x4, 0x00, 0x00,//head byte***
                //infoheader
                0x28, 0x00, 0x00, 0x00,//struct size

                //0x00,0x01,0x00,0x00,//map width***
                0x00, 0x00, 0x0, 0x00,//map width***
                //0x68,0x01,0x00,0x00,//map height***
                0x00, 0x00, 0x00, 0x00,//map height***

                0x01, 0x00,//must be 1
                0x08, 0x00,//color count
                0x00, 0x00, 0x00, 0x00, //compression
                //0x00,0x68,0x01,0x00,//data size***
                0x00, 0x00, 0x00, 0x00,//data size***
                0x00, 0x00, 0x00, 0x00, //dpix
                0x00, 0x00, 0x00, 0x00, //dpiy
                0x00, 0x00, 0x00, 0x00,//color used
                0x00, 0x00, 0x00, 0x00,//color important
        };
        byte[] head = new byte[1078];
        byte[] newbmp = new byte[1078 + IMAGE_X * IMAGE_Y];
        System.arraycopy(temp_head, 0, head, 0, temp_head.length);


        int i, j;
        long num;
        num = IMAGE_X;
        head[18] = (byte) (num & 0xFF);
        num = num >> 8;
        head[19] = (byte) (num & 0xFF);
        num = num >> 8;
        head[20] = (byte) (num & 0xFF);
        num = num >> 8;
        head[21] = (byte) (num & 0xFF);

        num = IMAGE_Y;
        head[22] = (byte) (num & 0xFF);
        num = num >> 8;
        head[23] = (byte) (num & 0xFF);
        num = num >> 8;
        head[24] = (byte) (num & 0xFF);
        num = num >> 8;
        head[25] = (byte) (num & 0xFF);

        j = 0;
        for (i = 54; i < 1078; i = i + 4) {
            head[i] = head[i + 1] = head[i + 2] = (byte) j;
            head[i + 3] = 0;
            j++;
        }
        System.arraycopy(head, 0, newbmp, 0, head.length);
        System.arraycopy(imput, 0, newbmp, 1078, IMAGE_X * IMAGE_Y);

        try {
            fout.write(newbmp);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            return -101;
        }
        try {
            fout.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return -102;
        }
        return 0;
    }

    /**
     * 采集指纹
     */
    private class ImputAsyncTask extends AsyncTask<Integer, String, Integer> {

        byte[][] fingerFeature = new byte[6][512];

        int Progress = 0;

        @Override
        protected Integer doInBackground(Integer... params) {

            //在onPreExecute()方法执行完之后，会马上执行这个方法

            // TODO Auto-generated method stub
            int cnt = 1;
            int ret;
            while (true) {
                if (mDeviceOpened == false) {
                    return -1;
                }

                while (msyUsbKey.SyGetImage() != PS_NO_FINGER) {
                    Log.e(TAG, "两次采集指纹间隔");
                    if (mDeviceOpened == false) {
                        Log.e(TAG, "设备未打开!");
                        return -1;
                    }
                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {
                        // TODO: handle exception
                        Log.i(TAG, e.toString());
                    }
                    //publishProgress("Please Remove finger");
                    publishProgress(getString(R.string.finger_leave));
                }


                while (msyUsbKey.SyGetImage() == PS_NO_FINGER) {
                    //Log.e(TAG, "两次采集指纹");

                    if (mDeviceOpened == false) {
                        Log.e(TAG, "设备未打开!");
                        return -1;
                    }

                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    //publishProgress("PS_NO_FINGER");
                    if (cnt == 1) {
                        publishProgress(getString(R.string.finger_press));
                    } else {
                        publishProgress(getString(R.string.finger_press_again));
                    }
                }

                if ((ret = msyUsbKey.SyUpImage(fingerBuf)) != 0) {
                    publishProgress("Sy UpImage:" + ret);
                    Log.e(TAG, "Sy UpImage:" + ret);
                    continue;
                }

              /*  while (msyUsbKey.PSGenChar(-1, cnt) != PS_NO_FINGER) {
                    Log.e(TAG, "获取指纹特征失败");
                }
*/

                if ((ret = WriteBmp(fingerBuf)) != 0) {
                    publishProgress("Sy UpImage:" + ret);
                    Log.e(TAG, "Sy UpImage:" + ret);

                    continue;
                }
                publishProgress("OK");


                if ((ret = msyUsbKey.SyEnroll(cnt, fingerCnt)) != PS_OK) {
                    // publishProgress("Sy Enroll:" + ret);
                    publishProgress("采集指纹失败");
                    Log.e(TAG, "Sy Enroll:" + ret);
                    //

                    return -1;
                } else {
                    Progress += 50;
                    bar.setProgress(Progress);
                    //publishProgress("Sy Enroll" + cnt);
                    publishProgress(getString(R.string.finger_collect) + cnt + getString(R.string.finger));
                    Log.e(TAG, "正在采集第 " + cnt + " 指纹");
                }

                if (cnt >= 2) {
                    publishProgress("end");
                    publishProgress(getString(R.string.collect_end));


                    int i = msyUsbKey.SyUpChar(-1, g_TempData);

                    if (i == 0) {
                        Log.e(TAG, "?: " + DataUtils.bytesToHexString(g_TempData));
                        publishProgress("char");
                    }

                    return 0;
                }
                cnt++;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            // globalControl = false;
            if (0 == result) {
                if (fingerCnt > 256) {
                    Log.i(TAG, "fingerCnt > 256");
                    return;
                }
                fingerCnt++;
                Log.i(TAG, "Enroll Success fingerCnt = " + fingerCnt);
                bar.setEnabled(false);
                return;
            } else {
                bar.setProgress(0);
                Log.i(TAG, "Enroll Error " + result);
                return;
            }
        }

        @Override
        protected void onPreExecute() {

            //在执行异步任务之前的时候执行,在UI Thread当中执行的
            Log.i(TAG, "Please press finger...");
            bar.setEnabled(true);
            return;
        }

        @Override
        @SuppressWarnings("null")
        protected void onProgressUpdate(String... values) {

            //当我们的异步任务执行完之后，就会将结果返回给这个方法，这个方法也是在UI Thread当中调用的，我们可以将返回的结果显示在UI控件上

            if (values[0].equals("OK")) {
                String localName = "finger.bmp";
                FileInputStream localStream = null;
                try {
                    localStream = openFileInput(localName);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    try {
                        localStream.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    return;
                }
                Bitmap bitmap = BitmapFactory.decodeStream(localStream);
                fingerView.setImageBitmap(bitmap);
                try {
                    localStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return;
            }
            if (values[0].equals("采集指纹失败")) {
                btnFingerState(true);
            }
            if (values[0].equals("end")) {
                btnEnble(true);
            } else {
                Log.e(TAG, values[0]);
                tvInfo.setText(values[0]);
            }
            if (values[0].equals("char")) {
                tvInfo.setText("");

                tvChar.setText(DataUtils.bytesToHexString(g_TempData));
            }

            return;
        }
    }

    /**
     * 搜索指纹
     */
    private class SearchAsyncTask extends AsyncTask<Integer, String, Integer> {
        @SuppressWarnings("unused")
        private int ret;

        @Override
        protected Integer doInBackground(Integer... params) {
            int[] fingerId = new int[1];
            while (true) {
                if (mDeviceOpened == false) {
                    return -1;
                }
                while (msyUsbKey.SyGetImage() == PS_NO_FINGER) {
                    if (mDeviceOpened == false) {
                        return -1;
                    }
                    publishProgress("");

                    try {
                        Thread.sleep(400);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    publishProgress(getResources().getString(R.string.finger_press));
                    try {
                        Thread.sleep(400);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
                if ((ret = msyUsbKey.SyUpImage(fingerBuf)) != 0) {
                    Log.e(TAG, "上传图片失败:" + ret);
                    continue;
                }
                if ((ret = WriteBmp(fingerBuf)) != 0) {
                    publishProgress("写入图片失败:" + ret);
                    continue;
                }
                publishProgress("OK");

                if (msyUsbKey.SySearch(fingerId) != PS_OK) {
                    //publishProgress("Search Error,Please Try again!");
                    publishProgress("搜索指纹失败,请改变手指按压手势!");
                    continue;
                } else {
                    int fingerid = fingerId[0] + 1;
                    //publishProgress("SySearch,ID===>" + fingerid);
                    publishProgress(getString(R.string.finger_search) + fingerid);
                    publishProgress("end");
                    return 0;
                }
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            return;
        }

        @Override
        protected void onPreExecute() {
            Log.e(TAG, "Start Seach, Please press finger");
            return;
        }

        @Override
        @SuppressWarnings("null")
        protected void onProgressUpdate(String... values) {
            if (values[0].equals("OK")) {
                String localName = "finger.bmp";
                FileInputStream localStream = null;
                try {
                    localStream = openFileInput(localName);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    try {
                        localStream.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    return;
                }
                Bitmap bitmap = BitmapFactory.decodeStream(localStream);
                fingerView.setImageBitmap(bitmap);
                try {
                    localStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return;
            }

            if (values[0].equals("end")) {
                btnEnble(true);
            } else {
                Log.e(TAG, values[0]);
                tvInfo.setText(values[0]);
            }
            return;
        }
    }


}
