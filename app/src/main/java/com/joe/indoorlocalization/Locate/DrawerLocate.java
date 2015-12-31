package com.joe.indoorlocalization.Locate;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import com.joe.indoorlocalization.Algorithms.DeterministicAlgorithm;
import com.joe.indoorlocalization.ApplicationState;
import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.Drawer;
import com.joe.indoorlocalization.IndoorLocalization;
import com.joe.indoorlocalization.R;

/**
 * Created by joe on 30/12/15.
 */



public class DrawerLocate extends Drawer {

    static String TAG = DrawerLocate.class.getSimpleName();
    private ApplicationState state;

    public DrawerLocate(Context context) {
        this.state = ((IndoorLocalization)context.getApplicationContext()).getApplicationState();
    }

    @Override
    public void draw(Canvas canvas, CustomImageView view, Point point) {
        Log.d(TAG, "at drawerLocate");

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        Log.d(TAG, "x is: " + state.getX() + " y is: " + state.getY());
        Point imagePoint = new Point(state.getX(), state.getY());

        Point screenPoint = view.convertImagePointToScreenPoint(imagePoint);

        canvas.drawCircle(screenPoint.x, screenPoint.y, 20, paint);

    }
}
