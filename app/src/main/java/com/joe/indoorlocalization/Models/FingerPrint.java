package com.joe.indoorlocalization.Models;

import com.orm.SugarRecord;

/**
 * Created by joe on 22/11/15.
 */
public class FingerPrint extends SugarRecord<FingerPrint> {

    private Long scanId;
    private String mac;
    private String RSSI;
    private String network;

    public FingerPrint() {

    }

    public FingerPrint(Long scanId, String mac, String RSSI) {
        this.scanId = scanId;
        this.mac = mac;
        this.RSSI = RSSI;
    }

    public FingerPrint(Long scanId, String mac, String RSSI, String network) {
        this.scanId = scanId;
        this.mac = mac;
        this.RSSI = RSSI;
        this.network = network;
    }

    public Long getScanId() {
        return this.scanId;
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
    public void setScanId(Long scanId) {
        this.scanId = scanId;
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
