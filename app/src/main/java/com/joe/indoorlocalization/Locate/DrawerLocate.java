package com.joe.indoorlocalization.Locate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import com.joe.indoorlocalization.ApplicationState;
import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.Drawer;
import com.joe.indoorlocalization.IndoorLocalization;
import com.joe.indoorlocalization.Models.FingerPrint;

import java.util.ArrayList;

/**
 * Created by joe on 30/12/15.
 */



public class DrawerLocate extends Drawer {

    static String TAG = DrawerLocate.class.getSimpleName();

    @Override
    public void draw(Canvas canvas, Context context, CustomImageView view, Point point) {
        ApplicationState state = ((IndoorLocalization)context.getApplicationContext()).getApplicationState();
        int z = (int) Math.round(state.getZ());
        if(state.getCurrentFloor() != z) {
            return;
        }
        drawNearestFps(canvas, context, view);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);

        Point imagePoint = new Point(state.getX(), state.getY());
        Point screenPoint = view.convertImagePointToScreenPoint(imagePoint);

        canvas.drawCircle(screenPoint.x, screenPoint.y, 20, paint);

    }

    private void drawNearestFps(Canvas canvas, Context context, CustomImageView view) {
        ApplicationState state = ((IndoorLocalization)context.getApplicationContext()).getApplicationState();
        if(state.getNearestFps().size() > 0) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.GREEN);
            for(FingerPrint fp : state.getNearestFps()) {
                int z = Math.round(fp.getZ());
                if(state.getCurrentFloor() == z) {
                    Point imagePoint = new Point((int) fp.getX(), (int) fp.getY());
                    Point screenPoint = view.convertImagePointToScreenPoint(imagePoint);

                    canvas.drawCircle(screenPoint.x, screenPoint.y, 15, paint);
                }
            }
            state.setNearestFps(new ArrayList<FingerPrint>());
        }
    }
}
