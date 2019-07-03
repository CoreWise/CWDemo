package com.cw.demo.fingerprint.jra;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.cw.demo.MyApplication;
import com.cw.demo.R;
import com.cw.serialportsdk.utils.DataUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.cw.fpjrasdk.JRA_API.PS_OK;


/**
 * 作者：李阳
 * 时间：2019/6/25
 * 描述：
 */
public class JraCR30AFragment extends BaseFragment {

    private static final String TAG = "JraCR30AFragment";


    ArrayList<String> fingerRaw = new ArrayList<>();

    @BindView(R.id.cr30a)
    Button cr30a;
    @BindView(R.id.up)
    Button up;
    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cr30a_jra, container, false);

        unbinder = ButterKnife.bind(this, root);
        return root;
    }


    public void readFromFpTxt() {
        fingerRaw.clear();
        int Row = 0;

        String pathName = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            pathName = Environment.getExternalStorageDirectory().getPath().toString();
        }
        pathName = pathName + "/fg.txt";

        Log.e(TAG, "pathName = " + pathName);
        File fp = new File(pathName);

        try {
            long startTime = System.currentTimeMillis();
            InputStream inputStream = new FileInputStream(fp);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("")) {
                    Log.i(TAG, "----------------空行--------------");
                    Log.i(TAG, "line = " + line);
                } else {
                    Log.i(TAG, "----------------++++--------------");
                    Row++;
                    Log.i(TAG, "Row = " + Row);
                    Log.i(TAG, "line = " + line);
                    fingerRaw.add(line);
                }
            }

            Log.i(TAG, "有几行指纹1:" + fingerRaw.size());

            for (String data : fingerRaw) {
                Log.i(TAG, "指纹数据1:  " + data);
            }

            for (int i = 0; i < fingerRaw.size(); i++) {
                if (fingerRaw.get(i).length() != 1024) {
                    //清除指纹数据不为1024的数据
                    fingerRaw.remove(i);
                }
            }

            Log.i(TAG, "有几行指纹2:" + fingerRaw.size());

            for (String data : fingerRaw) {
                Log.i(TAG, "指纹数据2:  " + data);
            }
            bufferedReader.close();
            parentActivity.updateMsg("fg.txt has " + fingerRaw.size() + " fp!\nread fp data time: " + (System.currentTimeMillis() - startTime) + " ms");

        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
            parentActivity.updateMsg(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
                        parentActivity.updateMsg(e.toString());

        }
    }


    public void downCharToJRA() {
        long time = System.currentTimeMillis();
        parentActivity.updateMsg("start Time = " + time);
        if (PS_OK != parentActivity.jraApi.PSEmpty()) {
            parentActivity.updateMsg("Failed to clear fingerprint library");
        }
        parentActivity.updateMsg("Clear the fingerprint database successfully, start uploading the fingerprint library to the module!");

        int[] id=new int[1];


        for (int i = 0; i < fingerRaw.size(); i++) {
            //pageId由你决定，和录入一样的,这个ID由你决定，这里必须存储，不然将搜索不到
            if (parentActivity.jraApi.PSDownCharToJRA(DataUtils.hexStringTobyte(fingerRaw.get(i)),id) != PS_OK) {
                parentActivity.updateMsg("Failed to store template");
                return;
            } else {
                parentActivity.updateMsg("The storage template is successful, and the id is returned:"+id[0]);
            }

            if (i == fingerRaw.size() - 1) {
                parentActivity.updateMsg("fingerRaw.size() = " + fingerRaw.size());
                long endTime = System.currentTimeMillis();
                parentActivity.updateMsg("downChar time = " + (endTime - time) + " ms");
                MyApplication.getApp().cancleProgressDialog();
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.cr30a, R.id.up})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cr30a:

                readFromFpTxt();
                break;
            case R.id.up:

                MyApplication.getApp().showProgressDialog(parentActivity, "down...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downCharToJRA();
                    }
                }).start();
                break;
        }
    }


}