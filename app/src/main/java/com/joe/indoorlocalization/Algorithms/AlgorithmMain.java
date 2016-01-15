package com.joe.indoorlocalization.Algorithms;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.joe.indoorlocalization.State.ApplicationState;
import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.IndoorLocalization;
import com.joe.indoorlocalization.R;
import com.joe.indoorlocalization.State.LocateState;

/**
 * Created by joe on 09/01/16.
 */
public class AlgorithmMain {

    static String TAG = AlgorithmMain.class.getSimpleName();

    private Context context;
    private ApplicationState state;
    private LocateState lState;
    private CustomImageView customImageView;

    // ALGORITHMS
    private K_NearestSignal k_nearestSignal;
    private Weighted_K_NearestSignal weighted_k_nearestSignal;
    private K_Nearest_FingerPrint k_nearest_fingerPrint;
    private Weighted_K_Nearest_FingerPrint weighted_k_nearest_fingerPrint;
    private K_NearestSignal_Counterweight k_nearestSignal_counterweight;

    public AlgorithmMain(Context context) {

        this.context = context;
        this.state = ((IndoorLocalization)context.getApplicationContext()).getApplicationState();
        this.lState = state.locateState;
        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        this.customImageView = (CustomImageView)rootView.findViewById(R.id.customImageViewLocate);

        this.k_nearestSignal = new K_NearestSignal(this, this.state);
        this.weighted_k_nearestSignal = new Weighted_K_NearestSignal(this, this.state);
        this.k_nearest_fingerPrint = new K_Nearest_FingerPrint(this, this.state);
        this.weighted_k_nearest_fingerPrint = new Weighted_K_Nearest_FingerPrint(this, this.state);
        this.k_nearestSignal_counterweight = new K_NearestSignal_Counterweight(this, this.state, context);
    }

    public void calcLocation(StringBuilder currentFingerPrintData) {
        Log.d(TAG, "Data in DB: " + this.state.getFingerPrints().size());
        String algorithm = this.lState.getCurrentAlgorithm();
        if(algorithm.equals("K_NearestSignal")) {
            Log.d(TAG, "Using K_NearestSignal Algoritm");
            this.k_nearestSignal.calcLocation(currentFingerPrintData);
        } else if(algorithm.equals("K_NearestSignal_Counterweight")) {
            this.k_nearestSignal_counterweight.calcLocation(currentFingerPrintData);
        } else if(algorithm.equals("Weighted_K_NearestSignal")) {
            Log.d(TAG, "Using Weighted_K_NearestSignal Algoritm");
            this.weighted_k_nearestSignal.calcLocation(currentFingerPrintData);
        } else if(algorithm.equals("K_Nearest_FingerPrint")) {
            Log.d(TAG, "Using K_Nearest_FingerPrint Algoritm");
            this.k_nearest_fingerPrint.calcLocation(currentFingerPrintData);
        } else if(algorithm.equals("Weighted_K_Nearest_FingerPrint")) {
            Log.d(TAG, "Using Weighted_K_Nearest_FingerPrint Algoritm");
            this.weighted_k_nearest_fingerPrint.calcLocation(currentFingerPrintData);
        } else {
            Log.d(TAG, "Algorithm not found, for unknown reason. Is there a bug in the code?");
        }
    }

        // results[] should contain float z, int x, int y
    public void handleResults(String[] results) {
        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        TextView locationCoordinates = (TextView) rootView.findViewById(R.id.locationCoordinates);

        double zDouble = Double.parseDouble(results[0]);
        int z = (int) Math.round(zDouble);

        if(zDouble == -1) {
            locationCoordinates.setText("x: - y: - z: -");
            Log.d(TAG, "Error at location algorithm, was not able to find familiar mac addresses");
            Toast.makeText(context, "Location error: was not able to find familiar mac addresses in this area", Toast.LENGTH_LONG).show();
            return;
        }
        int x = Integer.parseInt(results[1]);
        int y = Integer.parseInt(results[2]);
        state.setX(x);
        state.setY(y);
        state.setZ(zDouble);

        Log.d(TAG, "The result is x: " + x + " y: " + y + " z: " + z);
        locationCoordinates.setText("x: " + x + " y: " + y + " z: " + results[0]);

        boolean shouldChangeFloor = (z != this.state.getCurrentFloor()) ? true : false;
        if(shouldChangeFloor && this.state.getAutomaticallyChangeFloor()) {
            state.floorChanger.changeFloor(context, z, "locate");
        }
        customImageView.invalidate();
    }
}
