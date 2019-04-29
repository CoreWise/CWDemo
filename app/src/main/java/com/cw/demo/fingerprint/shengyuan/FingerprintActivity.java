package com.cw.demo.fingerprint.shengyuan;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.cw.demo.R;
import com.cw.fingersysdk.FingerprintAPI;


import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FingerprintActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "CoreWiseFingerprintActivity";

    private String[] m;

    private AsyncFingerprint asyncFingerprint;

    private Spinner spinner;

    private ArrayAdapter<String> adapter;

    private Button register, validate, register2, validate2, clear, calibration, back, register3, save, compares,
            dbclear;

    private EditText ID;

    private ImageView fingerprintImage;

    private ProgressDialog progressDialog;

    private byte[] model;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AsyncFingerprint.SHOW_PROGRESSDIALOG:
                    cancleProgressDialog();
                    showProgressDialog((Integer) msg.obj);
                    break;

                case AsyncFingerprint.SHOW_FINGER_IMAGE:
                    // imageNum++;
                    // upfail.setText("上传成功：" + imageNum + "\n" + EAR"上传失败：" +
                    // failTime+ "\n" + "解析出错：" + missPacket);
                    showFingerImage(msg.arg1, (byte[]) msg.obj);
                    break;

                case AsyncFingerprint.SHOW_FINGER_MODEL:
                    FingerprintActivity.this.model = (byte[]) msg.obj;
                    if (FingerprintActivity.this.model != null) {
                        Log.i(TAG, "#################model.length=" + FingerprintActivity.this.model.length);
                    }
                    cancleProgressDialog();
                    // ToastUtil.showToast(FingerprintActivity.this,
                    // "pageId="+msg.arg1+" store="+msg.arg2);
                    break;

                case AsyncFingerprint.REGISTER_SUCCESS:
                    cancleProgressDialog();
                    if (msg.obj != null) {
                        Integer id = (Integer) msg.obj;


                        Toast.makeText(FingerprintActivity.this, getString(R.string.fp_register_success) + "  pageId=" + id, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FingerprintActivity.this, R.string.fp_register_success, Toast.LENGTH_SHORT).show();
                    }

                    break;
                case AsyncFingerprint.REGISTER_FAIL:
                    cancleProgressDialog();
                    Toast.makeText(FingerprintActivity.this, R.string.fp_register_fail, Toast.LENGTH_SHORT).show();
                    break;
                case AsyncFingerprint.VALIDATE_RESULT1:
                    cancleProgressDialog();
                    showValidateResult((Boolean) msg.obj);
                    break;
                case AsyncFingerprint.VALIDATE_RESULT2:
                    cancleProgressDialog();
                    Integer r = (Integer) msg.obj;
                    if (r != -1) {
                        Toast.makeText(FingerprintActivity.this, getString(R.string.fp_verifying_through) + "  pageId=" + r, Toast.LENGTH_SHORT).show();
                    } else {
                        showValidateResult(false);
                    }
                    break;
                case AsyncFingerprint.UP_IMAGE_RESULT:
                    cancleProgressDialog();
                    Toast.makeText(FingerprintActivity.this, (Integer) msg.obj, Toast.LENGTH_SHORT).show();
                    // failTime++;
                    // upfail.setText("上传成功：" + imageNum + "\n" + "上传失败：" +
                    // failTime+ "\n" + "解析出错：" + missPacket);
                    break;
                case AsyncFingerprint.VERIFYMY:
                    cancleProgressDialog();
                    Toast.makeText(FingerprintActivity.this, msg.obj + "", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };

    private void showValidateResult(boolean matchResult) {
        if (matchResult) {
            Toast.makeText(FingerprintActivity.this, R.string.fp_verifying_through, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(FingerprintActivity.this, R.string.fp_verifying_fail, Toast.LENGTH_SHORT).show();
        }
    }

    private void showFingerImage(int fingerType, byte[] data) {
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        // saveImage(data);
        fingerprintImage.setBackgroundDrawable(new BitmapDrawable(image));
        writeToFile(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_sy);
        setTitle("SY指纹");
        initView();
        initViewListener();
        initData();
    }

    private void initView() {
        spinner = findViewById(R.id.spinner);
        register = findViewById(R.id.register);
        validate = findViewById(R.id.validate);
        register2 = findViewById(R.id.register2);
        validate2 = findViewById(R.id.validate2);
        clear = findViewById(R.id.clear_flash);
        calibration = findViewById(R.id.calibration);
        back = findViewById(R.id.backRegister);
        register3 = findViewById(R.id.bt_registe);
        register3.setOnClickListener(this);
        save = findViewById(R.id.bt_save);
        save.setOnClickListener(this);
        compares = findViewById(R.id.bt_compares);
        compares.setOnClickListener(this);
        dbclear = findViewById(R.id.bt_clear);
        dbclear.setOnClickListener(this);
        ID = findViewById(R.id.et_fingerId);
        fingerprintImage = findViewById(R.id.fingerprintImage);
        Button bt1 = findViewById(R.id.btn1_my);
        bt1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncFingerprint.verifyMy();
            }
        });
    }

    private String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    private void writeToFile(byte[] data) {
        String dir = rootPath + "/fingerprint_image";
        File dirPath = new File(dir);
        if (!dirPath.exists()) {
            dirPath.mkdir();
        }

        String filePath = dir + "/" + System.currentTimeMillis() + ".bmp";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initData() {
        m = this.getResources().getStringArray(R.array.fingerprint_size);

        // 将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m);

        // 设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 将adapter 添加到spinner中
        spinner.setAdapter(adapter);

        // 添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());

    }

    // 使用数组形式操作
    class SpinnerSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
            Log.i(TAG, "position=" + position);
            switch (position) {
                case 0:
                    asyncFingerprint.setFingerprintType(FingerprintAPI.SMALL_FINGERPRINT_SIZE);
                    break;
                case 1:
                    asyncFingerprint.setFingerprintType(FingerprintAPI.BIG_FINGERPRINT_SIZE);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }

    private void initData2() {
        asyncFingerprint = new AsyncFingerprint(getMainLooper(), mHandler);

        asyncFingerprint.setOnEmptyListener(new AsyncFingerprint.OnEmptyListener() {

            @Override
            public void onEmptySuccess() {
                Toast.makeText(FingerprintActivity.this, R.string.fp_clear_flash_success, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onEmptyFail() {
                Toast.makeText(FingerprintActivity.this, R.string.fp_clear_flash_fail, Toast.LENGTH_SHORT).show();

            }
        });

        asyncFingerprint.setOnCalibrationListener(new AsyncFingerprint.OnCalibrationListener() {

            @Override
            public void onCalibrationSuccess() {
                Log.i(TAG, "onCalibrationSuccess");
                Toast.makeText(FingerprintActivity.this, R.string.fp_calibration_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCalibrationFail() {
                Log.i(TAG, "onCalibrationFail");
                Toast.makeText(FingerprintActivity.this, R.string.fp_calibration_fail, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void initViewListener() {
        register.setOnClickListener(this);
        validate.setOnClickListener(this);
        register2.setOnClickListener(this);
        validate2.setOnClickListener(this);
        calibration.setOnClickListener(this);
        clear.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                asyncFingerprint.setStop(false);
                asyncFingerprint.register();
                break;
            case R.id.validate:
                if (model != null) {
                    asyncFingerprint.validate(model);
                } else {
                    Toast.makeText(FingerprintActivity.this, R.string.fp_first_register, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.register2:
                asyncFingerprint.register2();
                break;
            case R.id.validate2:
                asyncFingerprint.validate2();
                break;
            case R.id.calibration:
                Log.i(TAG, "calibration start");
                asyncFingerprint.PS_Calibration();
                break;
            case R.id.clear_flash:
                asyncFingerprint.PS_Empty();
                break;
            case R.id.backRegister:
                finish();
                break;
            case R.id.bt_registe:
                asyncFingerprint.setStop(false);
                asyncFingerprint.register();
                break;
            case R.id.bt_save:
                FingerModels fingerModels = new FingerModels();
                String id = ID.getText().toString();
                if (!TextUtils.isEmpty(id)) {
                    fingerModels.setId(id);
                    fingerModels.setModel(model);
                    fingerModels.save();
                    if (fingerModels.save()) {
                        Toast.makeText(getApplicationContext(), "save success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "save fail", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.fp_noID, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_compares:
                if (TextUtils.isEmpty(ID.getText())) {
                    Toast.makeText(getApplicationContext(), R.string.fp_noID, Toast.LENGTH_SHORT).show();
                } else {
                    List<FingerModels> list = DataSupport.select("model").where("model_ID = ?", ID.getText().toString())
                            .find(FingerModels.class);
                    if (list.size() > 0) {
                        asyncFingerprint.validate(list.get(0).getModel());
                    } else {
                        Toast.makeText(getApplicationContext(), "no model", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.bt_clear:
                DataSupport.deleteAll(FingerModels.class);
                Toast.makeText(FingerprintActivity.this, R.string.fp_clear_flash_success, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void showProgressDialog(int resId) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(resId));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    asyncFingerprint.setStop(true);
                }
                return false;
            }
        });
        progressDialog.show();
    }

    private void cancleProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
            progressDialog = null;
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

    @Override
    protected void onDestroy() {
        cancleProgressDialog();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData2();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancleProgressDialog();
        asyncFingerprint.setStop(true);
        Log.i(TAG, "onPause");
    }


}