package com.cw.demo.UHF.R2000UHF.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.Toast;

import com.cw.demo.R;
import com.cw.demo.UHF.R2000UHF.UHF2000Activity;


/**
 * 时间：2019/4/26
 * 描述：标签测试
 */
public class TagTestFragment extends Fragment {


    private Context mContext;

    private TableRow mInventoryReal6CRow;
    private TableRow mInventoryReal6BRow;
    private TableRow mInventoryBufferRow;
    private TableRow mInventoryFast4antRow;
    private TableRow mAccessTag6CRow;
    private TableRow mAccessTag6BRow;

    private View rootView;

    UHF2000Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity= (UHF2000Activity) getActivity();
        rootView = inflater.inflate(R.layout.r2000uhf_tag, null);
        initSettingView();
        return rootView;
    }


    private void initSettingView() {

        mInventoryReal6CRow = (TableRow) rootView.findViewById(R.id.table_row_inventory_real);
        mInventoryReal6CRow.setOnClickListener(setTagOnClickListener);

        mInventoryBufferRow = (TableRow) rootView.findViewById(R.id.table_row_inventory_buffer);
        mInventoryBufferRow.setOnClickListener(setTagOnClickListener);

        mInventoryFast4antRow = (TableRow) rootView.findViewById(R.id.table_row_inventory_fast4ant);
        mInventoryFast4antRow.setOnClickListener(setTagOnClickListener);

        mInventoryFast4antRow = (TableRow) rootView.findViewById(R.id.table_row_inventory_fast4ant);
        mInventoryFast4antRow.setOnClickListener(setTagOnClickListener);

        mInventoryReal6BRow = (TableRow) rootView.findViewById(R.id.table_row_inventory_real_6b);
        mInventoryReal6BRow.setOnClickListener(setTagOnClickListener);

        mAccessTag6CRow = (TableRow) rootView.findViewById(R.id.table_row_access_tag_6c);
        mAccessTag6CRow.setOnClickListener(setTagOnClickListener);
        mAccessTag6BRow = (TableRow) rootView.findViewById(R.id.table_row_access_tag_6b);
        mAccessTag6BRow.setOnClickListener(setTagOnClickListener);

    }


    private View.OnClickListener setTagOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {

            switch (arg0.getId()) {
                case R.id.table_row_inventory_real:
                    //intent = new Intent().setClass(mContext, PageInventoryReal.class);
                    activity.toFragmentBackStack(new PageInventoryReal());
                    break;
                case R.id.table_row_inventory_buffer:
                    //intent = new Intent().setClass(mContext, PageInventoryBuffer.class);
                    Toast.makeText(activity, R.string.r2000_uhf_support, Toast.LENGTH_SHORT).show();

                    return;
                case R.id.table_row_inventory_fast4ant:
                    //intent = new Intent().setClass(mContext, PageInventoryFast.class);
                    Toast.makeText(activity, R.string.r2000_uhf_support, Toast.LENGTH_SHORT).show();
                    return;
                case R.id.table_row_access_tag_6c:
                    //intent = new Intent().setClass(mContext, PageTagAccess.class);
                    //Toast.makeText(mContext, "暂不支持，敬请期待！", Toast.LENGTH_SHORT).show();
                    activity.toFragmentBackStack(new PageTagAccess());
                    break;
                case R.id.table_row_access_tag_6b:
                    //intent = new Intent().setClass(mContext, PageTag6BAccess.class);
                    Toast.makeText(activity, R.string.r2000_uhf_support, Toast.LENGTH_SHORT).show();

                    return;
                case R.id.table_row_inventory_real_6b:
                    //intent = new Intent().setClass(mContext, PageInventoryReal6B.class);
                    Toast.makeText(activity, R.string.r2000_uhf_support, Toast.LENGTH_SHORT).show();
                    return;
                default:

                    break;
            }
            activity.commitFragment();

        }
    };


}
