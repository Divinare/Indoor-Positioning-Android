package com.joe.indoorlocalization.Algorithms;

import android.util.Log;

import com.joe.indoorlocalization.State.ApplicationState;
import com.joe.indoorlocalization.Models.FingerPrint;
import com.joe.indoorlocalization.Models.Scan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by joe on 10/01/16.
 */
public class Weighted_K_NearestSignal {

    static String TAG = Weighted_K_NearestSignal.class.getSimpleName();

    private AlgorithmMain algorithmMain;
    private ApplicationState state;
    int k = 5;

    public Weighted_K_NearestSignal(AlgorithmMain algorithmMain, ApplicationState state) {
        this.algorithmMain = algorithmMain;
        this.state = state;
    }

    public void calcLocation(StringBuilder currentFingerPrintData) {
        String currentFP = currentFingerPrintData.toString();

        String[] currentFPArray = currentFP.split(";");

        ArrayList<FingerPrint> fps = this.state.getFingerPrints();

        ArrayList<Scan> allScans = new ArrayList<>();
        for (FingerPrint fp : fps) {
            ArrayList<Scan> scans = fp.getScans();
            for (Scan scan : scans) {
                allScans.add(scan);
            }
        }

        double zSumAverage = 0;
        float xSumAverage = 0;
        float ySumAverage = 0;
        int iterations = 0;
        for (int i = 0; i < currentFPArray.length - 1; i = i + 2) {
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
            for (Scan scan : scansByMacId) {
                int val1 = Math.abs(Integer.parseInt(currentRSSI));
                int val2 = Math.abs(Integer.parseInt(scan.getRSSI()));
                int distance = Math.abs(val1 - val2);
                distance++; // So that there would be no dividing with zero
                distances.put(distance, scan);
            }
            // Sorting distances array
            Map<Integer, Scan> sortedDistances = new TreeMap<>(distances);
            int nodesLimit = k;
            double zSum = 0;
            float xSum = 0;
            float ySum = 0;

            ArrayList<FingerPrint> weightedFps = new ArrayList<>();
            ArrayList<Integer> weights = new ArrayList<>();
            int sumOfDistances = 0;
            int nodes = 0;
            for (Map.Entry<Integer, Scan> entry : sortedDistances.entrySet()) {
                if (nodes >= nodesLimit) {
                    break;
                }
                FingerPrint fingerPrint = entry.getValue().getFingerPrint();
                weightedFps.add(fingerPrint);
                weights.add(entry.getKey());
                sumOfDistances += entry.getKey();
                nodes++;
            }
            Collections.reverse(weights);
            if (nodes > 0 && sumOfDistances > 0) {
                for(int ind = 0; ind < weightedFps.size(); ind++) {
                    FingerPrint fp = weightedFps.get(ind);
                    double weight = ((double)weights.get(ind)/(double)sumOfDistances);
                    zSum += (fp.getZ()*weight);
                    xSum += (fp.getX()*weight);
                    ySum += (fp.getY()*weight);
                }
                // Calc average of fingerPrints with mac currentMac
                if (zSum == 0) {
                    zSum = 0.000000001;
                }
                zSumAverage = zSum;
                xSumAverage = xSum;
                ySumAverage = ySum;
                iterations++;
            }
        }
        if (iterations > 0) {
            double z = zSumAverage;
            int x = (int)xSumAverage;
            int y = (int)ySumAverage;

            String[] results = {"" + z, "" + x, "" + y};
            algorithmMain.handleResults(results);
        } else {
            String[] results = {"-1", "-1", "-1"};
            algorithmMain.handleResults(results);
        }
    }
}
