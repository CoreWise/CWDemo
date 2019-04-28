package com.cw.demo.R2000UHF.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.cw.demo.R;
import com.cw.demo.R2000UHF.adapter.RealListAdapter;
import com.cw.r2000uhfsdk.helper.InventoryBuffer;
import com.cw.r2000uhfsdk.helper.ReaderHelper;

import java.util.ArrayList;
import java.util.List;


public class TagRealList extends LinearLayout {

	private Context mContext;
	private TableRow mTagRealRow;
	private ImageView mTagRealImage;
	private TextView mListTextInfo;
	
	private TextView mTagsCountText, mTagsTotalText;
	private TextView mTagsSpeedText, mTagsTimeText;
	private TextView mMinRSSIText, mMaxRSSIText;
	
	private ReaderHelper mReaderHelper;
	
	private List<InventoryBuffer.InventoryTagMap> data;
	private RealListAdapter mRealListAdapter;
	private ListView mTagRealList;
	
	private View mTagsRealListScrollView;
	
	//private static InventoryBuffer m_curInventoryBuffer;
	
	private OnItemSelectedListener mOnItemSelectedListener;
	public interface OnItemSelectedListener {
		public void onItemSelected(View arg1, int arg2,
                                   long arg3);
	}
	
	public TagRealList(Context context, AttributeSet attrs) {
		super(context, attrs);
		initContext(context);
	}
	
	public TagRealList(Context context) {
        super(context);
        initContext(context);
    }

	private void initContext(Context context) {
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.r2000_tag_real_list, this);
		
		try {
			mReaderHelper = ReaderHelper.getDefaultHelper();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		data = new ArrayList<InventoryBuffer.InventoryTagMap>();
		//m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
		
		mTagsRealListScrollView = findViewById(R.id.tags_real_list_scroll_view);
		mTagsRealListScrollView.setVisibility(View.GONE);
		
		mTagRealRow = (TableRow) findViewById(R.id.table_row_tag_real);
		mTagRealImage = (ImageView) findViewById(R.id.image_prompt);
		mTagRealImage.setImageDrawable(getResources().getDrawable(R.drawable.up));
		mListTextInfo = (TextView) findViewById(R.id.list_text_info);
		mListTextInfo.setText(getResources().getString(R.string.open_tag_list));

		mTagsCountText = (TextView) findViewById(R.id.tags_count_text);
		mTagsTotalText = (TextView) findViewById(R.id.tags_total_text);
		mTagsSpeedText = (TextView) findViewById(R.id.tags_speed_text);
		mTagsTimeText = (TextView) findViewById(R.id.tags_time_text);
		mMinRSSIText = (TextView) findViewById(R.id.min_rssi_text);
		mMaxRSSIText = (TextView) findViewById(R.id.max_rssi_text);
		
		mTagRealRow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (mTagsRealListScrollView.getVisibility() != View.VISIBLE) {
					mTagsRealListScrollView.setVisibility(View.VISIBLE);
					mTagRealImage.setImageDrawable(getResources().getDrawable(R.drawable.down));
					mListTextInfo.setText(getResources().getString(R.string.close_tag_list));
				} else {
					mTagsRealListScrollView.setVisibility(View.GONE);
					mTagRealImage.setImageDrawable(getResources().getDrawable(R.drawable.up));
					mListTextInfo.setText(getResources().getString(R.string.open_tag_list));
				}
			}
		});
		
		mTagRealList = (ListView) findViewById(R.id.tag_real_list_view);
		mRealListAdapter = new RealListAdapter(mContext, data);
		mTagRealList.setAdapter(mRealListAdapter);
		
		mTagRealList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
				
				if (mOnItemSelectedListener != null) {
					mOnItemSelectedListener.onItemSelected(arg1, arg2, arg3);
				}
			}
			
		});
	}
	
	public void setOnItemSelectedListener(
			OnItemSelectedListener onItemSelectedListener) {
		mOnItemSelectedListener = onItemSelectedListener;
	}
	
	public final void clearText() {
		mTagsCountText.setText("0");
		mTagsTotalText.setText("0");
		mTagsSpeedText.setText("0");
		mTagsTimeText.setText("0");
		mMinRSSIText.setText("");
		mMaxRSSIText.setText("");
	}
	
	public final void refreshText(InventoryBuffer inventoryBuffer) {
		mTagsCountText.setText(String.valueOf(inventoryBuffer.lsTagList.size()));
		mTagsTotalText.setText(String.valueOf(mReaderHelper.getInventoryTotal()));
		//if (m_curInventoryBuffer.nReadRate > 0) {
			mTagsSpeedText.setText(String.valueOf(inventoryBuffer.nReadRate));
		//}
		mTagsTimeText.setText(String.valueOf(inventoryBuffer.dtEndInventory.getTime() - inventoryBuffer.dtStartInventory.getTime()));
		mMinRSSIText.setText(String.valueOf(inventoryBuffer.nMinRSSI) + "dBm");
		mMaxRSSIText.setText(String.valueOf(inventoryBuffer.nMaxRSSI) + "dBm");
	}
	
	public final void refreshList(InventoryBuffer inventoryBuffer) {
		data.clear();
		data.addAll(inventoryBuffer.lsTagList);
		mRealListAdapter.notifyDataSetChanged();
	}	
}
