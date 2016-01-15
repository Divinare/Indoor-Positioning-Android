package com.joe.indoorlocalization.Algorithms;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.joe.indoorlocalization.R;
import com.joe.indoorlocalization.State.ApplicationState;
import com.joe.indoorlocalization.Models.FingerPrint;
import com.joe.indoorlocalization.Models.Scan;
import com.joe.indoorlocalization.State.LocateState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by joe on 13/01/16.
 */
public class K_NearestSignal_Counterweight {
    static String TAG = K_NearestSignal.class.getSimpleName();
    private AlgorithmMain algorithmMain;
    private ApplicationState state;
    private LocateState lState;
    private Context context;

    private ArrayList<FingerPrint> usedUniqueFingerPrints = new ArrayList<>();

    int k = 5;

    public K_NearestSignal_Counterweight(AlgorithmMain algorithmMain, ApplicationState state, Context context) {
        this.algorithmMain = algorithmMain;
        this.state = state;
        this.lState = state.locateState;
        this.context = context;
    }

    public void calcLocation(StringBuilder currentFingerPrintData) {
        this.usedUniqueFingerPrints = new ArrayList<>();
        String currentFP = currentFingerPrintData.toString();

        String[] currentFPArray = currentFP.split(";");

        ArrayList<FingerPrint> fps = this.state.getFingerPrints();

        ArrayList<Scan> allScans = new ArrayList<>();
        for(FingerPrint fp : fps) {
            ArrayList<Scan> scans = fp.getScans();
            for(Scan scan : scans) {
                allScans.add(scan);
            }
        }

        int zSumAverage = 0;
        float xSumAverage = 0;
        float ySumAverage = 0;
        int averageSums = 0; // How many times zSumAverage, xSumAverage, ySumAverage have been increased
        for(int i = 0; i < currentFPArray.length-1; i=i+2) {
            String currentMac = currentFPArray[i]; // list goes mac;rssi;mac;rssi...
            String currentRSSI = currentFPArray[i + 1];
            if (Integer.parseInt(currentRSSI) == 0) {
                Log.i(TAG, "Go to the next iteration in the currentFPArray loop because the RSSI value was 0");
                continue;
            }
            List<Scan> scansByMacId = new ArrayList<>();

            for (Scan scan : allScans) {
                if (scan.getMac().equals(currentMac)) {
                    scansByMacId.add(scan);
                }
            }
            Map<Integer, Scan> distances = new HashMap<>();

            // Making distances array
            for(Scan scan: scansByMacId) {
                int val1 = Math.abs(Integer.parseInt(currentRSSI));
                int val2 = Math.abs(Integer.parseInt(scan.getRSSI()));
                int distance = Math.abs(val1 - val2);
                distances.put(distance, scan);
            }
            // Sorting distances array
            Map<Integer, Scan> sortedDistances = new TreeMap<>(distances);
            int nodesLimit = k;
            int nodes = 0;
            double zSum = 0;
            float xSum = 0;
            float ySum = 0;

            // For max nodesLimit get sum of z, x, y
            for(Map.Entry<Integer,Scan> entry : sortedDistances.entrySet()) {
                if(nodes >= nodesLimit) {
                    break;
                }
                FingerPrint fingerPrint = entry.getValue().getFingerPrint();
                zSum += fingerPrint.getZ();
                xSum += fingerPrint.getX();
                ySum += fingerPrint.getY();
                addToUsedUniqueFingerPrints(fingerPrint);
                nodes++;
            }
            if(nodes > 0) {
                // Calc average of fingerPrints with mac currentMac
                if(zSum == 0) {
                    zSum = 0.000000001;
                }
                float zAverage = (float)zSum/(float)nodes;
                float xAverage = Math.round(xSum / nodes);
                float yAverage = Math.round(ySum / nodes);
                zSumAverage += zAverage;
                xSumAverage += xAverage;
                ySumAverage += yAverage;
                averageSums++;
            }
        }
        if(averageSums > 0) {

            FingerPrint emptyFp = calcAverageOfUsedUniqueFingerPrints();
            ArrayList<FingerPrint> drawAsRedFps = new ArrayList<>();
            drawAsRedFps.add(emptyFp);
            lState.setDrawWithRed(drawAsRedFps);
            Log.d(TAG, "set draw with red " + lState.getDrawWithRed().size());
            lState.setLocateAlgorithmFps(this.usedUniqueFingerPrints);

            float z = ((float)zSumAverage/(float)averageSums);
            int x = Math.round(xSumAverage/averageSums);
            int y = Math.round(ySumAverage/averageSums);

            float zMultiplier = z/(float)emptyFp.getZ(); // TODO, perhaps
            float xMultiplier = (float)x/emptyFp.getX();
            float yMultiplier = (float)y/emptyFp.getY();

            View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
            TextView counterWeight = (TextView) rootView.findViewById(R.id.locationWeightCount);
            int currentWeight = Integer.parseInt("" + counterWeight.getText());

            float newX = 0;
            float newY = 0;
            if(x > emptyFp.getX()) {
                newX = x + xMultiplier*currentWeight;
            } else {
                newX = x - xMultiplier*currentWeight;
            }

            if(y > emptyFp.getY()) {
                newY = y + yMultiplier*currentWeight;
            } else {
                newY = y - yMultiplier*currentWeight;
            }
            FingerPrint oldFingerPrint = new FingerPrint(Math.round(z), x, y);
            ArrayList<FingerPrint> drawAsCyanFps = new ArrayList<>();
            drawAsCyanFps.add(oldFingerPrint);
            lState.setDrawWithCyan(drawAsCyanFps);

            //Log.d(TAG, "newX: " + newX + " newY " + newY);

            String[] results = {"" + z, "" + Math.round(newX), "" + Math.round(newY)};
            algorithmMain.handleResults(results);
        } else {
            String[] results = {"-1", "-1", "-1"};
            algorithmMain.handleResults(results);
        }
    }

    private void addToUsedUniqueFingerPrints(FingerPrint fpToAdd) {
        boolean exist = false;
        for(FingerPrint fp : this.usedUniqueFingerPrints) {
            if(fp.getX() == fpToAdd.getX() && fp.getY() == fpToAdd.getY() && fp.getZ() == fpToAdd.getZ()) {
                exist = true;
            }
        }
        if(!exist) {
            this.usedUniqueFingerPrints.add(fpToAdd);
        }
    }

    private FingerPrint calcAverageOfUsedUniqueFingerPrints() {

        float x= 0;
        float y = 0;
        int z = 0;
        int sums = 0;
        for(FingerPrint fp: this.usedUniqueFingerPrints) {
            x += fp.getX();
            y += fp.getY();
            z += fp.getZ();
            sums++;
        }
        if(sums > 0) {
            float avgX = x/(float)sums;
            float avgY = y/(float)sums;
            int avgZ = Math.round((float)z/(float)sums);
            //Log.d(TAG, "AVERAGE RESULT, x: " + avgX + " y: " + avgY + " z: " + avgZ);
            FingerPrint emptyFp = new FingerPrint(avgZ, avgX, avgY);
            return emptyFp;
        }
        return null;
    }

}

