package com.cw.demo.fingerprint.gaa;


//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.hardware.usb.UsbDevice;
//import android.hardware.usb.UsbManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.widget.AppCompatTextView;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.cw.demo.MyApplication;
//import com.cw.demo.R;
//import com.cw.serialportsdk.USB.USBFingerManager;
//import com.fm.bio.ID_Fpr;
//
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//
//public class FpGAAActivity extends Activity implements OnClickListener {
public class FpGAAActivity{

}
//
//    private static final String TAG = "CwGAAActivity";
//    boolean isExit;
//    float fpThreshold;
//    //指纹特征
//    byte[] m_byFeature = new byte[ID_Fpr.LIVESCAN_FEATURE_SIZE];
//    //length shall refer to the state standard GA1011/1012
//    //指纹库特征
//    byte[] m_byFeatures = new byte[ID_Fpr.LIVESCAN_FEATURE_SIZE * 1000];
//    int nbyFeature = 0;
//    byte[] byIHVKey = new byte[]{(byte) 0xA5, (byte) 0xA5, (byte) 0xA5, (byte) 0xA5, (byte) 0xA5, (byte) 0xA5, (byte) 0xA5, (byte) 0xA5,
//            (byte) 0xA5, (byte) 0xA5, (byte) 0xA5, (byte) 0xA5, (byte) 0xA5, (byte) 0xA5, (byte) 0xA5, (byte) 0xA5};
//    byte[] byIHVData = new byte[]{(byte) 0x3a, (byte) 0xd7, (byte) 0x7b, (byte) 0xb4, (byte) 0x0d, (byte) 0x7a, (byte) 0x36, (byte) 0x60,
//            (byte) 0xa8, (byte) 0x9e, (byte) 0xca, (byte) 0xf3, (byte) 0x24, (byte) 0x66, (byte) 0xef, (byte) 0x97};
//    private TextView msgView;
//    private ImageView fpImageView;
//    private Button btn_version;
//    private Button btn_devversion;
//    private Button btn_begin;
//    private Button btn_getfpbmp;
//    private Button btn_feature;
//    private Button btn_match;
//    private Button btn_search;
//    private Button btn_end;
//    private Button btn_auth;
//    private AppCompatTextView tv_Char;
//    private ID_Fpr mLiveScan = null;
//    public Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == ID_Fpr.LIVESCAN_MSG_KEY) {
//                switch (msg.arg1) {
//                    case ID_Fpr.LIVESCAN_MSG_IN:
//                    case ID_Fpr.LIVESCAN_MSG_PERMISSION:
//                        if (mLiveScan != null) {
//                            fpInit();
//                        }
//                        break;
//                    case ID_Fpr.LIVESCAN_MSG_OUT:
//                        if (mLiveScan != null) {
//                            mLiveScan.LIVESCAN_Close();
//                        }
//                        break;
//                }
//            }
//            if (msg.arg1 == 1) {
//                msgView.setText(msg.obj.toString());
//            } else if (msg.arg1 == 2) {
//                fpImageView.setImageBitmap((Bitmap) msg.obj);
//            } else {
//                isExit = false;
//            }
//        }
//    };
//
//    private static byte[] encrypt(byte[] key, byte[] data) throws Exception {
//        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//        byte[] encrypted = cipher.doFinal(data);
//        return encrypted;
//    }
//
//    private static String byteToString(byte[] input) {
//        String ret = "";
//        for (int i = 0; i < input.length; i++) {
//            String hex = Integer.toHexString(input[i] & 0xFF);
//            if (hex.length() == 1) {
//                hex = '0' + hex;
//            }
//            ret += hex.toUpperCase();
//        }
//        return ret;
//    }
//
//    @SuppressLint("HandlerLeak")
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.i(TAG, "------------onCreate--------------");
//
//        setContentView(R.layout.activity_fp_gaa);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//        USBFingerManager.getInstance(this).setDelayMs(500);
//        initview();
//        //设备系统已做默认授权的处理
//        //checkPermission();
//
//    }
//
//    private void open() {
//        MyApplication.getApp().showProgressDialog(this, getString(R.string.fp_usb_init));
//
//
//
//        USBFingerManager.getInstance(this).openUSB(new USBFingerManager.OnUSBFingerListener() {
//            @Override
//            public void onOpenUSBFingerSuccess(String device, UsbManager usbManager, UsbDevice usbDevice) {
//
//                if (device.equals(USBFingerManager.GAA_DEVICE)) {
//                    MyApplication.getApp().cancleProgressDialog();
//
//                    if (mLiveScan != null) {
//                        return;
//                    }
//                    mLiveScan = new ID_Fpr(FpGAAActivity.this, handler);
//
//                } else {
//                    Toast.makeText(FpGAAActivity.this, "开发包和指纹模块不一致! 请联系商务", Toast.LENGTH_SHORT).show();
//                    btnsetEnabledALL(false);
//                }
//            }
//
//            @Override
//            public void onOpenUSBFingerFailure(String error,int errorCode) {
//                Log.e(TAG, error);
//                btnsetEnabledALL(false);
//                MyApplication.getApp().cancleProgressDialog();
//                Toast.makeText(FpGAAActivity.this, error, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
//
//    private void close() {
//        btnsetEnabledEnd(false);
//        if (mLiveScan != null) {
//            mLiveScan.LIVESCAN_Close();
//        }
//        USBFingerManager.getInstance(this).closeUSB();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.i(TAG, "------------onStart--------------");
//        open();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.i(TAG, "------------onResume--------------");
//
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.i(TAG, "------------onRestart--------------");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.i(TAG, "------------onStop--------------");
//        close();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.i(TAG, "------------onDestroy--------------");
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                Log.i(TAG, "点击了返回键");
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle(R.string.general_tips);
//                builder.setMessage(R.string.general_exit);
//
//                //设置确定按钮
//                builder.setNegativeButton(R.string.general_yes, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                });
//                //设置取消按钮
//                builder.setPositiveButton(R.string.general_no, null);
//                //显示提示框
//                builder.show();
//                break;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    public void initview() {
//        msgView = findViewById(R.id.edtv1);
//        fpImageView = findViewById(R.id.image_1);
//
//
//        btn_version = findViewById(R.id.btn_version);
//        btn_devversion = findViewById(R.id.btn_devversion);
//        btn_begin = findViewById(R.id.btn_begin);
//        btn_getfpbmp = findViewById(R.id.btn_getfpbmp);
//        btn_feature = findViewById(R.id.btn_feature);
//        btn_match = findViewById(R.id.btn_match);
//        btn_search = findViewById(R.id.btn_search);
//        btn_end = findViewById(R.id.btn_end);
//        btn_auth = findViewById(R.id.btn_auth);
//
//        tv_Char = findViewById(R.id.tv_char);
//
//        btn_begin.setOnClickListener(this);
//        btn_getfpbmp.setOnClickListener(this);
//        btn_feature.setOnClickListener(this);
//        btn_end.setOnClickListener(this);
//        btn_version.setOnClickListener(this);
//        btn_devversion.setOnClickListener(this);
//        btn_match.setOnClickListener(this);
//        btn_search.setOnClickListener(this);
//        btn_auth.setOnClickListener(this);
//
//        btn_begin.setEnabled(true);
//        btn_version.setEnabled(true);
//        btn_devversion.setEnabled(false);
//        btn_getfpbmp.setEnabled(false);
//        btn_feature.setEnabled(false);
//        btn_end.setEnabled(true);
//        btn_match.setEnabled(false);
//
//        btn_search.setEnabled(false);
//        btn_auth.setEnabled(false);
//
//    }
//
//    public void regFingerprint() {
//
//        //LIVESCAN_BeginCapture
//
//        SendMesg("Place Your Finger");
//
//        new Thread() {
//
//            int iRet = 0;
//            byte[] bScore = new byte[1];
//            int i = 0;
//            String Msg = "";
//            byte[] fpRaw = new byte[ID_Fpr.LIVESCAN_IMAGE_WIDTH * ID_Fpr.LIVESCAN_IMAGE_HEIGHT];
//
//            @Override
//            public void run() {
//
//                while (!Thread.interrupted()) {
//
//                    try {
//
//                        iRet = mLiveScan.LIVESCAN_GetFPRawData(fpRaw);
//
//                        if (iRet != ID_Fpr.LIVESCAN_SUCCESS) {
//                            Msg = String.format("GetFPRawData:%d_%s", iRet, mLiveScan.LIVESCAN_GetErrorInfo(iRet));
//                            SendMesg(Msg);
//                            break;
//                        } else {
//                            SendBmp(mLiveScan.LIVESCAN_FPRawDataToBmp(fpRaw));
//                        }
//                        iRet = mLiveScan.LIVESCAN_GetQualityScore(fpRaw, bScore);
//                        Msg = String.format("GetQualityScore:%d _%d", iRet, bScore[0] & 0xff);
//                        SendMesg(Msg);
//
//                        //experience value LIVESCAN_IMAGE_SCORE_THRESHOLD
//                        if ((bScore[0] & 0xff) >= ID_Fpr.LIVESCAN_IMAGE_SCORE_THRESHOLD) {
//                            //byte cScannerType=0x17
//                            //byte cFingerCode = 11~20 97~99 shall refer to the state standard GB5974.1-86
//                            iRet = mLiveScan.LIVESCAN_FeatureExtract(m_byFeature);
//                            if (iRet >= ID_Fpr.LIVESCAN_SUCCESS) {
//                                System.arraycopy(m_byFeature, 0, m_byFeatures, nbyFeature * ID_Fpr.LIVESCAN_FEATURE_SIZE, ID_Fpr.LIVESCAN_FEATURE_SIZE);
//                                Log.i(TAG, "---------------------------------:" + byteToString(m_byFeature));
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        tv_Char.setText(byteToString(m_byFeature));
//                                    }
//                                });
//                                Msg = String.format("LIVESCAN_FeatureExtract ID:%d ", nbyFeature);
//                                nbyFeature++;
//                            } else {
//                                Msg = String.format("FeatureExtract:%d", iRet);
//                            }
//                            SendMesg(Msg);
//
//                            break;
//                        } else {
//                            i++;
//                            Thread.sleep(200);
//                            if (i % 2 == 0) {
//                                SendMesg("Lift and Place Your Finger.");
//                            }
//                            if (i % 2 == 1) {
//                                SendMesg("Lift and Place Your Finger...");
//                            }
//                        }
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        btnsetEnabled(true);
//                    }
//                });
//
//            }
//
//        }.start();
//    }
//
//    private void matchFingerprint() {
//
//        SendMesg("Place Your Finger");
//        new Thread() {
//            int iRet = 0;
//            byte[] bScore = new byte[1];
//            int i = 0;
//            String Msg = "";
//            byte[] fpRaw = new byte[ID_Fpr.LIVESCAN_IMAGE_WIDTH * ID_Fpr.LIVESCAN_IMAGE_HEIGHT];
//            //shall refer to the state standard GA1011/1012
//            byte[] fpFtp = new byte[512];
//
//            @Override
//            public void run() {
//                while (!Thread.interrupted()) {
//                    try {
//                        iRet = mLiveScan.LIVESCAN_GetFPRawData(fpRaw);
//                        if (iRet != ID_Fpr.LIVESCAN_SUCCESS) {
//                            Msg = String.format("GetFPRawData:%d_%s", iRet, mLiveScan.LIVESCAN_GetErrorInfo(iRet));
//                            SendMesg(Msg);
//                            break;
//                        } else {
//                            SendBmp(mLiveScan.LIVESCAN_FPRawDataToBmp(fpRaw));
//                        }
//                        iRet = mLiveScan.LIVESCAN_GetQualityScore(fpRaw, bScore);
//                        Msg = String.format("GetQualityScore:%d _%d", iRet, bScore[0] & 0xff);
//                        SendMesg(Msg);
//
//                        //experience value 50
//                        if ((bScore[0] & 0xff) >= ID_Fpr.LIVESCAN_IMAGE_SCORE_THRESHOLD) {
//                            //byte cScannerType=0x17
//                            //byte cFingerCode = 11~20 97~99 shall refer to the state standard GB5974.1-86
//                            iRet = mLiveScan.LIVESCAN_FeatureExtract(fpFtp);
//                            Msg = String.format("FeatureExtract:%d", iRet);
//                            SendMesg(Msg);
//                            if (iRet == ID_Fpr.LIVESCAN_SUCCESS) {
//                                float[] fs = new float[1];
//                                iRet = mLiveScan.LIVESCAN_FeatureMatch(m_byFeature, fpFtp, fs);
//                                Msg = String.format("FP_FeatureMatch:%f :%s", fs[0], (fs[0] >= fpThreshold) ? "y" : "n");
//                                SendMesg(Msg);
//                            }
//                            break;
//                        } else {
//                            i++;
//                            Thread.sleep(200);
//                            if (i % 2 == 0) {
//                                SendMesg("Lift and Place Your Finger.");
//                            }
//                            if (i % 2 == 1) {
//                                SendMesg("Lift and Place Your Finger...");
//                            }
//                        }
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        btnsetEnabled(true);
//                    }
//                });
//
//            }
//
//        }.start();
//    }
//
//    private void searchFingerprint() {
//
//        //LIVESCAN_BeginCapture
//
//        SendMesg("Place Your Finger");
//
//        new Thread() {
//            int iRet = 0;
//            byte[] bScore = new byte[1];
//            int i = 0;
//            String Msg = "";
//            byte[] fpRaw = new byte[ID_Fpr.LIVESCAN_IMAGE_WIDTH * ID_Fpr.LIVESCAN_IMAGE_HEIGHT];
//            //shall refer to the state standard GA1011/1012
//            byte[] fpFtp = new byte[512];
//
//            @Override
//            public void run() {
//                while (!Thread.interrupted()) {
//
//                    try {
//
//                        iRet = mLiveScan.LIVESCAN_GetFPRawData(fpRaw);
//                        if (iRet != ID_Fpr.LIVESCAN_SUCCESS) {
//                            Msg = String.format("GetFPRawData:%d_%s", iRet, mLiveScan.LIVESCAN_GetErrorInfo(iRet));
//                            SendMesg(Msg);
//                            break;
//                        } else {
//                            SendBmp(mLiveScan.LIVESCAN_FPRawDataToBmp(fpRaw));
//                        }
//                        iRet = mLiveScan.LIVESCAN_GetQualityScore(fpRaw, bScore);
//                        Msg = String.format("GetQualityScore:%d _%d", iRet, bScore[0] & 0xff);
//                        SendMesg(Msg);
//
//                        //experience value 50
//                        if ((bScore[0] & 0xff) >= ID_Fpr.LIVESCAN_IMAGE_SCORE_THRESHOLD) {
//                            //byte cScannerType=0x17
//                            //byte cFingerCode = 11~20 97~99 shall refer to the state standard GB5974.1-86
//                            iRet = mLiveScan.LIVESCAN_FeatureExtract(fpFtp);
//                            Msg = String.format("FeatureExtract:%d", iRet);
//                            SendMesg(Msg);
//                            if (iRet == ID_Fpr.LIVESCAN_SUCCESS) {
//                                float[] fs = new float[1];
//                                int[] id = new int[1];
//                                iRet = mLiveScan.LIVESCAN_FeatureSearch(fpFtp, m_byFeatures, nbyFeature, id, fs);
//
//                                Msg = String.format("FP_FeatureSearch:id:%d %f :%s", id[0], fs[0], (fs[0] >= fpThreshold) ? "y" : "n");
//                                SendMesg(Msg);
//
//                            }
//                            break;
//                        } else {
//                            i++;
//                            Thread.sleep(200);
//                            if (i % 2 == 0) {
//                                SendMesg("Lift and Place Your Finger.");
//                            }
//                            if (i % 2 == 1) {
//                                SendMesg("Lift and Place Your Finger...");
//                            }
//                        }
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                //LIVESCAN_EndCapture
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        btnsetEnabled(true);
//                    }
//                });
//
//            }
//
//        }.start();
//    }
//
//    private boolean memcmp(byte[] data1, byte[] data2, int len) {
//        if (data1 == null && data2 == null) {
//            return true;
//        }
//        if (data1 == null || data2 == null) {
//            return false;
//        }
//        if (data1 == data2) {
//            return true;
//        }
//
//        boolean bEquals = true;
//        int i;
//        for (i = 0; i < data1.length && i < data2.length && i < len; i++) {
//            if (data1[i] != data2[i]) {
//                bEquals = false;
//                break;
//            }
//        }
//
//        return bEquals;
//    }
//
//    @Override
//    public void onClick(View v) {
//        int iRet = 0;
//        String Msg;
//        switch (v.getId()) {
//
//            case R.id.btn_version:
//
//                if (mLiveScan == null) {
//                    return;
//                }
//
//                Msg = String.format("SdkVersion_%s\r\n", mLiveScan.LIVESCAN_GetSdkVersion());
//                //Toast.makeText(getApplicationContext(), Msg, Toast.LENGTH_SHORT).show();
//                Log(Msg);
//                break;
//            case R.id.btn_devversion:
//                //ID_FprCap
//                byte[] bySN = new byte[32];
//                String sv = mLiveScan.LIVESCAN_GetDevVersion();
//
//                mLiveScan.LIVESCAN_GetDevSN(bySN);
//                String sn = byteToString(bySN);
//
//                Msg = String.format("DevVersion:%s\r\nSN:%s", sv, sn);
//
//                //Toast.makeText(getApplicationContext(), Msg, Toast.LENGTH_SHORT).show();
//                Log(Msg);
//
//                break;
//            case R.id.btn_begin:
//                if (mLiveScan == null) {
//                    return;
//                }
//                //LIVESCAN_Init
//                fpInit();
//
//                break;
//            case R.id.btn_auth:
//                //LIVESCAN_Init
//                byte[] plaintext1 = new byte[16];
//                byte[] plaintext2 = new byte[16];
//                System.arraycopy(byIHVData, 0, plaintext1, 0, 16);
//                System.arraycopy(byIHVData, 0, plaintext2, 0, 16);
//                if (mLiveScan.LIVESCAN_Encrypt(plaintext1) != ID_Fpr.LIVESCAN_SUCCESS) {
//                    Msg = String.format("LIVESCAN_Encrypt:%d_%s\r\n", iRet, mLiveScan.LIVESCAN_GetErrorInfo(iRet));
//                    //Toast.makeText(getApplicationContext(), Msg, Toast.LENGTH_SHORT).show();
//                    Log(Msg);
//
//                    return;
//                }
//                try {
//                    byte[] cryptText = encrypt(byIHVKey, plaintext2);
//
//                    if (memcmp(cryptText, plaintext1, 16) == true) {
//                        Msg = String.format("LIVESCAN_Encrypt ok & Auth Success");
//                        //Toast.makeText(getApplicationContext(), Msg,Toast.LENGTH_SHORT).show();
//                        Log(Msg);
//
//                    } else {
//                        Msg = String.format("LIVESCAN_Encrypt ok & Auth Failed");
//                        //Toast.makeText(getApplicationContext(), Msg, Toast.LENGTH_SHORT).show();
//                        Log(Msg);
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            case R.id.btn_getfpbmp:
//
//                byte[] fpBmp = new byte[ID_Fpr.LIVESCAN_IMAGE_WIDTH * ID_Fpr.LIVESCAN_IMAGE_HEIGHT + ID_Fpr.LIVESCAN_IMAGE_HEADER];
//                iRet = mLiveScan.LIVESCAN_GetFPBmpData(fpBmp);
//                if (iRet == ID_Fpr.LIVESCAN_SUCCESS) {
//                    SendBmp(BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.length));
//                }
//                Msg = String.format("LIVESCAN_GetFPBmpData:%d_%s\r\n", iRet, mLiveScan.LIVESCAN_GetErrorInfo(iRet));
//                msgView.setText(Msg);
//
//                break;
//            case R.id.btn_feature:
//                btnsetEnabled(false);
//                regFingerprint();
//                break;
//            case R.id.btn_match:
//                btnsetEnabled(false);
//                matchFingerprint();
//                break;
//            case R.id.btn_search:
//                btnsetEnabled(false);
//                searchFingerprint();
//                break;
//            case R.id.btn_end:
//                if (mLiveScan == null) {
//                    return;
//                }
//                //LIVESCAN_Init
//                iRet = mLiveScan.LIVESCAN_Close();
//                Msg = String.format("LIVESCAN_Close:%d_%s\r\n", iRet, mLiveScan.LIVESCAN_GetErrorInfo(iRet));
//                //Toast.makeText(getApplicationContext(), Msg, Toast.LENGTH_SHORT).show();
//                Log(Msg);
//
//                btnsetEnabledEnd(false);
//                break;
//        }
//    }
//
//
//    private void fpInit() {
//        int iRet;
//        String Msg;
//        iRet = mLiveScan.LIVESCAN_Init();
//        Msg = String.format("LIVESCAN_Init:%d_%s\r\n", iRet, mLiveScan.LIVESCAN_GetErrorInfo(iRet));
//        //Toast.makeText(getApplicationContext(), Msg, Toast.LENGTH_SHORT).show();
//        Log(Msg);
//
//        if (iRet == ID_Fpr.LIVESCAN_SUCCESS) {
//            btn_match.setEnabled(true);
//            btn_auth.setEnabled(true);
//            btn_search.setEnabled(true);
//            btn_devversion.setEnabled(true);
//            btn_getfpbmp.setEnabled(true);
//            btn_feature.setEnabled(true);
//            fpThreshold = mLiveScan.LIVESCAN_GetMatchThreshold();
//        }
//    }
//
//
//    private void btnsetEnabled(Boolean b) {
//        btn_begin.setEnabled(b);
//        btn_version.setEnabled(b);
//        btn_match.setEnabled(b);
//        btn_search.setEnabled(b);
//        btn_devversion.setEnabled(b);
//        btn_getfpbmp.setEnabled(b);
//        btn_feature.setEnabled(b);
//        btn_end.setEnabled(b);
//    }
//
//    private void SendMesg(String Msg) {
//        Message msg = handler.obtainMessage();
//        msg.arg1 = 1;
//        msg.obj = Msg;
//        handler.sendMessage(msg);
//    }
//
//    private void SendBmp(Bitmap bmp) {
//        Message msg = handler.obtainMessage();
//        msg.arg1 = 2;
//        msg.obj = bmp;
//        handler.sendMessage(msg);
//    }
//
//    private void btnsetEnabledEnd(Boolean b) {
//        btn_begin.setEnabled(!b);
//        btn_version.setEnabled(!b);
//
//        btn_auth.setEnabled(b);
//        btn_match.setEnabled(b);
//        btn_search.setEnabled(b);
//        btn_devversion.setEnabled(b);
//        btn_getfpbmp.setEnabled(b);
//        btn_feature.setEnabled(b);
//        btn_end.setEnabled(!b);
//    }
//
//    private void btnsetEnabledALL(Boolean b) {
//        btn_begin.setEnabled(b);
//        btn_version.setEnabled(b);
//        btn_auth.setEnabled(b);
//        btn_match.setEnabled(b);
//        btn_search.setEnabled(b);
//        btn_devversion.setEnabled(b);
//        btn_getfpbmp.setEnabled(b);
//        btn_feature.setEnabled(b);
//        btn_end.setEnabled(b);
//
//    }
//
//    private void Log(String msg) {
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//        Log.i(TAG, "------------------------------" + msg);
//    }
//
//}
