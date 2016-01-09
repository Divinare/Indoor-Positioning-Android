package com.joe.indoorlocalization.Calibration;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joe.indoorlocalization.ApplicationState;
import com.joe.indoorlocalization.CustomImageView;
import com.joe.indoorlocalization.Drawer;
import com.joe.indoorlocalization.FileChooser;
import com.joe.indoorlocalization.IndoorLocalization;
import com.joe.indoorlocalization.Locate.LocateActivity;
import com.joe.indoorlocalization.Models.ExportFile;
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
    private StringBuilder networks;
    private long scanStartTime;
    private long scanEndTime;
    private ProgressBar progressbar;
    private ProgressBarAnimation progressBarAnimation;
    private ArrayList<StringBuilder> fingerPrintData = new ArrayList<>();

    private ApplicationState state;
    private CalibrationState cState;
    private ImportFile importFile;



    //UI
    private MenuItem menuShowScans;
    private int bottomBarHeight = 60;

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
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        this.state = ((IndoorLocalization)getApplicationContext()).getApplicationState();
        this.cState = state.calibrationState;
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
        public void onReceive(Context c, Intent intent) {
            Log.d("FINGER","FingerPrint received");

            if(runningScan) {
                StringBuilder fingerPrint = new StringBuilder();
                fingerPrint.append((SystemClock.elapsedRealtime() - scanStartTime) + ";");
                networks = new StringBuilder();
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
                Log.d("FINGER", "FingerPrint initiated");
                fingerPrintData.add(fingerPrint);
                mainWifi.startScan();
                scanHasStarted = true;
                TextView scanCount = (TextView)findViewById(R.id.scanCount);
                int count = Integer.parseInt(("" + scanCount.getText()));
                count++;
                scanCount.setText("" + count);
            }
        }
    }

    public void startScan() {
        this.runningScan = true;
        mainWifi.startScan();
        scanHasStarted = true;
        this.scanStartTime = SystemClock.elapsedRealtime();
        cState.setAllowChangeFloor(false);

        Drawable showScansIcon = this.getResources().getDrawable(R.drawable.show_scans_locked);
        menuShowScans.setIcon(showScansIcon);
        cState.setAllowShowScans(false);
    }

    private void stopScan() {
        this.runningScan = false;
        if(scanHasStarted) {
            this.scanEndTime = SystemClock.elapsedRealtime();
            handleScanEnding();
        }
    }

    private void animateInLoop(final ProgressBarAnimation anim, final ProgressBar progressBar) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            if(runningScan) {
                anim.setDuration(1500);
                progressBar.startAnimation(anim);
                animateInLoop(anim, progressBar);
            }
            }
        }, 1500);
    }

    private class ProgressBarAnimation extends Animation {
        private ProgressBar progressBar;
        private float from;
        private float  to;
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
    // START
    private void createStartRecordingBtn() {
        // INITIAL SETUP
        removeExistingButtons();
        state.calibrationState.setPointsSelectable(true);
        cState.setAllowChangeFloor(true);
        cState.setViewCurrentFingerPrints(false);
        cState.resetCurrentScanLocations();

        if(this.menuShowScans != null) {
            Drawable showScansIcon = this.getResources().getDrawable(R.drawable.show_scans);
            menuShowScans.setIcon(showScansIcon);
            cState.setAllowShowScans(true);
        }
        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.calibrationBottomBar);

        // START btn
        Button btnStartRecording = new Button(this);
        modifyButtonStyling(btnStartRecording);
        btnStartRecording.setId(R.id.btnStartRecording);
        btnStartRecording.setText("Start Recording");
        btnStartRecording.setBackgroundResource(R.drawable.buttonshape);
        btnStartRecording.setTextColor(Color.WHITE);
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

        // LOCK btn
        /*
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setId(R.id.lockPointRelativeLayout);
        LinearLayout.LayoutParams relativeLayoutParams = createLinearLayoutParams();
        relativeLayoutParams.setMargins(30, 0, 30, 0);
        relativeLayout.setLayoutParams(relativeLayoutParams);
*/
        Button lockPoint = new Button(this);
        lockPoint.setText("Lock");
        lockPoint.setId(R.id.lockPoint);
        lockPoint.setTextColor(Color.WHITE);
        modifyButtonStyling(lockPoint);
        int sizeInPixels = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomBarHeight-10, getResources().getDisplayMetrics());
        lockPoint.setWidth(sizeInPixels);
        lockPoint.setHeight(sizeInPixels);
        //lockPoint.setPadding(((sizeInPixels / 4) + 10), 0, 0, 0); // An ugly hack, I know :(
        //lockPoint.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, 0, 0);
        lockPoint.setBackgroundResource(R.drawable.buttonshape);
        lockPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state.calibrationState.lockSelectedPoint();
            }
        });
        //relativeLayout.addView(lockPoint);
        bottomBar.addView(lockPoint);
        state.calibrationState.setLockToDrawing(false);
        customImageView.invalidate();
    }

    // STOP
    private void createStopRecordingBtn() {
        removeExistingButtons();
        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.calibrationBottomBar);

        // STOP RECORDING btn
        final Button btnStopRecording = new Button(this);
        modifyButtonStyling(btnStopRecording);
        btnStopRecording.setId(R.id.btnStopRecording);
        btnStopRecording.setText("Stop recording");
        btnStopRecording.setBackgroundResource(R.drawable.buttonshape);
        btnStopRecording.setTextColor(Color.WHITE);
        btnStopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScan();
                btnStopRecording.setEnabled(false);
            }
        });
        bottomBar.addView(btnStopRecording);

        // SCAN ANIMATION
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setId(R.id.progressbar_relativeLayout);
        LinearLayout.LayoutParams relativeLayoutParams = createLinearLayoutParams();
        relativeLayout.setBackgroundColor(000);
        relativeLayout.setPadding(10, 10, 10, 10);
        relativeLayout.setLayoutParams(relativeLayoutParams);
        LayoutInflater inflater = getLayoutInflater();
        this.progressbar = (ProgressBar ) inflater.inflate(R.layout.progressbar, null);
        float from = 0;
        float to = 100;
        progressBarAnimation = new ProgressBarAnimation(progressbar, from, to);
        progressBarAnimation.setDuration(1500);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1.0f;
        progressbar.setLayoutParams(params);
        progressbar.invalidate();

        progressbar.startAnimation(progressBarAnimation);
        animateInLoop(progressBarAnimation, progressbar);
        relativeLayout.addView(progressbar);
        bottomBar.addView(relativeLayout);

        // SCAN COUNT
        TextView scanCount = new TextView(this);
        scanCount.setId(R.id.scanCount);
        scanCount.setText("0");
        scanCount.setTextColor(Color.parseColor("#FFFFFF"));
        scanCount.setPadding(20, 20, 20, 20);
        bottomBar.addView(scanCount);
    }

    // SAVE, DISMISS
    private void createSaveAndDismissBtn() {
        removeExistingButtons();
        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.calibrationBottomBar);

        Button btnSaveRecording = new Button(this);
        modifyButtonStyling(btnSaveRecording);
        btnSaveRecording.setId(R.id.btnSaveRecording);
        btnSaveRecording.setText("Save");
        btnSaveRecording.setBackgroundResource(R.drawable.buttonshape);
        btnSaveRecording.setTextColor(Color.WHITE);
        btnSaveRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFingerPrintDataIntoApplicationState();
                createStartRecordingBtn();
            }
        });
        bottomBar.addView(btnSaveRecording);

        Button btnDismissRecording = new Button(this);
        modifyButtonStyling(btnDismissRecording);
        btnDismissRecording.setId(R.id.btnDismissRecording);
        btnDismissRecording.setText("Dismiss");
        btnDismissRecording.setBackgroundResource(R.drawable.buttonshape);
        btnDismissRecording.setTextColor(Color.WHITE);
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
        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.calibrationBottomBar);
        Button startRecordingBtn = (Button) bottomBar.findViewById(R.id.btnStartRecording);
        Button stopRecordingBtn = (Button) bottomBar.findViewById(R.id.btnStopRecording);
        Button saveRecordingBtn = (Button) bottomBar.findViewById(R.id.btnSaveRecording);
        Button dismissRecordingBtn = (Button) bottomBar.findViewById(R.id.btnDismissRecording);
        Button removeRecordingBtn = (Button) bottomBar.findViewById(R.id.btnRemoveRecording);
        Button lockPointBtn = (Button) bottomBar.findViewById(R.id.lockPoint);

        removeElementIfNotNull(startRecordingBtn);
        removeElementIfNotNull(stopRecordingBtn);
        removeElementIfNotNull(saveRecordingBtn);
        removeElementIfNotNull(dismissRecordingBtn);
        removeElementIfNotNull(removeRecordingBtn);
        removeElementIfNotNull(lockPointBtn);
    }
    private void removeElementIfNotNull(View element) {
        if(element != null) {
            ((ViewGroup) element.getParent()).removeView(element);
        }
    }

    // HELPER METHODS

    private void modifyButtonStyling(Button btn) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        params.setMargins(5, 5, 5, 5);
        btn.setLayoutParams(params);
    }

    // creates  bottomBarHeight x bottomBarHeight relativeLayout
    private LinearLayout.LayoutParams createLinearLayoutParams() {
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        float sizeInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomBarHeight, getResources().getDisplayMetrics());
        linearLayoutParams.width = (int)sizeInPixels;
        linearLayoutParams.height = (int)sizeInPixels;
        return linearLayoutParams;
    }

    private void handleScanEnding() {
        scanHasStarted = false;
        showAfterScanDialog();
        Button btnStopRecording = (Button) findViewById(R.id.btnStopRecording);
        btnStopRecording.setVisibility(View.INVISIBLE);

        LinearLayout bottomBar = (LinearLayout) findViewById(R.id.calibrationBottomBar);
        RelativeLayout relativeLayout = (RelativeLayout) bottomBar.findViewById(R.id.progressbar_relativeLayout);
        if(relativeLayout != null) {
            ((ViewGroup) relativeLayout.getParent()).removeView(relativeLayout);
        }
        TextView scanCount = (TextView) findViewById(R.id.scanCount);
        if(scanCount != null) {
            ((ViewGroup) scanCount.getParent()).removeView(scanCount);
        }
    }

    // DIALOG AFTER SCAN
    private void showAfterScanDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.after_scan_dialog);
        dialog.setTitle("Scan ended");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // SAVE
        Button buttonSave = (Button) dialog.findViewById(R.id.afterScanDialogSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFingerPrintDataIntoApplicationState();
                dialog.dismiss();
            }
        });

        // VIEW
        Button buttonView = (Button) dialog.findViewById(R.id.afterScanDialogView);
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (fingerPrintData.size() == 0) {
                    Toast.makeText(CalibrateActivity.this, "Nothing to show. You need to scan for at least 3 seconds.", Toast.LENGTH_LONG).show();
                } else {
                    saveCurrentScanLocationsIntoCalibrationState();
                    cState.setViewCurrentFingerPrints(true);
                    customImageView.invalidate();
                }
            }
        });

        // DISMISS
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
                Log.d(TAG, "At dismiss, " + fingerPrintData.size());
                if (fingerPrintData.size() > 0) {
                    createSaveAndDismissBtn();
                } else {
                    createStartRecordingBtn();
                }
            }
        });

        dialog.show();
    }

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

    private void saveCurrentScanLocationsIntoCalibrationState() {
        long scanTime = (scanEndTime - scanStartTime);
        Point p1 = cState.point1;
        Point p2 = cState.point2;
        for(StringBuilder fpCurrent : fingerPrintData) {
            String[] fpArray = fpCurrent.toString().split(";");
            long timestamp = Long.parseLong(fpArray[0]);
            double ratio = (double)timestamp/(double)scanTime;
            float x;
            float y;
            if(p1.x < p2.x ) {
                x = (int)((p2.x - p1.x)*ratio);
                x += p1.x;
            } else {
                float result = (int)((p1.x - p2.x)*ratio);
                x = p1.x - result;
            }
            if(p1.y < p2.y ) {
                y = (int)((p2.y - p1.y)*ratio);
                y += p1.y;
            } else {
                float result = (int)((p1.y - p2.y)*ratio);
                y = p1.y - result;
            }
            cState.addToCurrentScanLocations(new Point((int) x, (int) y));
        }
    }

    private void saveFingerPrintDataIntoApplicationState() {
        CalibrationState cState = this.state.calibrationState;

        long scanTime = (scanEndTime - scanStartTime);

        Point p1 = cState.point1;
        Point p2 = cState.point2;
        for(StringBuilder fpCurrent : fingerPrintData) {
            String[] fpArray = fpCurrent.toString().split(";");
            long timestamp = Long.parseLong(fpArray[0]);
            double ratio = (double)timestamp/(double)scanTime;
            float x;
            float y;
            if(p1.x < p2.x ) {
                x = (int)((p2.x - p1.x)*ratio);
                x += p1.x;
            } else {
                float result = (int)((p1.x - p2.x)*ratio);
                x = p1.x - result;
            }
            if(p1.y < p2.y ) {
                y = (int)((p2.y - p1.y)*ratio);
                y += p1.y;
            } else {
                float result = (int)((p1.y - p2.y)*ratio);
                y = p1.y - result;
            }
            Log.d(TAG, "X: " + x + " Y: " + y);
            int z = this.state.getCurrentFloor();
            FingerPrint fp = new FingerPrint(z, x, y);

            for(int i = 1; i < fpArray.length-1; i=i+2) {
                Log.d(TAG, fpArray[i]);
                Scan scan = new Scan(fp, fpArray[i], fpArray[i+1]);
                fp.addScan(scan);
            }
            this.state.addFingerPrint(fp);
        }
        Toast.makeText(CalibrateActivity.this, fingerPrintData.size() + " fingerprints saved to memory.", Toast.LENGTH_LONG).show();
        fingerPrintData = new ArrayList<>();
    }

    // OPTIONS MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menuShowScans = menu.findItem(R.id.menu_showScans);
        menuShowScans.setVisible(true);
        Drawable showScansIcon = this.getResources().getDrawable(R.drawable.show_scans);
        menuShowScans.setIcon(showScansIcon);
        cState.setAllowShowScans(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuViewSwitch = menu.findItem(R.id.menu_viewSwitch);
        menuViewSwitch.setTitle("Locate");
        return super.onPrepareOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (sideMenu.mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        final Intent intentLocate = new Intent(this, LocateActivity.class);
        final Intent intentImportDatabase = new Intent(this, FileChooser.class);
        ExportFile exportFile = new ExportFile(this);
        int id = item.getItemId();
        if (id == R.id.menu_viewSwitch) {
            this.cState.setAllowChangeFloor(true);
            this.startActivity(intentLocate);
        } else if(id == R.id.menu_import_database) {
            this.startActivityForResult(intentImportDatabase, 1);
            return true;
        } else if(id == R.id.menu_export_database) {
            exportFile.exportApplicationStateIntoFile();
        } else if(id == R.id.menu_help) {
            return true;
        } else if(id == R.id.menu_showScans) {
            this.cState.toggleShowingScans();
            if(!this.cState.showingScans()) {
                createStartRecordingBtn();
            }
            Toast.makeText(this, "Not allowed to show scans at this point", Toast.LENGTH_LONG);
            customImageView.invalidate();
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
        startScan();
    }


}
