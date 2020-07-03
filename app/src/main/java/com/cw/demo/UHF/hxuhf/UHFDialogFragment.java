package com.cw.demo.UHF.hxuhf;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.cw.demo.R;
import com.cw.phychipsuhfsdk.UHFHXAPI;
import com.cw.serialportsdk.utils.DataUtils;

/**
 * 设置功率Dialog
 */
public class UHFDialogFragment extends DialogFragment {
    boolean isFirstShow = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.hxuhf_setting_dialog, container);
        Spinner spinner = view.findViewById(R.id.power);
        final String[] strs = getResources().getStringArray(R.array.hxuhf_dbm);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, strs);
        spinner.setAdapter(adapter);

        UHFHXAPI.Response response = ((HXUHFActivity) getActivity()).api
                .getTxPowerLevel();

        if (response.result == UHFHXAPI.Response.RESPONSE_PACKET) {
            byte[] power = new byte[2];
            if ((android.os.Build.MODEL).equals("A370")) {
                power = response.data;
            } else {
                //Log.i("YYYYY",response.data);
                System.arraycopy(response.data, 5, power, 0, 2);
            }

            int value = DataUtils.getInt(power);
            String valueStr = String.valueOf(value);
            String formatStr = valueStr.substring(0, 2) + "." + valueStr.substring(2);

            for (int i = 0; i < strs.length; i++) {
                if (strs[i].equals(formatStr)) {
                    spinner.setSelection(i);
                }
            }
            Log.i("whw", "Power=" + DataUtils.toHexString(power));
        }

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (isFirstShow) {
                    isFirstShow = false;
                    return;
                }

                //默认最大功率26
                String[] dbmStr = strs[position].split("\\.");
                int dbm = Integer.parseInt(dbmStr[0] + dbmStr[1]);
                byte[] data = DataUtils.int2Byte2(dbm);
                Log.d("jokey", "dbm  " + DataUtils.toHexString(data));
//				byte[] data = { 0x00, (byte) dbm };
                UHFHXAPI.Response response = ((HXUHFActivity) getActivity()).api
                        .setTxPowerLevel(data);
                if (response.result == UHFHXAPI.Response.RESPONSE_PACKET) {
                    if (response.data[0] == 0x00) {

                        Toast.makeText(getActivity(), "Update success!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Toast.makeText(getActivity(), "Update fail!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }

        });
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return super.onCreateDialog(savedInstanceState);
    }

}
