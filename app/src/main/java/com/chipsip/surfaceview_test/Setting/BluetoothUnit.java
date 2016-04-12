package com.chipsip.surfaceview_test.Setting;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by kenneth on 2016/4/1.
 */
public class BluetoothUnit {
    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private boolean mEnableBeacon = true;

    public BluetoothUnit(Context context) {

        mContext = context;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        getBluetoothAdapter();
        setBluetooth(mEnableBeacon);
    }

    public Object getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext, "Device not support Bluetooth", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public boolean setBluetooth(boolean enable) {
        boolean isEnabled = mBluetoothAdapter.isEnabled();

        if (enable && !isEnabled) {
            return mBluetoothAdapter.enable();
        } else if (!enable && isEnabled) {
            return mBluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }
}
