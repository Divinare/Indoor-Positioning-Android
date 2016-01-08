package com.joe.indoorlocalization.Calibration;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joe.indoorlocalization.ApplicationState;
import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.Drawer;
import com.joe.indoorlocalization.IndoorLocalization;
import com.joe.indoorlocalization.Models.FingerPrint;
import com.joe.indoorlocalization.R;

import java.util.ArrayList;

/**
 * Created by joe on 30/12/15.
 */
public class DrawerCalibrate extends Drawer {

    static String TAG = DrawerCalibrate.class.getSimpleName();
    private ApplicationState state;
    private CalibrationState cState;
    private Context context;

    public DrawerCalibrate(Context context) {
        this.context = context;
        this.state = ((IndoorLocalization)context.getApplicationContext()).getApplicationState();
        this.cState = this.state.calibrationState;
    }

    private void drawScans(Canvas canvas, Context context, CustomImageView view) {
        for(FingerPrint fp: this.state.getFingerPrints()) {
            Point screenPoint = view.convertImagePointToScreenPoint(new Point((int) fp.getX(), (int) fp.getY()));

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.RED);

            canvas.drawCircle(screenPoint.x, screenPoint.y, 15, paint);

        }
    }

    private void lineCalibrate(Canvas canvas, Context context, CustomImageView view, Point screenPoint) {
        Log.d(TAG, "at drawScanLine");
        if(cState.getLockToDrawing()) {
            Log.d(TAG, "Drawing was locked.");
            drawPointsAndLine(canvas, view, cState);
            return;
        }
        Point imagePoint = view.convertScreenPointToImagePoint(new Point(screenPoint.x, screenPoint.y));

        //Not drawing outside of the image
        if(!view.isInsideImage(imagePoint)) {
            return;
        }

        int distanceForPoint1 = Integer.MAX_VALUE;
        int distanceForPoint2 = Integer.MAX_VALUE;
        int maxDistanceForSelection = 150; // Todo, calc it by looking screen width + also zoom level could affect it!

        if (cState.point1 != null) {
            distanceForPoint1 = calcDistanceBetweenTwoPoints(cState.point1, imagePoint);
        }
        if (cState.point2 != null) {
            distanceForPoint2 = calcDistanceBetweenTwoPoints(cState.point2, imagePoint);
        }
        // Handle selection of points
        if ((cState.point1Locked && distanceForPoint1 < maxDistanceForSelection) || (cState.point2Locked && distanceForPoint2 < maxDistanceForSelection)) {
            if(cState.pointsSelectable()) {
                if (distanceForPoint1 < distanceForPoint2) {
                    cState.setSelectedPoint("point1");
                } else {
                    cState.setSelectedPoint("point2");
                }
            } else {
                Log.d(TAG, "Points weren't selectable, should they be?");
            }
        } else if(!cState.getSelectedPoint().equals("")) {
            if(cState.getSelectedPoint().equals("point1") && !cState.point1Locked)  {
                cState.point1 = new Point(imagePoint.x, imagePoint.y);
            } else if(cState.getSelectedPoint().equals("point2") && !cState.point2Locked){
                cState.point2 = new Point(imagePoint.x, imagePoint.y);
            }

            View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
            TextView xTextView = (TextView) rootView.findViewById(R.id.positionX);
            xTextView.setText("x: " + imagePoint.x);
            TextView yTextView = (TextView)rootView.findViewById(R.id.positionY);
            yTextView.setText("y: " + imagePoint.y);
            TextView zTextView = (TextView)rootView.findViewById(R.id.positionZ);
            zTextView.setText("z: " + state.getCurrentFloor());
            Log.d(TAG, "Curr loor " + state.getCurrentFloor());

        }
        drawPointsAndLine(canvas, view, cState);
        if(cState.pointsExist()) {
            View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
            Button startScanButton = (Button) rootView.findViewById(R.id.btnStartRecording);
            if(startScanButton != null) {
                startScanButton.setEnabled(true);
            }
        }
    }

    private void drawPointsAndLine(Canvas canvas, CustomImageView view, CalibrationState cState) {
        drawPoint(canvas, view, cState.point1);
        drawPoint(canvas, view, cState.point2);

        if(cState.pointsExist()) {
            drawLine(canvas, view, cState.point1, cState.point2);
        }
    }

    //       ____________________
    //      /       2          2
    //    \/ (y2-y1)  + (x2-x1)
    private int calcDistanceBetweenTwoPoints(Point point1, Point point2) {
        return (int)(Math.sqrt(Math.pow((point2.x - point1.x), 2) + Math.pow((point2.y - point1.y), 2)));
    }

    private void drawLine(Canvas canvas, CustomImageView view, Point imagePoint1, Point imagePoint2) {
        Point screenPoint1 = view.convertImagePointToScreenPoint(new Point(imagePoint1.x, imagePoint1.y));
        Point screenPoint2 = view.convertImagePointToScreenPoint(new Point(imagePoint2.x, imagePoint2.y));

        int startX = screenPoint1.x;
        int startY = screenPoint1.y;
        int stopX = screenPoint2.x;
        int stopY = screenPoint2.y;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);

        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private void drawPoint(Canvas canvas, CustomImageView view, Point point) {
        if(point != null) {
            Point screenPoint = view.convertImagePointToScreenPoint(new Point(point.x, point.y));
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            canvas.drawCircle(screenPoint.x, screenPoint.y, 20, paint);
        }
    }

    @Override
    public void draw(Canvas canvas, Context context, CustomImageView view, Point screenPoint) {
        if(this.state.calibrationState.showingScans()) {
            Log.d(TAG, "at calibrate draw, return cuz showing scans");
            drawScans(canvas, context, view);
        } else {
            lineCalibrate(canvas, context, view, screenPoint);
        }
    }
}
