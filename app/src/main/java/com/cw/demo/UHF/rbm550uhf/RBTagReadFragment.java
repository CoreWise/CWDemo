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

    private static final String TAG = "TagReadFragment";

    private Spinner spinnerArea;
    private TextView txtEpc;
    private TextView txtResult;
    private MyunmberinputSpinner unmpOffset;
    private MyunmberinputSpinner unmpLength;
    private EditText editAccesspwd;

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
        String[] areas = new String[]{"EPC", "TID", "USER"};

        ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(
                rootView.getContext(), R.layout.hxuhf_simple_list_item, areas);
        areaAdapter.setDropDownViewResource(R.layout.hxuhf_simple_list_item);
        spinnerArea.setAdapter(areaAdapter);

        final Button buttonRead = rootView.findViewById(R.id.buttonRead);
        buttonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                read();
            }
        });

        txtEpc = rootView.findViewById(R.id.txtReadEpc);
        txtResult = rootView.findViewById(R.id.txtReadResult);
        unmpOffset = rootView.findViewById(R.id.myunmberinputSpinner_offset);
        unmpLength = rootView.findViewById(R.id.myunmberinputSpinner_length);
        editAccesspwd = rootView.findViewById(R.id.editAccesspwd);
        return rootView;
    }

    private void read() {
        String ap = editAccesspwd.getText().toString();
        byte mb = (byte) spinnerArea.getSelectedItemPosition();
        Log.i(TAG, mb + "");
        switch (mb) {
            case 0:
                //epc
                mb = 0x01;
                break;
            case 1:
                //tid
                mb = 0x02;
                break;
            case 2:
                mb = 0x03;
                break;
            default:
                break;
        }

        short sa = Short.parseShort(unmpOffset.getSelectedItem().toString());
        short dl = Short.parseShort(unmpLength.getSelectedItem().toString());
        Log.i(TAG, "ap:" + ap + "------mb:" + mb + "----------sa:" + sa + "-------dl:" + dl);

        if (Integer.parseInt(unmpLength.getSelectedItem().toString()) == 0) {
            DataUtils.showToast(getActivity(), getResources().getString(R.string.hxuhf_length_zero));
        } else {

            byte[] arguments = ((RBUHFActivity) getActivity()).api.readArguments(ap, mb, sa, dl);
            ((RBUHFActivity) getActivity()).api.readTypeC(arguments);

        }
    }
}
