package com.cw.demo.m1;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.cw.demo.R;
import com.cw.m1rfidsdk.AsyncM1Card;
import com.cw.m1rfidsdk.M1CardAPI;
import com.cw.serialportsdk.cw;


/**
 * M1卡
 */
public class RFIDM1Activity extends AppCompatActivity implements OnClickListener {

    private static final String[] cardtype = {"S50", "S70"};
    private static final String[] pwdtype = {"KEYA", "KEYB"};
    private static final int[] keyType = {M1CardAPI.KEY_A, M1CardAPI.KEY_B};
    private static final int[] modelType = {M1CardAPI.S50, M1CardAPI.S70};
    private Spinner mSpinnerCardType, mSpinnerPwdType;
    private ArrayAdapter<String> mAdapterCardType, mAdapterPwdType;
    private static int MODEL = 1;//型号选择
    private static int mKeyType = M1CardAPI.KEY_A;//密码选择
//    private static int NUM = 1;//次数
    private String DefaultKeyA = "ffffffffffff";// 默认密码A
    private String DefaultKeyB = "ffffffffffff";// 默认密码B
    private Button mBtnGetCardNum, mBtnSendPwd, mBtnValidPwd, mBtnWriteData,
            mBtnReadData, mBtnUpdate;
    private EditText mEdShowCard, mEdPwdA, mEdPwdB, mBlockNum, mEdWriteData,
            mReadData, mEdWritePwdA, mEdWritePwdB;
    private TextView mTips;
    private AsyncM1Card reader;
    private ProgressDialog progressDialog;

    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfid_m1);
        setTitle(R.string.m1_title);
        initView();
        initData();

        builder = new AlertDialog.Builder(this);
    }

    private void initView() {
        mSpinnerCardType = findViewById(R.id.spinner_card_type);
        mAdapterCardType = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, cardtype);
        mAdapterCardType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);// 设置下拉列表的风格
        mSpinnerCardType.setSelection(0);
        mSpinnerCardType.setAdapter(mAdapterCardType);
        mSpinnerCardType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                MODEL = modelType[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        mSpinnerPwdType = findViewById(R.id.spinner_pwd_type);
        mAdapterPwdType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pwdtype);
        mAdapterPwdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);// 设置下拉列表的风格
        mSpinnerPwdType.setSelection(0);
        mSpinnerPwdType.setAdapter(mAdapterPwdType);
        mSpinnerPwdType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                mKeyType = keyType[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        mEdShowCard = findViewById(R.id.ed_card_num);
        mEdPwdA = findViewById(R.id.ed_pwd_a);
        mEdPwdB = findViewById(R.id.ed_pwd_b);
        mBlockNum = findViewById(R.id.ed_block_num);
        mEdWriteData = findViewById(R.id.ed_write_block);
        mReadData = findViewById(R.id.ed_read_block);
        mEdWritePwdA = findViewById(R.id.ed_write_pwd_a);
        mEdWritePwdB = findViewById(R.id.ed_write_pwd_b);

        mBtnGetCardNum = findViewById(R.id.btn_getCardNum);
        mBtnSendPwd = findViewById(R.id.btn_sendCardPwd);
        mBtnValidPwd = findViewById(R.id.btn_validatePwd);
        mBtnWriteData = findViewById(R.id.btn_write);
        mBtnReadData = findViewById(R.id.btn_read);
        mBtnUpdate = findViewById(R.id.btn_update);
        mBtnGetCardNum.setOnClickListener(this);
        mBtnSendPwd.setOnClickListener(this);
        mBtnValidPwd.setOnClickListener(this);
        mBtnWriteData.setOnClickListener(this);
        mBtnReadData.setOnClickListener(this);
        mBtnUpdate.setOnClickListener(this);
        mTips = findViewById(R.id.tips);
    }

    private void initData() {
        mEdPwdA.setText(DefaultKeyA);
        mEdPwdB.setText(DefaultKeyB);


        reader = new AsyncM1Card(getMainLooper());


        reader.setOnReadCardNumListener(new AsyncM1Card.OnReadCardNumListener() {

            @Override
            public void onReadCardNumSuccess(String num) {
                Log.d("jokey", "num:" + num);
                mEdShowCard.setText(num);
                mTips.setText(R.string.m1_str_get_success);
                cancleProgressDialog();
            }

            @Override
            public void onReadCardNumFail(int confirmationCode, String errorMsg) {
                mEdShowCard.setText("");
                cancleProgressDialog();
                if (confirmationCode == M1CardAPI.Result.FIND_FAIL) {
                    mTips.setText(R.string.m1_no_card_with_data);
                } else if (confirmationCode == M1CardAPI.Result.TIME_OUT) {
                    mTips.setText(R.string.m1_no_card_without_data);
                } else if (confirmationCode == M1CardAPI.Result.OTHER_EXCEPTION) {
                    mTips.setText(R.string.m1_find_card_exception);
                }
            }
        });

        reader.setOnWriteAtPositionListener(new AsyncM1Card.OnWriteAtPositionListener() {

            @Override
            public void onWriteAtPositionSuccess(String num) {
                cancleProgressDialog();
                mEdShowCard.setText(num);
                mTips.setText(R.string.m1_writing_success);
            }

            @Override
            public void onWriteAtPositionFail(int comfirmationCode) {
                cancleProgressDialog();
                mTips.setText(R.string.m1_writing_fail);
            }
        });
        reader.setOnReadAtPositionListener(new AsyncM1Card.OnReadAtPositionListener() {

            @Override
            public void onReadAtPositionSuccess(String cardNum, String data) {
                cancleProgressDialog();
                mEdShowCard.setText(cardNum);
                if (data != null) {
                    mReadData.setText(data);
                }
                mTips.setText(R.string.m1_reading_success);
            }

            @Override
            public void onReadAtPositionFail(int comfirmationCode) {
                cancleProgressDialog();
                mTips.setText(R.string.m1_reading_fail);
            }
        });
        reader.setOnUpdatePwdListener(new AsyncM1Card.OnUpdatePwdListener() {
            @Override
            public void onUpdatePwdSuccess(String num) {
                cancleProgressDialog();
                mEdShowCard.setText(num);
                mTips.setText(R.string.m1_str_update_pwd_success);
            }

            @Override
            public void onUpdatePwdFail(int comfirmationCode) {
                cancleProgressDialog();
                mTips.setText(R.string.m1_str_update_pwd_failure);
            }
        });
    }

    @Override
    public void onClick(View v) {
        boolean isExit = false;

        if (isExit) {
            return;
        }
        int block;
        String keyA = "";
        String keyB = "";
        String data = "";

        switch (v.getId()) {
            case R.id.btn_getCardNum:
                mEdShowCard.setText("");
                showProgressDialog(R.string.m1_getcard_wait);
                reader.readCardNum();
                break;
            case R.id.btn_sendCardPwd:

                break;
            case R.id.btn_validatePwd:

                break;
            case R.id.btn_write:
                mEdShowCard.setText("");
                mReadData.setText("");
                if (TextUtils.isEmpty(mBlockNum.getText().toString())) {

                    Toast.makeText(this, R.string.m1_str_block_not_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                block = Integer.parseInt(mBlockNum.getText().toString());
                keyA = mEdPwdA.getText().toString();
                keyB = mEdPwdB.getText().toString();
                data = mEdWriteData.getText().toString();
                if (TextUtils.isEmpty(keyA) || TextUtils.isEmpty(keyB)
                        || TextUtils.isEmpty(data)) {

                    Toast.makeText(this, R.string.m1_str_all_not_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (RegexUtils.isCheckPwd(keyA) && RegexUtils.isCheckPwd(keyB)
                        && RegexUtils.isCheckWriteData(data)) {
                    showProgressDialog(R.string.m1_writing_wait);
                    reader.write(block, mKeyType, MODEL, keyA, keyB, data);
                } else {

                    Toast.makeText(this, R.string.m1_str_all_not_validate, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_read:
                //获取指定块数据
                mReadData.setText("");
                mEdShowCard.setText("");
                if (TextUtils.isEmpty(mBlockNum.getText().toString())) {

                    Toast.makeText(this, R.string.m1_str_block_not_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                block = Integer.parseInt(mBlockNum.getText().toString());


                //S50
                if (mSpinnerCardType.getSelectedItemPosition() == 0) {
                    if (block > 64) {

                        Toast.makeText(this, "块号范围（0 ~ 63）！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (block > 255) {

                        Toast.makeText(this, "块号范围（0 ~ 255）！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                keyA = mEdPwdA.getText().toString();
                keyB = mEdPwdB.getText().toString();

                if (TextUtils.isEmpty(keyA) || TextUtils.isEmpty(keyB)) {

                    Toast.makeText(this, R.string.m1_str_not_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressDialog(R.string.m1_reading_wait);
                reader.read(block, mKeyType, MODEL, keyA, keyB);
                break;
            case R.id.btn_update:


                mReadData.setText("");
                mEdShowCard.setText("");

                // 另外块号需要校验
                if (TextUtils.isEmpty(mBlockNum.getText().toString())) {

                    Toast.makeText(this, R.string.m1_str_block_not_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO: 2020/12/2 块号合法性检测

                final int block2 = Integer.parseInt(mBlockNum.getText().toString());
                final String keyA2 = mEdPwdA.getText().toString();
                final String keyB2 = mEdPwdB.getText().toString();
                final String dataA2 = mEdWritePwdA.getText().toString();
                final String dataB2 = mEdWritePwdB.getText().toString();

                if (TextUtils.isEmpty(keyA2) || TextUtils.isEmpty(keyB2) || TextUtils.isEmpty(dataA2) || TextUtils.isEmpty(dataB2)) {
                    Toast.makeText(this, R.string.m1_str_all_not_empty, Toast.LENGTH_SHORT).show();
                    return;
                }


                builder.setTitle("危险操作！");

                builder.setMessage("更改密码操作,请记录相应块号、扇区以及密码");

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setPositiveButton("确定修改密码", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressDialog(R.string.m1_updatepwding_wait);
                        reader.updatePwd(block2, mKeyType, MODEL, keyA2, keyB2, dataA2, dataB2);
                    }
                });


                if (RegexUtils.isCheckPwd(keyA2) && RegexUtils.isCheckPwd(keyB2) && RegexUtils.isCheckPwd(dataA2) && RegexUtils.isCheckPwd(dataB2)) {
                    //showProgressDialog(R.string.updatepwding_wait);
                    //reader.updatePwd(block, mKeyType, NUM, keyA, keyB, data);
                    builder.show();

                } else {

                    Toast.makeText(this, R.string.m1_str_all_not_validate, Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }

    private void showProgressDialog(int resId) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(resId));
        progressDialog.show();
    }

    private void cancleProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
            progressDialog = null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        reader.openM1RFIDSerialPort(cw.getDeviceModel());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();
        reader.closeM1RFIDSerialPort(cw.getDeviceModel());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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