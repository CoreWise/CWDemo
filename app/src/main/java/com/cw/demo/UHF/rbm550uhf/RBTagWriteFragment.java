package com.cw.demo.UHF.rbm550uhf;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
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

public class RBTagWriteFragment extends Fragment {
    private Spinner spinnerArea;
    private TextView txtEpc;
    private EditText editInput;
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
        View rootView = inflater.inflate(R.layout.rbuhf_tag_write_layout, container,
                false);
        spinnerArea = rootView.findViewById(R.id.spinnerArea);
        String[] areas = new String[]{"EPC", "USER"};

        ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(
                rootView.getContext(), R.layout.hxuhf_simple_list_item, areas);
        areaAdapter.setDropDownViewResource(R.layout.hxuhf_simple_list_item);
        spinnerArea.setAdapter(areaAdapter);

        final Button buttonWrite = rootView
                .findViewById(R.id.buttonWrite);
        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonWrite.setClickable(false);
                write();
                buttonWrite.setClickable(true);
            }
        });

        txtEpc = rootView.findViewById(R.id.txtWriteEpc);
        editInput = rootView.findViewById(R.id.editInputInfo);
        unmpOffset = rootView
                .findViewById(R.id.myunmberinputSpinner_offset);
        unmpLength = rootView
                .findViewById(R.id.myunmberinputSpinner_length);
        editAccesspwd = rootView.findViewById(R.id.editAccesspwd);

        return rootView;
    }

    public void write() {
        String ap = editAccesspwd.getText().toString();
        short epcLength = (short) (txtEpc.getText().toString().length() / 2);
        String epc = txtEpc.getText().toString();
        byte mb = (byte) spinnerArea.getSelectedItemPosition();
        switch (mb) {
            case 0:
                mb++;
                break;
            case 1:
                mb += 2;
                break;
            default:
                break;
        }
        short sa = Short.parseShort(unmpOffset.getSelectedItem().toString());
        short dl = Short.parseShort(unmpLength.getSelectedItem().toString());
        String writeData = editInput.getText().toString();

        if (!TextUtils.isEmpty(writeData) && writeData.length() / 4 == dl) {

            byte[] arguments = ((RBUHFActivity) getActivity()).api.writeArguments(ap, mb, sa, dl,writeData);
            ((RBUHFActivity) getActivity()).api.writeTypeC(arguments);

        } else {
            DataUtils.showToast(getActivity(), getResources().getString(R.string.hxuhf_length_not_right));
        }
    }
}