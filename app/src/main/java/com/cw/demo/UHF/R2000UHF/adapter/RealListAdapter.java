package com.cw.demo.UHF.R2000UHF.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cw.demo.R;
import com.cw.r2000uhfsdk.helper.InventoryBuffer;

import java.util.List;


public class RealListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;

	private Context mContext;
	
	private List<InventoryBuffer.InventoryTagMap> listMap;
	
	public final class ListItemView{                //自定义控件集合     
		public TextView mIdText;
		public TextView mEpcText;
		public TextView mPcText;
		public TextView mTimesText;
		public TextView mRssiText;
		public TextView mFreqText;
    }

	public RealListAdapter(Context context, List<InventoryBuffer.InventoryTagMap> listMap) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.listMap = listMap;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listMap.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView  listItemView = null;
		if (convertView == null) {
			listItemView = new ListItemView();
			convertView = mInflater.inflate(R.layout.r2000_tag_real_list_item, null);
			listItemView.mIdText = (TextView)convertView.findViewById(R.id.id_text);
			listItemView.mEpcText = (TextView)convertView.findViewById(R.id.epc_text);
			listItemView.mPcText = (TextView)convertView.findViewById(R.id.pc_text);
			listItemView.mTimesText = (TextView)convertView.findViewById(R.id.times_text);
			listItemView.mRssiText = (TextView)convertView.findViewById(R.id.rssi_text);
			listItemView.mFreqText = (TextView)convertView.findViewById(R.id.freq_text);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		
		InventoryBuffer.InventoryTagMap map = listMap.get(position);
		
		listItemView.mIdText.setText(String.valueOf(position));
		listItemView.mEpcText.setText(map.strEPC);
		listItemView.mPcText.setText(map.strPC);
		listItemView.mTimesText.setText(String.valueOf(map.nReadCount));
		try {
			listItemView.mRssiText.setText((Integer.parseInt(map.strRSSI) - 129) + "dBm");
		} catch (Exception e) {
			listItemView.mRssiText.setText("");
		}
		listItemView.mFreqText.setText(map.strFreq);
		
		return convertView;

	}	
}
