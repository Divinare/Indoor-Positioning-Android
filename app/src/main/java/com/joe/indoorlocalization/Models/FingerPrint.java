package com.joe.indoorlocalization.Models;

import com.orm.SugarRecord;

/**
 * Created by joe on 22/11/15.
 */
public class FingerPrint extends SugarRecord<FingerPrint> {

    private float x;
    private float y;
    private int z;
    private String mac;
    private String RSSI;
    private String network;

    public FingerPrint() {

    }

    public FingerPrint(float x, float y, int z, String mac, String RSSI, String network){
        this.x = x;
        this.y = y;
        this.z = z;
        this.mac = mac;
        this.RSSI = RSSI;
        this.network = network;
    }

    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public int getZ() { return this.z; }
    public String getMac() { return this.mac; };
    public String getRSSI() { return this.RSSI; };
    public String getNetwork() { return this.network; };
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setZ(int z) { this.z = z; }
    public void setMacs(String mac) { this.mac = mac; }
    public void setRSSIs(String RSSI) { this.RSSI = RSSI; }
    public void setNetworks(String network) { this.network = network; }

}
