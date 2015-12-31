package com.joe.indoorlocalization.Models;

/**
 * Created by joe on 22/11/15.
 */
public class Scan {

    private FingerPrint fingerPrint;
    private String mac;
    private String RSSI;
    private String network;

    public Scan() {

    }

    public Scan(FingerPrint fingerPrint, String mac, String RSSI) {
        this.fingerPrint = fingerPrint;
        this.mac = mac;
        this.RSSI = RSSI;
    }

    public Scan(FingerPrint fingerPrint, String mac, String RSSI, String network) {
        this.fingerPrint = fingerPrint;
        this.mac = mac;
        this.RSSI = RSSI;
        this.network = network;
    }

    public FingerPrint getFingerPrint() {
        return this.fingerPrint;
    }
    public String getMac() {
        return this.mac;
    };
    public String getRSSI() {
        return this.RSSI;
    };
    public String getNetwork() {
        return this.network;
    };
    public void setScan(FingerPrint fingerPrint) {
        this.fingerPrint = fingerPrint;
    }
    public void setMacs(String mac) {
        this.mac = mac;
    }
    public void setRSSIs(String RSSI) {
        this.RSSI = RSSI;
    }
    public void setNetworks(String network) {
        this.network = network;
    }

}
