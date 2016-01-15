package com.joe.indoorlocalization.Algorithms;

import android.util.Log;

import com.joe.indoorlocalization.State.ApplicationState;
import com.joe.indoorlocalization.Models.FingerPrint;
import com.joe.indoorlocalization.Models.Scan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by joe on 10/01/16.
 */
public class K_NearestSignal {
    static String TAG = K_NearestSignal.class.getSimpleName();
    private AlgorithmMain algorithmMain;
    private ApplicationState state;

    int k = 5;

    public K_NearestSignal(AlgorithmMain algorithmMain, ApplicationState state) {
        this.algorithmMain = algorithmMain;
        this.state = state;
    }

    public void calcLocation(StringBuilder currentFingerPrintData) {
        String currentFP = currentFingerPrintData.toString();

        // TEST fingerPrint, Expected: 2;1816.7079055808304;618 (Got z: 2.0 x: 1786.0 y: 567.0)
        //String currentFP = "48973141298628;-34;238257140157761;-78;92676807119905;-75;238257141023941;-69;238257140157765;-78;238257141023938;-69;238257141023936;-67;238257140692594;-69;238257140692593;-68;238257140692592;-69;238257139252917;-91;238257141258448;-75;238257141258449;-72;238257141258450;-72;238257139252912;-65;238257139252914;-64;238257139252913;-63;238257140692597;-67";

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
            String currentRSSI = currentFPArray[i+1];
            if(Integer.parseInt(currentRSSI) == 0) {
                Log.i(TAG, "Go to the next iteration in the currentFPArray loop because the RSSI value was 0");
                continue;
            }
            List<Scan> scansByMacId = new ArrayList<>();

            for(Scan scan : allScans) {
                if(scan.getMac().equals(currentMac)) {
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
                nodes++;
            }

            // Calc average of fingerPrints with mac currentMac
            if(nodes > 0) {
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
            float z = ((float)zSumAverage/(float)averageSums);
            int x = Math.round(xSumAverage/averageSums);
            int y = Math.round(ySumAverage/averageSums);
            String[] results = {"" + z, "" + x, "" + y};
            algorithmMain.handleResults(results);
        } else {
            String[] results = {"-1", "-1", "-1"};
            algorithmMain.handleResults(results);
        }
    }

}
