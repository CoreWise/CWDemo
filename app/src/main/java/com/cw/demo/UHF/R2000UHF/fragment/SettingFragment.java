package com.cw.demo.UHF.R2000UHF.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.cw.demo.R;

import com.cw.demo.UHF.R2000UHF.UHF2000Activity;
import com.cw.r2000uhfsdk.base.CMD;
import com.cw.r2000uhfsdk.base.ERROR;

/**
 * 时间：2019/4/26
 * 描述：设置
 */

public class SettingFragment extends Fragment {

    private TableRow mSettingResetRow;
    private TableRow mSettingReaderAddressRow;
    private TextView mSettingReaderAddressText;
    private TableRow mSettingIdentifierRow;
    private TextView mSettingIdentifierText;
    private TableRow mSettingFirmwareVersionRow;
    private TextView mSettingFirmwareVersionText;
    private TableRow mSettingTemperatureRow;
    private TextView mSettingTemperatureText;
    private TableRow mSettingGpioRow;
    private TextView mSettingGpioText;
    private TableRow mSettingBeeperRow;
    private TextView mSettingBeeperText;
    private TableRow mSettingOutPowerRow;
    private TextView mSettingOutPowerText;
    private TableRow mSettingAntennaRow;
    private TextView mSettingAntennaText;
    private TableRow mSettingReturnLossRow;
    private TextView mSettingReturnLossText;
    private TableRow mSettingAntDetectorRow;
    private TextView mSettingAntDetectorText;
    private TableRow mSettingMonzaRow;
    private TextView mSettingMonzaText;
    private TableRow mSettingRegionRow;
    private TextView mSettingRegionText;
    private TableRow mSettingProfileRow;
    private TextView mSettingProfileText;


    private View rootView;


    private UHF2000Activity activity;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity= (UHF2000Activity) getActivity();
        rootView = inflater.inflate(R.layout.r2000uhf_setting, null);
        initSettingView();

