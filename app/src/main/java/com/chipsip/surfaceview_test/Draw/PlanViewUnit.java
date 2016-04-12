package com.chipsip.surfaceview_test.Draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;

import com.chipsip.surfaceview_test.Beacon.CatchAlgorithmUnit;
import com.chipsip.surfaceview_test.Beacon.FinalCatchUnit;
import com.chipsip.surfaceview_test.Beacon.PositionUnit;
import com.chipsip.surfaceview_test.Beacon.ScanBeaconUnit;
import com.chipsip.surfaceview_test.R;
import com.chipsip.surfaceview_test.Setting.OrientationUnit;

/**
 * Created by kenneth on 2016/3/31.
 */
public class PlanViewUnit extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = "PlanViewUnit";

    private SurfaceHolder surfaceHolder;
    private Thread thread;
    private PositionUnit positionUnit;
    private FinalCatchUnit CatchUnit;
    private ScanBeaconUnit mbeacon;
    private OrientationUnit Orientation;
    private Canvas mCanvas, c;
    private Paint paint;
    private CatchAlgorithmUnit catchAlgorithmUnit = null;
    private Bitmap Background = null, Small_Background = null, NavigationArrow = null, Small_NavigationArrow = null;

    private int ScreenW, ScreenH,
            mPosition_x = 0, mPosition_y = 0,
            NavigationArrow_IconCenter = 30,
            FinalCatch = 0;
    private float PlanDegree = 0.0f, IconDegree = 0.0f, Catch_Beacon_Rssi = 0.0f;

    private boolean Running = true;

    public PlanViewUnit(Context context) {
        super(context);
        CatchUnit = new FinalCatchUnit(context);
        Orientation = new OrientationUnit(context);
        thread = new Thread(this);
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        positionUnit = new PositionUnit(context);

        this.setKeepScreenOn(true);// 保持螢幕常亮


    }

    public void getIconRotateDegree() {
        IconDegree = Orientation.getIconRadian();
    }

    public void getPlanRotateDegree() {
        PlanDegree = Orientation.setPlanRotateDegree(80);
    }

    public void getBeacon_Data() {
        mbeacon = CatchUnit.getBeacon();
    }

    public void getFinalCatch_Beacon() {
        FinalCatch = CatchUnit.getFinalCatch();
    }

    public void getBeacon_Rssi() {
        try {
            Catch_Beacon_Rssi = catchAlgorithmUnit.getRssi();
        } catch (NullPointerException ne) {
            Log.d(TAG, "getBeacon_Rssi: Null point");
        }

    }

    public void setPosition() {
        //資策會的座標會更新在這

        mPosition_x = positionUnit.mPeople_x;
        mPosition_y = positionUnit.mPeople_y;

        if (mPosition_x == 0 && mPosition_y == 0) {
            mPosition_x = 370;
            mPosition_y = 280;
        }
        Log.d(TAG, "mPosition_x : " + mPosition_x + " mPosition_y : " + mPosition_y);
    }

    public void setPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void setBitmap() {

        Background = BitmapFactory.decodeResource(getResources(), R.drawable.planview_config).copy(Bitmap.Config.RGB_565, true);
        Small_Background = Bitmap.createScaledBitmap(Background, ScreenW, ScreenH, true);

        NavigationArrow = BitmapFactory.decodeResource(getResources(), R.drawable.nav);
        Small_NavigationArrow = Bitmap.createScaledBitmap(NavigationArrow, NavigationArrow_IconCenter, NavigationArrow_IconCenter, true);

    }

    public void startAnimation(Animation animation) {
        super.startAnimation(animation);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        ScreenH = this.getHeight();
        ScreenW = this.getWidth();

        thread.start();
    }

    private void draw_Plan() {
        setBitmap();

        getPlanRotateDegree();
        try {
            mCanvas = surfaceHolder.lockCanvas();
            mCanvas.save();
            mCanvas.drawColor(Color.WHITE);
            mCanvas.drawBitmap(Small_Background, 0, 0, null);
            mCanvas.restore();
            surfaceHolder.unlockCanvasAndPost(mCanvas);
        } catch (Exception e) {
            Log.d(TAG, "draw_Plan: Not in ");
        }
    }

    private void draw_Icon() {
        setPosition();
        getIconRotateDegree();
        try {

            Canvas c = null;
            try {
                c = surfaceHolder.lockCanvas();
                if (c != null) {
                    mCanvas.rotate(-IconDegree + PlanDegree, mPosition_x + 15, mPosition_y + 18);
                    mCanvas.drawBitmap(Small_NavigationArrow, mPosition_x, mPosition_y, null);
                }
            } finally {
                if (c != null)
                    surfaceHolder.unlockCanvasAndPost(c);
            }
            Small_NavigationArrow.recycle(); //记得回收内存位图

//            mCanvas.rotate(-IconDegree, mPosition_x + 15, mPosition_y + 18);
//            mCanvas.drawBitmap(Small_NavigationArrow, mPosition_x, mPosition_y, null);


        } catch (Exception ex) {
            Log.d(TAG, "draw_Icon: Not in ");
        }
    }

    private void draw_Beacon() {
        getFinalCatch_Beacon();
        getBeacon_Data();
        getBeacon_Rssi();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Running = false;
    }

    @Override
    public void run() {
        while (Running) {
            setPaint();


            draw_Plan();
            draw_Icon();
            draw_Beacon();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
