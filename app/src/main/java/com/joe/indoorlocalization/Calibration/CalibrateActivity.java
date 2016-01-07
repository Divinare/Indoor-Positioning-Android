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
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
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

    // For scanning
    private int maxScans = 1;
    private boolean runningScan = false;
    private boolean scanHasStarted = false;
    //private ProgressDialog progressDialog;
    //private StringBuilder fingerPrintData; // mac;rssi;mac;rssi... format
    private StringBuilder networks;
    private ArrayList<StringBuilder> fingerPrintData = new ArrayList<>();

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
        //this.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        //progressDialog= new ProgressDialog(this);
        //setupProgressDialog();
        this.state = ((IndoorLocalization)getApplicationContext()).getApplicationState();
        importFile = new ImportFile(this, "calibrate");
        createStartRecordingBtn();
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

            StringBuilder fingerPrint = new StringBuilder();
            networks = new StringBuilder();

            //if (progressDialog.getProgress()<=progressDialog.getMax()) {
            if(runningScan) {
                wifiList = mainWifi.getScanResults();

                for(int j=0; j<wifiList.size(); j++) {

                    if(wifiList.get(j).level != 0) {
                        fingerPrint.append(wifiList.get(j).BSSID);
                        fingerPrint.append(';');
                        fingerPrint.append(wifiList.get(j).level);
                        if (j < wifiList.size() - 1) {
                            fingerPrint.append(";");
                        }
                    }
                }
                fingerPrintData.add(fingerPrint);
                //progressDialog.incrementProgressBy(1);

                mainWifi.startScan();
                scanHasStarted = true;
                Log.d("FINGER","FingerPrint initiated");
            } else if(scanHasStarted) {
                try {
                    unregisterReceiver(receiverWifi);
                } catch(Exception e) {
                    Log.e(TAG, "Couldn't unregister receiver");
                }
                handleScanEnding();
            }
        }
    }


    public void startScan() {
        Log.d(TAG, "scan started!");
        this.runningScan = true;
        mainWifi.startScan();
        scanHasStarted = true;

        //progressDialog.show();

        createStartRecordingBtn();


        /*
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setTitle("Title...");

        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText("Android custom dialog example!");
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressbar);
        float from = 0;
        float to = 100;
        final ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, from, to);
        anim.setDuration(1500);
        progressBar.startAnimation(anim);
        animateInLoop(anim, progressBar);
        */
    }

    private void stopScan() {
        this.runningScan = false;
    }

    private void animateInLoop(final ProgressBarAnimation anim, final ProgressBar progressBar) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "At animate run...");
                anim.setDuration(1500);
                progressBar.startAnimation(anim);
                animateInLoop(anim, progressBar);
            }
        }, 1500);

    }

    private class ProgressBarAnimation extends Animation {
        private ProgressBar progressBar;
        private float from;
        private float  to;
        private int val = 0;
        public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            progressBar.setProgress((int) value);
        }

    }

    // BOTTOM BAR BUTTONS
    private void createStartRecordingBtn() {
        removeExistingButtons();
        this.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        state.calibrationState.setPointsSelectable(true);

        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.calibrationBottomBar);
        Button btnStartRecording = new Button(this);
        modifyButtonStyling(btnStartRecording);
        btnStartRecording.setId(R.id.btnStartRecording);
        btnStartRecording.setText("Start Recording");
        btnStartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
                state.calibrationState.setPointsSelectable(false);
                state.calibrationState.setLockToDrawing(true);
                createStopRecordingBtn();

            }
        });

        if(this.state.calibrationState.pointsExist()) {
            btnStartRecording.setEnabled(true);
        } else {
            btnStartRecording.setEnabled(false);
        }
        bottomBar.addView(btnStartRecording);
        state.calibrationState.setLockToDrawing(false);
    }

    private void createStopRecordingBtn() {
        removeExistingButtons();
        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.calibrationBottomBar);

        // REMEMBER ANIMATION

        final Button btnStopRecording = new Button(this);
        modifyButtonStyling(btnStopRecording);
        btnStopRecording.setId(R.id.btnStopRecording);
        btnStopRecording.setText("Stop recording");
        btnStopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScan();
                btnStopRecording.setEnabled(false);
            }
        });
        bottomBar.addView(btnStopRecording);
    }

    private void createSaveAndDismissBtn() {
        removeExistingButtons();
        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.calibrationBottomBar);

        Button btnSaveRecording = new Button(this);
        modifyButtonStyling(btnSaveRecording);
        btnSaveRecording.setId(R.id.btnSaveRecording);
        btnSaveRecording.setText("Save");
        btnSaveRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "at save :) ");
                createStartRecordingBtn();
            }
        });
        bottomBar.addView(btnSaveRecording);

        Button btnDismissRecording = new Button(this);
        modifyButtonStyling(btnDismissRecording);
        btnDismissRecording.setId(R.id.btnDismissRecording);
        btnDismissRecording.setText("Dismiss");
        btnDismissRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerPrintData = new ArrayList<>(); // clear current fpData
                createStartRecordingBtn();
            }
        });
        bottomBar.addView(btnDismissRecording);
    }

    private void removeExistingButtons() {
        Log.d(TAG, "removing existing buttons!");
        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.calibrationBottomBar);

        Button startRecordingBtn = (Button) bottomBar.findViewById(R.id.btnStartRecording);
        Button stopRecordingBtn = (Button) bottomBar.findViewById(R.id.btnStopRecording);
        Button saveRecordingBtn = (Button) bottomBar.findViewById(R.id.btnSaveRecording);
        Button dismissRecordingBtn = (Button) bottomBar.findViewById(R.id.btnDismissRecording);
        removeElementIfNotNull(startRecordingBtn);
        removeElementIfNotNull(stopRecordingBtn);
        removeElementIfNotNull(saveRecordingBtn);
        removeElementIfNotNull(dismissRecordingBtn);
    }
    private void removeElementIfNotNull(View element) {
        if(element != null) {
            ((ViewGroup) element.getParent()).removeView(element);
        }
    }

    private void modifyButtonStyling(Button btn) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        btn.setLayoutParams(params);
    }


    private void handleScanEnding() {
        scanHasStarted = false;
        showAfterScanDialog();
        Button btnStopRecording = (Button) findViewById(R.id.btnStopRecording);
        btnStopRecording.setVisibility(View.INVISIBLE);
    }

    private void showAfterScanDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.after_scan_dialog);
        dialog.setTitle("Scan ended");

        Button buttonSave = (Button) dialog.findViewById(R.id.afterScanDialogSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call save here()
                dialog.dismiss();
            }
        });

        Button buttonView = (Button) dialog.findViewById(R.id.afterScanDialogView);
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button buttonDismiss = (Button) dialog.findViewById(R.id.afterScanDialogDismiss);
        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerPrintData = new ArrayList<>();
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                if(fingerPrintData.size() > 0) {
                    createSaveAndDismissBtn();
                } else {
                    createStartRecordingBtn();
                }
            }
        });

        dialog.show();
    }

    /*
    public void setupProgressDialog() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.setTitle("Recording");
        progressDialog.setMax(10);

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
    */

    /*
    private void showScanResults() {

        Dialog dialog = new Dialog(CalibrateActivity.this);
        dialog.setContentView(R.layout.popup_dialog);
        dialog.setTitle("Scan Results");
        TextView dialogTextView = (TextView) dialog.findViewById(R.id.popupDialog);
        dialogTextView.setText(fingerPrintData.toString());
        Log.d(TAG, "real currentFpData:");
        Log.d(TAG, fingerPrintData.toString());
        dialog.show();

    }
    */

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


    // OPTIONS MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuViewSwitch = menu.findItem(R.id.menu_viewSwitch);
        menuViewSwitch.setTitle("Locate");

        MenuItem menuShowScans = menu.findItem(R.id.menu_showScans);
        menuShowScans.setVisible(true);
        MenuItem menuLockPoint = menu.findItem(R.id.menu_lockPoint);
        menuLockPoint.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
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
        if (id == R.id.menu_viewSwitch) {
            this.startActivity(intentLocate);
        } else if(id == R.id.menu_import_database) {
            this.startActivityForResult(intentImportDatabase, 1);
            return true;
        } else if(id == R.id.menu_help) {
            return true;
        } else if(id == R.id.menu_showScans) {
            this.state.calibrationState.toggleShowingScans();
            customImageView.invalidate();
        } else if(id == R.id.menu_lockPoint) {
            state.calibrationState.lockSelectedPoint();
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
        Log.d(TAG, "START SCANNNN3");
        mainWifi.startScan();
        scanHasStarted = true;
    }


}
