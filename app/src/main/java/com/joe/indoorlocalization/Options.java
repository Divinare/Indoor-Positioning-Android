package com.joe.indoorlocalization;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;


/**
 * Created by joe on 22/11/15.
 */
public class Options {


    public static boolean optionsItemSelected(MenuItem item, Context context) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        final Intent intentLocate = new Intent(context, LocateActivity.class);
        final Intent intentCalibrate = new Intent(context, CalibrationActivity.class);


        int id = item.getItemId();
        if (id == R.id.menu_locate) {
            context.startActivity(intentLocate);
        } else if(id == R.id.menu_calibrate) {
            context.startActivity(intentCalibrate);
        } else if(id == R.id.menu_setPosition) {
            return true;
        } else if(id == R.id.menu_help) {
            return true;
        }

        return false;
    }
}
