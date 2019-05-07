package com.cw.demo.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cw.demo.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者：李阳
 * 时间：2019/4/1
 * 描述：
 */
public class FriendDialog extends Dialog implements View.OnClickListener {


    private int second = 8;

    private TextView tv_yes, tv_no, tv_message;
    private LinearLayout linearLayout;


    private onClickListener OnClickListener;

    public void setOnClickListener(onClickListener onClickListener) {
        OnClickListener = onClickListener;
    }

    public interface onClickListener {
        void OnClickPositive();

        void OnClickNegative();

    }


    public FriendDialog(@NonNull Context context, int width, int height, @IdRes int layout, int style) {
        super(context);
        View view = getLayoutInflater().inflate(R.layout.dialog_friend, null);

        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        //params.height = context.getResources().getDisplayMetrics().heightPixels - 60;
        //params.width = context.getResources().getDisplayMetrics().widthPixels - 60;
        params.height = context.getResources().getDisplayMetrics().heightPixels - height;
        params.width = context.getResources().getDisplayMetrics().widthPixels - width;

        //去背景
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(params);

        setCancelable(false);

        linearLayout = view.findViewById(R.id.ll_btn);


        tv_yes = view.findViewById(R.id.btn_yes);
        tv_no = view.findViewById(R.id.btn_no);

        tv_message = view.findViewById(R.id.tv_message);

        tv_yes.setOnClickListener(this);
        tv_no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                this.dismiss();
                OnClickListener.OnClickPositive();
                break;

            case R.id.btn_no:
                OnClickListener.OnClickNegative();
                break;
        }
    }

    public void setMessageSize(int sp) {
        if (tv_message == null) {
            return;
        }
        tv_message.setTextSize(sp);
    }

    public void setMessage(String message) {
        if (tv_message == null) {
            return;
        }
        tv_message.setText(message);
    }
}
