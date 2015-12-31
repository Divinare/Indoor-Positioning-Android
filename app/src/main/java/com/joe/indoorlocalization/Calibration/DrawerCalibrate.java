package com.joe.indoorlocalization.Calibration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.Drawer;

/**
 * Created by joe on 30/12/15.
 */
public class DrawerCalibrate extends Drawer {

    static String TAG = DrawerCalibrate.class.getSimpleName();

    @Override
    public void draw(Canvas canvas, CustomImageView view, Point screenPoint) {
        Log.d(TAG, "at drawerCalibration");

        Point imagePoint = view.convertScreenPointToImagePoint(new Point(screenPoint.x, screenPoint.y));

        //Not drawing outside of the image
        if(!view.isInsideImage(imagePoint)) {
            return;
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);

        canvas.drawCircle(screenPoint.x, screenPoint.y, 20, paint);
    }
}
