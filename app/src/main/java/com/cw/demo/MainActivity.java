package com.cw.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import com.cw.demo.ISO_15693.NFCISO15693Activity;
import com.cw.demo.UHF.R2000UHF.UHF2000Activity;
import com.cw.demo.UHF.rbm550uhf.RBUHFActivity;
import com.cw.demo.barcode.BarCodeActivity;
import com.cw.demo.beidou.BeiDouActivity;
import com.cw.demo.fingerprint.gaa.NewFpGAAActivity;
import com.cw.demo.fingerprint.gab.FpGABActivity;
import com.cw.demo.fingerprint.jra.JRAActivity;
import com.cw.demo.UHF.hxuhf.HXUHFActivity;
import com.cw.demo.idcard.ComparisonActivity;
import com.cw.demo.idcard.IDCardActivity;
import com.cw.demo.m1.NFCM1Activity;
import com.cw.demo.m1.RFIDM1Activity;
import com.cw.demo.rfid.RFIDFor125KActivity;
import com.cw.demo.ui.FriendDialog;
import com.cw.demo.utils.BaseUtils;

import com.xuexiang.xupdate.XUpdate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import kr.co.namee.permissiongen.PermissionGen;


/**
 * @author Administrator
 */
public class MainActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "CoreWise" + "MainActivity";

    private GridView gridview;
    private Button set, firmware;
    private Dialog dialog;

    private String[] compatible;
    private String[] keys;
    private List<Intent> mListIntent = new ArrayList<>();
    private int[] icons;

    private SharedPreferences preferences;

    private HashMap<String, Boolean> features = new HashMap<String, Boolean>();
    private ArrayList<Integer> indexList = new ArrayList<Integer>();
    private List<HashMap<String, Object>> menuList = new ArrayList<HashMap<String, Object>>();

    private SimpleAdapter menuAdapter;

    private TextView tvVersion;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
        initData();

        PermissionGen.with(MainActivity.this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.WRITE_SETTINGS)
                .request();

        String mUpdateUrl = "https://raw.githubusercontent.com/CoreWise/CWDemo/master/version.json";

        /*new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(this)
                //更新地址
                .setUpdateUrl(mUpdateUrl)
                //实现httpManager接口的对象
                .setHttpManager(new UpdateAppHttpUtil())
                .setTargetPath("/sdcard/DemoApp/")
                .build()
                .update();
*/
        XUpdate.newBuild(this)
                .updateUrl(mUpdateUrl)
                .apkCacheDir("/sdcard/DemoApp/")
                .update();

    }

    private void initView() {

        tvVersion = findViewById(R.id.tv_version);

        gridview = findViewById(R.id.gridview);

        firmware = findViewById(R.id.firmware);

        firmware.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GetVersionActivity.class));
            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showActivity(((TextView) view.findViewById(R.id.menu_name)).getText().toString());
                Log.i(TAG, "onItemClick  position=" + position);
            }

        });

        set = findViewById(R.id.set_menu);

        set.setOnClickListener(this);

    }

    private void initData() {
        keys = getResources().getStringArray(R.array.general_functions);
        icons = new int[keys.length];
        for (int i = 0; i < keys.length; i++) {
            icons[i] = getResources().obtainTypedArray(R.array.general_icons).getResourceId(i, 0);
        }
        Intent intent = null;

        intent = new Intent(this, BarCodeActivity.class);
        intent.putExtra("tag", "BarCodeActivity");
        mListIntent.add(intent);

        intent = new Intent(this, RFIDM1Activity.class);
        intent.putExtra("tag", "RFIDM1Activity");
        mListIntent.add(intent);

        intent = new Intent(this, RFIDFor125KActivity.class);
        intent.putExtra("tag", "RFIDFor125KActivity");
        mListIntent.add(intent);

        intent = new Intent(this, IDCardActivity.class);
        intent.putExtra("tag", "IDCardActivity");
        mListIntent.add(intent);

        intent = new Intent(this, ComparisonActivity.class);
        intent.putExtra("tag", "ComparisonActivity");
        mListIntent.add(intent);

        intent = new Intent(this, NFCM1Activity.class);
        intent.putExtra("tag", "NFCM1Activity");
        mListIntent.add(intent);

        intent = new Intent(this, NFCISO15693Activity.class);
        intent.putExtra("tag", "NFCISO15693Activity");
        mListIntent.add(intent);

        intent = new Intent(this, HXUHFActivity.class);
        intent.putExtra("tag", "HXUHFActivity");
        mListIntent.add(intent);

        intent = new Intent(this, RBUHFActivity.class);
        intent.putExtra("tag", "RBUHFActivity");
        mListIntent.add(intent);

        intent = new Intent(this, UHF2000Activity.class);
        intent.putExtra("tag", "UHF2000Activity");
        mListIntent.add(intent);

        intent = new Intent(this, NewFpGAAActivity.class);
        intent.putExtra("tag", "NewFpGAAActivity");
//        intent = new Intent(this, FpGAAActivity.class);
//        intent.putExtra("tag", "FpGAAActivity");
        mListIntent.add(intent);

        intent = new Intent(this, FpGABActivity.class);
        intent.putExtra("tag", "FpGABActivity");
        mListIntent.add(intent);

        intent = new Intent(this, JRAActivity.class);
        intent.putExtra("tag", "FpJRAActivity");
        mListIntent.add(intent);

        intent = new Intent(this, BeiDouActivity.class);
        intent.putExtra("tag", "BeiDouActivity");
        mListIntent.add(intent);



        /*icons = new int[]{R.drawable.icon_beidou, R.drawable.m1, R.drawable.icon_barcode, R.drawable.ic, R.drawable.sfz,
                R.drawable.hx, R.drawable.fingerprint, R.drawable.loop, R.drawable.icon_rfid15693, R.drawable.fingerprint,
                R.drawable.cpu, R.drawable.ic, R.drawable.ic, R.drawable.ic, R.drawable.cpu, R.drawable.ic, R.drawable.hx};*/

        preferences = getSharedPreferences("features", MODE_PRIVATE);
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            tvVersion.setText(info.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        getFeatures();
        showFeatures();
    }

    private void showActivity(String str) {
        Intent intent = null;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(str)) {

                intent = mListIntent.get(i);
                break;
            }
        }

