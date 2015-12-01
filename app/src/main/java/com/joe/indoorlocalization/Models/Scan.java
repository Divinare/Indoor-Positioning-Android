package com.joe.indoorlocalization.Models;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by joe on 01/12/15.
 */
public class Scan extends SugarRecord<FingerPrint> {

    private float x;
    private float y;
    private int z;
    //private int timestamp;
    private List<FingerPrint> fingerPrints;

    public Scan() {

    }


    public Scan(int z, float x, float y) {
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
    public List<FingerPrint> getFingerPrints() {
        return this.fingerPrints;
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

    public void setFingerPrints(List<FingerPrint> fingerPrints) {
        this.fingerPrints = fingerPrints;
    }
}
