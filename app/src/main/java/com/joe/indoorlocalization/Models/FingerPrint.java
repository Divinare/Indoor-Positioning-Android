package com.joe.indoorlocalization.Models;

import com.orm.SugarRecord;

/**
 * Created by joe on 22/11/15.
 */
public class FingerPrint extends SugarRecord<FingerPrint> {

    private String mac;
    private String RSSI;
    private String network;

    public FingerPrint() {

    }

    public FingerPrint(String mac, String RSSI){
        this.mac = mac;
        this.RSSI = RSSI;
    }

    public FingerPrint(String mac, String RSSI, String network){
        this.mac = mac;
        this.RSSI = RSSI;
        this.network = network;
    }

    public String getMac() { return this.mac; };
    public String getRSSI() { return this.RSSI; };
    public String getNetwork() { return this.network; };
    public void setMacs(String mac) { this.mac = mac; }
    public void setRSSIs(String RSSI) { this.RSSI = RSSI; }
    public void setNetworks(String network) { this.network = network; }

}
