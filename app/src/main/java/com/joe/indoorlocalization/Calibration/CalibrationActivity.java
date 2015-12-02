package com.joe.indoorlocalization.Calibration;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
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
import com.joe.indoorlocalization.Options;
import com.joe.indoorlocalization.R;
import com.joe.indoorlocalization.WifiScanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;


public class CalibrationActivity extends AppCompatActivity {

    static String TAG = CalibrationActivity.class.getSimpleName();

    private CustomImageView customImageView;
    private WifiScanner wifiScanner;

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private int floor = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        customImageView = (CustomImageView) findViewById(R.id.customImageViewCalibrate);

        wifiScanner = new WifiScanner(this);
        wifiScanner.start();

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_calibration);
        getSupportActionBar().setTitle("Calibrate");
        addDrawerItems();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setupDrawer();
    }

    public void saveRecord(View v) {
        //wifiScanner.startScan();
        saveIntoFile();

    }

    private void importIntoDatabase() {
    }

    private void saveIntoFile() {
        String fileName = "dataJoe.txt";

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
        /*  if( file.exists() ){
            file.delete();
            Log.d(TAG, "Old fingerprint file deleted...");
        }*/

        Log.i(TAG, "Trying to save file...");


        StringBuilder fingerPrintData = wifiScanner.getFingerPrintData();
        Point point = customImageView.getLastPoint();
        float x = point.x;
        float y = point.y;
        int z = floor;

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




    public static void Save(File file, String[] data)
    {
        FileOutputStream fos = null;
        try
        {
            Log.d(TAG, "Created fileOutputStream");
            fos = new FileOutputStream(file);
            Log.d(TAG, "" + fos);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        try
        {
            try
            {
                for (int i = 0; i<data.length; i++)
                {
                    fos.write(data[i].getBytes());
                    if (i < data.length-1)
                    {
                        fos.write("\n".getBytes());
                    }
                }
            }
            catch (IOException e) {e.printStackTrace();}
        }
        finally
        {
            try
            {
                fos.close();
            }
            catch (IOException e) {e.printStackTrace();}
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



    public void showScanLog(View v) {
        Intent intentScanLog = new Intent(this, ScanLogActivity.class);
        startActivity(intentScanLog);
    }

    // DRAWER MENU
    private void addDrawerItems() {
        String[] osArray = { "Basement", "Floor 1", "Floor 2", "Floor 3", "Floor 4" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        Log.i(TAG, "selected items set");
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        this.floor = position;
        Log.i(TAG, "position " + position + " clicked");

        setFloorText(position);
        changeFloor(position);

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void setFloorText(int floor) {
        TextView floorTextView = (TextView)findViewById(R.id.floorCalibration);
        if(floor == 0) {
            floorTextView.setText("Floor: basement");
        } else {
            floorTextView.setText("Floor " + floor);
        }
    }

    private void changeFloor(int floor) {
        CustomImageViewCalibrate imageView = (CustomImageViewCalibrate)findViewById(R.id.customImageViewCalibrate);
        TextView positionZ = (TextView)findViewById(R.id.positionZ);

        switch(floor) {
            case 0:
                imageView.setImageResource(R.drawable.exactum0);
                positionZ.setText("Z: 0");
                break;
            case 1:
                imageView.setImageResource(R.drawable.exactum1);
                positionZ.setText("Z: 1");
                break;
            case 2:
                imageView.setImageResource(R.drawable.exactum2);
                positionZ.setText("Z: 2");
                break;
            case 3:
                imageView.setImageResource(R.drawable.exactum3);
                positionZ.setText("Z: 3");
                break;
            case 4:
                imageView.setImageResource(R.drawable.exactum4);
                positionZ.setText("Z: 4");
                break;
        }
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mDrawerList.bringToFront();
                mDrawerLayout.requestLayout();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    // OPTIONS MENU
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

        if (mDrawerToggle.onOptionsItemSelected(item)) {
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
