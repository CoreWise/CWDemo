package com.cw.demo.UHF.rbm550uhf;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cw.demo.R;
import com.cw.demo.UHF.utils.MyunmberinputSpinner;
import com.cw.serialportsdk.utils.DataUtils;

public class RBTagReadFragment extends Fragment {

    private static final String TAG ="TagReadFragment";

    private Spinner spinnerArea;
    private TextView txtEpc;
    private TextView txtResult;
    private MyunmberinputSpinner unmpOffset;
    private MyunmberinputSpinner unmpLength;
    private EditText editAccesspwd;
    private EditText editTime;
    private Button buttonReadCycle;
    private Button buttonCancelSelected;

    private byte[] arguments;
    private boolean isStart = false;
    private int time = 0;
    private Handler mReadingHandler = new Handler();
    private Runnable mReadingRunnable = new Runnable() {
        @Override
        public void run() {

            if (isStart)
            {
                ((RBUHFActivity) getActivity()).api.readTypeC(arguments);
                mReadingHandler.postDelayed(this,time);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rbuhf_tag_read_layout, container,
                false);
        spinnerArea = rootView.findViewById(R.id.spinnerArea);
        String[] areas = new String[]{"RESERVED","EPC", "TID", "USER"};

        ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(
                rootView.getContext(), R.layout.hxuhf_simple_list_item, areas);
        areaAdapter.setDropDownViewResource(R.layout.hxuhf_simple_list_item);
        spinnerArea.setAdapter(areaAdapter);

        final Button buttonRead = rootView.findViewById(R.id.buttonRead);
        buttonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonRead.setClickable(false);
                read(false);
                buttonRead.setClickable(true);
            }
        });

        buttonReadCycle = rootView.findViewById(R.id.buttonReadCycle);
        buttonReadCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               read(true);
            }
        });

        buttonCancelSelected = rootView.findViewById(R.id.buttonCancelSelected);
        buttonCancelSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RBUHFActivity) getActivity()).api.cancelAccessEpcMatch();
                txtEpc.setText("");
                txtResult.setText("");
            }
        });

        txtEpc = rootView.findViewById(R.id.txtReadEpc);
        txtResult = rootView.findViewById(R.id.txtReadResult);
        unmpOffset = rootView
                .findViewById(R.id.myunmberinputSpinner_offset);
        unmpLength = rootView
                .findViewById(R.id.myunmberinputSpinner_length);
        editAccesspwd = rootView.findViewById(R.id.editAccesspwd);
        editTime = rootView.findViewById(R.id.time);
        return rootView;
    }

    private void read(boolean isCycle) {
        String ap = editAccesspwd.getText().toString();
        byte mb = (byte) spinnerArea.getSelectedItemPosition();
        Log.i(TAG, mb + "");
        switch (mb) {
            case 0:
                //RESERVED
                mb = 0x00;
                break;
            case 1:
                //epc
                mb = 0x01;
                break;
            case 2:
                //tid
                mb = 0x02;
                break;
            case 3:
                mb = 0x03;
                break;
            default:
                break;
        }

        short sa = Short.parseShort(unmpOffset.getSelectedItem().toString());
        short dl = Short.parseShort(unmpLength.getSelectedItem().toString());
        Log.i(TAG, "ap:" + ap + "------mb:" + mb + "----------sa:" + sa + "-------dl:" + dl);

        mReadingHandler.removeCallbacksAndMessages(null);

        if (Integer.parseInt(unmpLength.getSelectedItem().toString()) == 0) {
            DataUtils.showToast(getActivity(), getResources().getString(R.string.hxuhf_length_zero));
        } else {

            if (isCycle)
            {
                isStart = !isStart;
                buttonReadCycle.setText(isStart?getActivity().getResources().getString(R.string.hxuhf_stop):getActivity().getResources().getString(R.string.hxuhf_cycle_reading));
                String timeString = editTime.getText().toString();
                time = Integer.parseInt(timeString);
                arguments = ((RBUHFActivity) getActivity()).api.readArguments(ap, mb, sa, dl);
                mReadingHandler.postDelayed(mReadingRunnable,time);
            }
            else
            {
                isStart = false;
                buttonReadCycle.setText(getActivity().getResources().getString(R.string.hxuhf_cycle_reading));
                byte[] arguments = ((RBUHFActivity) getActivity()).api.readArguments(ap, mb, sa, dl);
                ((RBUHFActivity) getActivity()).api.readTypeC(arguments);
            }

        }
    }
}
