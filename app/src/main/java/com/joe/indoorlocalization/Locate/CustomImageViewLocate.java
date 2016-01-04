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
import com.joe.indoorlocalization.Drawer;
import com.joe.indoorlocalization.R;


/**
 * Created by joe on 22/11/15.
 */
public class CustomImageViewLocate extends CustomImageView {


   // public CustomImageViewLocate(Context context, AttributeSet attr) {
   //     super(context, attr);
   // }

    public CustomImageViewLocate(Context context, AttributeSet attr) {
        super(context, attr, new DrawerLocate());
    }

 //   @Override
  //  public void setText() {
  //      TextView test = (TextView) ((Activity) getContext()).findViewById(R.id.locationCoordinates);
  //  }

}
