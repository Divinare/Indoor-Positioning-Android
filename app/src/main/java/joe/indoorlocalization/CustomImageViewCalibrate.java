package joe.indoorlocalization;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by joe on 22/11/15.
 */
public class CustomImageViewCalibrate extends CustomImageView {

    static String TAG = "CustomImageViewCalibrate";


    public CustomImageViewCalibrate(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setX(float x) {
        TextView test = (TextView) ((Activity) getContext()).findViewById(R.id.positionX);
        test.setText("x: " + x);
    }

    @Override
    public void setY(float y) {
        TextView test = (TextView) ((Activity) getContext()).findViewById(R.id.positionY);
        test.setText("y: " + y);
    }

    @Override
    public void setText() {
    }

}