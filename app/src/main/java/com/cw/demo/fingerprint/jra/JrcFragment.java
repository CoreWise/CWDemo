package com.cw.demo.fingerprint.jra;

import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cw.demo.R;
import com.cw.fpjrcsdk.DBInfo;
import com.fm.bio.FPM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by 金宇凡 on 2020/6/2.
 */
public class JrcFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.fingerImage)
    ImageView fingerImage;
    @BindView(R.id.bar)
    ProgressBar bar;
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
    private List<Integer> fplist;//模板列标
    private List<DBInfo> fplistDB;
    private byte[] pMbn = new byte[3000 * FPM.FEATURE_LENTH];

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what)
           {
               case 0:
                   globalControl = false;
                   btnStatus(true);
                   break;
           }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_capture_jra, container, false);
        unbinder = ButterKnife.bind(this, root);

        fplist = new ArrayList<>();
        fplistDB = new ArrayList<>();
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
                //获取指纹图像
                globalControl = true;
                btnStatus(false);
                getImage();
                break;
            case R.id.enroll:
                //注册
                //获取未使用的id
                int idleID = getIdleID();
                if (idleID >= 0) {
                    globalControl = true;
                    btnStatus(false);
                    regFingerprint(idleID);
                } else {
                    //id已经注册满
                }
                break;
            case R.id.search:
                //搜索
                globalControl = true;
                btnStatus(false);
                searchFingerprint();
                break;
            case R.id.stop:
                globalControl = false;
                btnStatus(true);
                fingerImage.setImageBitmap(null);
                break;
            case R.id.infos:
                getInfos();
                break;
            case R.id.clear:
                //清空
                fplist.clear();
                fplistDB.clear();
                parentActivity.mJrcApi.Empty();
                parentActivity.mJrcApi.GetDBList(fplist);

                if (fplist.size() > 0) {
                    updateMsg("清空指纹库失败");
                } else {
                    updateMsg("清空指纹库成功");
                }
                break;
        }
    }

    /**
     * 获取指纹图片
     */
    public void getImage() {
        updateMsg("Place Your Finger in 15s");
        new Thread() {
            int iRet = 0;//指纹处理结果
            int[] isPressed = new int[1];
            int i = 0;
            String Msg = "";
            long st = System.currentTimeMillis();

            byte[] fpRaw = parentActivity.mJrcApi.PS_FingerBuf;

            @Override
            public void run() {
                //清理缓冲区数据
                parentActivity.mJrcApi.ClearFingerBuf();

                while (!Thread.interrupted() || (System.currentTimeMillis() - st) < 15 * 1000) {

                    if (globalControl == false) {
                        break;
                    }

                    try {
                        iRet = parentActivity.mJrcApi.DetectFinger(isPressed);
                        if (iRet != FPM.SUCCESS) {
                            Msg = "DetectFinger: " + iRet;
                            updateMsg(Msg);
                            break;
                        }
                        if (isPressed[0] == 1) {
                            //获取指纹图片
                            iRet = parentActivity.mJrcApi.ImageRawData(fpRaw);
                            if (iRet == FPM.SUCCESS) {
                                updateFingerImg(parentActivity.mJrcApi.DataToBmp(fpRaw));
                            }
                            Msg = "GetImageRawData: " + iRet;
                            updateMsg(Msg);
                            break;

                        }

                        i++;
                        Thread.sleep(200);
                        if (i == 0) {
                            updateMsg("Lift and Place Your Finger.");
                        }
//                        if (i % 2 == 0) {
//                            updateMsg("Lift and Place Your Finger.");
//                        }
//                        if (i % 2 == 1) {
//                            updateMsg("Lift and Place Your Finger...");
//                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (isPressed[0] == 0 && iRet == FPM.SUCCESS) {
                    updateMsg("Timeout");
                }

                sendMsg(0,"");

            }

        }.start();
    }

    public void stopThread()
    {
        globalControl = false;
        btnStatus(true);
    }

    /**
     * 注册指纹
     *
     * @param id 保存的id
     */
    public void regFingerprint(final int id) {
        updateMsg("Place Your Finger");
        new Thread() {
            int iRet = 0;
            int[] isPressed = new int[1];
            int i = 0;
            int index = 0;

            boolean isNeedUp = false;
            String Msg = "";
            boolean isReg = false;
            byte[] fpRaw = parentActivity.mJrcApi.PS_FingerBuf;
            byte[][] fpTzs = new byte[3][FPM.FEATURE_LENTH];
            byte[] fpMb = new byte[FPM.FEATURE_LENTH];
            long st = System.currentTimeMillis();

            @Override
            public void run() {
                parentActivity.mJrcApi.ClearFingerBuf();

                while (!Thread.interrupted() || (System.currentTimeMillis() - st) < 15 * 1000) {

                    if (globalControl == false) {
                        break;
                    }

                    try {
                        //检测手指
                        iRet = parentActivity.mJrcApi.DetectFinger(isPressed);
                        if (iRet != FPM.SUCCESS) {
                            Msg = "DetectFinger: " + iRet;
                            updateMsg(Msg);
                            break;
                        }

                        //判断手指是否需要离开感应器
                        if (isNeedUp) {
                            if (isPressed[0] == 1) {
                                updateMsg("Lift and Place Your Finger.");
                            } else {
                                isNeedUp = false;
                            }
                        } else {
                            //判断是否检测到手指
                            if (isPressed[0] == 1) {
                                //获取指纹图片
                                parentActivity.mJrcApi.ImageRawData(fpRaw);
                                if (iRet == FPM.SUCCESS) {
                                    //格式转换 输出图片
                                    updateFingerImg(parentActivity.mJrcApi.DataToBmp(fpRaw));
                                } else {
                                    Msg = "GetImageRawData: " + iRet;
                                    updateMsg(Msg);
                                    break;
                                }
                                //提取特征值
                                iRet = parentActivity.mJrcApi.FeatureExtract(fpTzs[index], index + 1);

                                if (iRet != FPM.SUCCESS) {
                                    Msg = "Extract: " + iRet;
                                    updateMsg(Msg);
                                    break;
                                }
                                index++;
                                isNeedUp = true;
                                bar.setProgress(33*index);
                                if (index == 3) {
                                    //将三个特征转换成模板
                                    iRet = parentActivity.mJrcApi.FeatureEnroll(fpTzs[0], fpTzs[1], fpTzs[2], fpMb);
                                    bar.setProgress(0);
                                    if (iRet != FPM.SUCCESS) {
                                        Msg = "Enroll: " + iRet;
                                        updateMsg(Msg);
                                        break;
                                    }

                                    DBInfo mInfo = new DBInfo();
                                    mInfo.setId(id);
                                    mInfo.setData(fpMb);
                                    fplistDB.add(mInfo);
                                    fplist.add(id);

                                    parentActivity.mJrcApi.SaveTemplate(id);

                                    if (iRet != FPM.SUCCESS) {
                                        Msg = "Save: " + iRet;
                                        updateMsg(Msg);
                                        break;
                                    }
                                    isReg = true;
                                    Msg = "id: " + id + " ,Enroll ok";
                                    updateMsg(Msg);
                                    break;
                                }
                            }
                        }
                        i++;
                        Thread.sleep(200);
                        if (i == 0) {
                            updateMsg("Lift and Place Your Finger.");
                        }
//                        if (i % 2 == 0) {
//                            updateMsg("Lift and Place Your Finger.");
//                        }
//                        if (i % 2 == 1) {
//                            updateMsg("Lift and Place Your Finger...");
//                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (isPressed[0] == 0 && iRet == FPM.SUCCESS) {
                    updateMsg("Timeout");
                }

                sendMsg(0,"");

                parentActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (isReg) {
                            parentActivity.mJrcApi.GetDBList(fplist);
                            //数据同步
                            for (int i = 0; i < fplistDB.size(); i++) {
                                System.arraycopy(fplistDB.get(i).getData(), 0, pMbn, i * FPM.FEATURE_LENTH, FPM.FEATURE_LENTH);
                            }

                        }
                    }
                });
            }
        }.start();
    }

    /**
     * 搜索指纹
     */
    public void searchFingerprint() {

        updateMsg("Place Your Finger");
        new Thread() {
            int iRet = 0;
            int[] isPressed = new int[1];
            int i = 0;
            String Msg = "";
            byte[] fpRaw = parentActivity.mJrcApi.PS_FingerBuf;
            byte[] fpTz = new byte[FPM.FEATURE_LENTH];
            long st = System.currentTimeMillis();

            @Override
            public void run() {
                parentActivity.mJrcApi.ClearFingerBuf();

                while (!Thread.interrupted() || (System.currentTimeMillis() - st) < 15 * 1000) {

                    if (globalControl == false) {
                        break;
                    }

                    try {
                        iRet = parentActivity.mJrcApi.DetectFinger(isPressed);
                        if (iRet != FPM.SUCCESS) {
                            Msg = String.format("DetectFinger:%d", iRet);
                            updateMsg(Msg);
                            break;
                        }

                        if (isPressed[0] == 1) {
                            parentActivity.mJrcApi.ImageRawData(fpRaw);
                            if (iRet == FPM.SUCCESS) {
                                updateFingerImg(parentActivity.mJrcApi.DataToBmp(fpRaw));
                            } else {
                                Msg = "GetImageRawData: " + iRet;
                                updateMsg(Msg);
                                break;
                            }

                            iRet = parentActivity.mJrcApi.FeatureExtract(fpTz, 1);
                            if (iRet != FPM.SUCCESS) {
                                Msg = String.format("Extract:%d", iRet);
                                updateMsg(Msg);
                                break;
                            }
                            int retid[] = new int[1];
                            iRet = parentActivity.mJrcApi.FeatureSearch(retid);
                            if (iRet == FPM.SUCCESS) {
                                Msg = String.format("Search OK Index:%d", retid[0]);
                                updateMsg(Msg);
                                break;
                            } else {
                                Msg = String.format("Search Failed");
                                updateMsg(Msg);
                                break;
                            }
                        }


                        i++;
                        Thread.sleep(200);
                        if (i == 0) {
                            updateMsg("Lift and Place Your Finger.");
                        }
//                        if (i % 2 == 0) {
//                            updateMsg("Place Your Finger.");
//                        }
//                        if (i % 2 == 1) {
//                            updateMsg("Place Your Finger...");
//                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (isPressed[0] == 0 && iRet == FPM.SUCCESS) {
                    updateMsg("Timeout");
                }


                sendMsg(0,"");
            }

        }.start();
    }

    /**
     * 比对指纹
     *
     * @param id 需要比对指纹的id
     */
    public void matchFingerprint(final int id) {

        updateMsg("Place Your Finger");
        parentActivity.mJrcApi.LoadTemplate(id, 2);
        new Thread() {
            int iRet = 0;
            int[] isPressed = new int[1];
            int i = 0;
            String Msg = "";
            byte[] fpRaw = parentActivity.mJrcApi.PS_FingerBuf;
            byte[] fpTz = new byte[FPM.FEATURE_LENTH];
            long st = System.currentTimeMillis();

            @Override
            public void run() {
                parentActivity.mJrcApi.ClearFingerBuf();

                while (!Thread.interrupted() || (System.currentTimeMillis() - st) < 15 * 1000) {

                    if (globalControl == false) {
                        break;
                    }

                    try {
                        iRet = parentActivity.mJrcApi.DetectFinger(isPressed);
                        if (iRet != FPM.SUCCESS) {
                            Msg = "DetectFinger: " + iRet;
                            updateMsg(Msg);
                            break;
                        }

                        if (isPressed[0] == 1) {
                            parentActivity.mJrcApi.ImageRawData(fpRaw);
                            if (iRet == FPM.SUCCESS) {
                                updateFingerImg(parentActivity.mJrcApi.DataToBmp(fpRaw));
                            } else {
                                Msg = "GetImageRawData: " + iRet;
                                updateMsg(Msg);
                                break;
                            }

                            iRet = parentActivity.mJrcApi.FeatureExtract(fpTz, 1);
                            if (iRet != FPM.SUCCESS) {
                                Msg = "Extract: " + iRet;
                                updateMsg(Msg);
                                break;
                            }
                            iRet = parentActivity.mJrcApi.FeatureMatch();
                            if (iRet == FPM.SUCCESS) {
                                Msg = "Match OK";
                                updateMsg(Msg);
                                break;
                            } else {
                                Msg = "Match failed";
                                updateMsg(Msg);
                                break;
                            }
                        }
                        i++;
                        Thread.sleep(200);
                        if (i == 0) {
                            updateMsg("Lift and Place Your Finger.");
                        }
//                        if (i % 2 == 0) {
//                            updateMsg("Place Your Finger.");
//                        }
//                        if (i % 2 == 1) {
//                            updateMsg("Place Your Finger...");
//                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (isPressed[0] == 0 && iRet == FPM.SUCCESS) {
                    updateMsg("Timeout");
                }

                sendMsg(0,"");
            }
        }.start();
    }

    /**
     * 获取闲置id
     *
     * @return
     */
    private int getIdleID() {
        parentActivity.mJrcApi.GetDBList(fplist);

        int regid = -1;
        boolean isFind = false;
        if (fplist.size() < parentActivity.mJrcApi.jrcDBSize) {
            for (int i = 0; i < parentActivity.mJrcApi.jrcDBSize; i++) {
                isFind = false;
                for (int j = 0; j < fplist.size(); j++) {
                    if (i == fplist.get(j)) {
                        isFind = true;
                        break;
                    }
                }
                if (isFind == false) {
                    regid = i;
                    break;
                }
            }
        }
        return regid;
    }

    public void updateMsg(String s) {
        if (isAdded()) {
            parentActivity.updateMsg(s);
        }
    }

    /**
     * 查看设备信息
     */
    private void getInfos() {

        StringBuilder result = new StringBuilder();
        List<UsbDevice> deviceList = getDeviceList();
        if (deviceList.size() > 0) {
            UsbDevice device = deviceList.get(0);
            result.append("-------------------------------------------------------------------------\n");
            result.append("device name:  ").append(device.getDeviceName()).append("\n");
            result.append("Product Id:  ").append(device.getProductId()).append("\n");
            result.append("Vendor Id:  ").append(device.getVendorId()).append("\n");
            result.append("Device Id:  ").append(device.getDeviceId()).append("\n");
            result.append("Interface Count:  ").append(device.getInterfaceCount()).append("\n");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                result.append("Manufacturer name:  ").append(device.getManufacturerName()).append("\n");
                result.append("Serial Number:  ").append(device.getSerialNumber()).append("\n");
                result.append("Product Name:  ").append(device.getProductName()).append("\n");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                result.append("Version:  ").append(device.getVersion()).append("\n");
            }
            result.append("-------------------------------------------------------------------------\n");

            updateMsg(result.toString());
        }
        else
        {
            updateMsg("没有发现指纹");
        }


    }

    private List<UsbDevice> getDeviceList() {
        HashMap<String, UsbDevice> deviceList = parentActivity.mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        List<UsbDevice> usbDevices = new ArrayList<>();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            usbDevices.add(device);
        }
        return usbDevices;
    }

    private void sendMsg(int what,String msg)
    {
        Message message = new Message();
        message.what = what;
        message.obj = msg;
        handler.sendMessage(message);
    }

    /**
     * 更新图片
     */
    private void updateFingerImg(final Bitmap bitmap) {
        parentActivity.runOnUiThread(new Runnable() {
            public void run() {
                fingerImage.setImageBitmap(bitmap);
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
//        bar.setProgress(0);
    }
}
