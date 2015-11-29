package joe.indoorlocalization;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class CalibrationActivity extends AppCompatActivity {

    static String TAG = CalibrationActivity.class.getSimpleName();
    private CustomImageView customImageView;
    WifiScanner wifiScanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        customImageView = (CustomImageView) findViewById(R.id.customImageViewCalibrate);

        wifiScanner = new WifiScanner(this);
        wifiScanner.start();
    }

    public void saveRecord(View v) {
        String macs = wifiScanner.getMacs().toString();
        String RSSIs = wifiScanner.getRSSIs().toString();
        String networks = wifiScanner.getNetworks().toString();

        Point point = customImageView.getLastPoint();
        float x = point.x;
        float y = point.y;
        int z = 1;

        FingerPrint fingerPrint = new FingerPrint(x, y, z, macs, RSSIs, networks);
        Log.i(TAG, x + ", " + y + ", " + z + "\n" + macs + "\n" + RSSIs + "\n" + networks + "\n");

        fingerPrint.save();

    }

    public void showScanLog(View v) {
        Intent intentScanLog = new Intent(this, ScanLogActivity.class);
        //wifiScanner.stop();
        startActivity(intentScanLog);
    }

    // OPTIONS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
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
