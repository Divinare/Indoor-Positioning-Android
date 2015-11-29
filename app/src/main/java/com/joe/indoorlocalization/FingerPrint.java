package com.joe.indoorlocalization;

import com.orm.SugarRecord;

/**
 * Created by joe on 22/11/15.
 */
public class FingerPrint extends SugarRecord<FingerPrint> {

    private float x;
    private float y;
    private int z;
    private String macs;
    private String RSSIs;
    private String networks;

    public FingerPrint() {

    }

    public FingerPrint(float x, float y, int z, String macs, String RSSIs, String networks){
        this.x = x;
        this.y = y;
        this.z = z;
        this.macs = macs;
        this.RSSIs = RSSIs;
        this.networks = networks;
    }

    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public int getZ() { return this.z; }
    public String getMacs() { return this.macs; };
    public String getRSSIs() { return this.RSSIs; };
    public String getNetworks() { return this.networks; };
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setZ(int z) { this.z = z; }
    public void setMacs(String macs) { this.macs = macs; }
    public void setRSSIs(String RSSIs) { this.RSSIs = RSSIs; }
    public void setNetworks(String networks) { this.networks = networks; }

}
