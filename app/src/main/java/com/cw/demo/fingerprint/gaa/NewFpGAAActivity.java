package com.cw.demo.fingerprint.gaa;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cw.demo.MyApplication;
import com.cw.demo.R;
import com.cw.fpgaasdk.GaaApiBHMDevice;
import com.cw.fpgaasdk.GaaApiBase;
import com.cw.fpgaasdk.GaaApiZiDevice;
import com.cw.fpgaasdk.GaaFingerFactory;
import com.cw.fpgaasdk.USBFingerManager;
import com.cw.serialportsdk.utils.DataUtils;
import com.fm.bio.ID_Fpr;


public class NewFpGAAActivity extends Activity implements OnClickListener {

    private static final String TAG = "CwGAAActivity";
    ProgressBar bar;
    ImageView fingerImage;
    Button capture;
    Button enroll;
    Button enroll2;
    Button search;
    Button stop;
    Button infos;
    Button clear;
    TextView msgText;
    boolean globalControl = true;

    //    public GAA_API mGaaApi;
    public GaaApiBase mGaaApi;


    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "------------onCreate--------------");

        setContentView(R.layout.fragment_capture_gaa);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initview();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "------------onStart--------------");
        open();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "------------onResume--------------");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "------------onRestart--------------");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "------------onStop--------------");

        close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "------------onDestroy--------------");
    }

    public void initview() {
        bar = findViewById(R.id.bar);
        fingerImage = findViewById(R.id.fingerImage);
        capture = findViewById(R.id.capture);
        enroll = findViewById(R.id.enroll);
        search = findViewById(R.id.search);
        enroll2 = findViewById(R.id.enroll2);
        stop = findViewById(R.id.stop);
        infos = findViewById(R.id.infos);
        clear = findViewById(R.id.clear);
        msgText = findViewById(R.id.msg);
        msgText.setMovementMethod(ScrollingMovementMethod.getInstance());

        capture.setOnClickListener(this);
        enroll.setOnClickListener(this);
        search.setOnClickListener(this);
        enroll2.setOnClickListener(this);
        stop.setOnClickListener(this);
        infos.setOnClickListener(this);
        clear.setOnClickListener(this);
    }

    private void open() {
        MyApplication.getApp().showProgressDialog(this, getString(R.string.fp_usb_init));
        USBFingerManager.getInstance(this).openUSB(new USBFingerManager.OnUSBFingerListener() {
            @Override
            public void onOpenUSBFingerSuccess(String device) {
                MyApplication.getApp().cancleProgressDialog();
                if (device.equals(GaaApiBase.ZiDevice)) {
                    //新固件
                    mGaaApi = new GaaFingerFactory().createGAAFinger(GaaApiBase.ZiDevice,NewFpGAAActivity.this);

//                    mGaaApi = new GAANewAPI(NewFpGAAActivity.this);
                    int ret = mGaaApi.openGAA();
                    if (ret == GaaApiBase.LIVESCAN_SUCCESS) {
                        updateMsg("open device success 1");
                    } else if (ret == GaaApiBase.LIVESCAN_NOTINIT) {
                        updateMsg("not init");
                    } else if (ret == GaaApiBase.LIVESCAN_AUTH_FAILED) {
                        updateMsg("auth failed");
                    } else {
                        updateMsg("unknown " + ret);
                    }

                } else if (device.equals(GaaApiBase.BHMDevice)) {
                    //旧固件
                    mGaaApi = new GaaFingerFactory().createGAAFinger(GaaApiBase.BHMDevice,NewFpGAAActivity.this);


//                    mGaaApi = new GAAOldAPI(NewFpGAAActivity.this);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int ret = ((GaaApiBHMDevice)mGaaApi).openGAA();
                            if (ret == GaaApiBase.LIVESCAN_SUCCESS) {
                                updateMsg("open device success 2");
                            } else if (ret == GaaApiBase.LIVESCAN_NOTINIT) {
                                updateMsg("not init");
                            } else if (ret == GaaApiBase.LIVESCAN_AUTH_FAILED) {
                                updateMsg("auth failed");
                            } else {
                                String msg = mGaaApi.ErrorInfo(ret);
                                updateMsg("unknown " + ret);
                                updateMsg("msg =" + msg);
                            }
                        }
                    },1000);
                } else {
                    updateMsg("开发包和指纹模块不一致! 请联系商务 " + device);
//                    Toast.makeText(NewFpGAAActivity.this, "device ="+device, Toast.LENGTH_SHORT).show();
                    Toast.makeText(NewFpGAAActivity.this, "开发包和指纹模块不一致! 请联系商务", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onOpenUSBFingerFailure(String error) {
                Log.e(TAG, error);
                MyApplication.getApp().cancleProgressDialog();
                Toast.makeText(NewFpGAAActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void close() {
        btnStatus(true);
        globalControl = false;
        updateMsg("设备已关闭");
        if (mGaaApi != null) {
            mGaaApi.closeGAA();
        }
        USBFingerManager.getInstance(this).closeUSB();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.capture:
                globalControl = true;
                btnStatus(false);
                UpAsyncTask asyncTask_up = new UpAsyncTask();
                asyncTask_up.execute(1);
                break;
            case R.id.enroll:
                btnStatus(false);

                globalControl = true;
                ImputAsyncTask asyncTask = new ImputAsyncTask();
                asyncTask.execute(1);
                break;
            case R.id.enroll2:
//                btnStatus(false);
//
//                globalControl = true;
//                ImputAsyncTask2 asyncTask2 = new ImputAsyncTask2();
//                asyncTask2.execute(1);
                break;
            case R.id.search:
                btnStatus(false);
                globalControl = true;
                SearchAsyncTask asyncTask_search = new SearchAsyncTask();
                asyncTask_search.execute(1);
                break;
            case R.id.stop:
                btnStatus(true);
                globalControl = false;
                fingerImage.setImageBitmap(null);
                break;

            case R.id.infos:
//                getInfos();
                break;
            case R.id.clear:
                updateMsg(null);

                if (GaaApiBase.LIVESCAN_SUCCESS != mGaaApi.PSEmpty()) {
                    updateMsg("清空指纹库失败");
                }
                updateMsg("清空指纹库成功");
                break;
        }
    }

    /**
     * 采集图片
     */
    @SuppressLint("StaticFieldLeak")
    public class UpAsyncTask extends AsyncTask<Integer, String, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            int ret = 0;
            while (true) {

                if (globalControl == false) {
                    return -1;
                }
                while (mGaaApi.PSGetImage() != GaaApiBase.LIVESCAN_NO_FINGER) {
                    if (globalControl == false) {
                        return -1;
                    }
                    publishProgress("mGaaApi.PSGetImage():" + mGaaApi.PSGetImage());
                    sleep(20);
                }
                while (mGaaApi.PSGetImage() == GaaApiBase.LIVESCAN_NO_FINGER) {

                    if (globalControl == false) {
                        return -1;
                    }
                    publishProgress("mGaaApi.PSGetImage():" + mGaaApi.PSGetImage());
                    sleep(10);
                }
                if ((ret = mGaaApi.PSUpImage(mGaaApi.PS_FingerBuf)) != ID_Fpr.LIVESCAN_SUCCESS) {
                    publishProgress("上传图像失败:" + ret);
                    continue;
                }
                publishProgress("OK");
            }
        }


        @Override
        protected void onPreExecute() {
            updateMsg("显示图片,请在传感器放上手指");
            return;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values[0].equals("OK")) {
                updateFingerImg(mGaaApi.PS_FingerBuf);
                return;
            }
            updateMsg(values[0]);
        }
    }

    /**
     * 录入指纹
     */
    @SuppressLint("StaticFieldLeak")
    public class ImputAsyncTask extends AsyncTask<Integer, String, Integer> {
        int Progress = 0;

        @Override
        protected Integer doInBackground(Integer... params) {
            int cnt = 1;
            int ret;

            while (true) {
                if (globalControl == false) {
                    return -1;
                }
                while (mGaaApi.PSGetImage() != GaaApiBase.LIVESCAN_NO_FINGER) {
                    if (globalControl == false) {
                        return -1;
                    }

                    sleep(20);
                }
                while (mGaaApi.PSGetImage() == GaaApiBase.LIVESCAN_NO_FINGER) {
                    if (globalControl == false) {
                        return -1;
                    }
                    sleep(10);
                }

                if ((ret = mGaaApi.PSUpImage(mGaaApi.PS_FingerBuf)) != GaaApiBase.LIVESCAN_SUCCESS) {
                    publishProgress("上传图像失败:" + ret);
                    continue;
                }
                publishProgress("OK");

                byte[] mFeature = new byte[ID_Fpr.LIVESCAN_FEATURE_SIZE];
                int[] fingerId = new int[1];
                //生成模板
                if ((ret = mGaaApi.PSGenChar(mFeature, fingerId)) != GaaApiBase.LIVESCAN_SUCCESS) {
                    publishProgress("生成特征失败:" + ret);
                    continue;
                } else {
                    publishProgress("updateProgress");
                }

                String s = DataUtils.bytesToHexString(mFeature);
                publishProgress(s);
                publishProgress("录入指纹成功,=====>ID:" + fingerId[0]);
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            globalControl = false;
            if (0 == result) {
                bar.setProgress(100);
                btnStatus(true);
            } else {
                bar.setProgress(0);
                updateMsg("指纹录入失败，请重新录入");
                globalControl = false;
            }
        }

        @Override
        protected void onPreExecute() {
            updateMsg("录入指纹=>开始,请放上手指");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values[0].equals("OK")) {
                updateFingerImg(mGaaApi.PS_FingerBuf);
                return;
            }
            if (values[0].equals("updateProgress")) {
                Progress += 50;
                bar.setProgress(Progress);
                return;
            }
            updateMsg(values[0]);
        }
    }

    long time;

    /**
     * 搜索指纹
     */
    @SuppressLint("StaticFieldLeak")
    public class SearchAsyncTask extends AsyncTask<Integer, String, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
            int ret;
            int[] fingerId = new int[1];
            while (true) {
                if (globalControl == false) {
                    return -1;
                }
                while (mGaaApi.PSGetImage() == GaaApiBase.LIVESCAN_NO_FINGER) {
                    if (globalControl == false) {
                        return -1;
                    }
                    sleep(20);
                }
                if ((ret = mGaaApi.PSUpImage(mGaaApi.PS_FingerBuf)) != GaaApiBase.LIVESCAN_SUCCESS) {
                    continue;
                }
                publishProgress("OK");

                byte[] mFeature = new byte[ID_Fpr.LIVESCAN_FEATURE_SIZE];
                if (mGaaApi.PSGenChar(mFeature) != GaaApiBase.LIVESCAN_SUCCESS) {
                    continue;
                }
                time = System.currentTimeMillis();

                int code = mGaaApi.PSSearch(mFeature, fingerId);
                if (GaaApiBase.LIVESCAN_SUCCESS != code) {//mGaaApi.PSSearch(mFeature, fingerId)
                    publishProgress("没有找到此指纹");
                    publishProgress("code = "+code);
                    continue;
                }

//                int[] fingerId1 = new int[3];
//                mGaaApi.newTime(mFeature, fingerId1);

                updateMsg("search time = " + (System.currentTimeMillis() - time));
                publishProgress("成功搜索到此指纹,ID===> 0 =" + fingerId[0]);
//                publishProgress("成功搜索到此指纹,ID===> 0 =" + fingerId1[0]+",1 = "+fingerId1[1]+",2 = "+fingerId1[2]);
            }
        }


        @Override
        protected void onPreExecute() {
            updateMsg("搜索指纹=>开始,请放上手指");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values[0].equals("OK")) {
                updateFingerImg(mGaaApi.PS_FingerBuf);
                return;
            }
            updateMsg(values[0]);
        }
    }

    private void updateFingerImg(byte[] fpBmp) {
        try {
            updateMsg("updateFingerImg");
//            Bitmap bitmap = BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.length);
//            fingerImage.setImageBitmap(bitmap);
            fingerImage.setImageBitmap(mGaaApi.DataToBmp(fpBmp));
        } catch (Exception e) {
            e.printStackTrace();
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

    public void updateMsg(final String msg) {
        if (msg == null) {
            msgText.setText("");
            msgText.scrollTo(0, 0);
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //获取当前行数
                int lineCount = msgText.getLineCount();
                if (lineCount > 2000) {
                    //大于100行自动清零
                    msgText.setText("");
                    msgText.setText(msg);
                } else {
                    //小于100行追加
                    msgText.append("\n" + msg);
                }

                //当前文本高度
                int scrollAmount = msgText.getLayout().getLineTop(msgText.getLineCount()) - msgText.getHeight();
                if (scrollAmount > 0) {
                    msgText.scrollTo(0, scrollAmount);
                } else {
                    msgText.scrollTo(0, 0);
                }
//                int offset = lineCount * msgText.getLineHeight();
//                if (offset > msgText.getHeight()) {
//                    msgText.scrollTo(0, offset - msgText.getLineHeight());
//                    txt_mmm.scrollTo(0, scrollAmount);
//                }
            }
        });
    }

    private void btnStatus(boolean status) {
        capture.setEnabled(status);
        enroll.setEnabled(status);
        search.setEnabled(status);
        stop.setEnabled(!status);
        infos.setEnabled(status);
        clear.setEnabled(status);
        bar.setProgress(0);
    }

    /**
     * 延时
     *
     * @param time
     */
    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.toString();
        }
    }
}
