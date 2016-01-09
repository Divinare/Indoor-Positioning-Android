package com.joe.indoorlocalization.Algorithms;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.joe.indoorlocalization.ApplicationState;
import com.joe.indoorlocalization.Calibration.CalibrateActivity;
import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.IndoorLocalization;
import com.joe.indoorlocalization.R;

/**
 * Created by joe on 09/01/16.
 */
public class AlgorithmMain {

    static String TAG = AlgorithmMain.class.getSimpleName();

    private Context context;
    private ApplicationState state;
    private CustomImageView customImageView;

    // ALGORITHMS
    private K_NearestAlgorithm k_nearestAlgorithm;

    public AlgorithmMain(Context context) {
        this.context = context;
        this.state = ((IndoorLocalization)context.getApplicationContext()).getApplicationState();
        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        this.customImageView = (CustomImageView)rootView.findViewById(R.id.customImageViewLocate);

        this.k_nearestAlgorithm = new K_NearestAlgorithm(this, this.state);
    }

    public void calcLocation(StringBuilder currentFingerPrintData) {
        k_nearestAlgorithm.calcLocation(currentFingerPrintData);
    }

        // results[] should contain float z, int x, int y
    public void handleResults(String[] results) {
        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        TextView locationCoordinates = (TextView) rootView.findViewById(R.id.locationCoordinates);

        float z = Float.parseFloat(results[0]);
        if(z == -1) {
            locationCoordinates.setText("x: - y: - z: -");
            Log.d(TAG, "Error at location algorithm, was not able to find familiar mac addresses");
            Toast.makeText(context, "Location error: was not able to find familiar mac addresses in this area", Toast.LENGTH_LONG).show();
            return;
        }
        int x = Integer.parseInt(results[1]);
        int y = Integer.parseInt(results[2]);
        state.setX(x);
        state.setY(y);
        state.setZ(z);
        customImageView.invalidate();
        Log.d(TAG, "Z ON NYT " + z);
        Log.d(TAG, "PYÖRISTETTY Z: " + ((int) z));

        Log.i(TAG, "RESULT: z: " + z + " x: " + x + " y: " + y);
        locationCoordinates.setText("x: " + x + " y: " + y + " z: " + z);

        boolean shouldChangeFloor = ((int)z != this.state.getCurrentFloor()) ? true : false;
        if(shouldChangeFloor && this.state.getAutomaticallyChangeFloor()) {
            state.floorChanger.changeFloor(context, (int) z, "locate");
        } else {
            Log.d(TAG, "Not allowed to change floor. Should: " + shouldChangeFloor + " auto: " + state.getAutomaticallyChangeFloor());
        }
        Log.d(TAG, "Current floor is: " + this.state.getCurrentFloor());

    }


}
