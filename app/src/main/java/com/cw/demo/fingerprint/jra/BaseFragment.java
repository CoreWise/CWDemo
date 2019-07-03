package com.cw.demo.fingerprint.jra;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * 作者：李阳
 * 时间：2019/6/25
 * 描述：
 */
public class BaseFragment extends Fragment {

    JRAActivity parentActivity;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity= (JRAActivity) context;
    }


}
