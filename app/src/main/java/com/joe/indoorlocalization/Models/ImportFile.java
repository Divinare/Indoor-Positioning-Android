package com.joe.indoorlocalization.Models;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.joe.indoorlocalization.ApplicationState;
import com.joe.indoorlocalization.Calibration.CalibrateActivity;
import com.joe.indoorlocalization.IndoorLocalization;
import com.joe.indoorlocalization.Locate.LocateActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Imports a file to the ApplicationState as objects
 * Created by joe on 02/01/16.
 */
public class ImportFile {

    static String TAG = ImportFile.class.getSimpleName();

    private ApplicationState state;
    private Context context;
    private String previousClassName;

    public ImportFile(Context context, String previousClassName) {
        this.context = context;
        this.previousClassName = previousClassName;
        this.state = ((IndoorLocalization)context.getApplicationContext()).getApplicationState();

    }

    public void importFile(String path) {
        String fileName = getFileName(path);
        try {
            StringBuilder fileContent = getFileContent(path);
            emptyDatabase();
            importIntoDatabase(fileContent, path);
            Toast.makeText(context, "File " + fileName + " imported to database succesfully.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Reading file from path " + path + " failed!");
            Toast.makeText(context, "Reading file " + path + " failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(String path) {
        String[] temp = path.split("/");
        return temp[temp.length-1];
    }

    // Read a file from path and return its content
    private StringBuilder getFileContent(String path) throws IOException {
        File file = new File(path);
        Log.d(TAG, path);
        StringBuilder fileContent = new StringBuilder();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            if(!line.isEmpty()) {
                fileContent.append(line);
                fileContent.append('_');
            }
        }
        br.close();
        return fileContent;
    }

    // Empty objects in memory
    private void emptyDatabase() {
        state.emptyCurrentDatabase();
        Log.d(TAG, "Database in memory emptied");
    }

    // Save objects from file to memory
    private void importIntoDatabase(StringBuilder fileContent, String fileName) {
        Log.d(TAG, "Importing into database " + fileName);
        String[] rows = fileContent.toString().split("_");
        int index = 0;
        Log.d(TAG, "ROWS :LENGTH: " + rows.length);
        for(int i = 0; i < rows.length; i++) {
            Log.d(TAG, "index : " + index);
            index++;
            String row = rows[i];
            String[] array = row.split(";"); // Each row is now in a row array
            int z = Integer.parseInt(array[0]);
            float x = Float.parseFloat(array[1]);
            float y = Float.parseFloat(array[2]);

            FingerPrint fingerPrint = new FingerPrint(z, x, y);
            //scan.save();
            ArrayList<Scan> scans = new ArrayList<>();

            for(int j = 3; j < array.length-1; j=j+2) {
                String mac = array[j]; // list goes mac;rssi;mac;rssi...
                String RSSI = array[j+1];
                Scan scan = new Scan(fingerPrint, mac, RSSI);
                //fp.save();
                scans.add(scan);
            }
            fingerPrint.setScans(scans);
            this.state.addFingerPrint(fingerPrint);

        }
        if(previousClassName.equals("calibrate")) {
            final Intent intentCalibrate = new Intent(context, CalibrateActivity.class);
            context.startActivity(intentCalibrate);
        } else {
            final Intent intentLocate = new Intent(context, LocateActivity.class);
            context.startActivity(intentLocate);
        }
    }

}
