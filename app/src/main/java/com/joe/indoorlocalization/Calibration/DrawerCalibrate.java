package com.joe.indoorlocalization.Calibration;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joe.indoorlocalization.ApplicationState;
import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.Drawer;
import com.joe.indoorlocalization.IndoorLocalization;
import com.joe.indoorlocalization.Models.FingerPrint;
import com.joe.indoorlocalization.R;

/**
 * Created by joe on 30/12/15.
 */
public class DrawerCalibrate extends Drawer {

    static String TAG = DrawerCalibrate.class.getSimpleName();
    private ApplicationState state;
    private CalibrationState cState;

    public DrawerCalibrate(Context context) {
        this.state = ((IndoorLocalization)context.getApplicationContext()).getApplicationState();
        this.cState = this.state.calibrationState;
    }

    private void drawCurrentFingerPrints(Canvas canvas, CustomImageView view) {
        for(Point point: cState.getCurrentScanLocations()) {
            Point screenPoint = view.convertImagePointToScreenPoint(new Point(point.x, point.y));
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            canvas.drawCircle(screenPoint.x, screenPoint.y, 15, paint);
        }
    }

    private void handleShowScans(Canvas canvas, Context context, CustomImageView view, Point screenPoint) {
        drawScans(canvas, view);
        removeStartScanElements(context);
        possiblySelectScan(screenPoint, context, view, canvas);
    }

    private void removeStartScanElements(Context context) {
        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        Button btnStartRecording = (Button) rootView.findViewById(R.id.btnStartRecording);
        Button lockPointBtn = (Button) rootView.findViewById(R.id.lockPoint);

        if(btnStartRecording != null) {
            ((ViewGroup) btnStartRecording.getParent()).removeView(btnStartRecording);
        }
        if(lockPointBtn != null) {
            ((ViewGroup) lockPointBtn.getParent()).removeView(lockPointBtn);
        }
    }

    private void drawScans(Canvas canvas, CustomImageView view) {

        for(FingerPrint fp: this.state.getFingerPrints()) {
            if(this.state.getCurrentFloor() == fp.getZ()) {
                Point screenPoint = view.convertImagePointToScreenPoint(new Point((int) fp.getX(), (int) fp.getY()));
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setColor(Color.RED);

                canvas.drawCircle(screenPoint.x, screenPoint.y, 15, paint);
            }
        }
    }

    private void possiblySelectScan(Point screenPoint, Context context, CustomImageView view,  Canvas canvas) {
        Point imagePoint = view.convertScreenPointToImagePoint(new Point(screenPoint.x, screenPoint.y));

        int nearestZ = 0;
        float nearestX = 0;
        float nearestY = 0;
        int nearestDistance = Integer.MAX_VALUE;

        for(FingerPrint fp : this.state.getFingerPrints()) {

           if(fp.getZ() == this.state.getCurrentFloor()) {
               Point point1 = new Point((int)fp.getX(), (int)fp.getY());
               Point point2 = new Point(imagePoint.x, imagePoint.y);
               int currentDistance = calcDistanceBetweenTwoPoints(point1, point2);
               if(currentDistance < nearestDistance) {
                   nearestZ = fp.getZ();
                   nearestX = fp.getX();
                   nearestY = fp.getY();
                   nearestDistance = currentDistance;
               }
           }
        }
        if(nearestDistance < 150) {
            createRemoveScanButton(context, view, nearestX, nearestY, nearestZ);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.CYAN);
            Point screenPointForSelectedScan = view.convertImagePointToScreenPoint(new Point((int)nearestX, (int)nearestY));
            canvas.drawCircle(screenPointForSelectedScan.x, screenPointForSelectedScan.y, 25, paint);
        }

    }

    // REMOVE btn
    private void createRemoveScanButton(Context context, final CustomImageView view, final float x, final float y, final int z) {
        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        LinearLayout bottomBar = (LinearLayout) rootView.findViewById(R.id.calibrationBottomBar);

        Button removeScan = (Button) rootView.findViewById(R.id.btnRemoveRecording);
        boolean removeScanBtnExist = true;
        if(removeScan == null) {
            removeScan = new Button(context);
            removeScan.setId(R.id.btnRemoveRecording);
            removeScanBtnExist = false;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;
            removeScan.setLayoutParams(params);
        }
        removeScan.setText("Remove scan (x: " + x + " y: " + y + ")");
        removeScan.setBackgroundResource(R.drawable.buttonshape);
        removeScan.setTextColor(Color.WHITE);
        removeScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state.removeFingerPrint(z, x, y);
                ViewGroup parentView = (ViewGroup) v.getParent();
                parentView.removeView(v);
                view.invalidate();
            }
        });
        if(!removeScanBtnExist) {
            bottomBar.addView(removeScan);
        }
    }

    private void lineCalibrate(Canvas canvas, Context context, CustomImageView view, Point screenPoint) {
        if(cState.getLockToDrawing()) {
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
        if(cState.pointsExist()) {
            drawLine(canvas, view, cState.point1, cState.point2);
        }
        drawPoints(canvas, view);
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

    private void drawPoints(Canvas canvas, CustomImageView view) {
        Paint paintCircle = new Paint();
        paintCircle.setAntiAlias(true);
        paintCircle.setColor(Color.RED);

        Paint paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(40);
        paintText.setStrokeWidth(10);

        int circleWidth = 30;
        Point point1 = cState.point1;
        Point point2 = cState.point2;
        if(point1 != null) {
            Point screenPoint = view.convertImagePointToScreenPoint(new Point(point1.x, point1.y));
            canvas.drawCircle(screenPoint.x, screenPoint.y, circleWidth, paintCircle);
            canvas.drawText("S", (screenPoint.x-(circleWidth/2)+3), (screenPoint.y+(circleWidth/2)), paintText);
        }
        if(point2 != null) {
            Point screenPoint = view.convertImagePointToScreenPoint(new Point(point2.x, point2.y));
            canvas.drawCircle(screenPoint.x, screenPoint.y, circleWidth, paintCircle);
            canvas.drawText("E", (screenPoint.x-(circleWidth/2)+3), (screenPoint.y+(circleWidth/2)), paintText);
        }
    }

    @Override
    public void draw(Canvas canvas, Context context, CustomImageView view, Point screenPoint) {
        if(this.cState.getViewCurrentFingerPrints() && this.cState.getCurrentScanLocations().size() > 0) {
            drawCurrentFingerPrints(canvas, view);
        } else if (this.cState.showingScans()) {
            handleShowScans(canvas, context, view, screenPoint);
        } else {
            lineCalibrate(canvas, context, view, screenPoint);
        }
    }
}
