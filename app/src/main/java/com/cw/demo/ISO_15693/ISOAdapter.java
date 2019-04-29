package com.cw.demo.ISO_15693;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cw.demo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 金宇凡 on 2019/3/11.
 */
public class ISOAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = ISOAdapter.class.getSimpleName();
    private ArrayList<Map.Entry<Integer, String>> mList = new ArrayList<>();

    public ISOAdapter() {
    }

    public void setData(HashMap<Integer, String> map) {
        mList.clear();
        mList.addAll(SortMap(map));
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iso_item_adapter, parent, false);
        ISOViewHolder viewHolder = new ISOViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ISOViewHolder holder1 = (ISOViewHolder) holder;
        Map.Entry<Integer, String> dataEntry = mList.get(position);
        holder1.name.setText("第" + dataEntry.getKey() + "块:");

        Log.d(TAG, "dataEntry.getKey() = " + dataEntry.getKey());
        Log.d(TAG, "dataEntry.getValue() = " + dataEntry.getValue());
        String value = dataEntry.getValue();
        value = null == value ? "00000000" : value;
        if (value.length() < 8) {
            int needF = 8 - value.length();
            for (int i = 0; i < needF; i++) {
                value = value + "0";
            }
        }
        String data = hexStr2Str(value);
        holder1.data.setText(data);
    }

    /**
     * 十六进制转换字符串
     *
     * @param hexStr Byte字符串(Byte之间无分隔符 如:[616C6B])
     * @return String 对应的字符串
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 排序
     */
    private ArrayList<Map.Entry<Integer, String>> SortMap(HashMap<Integer, String> map) {
        ArrayList<Map.Entry<Integer, String>> list = new ArrayList<>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Integer, String>>() {
            @Override
            public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2) {
                return o1.getKey() - o2.getKey();
            }
        });
        return list;
    }

    class ISOViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView name;
        private AppCompatTextView data;

        public ISOViewHolder(View itemView) {
            super(itemView);
            name = (AppCompatTextView) itemView.findViewById(R.id.iso_name);
            data = (AppCompatTextView) itemView.findViewById(R.id.iso_data);
        }
    }
}
