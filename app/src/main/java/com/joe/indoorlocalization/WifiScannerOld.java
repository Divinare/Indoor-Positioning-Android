package com.joe.indoorlocalization;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

//import jsc.distributions.Normal;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import com.joe.indoorlocalization.Calibration.CalibrationActivity;

/**
 *  Is used for Wifi fingerprints.
 *
 * This code has branched off an early version of HyperLoc, so if you see some oddities, this might be why.
 * @author joe
 *
 */
public class WifiScannerOld extends CalibrationActivity {

    static String TAG = WifiScannerOld.class.getSimpleName();

    //WiFi tools
    WifiManager mService;
    static PowerManager.WakeLock wakeLock;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;

    //The maximum number of fingerprints we want to record (for a ballpark figure, assume approx. 1 fingerprint/second on current Android devices)
    static final int MAXPRINTS = 10;

    //True while training is in process
    boolean running = false;
    //Grid cell as indicated by the user's selection in the gridview
    static int position = 0;
    //Asynchronous task (thread). We capture the initialization so we can control it after it's started
    AsyncTask<Integer, String, Hashtable<String, List<Integer>>> task = new RecordFingerprints();
    //Popup dialog that displays progress (also helps detect if the user has aborted the training process)
    static ProgressDialog progressDialog;

    int cells = 51; //Needs to match the number of icons in "drawable"

    static StringBuilder fingerPrintData; // mac;rssi;mac;rssi... format
    static StringBuilder networks;

    private Context context;
/*
    public WifiScannerOld() {
        init();
    }
*/
    public WifiScannerOld(Context c) {
        this.context = c;
        initialize();
    }

    private void initialize() {
        mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "My wakelock");
        receiverWifi = new WifiReceiver();
        context.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        progressDialog= new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.setTitle("RECORDING");
        progressDialog.setMax(MAXPRINTS);
        Log.d(TAG, "progressDialog created!");

    }

    public void start() {
        task.execute();
    }

    public void stop() {
        task.cancel(true);
    }

    public StringBuilder getFingerPrintData() {
        return this.fingerPrintData;
    }

    public StringBuilder getNetworks() {
        return this.networks;
    }


    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiverWifi);
        super.onStop();
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
                    /*
                    if (j<wifiList.size()-1) {
                        macs.append(",");
                    }
                    RSSIs.append(wifiList.get(j).level);
                    if (j<wifiList.size()-1) {
                        RSSIs.append(",");
                    }
                    networks.append(wifiList.get(j).SSID);
                    if (j<wifiList.size()-1) {
                        networks.append(",");
                    }
                    */
                    }
                }
                /*
                fingerprint.append(macs);
                fingerprint.append("\n");
                fingerprint.append(RSSIs);
                fingerprint.append("\n");
                fingerprint.append(networks);
                fingerprint.append("\n");
                */

                progressDialog.incrementProgressBy(1);

                mainWifi.startScan();
                Log.d("FINGER","Scan initiated");
            }

        }
    }

    //Asynchronous task runs in background so we don't make the UI wait
    private class RecordFingerprints extends AsyncTask<Integer, String, Hashtable<String,List<Integer>>> {
        boolean running = true;
        protected Hashtable<String,List<Integer>> doInBackground(Integer... params) {

            progressDialog.setProgress(0);
            mainWifi.startScan();

            wakeLock.acquire();
            while(running){
                //Store the recorded fingerprint in a file named after the cell in which it was recorded
                if(!progressDialog.isShowing() || progressDialog.getProgress()>=progressDialog.getMax()){
                    //Log.d(TAG, fingerprint.toString());

                    /*
                    File file = new File(PATH, "/fingerprints/"+params[0]+".txt");
                    try{
                        OutputStream os = new FileOutputStream(file,false);
                        os.write(fingerprint.toString().getBytes());
                        os.close();
                    }
                    catch(Exception e){Log.d("HELP","Need somebody");}
                    progressDialog.dismiss();
                    return null;
                    */
                }

                else{
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
            //progressDialog.incrementProgressBy(1);
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
}