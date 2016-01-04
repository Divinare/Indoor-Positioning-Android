package com.joe.indoorlocalization.Calibration;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.joe.indoorlocalization.ApplicationState;
import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.FileChooser;
import com.joe.indoorlocalization.IndoorLocalization;
import com.joe.indoorlocalization.Locate.LocateActivity;
import com.joe.indoorlocalization.Models.FingerPrint;
import com.joe.indoorlocalization.Models.ImportFile;
import com.joe.indoorlocalization.Models.Scan;
import com.joe.indoorlocalization.SideMenu;
import com.joe.indoorlocalization.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;


public class CalibrateActivity extends AppCompatActivity {

    static String TAG = CalibrateActivity.class.getSimpleName();

    SideMenu sideMenu = new SideMenu(this);
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

    private ApplicationState state;
    private ImportFile importFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        customImageView = (CustomImageView) findViewById(R.id.customImageViewCalibrate);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sideMenu.initSideMenu("calibration", this);

        mainWifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "My wakelock");
        receiverWifi = new WifiReceiver();
        this.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        progressDialog= new ProgressDialog(this);
        setupProgressDialog();
        this.state = ((IndoorLocalization)getApplicationContext()).getApplicationState();
        importFile = new ImportFile(this, "calibrate");
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




    public void saveRecord(View v) {
        startScan();
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
                if (fingerPrintData == null) {
                    Toast.makeText(CalibrateActivity.this, "fingerPrintData was null :( but its ok :D", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d(TAG, "Saving to file: " + fingerPrintData.toString());
                saveFingerPrintIntoApplicationState(fingerPrintData);
                saveFingerPrintIntoFile(fingerPrintData);
                progressDialog.dismiss();
            }
        });
        progressDialog.setButton(ProgressDialog.BUTTON_NEUTRAL, "View", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                progressDialog.setCancelable(false);
                showScanResults();
            }
        });
        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "Discard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                progressDialog.dismiss();
            }
        });
    }

    private void showScanResults() {

        Dialog dialog = new Dialog(CalibrateActivity.this);
        dialog.setContentView(R.layout.popup_dialog);
        dialog.setTitle("Hello");
        TextView dialogTextView = (TextView) dialog.findViewById(R.id.popupDialog);
        dialogTextView.setText(fingerPrintData.toString());
      //  Log.d(TAG, currentFingerPrintData.toString());
        Log.d(TAG, "real currentFpData:");
        Log.d(TAG, fingerPrintData.toString());
        dialog.show();

    }

    private void saveFingerPrintIntoApplicationState(StringBuilder fpData) {
        Point point = customImageView.getLastPoint();
        float x = point.x;
        float y = point.y;
        int z = this.state.getCurrentFloor();
        FingerPrint fp = new FingerPrint(z, x, y);

        String[] fpDataArray = fpData.toString().split(";");


        for(int i = 0; i < fpDataArray.length-1; i=i+2) {
            Log.d(TAG, fpDataArray[i]);
            Scan scan = new Scan(fp, fpDataArray[i], fpDataArray[i+1]);
            fp.addScan(scan);
        }
        this.state.addFingerPrint(fp);
        Log.d(TAG, "Added fp, z: " + fp.getZ() + " x: " + fp.getX() + " y: " + fp.getY());
    }

    private void saveFingerPrintIntoFile(StringBuilder fingerPrintData) {
        Log.d(TAG, "Trying to save file...");
        String fileName = "dataJoe.txt";
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
        Point point = customImageView.getLastPoint();
        float x = point.x;
        float y = point.y;
        int z = this.state.getCurrentFloor();
        Log.d(TAG, "ZZZ: " + z);
        // FORMAT: z;x;y;mac;rssi;mac;rssi;mac;rssi...
        String row = z + ";" + x + ";" + y + ";" + fingerPrintData.toString();
        Toast.makeText(CalibrateActivity.this, "Saving: " + row, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Saving: " + row);

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
        if (sideMenu.mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
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
        mainWifi.startScan();
    }
}
