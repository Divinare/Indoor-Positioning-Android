package com.joe.indoorlocalization.Calibration;

import android.graphics.Point;

/**
 * Created by joe on 06/01/16.
 */
public class CalibrationState {

    private boolean showingScans = false;

    public String selectedPoint = "point1";
    public Point point1 = null;
    public boolean point1Locked = false;

    public Point point2 = null;
    public boolean point2Locked = false;

    public void toggleShowingScans() {
        this.showingScans = !this.showingScans;
    }
    public boolean showingScans() {
        return this.showingScans;
    }

    public void lockSelectedPoint() {
        if(this.selectedPoint.equals("point1")) {
            point1Locked = true;
            if(point2 == null) {
                selectedPoint = "point2";
            }
        } else if(this.selectedPoint.equals("point2")) {
            point2Locked = true;
        }
        if(point1Locked && point2Locked) {
            this.selectedPoint = "";
        }
    }

    public void setSelectedPoint(String point) {
        // Dont allow selecting a point if one of the points is unlocked
        if(!this.point1Locked || !this.point2Locked) {
            return;
        }
        this.selectedPoint = point;
        if(point.equals("point1")) {
            this.point1Locked = false;
        } else {
            this.point2Locked = false;
        }
    }
}
