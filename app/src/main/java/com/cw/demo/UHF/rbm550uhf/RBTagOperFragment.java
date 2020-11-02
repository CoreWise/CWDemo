package com.cw.demo.UHF.rbm550uhf;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.cw.demo.R;


public class RBTagOperFragment extends Fragment {
	
    TabHost tabHost;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
    	tabHost = (TabHost)inflater.inflate(R.layout.rbuhf_tagoper_layout, container, false);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(getText(R.string.hxuhf_tab_read)).setContent(R.id.fragment_tagRead));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(getText(R.string.hxuhf_tab_write)).setContent(R.id.fragment_tagWrite));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator(getText(R.string.hxuhf_tab_open_lamp)).setContent(R.id.fragment_tagOpenLamp));
        tabHost.setCurrentTab(0); 
        updateTab(tabHost);
        return tabHost;
    }
    
    /**
     * 更新Tab标签的颜色，和字体的颜色
     * @param tabHost
     */ 
    private void updateTab(final TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) { 
            View view = tabHost.getTabWidget().getChildAt(i);
            TextView tv = view.findViewById(android.R.id.title);
            tv.setTextSize(20);
            tv.setTextColor(Color.WHITE);
        } 
    } 
}
