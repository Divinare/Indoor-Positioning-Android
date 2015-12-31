package com.joe.indoorlocalization;

import android.app.Application;

/**
 * Created by joe on 31/12/15.
 */
public class ApplicationState extends Application {


    private int x;
    private int y;
    private int z;

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
    public int getZ() {
        return this.z;
    }
    public void setZ(int z) {
        this.z = z;
    }

}