        return rootView;
    }


    private void initSettingView() {

        mSettingResetRow = (TableRow) rootView.findViewById(R.id.table_row_setting_reset);
        mSettingResetRow.setOnClickListener(setSettingOnClickListener);
        mSettingReaderAddressRow = (TableRow) rootView.findViewById(R.id.table_row_setting_reader_address);
        mSettingReaderAddressRow.setOnClickListener(setSettingOnClickListener);
        mSettingReaderAddressText = (TextView) rootView.findViewById(R.id.text_setting_reader_address);

        mSettingIdentifierRow = (TableRow) rootView.findViewById(R.id.table_row_setting_identifier);
        mSettingIdentifierRow.setOnClickListener(setSettingOnClickListener);
        mSettingIdentifierText = (TextView) rootView.findViewById(R.id.text_setting_identifier);

        mSettingFirmwareVersionRow = (TableRow) rootView.findViewById(R.id.table_row_setting_firmware_version);
        mSettingFirmwareVersionRow.setOnClickListener(setSettingOnClickListener);
        mSettingFirmwareVersionText = (TextView) rootView.findViewById(R.id.text_setting_firmware_version);

        mSettingTemperatureRow = (TableRow) rootView.findViewById(R.id.table_row_setting_temperature);
        mSettingTemperatureRow.setOnClickListener(setSettingOnClickListener);
        mSettingTemperatureText = (TextView) rootView.findViewById(R.id.text_setting_temperature);

        mSettingGpioRow = (TableRow) rootView.findViewById(R.id.table_row_setting_gpio);
        mSettingGpioRow.setOnClickListener(setSettingOnClickListener);
        mSettingGpioText = (TextView) rootView.findViewById(R.id.text_setting_gpio);

        mSettingBeeperRow = (TableRow) rootView.findViewById(R.id.table_row_setting_beeper);
        mSettingBeeperRow.setOnClickListener(setSettingOnClickListener);
        mSettingBeeperText = (TextView) rootView.findViewById(R.id.text_setting_beeper);


        mSettingOutPowerRow = (TableRow) rootView.findViewById(R.id.table_row_setting_out_power);
        mSettingOutPowerRow.setOnClickListener(setSettingOnClickListener);
        mSettingOutPowerText = (TextView) rootView.findViewById(R.id.text_setting_out_power);

        mSettingAntennaRow = (TableRow) rootView.findViewById(R.id.table_row_setting_antenna);
        mSettingAntennaRow.setOnClickListener(setSettingOnClickListener);
        mSettingAntennaText = (TextView) rootView.findViewById(R.id.text_setting_antenna);

        mSettingReturnLossRow = (TableRow) rootView.findViewById(R.id.table_row_setting_return_loss);
        mSettingReturnLossRow.setOnClickListener(setSettingOnClickListener);
        mSettingReturnLossText = (TextView) rootView.findViewById(R.id.text_setting_return_loss);

        mSettingAntDetectorRow = (TableRow) rootView.findViewById(R.id.table_row_setting_ant_detector);
        mSettingAntDetectorRow.setOnClickListener(setSettingOnClickListener);
        mSettingAntDetectorText = (TextView) rootView.findViewById(R.id.text_setting_ant_detector);

        mSettingMonzaRow = (TableRow) rootView.findViewById(R.id.table_row_setting_monza);
        mSettingMonzaRow.setOnClickListener(setSettingOnClickListener);
        mSettingMonzaText = (TextView) rootView.findViewById(R.id.text_setting_monza);

        mSettingRegionRow = (TableRow) rootView.findViewById(R.id.table_row_setting_region);
        mSettingRegionRow.setOnClickListener(setSettingOnClickListener);
        mSettingRegionText = (TextView) rootView.findViewById(R.id.text_setting_region);

        mSettingProfileRow = (TableRow) rootView.findViewById(R.id.table_row_setting_profile);
        mSettingProfileRow.setOnClickListener(setSettingOnClickListener);
        mSettingProfileText = (TextView) rootView.findViewById(R.id.text_setting_profile);
    }


    private void writeLog(String strLog, byte type) {
        if (activity.mLogList != null) {
            activity.mLogList.writeLog(strLog, type);
        }
    }


    private View.OnClickListener setSettingOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {

            switch (arg0.getId()) {
                case R.id.table_row_setting_reset:

                    activity.r2000UHFAPI.reset();
                    writeLog(CMD.format(CMD.RESET), ERROR.SUCCESS);
                    return;
                case R.id.table_row_setting_reader_address:
                    //intent = new Intent().setClass(mContext, PageReaderAddress.class);
                    Toast.makeText(getContext(), R.string.r2000_uhf_support, Toast.LENGTH_SHORT).show();

                    return;
                case R.id.table_row_setting_identifier:
                    //intent = new Intent().setClass(mContext, PageReaderIdentifier.class);
                    Toast.makeText(getContext(), R.string.r2000_uhf_support, Toast.LENGTH_SHORT).show();

                    return;
                case R.id.table_row_setting_firmware_version:
                    //intent = new Intent().setClass(mContext, PageReaderFirmwareVersion.class);
                    activity.toFragmentBackStack(new PageReaderFirmwareVersion());

                    break;
                case R.id.table_row_setting_temperature:
                    //intent = new Intent().setClass(mContext, PageReaderTemperature.class);
                    activity.toFragmentBackStack(new PageReaderTemperature());

                    break;
                case R.id.table_row_setting_gpio:
                    //intent = new Intent().setClass(mContext, PageReaderGpio.class);
                    Toast.makeText(getContext(), R.string.r2000_uhf_support, Toast.LENGTH_SHORT).show();

                    return;
                case R.id.table_row_setting_beeper:
                    //intent = new Intent().setClass(mContext, PageReaderBeeper.class);
                    Toast.makeText(getContext(), R.string.r2000_uhf_support, Toast.LENGTH_SHORT).show();

                    return;
                case R.id.table_row_setting_out_power:
                    //intent = new Intent().setClass(mContext, PageReaderOutPower.class);
                    activity.toFragmentBackStack(new PageReaderOutPower());
                    break;
                case R.id.table_row_setting_antenna:
                    //intent = new Intent().setClass(mContext, PageReaderAntenna.class);
                    Toast.makeText(getContext(), R.string.r2000_uhf_support, Toast.LENGTH_SHORT).show();

                    return;
                case R.id.table_row_setting_return_loss:
                    //intent = new Intent().setClass(mContext, PageReaderReturnLoss.class);
                    Toast.makeText(getContext(), R.string.r2000_uhf_support, Toast.LENGTH_SHORT).show();

                    return;
                case R.id.table_row_setting_ant_detector:
                    //intent = new Intent().setClass(mContext, PageReaderAntDetector.class);
                    Toast.makeText(getContext(), R.string.r2000_uhf_support, Toast.LENGTH_SHORT).show();
                    return;
                case R.id.table_row_setting_monza:
                    //intent = new Intent().setClass(mContext, PageReaderMonza.class);
                    Toast.makeText(getContext(), R.string.r2000_uhf_support, Toast.LENGTH_SHORT).show();

                    return;
                case R.id.table_row_setting_region:
                    //intent = new Intent().setClass(mContext, PageReaderRegion.class);
                    activity.toFragmentBackStack(new PageReaderRegion());
                    break;
                case R.id.table_row_setting_profile:
                    //intent = new Intent().setClass(mContext, PageReaderProfile.class);
                    activity.toFragmentBackStack(new PageReaderProfile());
                    break;
                default:
                    break;
            }
            activity.commitFragment();
        }
    };





}
