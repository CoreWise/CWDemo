package com.cw.demo.fingerprint.jra;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cw.demo.R;
import com.cw.fpjrasdk.JRA_API;
import com.cw.serialportsdk.utils.DataUtils;
import com.cw.serialportsdk.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.cw.fpjrasdk.syno_usb.OTG_KEY.CHAR_BUFFER_A;
import static com.cw.fpjrasdk.syno_usb.OTG_KEY.CHAR_BUFFER_B;
import static com.cw.fpjrasdk.syno_usb.OTG_KEY.PS_OK;

/**
 * 作者：李阳
 * 时间：2019/6/25
 * 描述：采集指纹图像
 */
public class JraFragment extends BaseFragment {

    private static final String TAG = "JraFragment";

    @BindView(R.id.bar)
    ProgressBar bar;
    @BindView(R.id.fingerImage)
    ImageView fingerImage;
    Unbinder unbinder;
    @BindView(R.id.capture)
    Button capture;
    @BindView(R.id.enroll)
    Button enroll;
    @BindView(R.id.search)
    Button search;
    @BindView(R.id.stop)
    Button stop;
    @BindView(R.id.infos)
    Button infos;
    @BindView(R.id.clear)
    Button clear;


    boolean globalControl = true;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_capture_jra, container, false);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.capture, R.id.enroll, R.id.search, R.id.stop, R.id.infos, R.id.clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
                getInfos();
                break;
            case R.id.clear:
                updateMsg(null);
//                parentActivity.updateMsg(null);

                if (JRA_API.PS_OK != parentActivity.jraApi.PSEmpty()) {
                    updateMsg("清空指纹库失败");
//                    parentActivity.updateMsg("清空指纹库失败");
                }
                updateMsg("清空指纹库成功");
