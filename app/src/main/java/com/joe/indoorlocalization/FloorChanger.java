package com.joe.indoorlocalization;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.joe.indoorlocalization.Calibration.CustomImageViewCalibrate;
import com.joe.indoorlocalization.Locate.CustomImageViewLocate;

import java.io.File;

/**
 * Created by joe on 09/01/16.
 */
public class FloorChanger {

    private ApplicationState state;
    static String TAG = FloorChanger.class.getSimpleName();


    public FloorChanger(ApplicationState state) {
        this.state = state;
        init();
    }

    private void init() {
        dynamicallyAddBlueprints();
    }

    private void dynamicallyAddBlueprints() {
        File blueprintsDir = new File("/sdcard/Android/data/com.joe.indoorlocalization/files/Documents/blueprints/");
        if(!blueprintsDir.exists()) {
            blueprintsDir.mkdirs();
        }
        File[] blueprintFiles = blueprintsDir.listFiles();
        for(File blueprint : blueprintFiles) {
            Log.d(TAG, blueprint.getAbsolutePath());
            String path = blueprint.getAbsolutePath();
            int floorNumber = getFloorNumberFromFileName(blueprint.getName());
            this.state.addToBlueprints(floorNumber, path);
        }
    }

    private int getFloorNumberFromFileName(String fileName) {
        String[] tokens  = fileName.split("\\.(?=[^\\.]+$)");
        char lastChar = tokens[0].charAt(tokens[0].length() - 1); // tokens[0] is the fileName without extension (like .png)
        return Integer.parseInt("" + lastChar);
    }

    public void changeFloor(Context context, int floorNumber, String className) {
        if(!this.state.calibrationState.getAllowChangeFloor()) {
            Toast.makeText(context, "Floor change is not allowed at this moment", Toast.LENGTH_LONG).show();
            return;
        }

        setFloorText(context, floorNumber, className);
        this.state.setCurrentFloor(floorNumber);

        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        if(className.equals("locate")) {
            CustomImageViewLocate imageView = (CustomImageViewLocate) rootView.findViewById(R.id.customImageViewLocate);
            imageView.setImageBitmap(getBitmap(floorNumber));
            imageView.invalidate();
        } else {
            CustomImageViewCalibrate imageView = (CustomImageViewCalibrate) rootView.findViewById(R.id.customImageViewCalibrate);
            imageView.setImageBitmap(getBitmap(floorNumber));
            imageView.invalidate();
        }
    }

    private Bitmap getBitmap(int floor) {
        String path = state.getBlueprints().get(floor);
        return BitmapFactory.decodeFile(path);
    }

    private void setFloorText(Context context, int floorNumber, String className) {
        View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        TextView floorTextView;
        if(className.equals("locate")) {
            floorTextView = (TextView)rootView.findViewById(R.id.floorLocate);
        } else {
            floorTextView = (TextView)rootView.findViewById(R.id.floorCalibration);
        }
        if(floorNumber == 0) {
            floorTextView.setText("Floor: basement");
        } else {
            floorTextView.setText("Floor " + floorNumber);
        }
    }

}
