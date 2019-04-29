package com.cw.demo.ISO_15693;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.cw.demo.R;

import java.util.ArrayList;

/**
 * Created by 金宇凡 on 2019/3/11.
 */
public class ISOShowView extends RelativeLayout {
    private Context mContxet;

    private AppCompatTextView tvM1Type;//iso卡名称、id
    private AppCompatSpinner spM1Blocknum;//选择区块
    private AppCompatEditText tvData;//写入数据
    private AppCompatButton btnWrite;//写入按键

    private ArrayList<Integer> blockDatas = new ArrayList<Integer>();//区块统计
    private int selectedItemPosition = -1;//记录选择区块

    public ISOShowView(Context context) {
        super(context);
        initView(context);
    }

    public ISOShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ISOShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        mContxet = context;
        View view = LayoutInflater.from(context).inflate(R.layout.iso_show_view, null);
        addView(view);

        tvM1Type = (AppCompatTextView) view.findViewById(R.id.tv_m1_type);
        spM1Blocknum = (AppCompatSpinner) view.findViewById(R.id.sp_m1_blocknum);
        tvData = (AppCompatEditText) view.findViewById(R.id.tv_data);
        btnWrite = (AppCompatButton) view.findViewById(R.id.btn_write);


        initListener();
    }

    /**
     * 监听
     */
    private void initListener() {
        //写入回调
        btnWrite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnWriterListener) {
                    mOnWriterListener.onWriter(getBlocknum(), getWriteData());
                }
            }
        });

        //记录选择区块
        spM1Blocknum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //点击软键盘外部，收起软键盘
        tvData.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager manager = ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null)
                        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }

    /**
     * 读取显示
     *
     * @param id        标题或卡片ID
     * @param totle     区块总数量
     * @param blockSize block的长度
     */
    public void showView(String id, int totle, int blockSize) {
        tvM1Type.setText(id);

        if (blockSize > 0) {
            tvData.setMaxEms(blockSize);
        }

        if (totle <= 0) {
            return;
        }
        blockDatas.clear();
        for (int i = 0; i < totle; i++) {
            blockDatas.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(mContxet, android.R.layout.simple_spinner_item, blockDatas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spM1Blocknum.setAdapter(adapter);

        if (selectedItemPosition <= totle && selectedItemPosition >= 0) {
            spM1Blocknum.setSelection(selectedItemPosition);
        } else {
            spM1Blocknum.setSelection(0);
            selectedItemPosition = 0;
        }
        selectedItemPosition = spM1Blocknum.getSelectedItemPosition();

    }

    /**
     * 获取选择区块
     */
    public int getBlocknum() {
        if (spM1Blocknum.getSelectedItemPosition() == -1) {
            spM1Blocknum.setSelection(0);
        }
        return spM1Blocknum.getSelectedItemPosition();
    }

    /**
     * 获取写入信息
     */
    public String getWriteData() {
        return tvData.getText().toString();
    }

    private OnWriterListener mOnWriterListener = null;

    public void setOnWriterListener(OnWriterListener onWriterListener) {
        mOnWriterListener = onWriterListener;
    }

    /**
     * 写入回调
     */
    interface OnWriterListener {
        void onWriter(int blockNum, String writeData);
    }

}
