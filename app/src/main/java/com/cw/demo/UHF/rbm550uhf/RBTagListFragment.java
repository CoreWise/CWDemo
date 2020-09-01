package com.cw.demo.UHF.rbm550uhf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.cw.demo.R;
import com.cw.serialportsdk.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

public class RBTagListFragment extends ListFragment {
    private List<String> presidents = new ArrayList<String>();
    public MyAdapter myadapter = null;
    private int curSelPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rbuhf_taglist, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myadapter = new MyAdapter(getActivity());
        setListAdapter(myadapter);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getListView().setVerticalScrollBarEnabled(true);
    }

    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
        // 记录当前选中的标签位置
        curSelPosition = position;

        TextView txtEpc = mActivity.findViewById(R.id.txtReadEpc);

        TextView txtWriteEpc = mActivity.findViewById(R.id.txtWriteEpc);

        String ClickEPC = presidents.get(position);

        txtEpc.setText(ClickEPC);

        txtWriteEpc.setText(ClickEPC);

        ((RBUHFActivity) mActivity).api.cancelAccessEpcMatch();

        byte[] bytes = DataUtils.hexStringTobyte(ClickEPC);
        Log.i("RBUHF", "bytes =" + DataUtils.toHexString(bytes));
        ((RBUHFActivity) mActivity).api.setAccessEpcMatch(bytes);
    }


    protected Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    /**
     * 增加列表显示项
     *
     * @param tagEPC 要显示的EPC信息
     */
    public void addItem(String tagEPC) {
        presidents.add(tagEPC);
        myadapter.notifyDataSetChanged();
    }

    /**
     * 清除列表的显示内容
     */
    public void clearItem() {
        presidents.clear();
        myadapter.notifyDataSetChanged();
    }

    class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return presidents.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.hxuhf_datalist, null);
                holder.epc = convertView.findViewById(R.id.epcId);
                holder.time = convertView.findViewById(R.id.readNum);
                holder.readTime = convertView.findViewById(R.id.readTime);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String epc = presidents.get(position);
            holder.epc.setText(epc);
            holder.time.setText("" + RBUHFActivity.number.get(epc));
            holder.readTime.setText("" + RBUHFActivity.readTime.get(epc) + "ms");

            return convertView;
        }

        class ViewHolder {
            public TextView epc;
            public TextView time;
            public TextView readTime;
        }

    }
}
