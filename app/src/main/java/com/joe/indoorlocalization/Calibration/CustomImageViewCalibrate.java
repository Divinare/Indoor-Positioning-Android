package com.joe.indoorlocalization.Calibration;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.R;

/**
 * Created by joe on 22/11/15.
 */
public class CustomImageViewCalibrate extends CustomImageView {

    static String TAG = CustomImageViewCalibrate.class.getSimpleName();

    public CustomImageViewCalibrate(Context context, AttributeSet attr) {
        super(context, attr, new DrawerCalibrate(context));
    }

    @Override
    public void setX(float x) {
        Log.d(TAG, "x:   " + x);
        TextView xTextView = (TextView) ((Activity) getContext()).findViewById(R.id.positionX);
        xTextView.setText("x: " + x);
    }

    @Override
    public void setY(float y) {
        TextView yTextView = (TextView) ((Activity) getContext()).findViewById(R.id.positionY);
        yTextView.setText("y: " + y);
    }

}