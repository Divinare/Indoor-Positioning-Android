package com.joe.indoorlocalization.Models;

import java.util.ArrayList;

/**
 * Created by joe on 01/12/15.
 */
public class FingerPrint {

    private float x;
    private float y;
    private int z;
    private ArrayList<Scan> scans = new ArrayList();

    // For locating
    private int distance = 0;
    private int macNotFoundCount = 0;

    public FingerPrint(int z, float x, float y) {
        this.z = z;
        this.x = x;
        this.y = y;
    }

    public int getZ() {
        return this.z;
    }
    public float getX() {
        return this.x;
    }
    public float getY() {
        return this.y;
    }

    public ArrayList<Scan> getScans() {
        return this.scans;
    }

    public void setScans(ArrayList<Scan> scans) {
        this.scans = scans;
    }

    public void addScan(Scan scan) {
        this.scans.add(scan);
    }

    public void setZ(int z) {
        this.z = z;
    }
    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }

    public void resetDistance() {
        this.distance = 0;
    }
    public void addToDistance(int num) {
        this.distance += num;
    }
    public int getDistance() {
        return this.distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public void increaseMacNotFoundCount() {
        this.macNotFoundCount++;
    }
    public int getMacNotFoundCount() {
        return this.macNotFoundCount;
    }
    public void resetMacNotFoundCount() {
        this.macNotFoundCount = 0;
    }
}
