package com.cw.demo.UHF.hxuhf;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.cw.phychipsuhfsdk.UHFHXAPI;
import com.cw.serialportsdk.utils.DataUtils;

public class TagReadFragment extends Fragment {

    private static final String TAG ="TagReadFragment";

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
        View rootView = inflater.inflate(R.layout.hxuhf_tag_read_layout, container,
                false);
        spinnerArea = rootView.findViewById(R.id.spinnerArea);
        String[] areas = new String[]{"EPC", "TID", "USER"};

        ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(
                rootView.getContext(), R.layout.hxuhf_simple_list_item, areas);
        areaAdapter.setDropDownViewResource(R.layout.hxuhf_simple_list_item);
        spinnerArea.setAdapter(areaAdapter);

        final Button buttonRead = rootView
                .findViewById(R.id.buttonRead);
        buttonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonRead.setClickable(false);
                read();
                buttonRead.setClickable(true);
            }
        });

        txtEpc = rootView.findViewById(R.id.txtReadEpc);
        txtResult = rootView.findViewById(R.id.txtReadResult);
        unmpOffset = rootView
                .findViewById(R.id.myunmberinputSpinner_offset);
        unmpLength = rootView
                .findViewById(R.id.myunmberinputSpinner_length);
        editAccesspwd = rootView.findViewById(R.id.editAccesspwd);

        return rootView;
    }

    private void read() {
        String ap = editAccesspwd.getText().toString();
        short epcLength = (short) (txtEpc.getText().toString().length() / 2);
        String epc = txtEpc.getText().toString();
        byte mb = (byte) spinnerArea.getSelectedItemPosition();
        Log.i(TAG, mb + "");
        switch (mb) {
            case 0:
                mb++;
                break;
            case 1:
                mb++;
                break;
            case 2:
                mb++;
                break;
            default:
                break;
        }

        short sa = Short.parseShort(unmpOffset.getSelectedItem().toString());
        short dl = Short.parseShort(unmpLength.getSelectedItem().toString());
        Log.i(TAG, "ap:" + ap + "------epcLength:" + epcLength + "------epc:" + epc + "------mb:" + mb + "----------sa:" + sa + "-------dl:" + dl);

        if (Integer.parseInt(unmpLength.getSelectedItem().toString()) == 0) {
            DataUtils.showToast(getActivity(), getResources().getString(R.string.hxuhf_length_zero));
            //Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
        } else {

            byte[] arguments = ((HXUHFActivity) getActivity()).api.arguments(ap, epcLength, epc, mb, sa, dl);

            String data = readTag(arguments);
            txtResult.setText(data);
            if (!TextUtils.isEmpty(data)) {
                DataUtils.showToast(getActivity(), getResources().getString(R.string.hxuhf_read_success));
            } else {
                DataUtils.showToast(getActivity(), getResources().getString(R.string.hxuhf_read_fail));
            }
        }
    }

    public String readTag(byte[] args) {
        UHFHXAPI.Response response = ((HXUHFActivity) getActivity()).api.readTypeCTagData(args);
//        UHFHXAPI.Response response = ((HXUHFActivity) getActivity()).api.readTypeCTagLongData(args);
        if (response.result == UHFHXAPI.Response.RESPONSE_PACKET
                && response.data != null) {
            return DataUtils.toHexString(response.data);
        }
        return "";
    }
}
