package com.joe.indoorlocalization.Models;

import com.orm.SugarRecord;

/**
 * Created by joe on 22/11/15.
 */
public class FingerPrint extends SugarRecord<FingerPrint> {

    private Scan scan;
    private String mac;
    private String RSSI;
    private String network;

    public FingerPrint() {

    }

    public FingerPrint(Scan scan, String mac, String RSSI) {
        this.scan = scan;
        this.mac = mac;
        this.RSSI = RSSI;
    }

    public FingerPrint(Scan scan, String mac, String RSSI, String network) {
        this.scan = scan;
        this.mac = mac;
        this.RSSI = RSSI;
        this.network = network;
    }

    public Scan getScan() {
        return this.scan;
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
    public void setScan(Scan scan) {
        this.scan = scan;
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
