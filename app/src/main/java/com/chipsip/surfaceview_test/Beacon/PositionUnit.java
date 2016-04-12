package com.chipsip.surfaceview_test.Beacon;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import com.hereapps.ibeacon.indoorlocalization.IndoorLocation;
import com.hereapps.ibeacon.indoorlocalization.Positioner;
import com.hereapps.ibeacon.indoorlocalization.PositionerConsumer;

import java.util.List;

/**
 * Created by kenneth on 2016/4/12.
 */
public class PositionUnit implements PositionerConsumer {
    protected static final String TAG = "PositionUnit";

    private Positioner positioner;
    public int mPeople_x = 0, mPeople_y = 0, PositionBeacon_Id = 0;

    public PositionUnit(Context context) {

        try {
//            this.positioner = new Positioner(this);
            // init with desired accuracy level
            this.positioner = new Positioner(context, Positioner.ACCURACY_LEVEL_4);
            this.positioner.setIdlePeriod(1000);
            this.positioner.setScanPeriod(1000);
            this.positioner.bind(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPositionerStart() {
        Log.d(TAG, "Position Start");
        if (this.positioner != null) {
            this.positioner.start();
        }
    }

    @Override
    public void onPositionerReady() {

    }

    @Override
    public void onPositionUpdate(IndoorLocation indoorLocation, List<Point> list) {
        mPeople_x = indoorLocation.getPoint().x;
        mPeople_y = indoorLocation.getPoint().y;
        PositionBeacon_Id = indoorLocation.getNearestBeaconId();
        Log.d(TAG, "onPositionUpdate:  mPeople_x : " + mPeople_x + " mPeople_y : " + mPeople_y);
    }
}
