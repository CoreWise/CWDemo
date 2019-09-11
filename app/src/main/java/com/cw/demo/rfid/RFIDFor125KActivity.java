package com.cw.demo.rfid;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.cw.demo.R;
import com.cw.rfidfor125k.RFIDFor125KAPI;
import com.cw.serialportsdk.utils.DataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：李阳
 * 时间：2019/9/10
 * 描述：
 */
public class RFIDFor125KActivity extends AppCompatActivity {


    private static final String TAG = "cwRFIDFor125KActivity";

    RFIDFor125KAPI rfidFor125KAPI;
    @BindView(R.id.iv_rfid)
    AppCompatImageView ivRfid;
    @BindView(R.id.tv_result)
    AppCompatTextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_125krfid);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);

        setTitle("125KRFID演示Demo");

        rfidFor125KAPI = new RFIDFor125KAPI();

        rfidFor125KAPI.setOnResultListenner(new RFIDFor125KAPI.OnResultListenner() {
            @Override
            public void onReceive(final byte[] result) {
                Log.i(TAG, "result: " + DataUtils.bytesToHexString(result));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvResult.setText(DataUtils.bytesToHexString(result));
                    }
                });
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        rfidFor125KAPI.openRFID125K();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();
        rfidFor125KAPI.closeRFID125K();

    }

    @OnClick({R.id.iv_rfid, R.id.tv_result})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_rfid:
                break;
            case R.id.tv_result:
                break;
        }
    }
}
