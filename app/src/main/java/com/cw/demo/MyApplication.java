package com.cw.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.HandlerThread;
import android.util.Log;


import org.litepal.LitePalApplication;

/**
 * @author Administrator
 */
public class MyApplication extends LitePalApplication {

    private String rootPath;

    private HandlerThread handlerThread;

    private static final String FILE_NAME = "CoreWise";

    private static MyApplication app;

    public HandlerThread getHandlerThread() {
        return handlerThread;
    }

    public static MyApplication getApp() {
        return app;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        //CrashHandler.getInstance().init(this);
        //handlerThread = new HandlerThread("handlerThread", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        // handlerThread = new HandlerThread("handlerThread");
        //handlerThread.start();
        //setRootPath();
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
        }else {
            return false;
        }
    }
}
