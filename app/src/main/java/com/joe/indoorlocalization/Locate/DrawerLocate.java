package com.joe.indoorlocalization.Locate;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import com.joe.indoorlocalization.Drawer;

/**
 * Created by joe on 30/12/15.
 */



public class DrawerLocate extends Drawer {

    static String TAG = DrawerLocate.class.getSimpleName();

    @Override
    public void draw(Canvas canvas, PointF point) {
        Log.d(TAG, "at drawerLocate");
    }
}
