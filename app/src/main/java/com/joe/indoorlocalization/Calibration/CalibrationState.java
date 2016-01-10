package com.joe.indoorlocalization.Calibration;

import android.graphics.Point;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by joe on 06/01/16.
 */
public class CalibrationState {

    static String TAG = CalibrationState.class.getSimpleName();

    private boolean showingScans = false;

    private String selectedPoint = "point1";
    public Point point1 = null;
    public boolean point1Locked = false;
    public Point point2 = null;
    public boolean point2Locked = false;
    private boolean pointsSelectable = true;
    private boolean allowChangeFloor = true;
    private boolean allowShowScans = true;

    private ArrayList<Point> currentScanLocations = new ArrayList<>();

    private boolean viewCurrentFingerPrints = false;

    // Means that you can't select or reposition any points
    private boolean lockToDrawing = false;

    public String getSelectedPoint() {
        return this.selectedPoint;
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

    public void toggleShowingScans() {
        Log.d(TAG, "At toggle, allow? " + allowShowScans);
        if(this.allowShowScans) {
            this.showingScans = !this.showingScans;
        }
    }
    public boolean showingScans() {
        return this.showingScans;
    }

    public void lockSelectedPoint() {
        if(this.point1 == null && this.point2 == null) {
            return;
        }
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

    public boolean pointsExist() {
        if(this.point1 != null && this.point2 != null) {
            return true;
        }
        return false;
    }

    public void setPointsSelectable(boolean selectable) {
        this.pointsSelectable = selectable;
    }

    public boolean pointsSelectable() {
        return this.pointsSelectable;
    }

    public void setLockToDrawing(boolean lock) {
        this.lockToDrawing = lock;
    }
    public boolean getLockToDrawing() {
        return this.lockToDrawing;
    }

    public void setViewCurrentFingerPrints(boolean val) {
        this.viewCurrentFingerPrints = val;
    }
    public boolean getViewCurrentFingerPrints() {
        return this.viewCurrentFingerPrints;
    }

    public void resetCurrentScanLocations() {
        this.currentScanLocations = new ArrayList<>();
    }
    public void addToCurrentScanLocations(Point point) {
        this.currentScanLocations.add(point);
    }
    public ArrayList<Point> getCurrentScanLocations() {
        return this.currentScanLocations;
    }

    public boolean getAllowChangeFloor() {
        return this.allowChangeFloor;
    }
     public void setAllowChangeFloor(boolean allow) {
         this.allowChangeFloor = allow;
     }
    public boolean getAllowShowScans() {
        return this.allowShowScans;
    }
    public void setAllowShowScans(boolean value) {
        this.allowShowScans = value;
    }
    
}
