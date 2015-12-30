package com.joe.indoorlocalization.Calibration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.joe.indoorlocalization.Drawer;

/**
 * Created by joe on 30/12/15.
 */
public class DrawerCalibrate extends Drawer {

    static String TAG = DrawerCalibrate.class.getSimpleName();

    @Override
    public void draw(Canvas canvas, PointF point) {
        Log.d(TAG, "at drawerCalibration");
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);

        canvas.drawCircle(point.x, point.y, 20, paint);
    }
}
