package com.cw.demo.idcard;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cw.demo.MyApplication;
import com.cw.demo.R;
import com.cw.fpgaasdk.IDCardIdentify;
import com.cw.fpgaasdk.USBFingerManager;
import com.cw.idcardsdk.AsyncParseSFZ;
import com.cw.idcardsdk.ParseSFZAPI;
import com.cw.serialportsdk.cw;
import com.fm.bio.ID_Fpr;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：李阳
 * 时间：2019/5/28
 * 描述：GAA指纹不能在插着USB数据线时使用
 */
public class ComparisonActivity extends AppCompatActivity {


    private static final String TAG = "ComparisonActivity";


    @BindView(R.id.read_sfz)
    Button readSfz;

    @BindView(R.id.verial)
    Button verial;


    String liyangAratek1 = "1b840df8801e007e004011060414200e03854020160f78c0000d0765c9c0010b7ae400120768f0e0050b8d0ce11a081021c11506dd2da100060489e10e0681e841120769f0810907ea2d820a0a7068820c0564b7820b11d0c50200026bff020f1a4f0c63050a5133c30c16284b23160b2882031127478ca301092b9d83190d3ccc23060832ef830a09c94f4412693b5de4030947e0a41a096538c51613022e75c30fa74104120101af37b0b0b1b6b3b3383437b3b130aeb02db9adb7485ba852a9a6cda0503d6e6c6957665259584e5e3d32362e231d420f2b0e1a05382728191c373e3fae35acadb3b2b3ab313833abb332aeb536b6bac6260eb637bfcac5b4453a88868171806b746c6578574b51483e37562a3f2723184524311f01382724011b";
    String liyangAratek2 = "1b8510c8801b006b0040060a7e04e01207103ce016058c45401b0705a1400f056cf1400a078100c11208eb41c10a0a7480e10c0665cba10b11d1d50100036902620e16511c4205095147c20c152b6b22160d1c732210584b986201092ec142190d3ed4c205093307430a0ad16b83116b3f714303093b7d631c0947008419091349840e0f6458a415136bc4e41411023181c20faa6163110101b0b43ab636b0b42fabaf2bb6aab44839a452a9a4ca9c5253a4423f58504d553c36312c221c42102b0e19023926291b1d3a3b3d393f4bb2af34ae34b3ad33adb736b9bac9261ab334c0cbc1b04046af3d3d746b666b58534c483e37552d3f28241e4425311e01382c261f232e011c";
    String liyangAratek3 = "1b971d888125008a01c012040964600b06896dc01207848d40110716cd001604dbf8a001057cf8800c085b5c61060d1b810114061a80c11809d69d61040652ddc1000790e5811207560a62091b4e2262060e2564c219072989a2160a31b6820c18bdb882040643bf82050837c3a20a0e34d60207084aed020008a5ffc2100a32114314084014030304b123830702a36aa30e17af73830f1933868308093e9c43170b3ba983020a151be40b12602a2412142a41040707a1c4a4060570d0841014022e75620ca4b9c30e0101ad31b1b33e35b0ba3f4935adc0bab252518129a195a99be05b9d1f06e7915597a045971e3c5242454041402f2a2b3a29321e121a352908201c0b183318252a1d1f21253937343b3d4d4da72fa9ab313dadc12c3641bda5c8c8362d014cc8e3d8b59429bf558789c637b7bc37b3303c786b6a65616658524a514f513c3a3d3d31213430232a40181e33220a091922311013232e230129";
    String liyang = "43018c12010b5400000000000000000000000021019a90e600ffffffffffffa21a54fec3239dfc4231c8fc6343bffc9b44a6fc3c4e14fcc154a3fc255ed4fcdd6749fe9f7454fe7582bafcb48fa0fc7a9564fe87a3b1fc23a927fc72b5cafc45bfe4fc8dc7c2fc0cd0eefc75d7e9fc21daeffc95e338fec7e923fe41edfdfc63fb0dfedf011fff98116bfd28150bfff62e07ff133912ffd84debfdb553cffd7d574fff00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000c5000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003843018c1201105600000000000000000000000027019abac600ffffffffffff7419e0fc8b1f1ffcc420bffcd02b60fea63814fc673a34fce74aacfc4651e9fc7e6138fc896e2afcda6f59feab7116fc988a1bfcbc8a00fcd88a50fe5d95ddfc429c38fc1f9eebfc4da333fc5ba4eefccfaa49fe58bb3ffc88c437fcbfc903fe56ccfafcdccb1cfec8d00dfe26d5ecfc49e646fc41e8f9fcefec0ffe6aeffafc54fa3efc32063cfd680645fd0e342efd873b34fdde41ecfd324c26fd00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000c90000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000082";


    byte[] idCardFpRaw;


    float fpThreshold;


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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_idcard_comparison);
        ButterKnife.bind(this);

        setTitle("GAA指纹和身份证指纹比对Demo");

        asyncParseSFZ = new AsyncParseSFZ(getMainLooper(), this);

        asyncParseSFZ.setOnReadSFZListener(new AsyncParseSFZ.OnReadSFZListener() {
            @Override
            public void onReadSuccess(ParseSFZAPI.People people) {
                // tvSfzModle.setText(people.toString());
                updateInfo(people);
            }

            @Override
            public void onReadFail(int i) {
                Toast.makeText(ComparisonActivity.this, "读身份证错误码 " + i, Toast.LENGTH_SHORT).show();
            }
        });
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

    @OnClick({R.id.read_sfz, R.id.verial})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.read_sfz:
                asyncParseSFZ.readSFZ();
                break;
            case R.id.verial:

                byte[] ddd = new byte[512];
                System.arraycopy(idCardFpRaw, 512, ddd, 0, 512);

                boolean b = IDCardIdentify.getInstance().idcardIdentifyWithGAA(mLiveScan, idCardFpRaw);


                Log.i(TAG, "-----" + b);

                Toast.makeText(this, "比对结果:" + b, Toast.LENGTH_SHORT).show();

                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "------------onStart--------------");
        asyncParseSFZ.openIDCardSerialPort(cw.getDeviceModel());

        open();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "------------onStop--------------");

        close();
        asyncParseSFZ.closeIDCardSerialPort(cw.getDeviceModel());

    }

    private void open() {


        MyApplication.getApp().showProgressDialog(this, "指纹模块初始化中...");

        USBFingerManager.getInstance(this).setDelayMs(1500);

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
                verial.setEnabled(false);
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


    private void updateInfo(ParseSFZAPI.People people) {


        sfzAddress.setText(people.getPeopleAddress());
        sfzDay.setText(people.getPeopleBirthday().substring(6));
        sfzId.setText(people.getPeopleIDCode());
        sfzMouth.setText(people.getPeopleBirthday().substring(4, 6));
        sfzName.setText(people.getPeopleName());
        sfzNation.setText(people.getPeopleNation());
        sfzSex.setText(people.getPeopleSex());
        sfzYear.setText(people.getPeopleBirthday().substring(0, 4));

        if (people.getPhoto() != null) {
            Bitmap photo = BitmapFactory.decodeByteArray(people.getPhoto(), 0, people.getPhoto().length);
            sfzPhoto.setBackgroundDrawable(new BitmapDrawable(photo));
        }

        if (people.getModel() != null) {
            idCardFpRaw = people.getModel();
        } else {
            Toast.makeText(this, "该身份证无指纹信息!", Toast.LENGTH_SHORT).show();
        }
    }


}
