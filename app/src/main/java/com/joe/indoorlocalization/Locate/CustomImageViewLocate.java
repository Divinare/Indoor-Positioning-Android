package com.joe.indoorlocalization.Locate;

import android.content.Context;
import android.util.AttributeSet;

import com.joe.indoorlocalization.CustomImageView;


/**
 * Created by joe on 22/11/15.
 */
public class CustomImageViewLocate extends CustomImageView {

    public CustomImageViewLocate(Context context, AttributeSet attr) {
        super(context, attr, new DrawerLocate());
    }

}
