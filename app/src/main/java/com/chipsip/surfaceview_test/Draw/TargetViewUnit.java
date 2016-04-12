package com.chipsip.surfaceview_test.Draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaRecorder;
import android.os.Handler;
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
 * Created by kenneth on 2016/4/6.
 */
public class TargetViewUnit extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = "TargetViewUnit";

    private SurfaceHolder surfaceHolder;
    private Thread thread;
    private PositionUnit positionUnit;
    private FinalCatchUnit CatchUnit;
    private ScanBeaconUnit mbeacon;
    private OrientationUnit Orientation;
    private Canvas mCanvas;
    private Paint paint, text;
    private CatchAlgorithmUnit catchAlgorithmUnit = null;
    private Bitmap Background = null, Small_Background = null, NavigationArrow = null, Size_Change_NavigationArrow = null;

    public Point Origin, Exhibition_1, Exhibition_2, Exhibition_3, Exhibition_4, Exhibition_5;


    private int ScreenW, ScreenH,
            mPosition_x = 0, mPosition_y = 0,
            NavigationArrow_IconCenter = 30,
            FinalCatch = 0, mPeople_x = 0, mPeople_y = 0, Final_x = 0, Final_y = 0, Orientation_Screen = 90;
    private float Atan2_Degree = 0.0f, IconDegree = 0.0f, Catch_Beacon_Rssi = 0.0f, NorthDegree = 0.0f;

    private boolean Running = true;

    public TargetViewUnit(Context context) {
        super(context);
        CatchUnit = new FinalCatchUnit(context);
        Orientation = new OrientationUnit(context);
        thread = new Thread(this);
        positionUnit = new PositionUnit(context);
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        this.setKeepScreenOn(true);// 保持螢幕常亮
    }

    public void getIconRotateDegree() {
        IconDegree = Orientation.getIconRadian();
    }

    public void setRotation() {
        PositionInit();
        Final_x = mPosition_x - mPeople_x;
        Final_y = mPosition_y - mPeople_y;
        Atan2_Degree = (float) (((float) Math.atan2(Final_x, Final_y)) * 180 / Math.PI);

        // Log.d(TAG, "Final_x : " + Final_x + " Final_y: " + Final_y + " Atan2_Degree: " + Atan2_Degree);

    }

    public void PositionInit() {

        Origin = new Point(5, 5);

        Exhibition_1 = new Point(0, 385);//右上
        Exhibition_2 = new Point(400, 385);//中上
        Exhibition_3 = new Point(780, 385);//左上
        Exhibition_4 = new Point(780, 5);//左下
        Exhibition_5 = new Point(400, 5);//中下

        // setPeoplePosition();
        setExhibitionPosition();
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            Origin.x = (int) (Math.random() * 801);
//            Origin.y = (int) (Math.random() * 401);
            //    Log.d(TAG, "Origin.x: " + Origin.x + " Origin.y : " + Origin.y);
            Origin.x = positionUnit.mPeople_x;
            Origin.y = positionUnit.mPeople_y;
            setPeoplePosition();
            handler.postDelayed(runnable, 1000);
        }

    };


    public void setPeoplePosition() {
        //============================
        //人的座標會更新在這

        mPeople_x = Origin.x;
        mPeople_y = Origin.y;
        //============================
    }

    public void setExhibitionPosition() {
        //============================
        //展品位置座標
        mPosition_x = Exhibition_5.x;
        mPosition_y = Exhibition_5.y;
        //============================
    }


    private void Draw() {
        setPaint();
        setBitmap();
        getIconRotateDegree();

        try {
            mCanvas = surfaceHolder.lockCanvas();

            mCanvas.save();
            mCanvas.translate(340, 128);//原點重設
            mCanvas.drawColor(Color.BLACK);
            mCanvas.rotate(IconDegree - Atan2_Degree, 60, 72);
            //  Log.d(TAG, "IconDegree : " + IconDegree);
            //  mCanvas.rotate(Atan2_Degree - Orientation_Screen, 60, 72);
            mCanvas.drawBitmap(Size_Change_NavigationArrow, 0, 0, null);
            mCanvas.restore();

            Log.d(TAG, "mPeople_x : " + mPeople_x + " mPeople_y : " + mPeople_y);
//== need fix
            if (0 <= mPeople_x && mPeople_x <= 20 || 0 <= mPeople_y && mPeople_y <= 20) {
                mCanvas.drawText("I'm in Origin", 350, 350, text);
            } else if (0 <= mPeople_x && mPeople_x <= 100 || 300 <= mPeople_y && mPeople_y <= 400) {
                mCanvas.drawText("I'm in A", 350, 350, text);
            } else if (300 <= mPeople_x && mPeople_x <= 500 || 300 <= mPeople_y && mPeople_y <= 400) {
                mCanvas.drawText("I'm in B", 350, 350, text);
            } else if (700 <= mPeople_x && mPeople_x <= 800 || 300 <= mPeople_y && mPeople_y <= 400) {
                mCanvas.drawText("I'm in C", 350, 350, text);
            } else if (700 <= mPeople_x && mPeople_x <= 800 || 0 <= mPeople_y && mPeople_y <= 100) {
                mCanvas.drawText("I'm in D", 350, 350, text);
            } else if (300 <= mPeople_x && mPeople_x <= 500 || 0 <= mPeople_y && mPeople_y <= 100) {
                mCanvas.drawText("I'm in E", 350, 350, text);
            }
//==

            surfaceHolder.unlockCanvasAndPost(mCanvas);
        } catch (Exception ex) {
            Log.d(TAG, "Draw: Not in ");
        }
    }

    public void setPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);

        text = new Paint();
        text.setAntiAlias(true);
        text.setColor(Color.WHITE);
        text.setTextSize(24);
    }

    public void setBitmap() {

        Background = BitmapFactory.decodeResource(getResources(), R.drawable.test5).copy(Bitmap.Config.RGB_565, true);
        Small_Background = Bitmap.createScaledBitmap(Background, 50, 20, true);

        NavigationArrow = BitmapFactory.decodeResource(getResources(), R.drawable.nav);
        Size_Change_NavigationArrow = Bitmap.createScaledBitmap(NavigationArrow, NavigationArrow_IconCenter * 4, NavigationArrow_IconCenter * 4, true);

    }

    public void startAnimation(Animation animation) {
        super.startAnimation(animation);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.start();
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Running = false;
        handler.removeCallbacks(runnable);
    }

    @Override
    public void run() {
        while (Running) {

            setRotation();
            Draw();


            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
