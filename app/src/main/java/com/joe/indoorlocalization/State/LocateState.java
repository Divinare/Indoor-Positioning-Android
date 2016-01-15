package com.joe.indoorlocalization.State;

import com.joe.indoorlocalization.Models.FingerPrint;

import java.util.ArrayList;

/**
 * Created by joe on 14/01/16.
 */
public class LocateState {

    static String TAG = LocateState.class.getSimpleName();

    // FingerPrints that a locating algorithm has given to draw
    private ArrayList<FingerPrint> locateAlgorithmFps = new ArrayList<>();
    private ArrayList<FingerPrint> drawWithRed = new ArrayList<>();
    private ArrayList<FingerPrint> drawWithCyan = new ArrayList<>();


    private String currentAlgorithm = "K_NearestSignal";

    public LocateState() {

    }

    public void setLocateAlgorithmFps(ArrayList<FingerPrint> fps) {
        this.locateAlgorithmFps = fps;
    }
    public ArrayList<FingerPrint> getLocateAlgorithmFps() {
        return this.locateAlgorithmFps;
    }

    public void setDrawWithRed(ArrayList<FingerPrint> fps) {
        this.drawWithRed = fps;
    }

    public ArrayList<FingerPrint> getDrawWithRed() {
        return this.drawWithRed;
    }

    public void setDrawWithCyan(ArrayList<FingerPrint> fps) {
        this.drawWithCyan = fps;
    }

    public ArrayList<FingerPrint> getDrawWithCyan() {
        return this.drawWithCyan;
    }


    public void changeCurrentAlgorithm(String algorithm) {
        this.currentAlgorithm = algorithm;
    }
    public String getCurrentAlgorithm() {
        return this.currentAlgorithm;
    }

}
