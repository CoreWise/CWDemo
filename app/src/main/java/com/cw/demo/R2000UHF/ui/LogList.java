package com.cw.demo.R2000UHF.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;


import com.cw.demo.R;
import com.cw.r2000uhfsdk.base.ERROR;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LogList extends LinearLayout {
    private Context mContext;
    private ListView mLogList;
    private TableRow mLogTextTableRow;
    private ImageView mLogTextImage;
    private TextView mLogInfoText, mLogHelpText;
    private ArrayAdapter<CharSequence> mLogListAdapter;
    private ArrayList<CharSequence> mLogListItem;


    private  final int LEFT = 0;
    private  final int RIGHT = 1;
    private  final int TOP = 2;
    private  final int BOTTOM = 3;


    public LogList(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray s = context.obtainStyledAttributes(attrs, R.styleable.LogList);//从xml那传来的一组值
        int location = s.getInt(R.styleable.LogList_location, BOTTOM);


        initContext(context,location);
    }

    public LogList(Context context) {
        super(context);
        initContext(context,BOTTOM);
    }

    private void initContext(Context context,int location) {
        mContext = context;

        switch (location){
            case TOP:

                break;

            case BOTTOM:
                LayoutInflater.from(context).inflate(R.layout.r2000uhf_log_list_bottom, this);

                break;
            case LEFT:
                LayoutInflater.from(context).inflate(R.layout.r2000uhf_log_list_left, this);

                break;

            case RIGHT:

                break;
        }

        //LayoutInflater.from(context).inflate(R.layout.r2000uhf_log_list_bottom, this);

        mLogList = (ListView) findViewById(R.id.log_list_view);
        mLogTextTableRow = (TableRow) findViewById(R.id.table_row_log);
        mLogTextImage = (ImageView) findViewById(R.id.image_prompt);
        mLogList.setVisibility(View.GONE);
        mLogTextImage.setImageDrawable(getResources().getDrawable(R.drawable.up));
        mLogInfoText = (TextView) findViewById(R.id.list_text_info);
        mLogInfoText.setText(getResources().getString(R.string.open_caption));

        mLogHelpText = (TextView) findViewById(R.id.list_text_help);

        mLogListItem = new ArrayList<CharSequence>();

        mLogListAdapter = new ArrayAdapter<CharSequence>(mContext, R.layout.r2000uhf_log_list_item, mLogListItem);

        mLogList.setAdapter(mLogListAdapter);

        mLogTextTableRow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mLogList.getVisibility() != View.VISIBLE) {
                    mLogList.setVisibility(View.VISIBLE);
                    mLogTextImage.setImageDrawable(getResources().getDrawable(R.drawable.down));
                    mLogInfoText.setText(getResources().getString(R.string.close_caption));
                } else {
                    mLogList.setVisibility(View.GONE);
                    mLogTextImage.setImageDrawable(getResources().getDrawable(R.drawable.up));
                    mLogInfoText.setText(getResources().getString(R.string.open_caption));
                }
            }
        });
    }

    public void writeLog(String strLog, int type) {
        Date now = new Date();
        SimpleDateFormat temp = new SimpleDateFormat("kk:mm:ss");
        SpannableString tSS = new SpannableString(temp.format(now) + ": " + strLog);
        tSS.setSpan(new ForegroundColorSpan(type == ERROR.SUCCESS ? Color.BLACK : Color.RED), 0, tSS.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  //红色高亮
        mLogListItem.add(tSS);

        mLogHelpText.setText(tSS);

        if (mLogListItem.size() > 50)
            mLogListItem.remove(0);

        mLogListAdapter.notifyDataSetChanged();
    }

    public boolean tryClose() {
        boolean isVisibility = mLogList.getVisibility() == View.VISIBLE;
        if (isVisibility) {
            mLogList.setVisibility(View.GONE);
            mLogTextImage.setImageDrawable(getResources().getDrawable(R.drawable.up));
            mLogInfoText.setText(getResources().getString(R.string.open_caption));
        }

        return isVisibility;
    }

}
