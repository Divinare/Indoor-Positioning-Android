package com.joe.indoorlocalization;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by joe on 30/12/15.
 */

// Is used to draw graphic into CustomImageView
public class Drawer {

    // This method is overrided in both LocateActivity and CalibrateActivities for custom draw method
    public void draw(Canvas canvas, Context context, CustomImageView view, Point point) {
    }
}
