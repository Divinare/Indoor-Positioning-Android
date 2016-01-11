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
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by joe on 09/01/16.
 */
public class FloorChanger {

    private ApplicationState state;
    static String TAG = FloorChanger.class.getSimpleName();
    private ArrayList<Integer> floorNumbers = new ArrayList<>();

    public FloorChanger(ApplicationState state) {
        this.state = state;
    }

    public void init(Context context) {
        dynamicallyAddBlueprints(context);
    }

    private void dynamicallyAddBlueprints(Context context) {
        Log.d(TAG, "Dynamically adding blueprints");
        String appDir = context.getExternalFilesDir(null).getAbsolutePath();
        File blueprintsDir = new File(appDir + "/Documents/blueprints/");
        //File blueprintsDir = new File("/sdcard/Android/data/com.joe.indoorlocalization/files/Documents/blueprints/");
        if(!blueprintsDir.exists()) {
            blueprintsDir.mkdirs();
        }
        File[] blueprintFiles = blueprintsDir.listFiles();
        Log.d(TAG, "blueprintFilesLength: " + blueprintFiles.length);
        if(blueprintFiles.length == 0) {
            Toast.makeText(context, "Could not read blueprints files from appPath/files/Documents/blueprints folder. Are the blueprints too large? Maximum size for a blueprint is 4096 x 4096", Toast.LENGTH_LONG).show();
        }
        for(File blueprint : blueprintFiles) {
            Log.d(TAG, blueprint.getAbsolutePath());
            String path = blueprint.getAbsolutePath();
            int floorNumber = getFloorNumberFromFileName(blueprint.getName());
            this.floorNumbers.add(floorNumber);
            this.state.addToBlueprints(floorNumber, path);
        }
    }

    private int getFloorNumberFromFileName(String fileName) {
        String[] tokens  = fileName.split("\\.(?=[^\\.]+$)");
        char lastChar = tokens[0].charAt(tokens[0].length() - 1); // tokens[0] is the fileName without extension (like .png)
        return Integer.parseInt("" + lastChar);
    }

    public void changeToInitialFloor(Context context, String className) {
        Collections.sort(floorNumbers);
        if(this.floorNumbers.get(0) == null ) {
            Toast.makeText(context, "There were no floor blueprints (.png) at blueprints folder, put there some", Toast.LENGTH_LONG).show();
            return;
        }
        int initialFloorNumber = 0;
        // If there is basement and floor 1, then initial floor is 1
        if(this.floorNumbers.get(1) != null && this.floorNumbers.get(0) == 0 && this.floorNumbers.get(1) == 1) {
            initialFloorNumber = 1;
        }
        else {
            // The smallest initial floorNumber is at index 0
            initialFloorNumber = this.floorNumbers.get(0);
        }
        changeFloor(context, initialFloorNumber, className);
    }

    public void changeFloor(Context context, int floorNumber, String className) {
        if(!this.state.calibrationState.getAllowChangeFloor()) {
            Toast.makeText(context, "Floor change is not allowed at this moment", Toast.LENGTH_LONG).show();
            return;
        }
        boolean floorFound = false;
        for(int num : this.floorNumbers) {
            if(num == floorNumber) {
                floorFound = true;
            }
        }
        if(!floorFound) {
            Toast.makeText(context, "Tried to change to floor " + floorNumber + " that doesn't exist! Are you using wrong location data?", Toast.LENGTH_LONG).show();
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
