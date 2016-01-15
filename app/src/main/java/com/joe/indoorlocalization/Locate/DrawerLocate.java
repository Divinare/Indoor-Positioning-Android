package com.joe.indoorlocalization.Locate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.joe.indoorlocalization.R;
import com.joe.indoorlocalization.State.ApplicationState;
import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.Drawer;
import com.joe.indoorlocalization.IndoorLocalization;
import com.joe.indoorlocalization.Models.FingerPrint;
import com.joe.indoorlocalization.State.LocateState;

import java.util.ArrayList;

/**
 * Created by joe on 30/12/15.
 */



public class DrawerLocate extends Drawer {

    static String TAG = DrawerLocate.class.getSimpleName();

    @Override
    public void draw(Canvas canvas, Context context, CustomImageView view, Point point) {
        ApplicationState state = ((IndoorLocalization)context.getApplicationContext()).getApplicationState();

        drawLocateAlgorithmFps(canvas, context, view);

        int z = (int) Math.round(state.getZ());
        if(state.getCurrentFloor() != z) {
            return;
        }
        // Drawing user's location
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);

        Point imagePoint = new Point(state.getX(), state.getY());
        Point screenPoint = view.convertImagePointToScreenPoint(imagePoint);

        canvas.drawCircle(screenPoint.x, screenPoint.y, 20, paint);

    }

    private void drawLocateAlgorithmFps(Canvas canvas, Context context, CustomImageView view) {
        ApplicationState state = ((IndoorLocalization)context.getApplicationContext()).getApplicationState();
        LocateState lState = state.locateState;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);

        drawFps(canvas, context, view, paint, lState.getLocateAlgorithmFps(), state);
        lState.setLocateAlgorithmFps(new ArrayList<FingerPrint>());

        paint.setColor(Color.RED);
        drawFps(canvas, context, view, paint, lState.getDrawWithRed(), state);
        lState.setDrawWithRed(new ArrayList<FingerPrint>());

        paint.setColor(Color.CYAN);
        drawFps(canvas, context, view, paint, lState.getDrawWithCyan(), state);
        lState.setDrawWithCyan(new ArrayList<FingerPrint>());
    }

    private void drawFps(Canvas canvas, Context context, CustomImageView view, Paint paint, ArrayList<FingerPrint> fpsToDraw, ApplicationState state) {
        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        TextView currentFloorView = (TextView) rootView.findViewById(R.id.floorLocate);

        String currentFloorText = "" + currentFloorView.getText();
        int currentFloor;
        if(currentFloorText.equals("basement")) {
            currentFloor = 0;
        } else {
            currentFloor = Integer.parseInt(currentFloorText);
        }

        for(FingerPrint fp : fpsToDraw) {
            int z = Math.round(fp.getZ());
            if(currentFloor == z) {
                Point imagePoint = new Point((int) fp.getX(), (int) fp.getY());
                Point screenPoint = view.convertImagePointToScreenPoint(imagePoint);
                canvas.drawCircle(screenPoint.x, screenPoint.y, 15, paint);
            }
        }
    }
}
