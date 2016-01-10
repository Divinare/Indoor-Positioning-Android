package com.joe.indoorlocalization.Calibration;

import android.content.Context;
import android.util.AttributeSet;

import com.joe.indoorlocalization.CustomImageView;

/**
 * Created by joe on 22/11/15.
 */
public class CustomImageViewCalibrate extends CustomImageView {

    public CustomImageViewCalibrate(Context context, AttributeSet attr) {
        super(context, attr, new DrawerCalibrate(context));
    }
}