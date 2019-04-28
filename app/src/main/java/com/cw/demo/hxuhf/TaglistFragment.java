package com.cw.demo.hxuhf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.cw.demo.R;

import java.util.ArrayList;
import java.util.List;

public class TaglistFragment extends ListFragment {
    private List<String> presidents = new ArrayList<String>();
    public MyAdapter myadapter = null;
    private int curSelPosition = -1;


    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hxuhf_taglist, container, false);
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


        TextView txtEpc = mActivity.findViewById(
                R.id.txtReadEpc);

        TextView txtWriteEpc = mActivity
                .findViewById(R.id.txtWriteEpc);

        txtEpc.setText(presidents.get(position));

        txtWriteEpc.setText(presidents.get(position));

        /*
		 * Toast.makeText(getActivity(), "You have selected " +
		 * presidents.get(position), Toast.LENGTH_SHORT) .show();
		 */
    }


    protected Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity= (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity= null;
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

        TextView txtEpc = mActivity.findViewById(
                R.id.txtReadEpc);
        TextView txtWriteEpc = mActivity
                .findViewById(R.id.txtWriteEpc);

        txtEpc.setText(getText(R.string.hxuhf_txt_null));
        txtWriteEpc.setText(getText(R.string.hxuhf_txt_null));

        myadapter.notifyDataSetChanged();
    }

    /**
     * 更新当前被选中的项目信息
     *
     * @param tagEPC
     */
    public void updateSelItem(String tagEPC) {
        if (curSelPosition != -1) {
            presidents.set(curSelPosition, tagEPC);
            myadapter.notifyDataSetChanged();
        }
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
            holder.time.setText("" + BaseUHFActivity.number.get(epc));
            holder.readTime.setText(""+ BaseUHFActivity.readTime.get(epc)+"ms");

            return convertView;
        }

        class ViewHolder {
            public TextView epc;
            public TextView time;
            public TextView readTime;
        }

    }
}
