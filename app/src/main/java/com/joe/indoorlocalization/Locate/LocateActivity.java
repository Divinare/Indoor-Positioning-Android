package com.joe.indoorlocalization.Locate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.joe.indoorlocalization.Algorithms.K_NearestAlgorithm;
import com.joe.indoorlocalization.Calibration.CalibrateActivity;
import com.joe.indoorlocalization.FileChooser;
import com.joe.indoorlocalization.Models.ExportFile;
import com.joe.indoorlocalization.Models.ImportFile;
import com.joe.indoorlocalization.SideMenu;
import com.joe.indoorlocalization.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class LocateActivity extends AppCompatActivity {

    static String TAG = LocateActivity.class.getSimpleName();

    //WiFi tools
    private static PowerManager.WakeLock wakeLock;
    private WifiManager mainWifi;
    private WifiReceiver receiverWifi;
    private List<ScanResult> wifiList;

    private int maxScans = 1;
    private ProgressDialog progressDialog;
    private StringBuilder fingerPrintData; // mac;rssi;mac;rssi... format
    private StringBuilder networks;

    public float x;
    public float y;

    SideMenu sideMenu = new SideMenu(this);
    private K_NearestAlgorithm deterministicAlgorithm;
    private ImportFile importFile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sideMenu.initSideMenu("locate", this);

        mainWifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "My wakelock");
        receiverWifi = new WifiReceiver();
        this.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        progressDialog= new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.setTitle("RECORDING");
        progressDialog.setMax(maxScans);

        deterministicAlgorithm = new K_NearestAlgorithm(this);
        importFile = new ImportFile(this, "locate");
        importFile.importFile("/sdcard/Android/data/com.joe.indoorlocalization/files/Documents/dataJoe.txt");
        mainWifi.startScan();

        Log.i(TAG, "The app is running :)))");
    }

    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();
        super.onResume();
    }

    class WifiReceiver extends BroadcastReceiver {

        /*
        What to do when our BroadcastReceiver (or in this case, the WifiReceiver that implements it) returns its result
         */
        public void onReceive(Context c, Intent intent) {
            Log.d("FINGER","FingerPrint received");

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
                deterministicAlgorithm.calcLocation(fingerPrintData);
                progressDialog.incrementProgressBy(1);

                mainWifi.startScan();
                Log.d("FINGER","FingerPrint initiated");
            } else {
                try {
                    unregisterReceiver(receiverWifi);
                } catch(Exception e) {
                    Log.e(TAG, "couldn't unregister receiver");
                }
            }
        }
    }




    public static String[] Load(File file)
    {
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        String test;
        int anzahl=0;
        try
        {
            while ((test=br.readLine()) != null)
            {
                anzahl++;
            }
        }
        catch (IOException e) {e.printStackTrace();}

        try
        {
            fis.getChannel().position(0);
        }
        catch (IOException e) {e.printStackTrace();}

        String[] array = new String[anzahl];

        String line;
        int i = 0;
        try
        {
            while((line=br.readLine())!=null)
            {
                array[i] = line;
                i++;
            }
        }
        catch (IOException e) {e.printStackTrace();}
        return array;
    }

    // OPTIONS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuViewSwitch = menu.findItem(R.id.menu_viewSwitch);
        menuViewSwitch.setTitle("Calibrate");

        MenuItem menuShowScans = menu.findItem(R.id.menu_showScans);
        menuShowScans.setVisible(false);
        MenuItem menuLockPoint = menu.findItem(R.id.menu_lockPoint);
        menuLockPoint.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (sideMenu.mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        final Intent intentCalibrate = new Intent(this, CalibrateActivity.class);
        final Intent intentImportDatabase = new Intent(this, FileChooser.class);
        ExportFile exportFile = new ExportFile(this);

        int id = item.getItemId();
        if (id == R.id.menu_viewSwitch) {
            this.startActivity(intentCalibrate);
        } else if(id == R.id.menu_import_database) {
            this.startActivityForResult(intentImportDatabase, 1);
            return true;
        } else if(id == R.id.menu_export_database) {
            exportFile.exportApplicationStateIntoFile();
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
        mainWifi.startScan();
    }

}
