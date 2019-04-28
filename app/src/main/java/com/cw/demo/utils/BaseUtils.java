package com.cw.demo.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间：2018/8/23
 * 描述：
 */
public class BaseUtils {


    public static void disableSubControls(ViewGroup viewGroup, Boolean enable) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                if (v instanceof Spinner) {
                    Spinner spinner = (Spinner) v;
                    spinner.setClickable(enable);
                    spinner.setEnabled(enable);

                    //Log.i(TAG, "A Spinner is unabled");
                } else if (v instanceof ListView) {
                    v.setClickable(enable);
                    v.setEnabled(enable);

                    //Log.i(TAG, "A ListView is unabled");
                } else {
                    disableSubControls((ViewGroup) v, enable);
                }
            } else if (v instanceof EditText) {
                v.setEnabled(enable);
                v.setClickable(enable);

                //Log.i(TAG, "A EditText is unabled");
            } else if (v instanceof Button) {
                v.setEnabled(enable);

                // Log.i(TAG, "A Button is unabled");
            }
        }
    }


    public static String getPerCent(int num, int all) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float) num / (float) all * 100);
        //System.out.println("num1和num2的百分比为:" + result + "%");
        return result + "%";
    }

    public static String div(long num, long all) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float) num / (float) all * 100);
        //System.out.println("num1和num2的百分比为:" + result + "%");
        return result;
    }


    public static void getTimeDiff(String before, String now) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = sdf.parse(before);
            date2 = sdf.parse(now);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l = date2.getTime() - date1.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        System.out.println(day + "天" + hour + "小时" + min + "分" + s + "秒");

    }


    public static String  getMoneyNumber(double number,Locale locale) {

        //NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        System.out.println("Locale.CHINA: " + format.format(number));
        format = NumberFormat.getCurrencyInstance(Locale.US);
        System.out.println("Locale.US: " + format.format(number));

        return format.format(number);
    }




}
