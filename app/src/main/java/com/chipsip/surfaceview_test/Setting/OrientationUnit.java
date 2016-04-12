package com.chipsip.surfaceview_test.Setting;

/**
 * Created by kenneth on 2016/4/1.
 */

import android.content.Context;
import android.util.Log;


import com.sime.gesture.SiMEFilteredOrientation;

import java.text.NumberFormat;

public class OrientationUnit {
    private static final String TAG = "OrientationUnit";

    private NumberFormat nf;
    private SiMEFilteredOrientation orientation;

    private float[] radiu = new float[3];
    private float degree = 0.0f, rotateDegree = 0.0f;

    public OrientationUnit(Context context) {

        orientation = new SiMEFilteredOrientation(context);
        orientation.onResume();

    }

    public void IconRadian() {
        try {
            radiu = orientation.getOrientation();
            degree = (float) (Math.toDegrees(radiu[0]) + 360) % 360;
            //    Log.d(TAG, "radiu : " + radiu[0] + " degree : " + degree);
        } catch (NullPointerException ex) {
            Log.d(TAG, "No Radian: ");
        }
    }

    public float getIconRadian() {
        IconRadian();
        return degree;
    }

    public float setPlanRotateDegree(int degree) {
        rotateDegree = degree;
        return rotateDegree;
    }

    public void NumberFormat() {
        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
    }
}
