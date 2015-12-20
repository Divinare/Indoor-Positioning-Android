package com.joe.indoorlocalization.Calibration;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.Drawer;
import com.joe.indoorlocalization.Options;
import com.joe.indoorlocalization.R;
import com.joe.indoorlocalization.WifiScanner;
import com.joe.indoorlocalization.WifiScannerOld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class CalibrationActivity extends AppCompatActivity {

    static String TAG = CalibrationActivity.class.getSimpleName();

    Drawer drawer = new Drawer(this);
    private CustomImageView customImageView;


    //WiFi tools
    private static PowerManager.WakeLock wakeLock;
    private WifiManager mainWifi;
    private WifiReceiver receiverWifi;
    private List<ScanResult> wifiList;

    private int maxScans = 1;
    private ProgressDialog progressDialog;
    private StringBuilder fingerPrintData; // mac;rssi;mac;rssi... format
    private StringBuilder networks;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        customImageView = (CustomImageView) findViewById(R.id.customImageViewCalibrate);

        getSupportActionBar().setTitle("Calibrate");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawer.initDrawer("calibration", this);

        mainWifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "My wakelock");
        receiverWifi = new WifiReceiver();
        this.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        progressDialog= new ProgressDialog(this);
        setupProgressDialog();

    }

    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    class WifiReceiver extends BroadcastReceiver {

        /*
        What to do when our BroadcastReceiver (or in this case, the WifiReceiver that implements it) returns its result
         */
        public void onReceive(Context c, Intent intent) {
            Log.d("FINGER","Scan received");

            fingerPrintData = new StringBuilder();
            networks = new StringBuilder();

            if (progressDialog.getProgress()<=progressDialog.getMax()) {
                wifiList = mainWifi.getScanResults();

                for(int j=0; j<wifiList.size(); j++) {

                    if(wifiList.get(j).level != 0) {
                        fingerPrintData.append(wifiList.get(j).BSSID);
                        fingerPrintData.append(';');
                        fingerPrintData.append(wifiList.get(j).level);
                        if (j < wifiList.size() - 1) {
                            fingerPrintData.append(";");
                        }
                    }
                }

                progressDialog.incrementProgressBy(1);

                mainWifi.startScan();
                Log.d("FINGER","Scan initiated");
            } else {
                try {
                    unregisterReceiver(receiverWifi);
                } catch(Exception e) {
                    Log.e(TAG, "couldn't unregister receiver");
                }
            }
        }
    }




    public void saveRecord(View v) {
        startScan();
        //wifiScanner.startScan();
        //saveIntoFile();

    }

    public void startScan() {
        Log.d(TAG, "scan started!");
        mainWifi.startScan();
        progressDialog.setTitle("Training");
        progressDialog.show();

    }

    public void setupProgressDialog() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.setTitle("Recording");
        progressDialog.setMax(maxScans);

        progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Toast.makeText(CalibrationActivity.this, "Saving " + prints.size() + " fingerprints...", Toast.LENGTH_SHORT).show();
                //savePrints();
            }
        });
        progressDialog.setButton(ProgressDialog.BUTTON_NEUTRAL, "View", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                progressDialog.dismiss();
                //showScanResults(prints);
            }
        });
        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                progressDialog.dismiss();
            }
        });
    }

    private void saveIntoFile(StringBuilder fingerPrintData) {
        String fileName = "dataJoe.txt";

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

        Log.i(TAG, "Trying to save file...");


        //StringBuilder fingerPrintData = wifiScanner.getFingerPrintData();
        Point point = customImageView.getLastPoint();
        float x = point.x;
        float y = point.y;
        int z = drawer.getCurrentFloor();

        // FORMAT: z;x;y;mac;rssi;mac;rssi;mac;rssi...
        String row = z + ";" + x + ";" + y + ";" + fingerPrintData.toString();

        try {
            OutputStream os = new FileOutputStream(file, true);
            os.write("\n".getBytes());
            os.write(row.getBytes());
            os.close();
            os.flush();
            Log.i(TAG, "File saved");
        } catch (Exception e) {
            Log.d(TAG, "Error on saving file");
            Log.d(TAG, "Exception on data export");
            Log.d(TAG, e.getMessage());
        }
    }

    public void showScanLog(View v) {
        Intent intentScanLog = new Intent(this, ScanLogActivity.class);
        startActivity(intentScanLog);
    }


    // OPTIONS MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawer.mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        Options options = new Options();
        boolean ret = options.optionsItemSelected(item, this);
        if (ret) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