//        SharedPreferences mySharedPreferences = getSharedPreferences("SysConfig", Activity.MODE_PRIVATE);

//        Editor editor = mySharedPreferences.edit();


        @SuppressLint("ResourceType") final FriendDialog mFriendDialog;


        if (MyApplication.getApp().isLandscape(this)) {
            mFriendDialog = new FriendDialog(this,
                    BaseUtils.dip2px(this, 160f),
                    BaseUtils.dip2px(this, 30f),
                    R.layout.dialog_friend,
                    R.style.DialogTheme);
        } else {
            mFriendDialog = new FriendDialog(this,
                    BaseUtils.dip2px(this, 80f),
                    BaseUtils.dip2px(this, 100f),
                    R.layout.dialog_friend,
                    R.style.DialogTheme);
            mFriendDialog.setMessageSize(15);
        }


        if (intent == null) {
            return;
        }
        String tag = intent.getStringExtra("tag");

        switch (tag) {

            case "NewFpGAAActivity":
                //GAA指纹
                mFriendDialog.setMessage(getResources().getString(R.string.general_usb_fp_tips));

                final Intent finalIntent = intent;
                mFriendDialog.setOnClickListener(new FriendDialog.onClickListener() {

                    @Override
                    public void OnClickPositive() {
                        startActivity(finalIntent);
                    }

                    @Override
                    public void OnClickNegative() {
                        mFriendDialog.dismiss();
                    }
                });
                mFriendDialog.show();
                return;

            case "FpJRAActivity":
                //JRA指纹
                mFriendDialog.setMessage(getResources().getString(R.string.general_usb_fp_tips));

                final Intent finalIntent2 = intent;
                mFriendDialog.setOnClickListener(new FriendDialog.onClickListener() {

                    @Override
                    public void OnClickPositive() {
                        startActivity(finalIntent2);
                    }

                    @Override
                    public void OnClickNegative() {
                        mFriendDialog.dismiss();
                    }
                });
                mFriendDialog.show();
                return;

            case "FpGABActivity":
                //GAB指纹
                mFriendDialog.setMessage(getResources().getString(R.string.general_usb_fp_tips));

                final Intent finalIntent3 = intent;
                mFriendDialog.setOnClickListener(new FriendDialog.onClickListener() {

                    @Override
                    public void OnClickPositive() {
                        startActivity(finalIntent3);
                    }

                    @Override
                    public void OnClickNegative() {
                        mFriendDialog.dismiss();
                    }
                });
                mFriendDialog.show();
                return;

            default:
                break;
        }

        startActivity(intent);

       /*
        compatible = getResources().getStringArray(R.array.general_compatible);

        mFriendDialog.setMessage(compatible[position]);
        final Intent finalIntent = intent;

        mFriendDialog.setOnClickListener(new FriendDialog.onClickListener() {
            @Override
            public void OnClickPositive() {
                startActivity(finalIntent);
            }

            @Override
            public void OnClickNegative() {
                mFriendDialog.dismiss();
            }
        });

        mFriendDialog.show();
        */


    }

    private void setMenuValue() {
        Editor editor = preferences.edit();
        for (int i = 0; i < keys.length; i++) {
            editor.putBoolean(keys[i], features.get(keys[i]));
        }
        editor.commit();
    }

    private void getFeatures() {

        for (int i = 0; i < keys.length; i++) {
            features.put(keys[i], preferences.getBoolean(keys[i], false));
        }

        indexList.clear();
        for (String key : features.keySet()) {
            Log.i(TAG, key + "=" + features.get(key));
            if (features.get(key)) {
                for (int i = 0; i < keys.length; i++) {
                    if (keys[i].equals(key)) {
                        indexList.add(i);
                        break;
                    }
                }
            }
        }

        Collections.sort(indexList);
        Log.i(TAG, "indexList=" + indexList.size());
        menuList.clear();
        for (Integer value : indexList) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("menuIcon", icons[value]);
            map.put("menuName", keys[value]);
            menuList.add(map);
        }
    }

    private void showFeatures() {
        menuAdapter = new SimpleAdapter(this, menuList, R.layout.main_grid_menu_item, new String[]{"menuIcon", "menuName"},
                new int[]{R.id.menu_icon, R.id.menu_name});
        gridview.setAdapter(menuAdapter);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.set_menu:
                showDialog();
                break;
            default:
                break;
        }
    }

    private void showDialog() {
        dialog = new Dialog(this, R.style.MyDialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.main_menu_dialog, null);
        ListView listView = view.findViewById(R.id.menu_list);
        MyAdapter adapter = new MyAdapter(this);
        adapter.setString(keys);
        listView.setAdapter(adapter);
        Button apply = view.findViewById(R.id.apply_dialog);
        apply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMenuValue();
                getFeatures();
                showFeatures();
                dialog.cancel();
            }
        });
        Button cancle = view.findViewById(R.id.cancle_dialog);
        cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }


    private class MyAdapter extends BaseAdapter {
        private String[] strs;
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public void setString(String[] strs) {
            this.strs = strs;
        }

        @Override
        public int getCount() {
            if (strs == null) {
                return 0;
            }
            return strs.length;
        }

        @Override
        public Object getItem(int position) {
            return strs[position];
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.main_menu_dialog_item, null);
                holder = new ViewHolder();
                holder.name = convertView.findViewById(R.id.feature_name);
                holder.checkBox = convertView.findViewById(R.id.check_box);
                holder.llItem = convertView.findViewById(R.id.item);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.name.setText(strs[position]);

            holder.checkBox.setOnCheckedChangeListener(null);

            if (holder.checkBox.isChecked() != features.get(strs[position])) {
                holder.checkBox.setChecked(features.get(strs[position]));
            }
            Log.i(TAG, "features.get(strs[position])=" + features.get(strs[position]) + "   " + strs[position]);
            holder.checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.i(TAG, "strs[position]=" + isChecked + "   " + strs[position]);

                    if (features.get(strs[position]) != isChecked) {
                        features.put(strs[position], isChecked);
                    }
                }
            });

            holder.llItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean b = !features.get(strs[position]);
                    features.put(strs[position], b);
                    holder.checkBox.setChecked(b);
                }
            });
            return convertView;
        }

        public final class ViewHolder {
            public TextView name;
            public CheckBox checkBox;
            public LinearLayout llItem;

        }
    }


}