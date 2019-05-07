package com.cw.demo.idcard_nfclocal;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.cw.demo.R;
import com.cw.localsfz.NFCReadLocalIDCardActivity;
import com.cw.localsfz.bean.PeopleBean;
import com.cw.serialportsdk.utils.DataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IDCard_NFC_Local_DemoActivity extends NFCReadLocalIDCardActivity {

    private static final String TAG = "MainActivity";


    @BindView(R.id.sfz_name)
    TextView sfzName;
    @BindView(R.id.sfz_mouth)
    TextView sfzMouth;
    @BindView(R.id.sfz_sex)
    TextView sfzSex;
    @BindView(R.id.sfz_nation)
    TextView sfzNation;
    @BindView(R.id.sfz_day)
    TextView sfzDay;
    @BindView(R.id.sfz_id)
    TextView sfzId;
    @BindView(R.id.sfz_year)
    TextView sfzYear;
    @BindView(R.id.sfz_address)
    TextView sfzAddress;
    @BindView(R.id.sfz_photo)
    ImageView sfzPhoto;
    @BindView(R.id.tv_read_time)
    TextView tvReadTime;
    @BindView(R.id.tv_fingerData)
    TextView tvFingerData;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    private int sum = 0, success = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_idcard_nfc_local);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ButterKnife.bind(this);

        //setIDCardBmpStorePath("/sdcard/sfzpic2/");

        tvVersion.setText(getSDKVersion());

    }

    @Override
    protected void onReadIDCardStart() {
        clear();
        sum++;
    }

    @Override
    protected void onReadIDCardSuccess(PeopleBean peopleBean, long readTime) {
        if (peopleBean == null) {
            return;
        }

        Log.i(TAG, peopleBean.toString());
        Log.i(TAG, "读卡时间:" + readTime);
        updateInfo(peopleBean);
        success++;
        tvReadTime.setText("读卡时间:" + readTime + " ms  总数: " + sum + "  成功: " + success);
    }

    @Override
    protected void onReadIDCardFailure(String errorCode) {
        Log.e(TAG, errorCode);
        Toast.makeText(this, "error:  " + errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onReadIDCardUID(byte[] uid) {
        Log.d(TAG, DataUtils.toHexString(uid));
    }


    /**
     * 清空数据
     */
    private void clear() {

        tvReadTime.setText("");
        sfzName.setText("");
        sfzSex.setText("");
        sfzNation.setText("");
        sfzYear.setText("");
        sfzMouth.setText("");
        sfzDay.setText("");
        sfzAddress.setText("");
        sfzId.setText("");
        tvFingerData.setText("");
        sfzPhoto.setBackgroundColor(0);

    }

    /**
     * 更新数据
     *
     * @param people
     */
    private void updateInfo(PeopleBean people) {
        sfzName.setText(people.getPeopleName());
        sfzNation.setText(people.getPeopleNation());
        sfzSex.setText(people.getPeopleSex());
        sfzYear.setText(people.getPeopleBirthday().substring(0, 4));
        sfzMouth.setText(people.getPeopleBirthday().substring(4, 6));
        sfzDay.setText(people.getPeopleBirthday().substring(6, 8));
        sfzAddress.setText(people.getPeopleAddress());
        sfzId.setText(people.getPeopleIDCode());


        if (people.getPhoto() != null) {
            Bitmap photo = BitmapFactory.decodeByteArray(people.getPhoto(), 0, people.getPhoto().length);
            sfzPhoto.setBackgroundDrawable(new BitmapDrawable(photo));
        }

        if (people.getModel() != null) {
            tvFingerData.setText(DataUtils.toHexString(people.getModel()));
        } else {
            tvFingerData.setText("该身份证属于二代证 , 无指纹信息 !");
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
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
        }
        return super.onKeyDown(keyCode, event);
    }

}
