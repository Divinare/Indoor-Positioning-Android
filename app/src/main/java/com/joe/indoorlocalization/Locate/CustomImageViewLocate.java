package com.joe.indoorlocalization.Locate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.R;


/**
 * Created by joe on 22/11/15.
 */
public class CustomImageViewLocate extends CustomImageView {

    static String TAG = "CustomImageViewLocate";


    public CustomImageViewLocate(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText() {
        TextView test = (TextView) ((Activity) getContext()).findViewById(R.id.locationCoordinates);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "do nothing...");
    }

}
