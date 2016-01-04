package com.joe.indoorlocalization.Locate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;
import android.widget.TextView;

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

    @Override
    public void draw(Canvas canvas, Context context, CustomImageView view, Point point) {
        ApplicationState state = ((IndoorLocalization)context.getApplicationContext()).getApplicationState();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);

        Point imagePoint = new Point(state.getX(), state.getY());
        Point screenPoint = view.convertImagePointToScreenPoint(imagePoint);

        canvas.drawCircle(screenPoint.x, screenPoint.y, 20, paint);

        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        TextView locationCoordinates = (TextView) rootView.findViewById(R.id.locationCoordinates);
        locationCoordinates.setText("x: " + state.getX() + " y: " + state.getY() + " z: " + state.getZ());

    }
}
