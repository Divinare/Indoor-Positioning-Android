package com.joe.indoorlocalization.Algorithms;

import android.util.Log;

import com.joe.indoorlocalization.ApplicationState;
import com.joe.indoorlocalization.Models.FingerPrint;
import com.joe.indoorlocalization.Models.Scan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by joe on 10/01/16.
 */
public class Weighted_K_Nearest_FingerPrint {

    static String TAG = Weighted_K_Nearest_FingerPrint.class.getSimpleName();

    private AlgorithmMain algorithmMain;
    private ApplicationState state;

    int k = 5;

    public Weighted_K_Nearest_FingerPrint(AlgorithmMain algorithmMain, ApplicationState state) {
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
        int penaltyWhenMacNotFound = 0;
        for(FingerPrint fp : this.state.getFingerPrints()) {
            fp.resetDistance();
            fp.resetMacNotFoundCount();

            for(Scan scan : fp.getScans()) {

                boolean macFound = false;
                for (int i = 0; i < currentFPArray.length - 1; i = i + 2) {
                    String currentMac = currentFPArray[i];
                    String currentRSSI = currentFPArray[i + 1];
                    if(scan.getMac().equals(currentMac)) {
                        macFound = true;
                        int val1 = Math.abs(Integer.parseInt(currentRSSI));
                        int val2 = Math.abs(Integer.parseInt(scan.getRSSI()));
                        if(val1 > 85 && val2 > 85) {
                            fp.addToDistance(1);
                        } else if(val1 > 80 && val2 > 80) {
                            fp.addToDistance(2);
                        } else if(val1 > 75 && val2 > 75) {
                            fp.addToDistance(3);
                        } else if(val1 > 70 && val2 > 70) {
                            fp.addToDistance(4);
                        } else {
                            fp.addToDistance(5);
                        }
                        int distance = Math.abs(val1 - val2);
                        //Log.d(TAG, "Distance: " + distance);
                             /*
                           if (distance > penaltyWhenMacNotFound) {
                               penaltyWhenMacNotFound = distance;
                               Log.d(TAG, "Penalty now: " + penaltyWhenMacNotFound);
                           }
                           */
                        fp.addToDistance(distance);
                    }
                }
                if(!macFound) {
                    fp.increaseMacNotFoundCount();
                }
            }
        }

        Map<Integer, FingerPrint> distances = new HashMap<>();
        // Making distances array
        for (FingerPrint fp : this.state.getFingerPrints()) {
            //int distanceWithPenalty = fp.getDistance() + (fp.getMacNotFoundCount() * 10);
            //fp.setDistance(distanceWithPenalty);
            if(fp.getMacNotFoundCount() < 7) {
                distances.put(fp.getDistance(), fp);
            }
        }

        Map<Integer, FingerPrint> sortedDistances = new TreeMap<>(distances);
        // Put fps into TreeSet<Fp, distance>
        int nodesLimit = k;
        int iterations = 0;

        double zSum = 0;
        int xSum = 0;
        int ySum = 0;
        this.state.setNearestFps(new ArrayList<FingerPrint>());

        ArrayList<FingerPrint> weightedFps = new ArrayList<>();
        ArrayList<Integer> weights = new ArrayList<>();
        int sumOfDistances = 0;
        for(Map.Entry<Integer,FingerPrint> entry : sortedDistances.entrySet()) {
            if (iterations >= nodesLimit) {
                break;
            }
            FingerPrint fp = entry.getValue();
            weightedFps.add(fp);
            weights.add(entry.getKey());
            sumOfDistances += entry.getKey();
            iterations++;
        }

        Collections.reverse(weights);
        if (iterations > 0 && sumOfDistances > 0) {
            for (int ind = 0; ind < weightedFps.size(); ind++) {
                FingerPrint fp = weightedFps.get(ind);
                double weight = ((double) weights.get(ind) / (double) sumOfDistances);
                zSum += (fp.getZ()*weight);
                xSum += (fp.getX()*weight);
                ySum += (fp.getY()*weight);
                this.state.addToNearestFps(fp);
            }
        }

        if(iterations > 0) {
            double z = zSum;
            int x = xSum;
            int y = ySum;

            String[] results = {"" + z, "" + x, "" + y};
            algorithmMain.handleResults(results);
        } else {
            String[] results = {"-1", "-1", "-1"};
            algorithmMain.handleResults(results);
        }

    }

}
