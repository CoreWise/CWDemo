package com.cw.demo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;


import com.cw.demo.utils.CrashHandler;
import com.cw.demo.utils.OKHttpUpdateHttpService;
import com.cw.serialportsdk.cw;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;

import android.app.Application;


import static com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION;

/**
 * @author Administrator
 */
public class MyApplication extends Application {

    private static final String TAG = "CWMyApplication";
    private static final String FILE_NAME = "cw";
    private static MyApplication app;
    private String rootPath;
    private ProgressDialog progressDialog;

    public static MyApplication getApp() {
        return app;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        CrashHandler.getInstance().init(this);


        XUpdate.get()
                .debug(true)
                .isGet(true)                                                    //默认设置使用get请求检查版本
                .isAutoMode(false)                                              //默认设置非自动模式，可根据具体使用配置
                .param("versionCode", UpdateUtils.getVersionCode(this))         //设置默认公共请求参数
                .param("appKey", getPackageName())
                .setOnUpdateFailureListener(new OnUpdateFailureListener() {     //设置版本更新出错的监听
                    @Override
                    public void onFailure(UpdateError error) {
                        if (error.getCode() != CHECK_NO_NEW_VERSION) {          //对不同错误进行处理
                            Log.e(TAG, error.toString());
                        }
                    }
                })
                .supportSilentInstall(true)                                     //设置是否支持静默安装，默认是true
                .setIUpdateHttpService(new OKHttpUpdateHttpService())           //这个必须设置！实现网络请求功能。
                .init(this);                                                    //这个必须初始化

    }

    private void setRootPath() {
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            rootPath = info.applicationInfo.dataDir;
            Log.i("rootPath", "################rootPath=" + rootPath);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public void setParam(Context context, String key, Object object) {
        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }
        editor.commit();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public Object getParam(Context context, String key, Object defaultObject) {
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 是否横屏
     *
     * @return
     */
    public boolean isLandscape(Context context) {
        //获取设置的配置信息
        Configuration mConfiguration = context.getResources().getConfiguration();
        int ori = mConfiguration.orientation;
        //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            return true;
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            return false;
        } else {
            return false;
        }
    }

    public boolean isShowingProgress() {
        if (progressDialog == null) {
            return false;
        }
        return progressDialog.isShowing();
    }

    public void showProgressDialog(Context context, String message) {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }

        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void cancleProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
            progressDialog = null;
        }

    }

    public void maintainScannerService() {
        if (cw.getDeviceModel() == cw.Device_U5 || cw.getDeviceModel() == cw.Device_U5_B || cw.getDeviceModel() == cw.Device_U5_C
                || cw.getDeviceModel() == cw.Device_U8) {
            if (cw.isScannerServiceRunning(getApplicationContext())) {
                Log.i("CWDevice", "cw.setGlobalSwicth(getApplicationContext(),false);");
                cw.setGlobalSwicth(getApplicationContext(), false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("CWDevice", "cw.setGlobalSwicth(getApplicationContext(),true);");
                        cw.setGlobalSwicth(getApplicationContext(), true);
                    }
                }, 200);
            }
        }
    }

}
