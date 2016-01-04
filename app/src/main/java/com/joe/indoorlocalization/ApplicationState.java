package com.joe.indoorlocalization;

import android.app.Application;

import com.joe.indoorlocalization.Models.FingerPrint;
import com.joe.indoorlocalization.Models.Scan;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by joe on 31/12/15.
 */
public class ApplicationState extends Application {


    private int x;
    private int y;
    private double z = 1.0;
    private int currentFloor = 1;

    private ArrayList<FingerPrint> fingerPrints = new ArrayList();

    public void emptyCurrentDatabase() {
        this.fingerPrints = new ArrayList<>();
    }

    public void addFingerPrint(FingerPrint fp) {
        this.fingerPrints.add(fp);
    }

    public ArrayList getFingerPrints() {
        return this.fingerPrints;
    }

    public int getX() {
        return this.x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return this.y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public double getZ() {
        return this.z;
    }
    public void setZ(double z) {
        this.z = z;
    }
    public int getCurrentFloor() {
        return this.currentFloor;
    }
    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }



}
