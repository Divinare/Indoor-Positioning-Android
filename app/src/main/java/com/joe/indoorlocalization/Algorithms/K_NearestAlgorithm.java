package com.joe.indoorlocalization.Algorithms;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.joe.indoorlocalization.ApplicationState;
import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.IndoorLocalization;
import com.joe.indoorlocalization.Models.FingerPrint;
import com.joe.indoorlocalization.Models.Scan;
import com.joe.indoorlocalization.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by joe on 31/12/15.
 * This algorithm calculates k nearest signals and calculates their average z, x and y
 */
public class K_NearestAlgorithm {

    static String TAG = K_NearestAlgorithm.class.getSimpleName();
    private ApplicationState state;
    private CustomImageView customImageView;

    public K_NearestAlgorithm(Context context) {
        this.state = ((IndoorLocalization)context.getApplicationContext()).getApplicationState();
        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        this.customImageView = (CustomImageView)rootView.findViewById(R.id.customImageViewLocate);
    }

    public void calcLocation(StringBuilder currentFingerPrintData) {
        Log.d(TAG, "at calcLocation");
        String currentFP = currentFingerPrintData.toString();

        // Expected: 2;1773.0;1776.0
        //String currentFP = "d8:b1:90:41:8a:3c;-76;00:3a:98:b9:9c:cb;-69;d8:b1:90:41:8a:3e;-73;84:b8:02:e2:a1:4c;-75;00:1d:e6:27:14:c1;-75;d8:b1:90:41:8a:3f;-74;00:3a:98:b9:9c:cd;-69;d8:b1:90:41:8a:32;-66;d8:b1:90:41:8a:30;-66;d8:b1:90:41:8a:33;-75;00:3a:98:b9:9c:ce;-69;00:3a:98:b9:9c:cc;-69;d8:b1:90:41:8a:3d;-76;d8:b1:90:41:8a:3b;-76;b0:aa:77:9f:14:43;-74;00:1d:e6:27:14:c2;-72;b0:aa:77:9f:14:40;-74;d8:b1:90:3c:83:4f;-75;d8:b1:90:3c:83:4e;-75;00:1d:e6:27:14:cd;-83;00:1d:e6:27:14:c3;-72;56:25:8d:aa:da:a8;-76;d8:b1:90:3c:83:4d;-75;d8:b1:90:3c:83:4b;-74;d8:b1:90:3c:83:4c;-74;b0:aa:77:9f:14:42;-74;b0:aa:77:9f:14:4d;-74;02:1a:11:f6:80:c2;-70;34:21:09:14:de:60;-67;00:3a:98:b9:9c:cf;-69;84:b8:02:e2:a1:4e;-79;84:b8:02:e2:a1:4d;-76;d8:b1:90:41:8d:22;-75;d8:b1:90:41:8d:23;-76;d8:b1:90:41:8d:21;-76;84:b8:02:e2:a5:9c;-77;84:b8:02:e2:a5:9b;-75;d8:b1:90:41:8d:20;-75;84:b8:02:e2:a5:9d;-77;d8:b1:90:41:8d:2d;-78;d8:b1:90:45:18:de;-90;b0:aa:77:9f:14:4b;-76;84:b8:02:e2:a5:9f;-75;84:b8:02:e2:a1:4f;-77;d8:b1:90:41:8d:2b;-78;00:1d:e6:27:14:ce;-83;5c:f8:a1:9e:dc:3e;-68;84:b8:02:e2:a5:90;-78;00:1d:e6:27:14:cc;-83;00:1d:e6:27:14:cf;-77;b0:aa:77:9f:14:4e;-74;00:1d:e6:27:14:cb;-83;b0:aa:77:a8:cf:ec;-84;58:97:bd:62:03:93;-84;84:b8:02:e2:a5:9e;-76;d8:b1:90:41:8a:31;-76;84:b8:02:e2:a5:92;-80;b0:aa:77:a8:cf:ed;-82;34:62:88:ea:0b:2d;-88;b0:aa:77:a8:cf:ee;-82;58:97:bd:62:03:9d;-85;b0:aa:77:a8:b5:82;-90;b0:aa:77:a8:cf:eb;-81;b0:aa:77:a8:cf:ef;-84;34:62:88:ea:0b:2f;-89;34:62:88:ea:0b:2b;-87;34:62:88:ea:0b:2c;-88;58:97:bd:62:03:9c;-85;b0:aa:77:a8:b5:80;-90;b0:aa:77:a8:b5:83;-90;d8:b1:90:41:8d:2e;-81;84:b8:02:e2:a5:91;-77;b0:aa:77:9f:12:cd;-89;58:97:bd:62:03:91;-89;d8:b1:90:41:8d:2c;-79;58:97:bd:6e:b0:bc;-89;84:b8:02:e2:a5:93;-78;00:1d:e6:27:14:c0;-70;b0:aa:77:9f:14:4c;-74;58:97:bd:62:03:90;-83;b0:aa:77:9f:12:cb;-89;34:62:88:ea:0b:22;-84;58:97:bd:6e:b0:bd;-90;b0:aa:77:cc:d3:e2;-88;b0:aa:77:9f:14:4f;-74;d8:b1:90:41:8d:2f;-76;34:62:88:ea:0b:23;-84;b0:aa:77:cc:d3:ec;-85;b0:aa:77:9f:12:cc;-91;58:97:bd:6e:b0:b3;-86;d8:b1:90:3c:76:03;-88;58:97:bd:6e:b0:bb;-92;58:97:bd:62:03:92;-84;d8:b1:90:3c:76:0e;-91;b0:aa:77:9f:12:cf;-87;58:97:bd:6e:b0:bf;-90;b0:aa:77:cc:d3:e0;-89";

        // Expected: 2;1404.0;1770.0
        // String currentFP = "d8:b1:90:41:8a:3c;-68;00:3a:98:b9:9c:cb;-61;d8:b1:90:41:8a:3e;-68;84:b8:02:e2:a1:4c;-72;00:1d:e6:27:14:c1;-85;d8:b1:90:41:8a:3f;-67;00:3a:98:b9:9c:cd;-61;d8:b1:90:41:8a:32;-66;d8:b1:90:41:8a:30;-65;d8:b1:90:41:8a:33;-66;00:3a:98:b9:9c:ce;-62;00:3a:98:b9:9c:cc;-61;d8:b1:90:41:8a:3d;-68;d8:b1:90:41:8a:3b;-67;b0:aa:77:9f:14:43;-73;00:1d:e6:27:14:c2;-73;b0:aa:77:9f:14:40;-74;d8:b1:90:3c:83:4f;-74;d8:b1:90:3c:83:4e;-75;00:1d:e6:27:14:c3;-71;56:25:8d:aa:da:a8;-78;d8:b1:90:3c:83:4d;-75;d8:b1:90:3c:83:4b;-74;d8:b1:90:3c:83:4c;-74;b0:aa:77:9f:14:42;-74;02:1a:11:f6:80:c2;-68;34:21:09:14:de:60;-67;00:3a:98:b9:9c:cf;-63;d8:b1:90:41:8d:22;-77;d8:b1:90:41:8d:23;-76;84:b8:02:e2:a5:9c;-72;d8:b1:90:41:8d:20;-77;84:b8:02:e2:a5:90;-72;00:1d:e6:27:14:cc;-77;00:1d:e6:27:14:cf;-77;00:1d:e6:27:14:cb;-77;b0:aa:77:a8:cf:ec;-83;84:b8:02:e2:a5:9e;-73;84:b8:02:e2:a5:92;-73;b0:aa:77:a8:cf:ef;-84;d8:b1:90:41:8d:2c;-76;84:b8:02:e2:a5:93;-71;b0:aa:77:9f:14:4c;-71;58:97:bd:62:03:90;-84;b0:aa:77:cc:d3:e0;-82;34:62:88:ea:0b:20;-84;b0:aa:77:a8:b5:82;-88;84:b8:02:e2:a5:9b;-76;00:1d:e6:27:14:cd;-77;00:1d:e6:27:14:ce;-77;b0:aa:77:a8:a0:32;-87;b0:aa:77:9f:12:cf;-90;d8:b1:90:41:8d:2d;-72;b0:aa:77:a8:cf:ee;-84;b0:aa:77:a8:cf:ed;-85;d8:b1:90:41:8d:2f;-76;b0:aa:77:a8:cf:eb;-84;b0:aa:77:a8:a0:33;-88;b0:aa:77:cc:d3:ee;-88;d8:b1:90:45:18:dd;-88;b0:aa:77:9f:12:ce;-91;58:97:bd:62:03:9d;-88;88:32:9b:a1:96:9c;-91;b0:aa:77:9f:14:4b;-71;84:b8:02:e2:a1:4b;-81;84:b8:02:e2:a1:4f;-80;b0:aa:77:9f:14:4e;-72;b0:aa:77:9f:14:4d;-73;d8:b1:90:41:8a:31;-69;84:b8:02:e2:a1:4d;-83;b0:aa:77:9f:12:cd;-90;d8:b1:90:3c:7e:8d;-92;58:97:bd:62:03:92;-84;b0:aa:77:9f:14:4f;-71;d8:b1:90:41:8d:2b;-72;84:b8:02:e2:a5:9f;-76;58:97:bd:62:03:9c;-89;b0:aa:77:9f:12:cc;-90;d8:b1:90:3c:7e:8c;-92;b0:aa:77:9f:12:cb;-91;d8:b1:90:3c:7e:8b;-91;b0:aa:77:a8:b5:83;-86;58:97:bd:62:03:93;-87;d8:b1:90:3c:7e:8f;-90;34:62:88:ea:0b:2d;-87;b0:aa:77:a8:bb:1e;-90;f4:cf:e2:62:da:1e;-91;d8:b1:90:3c:76:92;-89;34:62:88:ea:0b:2f;-88;34:62:88:ea:0b:2c;-88;34:62:88:ea:0b:2b;-89;f4:cf:e2:66:a8:cc;-91;d8:b1:90:3c:76:93;-90";

        // Expected: 1;1466.0;1758.0
        //String currentFP = "d8:b1:90:3c:83:4d;-71;d8:b1:90:3c:83:4e;-71;58:97:bd:62:03:92;-79;d8:b1:90:3c:83:4c;-71;d8:b1:90:3c:83:4b;-71;58:97:bd:62:03:90;-80;58:97:bd:62:03:9c;-85;02:1a:11:f6:80:c2;-73;34:21:09:14:de:60;-65;00:3a:98:b9:9c:ce;-68;00:3a:98:b9:9c:cc;-68;d8:b1:90:41:8a:3c;-67;00:3a:98:b9:9c:cb;-68;00:3a:98:b9:9c:cd;-67;84:b8:02:e2:a1:4d;-66;d8:b1:90:41:8a:3e;-67;00:3a:98:b9:9c:cf;-68;84:b8:02:e2:a1:4b;-67;84:b8:02:e2:a1:4c;-67;b0:aa:77:a8:cf:ec;-82;b0:aa:77:9f:14:4c;-76;d8:b1:90:41:8d:2d;-85;5c:f8:a1:9e:dc:3e;-84;84:b8:02:e2:a5:9c;-77;d8:b1:90:41:8a:30;-71;d8:b1:90:41:8d:2c;-80;00:1d:e6:27:14:cb;-79;d8:b1:90:41:8a:32;-69;b0:aa:77:9f:14:4e;-78;84:b8:02:e2:a5:9e;-79;b0:aa:77:9f:14:42;-75;d8:b1:90:41:8a:33;-69;d8:b1:90:41:8d:2b;-81;00:1d:e6:27:14:c3;-72;b0:aa:77:9f:14:40;-75;84:b8:02:e2:a5:9d;-80;56:25:8d:aa:da:a8;-74;00:1d:e6:27:14:c2;-78;00:1d:e6:27:14:c1;-76;b0:aa:77:a8:cf:ee;-84;d8:b1:90:41:8d:2e;-81;58:97:bd:6e:b0:b0;-86;b0:aa:77:9f:14:4d;-77;84:b8:02:e2:a1:4f;-69;58:97:bd:6e:b0:b3;-84;d8:b1:90:3c:76:00;-82;d8:b1:90:3c:76:03;-87;d8:b1:90:3c:76:02;-85;88:32:9b:a1:96:9c;-86;b0:aa:77:9f:12:cc;-84;84:b8:02:e2:a1:4e;-67;d8:b1:90:3c:83:4f;-71;00:1d:e6:27:14:cd;-79;b0:aa:77:a8:b5:82;-76;d8:b1:90:41:8a:3f;-67;84:b8:02:e2:a5:9f;-78;b0:aa:77:a8:b5:83;-77;d8:b1:90:41:8a:3d;-67;00:1d:e6:27:14:ce;-81;58:97:bd:6e:b0:bd;-86;d8:b1:90:41:8a:3b;-67;00:1d:e6:27:14:cc;-80;84:b8:02:e2:a5:93;-78;b0:aa:77:9f:14:43;-74;d8:b1:90:41:8d:22;-80;58:97:bd:6e:b0:bc;-87;84:b8:02:e2:a5:92;-78;58:97:bd:62:03:9d;-82;58:97:bd:62:03:93;-76;d8:b1:90:41:8d:20;-79;d8:b1:90:41:8d:23;-77;00:1d:e6:27:14:cf;-78;b0:aa:77:a8:cf:ed;-82;b0:aa:77:9f:14:4f;-77;58:97:bd:62:03:91;-77;58:97:bd:6e:b0:b1;-83;58:97:bd:6e:b0:be;-87;84:b8:02:e2:a5:91;-91;84:b8:02:e2:a5:9b;-81;b0:aa:77:a8:b5:80;-75;58:97:bd:62:03:9f;-85;58:97:bd:62:03:9e;-83;b0:aa:77:9f:12:cd;-84;b0:aa:77:9f:12:ce;-84;d8:b1:90:3c:76:0e;-88;b0:aa:77:9f:14:4b;-76;b0:aa:77:a8:cf:eb;-83;b0:aa:77:9f:12:cb;-88;58:97:bd:6e:b0:bf;-89;54:4a:00:2a:d4:2c;-88;b0:aa:77:9f:14:41;-74;00:1d:e6:27:14:c0;-74;b0:aa:77:9f:12:cf;-85;b0:aa:77:a8:b5:8f;-91";

        // Expected: 3;1818.0;1194.0
        //String currentFP = "d8:b1:90:45:18:df;-84;d8:b1:90:45:18:dc;-86;d8:b1:90:45:18:db;-87;54:4a:00:2a:d4:2d;-87;54:4a:00:2a:d4:22;-86;54:4a:00:2a:d4:2c;-87;54:4a:00:2a:d4:23;-88;54:4a:00:2a:d4:20;-89;d8:b1:90:34:4d:43;-91;00:1d:e6:27:14:c3;-67;b0:aa:77:9f:14:4c;-67;b0:aa:77:9f:14:43;-62;b0:aa:77:9f:14:42;-62;00:1d:e6:27:14:c2;-70;00:1d:e6:27:14:c1;-68;00:1d:e6:27:14:c0;-68;54:4a:00:2a:d4:2b;-81;b0:aa:77:9f:14:40;-62;b0:aa:77:9f:14:4e;-67;84:b8:02:e2:a1:4c;-86;00:1d:e6:27:14:ce;-81;00:1d:e6:27:14:cc;-85;00:1d:e6:27:14:cb;-81;d8:b1:90:34:4d:42;-85;54:4a:00:2a:d4:2e;-81;d8:b1:90:45:18:dd;-86;d8:b1:90:26:7e:b2;-89;00:1d:e6:27:14:cf;-82;d8:b1:90:26:7e:b3;-92;d8:b1:90:45:18:de;-86;d8:b1:90:45:18:d2;-84;d8:b1:90:45:18:d3;-87;00:1d:e6:27:14:cd;-79;84:db:2f:1a:7d:1d;-86;b0:aa:77:9f:14:4f;-75;54:4a:00:2a:d4:2f;-88;d8:b1:90:41:8a:3c;-83;d8:b1:90:34:4d:41;-89;d8:b1:90:23:25:e2;-72;84:b8:02:e2:a5:92;-76;84:b8:02:e2:a1:4b;-89;84:b8:02:e2:a1:4f;-86;84:b8:02:e2:a5:9f;-78;84:b8:02:e2:a5:90;-77;00:3a:98:b9:9c:cb;-91;84:b8:02:e2:a5:9e;-77;84:b8:02:e2:a1:4d;-90;d8:b1:90:41:8a:3d;-82;d8:b1:90:3c:83:4d;-89;d8:b1:90:41:8a:3b;-83;84:b8:02:e2:a5:93;-76;d8:b1:90:3c:83:4b;-88;84:b8:02:e2:a1:4e;-87;d8:b1:90:3c:83:4e;-89;84:b8:02:e2:a5:9b;-76;d8:b1:90:23:25:ef;-81;d8:b1:90:3c:83:4c;-89;d8:b1:90:3c:83:4f;-89;b0:aa:77:9f:14:4d;-76;d8:b1:90:23:25:ee;-79;d8:b1:90:23:25:ed;-79;00:3a:98:b9:9c:cd;-88;d8:b1:90:23:25:ec;-79;d8:b1:90:23:25:e0;-82;d8:b1:90:26:7e:bc;-86;d8:b1:90:26:7e:be;-90;d8:b1:90:26:7e:bd;-86;02:1a:11:f6:80:c2;-88;b0:aa:77:9f:14:4b;-66;d8:b1:90:41:8a:3f;-82;d8:b1:90:41:8a:30;-76;d8:b1:90:23:25:e3;-78;d8:b1:90:41:8d:2f;-91;d8:b1:90:41:8a:32;-77;d8:b1:90:41:8a:31;-76;f4:cf:e2:66:a8:ce;-91;d8:b1:90:41:8a:33;-76;84:b8:02:e2:a5:9d;-78;d8:b1:90:41:8a:3e;-82;34:21:09:14:de:60;-81;00:3a:98:b9:9c:cf;-90";

        // TEST fingerPrint, Expected: 2;1816.7079055808304;618 (Got z: 2.0 x: 1786.0 y: 567.0)
        //String currentFP = "48973141298628;-34;238257140157761;-78;92676807119905;-75;238257141023941;-69;238257140157765;-78;238257141023938;-69;238257141023936;-67;238257140692594;-69;238257140692593;-68;238257140692592;-69;238257139252917;-91;238257141258448;-75;238257141258449;-72;238257141258450;-72;238257139252912;-65;238257139252914;-64;238257139252913;-63;238257140692597;-67";

        String[] currentFPArray = currentFP.split(";");

        ArrayList<FingerPrint> fps = state.getFingerPrints();
        Log.d(TAG, "NOW IN DATABASE FPS LENGTH: " + fps.size());

        for(int x = 0; x < fps.size(); x++) {
            FingerPrint f = fps.get(x);
            Log.d(TAG, "z: " + f.getZ() + " x: " + f.getX() + " y: " + f.getY());
            Log.d(TAG, "scans size: " + f.getScans().size());
            for(Scan scan : f.getScans()) {
                //Log.d(TAG, "Mac: " + scan.getMac() + " RSSI: " + scan.getRSSI() + " network: " + scan.getNetwork());
            }

        }



        ArrayList<String> macList = new ArrayList<>();
        ArrayList<Scan> allScans = new ArrayList<>();
        for(FingerPrint fp : fps) {
            ArrayList<Scan> scans = fp.getScans();
            for(Scan scan : scans) {
                macList.add(scan.getMac());
                allScans.add(scan);
            }
        }

        Set<String> uniqueMacs = new HashSet<>(macList);

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
            for(Scan fp: scansByMacId) {
                int val1 = Math.abs(Integer.parseInt(currentRSSI));
                int val2 = Math.abs(Integer.parseInt(fp.getRSSI()));
                int distance = Math.abs(val1 - val2);
                distances.put(distance, fp);
            }
            // Sorting distances array
            Map<Integer, Scan> sortedDistances = new TreeMap<>(distances);
            int nodesLimit = 10;
            int nodes = 0;
            double zSum = 0;
            float xSum = 0;
            float ySum = 0;
            Log.d(TAG, "ASDDD: " );
            // For max nodesLimit get sum of z, x, y
            for(Map.Entry<Integer,Scan> entry : sortedDistances.entrySet()) {
                if(nodes >= nodesLimit) {
                    break;
                }
                FingerPrint fingerPrint = entry.getValue().getFingerPrint();
                Log.d(TAG, "Nearest Z: " + fingerPrint.getZ());
                zSum += fingerPrint.getZ();
                xSum += fingerPrint.getX();
                ySum += fingerPrint.getY();
                nodes++;
            }
            if(nodes > 0) {
                // Calc average of fingerPrints with mac currentMac
                //Log.d(TAG, "zSum: " + zSum + " nodes " + nodes);
                if(zSum == 0) {
                    zSum = 0.000000001;
                }
                float zAverage = (float)zSum/(float)nodes;
                //Log.d(TAG, "Resss: " + zAverage);
                float xAverage = Math.round(xSum / nodes);
                float yAverage = Math.round(ySum / nodes);
                //Log.d(TAG, "zAverage: " + zAverage);
                zSumAverage += zAverage;
                xSumAverage += xAverage;
                ySumAverage += yAverage;
                averageSums++;
            }
        }
        if(averageSums > 0) {
            //Log.d(TAG, "averageSums: " + averageSums + " zSumAverage: " + zSumAverage);
            float z = ((float)zSumAverage/(float)averageSums);
            int x = Math.round(xSumAverage/averageSums);
            int y = Math.round(ySumAverage/averageSums);
            handleResults(z, x, y);
            Log.i(TAG, "RESULT: z: " + z + " x: " + x + " y: " + y);
        } else {
            Log.d(TAG, "Error at determistic algorithm, was not able to find familiar mac addresses");
        }
    }

    public void handleResults(float z, int x, int y) {
        state.setX(x);
        state.setY(y);
        state.setZ(z);
        customImageView.invalidate();


    }


}
