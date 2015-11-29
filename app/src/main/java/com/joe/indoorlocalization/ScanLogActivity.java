package com.joe.indoorlocalization;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ScanLogActivity extends Activity {

    static String TAG = "ScanLogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_log);

        LinearLayout scanLogScrollView = (LinearLayout)findViewById(R.id.scanLogLinearLayout);

        List<FingerPrint> fingerPrints = FingerPrint.listAll(FingerPrint.class);
        for(int i = 0; i < fingerPrints.size(); i++) {
            float x = fingerPrints.get(i).getX();
            float y = fingerPrints.get(i).getY();
            float z = fingerPrints.get(i).getZ();
            String macs = fingerPrints.get(i).getMacs();
            String RSSIs = fingerPrints.get(i).getRSSIs();
            String networks = fingerPrints.get(i).getNetworks();

            Log.i(TAG, x + ", " + y + ", " + z + "\n" + macs + "\n" + RSSIs + "\n" + networks + "\n");
            LinearLayout ll = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(30, 0, 30, 20); // left, top, right, bottom
            TextView tv = new TextView(this);
            //tv.setId(("scanLogTextView"+i));
            tv.setText("x: " + x + " y: " + y + " z: " + z);
            ll.addView(tv);

            final Long id = fingerPrints.get(i).getId();
            Button removeBtn = new Button(this);
            removeBtn.setHeight(50);
            removeBtn.setText("Remove");
            removeBtn.setBackgroundColor(Color.rgb(70, 80, 90));
            removeBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Log.i(TAG, "Clicked button with id: " + id);
                    FingerPrint fingerPrint = FingerPrint.findById(FingerPrint.class, id);
                    fingerPrint.delete();
                }
            });
            ll.addView(removeBtn, layoutParams);
            scanLogScrollView.addView(ll);
        }

    }



    public void showCalibration(View v) {
        Intent intentCalibrate = new Intent(this, CalibrationActivity.class);
        startActivity(intentCalibrate);
    }


    // OPTIONS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Options options = new Options();
        boolean ret = options.optionsItemSelected(item, this);
        if (ret) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
