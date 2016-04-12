package com.chipsip.surfaceview_test.Beacon; /**
 * Created by kenneth on 2016/3/16.
 */
import com.THLight.USBeacon.App.Lib.iBeaconData;

/**
 * ==============================================================
 */
public class ScanBeaconUnit extends iBeaconData {
    public long lastUpdate = 0;

    /**
     * ================================================
     */
    public static ScanBeaconUnit copyOf(iBeaconData iBeaconData) {
        ScanBeaconUnit newBeacon = new ScanBeaconUnit();

        newBeacon.beaconUuid = iBeaconData.beaconUuid;
        newBeacon.major = iBeaconData.major;
        newBeacon.minor = iBeaconData.minor;
        newBeacon.oneMeterRssi = iBeaconData.oneMeterRssi;
        newBeacon.rssi = iBeaconData.rssi;
        newBeacon.lastUpdate = 0;

        return newBeacon;
    }
}
