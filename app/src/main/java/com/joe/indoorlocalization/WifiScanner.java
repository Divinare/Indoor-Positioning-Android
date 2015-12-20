package com.joe.indoorlocalization;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by joe on 14/12/15.
 */
public abstract class WifiScanner extends AppCompatActivity {

    static String TAG = WifiScanner.class.getSimpleName();

    //WiFi tools
    protected static PowerManager.WakeLock wakeLock;
    protected WifiManager mainWifi;
    protected WifiReceiver receiverWifi;
    protected List<ScanResult> wifiList;

    protected int maxScans = 1;

    //True while training is in process
    boolean running = false;
    //Grid cell as indicated by the user's selection in the gridview
    static int position = 0;
    //Asynchronous task (thread). We capture the initialization so we can control it after it's started

    //AsyncTask<Integer, String, Hashtable<String, List<Integer>>> task = new RecordFingerprints();

    //Popup dialog that displays progress (also helps detect if the user has aborted the training process)
    protected ProgressDialog progressDialog;
    protected StringBuilder fingerPrintData; // mac;rssi;mac;rssi... format
    protected StringBuilder networks;

    //private Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    public void startScan() {
        Log.d(TAG, "scan started!");
        //Task has been initialized but not run a single time yet
            mainWifi.startScan();


            //Show the progress dialog
            progressDialog.setTitle("TRAINING CELL "+position);
            progressDialog.show();
            //Start the recording

        //Task has been allowed to finish
            //Re-initialize
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
    /*

    //Asynchronous task runs in background so we don't make the UI wait
    private class RecordFingerprints extends AsyncTask<Integer, String, Hashtable<String,List<Integer>>> {
        boolean running = true;

        protected Hashtable<String, List<Integer>> doInBackground(Integer... params) {

            progressDialog.setProgress(0);
            mainWifi.startScan();

            wakeLock.acquire();
            while (running) {
                //Store the recorded fingerprint in a file named after the cell in which it was recorded
                if (!progressDialog.isShowing() || progressDialog.getProgress() >= progressDialog.getMax()) {
                    //Log.d(TAG, fingerprint.toString());
                    Log.d(TAG, "at progress dialog running method");

                    File file = new File(PATH, "/fingerprints/"+params[0]+".txt");
                    try{
                        OutputStream os = new FileOutputStream(file,false);
                        os.write(fingerprint.toString().getBytes());
                        os.close();
                    }
                    catch(Exception e){Log.d("HELP","Need somebody");}
                    progressDialog.dismiss();
                    return null;

                } else {
                    publishProgress("");
                }

                Thread.currentThread();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            wakeLock.release();
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d(TAG, "at progress update");
            progressDialog.incrementProgressBy(1);
        }

        @SuppressWarnings("unused")
        protected void onPostExecute(ArrayList<Integer> result) {
            running = true;
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
            running = false;
            return;
        }
    }
    */

    public StringBuilder getFingerPrintData() {
        return this.fingerPrintData;
    }
    public StringBuilder getNetworks() {
        return this.networks;
    }
}
