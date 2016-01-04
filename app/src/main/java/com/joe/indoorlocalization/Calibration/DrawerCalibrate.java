package com.joe.indoorlocalization.Calibration;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.Drawer;
import com.joe.indoorlocalization.R;

/**
 * Created by joe on 30/12/15.
 */
public class DrawerCalibrate extends Drawer {

    static String TAG = DrawerCalibrate.class.getSimpleName();

    @Override
    public void draw(Canvas canvas, Context context, CustomImageView view, Point screenPoint) {
        Point imagePoint = view.convertScreenPointToImagePoint(new Point(screenPoint.x, screenPoint.y));

        //Not drawing outside of the image
        if(!view.isInsideImage(imagePoint)) {
            return;
        }

        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        TextView xTextView = (TextView) rootView.findViewById(R.id.positionX);
        xTextView.setText("x: " + imagePoint.x);
        TextView yTextView = (TextView)rootView.findViewById(R.id.positionY);
        yTextView.setText("y: " + imagePoint.y);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);

        canvas.drawCircle(screenPoint.x, screenPoint.y, 20, paint);
    }
}
