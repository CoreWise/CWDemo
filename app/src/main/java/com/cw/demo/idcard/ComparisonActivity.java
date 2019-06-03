package com.cw.demo.idcard;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cw.demo.MyApplication;
import com.cw.demo.R;
import com.cw.demo.fingerprint.gaa.FpGAAActivity;
import com.cw.fpgaasdk.USBFingerManager;
import com.cw.idcardsdk.AsyncParseSFZ;
import com.cw.idcardsdk.ParseSFZAPI;
import com.cw.serialportsdk.cw;
import com.cw.serialportsdk.utils.DataUtils;
import com.fm.bio.ID_Fpr;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：李阳
 * 时间：2019/5/28
 * 描述：
 */
public class ComparisonActivity extends AppCompatActivity {


    private static final String TAG = "ComparisonActivity";

    @BindView(R.id.tv_sfz_modle)
    TextView tvSfzModle;
    @BindView(R.id.scv)
    ScrollView scv;
    @BindView(R.id.read_sfz)
    Button readSfz;

    @BindView(R.id.verial)
    Button verial;

    String liyang = "43018c12010b5400000000000000000000000021019a90e600ffffffffffffa21a54fec3239dfc4231c8fc6343bffc9b44a6fc3c4e14fcc154a3fc255ed4fcdd6749fe9f7454fe7582bafcb48fa0fc7a9564fe87a3b1fc23a927fc72b5cafc45bfe4fc8dc7c2fc0cd0eefc75d7e9fc21daeffc95e338fec7e923fe41edfdfc63fb0dfedf011fff98116bfd28150bfff62e07ff133912ffd84debfdb553cffd7d574fff00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000c5000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003843018c1201105600000000000000000000000027019abac600ffffffffffff7419e0fc8b1f1ffcc420bffcd02b60fea63814fc673a34fce74aacfc4651e9fc7e6138fc896e2afcda6f59feab7116fc988a1bfcbc8a00fcd88a50fe5d95ddfc429c38fc1f9eebfc4da333fc5ba4eefccfaa49fe58bb3ffc88c437fcbfc903fe56ccfafcdccb1cfec8d00dfe26d5ecfc49e646fc41e8f9fcefec0ffe6aeffafc54fa3efc32063cfd680645fd0e342efd873b34fdde41ecfd324c26fd00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000c90000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000082";

    //指纹特征
    byte[] m_byFeature = new byte[ID_Fpr.LIVESCAN_FEATURE_SIZE];


