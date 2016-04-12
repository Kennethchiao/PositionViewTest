package com.chipsip.surfaceview_test.Beacon;

/**
 * Created by kenneth on 2016/3/31.
 */


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.THLight.USBeacon.App.Lib.iBeaconData;
import com.THLight.USBeacon.App.Lib.iBeaconScanManager;

public class FinalCatchUnit implements iBeaconScanManager.OniBeaconScan {
    private static final int MSG_SCAN_IBEACON = 100;
    private static final String TAG = "FinalCatchUnit";

    private ScanBeaconUnit mBeacon;
    private iBeaconScanManager miScaner = null;
    private CatchAlgorithmUnit mBeaconCatcher;

    private boolean mEnableBeacon = true, mRepeatScan = true;
    private int FinalCatch = 0;


    public FinalCatchUnit(Context context) {
        miScaner = new iBeaconScanManager(context, this);
        mBeaconCatcher = new CatchAlgorithmUnit();

        setmHandler();
    }

    @Override
    public void onScaned(iBeaconData iBeaconData) {
        mBeacon = ScanBeaconUnit.copyOf(iBeaconData);
        final String msg = "beaconFound: " + mBeacon.beaconUuid + ", " + mBeacon.rssi;
        FinalCatch = mBeaconCatcher.getCatched(mBeacon.minor, mBeacon.rssi);
        //  Log.d(TAG, "Beacon ID :  " + mBeacon + "FinalCatch : " + FinalCatch);
    }

    public int getFinalCatch() {
        return FinalCatch;
    }

    public ScanBeaconUnit getBeacon() {
        return mBeacon;
    }

    public void setmHandler() {
        if (mEnableBeacon) {
            mRepeatScan = true;
            Message msg = Message.obtain(mHandler, MSG_SCAN_IBEACON, 100, 120);//設定Thread  ps:Thread在jar檔內
            msg.sendToTarget();
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SCAN_IBEACON: {
                    int timeForScaning = msg.arg1;
                    int nextTimeStartScan = msg.arg2;
                    miScaner.startScaniBeacon(timeForScaning);
                    if (mRepeatScan) {
                        this.sendMessageDelayed(Message.obtain(msg), nextTimeStartScan);
                    }
                }
                break;
            }
        }
    };

}