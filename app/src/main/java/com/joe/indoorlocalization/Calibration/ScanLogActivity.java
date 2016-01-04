package com.joe.indoorlocalization.Calibration;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.joe.indoorlocalization.FileChooser;
import com.joe.indoorlocalization.Locate.LocateActivity;
import com.joe.indoorlocalization.Models.ImportFile;
import com.joe.indoorlocalization.R;

public class ScanLogActivity extends Activity {

    static String TAG = "ScanLogActivity";
    private ImportFile importFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_log);
        importFile = new ImportFile(this, "calibrate");

        LinearLayout scanLogScrollView = (LinearLayout)findViewById(R.id.scanLogLinearLayout);
/*
       // List<Scan> scans = Scan.listAll(Scan.class);

        for(int i = 0; i < fingerPrints.size(); i++) {
            //float x = fingerPrints.get(i).getX();
            //float y = fingerPrints.get(i).getY();
            //float z = fingerPrints.get(i).getZ();
            String mac = fingerPrints.get(i).getMac();
            String RSSI = fingerPrints.get(i).getRSSI();
            //String networks = fingerPrints.get(i).getNetworks();

            Log.i(TAG, mac + "\n" + RSSI + "\n");

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
                    Scan fingerPrint = Scan.findById(Scan.class, id);
                    fingerPrint.delete();
                }
            });
            ll.addView(removeBtn, layoutParams);
            scanLogScrollView.addView(ll);

        }
*/
    }



    public void showCalibration(View v) {
        Intent intentCalibrate = new Intent(this, CalibrateActivity.class);
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
        final Intent intentLocate = new Intent(this, LocateActivity.class);
        final Intent intentCalibrate = new Intent(this, CalibrateActivity.class);
        final Intent intentImportDatabase = new Intent(this, FileChooser.class);

        int id = item.getItemId();
        if (id == R.id.menu_locate) {
            this.startActivity(intentLocate);
        } else if(id == R.id.menu_calibrate) {
            this.startActivity(intentCalibrate);
        } else if(id == R.id.menu_import_database) {
            this.startActivityForResult(intentImportDatabase, 1);
            return true;
        } else if(id == R.id.menu_help) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Log.d(TAG, "Got result!");
                String path=data.getStringExtra("path");
                Log.d(TAG, "result: " + path);
                importFile.importFile(path);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Didnt get result from importFile");
                Toast.makeText(this, "Couldn't get file data", Toast.LENGTH_SHORT);
            }
        }
    }

}