    //length shall refer to the state standard GA1011/1012
    //指纹库特征
    byte[] m_byFeatures = new byte[ID_Fpr.LIVESCAN_FEATURE_SIZE * 1000];
    int nbyFeature = 0;
    float fpThreshold;
    private AsyncParseSFZ asyncParseSFZ;
    private ID_Fpr mLiveScan = null;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ID_Fpr.LIVESCAN_MSG_KEY) {
                switch (msg.arg1) {
                    case ID_Fpr.LIVESCAN_MSG_IN:
                    case ID_Fpr.LIVESCAN_MSG_PERMISSION:
                        if (mLiveScan != null) {
                            fpInit();
                        }
                        break;
                    case ID_Fpr.LIVESCAN_MSG_OUT:
                        if (mLiveScan != null) {
                            mLiveScan.LIVESCAN_Close();
                        }
                        break;
                }
            }
        }
    };

    private static String byteToString(byte[] input) {
        String ret = "";
        for (int i = 0; i < input.length; i++) {
            String hex = Integer.toHexString(input[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_idcard_comparison);
        ButterKnife.bind(this);

        asyncParseSFZ = new AsyncParseSFZ(getMainLooper(), this);

        asyncParseSFZ.setOnReadSFZListener(new AsyncParseSFZ.OnReadSFZListener() {
            @Override
            public void onReadSuccess(ParseSFZAPI.People people) {
                tvSfzModle.setText(people.toString());
            }

            @Override
            public void onReadFail(int i) {
                Toast.makeText(ComparisonActivity.this, "读身份证错误码 " + i, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        asyncParseSFZ.openIDCardSerialPort(cw.getDeviceModel());

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        asyncParseSFZ.closeIDCardSerialPort(cw.getDeviceModel());
        super.onPause();
    }


    @OnClick({R.id.read_sfz, R.id.verial})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.read_sfz:

                asyncParseSFZ.readSFZ();

                break;
            case R.id.verial:

                idcardIdentify(DataUtils.hexStringTobyte(liyang), m_byFeature, ID_Fpr.LIVESCAN_IMAGE_SCORE_THRESHOLD);

                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "------------onStart--------------");
        open();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "------------onStop--------------");
        close();
    }

    private void open() {
        MyApplication.getApp().showProgressDialog(this, getString(R.string.fp_usb_init));
        USBFingerManager.getInstance(this).openUSB(new USBFingerManager.OnUSBFingerListener() {
            @Override
            public void onOpenUSBFingerSuccess(String device) {

                if (device.equals(USBFingerManager.BYD_BIG_DEVICE2)) {
                    MyApplication.getApp().cancleProgressDialog();

                    if (mLiveScan != null) {
                        return;
                    }
                    mLiveScan = new ID_Fpr(ComparisonActivity.this, handler);

                } else {
                    Toast.makeText(ComparisonActivity.this, "开发包和指纹模块不一致! 请联系商务", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onOpenUSBFingerFailure(String error) {
                Log.e(TAG, error);
                MyApplication.getApp().cancleProgressDialog();
                Toast.makeText(ComparisonActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void close() {
        if (mLiveScan != null) {
            mLiveScan.LIVESCAN_Close();
        }
        USBFingerManager.getInstance(this).closeUSB();
    }

    private void fpInit() {
        int iRet;

        iRet = mLiveScan.LIVESCAN_Init();

        if (iRet == ID_Fpr.LIVESCAN_SUCCESS) {
            fpThreshold = mLiveScan.LIVESCAN_GetMatchThreshold();
        }
    }

    public void regFingerprint() {

        //LIVESCAN_BeginCapture


        new Thread() {

            int iRet = 0;
            byte[] bScore = new byte[1];
            int i = 0;
            String Msg = "";
            byte[] fpRaw = new byte[ID_Fpr.LIVESCAN_IMAGE_WIDTH * ID_Fpr.LIVESCAN_IMAGE_HEIGHT];

            @Override
            public void run() {

                while (!Thread.interrupted()) {

                    try {

                        iRet = mLiveScan.LIVESCAN_GetFPRawData(fpRaw);

                        if (iRet != ID_Fpr.LIVESCAN_SUCCESS) {
                            Msg = String.format("GetFPRawData:%d_%s", iRet, mLiveScan.LIVESCAN_GetErrorInfo(iRet));
                            break;
                        } else {
                        }
                        iRet = mLiveScan.LIVESCAN_GetQualityScore(fpRaw, bScore);
                        Msg = String.format("GetQualityScore:%d _%d", iRet, bScore[0] & 0xff);

                        //experience value LIVESCAN_IMAGE_SCORE_THRESHOLD
                        if ((bScore[0] & 0xff) >= ID_Fpr.LIVESCAN_IMAGE_SCORE_THRESHOLD) {
                            //byte cScannerType=0x17
                            //byte cFingerCode = 11~20 97~99 shall refer to the state standard GB5974.1-86
                            iRet = mLiveScan.LIVESCAN_FeatureExtract(m_byFeature);
                            if (iRet >= ID_Fpr.LIVESCAN_SUCCESS) {
                                System.arraycopy(m_byFeature, 0, m_byFeatures, nbyFeature * ID_Fpr.LIVESCAN_FEATURE_SIZE, ID_Fpr.LIVESCAN_FEATURE_SIZE);
                                Log.i(TAG, "---------------------------------:" + byteToString(m_byFeature));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvSfzModle.setText(byteToString(m_byFeature));
                                    }
                                });
                                Msg = String.format("LIVESCAN_FeatureExtract ID:%d ", nbyFeature);
                                nbyFeature++;
                            } else {
                                Msg = String.format("FeatureExtract:%d", iRet);
                            }

                            break;
                        } else {
                            i++;
                            Thread.sleep(200);
                            if (i % 2 == 0) {
                            }
                            if (i % 2 == 1) {
                            }
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    /**
     * 比对用户提供的二代身份证指纹特征集合和设备取到的设备取到的设备取到的指纹特征是否有匹配
     * <p>
     * 采用 LIVESCAN_FeatureMatch
     */
    private boolean idcardIdentify(byte[] fpIDCard, byte[] fpFinger, float fpThreshold) {

        /*float[] fs = new float[1];
        int iRet = mLiveScan.LIVESCAN_FeatureMatch(fpIDCard, fpFinger, fs);
        if (iRet != 0) {
            String Msg = String.format("LIVESCAN_FeatureMatch:%d", iRet);
            Log.e(TAG, Msg);

            Toast.makeText(this, "LIVESCAN_FeatureMatch失败！" + Msg, Toast.LENGTH_SHORT).show();

            return false;
        }
        String Msg = String.format("FP_FeatureMatch:%f :%s", fs[0], (fs[0] >= fpThreshold) ? "y" : "n");
        if (fs[0] >= fpThreshold) {
            Log.i(TAG, Msg);

            Toast.makeText(this, "比对成功！", Toast.LENGTH_SHORT).show();

            return true;
        } else {
            Log.e(TAG, Msg);
            Toast.makeText(this, "比对失败！", Toast.LENGTH_SHORT).show();

            return false;
        }
*/


        byte[] fpRaw = new byte[ID_Fpr.LIVESCAN_IMAGE_WIDTH * ID_Fpr.LIVESCAN_IMAGE_HEIGHT];
        byte[] bScore = new byte[1];
        byte[] fpFtp = new byte[512];


        int iRet = mLiveScan.LIVESCAN_GetFPRawData(fpRaw);
        String Msg;
        if (iRet != ID_Fpr.LIVESCAN_SUCCESS) {
            Msg = String.format("GetFPRawData:%d_%s", iRet, mLiveScan.LIVESCAN_GetErrorInfo(iRet));
            return false;
        } else {

        }

        iRet = mLiveScan.LIVESCAN_GetQualityScore(fpRaw, bScore);

        Msg = String.format("GetQualityScore:%d _%d", iRet, bScore[0] & 0xff);

        //experience value 50
        if ((bScore[0] & 0xff) >= ID_Fpr.LIVESCAN_IMAGE_SCORE_THRESHOLD) {
            //byte cScannerType=0x17
            //byte cFingerCode = 11~20 97~99 shall refer to the state standard GB5974.1-86
            iRet = mLiveScan.LIVESCAN_FeatureExtract(fpFtp);
            Msg = String.format("FeatureExtract:%d", iRet);
            if (iRet == ID_Fpr.LIVESCAN_SUCCESS) {
                float[] fs = new float[1];
                iRet = mLiveScan.LIVESCAN_FeatureMatch(DataUtils.hexStringTobyte(liyang), fpFtp, fs);

                Msg = String.format("FP_FeatureMatch:%f :%s", fs[0], (fs[0] >= fpThreshold) ? "y" : "n");

                Toast.makeText(this, Msg, Toast.LENGTH_SHORT).show();

            }
            return true;
        }else {
            return false;
        }
    }


    /**
     * 比对用户提供的二代身份证指纹特征集合和设备取到的设备取到的设备取到的指纹特征是否有匹配
     * <p>
     * 采用 LIVESCAN_FeatureSearch
     */
    /*private boolean idcardIdentify(byte[] fpIDCard, byte[]... fpFinger) {

        float[] fs = new float[1];

        //指纹库特征
        byte[] m_byFeatures = new byte[ID_Fpr.LIVESCAN_FEATURE_SIZE * 1000];


        for (byte[] fpFinger:fpFinger){


        }

        System.arraycopy(m_byFeatures,fpFinger);

        int iRet = mLiveScan.LIVESCAN_FeatureMatch(fpIDCard, fpFinger, fs);

        if (iRet != 0) {

            String Msg = String.format("LIVESCAN_FeatureMatch:%d", iRet);
            Log.e(TAG, Msg);
            return false;
        }

        String Msg = String.format("FP_FeatureMatch:%f :%s", fs[0], (fs[0] >= fpThreshold) ? "y" : "n");

        if (fs[0] >= fpThreshold) {
            Log.i(TAG, Msg);
            return true;
        } else {
            Log.e(TAG, Msg);
            return false;
        }


    }*/
    private void matchFingerprint() {

        new Thread() {
            int iRet = 0;
            byte[] bScore = new byte[1];
            int i = 0;
            String Msg = "";
            byte[] fpRaw = new byte[ID_Fpr.LIVESCAN_IMAGE_WIDTH * ID_Fpr.LIVESCAN_IMAGE_HEIGHT];
            //shall refer to the state standard GA1011/1012
            byte[] fpFtp = new byte[512];

            @Override
            public void run() {
                while (!Thread.interrupted()) {

                    try {

                        iRet = mLiveScan.LIVESCAN_GetFPRawData(fpRaw);
                        if (iRet != ID_Fpr.LIVESCAN_SUCCESS) {
                            Msg = String.format("GetFPRawData:%d_%s", iRet, mLiveScan.LIVESCAN_GetErrorInfo(iRet));
                            break;
                        } else {

                        }
                        iRet = mLiveScan.LIVESCAN_GetQualityScore(fpRaw, bScore);
                        Msg = String.format("GetQualityScore:%d _%d", iRet, bScore[0] & 0xff);

                        //experience value 50
                        if ((bScore[0] & 0xff) >= ID_Fpr.LIVESCAN_IMAGE_SCORE_THRESHOLD) {
                            //byte cScannerType=0x17
                            //byte cFingerCode = 11~20 97~99 shall refer to the state standard GB5974.1-86
                            iRet = mLiveScan.LIVESCAN_FeatureExtract(fpFtp);
                            Msg = String.format("FeatureExtract:%d", iRet);
                            if (iRet == ID_Fpr.LIVESCAN_SUCCESS) {
                                float[] fs = new float[1];
                                iRet = mLiveScan.LIVESCAN_FeatureMatch(m_byFeature, fpFtp, fs);
                                Msg = String.format("FP_FeatureMatch:%f :%s", fs[0], (fs[0] >= fpThreshold) ? "y" : "n");

                                if (fs[0] >= fpThreshold) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ComparisonActivity.this, Msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {

                                }
                            }
                            break;
                        } else {
                            i++;
                            Thread.sleep(200);
                            if (i % 2 == 0) {
                            }
                            if (i % 2 == 1) {
                            }
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.start();
    }


}
