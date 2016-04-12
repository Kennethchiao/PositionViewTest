package com.chipsip.surfaceview_test;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.chipsip.surfaceview_test.Beacon.FinalCatchUnit;

import com.chipsip.surfaceview_test.Beacon.PositionUnit;

import com.chipsip.surfaceview_test.Draw.PlanViewUnit;
import com.chipsip.surfaceview_test.Draw.TargetViewUnit;

import com.chipsip.surfaceview_test.Setting.BluetoothUnit;
import com.chipsip.surfaceview_test.Setting.OrientationUnit;

public class MainActivity extends AppCompatActivity {
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
     //   setContentView(new PlanViewUnit(mContext));
        setContentView(new TargetViewUnit(mContext));
        PositionUnit postationUnit = new PositionUnit(mContext);
        FinalCatchUnit nav = new FinalCatchUnit(mContext);
        OrientationUnit orientation = new OrientationUnit(mContext);
        BluetoothUnit bluetoothUnit = new BluetoothUnit(mContext);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
