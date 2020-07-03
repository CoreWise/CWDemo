package com.cw.demo.UHF.R2000UHF.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.cw.demo.R;
import com.cw.r2000uhfsdk.helper.OperateTagBuffer;
import com.cw.r2000uhfsdk.helper.ReaderHelper;

import java.util.ArrayList;
import java.util.List;


public class TagAccessList extends LinearLayout {

	private Context mContext;
	private TableRow mTagAccessRow;
	private ImageView mTagAccessImage;
	private TextView mListTextInfo;
	
	private ReaderHelper mReaderHelper;
	
	private List<OperateTagBuffer.OperateTagMap> data;
	private AccessListAdapter mAccessListAdapter;
	private ListView mTagAccessList;
	
	private View mTagsAccessListScrollView;
	

	public TagAccessList(Context context, AttributeSet attrs) {
		super(context, attrs);
		initContext(context);
	}
	
	public TagAccessList(Context context) {
        super(context);
        initContext(context);
    }

	private void initContext(Context context) {
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.r2000uhf_tag_access_list, this);
		

		
		data = new ArrayList<OperateTagBuffer.OperateTagMap>();
		
		//m_curOperateTagBuffer = mReaderHelper.getCurOperateTagBuffer();
		
		mTagsAccessListScrollView = findViewById(R.id.tags_access_list_scroll_view);
		mTagsAccessListScrollView.setVisibility(View.GONE);
		
		mTagAccessRow = (TableRow) findViewById(R.id.table_row_tag_access);
		mTagAccessImage = (ImageView) findViewById(R.id.image_prompt);
		mTagAccessImage.setImageDrawable(getResources().getDrawable(R.drawable.up));
		mListTextInfo = (TextView) findViewById(R.id.list_text_info);
		mListTextInfo.setText(getResources().getString(R.string.open_tag_list));

		mTagAccessRow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (mTagsAccessListScrollView.getVisibility() != View.VISIBLE) {
					mTagsAccessListScrollView.setVisibility(View.VISIBLE);
					mTagAccessImage.setImageDrawable(getResources().getDrawable(R.drawable.down));
					mListTextInfo.setText(getResources().getString(R.string.close_tag_list));
				} else {
					mTagsAccessListScrollView.setVisibility(View.GONE);
					mTagAccessImage.setImageDrawable(getResources().getDrawable(R.drawable.up));
					mListTextInfo.setText(getResources().getString(R.string.open_tag_list));
				}
			}
		});
		
		mTagAccessList = (ListView) findViewById(R.id.tag_real_list_view);
		mAccessListAdapter = new AccessListAdapter(mContext, data);
		mTagAccessList.setAdapter(mAccessListAdapter);
	}
	
	public final void clearText() {
		;
	}
	
	public final void refreshText() {
		;
	}
	
	public final void refreshList(OperateTagBuffer m_curOperateTagBuffer) {
		data.clear();
		data.addAll(m_curOperateTagBuffer.lsTagList);
		mAccessListAdapter.notifyDataSetChanged();
	}	
}