//                parentActivity.updateMsg("清空指纹库成功");

                break;
        }
    }

    /**
     * 查看设备信息
     */
    private void getInfos() {

        int userIndex = parentActivity.jraApi.getUserIndex();
        int[] userId = parentActivity.jraApi.getUserId();
        int userMaxId = parentActivity.jraApi.getUserMaxId();

        updateMsg("jra has " + userIndex + " fp");
//        parentActivity.updateMsg("jra has " + userIndex + " fp");

        updateMsg("there are :");
//        parentActivity.updateMsg("there are :");
        for (int i = 0; i < userIndex; i++) {
            updateMsg("id:" + userId[i]);
//            parentActivity.updateMsg("id:" + userId[i]);
        }
        if (userIndex != 0) {
            updateMsg("Max Id：" + userMaxId);
            updateMsg("jra has " + userIndex + " fp");
//            parentActivity.updateMsg("Max Id：" + userMaxId);
//            parentActivity.updateMsg("jra has " + userIndex + " fp");

        }
    }

    /**
     * 更新图片
     */
    private void updateFingerImg() {

        String localName = "finger.bmp";
        FileInputStream localStream = null;
        try {
            localStream = parentActivity.openFileInput(localName);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(localStream);
        fingerImage.setImageBitmap(bitmap);
        try {
            localStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void writeDataToFile(String data) {

        String root = "/sdcard/";
        String name = "JRAFpRaw.txt";

        //先判断是否有文件，没有就创建，有就追加
        FileUtils.makeFilePath(root, name);

        // 每次写入时，都换行写
        String strContent = data + "\r\n";
        try {
            File file = new File(root + name);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e(TAG, "Error on write File:" + e);
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
                while (parentActivity.jraApi.PSGetImage() != JRA_API.PS_NO_FINGER) {

                    if (globalControl == false) {
                        return -1;
                    }
                    sleep(20);
                }

                while (parentActivity.jraApi.PSGetImage() == JRA_API.PS_NO_FINGER) {

                    if (globalControl == false) {
                        return -1;
                    }
                    sleep(10);
                }

                if ((ret = parentActivity.jraApi.PSUpImage(JRA_API.PS_FingerBuf)) != 0) {
                    publishProgress(getString(R.string.fp_jra_up_image_failure) + ret);
                    continue;
                }

                if ((ret = parentActivity.jraApi.WriteBmp(parentActivity, JRA_API.PS_FingerBuf)) != 0) {
                    publishProgress(getString(R.string.fp_jra_write_bmp_failure) + ret);
                    continue;
                }
                publishProgress("OK");
            }
        }


        @Override
        protected void onPreExecute() {
            parentActivity.updateMsg(getString(R.string.fp_jra_enter_fp_please));
            return;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values[0].equals("OK")) {
                updateFingerImg();
                return;
            }
            parentActivity.updateMsg(values[0]);
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
                while (parentActivity.jraApi.PSGetImage() != JRA_API.PS_NO_FINGER) {
                    if (globalControl == false) {
                        return -1;
                    }

                    sleep(20);
                }
                while (parentActivity.jraApi.PSGetImage() == JRA_API.PS_NO_FINGER) {
                    if (globalControl == false) {
                        return -1;
                    }
                    sleep(10);
                }

                if ((ret = parentActivity.jraApi.PSUpImage(JRA_API.PS_FingerBuf)) != 0) {
                    publishProgress(getString(R.string.fp_jra_up_image_failure) + ret);
                    continue;
                }
                if ((ret = parentActivity.jraApi.WriteBmp(parentActivity, JRA_API.PS_FingerBuf)) != 0) {
                    publishProgress(getString(R.string.fp_jra_write_bmp_failure) + ret);
                    continue;
                }
                publishProgress("OK");

                //生成模板
                if (cnt == 1) {
                    if ((ret = parentActivity.jraApi.PSGenChar(JRA_API.CHAR_BUFFER_A)) != JRA_API.PS_OK) {
                        publishProgress(getString(R.string.fp_jra_fp_raw_1_failure) + ret);
                        cnt--;//减掉重新来
                        //return -1;
                    } else {
                        publishProgress("updateProgress");

                        publishProgress(getString(R.string.fp_jra_enter_fp_again));
                    }
                }
                if (cnt == 2) {
                    if ((ret = parentActivity.jraApi.PSGenChar(JRA_API.CHAR_BUFFER_B)) != JRA_API.PS_OK) {
                        publishProgress(getString(R.string.fp_jra_fp_raw_2_failure) + ret);
                        continue;
                    } else {
                        publishProgress("updateProgress");

                    }
                    byte[] fpRaw = new byte[512];
                    if (parentActivity.jraApi.PSRegModule() != JRA_API.PS_OK) {
                        bar.setProgress(0);
                        publishProgress(getString(R.string.fp_jra_fp_raw_failure_please_enter_again));
                        return -1;
                    }

         /*           if (parentActivity.jraApi.getUserIndex() >= JRA_API.PS_MAX_FINGER) {
                        publishProgress("模板存满，请删除部分指纹信息");
                    }*/

                    int[] fingerId = new int[1];


                    ret = parentActivity.jraApi.PSStoreChar(fingerId, fpRaw);
                    if (ret != JRA_API.PS_OK) {
                        bar.setProgress(0);
                        if (ret == JRA_API.PS_MAX_FINGER) {
                            publishProgress(getString(R.string.fp_jra_fp_raw_full));
                        } else {
                            publishProgress(getString(R.string.fp_jra_store_raw_failure));
                        }
                        return -1;
                    }
                    String s = DataUtils.bytesToHexString(fpRaw);
                    publishProgress(s);
                    writeDataToFile(s);
                    publishProgress(getString(R.string.fp_jra_eroll_success) + fingerId[0]);
                    return 0;
                }
                cnt++;
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
                updateMsg(getString(R.string.fp_jra_enroll_failure));
//                parentActivity.updateMsg(getString(R.string.fp_jra_enroll_failure));
                globalControl = false;
            }
        }

        @Override
        protected void onPreExecute() {
            updateMsg(getString(R.string.fp_jra_enroll_start));

//            if (isAdded())
//            {
//                parentActivity.updateMsg(getString(R.string.fp_jra_enroll_start));
//            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values[0].equals("OK")) {
                updateFingerImg();
                return;
            }
            if (values[0].equals("updateProgress")) {
                Progress += 50;
                bar.setProgress(Progress);
                return;
            }

            updateMsg(values[0]);

//            if (isAdded())
//            {
//                parentActivity.updateMsg(values[0]);
//            }
        }
    }

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
                while (parentActivity.jraApi.PSGetImage() == JRA_API.PS_NO_FINGER) {
                    if (globalControl == false) {
                        return -1;
                    }
                    sleep(20);
                }
                if ((ret = parentActivity.jraApi.PSUpImage(JRA_API.PS_FingerBuf)) != 0) {
                    continue;
                }
                if ((ret = parentActivity.jraApi.WriteBmp(parentActivity, JRA_API.PS_FingerBuf)) != 0) {
                    continue;
                }
                publishProgress("OK");

                if (parentActivity.jraApi.PSGenChar(JRA_API.CHAR_BUFFER_A) != JRA_API.PS_OK) {
                    continue;
                }
                if (PS_OK != parentActivity.jraApi.PSSearch(JRA_API.CHAR_BUFFER_A, fingerId)) {
                    publishProgress(getString(R.string.fp_jra_search_no));
                    continue;
                }
                publishProgress(getString(R.string.fp_jra_search_fp_success) + fingerId[0]);
            }
        }


        @Override
        protected void onPreExecute() {
            parentActivity.updateMsg(getString(R.string.fp_jra_search_start));
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values[0].equals("OK")) {
                updateFingerImg();
                return;
            }
            parentActivity.updateMsg(values[0]);
        }
    }

    public void updateMsg(String s)
    {
        if (isAdded())
        {
            parentActivity.updateMsg(s);
        }
    }

}
