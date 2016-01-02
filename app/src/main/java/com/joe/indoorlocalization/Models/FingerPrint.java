package com.joe.indoorlocalization.Models;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joe on 01/12/15.
 */
public class FingerPrint {

    private float x;
    private float y;
    private int z;
    //private int timestamp;
    private ArrayList<Scan> scans = new ArrayList();

    public FingerPrint() {

    }


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
        return this.scans; // Scan.find(Scan.class, "scan = ?", "" + this.getId());
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
}
